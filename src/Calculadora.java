import java.util.*;

public class Calculadora {

    public static void main(String[] args) {

        String inputString;
        Scanner keyb = new Scanner(System.in);

        while (true) {
            System.out.println("Introduzca una expresion en notacion infija:");
            System.out.print("> ");
            inputString = keyb.nextLine();
            if (inputString.equalsIgnoreCase("quit")) {
                break;
            }

            if (!areParenthesesBalanced(inputString)) {
                System.out.println("Error: Parentesis desbalanceados.");
                continue;
            }

            List<String> tokens = getTokens(inputString);
            if (hasUnknownTokens(tokens)) {
                System.out.println("Error: Expresion contiene tokens no validos.");
                continue;
            }
            if (!isValidTokenSequence(tokens)) {
                System.out.println("Error: Secuencia de tokens no valida.");
                continue;
            }
            List<String> postfix = Calculadora.toPostfix(tokens);
            System.out.println("Notacion Postfija: " + Calculadora.toString(postfix));
            double result = evaluatePostfix(postfix);
            System.out.println("Resultado: " + result);
        }
    }

    public static boolean areParenthesesBalanced(String expression) {
        Stack<Character> stack = new Stack<>();
        for (char ch : expression.toCharArray()) {
            if (ch == '(') {
                stack.push(ch);
            } else if (ch == ')') {
                if (stack.isEmpty()) {
                    return false;
                }
                stack.pop();
            }
        }
        return stack.isEmpty();
    }

    public static boolean isValidTokenSequence(List<String> tokens) {
        String previousToken = null;
        for (String token : tokens) {
            if (isOperator(token) && (previousToken == null || isOperator(previousToken))) {
                return false;
            }
            previousToken = token;
        }
        return previousToken == null || !isOperator(previousToken);
    }

    public static boolean hasUnknownTokens(List<String> tokens) {
        for (String token : tokens) {
            if (!isOperator(token) && !isOperand(token) && !token.equals("(") && !token.equals(")")) {
                return true; // Token desconocido
            }
        }
        return false;
    }

    public static ArrayList<String> toPostfix(List<String> input) {
        Stack<String> stack = new Stack<>();
        ArrayList<String> output = new ArrayList<>();
        String t;

        for (String token : input) {
            if (token.equalsIgnoreCase("(")) {
                stack.push(token);
            } else if (token.equalsIgnoreCase(")")) {
                while (!(t = stack.pop()).equals("(")) {
                    output.add(t);
                }
            } else if (isOperand(token)) {
                output.add(token);
            }
            if (isOperator(token)) {
                if (stack.isEmpty()) {
                    stack.push(token);
                } else {
                    int r1 = Calculadora.getPrec(token);
                    int r2 = Calculadora.getPrec(stack.peek());

                    if (r1 > r2) {
                        stack.push(token);
                    } else {
                        output.add(stack.pop());
                        stack.push(token);
                    }
                }
            }
        }
        String token;
        while (!stack.isEmpty()) {
            token = stack.pop();
            output.add(token);
        }
        return output;
    }

    public static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();
        for (String token : postfix) {
            if (isOperand(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                double result = applyOperator(token, a, b);
                stack.push(result);
            }
        }
        return stack.pop();
    }

    public static double applyOperator(String operator, double a, double b) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Operador desconocido: " + operator);
        }
    }

    public static List<String> getTokens(String input) {
        StringTokenizer st = new StringTokenizer(input, " ()+-*/^", true);
        ArrayList<String> tokenList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.trim().length() == 0) {
                continue;
            }
            tokenList.add(token);
        }
        return tokenList;
    }

    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    public static boolean isOperand(String token) {
        boolean result = true;
        try {
            Double.parseDouble(token);
        } catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }

    public static int getPrec(String token) {
        String t = token.toLowerCase();
        int rank = 0;
        switch (t) {
            case "^":
                rank = 3;
                break;
            case "*":
            case "/":
                rank = 2;
                break;
            case "+":
            case "-":
                rank = 1;
                break;
        }
        return rank;
    }

    public static String toString(List<String> list) {
        StringBuilder output = new StringBuilder();
        for (String token : list) {
            output.append(token).append(" ");
        }
        return output.toString();
    }
}