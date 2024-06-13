import java.io.*;
import java.security.InvalidParameterException;

public class Main {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String arithmeticExpression;
        try {
            arithmeticExpression = reader.readLine();
        } catch (IOException exception) {
            System.out.println(exception.getLocalizedMessage());
            return;
        }

        Node tree;
        try {
            tree = TreeCalculator.parse(arithmeticExpression);
        } catch (InvalidParameterException exception) {
            System.out.println(exception.getLocalizedMessage());
            return;
        }

        TreeCalculator.print(tree);
        double result = TreeCalculator.calculate(tree);
        System.out.printf("\nResult: %f\n", result);
    }
}