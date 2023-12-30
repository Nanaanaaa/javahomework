import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpressionEvaluatorGUI().setVisible(true)); // 设置窗口为可见状态
    }
}