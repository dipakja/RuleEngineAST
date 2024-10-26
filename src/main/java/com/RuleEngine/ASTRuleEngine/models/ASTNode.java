package com.RuleEngine.ASTRuleEngine.models;


import jakarta.persistence.*;

@Entity
@Table(name = "ast_nodes")
public class ASTNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type; // "operator" or "operand"

    @Column(name = "operator_value")
    @Enumerated(EnumType.STRING)
    private Operator operatorValue; // Enum for "AND" or "OR"

    @Column(name = "operand_value")
    private String operandValue; // For conditions (e.g., "age > 30")

    @Column(name = "parent_id")
    private Long parentId; // References parent node

    @Column(name = "left_child_id")
    private Long leftChildId; // References left child node

    @Column(name = "right_child_id")
    private Long rightChildId; // References right child node

    @Column(name = "rule_id", nullable = false)
    private Long ruleId; // Foreign key to the Rule entity

    // Constructors
    public ASTNode() {
    }

    public ASTNode(String type, Operator operatorValue, String operandValue, Long parentId, Long leftChildId, Long rightChildId, Long ruleId) {
        this.type = type;
        this.operatorValue = operatorValue;
        this.operandValue = operandValue;
        this.parentId = parentId;
        this.leftChildId = leftChildId;
        this.rightChildId = rightChildId;
        this.ruleId = ruleId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Operator getOperatorValue() {
        return operatorValue;
    }

    public void setOperatorValue(Operator operatorValue) {
        this.operatorValue = operatorValue;
    }

    public String getOperandValue() {
        return operandValue;
    }

    public void setOperandValue(String operandValue) {
        this.operandValue = operandValue;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLeftChildId() {
        return leftChildId;
    }

    public void setLeftChildId(Long leftChildId) {
        this.leftChildId = leftChildId;
    }

    public Long getRightChildId() {
        return rightChildId;
    }

    public void setRightChildId(Long rightChildId) {
        this.rightChildId = rightChildId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    // Enum for operator values
    public enum Operator {
        AND,
        OR
    }
}
