UPDATE sqldados.eord
SET eord.s13 = :userno
WHERE ordno = :ordno
  AND storeno = :storeno