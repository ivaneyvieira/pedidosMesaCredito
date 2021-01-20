DO @CT := :codigo;

DROP TEMPORARY TABLE IF EXISTS T_CONTRATOS;
CREATE TEMPORARY TABLE T_CONTRATOS (
  PRIMARY KEY (storeno, contrno)
)
select I.storeno,
       I.contrno,
       'P' as tipo
from sqldados.inst AS I
where custno = @CT
  and status = 0
UNION
select I.storeno,
       I.contrno,
       'A' as tipo
from sqldados.inst           AS I
  inner join sqldados.itaval AS A
	       ON I.storeno = A.storeno AND I.contrno = A.contrno
where A.avalno = @CT
  and status = 0
  and A.custno <> A.avalno;

DROP TEMPORARY TABLE IF EXISTS T_VALOR_DEVIDO;
CREATE TEMPORARY TABLE T_VALOR_DEVIDO (
  PRIMARY KEY (custno)
)
SELECT @CT                                                             as custno,
       SUM(IF(I.instamt >= I.paidamt, I.instamt - I.paidamt, 0) / 100) as valorDevido
FROM sqldados.itxa       as I
  INNER JOIN T_CONTRATOS AS C
	       USING (storeno, contrno)
WHERE I.status NOT IN (1, 5);

/*
SELECT custno, status, SUM(IF(D.amtdue >= D.amtpaid, D.amtdue - D.amtpaid, 0)/100) as valorDevido
FROM sqldados.dup AS D
WHERE type = 0
  and status in (0, 1, 3, 4, 7, 8, 9)
GROUP BY custno, status;
*/

DROP TEMPORARY TABLE IF EXISTS T_VALOR_DEVIDO_DUP;
CREATE TEMPORARY TABLE T_VALOR_DEVIDO_DUP (
  PRIMARY KEY (custno)
)
SELECT custno, SUM(IF(D.amtdue >= D.amtpaid, D.amtdue - D.amtpaid, 0) / 100) as valorDevido
FROM sqldados.dup AS D
WHERE type = 0
  and custno = @CT
  and status in (0, 1, 3, 4, 7, 8, 9)
GROUP BY custno;


select no                                                                  as codigo,
       name                                                                as nome,
       @LIMITE := sqldados.fn_atualizaLimiteCR(crlimit, dtcrlimit)         as limite,
       IFNULL(V.valorDevido, 0.00) + IFNULL(D.valorDevido, 0.00)           as limiteUsado,
       @LIMITE - IFNULL(V.valorDevido, 0.00) - IFNULL(D.valorDevido, 0.00) as limiteDisponivel
from sqldados.custp            AS C
  LEFT JOIN T_VALOR_DEVIDO     AS V
	      ON V.custno = C.no
  LEFT JOIN T_VALOR_DEVIDO_DUP AS D
	      ON D.custno = C.no
WHERE no = @CT;

