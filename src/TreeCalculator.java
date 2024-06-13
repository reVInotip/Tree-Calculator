import java.util.ArrayDeque;

import static java.lang.Character.isDigit;

import java.security.InvalidParameterException;

final public class TreeCalculator {
    static private int currIndex = 0;

    /**
     * @param arithmeticExpression String
     */
    static private void skipSpaces(String arithmeticExpression) {
        while (currIndex < arithmeticExpression.length() &&
                arithmeticExpression.charAt(currIndex) == ' ') {
            ++currIndex;
        }
    }

    /**
     * @param arithmeticExpression String
     * @return Node
     */
    static private Node parseBracket(String arithmeticExpression) {
        skipSpaces(arithmeticExpression);
        if (currIndex == arithmeticExpression.length()) {
            return null;
        }

        if (arithmeticExpression.charAt(currIndex) == '(') {
            ++currIndex;
            Node tree = parseAddOrSub(arithmeticExpression);
            if (currIndex != arithmeticExpression.length() && arithmeticExpression.charAt(currIndex) == ')') {
                ++currIndex;
            } else {
                throw new InvalidParameterException("Expression string in invalid!");
            }

            return tree;
        }
        return null;
    }

    /**
     * @param arithmeticExpression String
     * @return Node
     */
    static private Node parseDigit(String arithmeticExpression) {
        skipSpaces(arithmeticExpression);
        Node tree = parseBracket(arithmeticExpression);
        if (tree != null || currIndex == arithmeticExpression.length()) {
            return tree;
        }

        int startIndex = currIndex;
        char character = arithmeticExpression.charAt(currIndex);
        if (character == '+' || character == '-') {
            ++currIndex;
            character = arithmeticExpression.charAt(currIndex);
        }

        while (isDigit(character) || character == '.') {
            ++currIndex;

            if (currIndex == arithmeticExpression.length()) {
                break;
            }

            character = arithmeticExpression.charAt(currIndex);
        }

        double value;
        try {
            if (currIndex > startIndex) {
                value = Double.parseDouble(arithmeticExpression.substring(startIndex, currIndex));
                return new Node(Operations.VAL, value, null, null);
            } else if (currIndex == arithmeticExpression.length() - 1) {
                value = Double.parseDouble(String.valueOf(arithmeticExpression.charAt(startIndex)));
                return new Node(Operations.VAL, value, null, null);
            }
        } catch (NumberFormatException exception) {
            throw new InvalidParameterException("Expression string in invalid!");
        }
        return null;
    }

    /**
     * @param arithmeticExpression String
     * @return Node
     */
    static private Node parseMulOrDivOrMod(String arithmeticExpression) {
        Node rightTree;
        Node currTree = parseDigit(arithmeticExpression);
        if (currTree == null || currIndex == arithmeticExpression.length()) {
            return currTree;
        }

        char operation = arithmeticExpression.charAt(currIndex);

        while (operation == '*' || operation == '/' || operation == '%' || operation == ' ') {
            ++currIndex;

            if (operation == ' ') {
                operation = arithmeticExpression.charAt(currIndex);
                continue;
            }

            rightTree = parseDigit(arithmeticExpression);
            if (rightTree == null) {
                break;
            }
            currTree = new Node(Operations.getOperation(operation), operation, currTree, rightTree);

            if (currIndex == arithmeticExpression.length()) {
                break;
            }
            operation = arithmeticExpression.charAt(currIndex);
        }

        return currTree;
    }

    /**
     * @param arithmeticExpression String
     * @param currTree Node
     * @param operation char
     * @return Node
     */
    static private Node parseMulOrDivOrMod(String arithmeticExpression, Node currTree, char operation) {
        Node rightTree;
        while (operation == '*' || operation == '/' || operation == '%' || operation == ' ') {
            ++currIndex;

            if (operation == ' ') {
                operation = arithmeticExpression.charAt(currIndex);
                continue;
            }

            rightTree = parseDigit(arithmeticExpression);
            if (rightTree == null) {
                break;
            }
            currTree = new Node(Operations.getOperation(operation), operation, currTree, rightTree);

            if (currIndex == arithmeticExpression.length()) {
                break;
            }
            operation = arithmeticExpression.charAt(currIndex);
        }

        return currTree;
    }

    /**
     * @param arithmeticExpression String
     * @return Node
     */
    static private Node parseAddOrSub(String arithmeticExpression) {
        Node tree = parseDigit(arithmeticExpression);
        if (currIndex == arithmeticExpression.length()) {
            return tree;
        }

        skipSpaces(arithmeticExpression);
        char operation = arithmeticExpression.charAt(currIndex);
        if (operation != '+' && operation != '-') {
            tree = parseMulOrDivOrMod(arithmeticExpression, tree, operation);
            if (currIndex == arithmeticExpression.length()) {
                return tree;
            }

            operation = arithmeticExpression.charAt(currIndex);
        }

        while (operation == '+' || operation == '-' || operation == ' ') {
            ++currIndex;

            if (operation == ' ') {
                operation = arithmeticExpression.charAt(currIndex);
                continue;
            }

            Node rightTree = parseMulOrDivOrMod(arithmeticExpression);
            if (rightTree == null) {
                break;
            }
            tree = new Node(Operations.getOperation(operation), operation, tree, rightTree);

            if (currIndex == arithmeticExpression.length()) {
                break;
            }
            operation = arithmeticExpression.charAt(currIndex);
        }

        return tree;
    }

    /**
     * @param arithmeticExpression String
     * @return Node
     */
    static public Node parse(String arithmeticExpression) throws InvalidParameterException {
        Node tree = parseAddOrSub(arithmeticExpression);
        currIndex = 0;
        return tree;
    }

    /**
     * @param tree Node
     */
    static public void print(Node tree) {
        ArrayDeque<Node> children = new ArrayDeque<>();
        Node.printContent(tree);
        if (tree.left != null && tree.right != null) {
            children.add(tree.left);
            children.add(tree.right);
        }

        Node currNode;
        int isNextLevel = 0;
        while (!children.isEmpty()) {
            currNode = children.pop();
            Node.printContent(currNode);
            if (isNextLevel == 1) {
                isNextLevel = 0;
            }

            if (currNode.left != null) {
                children.add(currNode.left);
            }
            if (currNode.right != null) {
                children.add(currNode.right);
            }

            ++isNextLevel;
        }
    }


    /**
     * @param tree Node
     * @return double
     */
    static public double calculate(Node tree) {
        if(tree.operation == Operations.VAL) {
            return tree.value;
        }

        assert tree.left != null;
        assert tree.right != null;
        double leftVal = calculate(tree.left);
        double rightVal = calculate(tree.right);

        double result = 0;
        switch (tree.operation) {
            case ADD -> result = leftVal + rightVal;
            case SUB -> result = leftVal - rightVal;
            case MUL -> result = leftVal * rightVal;
            case DIV -> result = leftVal / rightVal;
            case MOD -> result = leftVal % rightVal;
        }

        return result;
    }
}
