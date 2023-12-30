import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExpressionEvaluatorGUI extends JFrame {

    private final JTextField inputField;
    private final JTextArea postfixArea;
    private final JLabel resultLabel;

    public ExpressionEvaluatorGUI() {
        super("表达式求值运算器");

        // 输入框和计算按钮
        inputField = new JTextField(20);
        JButton evaluateButton = new JButton("计算");
        evaluateButton.addActionListener(e -> evaluateExpression());

        // 后缀表达式区域
        postfixArea = new JTextArea(5, 20);
        postfixArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(postfixArea);

        // 结果标签
        resultLabel = new JLabel();

        // 清空按钮
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearFields());

        // 布局
        JPanel inputPane = new JPanel(new FlowLayout());
        inputPane.add(new JLabel("表达式:"));
        inputPane.add(inputField);
        inputPane.add(evaluateButton);

        JPanel postfixPane = new JPanel(new BorderLayout());
        postfixPane.add(new JLabel("后缀表达式:"), BorderLayout.NORTH);
        postfixPane.add(scrollPane, BorderLayout.CENTER);

        JPanel resultPane = new JPanel(new FlowLayout());
        resultPane.add(resultLabel);

        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(clearButton);

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

    private void evaluateExpression() {
        String expression = inputField.getText();
        try {
            List<String> postfixExpression = ExpressionEvaluator.toPostfix(expression);
            double result = ExpressionEvaluator.evaluatePostfix(postfixExpression);
            postfixArea.setText(String.join(" ", postfixExpression));
            resultLabel.setText("Result: " + result);
        } catch (Exception e) {
            postfixArea.setText("");
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        inputField.setText("");
        postfixArea.setText("");
        resultLabel.setText("");
    }
}
