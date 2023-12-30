
// 双目运算符接口

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface BinaryOperator {
    double apply(double leftOperand, double rightOperand); // 运算的接口

    int getPrecedence(); // 优先级的接口

    ExpressionEvaluator.Associativity getAssociativity(); // 运算结合性的接口
}

public class ExpressionEvaluator {

    // 这是固有运算符加减乘除和它们的优先级
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    static {
        OPERATOR_PRECEDENCE.put("+", 5);
        OPERATOR_PRECEDENCE.put("-", 5);
        OPERATOR_PRECEDENCE.put("*", 4);
        OPERATOR_PRECEDENCE.put("/", 4);
    }

    // 这是类的使用者可以自定义的运算符
    private static final Map<String, BinaryOperator> CUSTOM_OPERATORS = new HashMap<>();
    static {
        final double eps = 1e-6;
        // 自定义运算符: 取模
        CUSTOM_OPERATORS.put("%", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                return leftOperand % rightOperand;
            }

            @Override
            public int getPrecedence() {
                return 4;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });

        // 自定义运算符: 按位异或
        CUSTOM_OPERATORS.put("^", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
                double delta1 = Math.abs(t1 - leftOperand), delta2 = Math.abs(t2 - rightOperand);

                if (delta1 >= eps || delta2 >= eps) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                return t1 ^ t2;
            }

            @Override
            public int getPrecedence() {
                return 10;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });
        // 自定义运算符: 按位与
        CUSTOM_OPERATORS.put("&", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
                double delta1 = Math.abs(t1 - leftOperand), delta2 = Math.abs(t2 - rightOperand);

                if (delta1 >= eps || delta2 >= eps) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                return t1 & t2;
            }

            @Override
            public int getPrecedence() {
                return 9;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });
        // 自定义运算符: 按位或
        CUSTOM_OPERATORS.put("|", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
                double delta1 = Math.abs(t1 - leftOperand), delta2 = Math.abs(t2 - rightOperand);

                if (delta1 >= eps || delta2 >= eps) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                return t1 | t2;
            }

            @Override
            public int getPrecedence() {
                return 11;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });
    }

    // 运算符结合性
    enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    // 判断字符是否为数字
    private static boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    // 判断字符是否为运算符
    private static boolean isOperator(char ch) {
        boolean pred1 = OPERATOR_PRECEDENCE.containsKey(Character.toString(ch));
        boolean pred2 = CUSTOM_OPERATORS.containsKey(Character.toString(ch));
        return pred1 || pred2;
    }

    // 判断字符是否为左括号
    private static boolean isLeftParenthesis(char ch) {
        return ch == '(';
    }

    // 判断字符是否为右括号
    private static boolean isRightParenthesis(char ch) {
        return ch == ')';
    }

    // 将中缀表达式转换为后缀表达式
    public static List<String> toPostfix(String infixExpression) {
        List<String> postfixTokens = new ArrayList<>(); // 用于存储后缀表达式的token
        Deque<Character> operatorStack = new ArrayDeque<>(); // 用于存储运算符的栈
        final int N = infixExpression.length();

        for (int i = 0; i < N; i++) {
            char ch = infixExpression.charAt(i);
            if (ch == ' ') {
                continue;
            }
            if (isDigit(ch)) { // 如果是数字，读取整个数字并添加到后缀表达式中
                StringBuilder builder = new StringBuilder();
                builder.append(infixExpression.charAt(i));
                int j = i;
                while (j + 1 < N) {
                    final char c = infixExpression.charAt(j + 1);
                    if (isDigit(c) || c == '.') {
                        builder.append(c);
                        j++;
                    } else {
                        break;
                    }
                }
                i = j;
                postfixTokens.add(builder.toString());
            } else if (isOperator(ch)) { // 如果是运算符
                String operator = Character.toString(ch);
                BinaryOperator binaryOperator = CUSTOM_OPERATORS.get(operator);
                int precedence = (binaryOperator != null) ? binaryOperator.getPrecedence()
                        : OPERATOR_PRECEDENCE.get(operator);
                Associativity associativity = (binaryOperator != null) ? binaryOperator.getAssociativity()
                        : Associativity.LEFT_TO_RIGHT;

                // 将栈中优先级高于或等于当前运算符的运算符弹出并添加到后缀表达式中
                while (!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
                    String topOperator = Character.toString(operatorStack.peek());
                    BinaryOperator topBinaryOperator = CUSTOM_OPERATORS.get(topOperator);
                    int topPrecedence = (topBinaryOperator != null) ? topBinaryOperator.getPrecedence()
                            : OPERATOR_PRECEDENCE.get(topOperator);
                    boolean pred1 = associativity == Associativity.LEFT_TO_RIGHT && precedence >= topPrecedence;
                    boolean pred2 = associativity == Associativity.RIGHT_TO_LEFT && precedence > topPrecedence;
                    if (pred1 || pred2) {
                        postfixTokens.add(Character.toString(operatorStack.pop()));
                    } else {
                        break;
                    }
                }

                // 将当前运算符压入栈中
                operatorStack.push(ch);
            } else if (isLeftParenthesis(ch)) { // 如果是左括号，将其压入栈中
                operatorStack.push(ch);
            } else if (isRightParenthesis(ch)) { // 如果是右括号，将栈中的运算符弹出并添加到后缀表达式中，直到碰到左括号为止
                while (!operatorStack.isEmpty() && !isLeftParenthesis(operatorStack.peek())) {
                    postfixTokens.add(Character.toString(operatorStack.pop()));
                }
                if (!operatorStack.isEmpty() && isLeftParenthesis(operatorStack.peek())) {
                    operatorStack.pop(); // 弹出左括号
                } else {
                    throw new IllegalArgumentException("Unmatched right parenthesis");
                }
            } else {
                throw new IllegalArgumentException("Invalid character: " + ch);
            }
        }

        // 将栈中的运算符弹出并添加到后缀表达式中
        while (!operatorStack.isEmpty()) {
            if (isLeftParenthesis(operatorStack.peek())) {
                throw new IllegalArgumentException("Unmatched left parenthesis");
            }
            postfixTokens.add(Character.toString(operatorStack.pop()));
        }

        return postfixTokens;
    }

    // 计算后缀表达式的值
    public static double evaluatePostfix(List<String> postfixExpression) {
        Deque<Double> operandStack = new ArrayDeque<>();

        for (String token : postfixExpression) {
            if (isOperator(token.charAt(0))) {
                BinaryOperator binaryOperator = CUSTOM_OPERATORS.get(token);
                double rightOperand = operandStack.pop();
                double leftOperand = operandStack.pop();
                if (binaryOperator == null) {
                    operandStack.push(evaluate(leftOperand, rightOperand, token));
                } else {
                    operandStack.push(binaryOperator.apply(leftOperand, rightOperand));
                }
            } else {
                operandStack.push(Double.parseDouble(token));
            }
        }

        if (operandStack.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return operandStack.pop();
    }

    // 计算两个操作数之间的值
    private static double evaluate(double leftOperand, double rightOperand, String operator) {
        return switch (operator) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "*" -> leftOperand * rightOperand;
            case "/" -> leftOperand / rightOperand;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}
