package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroContrato
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.DataProvider

class PainelGridContrato(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit) :
        PainelGrid<Contrato>(view, blockUpdate) {
  override fun HasComponents.gridPanel(dataProvider: DataProvider<Contrato, *>,
                                       block: (Grid<Contrato>).() -> Unit): Grid<Contrato> {
    return grid(dataProvider, block)
  }

  override fun filterBar() = FilterBarContrato()

  override fun Grid<Contrato>.gridConfig() {
    addColumnButton(VaadinIcon.PRINT, "ImprimirContrato", view::imprimirContrato)
    val colContrato = contratoNumero()
    contratoCodigo()
    contratoCliente()
    contratoData()
    contratoTotal()
    contratoAnalista()
    val ordem = GridSortOrder.asc(colContrato).build()
    this.sort(ordem)
  }

  inner class FilterBarContrato : FilterBar(), IFiltroContrato {
    lateinit var edtCliente: TextField
    lateinit var edtAnalista: TextField

    override fun FilterBar.contentBlock() {
      edtCliente = cliente {
        addValueChangeListener { blockUpdate() }
      }
      edtAnalista = analista {
        addValueChangeListener { blockUpdate() }
      }
    }

    override fun cliente(): String = edtCliente.value ?: ""
    override fun analista(): String = edtAnalista.value ?: ""
  }
}