package br.com.astrosoft.pedidosMesaCredito.viewmodel

import br.com.astrosoft.framework.util.DB
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.pedidosMesaCredito.model.Capacitor
import br.com.astrosoft.pedidosMesaCredito.model.RelatorioContrato
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.*
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.model.json.InputCapacitor
import kotlin.concurrent.thread

class PedidoMesaCreditoViewModel(view: IPedidoMesaCreditoView) : ViewModel<IPedidoMesaCreditoView>(view) {
  fun updateGridAberto() {
    view.updateGridAberto(listAberto())
  }

  private fun listAberto(): List<PedidoMesaCredito> {
    val filtro = view.filtroAberto
    return PedidoMesaCredito.listaAberto(view.userSaci()).filter {
      it.filtroPedido(filtro.pedido()) && it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }
  }

  fun updateGridAnalise() {
    view.updateGridAnalise(listAnalise())
  }

  private fun listAnalise(): List<PedidoMesaCredito> {
    val filtro = view.filtroAnalise
    return PedidoMesaCredito.listaAnalise(view.userSaci()).filter {
      it.filtroPedido(filtro.pedido()) && it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }
  }

  fun updateGridAprovado() {
    view.updateGridAprovado(listAprovado())
  }

  private fun listAprovado(): List<PedidoMesaCredito> {
    val filtro = view.filtroAprovado
    return PedidoMesaCredito.listaAprovado(view.userSaci()).filter {
      it.filtroPedido(filtro.pedido()) && it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }
  }

  fun updateGridReprovado() {
    view.updateGridReprovado(listReprovado())
  }

  private fun listReprovado(): List<PedidoMesaCredito> {
    val filtro = view.filtroReprovado
    return PedidoMesaCredito.listaReprovado(view.userSaci()).filter {
      it.filtroPedido(filtro.pedido()) && it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }
  }

  fun updateGridPendente() {
    view.updateGridPendente(listPendente())
  }

  private fun listPendente(): List<PedidoMesaCredito> {
    val filtro = view.filtroPendente
    return PedidoMesaCredito.listaPendente(view.userSaci()).filter {
      it.filtroPedido(filtro.pedido()) && it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }
  }

  fun marcaStatusCrediario(pedido: PedidoMesaCredito, status: StatusCrediario, userSaci: UserSaci) = exec {
    val statusPedido = pedido.findPedidoStatus() ?: fail("Pedido não encontrado")
    when {
      statusPedido.statusCrediario != status.num -> pedido.marcaStatusCrediario(status, userSaci)
      statusPedido.userAnalise != userSaci.no    -> fail("Esse pedido pertente a analista ${statusPedido.analistaName}")
    }
    when (status) {
      ABERTO    -> updateGridAberto()
      ANALISE   -> updateGridAnalise()
      APROVADO  -> updateGridAprovado()
      REPROVADO -> updateGridReprovado()
      PENDENTE  -> updateGridPendente()
      CONTRATO  -> updateGridContrato()
    }
    view.selectTab(status)
  }

  fun updateGridContrato() {
    view.updateGridContrato(listContrato())
  }

  private fun listContrato(): List<Contrato> {
    val filtro = view.filtroContrato
    return Contrato.findContratos().filter {
      it.filtroCliente(filtro.cliente()) && it.filtroAnalista(filtro.analista())
    }.sortedBy { it.numeroContrato }
  }

  fun imprimirContrato(contrato: Contrato?) = exec {
    contrato ?: fail("Contrato não selecionado")
    val bytePdf = RelatorioContrato(contrato).build()
    view.showContratoPdf(bytePdf)
  }

  fun pesquisaCapacitor(pedidoMesaCredito: PedidoMesaCredito?) = exec {
    if (pedidoMesaCredito == null) fail("Nenhum pedido foi selecionado")
    else {
      thread {
        view.startAnimate()
        val input = pedidoMesaCredito.toInputCapacitor()
        val output = Capacitor.execute(input, 3)

        val requestIdentification = output?.string("requestIdentification") ?: ""
        val link = Capacitor.link(requestIdentification)
        view.openLink(link)
        view.stopAnimate()
      }
    }
  }
}

interface IFiltroAberto {
  fun pedido(): Int
  fun cliente(): String
  fun analista(): String
}

interface IFiltroAprovado {
  fun pedido(): Int
  fun cliente(): String
  fun analista(): String
}

interface IFiltroAnalise {
  fun pedido(): Int
  fun cliente(): String
  fun analista(): String
}

interface IFiltroReprovado {
  fun pedido(): Int
  fun cliente(): String
  fun analista(): String
}

interface IFiltroPendente {
  fun pedido(): Int
  fun cliente(): String
  fun analista(): String
}

interface IFiltroContrato {
  fun cliente(): String
  fun analista(): String
}

interface IPedidoMesaCreditoView : IView {
  fun updateGridAberto(itens: List<PedidoMesaCredito>)
  fun updateGridAnalise(itens: List<PedidoMesaCredito>)
  fun updateGridAprovado(itens: List<PedidoMesaCredito>)
  fun updateGridReprovado(itens: List<PedidoMesaCredito>)
  fun updateGridPendente(itens: List<PedidoMesaCredito>)
  fun updateGridContrato(itens: List<Contrato>)

  val filtroAberto: IFiltroAberto
  val filtroAnalise: IFiltroAnalise
  val filtroAprovado: IFiltroAprovado
  val filtroReprovado: IFiltroReprovado
  val filtroPendente: IFiltroPendente
  val filtroContrato: IFiltroContrato

  //
  fun marcaStatusCrediario(pedidoMesaCredito: PedidoMesaCredito?, status: StatusCrediario)

  fun marcaAberto(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, ABERTO)
  }

  fun marcaAnalise(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, ANALISE)
  }

  fun marcaAprovado(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, APROVADO)
  }

  fun marcaReprovado(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, REPROVADO)
  }

  fun marcaPendente(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, PENDENTE)
  }

  fun pesquisaCapacitor(pedidoMesaCredito: PedidoMesaCredito?)

  fun imprimirContrato(contrato: Contrato?)

  fun selectTab(status: StatusCrediario)
  fun userSaci(): UserSaci?
  fun showContratoPdf(report: ByteArray)
  fun openLink(link: String)

  fun startAnimate()
  fun stopAnimate()
}

data class SenhaUsuario(var nome: String, var senha: String?)

fun PedidoMesaCredito.toInputCapacitor(): InputCapacitor {
  return InputCapacitor(documento = this.documento.replace("\\D".toRegex(), ""),
                        nome = this.nome,
                        dataNascimento = this.dtNascimento,
                        renda = this.renda,
                        consultaBacen = DB("saci").consultaBACEN)
}