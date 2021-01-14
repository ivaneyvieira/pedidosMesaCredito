DO @DT := 20210111;
DO @DT := current_date * 1;

DROP TEMPORARY TABLE IF EXISTS T_CONTRATOS;
CREATE TEMPORARY TABLE T_CONTRATOS (
  PRIMARY KEY (storeno, contrno)
)
SELECT I.storeno,
       I.contrno,
       I.custno,
       I.date,
       rate / 100          AS valor,
       cashamt / 100       AS valor_contrato,
       chargeamt / 100     AS valorEncargos,
       downpay / 100       AS valorEntrada,
       finstdt             AS pdvno,
       noofinst            AS numeroParcelas,
       empno               AS vendedor,
       E.name              AS analistaNome,
       credanal            AS analista,
       paymno,
       IFNULL(A.avalno, 0) AS avalno
FROM sqldados.inst          AS I
  LEFT JOIN sqldados.itaval AS A
	      USING (storeno, contrno)
  LEFT JOIN sqldados.emp    AS E
	      ON E.no = I.credanal
WHERE I.date = @DT
  AND I.status IN (0)
GROUP BY I.storeno, I.contrno;

DROP TEMPORARY TABLE IF EXISTS T_CUSTNO;
CREATE TEMPORARY TABLE T_CUSTNO (
  PRIMARY KEY (custno)
)
SELECT custno
FROM T_CONTRATOS
GROUP BY custno;

DROP TEMPORARY TABLE IF EXISTS T_AVALNO;
CREATE TEMPORARY TABLE T_AVALNO (
  PRIMARY KEY (custno)
)
SELECT avalno AS custno
FROM T_CONTRATOS
WHERE avalno <> 0
GROUP BY avalno;

DROP TEMPORARY TABLE IF EXISTS T_CLIENTE;
CREATE TEMPORARY TABLE T_CLIENTE (
  PRIMARY KEY (custno)
)
SELECT no                           AS custno,
       name,
       CONCAT(add1, ' ', nei1, ' ', city1, ' - ', state1, ' CEP ', TRIM(MID(zip, 1, 5)), '-',
	      TRIM(MID(zip, 6, 3))) AS endereco
FROM sqldados.custp   AS C
  INNER JOIN T_CUSTNO AS T
	       ON T.custno = C.no;

DROP TEMPORARY TABLE IF EXISTS T_AVALISTA;
CREATE TEMPORARY TABLE T_AVALISTA (
  PRIMARY KEY (custno)
)
SELECT no                           AS custno,
       name,
       CONCAT(add1, ' ', nei1, ' ', city1, ' - ', state1, ' CEP ', TRIM(MID(zip, 1, 5)), '-',
	      TRIM(MID(zip, 6, 3))) AS endereco
FROM sqldados.custp   AS C
  INNER JOIN T_AVALNO AS T
	       ON T.custno = C.no;

DROP TEMPORARY TABLE IF EXISTS T_GRUPO;
CREATE TEMPORARY TABLE T_GRUPO (
  PRIMARY KEY (storeno, contrno)
)
SELECT C.storeno,
       contrno,
       CAST(X.date AS DATE)                                     as dataCompra,
       GROUP_CONCAT(DISTINCT cl.name ORDER BY (X.price * qtty)) AS nomeGrupo
FROM T_CONTRATOS             AS C
  INNER JOIN sqldados.ithist AS H
	       USING (storeno, contrno)
  INNER JOIN sqldados.xalog2 AS X
	       ON X.storeno = H.storeno AND X.pdvno = H.pdvno AND X.xano = H.xano
  INNER JOIN sqldados.cl
	       ON cl.no = MID(X.clno, 1, 2) * 10000
WHERE status = 45
GROUP BY C.storeno, C.contrno;

DROP TEMPORARY TABLE IF EXISTS T_PARCELA;
CREATE TEMPORARY TABLE T_PARCELA (
  PRIMARY KEY (storeno, contrno)
)
SELECT C.storeno,
       contrno,
       X.duedate
FROM T_CONTRATOS           AS C
  INNER JOIN sqldados.itxa AS X
	       USING (storeno, contrno)
WHERE instno = 1
GROUP BY C.storeno, C.contrno;

SELECT CONCAT(C.storeno)                                                    AS loja,
       CONCAT(C.contrno)                                                    AS contrato,
       TC.name                                                              AS nomeCliente,
       CONCAT(TC.custno)                                                    AS codigoCliente,
       TC.endereco                                                          AS enderecoCliente,
       IFNULL(TA.name, '')                                                  AS nomeAvalista,
       IFNULL(CONCAT(TA.custno), '')                                        AS codigoAvalista,
       IFNULL(TA.endereco, '')                                              AS enderecoAvalista,
       sqldados.moneyformat2(C.valor, 2)                                    AS totalAvista,
       sqldados.moneyformat2(C.valorEncargos, 2)                            AS despesas,
       sqldados.moneyformat2(C.valorEntrada, 2)                             AS entrada,
       sqldados.moneyformat2(C.valor - C.valorEntrada, 2)                   AS afinanciar,
       LPAD(C.paymno, 3, '0')                                               AS plano,
       CONCAT(LPAD(C.storeno, 2, '0'), '-', C.contrno)                      AS numeroContrato,
       CAST(IFNULL(G.nomeGrupo, '-') AS CHAR)                               AS grupo,
       DATE_FORMAT(G.dataCompra, '%d/%m/%Y')                                AS dataVenda,
       DATE_FORMAT(CAST(P.duedate AS DATE), '%d/%m/%Y')                     AS dataVencimento,
       analistaNome                                                         AS analistaNome,
       DATE_FORMAT(C.date, '%d/%m/%Y')                                      AS dataContrato,
       sqldados.moneyformat2(C.valor - C.valorEntrada + C.valorEncargos, 2) AS valorAprovado
FROM T_CONTRATOS        AS C
  LEFT JOIN  T_GRUPO    AS G
	       USING (storeno, contrno)
  INNER JOIN T_PARCELA  AS P
	       USING (storeno, contrno)
  INNER JOIN T_CLIENTE  AS TC
	       ON C.custno = TC.custno
  LEFT JOIN  T_AVALISTA AS TA
	       ON C.avalno = TA.custno
ORDER BY numeroContrato




