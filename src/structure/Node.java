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

public class Node<T> {

    public Node<T> parent; // The parent of the current node
    public List<Node<T>> children; // The children of the current node
    public T data; // Descriptors


    /**
     * 
     * @param data
     */
    public Node (T data){
        this.data=data; 
        this.children  = new ArrayList<>();
    }

    /**
     * Add a child to the node
     * @param child
     */
    public void addChild(Node<T> child) {
        child.setParent(this);
        children.add(child);
    }

    /**
     * Add a given child node at given index
     * @param index 
     * @param child 
     */
    public void addChildAt(int index, Node<T> child) {
        child.setParent(this);
        this.children.add(index, child);
    }
    
    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return this.parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }
    
    public Node<T> getFirstChild(){
        return this.children.get(0);
    }
    
    /**
     *
     * @return list of children
     */
    public List<Node<T>> getChildren() {
        return this.children;
    }
    
    /**
     *
     * @return number of children
     */
    public int getNumberOfChildren() {
        return getChildren().size();
    }

    /**
     *
     * @return true if Node has child/children
     */
    public boolean hasChildren() {
        return (getNumberOfChildren() > 0);
    }
    
    /**
     * Remove all children of this node.
     */
    public void removeChildren() {
        this.children.clear();
    }

    /**
     * Remove child at given index.
     * @param index The index at which the child has to be removed.
     * @return the removed node.
     */
    public Node<T> removeChildAt(int index) {
        return children.remove(index);
    }
    
}
