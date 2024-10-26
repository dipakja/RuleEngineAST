package com.RuleEngine.ASTRuleEngine.controllers;

import com.RuleEngine.ASTRuleEngine.models.Rule;
import com.RuleEngine.ASTRuleEngine.services.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    // Endpoint to create a new rule
    @PostMapping
    public ResponseEntity<Rule> createRule(@RequestBody Map<String, String> ruleRequest) {
        String ruleString = ruleRequest.get("ruleString");
        if (ruleString == null || ruleString.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Rule createdRule = ruleService.createRule(ruleString);
        return ResponseEntity.ok(createdRule);
    }

    // Endpoint to get a rule by ID
    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRuleById(@PathVariable Long id) {
        Optional<Rule> rule = Optional.ofNullable(ruleService.getRuleById(id));
        return rule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to evaluate a rule
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<Boolean> evaluateRule(
            @PathVariable Long id,
            @RequestBody Map<String, Object> attributes) {
        boolean result = ruleService.evaluateRule(id, attributes);
        return ResponseEntity.ok(result);
    }

    // Endpoint to update a rule
    @PutMapping("/{id}")
    public ResponseEntity<Rule> updateRule(
            @PathVariable Long id,
            @RequestBody Map<String, String> ruleRequest) {
        String ruleString = ruleRequest.get("ruleString");
        if (ruleString == null || ruleString.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Rule updatedRule = ruleService.updateRule(id, ruleString);
        return ResponseEntity.ok(updatedRule);
    }

    // Endpoint to delete a rule by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
