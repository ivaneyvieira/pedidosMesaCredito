package br.com.astrosoft.pedidosMesaCredito.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.framework.viewmodel.fail
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ABERTO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.PENDENTE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci

class PedidoMesaCreditoViewModel(view: IPedidoMesaCreditoView): ViewModel<IPedidoMesaCreditoView>(view) {
  fun updateGridAberto() {
    view.updateGridAberto(listAberto())
  }
  
  private fun listAberto(): List<PedidoMesaCredito> {
    val filtro = view.filtroAberto
    return PedidoMesaCredito.listaAberto()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente()) &&
        it.filtroAnalista(filtro.analista())
      }
  }
  
  fun updateGridAnalise() {
    view.updateGridAnalise(listAnalise())
  }
  
  private fun listAnalise(): List<PedidoMesaCredito> {
    val filtro = view.filtroAnalise
    return PedidoMesaCredito.listaAnalise()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente()) &&
        it.filtroAnalista(filtro.analista())
      }
  }
  
  fun updateGridAprovado() {
    view.updateGridAprovado(listAprovado())
  }
  
  private fun listAprovado(): List<PedidoMesaCredito> {
    val filtro = view.filtroAprovado
    return PedidoMesaCredito.listaAprovado()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente()) &&
        it.filtroAnalista(filtro.analista())
      }
  }
  
  fun updateGridReprovado() {
    view.updateGridReprovado(listReprovado())
  }
  
  private fun listReprovado(): List<PedidoMesaCredito> {
    val filtro = view.filtroReprovado
    return PedidoMesaCredito.listaReprovado()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente()) &&
        it.filtroAnalista(filtro.analista())
      }
  }
  
  
  fun updateGridPendente() {
    view.updateGridPendente(listPendente())
  }
  
  private fun listPendente(): List<PedidoMesaCredito> {
    val filtro = view.filtroPendente
    return PedidoMesaCredito.listaPendente()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente()) &&
        it.filtroAnalista(filtro.analista())
      }
  }
  
  //
  fun marcaStatusCrediario(pedido: PedidoMesaCredito, status: StatusCrediario, userSaci: UserSaci) = exec {
    pedido.marcaStatusCrediario(status, userSaci)
    when(status) {
      ABERTO  -> updateGridAberto()
      ANALISE -> updateGridAnalise()
      APROVADO  -> updateGridAprovado()
      REPROVADO -> updateGridReprovado()
      PENDENTE  -> updateGridPendente()
    }
    view.selectTab(status)
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

interface IPedidoMesaCreditoView: IView {
  fun updateGridAberto(itens: List<PedidoMesaCredito>)
  fun updateGridAnalise(itens: List<PedidoMesaCredito>)
  fun updateGridAprovado(itens: List<PedidoMesaCredito>)
  fun updateGridReprovado(itens: List<PedidoMesaCredito>)
  fun updateGridPendente(itens: List<PedidoMesaCredito>)
  
  val filtroAberto: IFiltroAberto
  val filtroAnalise: IFiltroAnalise
  val filtroAprovado: IFiltroAprovado
  val filtroReprovado: IFiltroReprovado
  val filtroPendente: IFiltroPendente
  
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
  
  fun selectTab(status: StatusCrediario)
}

data class SenhaUsuario(var nome: String, var senha: String?)