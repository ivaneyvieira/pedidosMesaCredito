DROP TABLE IF EXISTS TMETODO_CREDIARIO;
CREATE TEMPORARY TABLE TMETODO_CREDIARIO (
  PRIMARY KEY (paymno)
)
SELECT no  AS paymno,
       name,
       sname,
       CASE
	 WHEN no IN (900, 903, 904, 905, 913, 953)
	   THEN 'CRE'
	 WHEN no IN (923)
	   THEN 'DEB'
	 WHEN no IN (960)
	   THEN 'PIN'
       END AS tipoContrato
FROM sqldados.paym
WHERE no IN (900, 903, 904, 905, 913, 953, 923, 960);

DO @HOJE := CURRENT_DATE * 1;
DO @DATA := @HOJE;

DROP TABLE IF EXISTS TPedido;
CREATE TEMPORARY TABLE TPedido (
  PRIMARY KEY (storeno, pedido)
)
SELECT O.storeno,
       O.ordno                                            AS pedido,
       O.status,
       CAST(O.date AS DATE)                               AS datePedido,
       SEC_TO_TIME(O.l4)                                  AS timePedido,
       O.custno,
       C.name                                             AS nome,
       C.cpf_cgc                                          AS documento,
       CAST(IF(C.birthday = 0, NULL, C.birthday) AS DATE) AS dtNascimento,
       IFNULL(S.otherName, '')                            AS filial,
       O.amount / 100                                     AS valor,
       discount / 100                                     AS desconto,
       O.s16                                              AS statusCrediario,
       O.s15                                              AS userAnalise,
       IFNULL(U.auxStr, '')                               AS analistaName,
       M.tipoContrato                                     AS tipoContrato
FROM sqldados.eord             AS O
  LEFT JOIN  sqldados.custp    AS C
	       ON C.no = O.custno
  LEFT JOIN  sqldados.store    AS S
	       ON S.no = O.storeno
  INNER JOIN TMETODO_CREDIARIO AS M
	       USING (paymno)
  LEFT JOIN  sqldados.users    AS U
	       ON U.no = O.s15
WHERE O.date = @DATA
  AND O.status IN (:statusSaci)
  AND O.storeno IN (1, 3, 5, 8, 9, 11, 12)
  AND O.s16 IN (:status)
  AND C.no IS NOT NULL
  AND M.paymno IS NOT NULL;

DROP TEMPORARY TABLE IF EXISTS T_RENDA;
CREATE TEMPORARY TABLE T_RENDA (
  PRIMARY KEY (custno)
)
SELECT custno, SUM(declarada / 100 + comprovada / 100) AS renda
FROM sqldados.ctrenda
WHERE auxShort1 = 0
  AND custno IN (SELECT custno
		 FROM TPedido)
GROUP BY custno;

DROP TABLE IF EXISTS TSIMULADOR;
CREATE TEMPORARY TABLE TSIMULADOR (
  PRIMARY KEY (storeno, pedido)
)
SELECT E.storeno,
       E.ordno                                       AS pedido,
       SUM(IF(seqno = 0, amt / 100, 0))              AS entrada,
       ROUND(AVG(IF(seqno > 0, amt / 100, NULL)), 2) AS parcelas,
       SUM(IF(seqno > 0 AND E.amt <> 0, 1, 0))       AS quant,
       SUM(IF(seqno > 0, amt / 100, NULL))           AS totalFinanciado,
       SUM(amt / 100)                                AS totalSimuldado,
       SUM(chargeamt / 100)                          AS totalJuros,
       P.tipoContrato                                AS tipoContrato
FROM sqldados.eordcr AS E
  INNER JOIN TPedido AS P
	       ON P.storeno = E.storeno AND P.pedido = E.ordno
GROUP BY storeno, pedido;

SELECT P.storeno,
       pedido,
       P.status,
       datePedido,
       timePedido,
       P.custno,
       nome,
       documento,
       dtNascimento,
       IFNULL(R.renda, 0.00)                            AS renda,
       filial,
       valor,
       desconto,
       entrada,
       ROUND((valor - entrada + totalJuros) / quant, 2) AS parcelas,
       ROUND((valor - entrada + totalJuros), 2)         AS parcelaTotal,
       quant,
       (valor - entrada)                                AS totalFinanciado,
       P.statusCrediario                                AS statusCrediario,
       P.userAnalise                                    AS userAnalise,
       P.analistaName                                   AS analistaName,
       S.tipoContrato                                   AS tipoContrato
FROM TPedido            AS P
  INNER JOIN TSIMULADOR AS S
	       USING (storeno, pedido)
  LEFT JOIN  T_RENDA    AS R
	       USING (custno)
ORDER BY datePedido, timePedido

