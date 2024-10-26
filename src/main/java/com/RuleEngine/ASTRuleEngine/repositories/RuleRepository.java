package com.RuleEngine.ASTRuleEngine.repositories;

import com.RuleEngine.ASTRuleEngine.models.Rule;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

}
