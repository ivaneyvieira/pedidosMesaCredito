package br.com.astrosoft.pedidosMesaCredito.model.beans

import br.com.astrosoft.pedidosMesaCredito.model.saci

data class Contrato(val loja: String,
                    val contrato: String,
                    val nomeCliente: String,
                    val codigoCliente: String,
                    val enderecoCliente: String,
                    val nomeAvalista: String,
                    val codigoAvalista: String,
                    val enderecoAvalista: String,
                    val totalAvista: String,
                    val despesas: String,
                    val entrada: String,
                    val afinanciar: String,
                    val plano: String,
                    val numeroContrato: String,
                    val grupo: String,
                    val dataVenda: String,
                    val dataVencimento: String,
                    val analistaNome: String,
                    val dataContrato: String,
                    val valorAprovado: String,
                    val pedido: String) {
  fun filtroCliente(cliente: String): Boolean {
    return nomeCliente.contains(cliente) || codigoCliente.startsWith(cliente)
  }

  fun filtroAnalista(analista: String): Boolean {
    return analistaNome.contains(analista)
  }

  companion object {
    fun findContratos() = saci.findContratos()
  }
}