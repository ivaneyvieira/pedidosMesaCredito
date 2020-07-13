UPDATE sqldados.eord
SET eord.s15 = :userno
WHERE ordno = :ordno
  AND storeno = :storeno