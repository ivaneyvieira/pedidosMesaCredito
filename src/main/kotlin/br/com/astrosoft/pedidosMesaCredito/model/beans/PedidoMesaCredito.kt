package br.com.astrosoft.pedidosMesaCredito.model.beans

import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.*
import br.com.astrosoft.pedidosMesaCredito.model.saci
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class PedidoMesaCredito(
  val storeno: Int,
  val pedido: Int,
  val status: Int,
  val datePedido: LocalDate,
  val timePedido: LocalTime,
  val custno: Int,
  val nome: String,
  val documento: String,
  val dtNascimento: LocalDate?,
  val renda: Double,
  val filial: String,
  val valor: Double,
  val desconto: Double,
  val entrada: Double,
  val parcelas: Double,
  val parcelaTotal: Double,
  val quant: Int,
  val totalFinanciado: Double,
  val statusCrediario: Int,
  val userAnalise: Int,
  val analistaName: String
) {
  fun marcaStatusCrediario(status: StatusCrediario, userSaci: UserSaci) {
    saci.marcaStatusCrediario(storeno, pedido, userSaci.no, status.num)
  }

  fun limite() = saci.limiteCredito(custno)

  val limiteDisponivel
    get() = limite()?.limiteDisponivel ?: 0.00

  fun findPedidoStatus() = saci.findPedidoStatus(storeno, pedido)

  fun filtroPedido(pedidoNum: Int) = (pedidoNum == this.pedido) || (pedidoNum == 0)

  fun filtroCliente(cliente: String) =
    (this.nome.startsWith(cliente)) || (cliente == "") || (this.custno.toString()
      .startsWith(cliente))

  fun filtroAnalista(analista: String) =
    (this.analistaName.contains(analista, ignoreCase = true)) || (analista == "")

  val dataHoraStatus
    get() = LocalDateTime.of(datePedido, timePedido)
  val parcelaDesc: String
    get() {
      val formatNumber = DecimalFormat("#,##0.00")
      val parcelaFormat = formatNumber.format(parcelas)
      return "$quant x $parcelaFormat"
    }
  val statusCrediarioEnum
    get() = StatusCrediario.valueByNum(statusCrediario)
  val statusPedidoEnum
    get() = StatusSaci.valueByNum(status)
  val statusPedidoStr
    get() = statusPedidoEnum.descricao
  val statusCrediarioStr
    get() = statusCrediarioEnum.descricao

  companion object {
    private fun listaPedidos(
      userSaci: UserSaci?,
      status: List<StatusCrediario>
    ): List<PedidoMesaCredito> {
      return saci.listaPedidoMesa(status.map { it.num }).filtroLoja(userSaci?.storeno)
    }

    fun listaAberto(userSaci: UserSaci?): List<PedidoMesaCredito> {
      return listaPedidos(userSaci, listOf(ABERTO, ANALISE))
    }

    fun listaAnalise(userSaci: UserSaci?): List<PedidoMesaCredito> {
      return listaPedidos(userSaci, listOf(ANALISE))
    }

    fun listaAprovado(userSaci: UserSaci?): List<PedidoMesaCredito> {
      return listaPedidos(userSaci, listOf(APROVADO))
    }

    fun listaReprovado(userSaci: UserSaci?): List<PedidoMesaCredito> {
      return listaPedidos(userSaci, listOf(REPROVADO))
    }

    fun listaPendente(userSaci: UserSaci?): List<PedidoMesaCredito> {
      return listaPedidos(userSaci, listOf(PENDENTE))
    }
  }
}

private fun List<PedidoMesaCredito>.filtroLoja(storeno: Int?): List<PedidoMesaCredito> {
  return this.filter { it.storeno == storeno || storeno == 0 }
}

enum class StatusCrediario(val num: Int, val descricao: String) {
  ABERTO(0, "Aberto"),
  ANALISE(1, "Em análise"),
  APROVADO(2, "Aprovado"),
  REPROVADO(3, "Reprovado"),
  PENDENTE(4, "Pendencia"),
  CONTRATO(5, "Contrato");

  companion object {
    fun valueByNum(num: Int) = values().firstOrNull { it.num == num } ?: ABERTO
  }
}

enum class StatusSaci(val numero: Int, val descricao: String, val analise: Boolean) {
  INCLUIDO(0, "Incluído", false),
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
    fun valueByNum(num: Int) = values().firstOrNull { it.numero == num } ?: INCLUIDO
  }
}