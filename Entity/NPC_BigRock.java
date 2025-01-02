package Entity;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;

import Main.GamePanel;
import Object.OBJDoor_Iron;
import TileInteractive.IT_MetalPlate;
import TileInteractive.InteractiveTile;

public class NPC_BigRock extends Entity{

    public static final String npcName = "Big Rock";

    public NPC_BigRock(GamePanel gp){

        super(gp);

        name = npcName;
        direction = "down";
        speed = 4;

        solidArea = new Rectangle();
        solidArea.x = 2;
        solidArea.y = 6;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 44;
        solidArea.height = 40;

        dialogueSet = -1;
        getNPCImage();
        setDialogue();
    }
     public void getNPCImage() 
    {
        up1 = setUp("/res/NPC/bigrock", gp.tileSize, gp.tileSize);
        up2 = setUp("/res/NPC/bigrock", gp.tileSize, gp.tileSize);
        down1 = setUp("/res/NPC/bigrock", gp.tileSize, gp.tileSize);
        down2 = setUp("/res/NPC/bigrock",  gp.tileSize, gp.tileSize);
        left1 = setUp("/res/NPC/bigrock",  gp.tileSize, gp.tileSize);
        left2 = setUp("/res/NPC/bigrock",  gp.tileSize, gp.tileSize);
        right1 = setUp("/res/NPC/bigrock",  gp.tileSize, gp.tileSize);
        right2 = setUp("/res/NPC/bigrock", gp.tileSize, gp.tileSize);
    }
    public void setDialogue(){

        dialogues[0][0] = "It's giant rock";

    }
    public void setAction(){}
    public void update(){}
    public void speak(){

        // do this character specific stuff
        facePlayer();
        startDialogue(this, dialogueSet);

        dialogueSet++;
        if(dialogues[dialogueSet][0] == null){
            dialogueSet--;
        }
    }
    public void move(String d){
        this.direction = d;

        checkCollision();
        
        if(collisionOn == false){
            switch(direction){
                case "up": worldY -= speed; break;
                case "down": worldY -= speed; break;
                case "left": worldX -= speed; break;
                case "right": worldY -= speed; break;
            }
        }
        detectPlate();
    }
    public void detectPlate(){
        ArrayList<InteractiveTile>plateList = new ArrayList<>();
        ArrayList<Entity>rockList = new ArrayList<>();

        //create a plate list
        for(int i = 0; i < gp.iTile[1].length; i++){
            if(gp.iTile[gp.currentMap][i] != null && gp.iTile[gp.currentMap][i].name != null && gp.iTile[gp.currentMap][i].name.equals(IT_MetalPlate.itName)){
                plateList.add(gp.iTile[gp.currentMap][i]);
            }
        }

        //create a rock list
        for(int i = 0; i < gp.NPC[1].length; i++){
            if(gp.NPC[gp.currentMap][i] != null && gp.iTile[gp.currentMap][i].name.equals(NPC_BigRock.npcName)){
                rockList.add(gp.NPC[gp.currentMap][i]);
            }
        }

        int count = 0;
        
        //scan the plate list
        for(int i = 0; i < plateList.size(); i++){
            int xDistance = Math.abs(worldX - plateList.get(i).worldX);
            int yDistance = Math.abs(worldY - plateList.get(i).worldY);
            int distance = Math.max(xDistance, yDistance);

            if(distance < 8){
                if(linkedEntity == null){
                    linkedEntity = plateList.get(i);
                    gp.playSE(3);
                }
            }
            else{
                if(linkedEntity == plateList.get(i)){
                    linkedEntity = null;
                }
            }
        }
        //scan the rock list
        for(int i = 0; i < plateList.size(); i++){
            //count the rock on the plate
            if(rockList.get(i).linkedEntity != null){
                count++;
            }
        }

        //if all the rocks are on the plate, iron door open 
        if(count == rockList.size()){
            for(int i = 0; i < gp.obj[i].length; i++){
                if(gp.obj[gp.currentMap][i] != null && gp.obj[gp.currentMap][i].name.equals(OBJDoor_Iron.objName)){
                    gp.obj[gp.currentMap][i] = null;
                    gp.playSE(21);
                }
            }
        }
    }
}
