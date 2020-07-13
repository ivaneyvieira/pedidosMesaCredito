UPDATE sqldados.eord
SET eord.s14 = :userno
WHERE ordno = :ordno
  AND storeno = :storeno