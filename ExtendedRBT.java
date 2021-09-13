import java.util.ArrayList;

/**
 * This class extends the RedBlackTree and implements Remove() and Search() and
 * performs InOrder Traversal.
 * 
 * @author kinjal
 *
 * @param <T>
 */
public class ExtendedRBT<T extends Comparable<T>> extends RedBlackTree<T> {

	ArrayList<T> list = new ArrayList<>();

	/**
	 * Recursively calls and adds all objects of Type T to an ArrayList in ascending
	 * order
	 * 
	 * @param node
	 */
	public void inOrder(Node<T> node) {

		if (node == null) {
			return;
		}
		inOrder(node.leftChild);

		list.add(node.data);

		inOrder(node.rightChild);
	}

	/**
	 * Recursively searches for data in the tree and returns the data, if present
	 * 
	 * @param node
	 * @return T data
	 */
	public Node<T> search(Node<T> node, T contactInfo) {

		if (node == null || contactInfo.compareTo(node.data) == 0) {
			return node;
		}

		else if (contactInfo.compareTo(node.data) < 0) {
			return search(node.leftChild, contactInfo);
		}

		else {
			return search(node.rightChild, contactInfo);
		}
	}

	/**
	 * Helper method that returns the minimum value node / left-most node in subtree
	 * rooted at node parameter
	 * 
	 * @param node - The subtree from which the left-most node is returned
	 * @return Node<T> - the minimum value node
	 */
	private Node<T> minimum(Node<T> node) {

		while (node.leftChild != null) {
			node = node.leftChild;
		}
		return node;
	}

	/**
	 * Removes the node from the
	 * 
	 * @param T data - data to be removed
	 * @return
	 */
	public Node<T> remove(T contact) {

		Node<T> toBeRemoved = search(root, contact); // searches for the node to be removed in the tree

		if (toBeRemoved == null) { // data not found in the tree
			return null;
		}

		// CASE 1: node to be removed has no children
		if (toBeRemoved.leftChild == null && toBeRemoved.rightChild == null) {
			if (!(toBeRemoved.isBlack)) { // removes the node if it is red
				if (toBeRemoved.isLeftChild()) {
					toBeRemoved.parent.leftChild = null; // change the parent reference to null
				} else {
					toBeRemoved.parent.rightChild = null;
				}
			} else { // the node to be removed is black
				enforceRBTreePropertiesAfterRemove(toBeRemoved);
			}
		}

		// CASE 2: node to be removed has one child
		else if (toBeRemoved.rightChild == null && toBeRemoved.leftChild != null) { // if that one child
																					// is a left child
			toBeRemoved.leftChild.isBlack = true;
			toBeRemoved.parent.leftChild = toBeRemoved.leftChild;
			toBeRemoved.leftChild.parent = toBeRemoved.parent;

		} else if (toBeRemoved.rightChild != null && toBeRemoved.leftChild == null) { // if that one
																						// child is a
																						// right child
			toBeRemoved.rightChild.isBlack = true;
			toBeRemoved.rightChild.parent = toBeRemoved.parent;
			toBeRemoved.parent.rightChild = toBeRemoved.rightChild;

		}

		// CASE 3: node to be removed has two children
		if (toBeRemoved.leftChild != null && toBeRemoved.rightChild != null) {
			Node<T> y = minimum(toBeRemoved.rightChild); // gets the left-most value of the right subtree for
															// replacement

			if (y.rightChild != null) {
				y.rightChild = toBeRemoved.rightChild;
			}

			if (toBeRemoved.parent != null) {
				if (toBeRemoved.isLeftChild()) {
					toBeRemoved.parent.leftChild = y;
					y.leftChild = toBeRemoved.leftChild;
				} else {
					toBeRemoved.parent.rightChild = y;
					y.leftChild = toBeRemoved.leftChild;
				}
			} else {
				root = y;
			}
			if (y.rightChild != null) {
				y.parent.leftChild = y.rightChild;
				y.rightChild.parent = y.parent;
			}
		}

		return toBeRemoved;
	}

	/**
	 * Resolve 'unequal black node length violation that are introduced by removing
	 * new nodes from a red-black tree. While doing do, all other red-black tree
	 * properties must be preserved.
	 * 
	 * @param newNode - new added red node
	 */
	private void enforceRBTreePropertiesAfterRemove(Node<T> doubleBlack) {

		Node<T> sibling = null;

		// gets the location of doubleBlack's sibling
		if (doubleBlack.isLeftChild()) {
			sibling = doubleBlack.parent.rightChild;
		} else if (doubleBlack.parent != null && doubleBlack.parent.rightChild == doubleBlack) {
			sibling = doubleBlack.parent.leftChild;
		}

		// makes sure that the node entered has zero children and is black
		if (doubleBlack.isBlack && doubleBlack.leftChild == null && doubleBlack.rightChild == null) {

			// CASE 2: sibling of double black is black
			if (sibling == null || sibling.isBlack) {

				// CASE 2.a: children of sibling are black or null
				if (sibling.leftChild == null && sibling.rightChild == null
						|| sibling.leftChild.isBlack && sibling.rightChild.isBlack) {
					if (doubleBlack.isLeftChild()) {
						doubleBlack.parent.leftChild = null;
					} else {
						doubleBlack.parent.rightChild = null;
					}
					if (sibling != null)
						sibling.isBlack = false;
					if (!(doubleBlack.parent.isBlack)) {
						doubleBlack.parent.isBlack = true;
					} else {
						doubleBlack = doubleBlack.parent;
						if (!doubleBlack.data.equals(root.data)) {
							enforceRBTreePropertiesAfterRemove(doubleBlack);
						}
					}
				}

				// CASE 2.b: Sibling.same side child as Double Black is red, other is black
				else if (doubleBlack.isLeftChild() && !(sibling.leftChild.isBlack) && sibling.rightChild.isBlack) {
					sibling.isBlack = false;
					sibling.leftChild.isBlack = true;
					rotate(sibling.leftChild, sibling);
					enforceRBTreePropertiesAfterRemove(doubleBlack);
				} else if (!doubleBlack.isLeftChild() && sibling.leftChild.isBlack && !(sibling.rightChild.isBlack)) {
					sibling.isBlack = false;
					sibling.rightChild.isBlack = true;
					rotate(sibling.rightChild, sibling);
					enforceRBTreePropertiesAfterRemove(doubleBlack);
				}

				// CASE 2.c: sibling.other side child as compared to Double Black is red
				else if (doubleBlack.isLeftChild() && !(sibling.rightChild.isBlack)) {
					if (!(sibling.parent.isBlack)) {
						sibling.isBlack = false;
					} else {
						sibling.isBlack = true;
					}
					sibling.parent.isBlack = true;
					sibling.rightChild.isBlack = true;
					rotate(sibling, sibling.parent);
					doubleBlack.parent.leftChild = null;
				}

				else if (!doubleBlack.isLeftChild() && !(sibling.leftChild.isBlack)) {
					if (!(sibling.parent.isBlack)) {
						sibling.isBlack = false;
					} else {
						sibling.isBlack = true;
					}
					sibling.parent.isBlack = true;
					sibling.leftChild.isBlack = true;
					rotate(sibling, sibling.parent);
					doubleBlack.parent.rightChild = null;
				}
			}

			// CASE 1: sibling of double black is red
			else if (!(sibling.isBlack)) {
				sibling.isBlack = true;
				sibling.parent.isBlack = false;
				rotate(sibling, sibling.parent);
				enforceRBTreePropertiesAfterRemove(doubleBlack);
			}

		}
	}
}
