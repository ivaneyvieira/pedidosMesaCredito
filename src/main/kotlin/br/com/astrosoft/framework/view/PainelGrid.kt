package br.com.astrosoft.framework.view

import br.com.astrosoft.pedidosMesaCredito.view.main.FilterBar
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.data.provider.ListDataProvider
import kotlin.reflect.KClass

abstract class PainelGrid<T: Any>(val view: IPedidoMesaCreditoView, val blockUpdate: () -> Unit): VerticalLayout() {
  private var grid: Grid<T>
  private val dataProvider = ListDataProvider<T>(mutableListOf())
  val filterBar: FilterBar by lazy {
    filterBar()
  }
  
  abstract fun (@VaadinDsl HasComponents).gridPanel(dataProvider : DataProvider<T, *>, block: (@VaadinDsl Grid<T>).() -> Unit) : Grid<T>
  
  init {
    this.setSizeFull()
    isMargin = false
    isPadding = false
    filterBar.also {add(it)}
    grid = gridPanel(dataProvider = dataProvider) {
      addThemeVariants(LUMO_COMPACT, LUMO_COLUMN_BORDERS, LUMO_ROW_STRIPES)
      this.gridConfig()
    }
  }
  
  fun refreshGrid() {
    dataProvider.refreshAll()
  }
  
  fun selectionItem(): T? = grid.asSingleSelect().value
  
  protected abstract fun filterBar(): FilterBar
  
  fun updateGrid(itens: List<T>) {
    grid.deselectAll()
    dataProvider.updateItens(itens)
  }
  
  protected abstract fun Grid<T>.gridConfig()
  
  fun selectedItems(): List<T> {
    return grid.selectedItems.toList()
  }
}

