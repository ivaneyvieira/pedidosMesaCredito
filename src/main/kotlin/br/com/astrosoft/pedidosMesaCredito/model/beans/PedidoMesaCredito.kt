package br.com.astrosoft.pedidosMesaCredito.model.beans

import br.com.astrosoft.AppConfig
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ABERTO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.PENDENTE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPROVADO
import br.com.astrosoft.pedidosMesaCredito.model.saci
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class PedidoMesaCredito(val storeno: Int,
                             val pedido: Int,
                             val status: Int,
                             val datePedido: LocalDate,
                             val timePedido: LocalTime,
                             val custno: Int,
                             val nome: String,
                             val filial: String,
                             val valor: Double,
                             val desconto: Double,
                             val entrada: Double,
                             val parcelas: Double,
                             val quant: Int,
                             val totalFinanciado: Double,
                             val statusCrediario: Int,
                             val userAnalise: Int) {
  fun marcaStatusCrediario(status: StatusCrediario, userSaci: UserSaci) {
    saci.marcaStatusCrediario(storeno, pedido, userSaci.no, status.num)
  }
  
  fun filtroPedido(pedidoNum: Int) = (pedidoNum == this.pedido) || (pedidoNum == 0)
  
  fun filtroCliente(cliente: String) = (this.nome.startsWith(cliente)) || (cliente == "")
                                       || (this.custno.toString().startsWith(cliente))
  
  fun filtroAnalista(analista: String) =  (this.analistaName.contains(analista, ignoreCase = true)) || (analista == "")
  
  val dataHoraStatus
    get() = LocalDateTime.of(datePedido, timePedido)
  val parcelaDesc: String
    get() {
      val formatNumber = DecimalFormat("#,##0.00")
      val parcelaFormat = formatNumber.format(parcelas)
      return "$quant x $parcelaFormat"
    }
  val analistaName
    get() = saci.findAllUser()
              .firstOrNull {it.no == userAnalise}?.funcionario ?: ""
  val isUserValid
    get() = userSaci.admin || userSaci.no == userAnalise
  val statusCrediarioEnum
    get() = StatusCrediario.valueByNum(statusCrediario)
  val statusPedidoEnum
    get() = StatusSaci.valueByNum(status)
  val statusPedidoStr
    get() = statusPedidoEnum.descricao
  val statusCrediarioStr
    get() = statusCrediarioEnum.descricao
  
  companion object {
    private val userSaci: UserSaci
      get() =AppConfig.userSaci as UserSaci
    
    private fun listaPedidos(status: List<StatusCrediario>): List<PedidoMesaCredito> {
      return saci.listaPedidoMesa(status.map {it.num}).filtroLoja(userSaci.storeno)
    }
    
    fun listaAberto(): List<PedidoMesaCredito> {
      return listaPedidos(listOf(ABERTO, ANALISE)).filtroLoja(userSaci.storeno)
    }
    
    fun listaAnalise(): List<PedidoMesaCredito> {
      return listaPedidos(listOf(ANALISE)).filtroLoja(userSaci.storeno)
    }
    
    fun listaAprovado(): List<PedidoMesaCredito> {
      return listaPedidos(listOf(APROVADO)).filtroLoja(userSaci.storeno)
    }
    
    fun listaReprovado(): List<PedidoMesaCredito> {
      return listaPedidos(listOf(REPROVADO)).filtroLoja(userSaci.storeno)
    }
    
    fun listaPendente(): List<PedidoMesaCredito> {
      return listaPedidos(listOf(PENDENTE)).filtroLoja(userSaci.storeno)
    }
  }
}

private fun List<PedidoMesaCredito>.filtroLoja(storeno: Int): List<PedidoMesaCredito> {
  return this.filter {it.storeno == storeno || storeno == 0}
}

enum class StatusCrediario(val num: Int, val descricao: String) {
  ABERTO(0, "Aberto"),
  ANALISE(1, "Em análise"),
  APROVADO(2, "Aprovado"),
  REPROVADO(3, "Reprovado"),
  PENDENTE(4, "Pendencia");
  
  companion object {
    fun valueByNum(num: Int) = values()
                                 .firstOrNull {it.num == num} ?: ABERTO
  }
}

enum class StatusSaci(val numero: Int, val descricao: String, val analise: Boolean) {
  INCLUIDO(0, "Incluído", true),
  ORCADO(1, "Orçado", true),
  RESERVADO(2, "Reservado", true),
  VENDIDO(3, "Vendido", false),
  EXPIRADO(4, "Expirado", false),
  CANCELADO(5, "Cancelado", false),
  RESERVADO_B(6, "Reserva B", true),
  TRANSITO(7, "Trânsito", false),
  FUTURA(8, "Futura", false);
  
  override fun toString() = descricao
  
  companion object {
    fun valueByNum(num: Int) = values()
                                 .firstOrNull {it.numero == num} ?: INCLUIDO
  }
}