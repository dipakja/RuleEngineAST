# Rule Engine System

A robust rule engine that evaluates complex conditions using an Abstract Syntax Tree (AST) representation. This project allows users to create, manage, and combine rules dynamically, enabling advanced decision-making capabilities.

## Table of Contents

- [Features](#features)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Usage](#usage)


## Features
- Dynamic rule creation and management.
- Support for combining rules with logical operators (AND, OR).
- Evaluation of rules against JSON input data.
- Use of an Abstract Syntax Tree (AST) for complex condition evaluation.

## System Requirements
- Java 21 (OpenJDK)
- Maven 3.8 or higher
- MySQL or another relational database for storage

## Installation
1. **Clone the Repository:**
   ```
   git clone https://github.com/dipakja/RuleEngineAST.git
   cd RuleEngineAST
   ```
2.Install Dependencies: Ensure you have Maven installed. Then, run the following command in the project directory:

```bash
mvn install
```
## Configuration
 1.Database Credentials: Open the ```src/main/resources/application.properties``` file and configure your database credentials:
 ```
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password
```
2.Database Schema: Create the necessary tables using the following SQL queries:
```rules``` Table:
```
CREATE TABLE rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_string TEXT NOT NULL,  
    root_node_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```
```ast_nodes ``` Table:

```
CREATE TABLE ast_nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('operator', 'operand') NOT NULL,  
    operator_value ENUM('AND', 'OR') NULL, 
    operand_value TEXT NULL,  
    parent_id BIGINT,  
    left_child_id BIGINT NULL,  
    right_child_id BIGINT NULL, 
    rule_id BIGINT NOT NULL, 
    FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE CASCADE,
    FOREIGN KEY (left_child_id) REFERENCES ast_nodes(id) ON DELETE SET NULL,
    FOREIGN KEY (right_child_id) REFERENCES ast_nodes(id) ON DELETE SET NULL
);
```
```combined_rules``` Table:
```
CREATE TABLE combined_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_1_id BIGINT NOT NULL,  
    rule_2_id BIGINT NOT NULL,  
    operator_value ENUM('AND', 'OR') NOT NULL,  
    combined_rule_id BIGINT, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rule_1_id) REFERENCES rules(id) ON DELETE CASCADE,
    FOREIGN KEY (rule_2_id) REFERENCES rules(id) ON DELETE CASCADE,
    FOREIGN KEY (combined_rule_id) REFERENCES rules(id) ON DELETE CASCADE
);
```
## Running the Application
To run the application, use the following command in the project directory:
```
mvn spring-boot:run
```
Once the application is running, you can access it at:
```
http://localhost:8080
```
## Testing
Test Cases:
1.System Setup: Verify that the system starts successfully and connects to the database.
2.Rule Evaluation: Test the evaluation of various rules against input data to ensure correct results.
3.Rule Combination: Verify that combined rules evaluate correctly based on the logical operator.
4.Data Integrity: Check that the rules and nodes are correctly stored and retrievable from the database.

## Usage
After accessing the application in your browser, you will see a user interface that allows you to create, evaluate, and manage rules. You can input JSON data for evaluation and visualize how rules are combined.

