package br.com.astrosoft.pedidosMesaCredito.model.beans

import br.com.astrosoft.AppConfig
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.NOVO
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
              .firstOrNull {it.no == userAnalise}?.login ?: ""
  val isNovo
    get() = statusCrediario == 0
  val isAnalise
    get() = statusCrediario == 1 && (userSaci.admin || userSaci.no == userAnalise)
  val isAprovado
    get() = statusCrediario == 2 && (userSaci.admin || userSaci.no == userAnalise)
  val isReprovado
    get() = statusCrediario == 3 && (userSaci.admin || userSaci.no == userAnalise)
  val statusCrediarioEnum
    get() = StatusCrediario.values()
              .toList()
              .firstOrNull() ?: NOVO
  
  companion object {
    private val userSaci: UserSaci by lazy {
      AppConfig.userSaci as UserSaci
    }
    
    @Synchronized
    fun updateList(): List<PedidoMesaCredito> {
      return saci.listaPedidoRetira()
    }
    
    fun listaNovo(): List<PedidoMesaCredito> {
      return updateList().filter {it.isNovo}
    }
    
    fun listaAnalise(): List<PedidoMesaCredito> {
      return updateList().filter {it.isAnalise}
    }
    
    fun listaAprovado(): List<PedidoMesaCredito> {
      return updateList().filter {it.isAprovado}
    }
    
    fun listaReprovado(): List<PedidoMesaCredito> {
      return updateList().filter {it.isReprovado}
    }
  }
}

enum class StatusCrediario(val num: Int, val descicao: String) {
  NOVO(0, "Novo"),
  ANALISE(1, "Em análise"),
  APROVADO(2, "Aprovado"),
  REPOVADO(3, "Reprovado");
}
