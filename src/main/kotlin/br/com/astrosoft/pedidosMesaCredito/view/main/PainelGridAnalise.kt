package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.PainelGrid
import br.com.astrosoft.framework.view.addColumnButton
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAnalise
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

class PainelGridAnalise(view: IPedidoMesaCreditoView, blockUpdate: () -> Unit) :
        PainelGrid<PedidoMesaCredito>(view, blockUpdate) {
  override fun Grid<PedidoMesaCredito>.gridConfig() {
    addColumnButton(VaadinIcon.THUMBS_UP_O, "Aprova proposta", view::marcaAprovado)
    addColumnButton(VaadinIcon.THUMBS_DOWN_O, "Reprova proposta", view::marcaReprovado)
    addColumnButton(VaadinIcon.CLOCK, "Proposta pendente", view::marcaPendente)
    addColumnButton(VaadinIcon.COG_O, "Capacitor", view::pesquisaCapacitor)

    colTipoContrato()
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
    (dataProvider as ListDataProvider).setSortComparator { o1, o2 ->
      o1.dataHoraStatus.compareTo(o2.dataHoraStatus)
    }
  }

  override fun filterBar() = FilterBarPedido()

  inner class FilterBarPedido : FilterBar(), IFiltroAnalise {
    private lateinit var edtPedido: IntegerField
    private lateinit var edtCliente: TextField
    private lateinit var edtAnalista: TextField

    override fun FilterBar.contentBlock() {
      edtPedido = pedido {
        addValueChangeListener { blockUpdate() }
      }
      edtCliente = cliente {
        addValueChangeListener { blockUpdate() }
      }
      edtAnalista = analista {
        addValueChangeListener { blockUpdate() }
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
