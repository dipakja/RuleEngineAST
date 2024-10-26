package com.RuleEngine.ASTRuleEngine.controllers;

import com.RuleEngine.ASTRuleEngine.models.CombinedRules;
import com.RuleEngine.ASTRuleEngine.services.CombinedRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combined-rules")
public class CombinedRulesController {

    @Autowired
    private CombinedRulesService combinedRulesService;

    // Endpoint to combine two rules
    @PostMapping("/combine")
    public ResponseEntity<CombinedRules> combineRules(@RequestParam Long rule1Id, @RequestParam Long rule2Id, @RequestParam String operator) {
        CombinedRules combinedRules = combinedRulesService.createCombinedRule(rule1Id, rule2Id, CombinedRules.Operator.valueOf(operator));
        return new ResponseEntity<>(combinedRules, HttpStatus.CREATED);
    }

    // Endpoint to get all combined rules
    @GetMapping
    public ResponseEntity<List<CombinedRules>> getAllCombinedRules() {
        List<CombinedRules> combinedRulesList = combinedRulesService.getAllCombinedRules();
        return new ResponseEntity<>(combinedRulesList, HttpStatus.OK);
    }

    // Endpoint to get a specific combined rule by ID
    @GetMapping("/{id}")
    public ResponseEntity<CombinedRules> getCombinedRuleById(@PathVariable Long id) {
        CombinedRules combinedRule = combinedRulesService.findById(id);
        return new ResponseEntity<>(combinedRule, HttpStatus.OK);
    }

    // Endpoint to delete a combined rule
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCombinedRule(@PathVariable Long id) {
        combinedRulesService.deleteCombinedRule(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
