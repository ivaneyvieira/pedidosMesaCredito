package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAnalise
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroNovo
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.vaadin.flow.component.button.ButtonVariant.LUMO_SMALL
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField

class PainelGridAnalise(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit): PainelGrid<PedidoMesaCredito>(view, blockUpdate) {
  override fun Grid<PedidoMesaCredito>.gridConfig() {
    addColumnButton(VaadinIcon.THUMBS_UP_O, view::marcaAprovado)
    addColumnButton(VaadinIcon.THUMBS_DOWN_O, view::marcaReprovado)
    colnumPedido()
    colDataHoraPedido()
    colNome()
    colFilial()
    colValor()
    colParcelasDesc()
    colAnalista()
  }
  
  override fun filterBar() = FilterBarPedido()
  
  inner class FilterBarPedido: FilterBar(), IFiltroAnalise {
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
