SELECT O.storeno            AS loja,
       O.ordno              AS numeroPedido,
       O.status             AS status,
       O.s16                AS statusCrediario,
       O.s15                AS userAnalise,
       IFNULL(U.auxStr, '') AS analistaName
FROM sqldados.eord         AS O
  LEFT JOIN sqldados.users AS U
	      ON U.no = O.s15
WHERE O.storeno = :storeno
  AND O.ordno = :ordno