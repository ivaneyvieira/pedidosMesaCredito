package br.com.astrosoft.pedidosMesaCredito.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.pedidosMesaCredito.model.RelatorioContrato
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ABERTO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.CONTRATO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.PENDENTE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci

class PedidoMesaCreditoViewModel(view: IPedidoMesaCreditoView): ViewModel<IPedidoMesaCreditoView>(view) {
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
    if(statusPedido.userAnalise == 0 || statusPedido.userAnalise == userSaci.no) pedido.marcaStatusCrediario(status,
                                                                                                             userSaci)
    else fail("Esse pedido pertente a analista ${statusPedido.analistaName}")
    when(status) {
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
    }.sortedBy {it.numeroContrato}
  }
  
  fun imprimirContrato(contrato: Contrato?) = exec {
    contrato ?: fail("Contrato não selecionado")
    val bytePdf = RelatorioContrato(contrato).build()
    view.showContratoPdf(bytePdf)
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

interface IPedidoMesaCreditoView: IView {
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
  
  fun imprimirContrato(contrato: Contrato?)
  
  fun selectTab(status: StatusCrediario)
  fun userSaci(): UserSaci?
  fun showContratoPdf(report: ByteArray)
}

data class SenhaUsuario(var nome: String, var senha: String?)