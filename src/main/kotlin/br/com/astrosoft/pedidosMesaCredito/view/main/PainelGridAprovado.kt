package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.ListDataProvider

class PainelGridAprovado(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit):
  PainelGrid<PedidoMesaCredito>(view, blockUpdate) {
  override fun Grid<PedidoMesaCredito>.gridConfig() {
    addColumnButton(VaadinIcon.COG_O, "Capacitor", view::pesquisaCapacitor)

    colNumPedido()
    colDataHoraPedido()
    colCodigo()
    colNome()
    colFilial()
    colStatus()
    colValor()
    colEntrada()
    colParcelasDesc()
    colAnalista()
    colParcelasTotal()
    colLimiteDisponivel()
    (dataProvider as ListDataProvider).setSortComparator {o1, o2 ->
      o1.dataHoraStatus.compareTo(o2.dataHoraStatus)
    }
  }
  
  override fun filterBar() = FilterBarPedido()
  
  inner class FilterBarPedido: FilterBar(), IFiltroAprovado {
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
  
  override fun (@VaadinDsl HasComponents).gridPanel(dataProvider: DataProvider<PedidoMesaCredito, *>,
                                                    block: (Grid<PedidoMesaCredito>).() -> Unit): Grid<PedidoMesaCredito> {
    return grid(dataProvider, block)
  }
}
