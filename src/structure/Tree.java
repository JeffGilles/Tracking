
package structure;

import ij.IJ;
import java.util.ArrayList;

/**
 *
 * @author Jean-Francois Gilles
 */
public class Tree {
    
    public Node root;

    /**
     * New tree
     * @param kAryTree = nb time points
     */
    public Tree(int kAryTree){
        Node.maxNbOfChildren=kAryTree;        
    }

    /**
     * Add a root to the tree
     * @param info 
     */
    public void addRoot(Object info){
        root=new Node(info);
        root.parent=null;
        root.children=new ArrayList<>(Node.maxNbOfChildren);
    }

    /**
     * 
     * @param u
     * @param info
     * @param i 
     */
    public void addNewNodeChildOfNodeU(Node u, Object info, int i){
        Node child=new Node(info);
        u.addChild(child, i);
    }

    /**
     * 
     * @param rootNode
     * @return 
     */
    public int numberOfNodesInTree(Node rootNode){//if it can be useful
        int count=0;
        count++;
        if(!rootNode.children.isEmpty()) {
            for(Node ch : rootNode.children)
                count=count+numberOfNodesInTree(ch);
        }
        return count;
    }

    public int numberOfNodesInTree(){//if it can be useful
        return numberOfNodesInTree(this.root);
    }

    public void changeRoot(Node newRoot, int i){
        Node oldRoot=this.root;
        newRoot.parent=null;
        newRoot.addChild(oldRoot, i);
        oldRoot.parent=newRoot;
        this.root=newRoot;
    }
}
