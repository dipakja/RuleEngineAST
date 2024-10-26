package com.RuleEngine.ASTRuleEngine.repositories;

import com.RuleEngine.ASTRuleEngine.models.CombinedRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombinedRulesRepository extends JpaRepository<CombinedRules, Long> {

}
