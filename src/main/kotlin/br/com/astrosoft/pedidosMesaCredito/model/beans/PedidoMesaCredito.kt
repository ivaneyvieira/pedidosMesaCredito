package br.com.astrosoft.pedidosMesaCredito.model.beans

import br.com.astrosoft.AppConfig
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.NOVO
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
  val isUserValid
  get() = userSaci.admin || userSaci.no == userAnalise
  val isNovo
    get() = statusCrediarioEnum == NOVO && statusPedidoEnum.analise
  val isAnalise
    get() = statusCrediarioEnum == ANALISE && isUserValid
  val isAprovado
    get() = statusCrediarioEnum == APROVADO && isUserValid
  val isReprovado
    get() = statusCrediarioEnum == REPROVADO && isUserValid
  val statusCrediarioEnum
    get() = StatusCrediario.valueByNum(statusCrediario)
  val statusPedidoEnum
    get() = StatusPedido.valueByNum(status)
  val statusStr
    get() = statusPedidoEnum.descricao
  
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
  REPROVADO(3, "Reprovado");
  
  companion object {
    fun valueByNum(num: Int) = values()
                                 .firstOrNull {it.num == num} ?: NOVO
  }
}

enum class StatusPedido(val numero: Int, val descricao: String, val analise : Boolean) {
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