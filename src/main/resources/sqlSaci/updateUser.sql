UPDATE sqldados.users AS U
SET bits2    = :bitAcesso,
    auxLong1 = :storeno,
    auxStr = :funcionario
WHERE no = :no