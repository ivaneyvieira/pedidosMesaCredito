package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAnalise
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroPendente
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider

class PainelGridPendente(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit): PainelGrid<PedidoMesaCredito>(view, blockUpdate) {
  override fun Grid<PedidoMesaCredito>.gridConfig() {
    addColumnButton(VaadinIcon.THUMBS_UP_O, view::marcaAprovado)
    addColumnButton(VaadinIcon.THUMBS_DOWN_O, view::marcaReprovado)
    colnumPedido()
    colDataHoraPedido()
    colNome()
    colFilial()
    colStatus()
    colValor()
    colParcelasDesc()
    colAnalista()
    (dataProvider as ListDataProvider).setSortComparator {o1, o2 ->
      o1.dataHoraStatus.compareTo(o2.dataHoraStatus)
    }
  }
  
  override fun filterBar() = FilterBarPedido()
  
  inner class FilterBarPedido: FilterBar(), IFiltroPendente {
    lateinit var edtPedido: IntegerField
    lateinit var edtCliente: TextField
    lateinit var edtAnalista: TextField
    
    override fun FilterBar.contentBlock() {
      edtPedido = pedido {
        addValueChangeListener {blockUpdate()}
      }
      edtCliente = cliente {
        addValueChangeListener {blockUpdate()}
      }
      edtAnalista = analista {
        addValueChangeListener {blockUpdate()}
      }
    }
    
    override fun pedido(): Int = edtPedido.value ?: 0
    override fun cliente(): String = edtCliente.value ?: ""
    override fun analista(): String = edtAnalista.value ?: ""
  }
}
