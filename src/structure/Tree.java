
package structure;

import ij.IJ;
import java.util.ArrayList;

/**
 *
 * @author Jean-Francois Gilles
 */
public class Tree<T> {
    
    public Node<T> root;

    /**
     * New tree
     */
    public Tree() {
    }
    
    /**
     * New tree
     * @param root
     */
    public Tree(Node<T> root) {
        this.root = root;
    }
    
    public Node<T> getRoot() {
        return root;
    }
    
    public void setRoot(Node<T> root) {
        this.root = root;
    }
    
    /**
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    /**
     * Search data in tree root
     * @param data
     * @return true if found
     */
    public boolean exists(T data) {
        return find(root, data);
    }

    /**
     * 
     * @param rootNode
     * @return 
     */
    public int numberOfNodesInTree(Node<T> rootNode){//if it can be useful
        int count=0;
        count++;
        if(!rootNode.getChildren().isEmpty()) {
            for(Node ch : rootNode.getChildren())
                count=count+numberOfNodesInTree(ch);
        }
        return count;
    }

    public int numberOfNodesInTree(){//if it can be useful
        return numberOfNodesInTree(this.root);
    }
    

//    public void changeLine(Node<T> newChange, Node<T> Parent){
//        Node<T> previous = this.findNode(root, newChange.getData());
//        previous.
//        Parent.addChild(newChange);
//    }
    
    public int getNumberOfDescendants(Node<T> node) {
        int n = node.getChildren().size();
        for(Node<T> child :node.children){
           n += getNumberOfDescendants(child);
        }
        return n;
    }
    
//    public ArrayList<Node<T>> getAllDescendantsToEnd(Node<T> node) {
//        ArrayList<Node<T>> list = new ArrayList<>();
//        list.add(node);
//        if (node.getChildren().isEmpty()) return list;
//        for (Node<T> child : node.getChildren()) {
//            list.addAll(child.getAllDescendantsToEnd(node));
//        }
//        return list;
//    }
    
    /**
     * Search in given node and its children a node with nodeData
     * @param node source of search
     * @param nodeData what to find
     * @return True if found
     */
    public boolean find(Node<T> node, T nodeData) {
        boolean res = false;
        if (node.getData().equals(nodeData))
            return true;

        else {
            for (Node<T> child : node.getChildren()){
                if (find(child, nodeData))
                    res = true;
            }
        }
        return res;
    }

    /**
     * Search in given node and its children a node with nodeData
     * @param node source of search
     * @param nodeData what to find
     * @return 
     */
    public Node<T> findNode(Node<T> node, T nodeData) {
        if (node == null)
            return null;
        if (node.getData().equals(nodeData))
            return node;
        else {
            Node<T> cnode = null;
            for (Node<T> child : node.getChildren()){
                if ((cnode = findNode(child, nodeData)) != null)
                    return cnode;
            }
        }
        return null;
    }
    
    
}
