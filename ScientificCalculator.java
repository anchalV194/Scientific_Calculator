import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField textField;
    private final Map<String, String> functionAliases = Map.of("|x|", "abs", "!", "fact");

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(300, 40));
        add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(9, 4));
        buttonPanel.setBackground(Color.ORANGE);
        buttonPanel.setForeground(Color.PINK);

        String[] buttonLabels = {
            "1", "2", "3", "/", "4", "5", "6", "*", "7", "8", "9", "-",
            "0", ".", "=", "+", "Clear", "(", ")", "^", "sqrt", "cbrt", 
            "log", "sin", "cos", "tan", "asin", "acos", "atan", "!", "%", "|x|"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        String expression = textField.getText();

        switch (command) {
            case "=":
                try {
                    double result = evaluateExpression(expression);
                    textField.setText(Double.toString(result));
                } catch (Exception e) {
                    textField.setText("Error: " + e.getMessage());
                }
                break;
            case "Clear":
                textField.setText("");
                break;
            case "<=":
                if (!expression.isEmpty()) {
                    String newExpression = expression.substring(0, expression.length() - 1);
                    textField.setText(newExpression);
                }
                break;
            default:
                textField.setText(expression + command);
                break;
        }
    }

    private double evaluateExpression(String expression) {
        ExpressionParser parser = new ExpressionParser();
        return parser.parse(expression);
    }

    private static class ExpressionParser {
        private int pos = -1;
        private int ch;
        private String input;

        public double parse(String str) {
            this.input = str;
            nextChar();
            double x = parseExpression();
            if (pos < input.length()) throw new RuntimeException("Unexpected: " + (char) ch);
            return x;
        }

        private void nextChar() {
            ch = (++pos < input.length()) ? input.charAt(pos) : -1;
        }

        private boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        private double parseExpression() {
            double x = parseTerm();
            while (true) {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        private double parseTerm() {
            double x = parseFactor();
            while (true) {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) x /= parseFactor();
                else if (eat('^')) x = Math.pow(x, parseFactor());
                else return x;
            }
        }

        private double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double x;
            int startPos = this.pos;
            if (eat('(')) {
                x = parseExpression();
                eat(')');
            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(input.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') {
                while (ch >= 'a' && ch <= 'z') nextChar();
                String func = input.substring(startPos, this.pos);
                x = parseFactor();
                switch (func) {
    case "sqrt":
        x = Math.sqrt(x);
        break;
    case "cbrt":
        x = Math.cbrt(x);
        break;
    case "log":
        x = Math.log10(x);
        break;
    case "sin":
        x = Math.sin(Math.toRadians(x));
        break;
    case "cos":
        x = Math.cos(Math.toRadians(x));
        break;
    case "tan":
        x = Math.tan(Math.toRadians(x));
        break;
    case "asin":
        x = Math.toDegrees(Math.asin(x));
        break;
    case "acos":
        x = Math.toDegrees(Math.acos(x));
        break;
    case "atan":
        x = Math.toDegrees(Math.atan(x));
        break;
    case "abs":
        x = Math.abs(x);
        break;
    case "fact":
        x = factorial((int) x);
        break;
    case "%":
        x = x / 100.0;
        break;
    default:
        throw new RuntimeException("Unknown function: " + func);
}

            } else {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            return x;
        }

        private int factorial(int n) {
            if (n < 0) throw new RuntimeException("Factorial of negative number");
            if (n == 0) return 1;
            int fact = 1;
            for (int i = 1; i <= n; i++) fact *= i;
            return fact;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}
