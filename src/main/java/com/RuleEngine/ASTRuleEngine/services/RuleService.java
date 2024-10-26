package com.RuleEngine.ASTRuleEngine.services;

import com.RuleEngine.ASTRuleEngine.exceptions.ResourceNotFoundException;
import com.RuleEngine.ASTRuleEngine.models.ASTNode;
import com.RuleEngine.ASTRuleEngine.models.Rule;
import com.RuleEngine.ASTRuleEngine.repositories.ASTNodeRepository;
import com.RuleEngine.ASTRuleEngine.repositories.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Service
public class RuleService {
    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ASTNodeRepository astNodeRepository;

    // Method to create a new rule
    public Rule createRule(String ruleString) {
        // Validate the rule string before creating
        validateRuleString(ruleString);

        // Step 1: Create Rule entity
        Rule rule = new Rule();
        rule.setRuleString(ruleString);

        // Step 2: Save the rule to the database to get the generated ID
        Rule savedRule = ruleRepository.save(rule);

        // Step 3: Create AST with the saved rule's ID
        ASTNode rootNode = createASTFromRuleString(ruleString, savedRule.getId());
        rootNode.setRuleId(savedRule.getId()); // Set the rule ID for the root node

        // Step 4: Save the root node to the database
        ASTNode savedNode = astNodeRepository.save(rootNode);

        // Step 5: Update the rule with the root node ID
        savedRule.setRootNodeId(savedNode.getId());
        return ruleRepository.save(savedRule);
    }

    // New validation method for rule strings
    private void validateRuleString(String ruleString) {
        // Simple validation to check for empty or malformed rules
        if (ruleString == null || ruleString.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule string cannot be null or empty.");
        }
        // Further validation can be added here as needed
    }

    private ASTNode createASTFromRuleString(String ruleString, Long ruleId) {
        // Step 1: Tokenize the rule string
        String[] tokens = tokenize(ruleString);

        // Step 2: Parse tokens to create AST
        return parseTokens(tokens, ruleId);
    }

    // Tokenization logic
    private String[] tokenize(String ruleString) {
        // Handle cases with parentheses and operators; regex can be useful
        return ruleString
                .replaceAll("\\(", " ( ")
                .replaceAll("\\)", " ) ")
                .replaceAll(">", " > ")
                .replaceAll("<", " < ")
                .replaceAll("=", " = ")
                .replaceAll("AND", " AND ")
                .replaceAll("OR", " OR ")
                .trim()
                .split("\\s+");
    }

    // Parsing logic to create the AST
    private ASTNode parseTokens(String[] tokens, Long ruleId) {
        Stack<ASTNode> nodeStack = new Stack<>();
        Stack<String> opStack = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.equals("(")) {
                opStack.push(token);
            } else if (token.equals(")")) {
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
                    nodeStack.push(createNodeFromOperator(opStack.pop(), nodeStack, ruleId));
                }
                opStack.pop(); // Remove the '(' from the stack
            } else if (token.equals("AND") || token.equals("OR")) {
                while (!opStack.isEmpty() && precedence(opStack.peek()) >= precedence(token)) {
                    nodeStack.push(createNodeFromOperator(opStack.pop(), nodeStack, ruleId));
                }
                opStack.push(token); // Push the current operator
            } else {
                // Handle binary conditions (attribute operator value)
                if (i + 2 < tokens.length && (tokens[i + 1].equals(">") || tokens[i + 1].equals("<") || tokens[i + 1].equals("=") || tokens[i + 1].equals("!="))) {
                    // Create a single operand node with the complete condition
                    String condition = tokens[i] + " " + tokens[i + 1] + " " + tokens[i + 2];
                    nodeStack.push(createOperandNode(condition, ruleId));
                    i += 2; // Skip the next two tokens as they are part of this condition
                } else {
                    // Handle simple attributes without conditions
                    nodeStack.push(createOperandNode(token, ruleId));
                }
            }
        }

        while (!opStack.isEmpty()) {
            nodeStack.push(createNodeFromOperator(opStack.pop(), nodeStack, ruleId));
        }

        return nodeStack.isEmpty() ? null : nodeStack.pop();
    }


    private void validateOperandFormat(String operand) {
        String[] tokens = operand.split(" ");

        // Check if the condition is a single attribute, e.g., "department"
        if (tokens.length == 1) {
            return; // Single attributes are valid without further checks
        }

        // Ensure binary condition format (attribute operator value)
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + operand);
        }

        String operator = tokens[1];
        // Validate the operator (supports >, <, ==, != for now)
        if (!(operator.equals(">") || operator.equals("<") || operator.equals("==") || operator.equals("!="))) {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }



    private ASTNode createNodeFromOperator(String operator, Stack<ASTNode> nodeStack, Long ruleId) {
        ASTNode operatorNode = new ASTNode();
        operatorNode.setType("operator");
        operatorNode.setOperatorValue(ASTNode.Operator.valueOf(operator));
        operatorNode.setRuleId(ruleId);

        ASTNode rightChild = null;
        ASTNode leftChild = null;

        if (!nodeStack.isEmpty()) {
            rightChild = nodeStack.pop();
            rightChild = astNodeRepository.save(rightChild);
            operatorNode.setRightChildId(rightChild.getId());
        }

        if (!nodeStack.isEmpty()) {
            leftChild = nodeStack.pop();
            leftChild = astNodeRepository.save(leftChild);
            operatorNode.setLeftChildId(leftChild.getId());
        }

        operatorNode = astNodeRepository.save(operatorNode);

        if (leftChild != null) {
            leftChild.setParentId(operatorNode.getId());
            astNodeRepository.save(leftChild);
        }
        if (rightChild != null) {
            rightChild.setParentId(operatorNode.getId());
            astNodeRepository.save(rightChild);
        }

        return operatorNode;
    }

    private ASTNode createOperandNode(String operand, Long ruleId) {
        ASTNode operandNode = new ASTNode();
        operandNode.setType("operand");
        operandNode.setRuleId(ruleId);

        String[] tokens = operand.split(" ");

        if (tokens.length == 1) {
            // Single attribute case (e.g., "department")
            operandNode.setOperandValue(operand);
        } else if (tokens.length == 3) {
            // Binary condition case (e.g., "age > 30")
            String attribute = tokens[0];
            String operator = tokens[1];
            String value = tokens[2];

            // Remove quotes if the value is in quotes (e.g., 'Sales')
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }

            // Store the complete condition in the operand value
            operandNode.setOperandValue(attribute + " " + operator + " " + value);
        } else {
            throw new IllegalArgumentException("Invalid condition format: " + operand);
        }

        return operandNode;
    }


    private int precedence(String operator) {
        switch (operator) {
            case "AND":
                return 1;
            case "OR":
                return 0;
            default:
                return -1;
        }
    }

    private boolean evaluateAST(ASTNode node, Map<String, Object> attributes) {
        if (node == null) {
            return false; // Handle null node case
        }

        // Log node type and values
        System.out.println("Evaluating AST Node: " + node.getType() + " with ID: " + node.getId());

        // Handle operands and conditions
        if (node.getType().equals("operand")) {
            // If there's no operator value, it's just an operand
            if (node.getOperatorValue() == null) {
                String attributeName = node.getOperandValue(); // Assuming operandValue corresponds to the attribute name
                return attributes.containsKey(attributeName); // Check if the attribute is present
            } else {
                // It's a condition; evaluate using the operator
                String operator = node.getOperatorValue().name(); // Convert enum to String
                String operandValue = node.getOperandValue(); // This should be the numeric value
                String attributeName = node.getLeftChildId() != null ? node.getLeftChildId().toString() : null; // Assume leftChildId corresponds to attribute name

                // If there's a valid attribute name, evaluate it
                if (attributeName != null) {
                    return evaluateNumericOperand(operator, operandValue, attributes, attributeName);
                }
            }
        }

        // Recursively evaluate left and right children
        boolean leftResult = evaluateAST(astNodeRepository.findById(node.getLeftChildId()).orElse(null), attributes);
        boolean rightResult = evaluateAST(astNodeRepository.findById(node.getRightChildId()).orElse(null), attributes);

        // Log results of left and right children
        System.out.println("Left Result: " + leftResult + ", Right Result: " + rightResult);

        // Combine results based on the logical operator
        ASTNode.Operator operator = node.getOperatorValue(); // This is now an Operator enum
        if (operator != null) { // Ensure operator is not null before comparing
            if (ASTNode.Operator.AND.equals(operator)) {
                return leftResult && rightResult;
            } else if (ASTNode.Operator.OR.equals(operator)) {
                return leftResult || rightResult;
            }
        }

        return false; // If no valid operator is found, return false
    }


    private boolean evaluateNumericOperand(String operator, String operandValue, Map<String, Object> data, String attributeName) {
        // Ensure the attribute exists in the data
        if (!data.containsKey(attributeName)) {
            return false; // Attribute not found
        }

        // Get the actual value for the attribute from the data
        double dataValue;
        try {
            dataValue = Double.parseDouble(data.get(attributeName).toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid data value for attribute: " + attributeName);
        }

        double numericValue;
        try {
            numericValue = Double.parseDouble(operandValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid operand value: " + operandValue);
        }

        // Perform the comparison based on the operator
        switch (operator) {
            case ">":
                return dataValue > numericValue;
            case ">=":
                return dataValue >= numericValue;
            case "<":
                return dataValue < numericValue;
            case "<=":
                return dataValue <= numericValue;
            case "=":
                return dataValue == numericValue;
            case "==":
                return dataValue == numericValue; // Added for equality check
            // Add more operators as needed
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    public boolean evaluateCondition(String condition, Map<String, Object> data) {
        // Example condition: "salary > 5000"
        String[] tokens = condition.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid condition format: " + condition);
        }

        String attributeName = tokens[0];
        String operator = tokens[1];
        String operandValue = tokens[2];

        // Extract the actual data value using the attribute name
        Object dataValue = data.get(attributeName);
        if (dataValue == null) {
            return false; // Attribute not found in data
        }

        // Perform the actual comparison (this may require more complex parsing and type handling)
        return compareValues(dataValue, operator, operandValue);
    }

    private boolean compareValues(Object dataValue, String operator, String operandValue) {
        // Check if dataValue and operandValue are numeric
        boolean isNumericComparison = isNumeric(dataValue) && isNumeric(operandValue);

        if (isNumericComparison) {
            double dataNum = Double.parseDouble(dataValue.toString());
            double operandNum = Double.parseDouble(operandValue);

            switch (operator) {
                case ">":
                    return dataNum > operandNum;
                case "=":
                    return dataNum == operandNum;
                case "<":
                    return dataNum < operandNum;
                // Add more numerical cases as needed
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        } else {
            // Handle string comparison
            String dataStr = dataValue.toString();
            switch (operator) {
                case "=":
                    return dataStr.equals(operandValue);
                // Add more cases for string comparison as needed
                default:
                    throw new IllegalArgumentException("Unknown operator for string comparison: " + operator);
            }
        }
    }
    private boolean isNumeric(Object value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }




    private String[] splitCondition(String condition) {
        return condition.split("(?<=\\s|\\b)(=|>|<|!=)(?=\\s|\\b)|\\s+");
    }



    public Rule getRuleById(Long id) {
        return ruleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
    }

    public Rule updateRule(Long id, String ruleString) {
        validateRuleString(ruleString); // Validate before updating
        Rule existingRule = ruleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
        existingRule.setRuleString(ruleString);

        ASTNode rootNode = createASTFromRuleString(ruleString, id);
        rootNode.setRuleId(existingRule.getId());
        astNodeRepository.save(rootNode);

        existingRule.setRootNodeId(rootNode.getId());
        return ruleRepository.save(existingRule);
    }

    public void deleteRule(Long id) {
        Rule existingRule = ruleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Rule not found"));
        ruleRepository.delete(existingRule);
    }

    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    public boolean evaluateRule(Long ruleId, Map<String, Object> attributes) {
        try {
            if (attributes == null || attributes.isEmpty()) {
                throw new IllegalArgumentException("Attributes must not be null or empty");
            }

            Rule rule = ruleRepository.findById(ruleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rule not found"));

            ASTNode rootNode = astNodeRepository.findById(rule.getRootNodeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Root node not found for rule"));

            return evaluateAST(rootNode, attributes);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating rule: " + e.getMessage(), e);
        }
    }
}
