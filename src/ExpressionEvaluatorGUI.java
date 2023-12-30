import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExpressionEvaluatorGUI extends JFrame {

    private final JTextField inputField; // 输入和计算按钮
    private final JTextArea postfixArea; // 后缀表达式区域
    private final JTextArea resultArea; // 计算结果区域

    public ExpressionEvaluatorGUI() {
        super("表达式求值运算器");

        // 输入框和计算按钮
        inputField = new JTextField(25);
        JButton evaluateButton = new JButton("计算");
        evaluateButton.addActionListener(e -> evaluateExpression());

        // 后缀表达式区域
        postfixArea = new JTextArea(2, 25);
        postfixArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(postfixArea);

        // 结果区域
        resultArea = new JTextArea(2, 25);
        resultArea.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(resultArea);

        // 清空按钮
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearFields());

        // 布局
        JPanel inputPane = new JPanel(new FlowLayout());
        inputPane.add(new JLabel("表达式:"));
        inputPane.add(inputField);
        inputPane.add(evaluateButton);

        // 后缀
        JPanel postfixPane = new JPanel(new BorderLayout());
        postfixPane.add(new JLabel("后缀表达式:"), BorderLayout.NORTH);
        postfixPane.add(scrollPane, BorderLayout.CENTER);

        // 結果
        JPanel resultPane = new JPanel(new BorderLayout());
        resultPane.add(new JLabel("运算结果:"), BorderLayout.NORTH);
        resultPane.add(scrollPane2, BorderLayout.CENTER);

        // 底部按鈕
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(clearButton);

        // 整体
        JPanel contentPane = new JPanel(new GridLayout(4, 1, 10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(inputPane);
        contentPane.add(postfixPane);
        contentPane.add(resultPane);
        contentPane.add(buttonPane);

        setContentPane(contentPane);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // 输入
    private void evaluateExpression() {
        String expression = inputField.getText();
        try {
            List<String> postfixExpression = ExpressionEvaluator.toPostfix(expression);
            double result = ExpressionEvaluator.evaluatePostfix(postfixExpression);
            postfixArea.setText(String.join(" ", postfixExpression));
            resultArea.setText(Double.toString(result));
        } catch (Exception e) {
            postfixArea.setText("");
            resultArea.setText("Error: " + e.getMessage());
        }
    }

    // 清空
    private void clearFields() {
        inputField.setText("");
        postfixArea.setText("");
        resultArea.setText("");
    }
}