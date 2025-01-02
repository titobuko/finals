package AI;

import java.util.ArrayList;

import Entity.Entity;
import Main.GamePanel;

public class PathFinder {
    
    GamePanel gp;
    Node [][] node;
    ArrayList<Node> openList = new ArrayList<>(); 
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public PathFinder(GamePanel gp){
        this.gp = gp;
        instantiateNodes();
    }
    public void instantiateNodes(){
        node = new Node[gp.maxWorldCol][gp.maxWorldRow];

        int col = 0;
        int row = 0;
    
        while(col < gp.maxWorldCol && row < gp.maxWorldRow){
            node[col][row] = new Node(col, row);
    
            col++;
            if(col == gp.maxWorldCol){  // Fixed: use maxWorldCol instead of maxScreenCol
                col = 0;
                row++;
            }
        }
    }
    public void resetNodes(){
        int col = 0;
        int row = 0;

        while(col < gp.maxWorldCol && row < gp.maxWorldRow){
            
            //reset open, checked and solid state
            node[col][row].open = false;
            node[col][row].checked = false;
            node[col][row].solid = false;

            col++;
            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }
        }

        //reset other settings
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }
    public void setNodes(int startCol, int startRow, int goalCol, int goalRow, Entity entity){

        // Add boundary checks
        if (startCol < 0 || startCol >= gp.maxWorldCol || 
        startRow < 0 || startRow >= gp.maxWorldRow ||
        goalCol < 0 || goalCol >= gp.maxWorldCol ||
        goalRow < 0 || goalRow >= gp.maxWorldRow) {
        return;
       }
        
        resetNodes();

        //set Start and goal node
        startNode = node [startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];
        openList.add(currentNode);
        int col = 0;
        int row = 0;
        
        while(col < gp.maxWorldCol && row < gp.maxWorldRow){

            //set solid node
            //checktile
            int tileNum = gp.tileM.mapTileNum[gp.currentMap][row][col];
            if(gp.tileM.tile[tileNum].collision == true){
                node[col][row].solid = true;
            }
            //check interactive tiles
            for(int i = 0; i < gp.iTile[1].length; i++){
                if(gp.iTile[gp.currentMap][i] != null && gp.iTile[gp.currentMap][i].destructible == true){
                    int itCol = gp.iTile[gp.currentMap][i].worldX/gp.tileSize;
                    int itRow =  gp.iTile[gp.currentMap][i].worldY/gp.tileSize;
                    node[itCol][itRow].solid = true;
                }
            }
            //set cost
            getCost(node[col][row]);

            col++;
            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }
        }
    }
    public void getCost(Node node){

        //G cost
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;
        //H cost
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;
        //f cost
        node.fCost = node.gCost + node.hCost; 
    }
    public boolean search(){
        while (goalReached == false && step < 500){
            int col = currentNode.col;
            int row = currentNode.row;

            //check currentNode
            currentNode.checked = true;
            openList.remove(currentNode);

            //open the up node 
            if(row - 1 >= 0){
                openNode(node[col][row-1]);
            }
            //open the left node
            if(col -1 >= 0){
                openNode(node[col-1][row]);
            }
            //open the down node
            if(row + 1 < gp.maxWorldRow){
                openNode(node[col][row+1]);
            }
            //opent the right node
            if(col+ 1 < gp.maxWorldCol){
                openNode(node[col+1][row]);
            }
            //Find the best node
            int bestNodeIndex = 0;
            int bestNodefCost = 999;

            for(int i = 0; i < openList.size(); i++){
                //check if this node's F cost is better
                if(openList.get(i).fCost < bestNodefCost){
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                }
                //if F cost is equal, check the g cost
                else if(openList.get(i).fCost == bestNodefCost){
                    if(openList.get(i).gCost < openList.get(bestNodeIndex).gCost){
                        bestNodeIndex = i;
                    }
                }
            }

            //if there is no node in the openList, end the loop
            if(openList.size() == 0){
                break;
            }
            //after the loop openlist[bestnodeindex] is the next step (=currentNode)
            currentNode = openList.get(bestNodeIndex);

            if(currentNode == goalNode){
                goalReached = true;
                trackThePath();
            }
            step++;
        } 
        return goalReached;
    }
    public void openNode(Node node){
        if(node.open == false && node.checked == false && node.solid == false){
            node.open = true;
            node.parent = currentNode;
            openList.add(node);
        }
    }
    public void trackThePath(){
        Node current = goalNode;

        while(current != startNode){
            pathList.add(0, current);  // Fixed: add current instead of currentNode
            current = current.parent;
        }
    }
}
