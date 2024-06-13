final public class Node {
    final public double value;
    final public Operations operation;
    final public Node left;
    final public Node right;

    public Node(Operations operation, double value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    public static void printContent(Node node) {
        if (node.operation == Operations.VAL) {
            System.out.print(node.value);
        } else {
            System.out.print(Operations.getOperationSymbol(node.operation));
        }
        System.out.print(' ');
    }
}
