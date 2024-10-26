package com.RuleEngine.ASTRuleEngine.repositories;

import com.RuleEngine.ASTRuleEngine.models.ASTNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ASTNodeRepository  extends JpaRepository<ASTNode, Long> {
}
