package br.com.astrosoft.pedidosMesaCredito.model.beans

data class PedidoStatus(val loja: Int,
                        val numeroPedido: Int,
                        val status: Int,
                        val statusCrediario: Int,
                        val userAnalise: Int,
                        val analistaName: String)