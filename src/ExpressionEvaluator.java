import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

// 双目运算符接口
interface BinaryOperator {
    double apply(double leftOperand, double rightOperand); // 运算的接口, 返回两个值的运算结果

    int getPrecedence(); // 优先级的接口, 数字越小代表优先级越高

    ExpressionEvaluator.Associativity getAssociativity(); // 运算结合性的接口
}

public class ExpressionEvaluator {
    private static final double eps = 1e-9; // 精度

    // 默认的所有类的使用者可以自定义的常用运算符, 类的使用者的可以自行添加需要定义的运算符
    private static final HashSet<Character> OPSET = new HashSet<>();
    static {
        OPSET.add('~');
        OPSET.add('+');
        OPSET.add('-');
        OPSET.add('*');
        OPSET.add('/');
        OPSET.add('?');
        OPSET.add('>');
        OPSET.add('<');
        OPSET.add('!');
        OPSET.add('$');
        OPSET.add('%');
        OPSET.add('^');
        OPSET.add('&');
        OPSET.add('=');
        OPSET.add('|');
    }
    // 这是固有运算符加减乘除和它们的优先级
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    static {
        OPERATOR_PRECEDENCE.put("+", 5);
        OPERATOR_PRECEDENCE.put("-", 5);
        OPERATOR_PRECEDENCE.put("*", 4);
        OPERATOR_PRECEDENCE.put("/", 4);
    }

    private static final boolean isValid(double a, double b) {
        long t1 = Math.round(a), t2 = Math.round(b);
        double delta1 = Math.abs(t1 - a), delta2 = Math.abs(t2 - b);
        return delta1 <= eps && delta2 <= eps;
    }

    // 这是类的使用者自定义的运算符的区域
    private static final Map<String, BinaryOperator> CUSTOM_OPERATORS = new HashMap<>();
    static {

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
                if (!isValid(leftOperand, rightOperand)) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
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
                if (!isValid(leftOperand, rightOperand)) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
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
                if (!isValid(leftOperand, rightOperand)) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
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

        // 自定义运算符: 乘方
        CUSTOM_OPERATORS.put("**", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                return Math.pow(leftOperand, rightOperand);
            }

            @Override
            public int getPrecedence() {
                return 6;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });

        // 自定义运算符: 左移
        CUSTOM_OPERATORS.put("<<", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                if (!isValid(leftOperand, rightOperand)) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
                return t1 << t2;
            }

            @Override
            public int getPrecedence() {
                return 6;
            }

            @Override
            public Associativity getAssociativity() {
                return Associativity.RIGHT_TO_LEFT;
            }
        });
        // 自定义运算符: 右移
        CUSTOM_OPERATORS.put(">>", new BinaryOperator() {
            @Override
            public double apply(double leftOperand, double rightOperand) {
                if (!isValid(leftOperand, rightOperand)) {
                    throw new UnsupportedOperationException("Unimplemented method 'apply'");
                }
                long t1 = Math.round(leftOperand), t2 = Math.round(rightOperand);
                return t1 >> t2;
            }

            @Override
            public int getPrecedence() {
                return 3;
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
    private static boolean isOperator(String s) {
        boolean pred1 = OPERATOR_PRECEDENCE.containsKey(s);
        boolean pred2 = CUSTOM_OPERATORS.containsKey(s);
        return pred1 || pred2;
    }

    // 将中缀表达式转换为后缀表达式
    public static List<String> toPostfix(String infixExpression) {
        List<String> postfixTokens = new ArrayList<>(); // 用于存储后缀表达式的token
        Deque<String> operatorStack = new ArrayDeque<>(); // 用于存储运算符的栈
        final int N = infixExpression.length();

        for (int i = 0; i < N; i++) {
            final char ch = infixExpression.charAt(i);
            if (ch == ' ') {
                continue;
            }
            StringBuilder s = new StringBuilder(Character.toString(ch));
            if (isDigit(ch)) { // 如果是数字，读取整个数字并添加到后缀表达式中
                int[] cnt = new int[2];
                while (i + 1 < N) {
                    final char c = infixExpression.charAt(i + 1);
                    if (isDigit(c) || c == '.' || c == 'e' || c == 'E') {
                        if (c == 'e' || c == 'E') {
                            cnt[0]++;
                        } else if (c == '.') {
                            cnt[1]++;
                        }
                        s.append(c);
                        i++;
                    } else {
                        break;
                    }
                }
                if (cnt[0] > 1 || cnt[1] > 1) {
                    throw new IllegalArgumentException("Invalid character");
                }
                postfixTokens.add(s.toString());
                continue;
            } else if (ch == '(') { // 如果是左括号，将其压入栈中
                operatorStack.push("(");
                continue;
            } else if (ch == ')') { // 如果是右括号，将栈中的运算符弹出并添加到后缀表达式中，直到碰到左括号为止
                while (!operatorStack.isEmpty() && operatorStack.peek() != "(") {
                    postfixTokens.add(operatorStack.pop());
                }
                if (!operatorStack.isEmpty() && operatorStack.peek() == "(") {
                    operatorStack.pop(); // 弹出左括号
                } else {
                    throw new IllegalArgumentException("Unmatched right parenthesis");
                }
                continue;
            }
            // 接下来看是什么运算符
            while (i + 1 < N && OPSET.contains(infixExpression.charAt(i + 1))) {
                s.append(infixExpression.charAt(i + 1));
                i++;
            }

            String operator = s.toString();

            if (!isOperator(operator)) {
                throw new IllegalArgumentException("Invalid operator");
            }
            BinaryOperator binaryOperator = CUSTOM_OPERATORS.get(operator);
            int precedence = (binaryOperator != null)
                    ? binaryOperator.getPrecedence()
                    : OPERATOR_PRECEDENCE.get(operator);
            Associativity associativity = (binaryOperator != null)
                    ? binaryOperator.getAssociativity()
                    : Associativity.LEFT_TO_RIGHT;

            // 将栈中优先级高于或等于当前运算符的运算符弹出并添加到后缀表达式中
            while (!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
                String topOperator = operatorStack.peek();
                BinaryOperator topBinaryOperator = CUSTOM_OPERATORS.get(topOperator);
                int topPrecedence = (topBinaryOperator != null)
                        ? topBinaryOperator.getPrecedence()
                        : OPERATOR_PRECEDENCE.get(topOperator);
                boolean pred1 = (associativity == Associativity.LEFT_TO_RIGHT) && (precedence >= topPrecedence);
                boolean pred2 = (associativity == Associativity.RIGHT_TO_LEFT) && (precedence > topPrecedence);
                if (pred1 || pred2) {
                    postfixTokens.add(operatorStack.pop());
                } else {
                    break;
                }
            }
            // 将当前运算符压入栈中
            operatorStack.push(operator);
        }

        // 将栈中的运算符弹出并添加到后缀表达式中
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek() == "(") {
                throw new IllegalArgumentException("Unmatched left parenthesis");
            }
            postfixTokens.add(operatorStack.pop());
        }

        return postfixTokens;
    }

    // 计算后缀表达式的值
    public static double evaluatePostfix(List<String> postfixExpression) {
        Deque<Double> operandStack = new ArrayDeque<>();

        for (String token : postfixExpression) {
            if (isOperator(token)) {
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

    // 计算两个操作数之间的值(固有的加减乘除运算)
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