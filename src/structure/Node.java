/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structure;

import ij.IJ;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jean-Francois Gilles
 */

public class Node {

    public Node parent; // The parent of the current node
    public List<Node> children; // The children of the current node
    public Object info;

    public static int maxNbOfChildren; // Equal to the k-ary tree; a cell devides by 2 but I add one child to be sure!

    /**
     * 
     * @param info 
     */
    public Node (Object info){
        this.info=info; 
        this.children  = new ArrayList<>(maxNbOfChildren);
    }

    /**
     * Add a child to the node
     * @param childNode
     * @param position 
     */
    public void addChild(Node childNode, int position){
        if(position>=maxNbOfChildren-1){ // !!!! override a child on i-th position, Not yet used
            // if some error
        }
        else {
            if(this.children.isEmpty()==true){
                childNode.parent=this;
                this.children.add(position, childNode);
            }
//            else{
//                 //There is already a child node on this position; throw some error, Not yet used
//            }
            
        }
    }
}
