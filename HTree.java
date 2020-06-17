/*
 * Huffmann Tree
 *
 * Binary Tree with key and value elements that implements Comparable. To be used to help with Compressor.java
 * which does Huffmann Encoding.
 *
 * @author Caroline Tornquist, Dartmouth CS 10, Fall 2019
 */


public class HTree<Character,Integer> implements Comparable<HTree<Character, Integer>>{
    private HTree<Character, Integer> left, right;
    private Character key;
    private Integer value;

    //making leaf node, left and right are null
    public HTree(Character key, Integer value) {
        this.left = null;
        this.right = null;
        this.key = key;
        this.value = value;
    }

    //making inner node, takes children as parameters
    public HTree(Character key, Integer value,  HTree<Character, Integer> left,  HTree<Character, Integer> right) {
        this.key = key; this.value = value;
        this.left = left; this.right = right;
    }

    public boolean isInner() {
        return left != null || right != null;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public boolean hasLeft() { return left != null; }

    public boolean hasRight() { return right != null;}

    //getters
    public HTree<Character, Integer> getLeft() { return left;}

    public HTree<Character, Integer> getRight() {return right;}

    public Character getKey() { return key;}

    public Integer getValue() {return value;}

    //setters
    public void setLeft(HTree<Character, Integer> left) {this.left = left;}

    public void setRight(HTree<Character, Integer> right) {this.right = right;}

    public void setKey(Character key) {this.key = key;}

    public void setValue(Integer value) {this.value = value;}

    /*
     * Overriding compareTo method. Returns the difference in the frequencies of the two nodes.
     */
    @Override
    public int compareTo(HTree<Character, Integer> compareTree) {
        return (int)this.value - (int)compareTree.getValue();
    }

}

