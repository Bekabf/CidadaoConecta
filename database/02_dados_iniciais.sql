
-- =====================================================
-- GLOBAL SOLUTION 2026/2 - FIAP
-- PROJETO: CIDADÃOCONECTA 2.0
-- ARQUIVO: 02_dados_iniciais.sql
--
-- CARGA INICIAL DE DADOS DEMONSTRATIVOS
--
-- Executar somente depois de:
-- 01_criar_tabelas.sql
--
-- Destinado a um banco sem dados do projeto.
-- Nao executar novamente na conexao atual.
-- =====================================================


-- =====================================================
-- 01 - CATEGORIAS DE PROBLEMAS URBANOS
-- =====================================================

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'pavimentacao',
    'Ruas e calçadas',
    'Buracos e problemas na pavimentação urbana'
);

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'iluminacao',
    'Iluminação pública',
    'Postes apagados e problemas de iluminação'
);

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'saneamento',
    'Água e saneamento',
    'Vazamentos, esgoto e problemas sanitários'
);

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'acessibilidade',
    'Acessibilidade',
    'Barreiras de acesso e mobilidade urbana'
);

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'limpeza',
    'Limpeza urbana',
    'Acúmulo de lixo e descarte irregular'
);

INSERT INTO CC2_CATEGORIA (
    CODIGO,
    NOME,
    DESCRICAO
) VALUES (
    'outro',
    'Outro problema',
    'Outras situações que afetam a cidade'
);


-- =====================================================
-- 02 - OCORRENCIA DEMONSTRATIVA
-- =====================================================

INSERT INTO CC2_OCORRENCIA (
    PROTOCOLO,
    ID_PUBLICO,
    TITULO,
    DESCRICAO,
    CIDADE,
    UF,
    BAIRRO,
    RUA,
    NUMERO_ENDERECO,
    STATUS
) VALUES (
    'CC-TESTE-0001',
    'PUBLICO-TESTE-0001',
    'Buraco na Rua das Flores',
    'Buraco na via causando riscos aos motoristas e pedestres.',
    'Anápolis',
    'GO',
    'Centro',
    'Rua das Flores',
    '150',
    'ABERTA'
);


-- =====================================================
-- 03 - RELACIONAMENTO N:N
-- OCORRENCIA X CATEGORIAS
-- =====================================================

-- Associar a ocorrencia a Pavimentacao

INSERT INTO CC2_OCORRENCIA_CATEGORIA (
    ID_OCORRENCIA,
    ID_CATEGORIA
)
SELECT
    O.ID_OCORRENCIA,
    C.ID_CATEGORIA
FROM CC2_OCORRENCIA O
CROSS JOIN CC2_CATEGORIA C
WHERE O.PROTOCOLO = 'CC-TESTE-0001'
  AND C.CODIGO = 'pavimentacao';


-- Associar a mesma ocorrencia a Acessibilidade

INSERT INTO CC2_OCORRENCIA_CATEGORIA (
    ID_OCORRENCIA,
    ID_CATEGORIA
)
SELECT
    O.ID_OCORRENCIA,
    C.ID_CATEGORIA
FROM CC2_OCORRENCIA O
CROSS JOIN CC2_CATEGORIA C
WHERE O.PROTOCOLO = 'CC-TESTE-0001'
  AND C.CODIGO = 'acessibilidade';


-- =====================================================
-- 04 - HISTORICO INICIAL
--
-- Registra a criacao da ocorrencia.
-- Sem usuario administrativo, pois
-- o registro foi criado anonimamente.
-- =====================================================

INSERT INTO CC2_HISTORICO_STATUS (
    ID_OCORRENCIA,
    ID_USUARIO,
    STATUS_ANTERIOR,
    STATUS_NOVO,
    TIPO_EVENTO,
    DESCRICAO
)
SELECT
    ID_OCORRENCIA,
    NULL,
    NULL,
    'ABERTA',
    'CRIACAO',
    'Ocorrencia cadastrada no sistema.'
FROM CC2_OCORRENCIA
WHERE PROTOCOLO = 'CC-TESTE-0001';


-- =====================================================
-- 05 - CONFIRMAR CADASTROS
-- =====================================================

COMMIT;


-- =====================================================
-- 06 - CONSULTAR DADOS INSERIDOS
-- =====================================================

-- Conferir categorias

SELECT
    ID_CATEGORIA,
    CODIGO,
    NOME
FROM CC2_CATEGORIA
ORDER BY ID_CATEGORIA;


-- Conferir ocorrencia

SELECT
    ID_OCORRENCIA,
    PROTOCOLO,
    TITULO,
    CIDADE,
    BAIRRO,
    STATUS
FROM CC2_OCORRENCIA
WHERE PROTOCOLO = 'CC-TESTE-0001';


-- Conferir relacionamento N:N

SELECT
    O.PROTOCOLO,
    O.TITULO,
    C.NOME AS CATEGORIA
FROM CC2_OCORRENCIA O
JOIN CC2_OCORRENCIA_CATEGORIA OC
    ON OC.ID_OCORRENCIA = O.ID_OCORRENCIA
JOIN CC2_CATEGORIA C
    ON C.ID_CATEGORIA = OC.ID_CATEGORIA
WHERE O.PROTOCOLO = 'CC-TESTE-0001'
ORDER BY C.NOME;


-- Conferir historico inicial

SELECT
    H.TIPO_EVENTO,
    H.STATUS_NOVO,
    H.DESCRICAO,
    H.DATA_ALTERACAO
FROM CC2_HISTORICO_STATUS H
JOIN CC2_OCORRENCIA O
    ON O.ID_OCORRENCIA = H.ID_OCORRENCIA
WHERE O.PROTOCOLO = 'CC-TESTE-0001'
ORDER BY H.DATA_ALTERACAO;


-- =====================================================
-- FIM DO SCRIPT
-- =====================================================
