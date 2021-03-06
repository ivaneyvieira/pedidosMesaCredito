package br.com.astrosoft.pedidosMesaCredito.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB
import br.com.astrosoft.pedidosMesaCredito.model.beans.*

class QuerySaci : QueryDB(driver, url, username, password) {
  fun findUser(login: String?): List<UserSaci> {
    login ?: return emptyList()
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", login)
    }
  }

  fun findAllUser(): List<UserSaci> {
    val sql = "/sqlSaci/userSenha.sql"
    return query(sql, UserSaci::class) {
      addParameter("login", "TODOS")
    }
  }

  fun updateUser(user: UserSaci) {
    val sql = "/sqlSaci/updateUser.sql"
    script(sql) {
      addOptionalParameter("no", user.no)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("storeno", user.storeno)
      addOptionalParameter("funcionario", user.funcionario)
    }
  }

  fun listaPedidoMesa(status: List<Int>): List<PedidoMesaCredito> {
    val sql = "/sqlSaci/pedidoMesaCredito.sql" //val data = LocalDate.now().minusDays(7).toSaciDate()
    val statusSaci = StatusSaci.values().filter { it.analise }.map { it.numero }
    return query(sql, PedidoMesaCredito::class) {
      addOptionalParameter("status", status)
      addOptionalParameter("statusSaci", statusSaci)
    }
  }

  fun findPedidoStatus(loja: Int, numPedio: Int): PedidoStatus? {
    val sql = "/sqlSaci/pedidoStatus.sql"

    return query(sql, PedidoStatus::class) {
      addOptionalParameter("storeno", loja)
      addOptionalParameter("ordno", numPedio)
    }.firstOrNull()
  }

  fun marcaStatusCrediario(storeno: Int, pedido: Int, userno: Int, status: Int) {
    val sql = "/sqlSaci/marcaStatusCrediario.sql"

    return script(sql) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("pedido", pedido)
      addOptionalParameter("userno", userno)
      addOptionalParameter("status", status)
    }
  }

  fun findContratos(): List<Contrato> {
    val sql = "/sqlSaci/contratosHoje.sql"

    return query(sql, Contrato::class)
  }

  fun limiteCredito(codigo: Int): Limite? {
    val sql = "/sqlSaci/limiteCredito.sql"

    return query(sql, Limite::class) {
      addOptionalParameter("codigo", codigo)
    }.firstOrNull()
  }

  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer = url.split("/").getOrNull(2)
  }
}

val saci = QuerySaci()