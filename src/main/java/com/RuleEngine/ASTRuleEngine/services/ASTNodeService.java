package com.RuleEngine.ASTRuleEngine.services;

import com.RuleEngine.ASTRuleEngine.exceptions.ResourceNotFoundException;
import com.RuleEngine.ASTRuleEngine.models.ASTNode;
import com.RuleEngine.ASTRuleEngine.repositories.ASTNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ASTNodeService {

    @Autowired
    private ASTNodeRepository astNodeRepository;


    // Retrieve a node by its ID
    public ASTNode getNodeById(Long nodeId) {
        return astNodeRepository.findById(nodeId).orElse(null);
    }

    // Create a new node or update an existing node
    public ASTNode saveOrUpdateNode(ASTNode node) {
        return astNodeRepository.save(node);
    }
    // Delete a node by its ID
    public boolean deleteNode(Long nodeId) {
        if (astNodeRepository.existsById(nodeId)) {
            astNodeRepository.deleteById(nodeId);
            return true;
        }
        return false;
    }

    // Retrieve left and right children of a node
    public ASTNode getLeftChild(ASTNode node) {
        return node.getLeftChildId() != null ? getNodeById(node.getLeftChildId()) : null;
    }

    public ASTNode getRightChild(ASTNode node) {
        return node.getRightChildId() != null ? getNodeById(node.getRightChildId()) : null;
    }

    // Retrieve subtree nodes recursively
    public List<ASTNode> getSubTree(ASTNode rootNode) {
        List<ASTNode> nodes = new ArrayList<>();
        if (rootNode != null) {
            nodes.add(rootNode);
            ASTNode left = getLeftChild(rootNode);
            ASTNode right = getRightChild(rootNode);

            if (left != null) nodes.addAll(getSubTree(left));
            if (right != null) nodes.addAll(getSubTree(right));
        }
        return nodes;
    }


    // Find all AST nodes
    public List<ASTNode> findAllNodes() {
        return astNodeRepository.findAll();
    }

    // Find a specific AST node by ID
    public ASTNode findNodeById(Long id) {
        return astNodeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AST Node not found with id: " + id));
    }

}

