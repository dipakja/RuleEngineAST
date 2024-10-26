package com.RuleEngine.ASTRuleEngine.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "combined_rules")
public class CombinedRules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_1_id", nullable = false)
    private Long rule1Id; // Foreign key referencing the first rule

    @Column(name = "rule_2_id", nullable = false)
    private Long rule2Id; // Foreign key referencing the second rule

    @Column(name = "operator_value", nullable = false)
    @Enumerated(EnumType.STRING)
    private Operator operatorValue; // Enum for "AND" or "OR"

    @Column(name = "combined_rule_id")
    private Long combinedRuleId; // Foreign key referencing the combined rule

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Timestamp for creation

    // Constructors
    public CombinedRules() {
    }

    public CombinedRules(Long rule1Id, Long rule2Id, Operator operatorValue, Long combinedRuleId) {
        this.rule1Id = rule1Id;
        this.rule2Id = rule2Id;
        this.operatorValue = operatorValue;
        this.combinedRuleId = combinedRuleId;
        this.createdAt = LocalDateTime.now(); // Set the createdAt timestamp
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRule1Id() {
        return rule1Id;
    }

    public void setRule1Id(Long rule1Id) {
        this.rule1Id = rule1Id;
    }

    public Long getRule2Id() {
        return rule2Id;
    }

    public void setRule2Id(Long rule2Id) {
        this.rule2Id = rule2Id;
    }

    public Operator getOperatorValue() {
        return operatorValue;
    }

    public void setOperatorValue(Operator operatorValue) {
        this.operatorValue = operatorValue;
    }

    public Long getCombinedRuleId() {
        return combinedRuleId;
    }

    public void setCombinedRuleId(Long combinedRuleId) {
        this.combinedRuleId = combinedRuleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Enum for operator values
    public enum Operator {
        AND,
        OR
    }
}
