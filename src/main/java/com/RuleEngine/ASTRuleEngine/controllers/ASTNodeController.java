package com.RuleEngine.ASTRuleEngine.controllers;

import com.RuleEngine.ASTRuleEngine.models.ASTNode;
import com.RuleEngine.ASTRuleEngine.services.ASTNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/astnodes")
public class ASTNodeController {

    @Autowired
    private ASTNodeService astNodeService;

    // Endpoint to create a new AST node
    @PostMapping
    public ResponseEntity<ASTNode> createNode(@RequestBody ASTNode node) {
        ASTNode createdNode = astNodeService.saveOrUpdateNode(node);
        return ResponseEntity.ok(createdNode);
    }

    // Endpoint to update an existing AST node
    @PutMapping("/{id}")
    public ResponseEntity<ASTNode> updateNode(@PathVariable Long id, @RequestBody ASTNode node) {
        node.setId(id); // Set the ID to ensure the correct node is updated
        ASTNode updatedNode = astNodeService.saveOrUpdateNode(node);
        return ResponseEntity.ok(updatedNode);
    }

    // Endpoint to get all AST nodes
    @GetMapping
    public ResponseEntity<List<ASTNode>> getAllNodes() {
        List<ASTNode> nodes = astNodeService.findAllNodes();
        return ResponseEntity.ok(nodes);
    }

    // Endpoint to get a specific AST node by ID
    @GetMapping("/{id}")
    public ResponseEntity<ASTNode> getNodeById(@PathVariable Long id) {
        ASTNode node = astNodeService.findNodeById(id);
        return ResponseEntity.ok(node);
    }

    // Endpoint to delete an AST node
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        astNodeService.deleteNode(id);
        return ResponseEntity.noContent().build();
    }
}