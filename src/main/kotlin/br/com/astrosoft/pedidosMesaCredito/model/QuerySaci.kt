package br.com.astrosoft.pedidosMesaCredito.model

import br.com.astrosoft.framework.model.QueryDB
import br.com.astrosoft.framework.util.DB
import br.com.astrosoft.framework.util.toSaciDate
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import java.time.LocalDate

class QuerySaci: QueryDB(driver, url, username, password) {
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
      addOptionalParameter("login", user.login)
      addOptionalParameter("bitAcesso", user.bitAcesso)
      addOptionalParameter("storeno", user.storeno)
    }
  }
  
  fun listaPedidoRetira(): List<PedidoMesaCredito> {
    val sql = "/sqlSaci/pedidoMesaCredito.sql"
    val data =
      LocalDate.now()
        .toSaciDate();
    return query(sql, PedidoMesaCredito::class) {
      addOptionalParameter("date", 20200713)
    }
  }
  
  fun marcaStatusCrediario(storeno : Int, pedido : Int, userno: Int, status: Int) {
    val sql = "/sqlSaci/marcaStatusCrediario.sql"

    return script(sql) {
      addOptionalParameter("storeno", storeno)
      addOptionalParameter("pedido", pedido)
      addOptionalParameter("userno", userno)
      addOptionalParameter("status", status)
    }
  }
  
  companion object {
    private val db = DB("saci")
    internal val driver = db.driver
    internal val url = db.url
    internal val username = db.username
    internal val password = db.password
    internal val test = db.test
    val ipServer =
      url.split("/")
        .getOrNull(2)
  }
}

val saci = QuerySaci()