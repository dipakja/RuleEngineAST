// This is a placeholder for your existing rule handling logic.
// You'll need to implement the logic for creating rules, evaluating, and combining them.

document.getElementById('createRuleBtn').addEventListener('click', function() {
    const ruleCondition = document.getElementById('ruleCondition').value;
    // Call your API to create the rule
    console.log('Creating rule:', ruleCondition);
});

document.getElementById('evaluateBtn').addEventListener('click', function() {
    const userData = document.getElementById('evaluateData').value;
    // Call your API to evaluate the rule with user data
    console.log('Evaluating with data:', userData);
});

// Function to load existing rules (you'll fetch from your backend)
function loadRules() {
    // Mockup data for example
    const rules = [
        { id: 1, condition: "(age > 30 AND department = 'Sales')" },
        { id: 2, condition: "(salary < 50000 OR department = 'HR')" }
    ];

    const tableBody = document.getElementById('rulesTableBody');
    rules.forEach(rule => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${rule.id}</td>
            <td>${rule.condition}</td>
            <td>
                <input type="checkbox" class="checkbox" data-id="${rule.id}">
            </td>
        `;
        tableBody.appendChild(row);
    });

    const combineRules = document.getElementById('combineRules');
    rules.forEach(rule => {
        const checkbox = document.createElement('input');
        checkbox.type = 'checkbox';
        checkbox.value = rule.id;
        checkbox.className = 'checkbox';
        checkbox.dataset.id = rule.id;

        combineRules.appendChild(checkbox);
        combineRules.appendChild(document.createTextNode(rule.condition));
        combineRules.appendChild(document.createElement('br'));
    });
}

loadRules();
