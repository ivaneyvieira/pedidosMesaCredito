package br.com.astrosoft

import br.com.astrosoft.pedidosMesaCredito.model.saci
import br.com.astrosoft.framework.spring.IUser
import br.com.astrosoft.framework.spring.SecurityUtils
import br.com.astrosoft.framework.view.ViewUtil

object AppConfig {
  val version = ViewUtil.versao
  const val commpany = "Pintos"
  const val title = "Pedidos da mesa de crédito"
  const val shortName = title
  const val iconPath = "icons/order.png"
  
  val test : Boolean?
    get() = false
  
  val userDetails
    get() = SecurityUtils.userDetails
  val userSaci
    get() = userDetails?.user
  
  fun findUser(username : String?) : IUser? = saci.findUser(username).firstOrNull()
}
