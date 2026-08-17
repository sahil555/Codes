import java.util.LinkedList;
import java.util.Queue;

// Binary Search Tree Implementation
// includes insert, delete, search, min, max, height, size, leaf count, traversals (inorder, preorder, postorder, level order), invert (mirror), isEmpty and clear methods.

public class BST {
    // TreeNode class represents a node in the binary search tree
    // using a static nested class to encapsulate the node structure within the BinarySearchTree class.
    // a constructor is provided to initialize the node with a value, 
    // and left and right child pointers are initialized to null by default.
    static class TreeNode {

        int value;
        TreeNode left;
        TreeNode right;

        TreeNode(int value) {
            this.value = value;
        }
    }
    // The root of the binary search tree is represented by a private member variable of type TreeNode.
    private TreeNode root;
    
    public void insert(int value) {
        root = insert(root, value);
    }

    private TreeNode insert(TreeNode node, int value) {

        if (node == null)
            return new TreeNode(value);

        if (value < node.value)
            node.left = insert(node.left, value);

        else if (value > node.value)
            node.right = insert(node.right, value);

        return node;
    }

    public boolean contains(int value) {
        return contains(root, value);
    }

    private boolean contains(TreeNode node, int value) {

        if (node == null)
            return false;

        if (node.value == value)
            return true;

        if (value < node.value)
            return contains(node.left, value);

        return contains(node.right, value);
    }

    public void delete(int value) {
        root = delete(root, value);
    }

    private TreeNode delete(TreeNode node, int value) {

        if (node == null)
            return null;

        if (value < node.value) {

            node.left = delete(node.left, value);

        } else if (value > node.value) {

            node.right = delete(node.right, value);

        } else {

            // No child
            if (node.left == null && node.right == null)
                return null;

            // One child
            if (node.left == null)
                return node.right;

            if (node.right == null)
                return node.left;

            // Two children

            int successor = min(node.right);

            node.value = successor;

            node.right = delete(node.right, successor);
        }

        return node;
    }

    public int min() {

        if (root == null)
            throw new IllegalStateException("Tree Empty");

        return min(root);
    }

    private int min(TreeNode node) {

        while (node.left != null)
            node = node.left;

        return node.value;
    }

    public int max() {

        if (root == null)
            throw new IllegalStateException("Tree Empty");

        TreeNode current = root;

        while (current.right != null)
            current = current.right;

        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(TreeNode node) {

        if (node == null)
            return 0;

        return 1 + Math.max(height(node.left), height(node.right));
    }

    public int size() {
        return size(root);
    }

    private int size(TreeNode node) {

        if (node == null)
            return 0;

        return 1 + size(node.left) + size(node.right);
    }

    public int leafCount() {
        return leafCount(root);
    }

    private int leafCount(TreeNode node) {

        if (node == null)
            return 0;

        if (node.left == null && node.right == null)
            return 1;

        return leafCount(node.left) + leafCount(node.right);
    }

    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(TreeNode node) {

        if (node == null)
            return;

        inorder(node.left);

        System.out.print(node.value + " ");

        inorder(node.right);
    }

    public void preorder() {
        preorder(root);
        System.out.println();
    }

    private void preorder(TreeNode node) {

        if (node == null)
            return;

        System.out.print(node.value + " ");

        preorder(node.left);

        preorder(node.right);
    }

    public void postorder() {
        postorder(root);
        System.out.println();
    }

    private void postorder(TreeNode node) {

        if (node == null)
            return;

        postorder(node.left);

        postorder(node.right);

        System.out.print(node.value + " ");
    }

    public void levelOrder() {

        if (root == null)
            return;

        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {

            TreeNode node = queue.poll();

            System.out.print(node.value + " ");

            if (node.left != null)
                queue.offer(node.left);

            if (node.right != null)
                queue.offer(node.right);
        }

        System.out.println();
    }

    public void invert() {
        invert(root);
    }

    private void invert(TreeNode node) {

        if (node == null)
            return;

        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;

        invert(node.left);
        invert(node.right);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    public int diameter(){
        return diameter(root)[0];
    }

    private int height(TreeNode node, int[] diameter) {

        if (node == null) {
            return 0;
        }

        int leftHeight = height(node.left, diameter);
        int rightHeight = height(node.right, diameter);

        // Longest path passing through current node
        int currentDiameter = leftHeight + rightHeight;

        diameter[0] = Math.max(
                diameter[0],
                currentDiameter);

        // Return height to parent
        return 1 + Math.max(
                leftHeight,
                rightHeight);
    }

    private int[] diameter(TreeNode rootNode){
        // Given the root of a binary tree, return the length of the longest path between any two nodes.
        // Important: the diameter is measured in number of edges, not nodes.

        int[] diameter = new int[1];
        
        height(rootNode, diameter);

        return diameter;


    }

    public int maxDepth() {
        return maxDepth(root);
    }

    private int maxDepth(TreeNode node) {

        if (node == null) {
            return 0;
        }

        int left = maxDepth(node.left);
        int right = maxDepth(node.right);

        return 1 + Math.max(left, right);
    }
    public static void main(String[] args) {

        BST tree = new BST();

        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);

        System.out.println("Inorder");
        tree.inorder();

        System.out.println("Preorder");
        tree.preorder();

        System.out.println("Postorder");
        tree.postorder();

        System.out.println("Level Order");
        tree.levelOrder();

        System.out.println("Height : " + tree.height());
        System.out.println("Max Depth : " + tree.maxDepth());
        System.out.println("Size : " + tree.size());

        System.out.println("Leaf Count : " + tree.leafCount());

        System.out.println("Min : " + tree.min());

        System.out.println("Max : " + tree.max());

        System.out.println("Contains 60 : " + tree.contains(60));
        tree.delete(80);

        System.out.println("diameter : " + tree.diameter());

        System.out.println("After Delete 80");
        tree.inorder();
        tree.invert();

        System.out.println("Mirror Tree");
        tree.levelOrder();
    }
}