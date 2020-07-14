package br.com.astrosoft.pedidosMesaCredito.viewmodel

import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.NOVO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci

class PedidoMesaCreditoViewModel(view: IPedidoMesaCreditoView): ViewModel<IPedidoMesaCreditoView>(view) {
  fun updateGridNovo() {
    view.updateGridNovo(listNovo())
  }
  
  private fun listNovo(): List<PedidoMesaCredito> {
    val filtro = view.filtroNovo
    return PedidoMesaCredito.listaNovo()
      .filter {
        it.filtroPedido(filtro.pedido()) &&
        it.filtroCliente(filtro.cliente())
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
        it.filtroCliente(filtro.cliente())
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
        it.filtroCliente(filtro.cliente())
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
        it.filtroCliente(filtro.cliente())
      }
  }
  
  //
  fun marcaStatusCrediario(pedido: PedidoMesaCredito, status: StatusCrediario, userSaci: UserSaci) = exec {
    pedido.marcaStatusCrediario(status, userSaci)
    when(status) {
      NOVO      -> updateGridNovo()
      ANALISE   -> updateGridAnalise()
      APROVADO  -> updateGridAprovado()
      REPROVADO -> updateGridReprovado()
    }
    view.selectTab(status)
  }
}

interface IFiltroNovo {
  fun pedido(): Int
  fun cliente(): String
}

interface IFiltroAprovado {
  fun pedido(): Int
  fun cliente(): String
}

interface IFiltroAnalise {
  fun pedido(): Int
  fun cliente(): String
}

interface IFiltroReprovado {
  fun pedido(): Int
  fun cliente(): String
}

interface IPedidoMesaCreditoView: IView {
  fun updateGridNovo(itens: List<PedidoMesaCredito>)
  fun updateGridAnalise(itens: List<PedidoMesaCredito>)
  fun updateGridAprovado(itens: List<PedidoMesaCredito>)
  fun updateGridReprovado(itens: List<PedidoMesaCredito>)
  
  val filtroNovo: IFiltroNovo
  val filtroAnalise: IFiltroAnalise
  val filtroAprovado: IFiltroAprovado
  val filtroReprovado: IFiltroReprovado
  
  //
  fun marcaStatusCrediario(pedidoMesaCredito: PedidoMesaCredito?, status: StatusCrediario)
  fun marcaNovo(pedidoMesaCredito: PedidoMesaCredito?) {
    marcaStatusCrediario(pedidoMesaCredito, NOVO)
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
  
  fun selectTab(status: StatusCrediario)
}

data class SenhaUsuario(var nome: String, var senha: String?)