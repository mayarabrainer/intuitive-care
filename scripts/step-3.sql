-- **Tabela de Operadoras**
-- Armazena informações sobre as operadoras de planos de saúde.
CREATE TABLE health_operators (
    id SERIAL PRIMARY KEY,
    operator_name VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL,
    state VARCHAR(2),
    city VARCHAR(255)
);
-- A tabela 'health_operators' será usada para armazenar as operadoras de planos de saúde
-- que estão presentes no arquivo CSV de operadoras ativas.


-- **Tabela de Despesas**
-- Armazena informações sobre as despesas das operadoras em diferentes categorias.
CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    operator_id INT REFERENCES health_operators(id),
    expense_category VARCHAR(255) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    year INT,
    quarter INT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- A tabela 'expenses' armazena os dados das despesas das operadoras. Ela inclui o valor da despesa,
-- a categoria da despesa, o ano e o trimestre. Cada registro de despesa é vinculado à operadora correspondente
-- pela chave estrangeira 'operator_id'.


-- **Tabela de Eventos de Sinistros**
-- Armazena detalhes sobre os eventos de sinistros relacionados às operadoras.
CREATE TABLE claims_events (
    id SERIAL PRIMARY KEY,
    operator_id INT REFERENCES health_operators(id),
    event_type VARCHAR(255) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    event_date TIMESTAMP,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- A tabela 'claims_events' armazena informações sobre os eventos de sinistros ocorridos com as operadoras,
-- incluindo o valor do sinistro e o tipo de evento.


-- **Importando Operadoras de Planos de Saúde**
-- Importa os dados das operadoras do arquivo CSV para a tabela 'health_operators'.
COPY health_operators (operator_name, cnpj, state, city)
FROM '/path/to/operadoras_de_plano_de_saude_ativas.csv'
DELIMITER ','
CSV HEADER
ENCODING 'UTF8';
-- Essa query importa os dados de operadoras a partir do arquivo CSV
-- contendo os dados das operadoras ativas, assumindo que o CSV
-- está no formato correto (com cabeçalho e encoding UTF-8).


-- **Importando Despesas**
-- Importa os dados de despesas para a tabela 'expenses'.
COPY expenses (operator_id, expense_category, amount, year, quarter)
FROM '/path/to/despesas.csv'
DELIMITER ','
CSV HEADER
ENCODING 'UTF8';
-- A query 'COPY expenses' importa os dados das despesas das operadoras.
-- O arquivo CSV de despesas deve conter as colunas correspondentes.



-- **Query 1: Operadoras com maiores despesas no último trimestre**
-- Esta query retorna as 10 operadoras com maiores despesas na categoria 'EVENTOS/ SINISTROS CONHECIDOS...'
-- no último trimestre.
SELECT
    h.operator_name,
    SUM(e.amount) AS total_expenses
FROM
    expenses e
JOIN
    health_operators h ON e.operator_id = h.id
WHERE
    e.expense_category = 'EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR'
    AND e.year = EXTRACT(YEAR FROM CURRENT_DATE)  -- Ano atual
    AND e.quarter = EXTRACT(QUARTER FROM CURRENT_DATE)  -- Trimestre atual
GROUP BY
    h.operator_name
ORDER BY
    total_expenses DESC
LIMIT 10;
-- Essa query retorna as 10 operadoras com maiores despesas no último trimestre
-- na categoria "EVENTOS/ SINISTROS CONHECIDOS..." para o ano e trimestre atuais.


-- **Query 2: Operadoras com maiores despesas no último ano**
-- Esta query retorna as 10 operadoras com maiores despesas na categoria 'EVENTOS/ SINISTROS CONHECIDOS...'
-- no último ano.
SELECT
    h.operator_name,
    SUM(e.amount) AS total_expenses
FROM
    expenses e
JOIN
    health_operators h ON e.operator_id = h.id
WHERE
    e.expense_category = 'EVENTOS/ SINISTROS CONHECIDOS OU AVISADOS DE ASSISTÊNCIA A SAÚDE MEDICO HOSPITALAR'
    AND e.year = EXTRACT(YEAR FROM CURRENT_DATE) - 1  -- Ano anterior
GROUP BY
    h.operator_name
ORDER BY
    total_expenses DESC
LIMIT 10;
-- Esta query retorna as 10 operadoras com maiores despesas no último ano
-- na categoria "EVENTOS/ SINISTROS CONHECIDOS...", agrupadas por operadora
-- e ordenadas por valor total de despesas.

