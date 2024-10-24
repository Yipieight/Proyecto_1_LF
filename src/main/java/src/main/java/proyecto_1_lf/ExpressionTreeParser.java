package src.main.java.proyecto_1_lf;

import java.util.Stack;

class ExpressionTreeParser {
    private int idCounter = 1;  
    
    public TreeNode parse(String regex) {
        Stack<TreeNode> operands = new Stack<>();
        Stack<String> operators = new Stack<>();  

        for (int i = 0; i < regex.length(); i++) {
            char token = regex.charAt(i);

            if (Character.isLetterOrDigit(token) || isSpecialSymbol(token)) {  
                StringBuilder value = new StringBuilder();
                
                while (i < regex.length() && (Character.isLetterOrDigit(regex.charAt(i)) || isSpecialSymbol(regex.charAt(i)))) {
                    if (regex.charAt(i) == '\'') {
                        value.append('\'');  
                        i++;  
                
                        if (i < regex.length() && (isOperator(regex.charAt(i)) || regex.charAt(i) == '(' || regex.charAt(i) == ')')) {
                            value.append(regex.charAt(i));  
                            i++;  
                
                            if (i < regex.length() && regex.charAt(i) == '\'') {
                                value.append('\'');  
                            } else {
                                value.setLength(value.length() - 1);  
                                i = i-2;  
                            }
                        } else {
                            value.setLength(value.length() - 1);
                            i = i-2;  
                        }
                    } else {
                        value.append(regex.charAt(i));
                    }
                    i++;
                }
                
                
                
                if (i < regex.length() && regex.charAt(i) == '(') {
                    value.append('(');
                    i++;
                    int parenCount = 1;  
                    while (i < regex.length() && parenCount > 0) {
                        value.append(regex.charAt(i));
                        if (regex.charAt(i) == '(') parenCount++;
                        if (regex.charAt(i) == ')') parenCount--;
                        i++;
                    }
                }

                i--; 
                operands.push(new OperandNode(value.toString(), idCounter++));  
                
                if (i + 1 < regex.length() && (regex.charAt(i + 1) == '(' || Character.isLetterOrDigit(regex.charAt(i + 1)))) {
                    operators.push(".");  
                }
            } 
            else if (token == '(') {
                operators.push(String.valueOf(token)); 
            } else if (token == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    processOperator(operands, operators.pop());
                }
                operators.pop(); 
                if (i + 1 < regex.length() && (regex.charAt(i + 1) == '(' || Character.isLetterOrDigit(regex.charAt(i + 1)))) {
                    operators.push(".");  
                }
            } 
            else if (isOperator(token)) {
                while (!operators.isEmpty() && !operators.peek().equals("(") && precedence(operators.peek()) >= precedence(String.valueOf(token))) {
                    processOperator(operands, operators.pop());
                }
                operators.push(String.valueOf(token)); 
            }
        }

        while (!operators.isEmpty()) {
            if (operators.peek().equals("(")) {
                throw new IllegalStateException("Error: paréntesis sin cerrar");
            }
            processOperator(operands, operators.pop());
        }
        
        if (operands.size() != 1) {
            throw new IllegalStateException("Error: falta de operandos al final");
        }

        return operands.pop();
    }

    private boolean isSpecialSymbol(char token) {
        return "=[]{}^'#%&/!<>;:\"~$¡´°-_,".indexOf(token) != -1;  
    }


    private boolean isOperator(char token) {
        return token == '*' || token == '+' || token == '?' || token == '|' || token == '.';
    }

    private int precedence(String operator) {
        switch (operator) {
            case "*":
            case "+": 
            case "?":
                return 3;
            case ".":
                return 2;
            case "|":
                return 1;
            default:
                return 0;
        }
    }

    private void processOperator(Stack<TreeNode> operands, String operator) {
        if (operator.equals("*") || operator.equals("?") || operator.equals("+")) {
            TreeNode child = operands.pop();
            operands.push(new UnaryOperatorNode(operator, child));
        } else {
            TreeNode right = operands.pop();
            TreeNode left = operands.pop();
            operands.push(new OperatorNode(operator, left, right));
        }
    }

   
}
