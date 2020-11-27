DROP TABLE IF EXISTS TMETODO_CREDIARIO;
CREATE TEMPORARY TABLE TMETODO_CREDIARIO (
  PRIMARY KEY (paymno)
)
SELECT no AS paymno,
       name,
       sname
FROM sqldados.paym
WHERE no IN (900, 903, 904, 905, 913, 953);

DO @HOJE := current_date * 1;
DO @DATA := 20201101;

DROP TABLE IF EXISTS TPedido;
CREATE TEMPORARY TABLE TPedido (
  PRIMARY KEY (storeno, pedido)
)
SELECT O.storeno,
       O.ordno                 AS pedido,
       O.status,
       cast(O.date AS DATE)    AS datePedido,
       SEC_TO_TIME(O.l4)       AS timePedido,
       O.custno,
       C.name                  AS nome,
       IFNULL(S.otherName, '') AS filial,
       O.amount / 100          AS valor,
       discount / 100          AS desconto,
       O.s16                   AS statusCrediario,
       O.s15                   AS userAnalise,
       IFNULL(U.name, '')      AS analistaName
FROM sqldados.eord             AS O
  LEFT JOIN  sqldados.custp    AS C
	       ON C.no = O.custno
  LEFT JOIN  sqldados.store    AS S
	       ON S.no = O.storeno
  INNER JOIN TMETODO_CREDIARIO AS M
	       USING (paymno)
  LEFT JOIN  sqldados.users    AS U
	       ON U.no = O.s15
WHERE O.date > @DATA
  AND O.status IN (:statusSaci)
  AND O.storeno IN (1, 3, 5, 8, 9, 11, 12)
  AND O.s16 IN (:status)
  AND C.no IS NOT NULL
  AND M.paymno IS NOT NULL;

DROP TABLE IF EXISTS TSIMULADOR;
CREATE TEMPORARY TABLE TSIMULADOR (
  PRIMARY KEY (storeno, pedido)
)
SELECT E.storeno,
       E.ordno                                       AS pedido,
       SUM(if(seqno = 0, amt / 100, 0))              AS entrada,
       ROUND(AVG(if(seqno > 0, amt / 100, NULL)), 2) AS parcelas,
       SUM(if(seqno > 0, 1, 0))                      AS quant,
       SUM(if(seqno > 0, amt / 100, NULL))           AS totalFinanciado,
       SUM(amt / 100)                                AS totalSimuldado,
       SUM(chargeamt / 100)                          AS totalJuros
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
       filial,
       valor,
       desconto,
       entrada,
       ROUND((valor - entrada + totalJuros) / quant, 2) AS parcelas,
       quant,
       (valor - entrada)                                AS totalFinanciado,
       P.statusCrediario                                AS statusCrediario,
       P.userAnalise                                    AS userAnalise,
       P.analistaName                                   AS analistaName
FROM TPedido            AS P
  INNER JOIN TSIMULADOR AS S
	       USING (storeno, pedido)
ORDER BY datePedido, timePedido

