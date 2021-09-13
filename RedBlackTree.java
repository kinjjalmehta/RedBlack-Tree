import java.util.LinkedList;


/**
 * Binary Search Tree implementation with a Node inner class for representing the nodes within a
 * binary search tree. You can use this class' insert method to build a binary search tree, and its
 * toString method to display the level order (breadth first) traversal of values in that tree.
 */
public class RedBlackTree<T extends Comparable<T>> {

  /**
   * This class represents a node holding a single value within a binary tree the parent, left, and
   * right child references are always be maintained.
   */
  protected static class Node<T> {
    public T data;
    public Node<T> parent; // null for root node
    public Node<T> leftChild;
    public Node<T> rightChild;
    public boolean isBlack; // all new Node objects will be red by default

    public Node(T data) {
      this.data = data;
      isBlack = false;
    }

    /**
     * @return true when this node has a parent and is the left child of that parent, otherwise
     *         return false
     */
    public boolean isLeftChild() {
      return parent != null && parent.leftChild == this;
    }

    /**
     * This method performs a level order traversal of the tree rooted at the current node. The
     * string representations of each data value within this tree are assembled into a comma
     * separated string within brackets (similar to many implementations of java.util.Collection).
     * 
     * @return string containing the values of this tree in level order
     */
    @Override
    public String toString() { // display subtree in order traversal
      String output = "[";
      LinkedList<Node<T>> q = new LinkedList<>();
      q.add(this);
      while (!q.isEmpty()) {
        Node<T> next = q.removeFirst();
        if (next.leftChild != null)
          q.add(next.leftChild);
        if (next.rightChild != null)
          q.add(next.rightChild);
        output += next.data.toString();
        if (!q.isEmpty())
          output += ", ";
      }
      return output + "]";
    }
  }

  protected Node<T> root; // reference to root node of tree, null when empty

  /**
   * Performs a naive insertion into a binary search tree: adding the input data value to a new node
   * in a leaf position within the tree. After this insertion, no attempt is made to restructure or
   * balance the tree. This tree will not hold null references, nor duplicate data values.
   * 
   * @param data to be added into this binary search tree
   * @throws NullPointerException     when the provided data argument is null
   * @throws IllegalArgumentException when the tree already contains data
   */
  public void insert(T data) throws NullPointerException, IllegalArgumentException {
    // null references cannot be stored within this tree
    if (data == null)
      throw new NullPointerException("This RedBlackTree cannot store null references.");

    Node<T> newNode = new Node<>(data);
    if (root == null) {
      root = newNode;
    } // add first node to an empty tree
    else
      insertHelper(newNode, root); // recursively insert into subtree

    root.isBlack = true; // set the root node to black
  }

  /**
   * Recursive helper method to find the subtree with a null reference in the position that the
   * newNode should be inserted, and then extend this tree by the newNode in that position.
   * 
   * @param newNode is the new node that is being added to this tree
   * @param subtree is the reference to a node within this tree which the newNode should be inserted
   *                as a descendant beneath
   * @throws IllegalArgumentException when the newNode and subtree contain equal data references (as
   *                                  defined by Comparable.compareTo())
   */
  private void insertHelper(Node<T> newNode, Node<T> subtree) {
    int compare = newNode.data.compareTo(subtree.data);
    // do not allow duplicate values to be stored within this tree
    if (compare == 0)
      throw new IllegalArgumentException("This RedBlackTree already contains that value.");

    // store newNode within left subtree of subtree
    else if (compare < 0) {
      if (subtree.leftChild == null) { // left subtree empty, add here
        subtree.leftChild = newNode;
        newNode.parent = subtree;
        enforceRBTreePropertiesAfterInsert(newNode);
        // otherwise continue recursive search for location to insert
      } else
        insertHelper(newNode, subtree.leftChild);
    }

    // store newNode within the right subtree of subtree
    else {
      if (subtree.rightChild == null) { // right subtree empty, add here
        subtree.rightChild = newNode;
        newNode.parent = subtree;
        enforceRBTreePropertiesAfterInsert(newNode);
        // otherwise continue recursive search for location to insert
      } else
        insertHelper(newNode, subtree.rightChild);
    }
  }

  /**
   * This method performs a level order traversal of the tree. The string representations of each
   * data value within this tree are assembled into a comma separated string within brackets
   * (similar to many implementations of java.util.Collection, like java.util.ArrayList, LinkedList,
   * etc).
   * 
   * @return string containing the values of this tree in level order
   */
  @Override
  public String toString() {
    return root.toString();
  }

  /**
   * Performs the rotation operation on the provided nodes within this BST. When the provided child
   * is a leftChild of the provided parent, this method will perform a right rotation (sometimes
   * called a left-right rotation). When the provided child is a rightChild of the provided parent,
   * this method will perform a left rotation (sometimes called a right-left rotation). When the
   * provided nodes are not related in one of these ways, this method will throw an
   * IllegalArgumentException.
   * 
   * @param child  is the node being rotated from child to parent position (between these two node
   *               arguments)
   * @param parent is the node being rotated from parent to child position (between these two node
   *               arguments)
   * @throws IllegalArgumentException when the provided child and parent node references are not
   *                                  initially (pre-rotation) related that way
   */
  @SuppressWarnings("unused")
  protected void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {

    if (child.isLeftChild()) { // perform the right rotation

      // set the values between child and parent to parent's left child
      parent.leftChild = child.rightChild;

      if (child.rightChild != null) {
        child.rightChild.parent = parent;
      }

      // set the grandparents' reference to child's parents
      child.parent = parent.parent;

      if (parent.parent == null) {
        root = child;
      } else if (parent.isLeftChild()) {
        parent.parent.leftChild = child;
      } else {
        parent.parent.rightChild = child;
      }

      child.rightChild = parent; // linking parent as the right child of the child
      parent.parent = child;
    }

    else if (parent != null & parent.rightChild == child) { // perform left rotation

      // set the values between parent and child to parent's right child
      parent.rightChild = child.leftChild;

      if (child.leftChild != null) {
        child.leftChild.parent = parent;
      }

      // set the grandparents' reference as the child's parents
      child.parent = parent.parent;

      if (parent.parent == null) {
        root = child;
      } else if (parent.isLeftChild()) {
        parent.parent.leftChild = child;
      } else {
        parent.parent.rightChild = child;
      }

      child.leftChild = parent; // link parent as the left child of the child
      parent.parent = child;
    }

    else {
      throw new IllegalArgumentException(
          "The provided child and parent references are not related.");
    }
  }

  /**
   * Resolve 'red child under red parent' violation that are introduced by inserting new nodes into
   * a red-black tree. While doing do, all other red-black tree properties must be preserved.
   * 
   * @param newNode - new added red node
   */
  private void enforceRBTreePropertiesAfterInsert(Node<T> child) {

    Node<T> sibling = null;
    Node<T> grandParent = null;

    // makes sure that the child passed as a reference is red
    if (child.isBlack) {
      child.isBlack = false;
    }

    // referencing nodes as grandparent of the child and sibling of the parent
    if (child.parent.parent != null) {
      grandParent = child.parent.parent;
    }

    // gets the location of parent's sibling
    if (child.parent.isLeftChild()) {
      sibling = child.parent.parent.rightChild;
    } else if (child.parent.parent != null && child.parent.parent.rightChild == child.parent) {
      sibling = child.parent.parent.leftChild;
    }

    // makes sure that the parent node is red for the 'red under red' violation
    if (!(child.parent.isBlack)) {


      if (sibling == null || sibling.isBlack) {

        // parent's sibling is null or black and child is on opposite side as the sibling
        if (sibling == null || (!sibling.isLeftChild()) && child.isLeftChild()
            || (sibling.isLeftChild() && (!child.isLeftChild()))) { // CASE 2

          rotate(child.parent, grandParent);
          child.parent.isBlack = true;
          if (child.isLeftChild()) {
            child.parent.rightChild.isBlack = false;
          } else {
            child.parent.leftChild.isBlack = false;
          }
        }

        // parent's sibling is black and child is on the same side as the sibling
        else if ((sibling.isLeftChild() && child.isLeftChild())
            || !sibling.isLeftChild() && !child.isLeftChild()) { // CASE 3

          rotate(child, child.parent);
          enforceRBTreePropertiesAfterInsert(child);
        }

      }

      else if (!(sibling.isBlack)) { // CASE 1

        child.parent.isBlack = true;
        sibling.isBlack = true;
        grandParent.isBlack = false;

        if (grandParent.parent != null && !(grandParent.parent.isBlack)) {
          enforceRBTreePropertiesAfterInsert(grandParent);
        }
      }
    }


  }


}


//
