package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroNovo
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroReprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField

class PainelGridReprovado(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit):
  PainelGrid<PedidoMesaCredito>(view, blockUpdate) {
  override fun Grid<PedidoMesaCredito>.gridConfig() {
    colnumPedido()
    colDataHoraPedido()
    colNome()
    colFilial()
    colStatus()
    colValor()
    colParcelasDesc()
    colAnalista()
  }
  
  override fun filterBar() = FilterBarReprovado()
  
  inner class FilterBarReprovado: FilterBar(), IFiltroReprovado {
    lateinit var edtPedido: IntegerField
    lateinit var edtCliente: TextField
    
    override fun FilterBar.contentBlock() {
      edtPedido = pedido {
        addValueChangeListener {blockUpdate()}
      }
      edtCliente = cliente {
        addValueChangeListener {blockUpdate()}
      }
    }
    
    override fun pedido(): Int = edtPedido.value ?: 0
    
    override fun cliente(): String = edtCliente.value ?: ""
  }
}

