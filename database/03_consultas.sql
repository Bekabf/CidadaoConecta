
-- =====================================================
-- GLOBAL SOLUTION 2026/2 - FIAP
-- PROJETO: CIDADÃOCONECTA 2.0
-- ARQUIVO: 03_consultas.sql
--
-- CONSULTAS E RELATORIOS DO OBSERVATORIO URBANO
-- BANCO ORACLE + JAVA JDBC
--
-- Todas as consultas sao SELECT.
-- Nao modificam os dados existentes.
-- =====================================================


-- =====================================================
-- 01 - LISTAR TODAS AS OCORRENCIAS
-- =====================================================

SELECT
    ID_OCORRENCIA,
    PROTOCOLO,
    TITULO,
    CIDADE,
    UF,
    BAIRRO,
    STATUS,
    DATA_CADASTRO
FROM CC2_OCORRENCIA
ORDER BY DATA_CADASTRO DESC, ID_OCORRENCIA DESC;


-- =====================================================
-- 02 - CONSULTAR UMA OCORRENCIA POR PROTOCOLO
-- =====================================================

SELECT
    PROTOCOLO,
    TITULO,
    DESCRICAO,
    CIDADE,
    UF,
    BAIRRO,
    STATUS,
    DATA_CADASTRO
FROM CC2_OCORRENCIA
WHERE PROTOCOLO = 'CC-TESTE-0001';


-- =====================================================
-- 03 - LISTAR OCORRENCIAS COM SUAS CATEGORIAS
-- RELACIONAMENTO N:N COM JOIN
-- =====================================================

SELECT
    O.PROTOCOLO,
    O.TITULO,
    C.NOME AS CATEGORIA,
    O.BAIRRO,
    O.STATUS
FROM CC2_OCORRENCIA O
JOIN CC2_OCORRENCIA_CATEGORIA OC
    ON OC.ID_OCORRENCIA = O.ID_OCORRENCIA
JOIN CC2_CATEGORIA C
    ON C.ID_CATEGORIA = OC.ID_CATEGORIA
ORDER BY O.ID_OCORRENCIA, C.NOME;


-- =====================================================
-- 04 - TOTAL DE OCORRENCIAS
-- =====================================================

SELECT
    COUNT(*) AS TOTAL_OCORRENCIAS
FROM CC2_OCORRENCIA;


-- =====================================================
-- 05 - QUANTIDADE DE OCORRENCIAS POR STATUS
-- =====================================================

SELECT
    STATUS,
    COUNT(*) AS QUANTIDADE
FROM CC2_OCORRENCIA
GROUP BY STATUS
ORDER BY QUANTIDADE DESC, STATUS;


-- =====================================================
-- 06 - QUANTIDADE DE OCORRENCIAS POR BAIRRO
-- =====================================================

SELECT
    CIDADE,
    UF,
    BAIRRO,
    COUNT(*) AS QUANTIDADE
FROM CC2_OCORRENCIA
GROUP BY CIDADE, UF, BAIRRO
ORDER BY QUANTIDADE DESC, CIDADE, BAIRRO;


-- =====================================================
-- 07 - QUANTIDADE DE OCORRENCIAS POR CATEGORIA
--
-- COUNT DISTINCT evita repeticao da mesma ocorrencia
-- dentro da contagem de cada categoria.
-- =====================================================

SELECT
    C.CODIGO,
    C.NOME AS CATEGORIA,
    COUNT(DISTINCT OC.ID_OCORRENCIA) AS QUANTIDADE
FROM CC2_CATEGORIA C
LEFT JOIN CC2_OCORRENCIA_CATEGORIA OC
    ON OC.ID_CATEGORIA = C.ID_CATEGORIA
GROUP BY C.CODIGO, C.NOME
ORDER BY QUANTIDADE DESC, C.NOME;


-- =====================================================
-- 08 - RANKING DOS CINCO BAIRROS COM MAIS REGISTROS
-- =====================================================

SELECT
    CIDADE,
    UF,
    BAIRRO,
    QUANTIDADE
FROM (
    SELECT
        CIDADE,
        UF,
        BAIRRO,
        COUNT(*) AS QUANTIDADE
    FROM CC2_OCORRENCIA
    GROUP BY CIDADE, UF, BAIRRO
    ORDER BY COUNT(*) DESC, BAIRRO
)
WHERE ROWNUM <= 5;


-- =====================================================
-- 09 - HISTORICO DE UMA OCORRENCIA
-- =====================================================

SELECT
    O.PROTOCOLO,
    H.TIPO_EVENTO,
    H.STATUS_ANTERIOR,
    H.STATUS_NOVO,
    H.DESCRICAO,
    H.DATA_ALTERACAO
FROM CC2_HISTORICO_STATUS H
JOIN CC2_OCORRENCIA O
    ON O.ID_OCORRENCIA = H.ID_OCORRENCIA
WHERE O.PROTOCOLO = 'CC-TESTE-0001'
ORDER BY H.DATA_ALTERACAO, H.ID_HISTORICO;


-- =====================================================
-- 10 - CONTAGEM DE APOIOS POR OCORRENCIA
-- =====================================================

SELECT
    O.PROTOCOLO,
    O.TITULO,
    COUNT(A.ID_USUARIO) AS TOTAL_APOIOS
FROM CC2_OCORRENCIA O
LEFT JOIN CC2_APOIO A
    ON A.ID_OCORRENCIA = O.ID_OCORRENCIA
GROUP BY O.PROTOCOLO, O.TITULO
ORDER BY TOTAL_APOIOS DESC, O.TITULO;


-- =====================================================
-- 11 - FEEDBACK SOBRE OCORRENCIAS RESOLVIDAS
-- =====================================================

SELECT
    O.PROTOCOLO,
    O.TITULO,
    F.RESOLVIDO,
    F.COMENTARIO,
    F.DATA_FEEDBACK
FROM CC2_FEEDBACK F
JOIN CC2_OCORRENCIA O
    ON O.ID_OCORRENCIA = F.ID_OCORRENCIA
ORDER BY F.DATA_FEEDBACK DESC;


-- =====================================================
-- 12 - OCORRENCIAS COM POSSIVEIS DUPLICIDADES
-- =====================================================

SELECT
    A.PROTOCOLO AS PROTOCOLO_A,
    B.PROTOCOLO AS PROTOCOLO_B,
    D.PONTUACAO,
    D.STATUS_ANALISE
FROM CC2_DUPLICIDADE D
JOIN CC2_OCORRENCIA A
    ON A.ID_OCORRENCIA = D.ID_OCORRENCIA_A
JOIN CC2_OCORRENCIA B
    ON B.ID_OCORRENCIA = D.ID_OCORRENCIA_B
ORDER BY D.PONTUACAO DESC;


-- =====================================================
-- 13 - INDICADORES GERAIS DO OBSERVATORIO
-- =====================================================

SELECT
    (SELECT COUNT(*)
     FROM CC2_OCORRENCIA)
        AS TOTAL_OCORRENCIAS,

    (SELECT COUNT(*)
     FROM (
         SELECT DISTINCT
             UF, CIDADE, BAIRRO
         FROM CC2_OCORRENCIA
     ))
        AS TOTAL_BAIRROS,

    (SELECT COUNT(*)
     FROM CC2_CATEGORIA
     WHERE ATIVO = 1)
        AS TOTAL_CATEGORIAS_ATIVAS
FROM DUAL;


-- =====================================================
-- 14 - LISTAGEM PUBLICA DA COMUNIDADE
--
-- Apenas campos preparados para publicacao.
-- Nao retorna endereco completo nem usuario.
-- A API tambem deve restringir os campos.
-- =====================================================

SELECT
    O.ID_PUBLICO,
    O.TITULO_PUBLICO,
    O.RESUMO_PUBLICO,
    O.BAIRRO,
    O.CIDADE,
    O.UF,
    O.STATUS,
    O.DATA_CADASTRO
FROM CC2_OCORRENCIA O
WHERE O.VISIBILIDADE_PUBLICA = 1
ORDER BY O.DATA_CADASTRO DESC;


-- =====================================================
-- 15 - QUANTIDADE DE FOTOS POR OCORRENCIA
-- =====================================================

SELECT
    O.PROTOCOLO,
    O.TITULO,
    COUNT(E.ID_EVIDENCIA) AS TOTAL_FOTOS
FROM CC2_OCORRENCIA O
LEFT JOIN CC2_EVIDENCIA E
    ON E.ID_OCORRENCIA = O.ID_OCORRENCIA
GROUP BY O.PROTOCOLO, O.TITULO
ORDER BY TOTAL_FOTOS DESC;


-- =====================================================
-- FIM DAS CONSULTAS
-- TOTAL: 15 CONSULTAS SQL
-- =====================================================
