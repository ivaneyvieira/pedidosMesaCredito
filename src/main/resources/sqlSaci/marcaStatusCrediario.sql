UPDATE sqldados.eord
SET eord.s16 = :status,
    eord.s15 = :userno
WHERE ordno = :pedido
  AND storeno = :storeno
  AND eord.s16 != :status