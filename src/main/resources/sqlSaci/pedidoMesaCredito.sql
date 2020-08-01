DROP TABLE IF EXISTS TMETODO_CREDIARIO;
CREATE TEMPORARY TABLE TMETODO_CREDIARIO (
  PRIMARY KEY (paymno)
)
SELECT no AS paymno,
       name,
       sname
FROM sqldados.paym
WHERE name LIKE '%CREDIA%';


DO @HOJE := current_date * 1;
DO @DATA := :date;

DROP TABLE IF EXISTS TPedido;
CREATE TEMPORARY TABLE TPedido (
  PRIMARY KEY (storeno, pedido)
)
SELECT O.storeno,
       O.ordno            AS pedido,
       O.status,
       cast(date AS DATE) AS datePedido,
       SEC_TO_TIME(O.l4)  AS timePedido,
       O.custno,
       C.name             AS nome,
       S.otherName        AS filial,
       O.amount / 100     AS valor,
       discount / 100     AS desconto
FROM sqldados.eord             AS O
  INNER JOIN sqldados.custp    AS C
	       ON C.no = O.custno
  INNER JOIN sqldados.store    AS S
	       ON S.no = O.storeno
  INNER JOIN TMETODO_CREDIARIO AS M
	       USING (paymno)
WHERE (O.date >= @DATA AND O.status IN (:statusSaci))
  AND O.storeno IN (1, 3, 5, 8, 9, 11, 12);

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
       SUM(amt / 100)                                AS totalSimuldado
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
       ROUND((valor - entrada) / quant, 2) AS parcelas,
       quant,
       (valor - entrada)                   AS totalFinanciado,
       O.s16                               AS statusCrediario,
       O.s15                               AS userAnalise
FROM TPedido               AS P
  INNER JOIN TSIMULADOR    AS S
	       USING (storeno, pedido)
  INNER JOIN sqldados.eord AS O
	       ON O.storeno = P.storeno AND O.ordno = P.pedido
WHERE O.s16 IN (:status)
ORDER BY datePedido, timePedido

