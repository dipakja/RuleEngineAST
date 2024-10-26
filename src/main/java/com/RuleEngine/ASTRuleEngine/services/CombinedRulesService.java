package com.RuleEngine.ASTRuleEngine.services;

import com.RuleEngine.ASTRuleEngine.exceptions.ResourceNotFoundException;
import com.RuleEngine.ASTRuleEngine.models.CombinedRules;
import com.RuleEngine.ASTRuleEngine.models.Rule;
import com.RuleEngine.ASTRuleEngine.repositories.CombinedRulesRepository;
import com.RuleEngine.ASTRuleEngine.repositories.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CombinedRulesService {
    @Autowired
    private CombinedRulesRepository combinedRulesRepository;

    @Autowired
    private RuleRepository ruleRepository;

    // Method to create a combined rule
    public CombinedRules createCombinedRule(Long rule1Id, Long rule2Id, CombinedRules.Operator operatorValue) {
        // Fetch both rules to ensure they exist
        Rule rule1 = ruleRepository.findById(rule1Id).orElseThrow(() -> new RuntimeException("Rule 1 not found"));
        Rule rule2 = ruleRepository.findById(rule2Id).orElseThrow(() -> new RuntimeException("Rule 2 not found"));

        // Create a combined rule entity
        CombinedRules combinedRule = new CombinedRules();
        combinedRule.setRule1Id(rule1Id);
        combinedRule.setRule2Id(rule2Id);
        combinedRule.setOperatorValue(operatorValue);

        // Save the combined rule
        return combinedRulesRepository.save(combinedRule);
    }

    // Method to retrieve a combined rule by ID
    public CombinedRules getCombinedRuleById(Long combinedRuleId) {
        return combinedRulesRepository.findById(combinedRuleId)
                .orElseThrow(() -> new RuntimeException("Combined Rule not found"));
    }

    public List<CombinedRules> getAllCombinedRules() {
        return combinedRulesRepository.findAll();
    }

    // Method to find a combined rule by ID
    public CombinedRules findById(Long id) {
        Optional<CombinedRules> combinedRule = combinedRulesRepository.findById(id);
        return combinedRule.orElseThrow(() -> new ResourceNotFoundException("Combined rule not found with ID: " + id));
    }

    public void deleteCombinedRule(Long combinedRuleId) {
        combinedRulesRepository.deleteById(combinedRuleId);
    }
}
