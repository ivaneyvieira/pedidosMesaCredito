package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDate
import br.com.astrosoft.framework.view.addColumnLocalDateTime
import br.com.astrosoft.framework.view.addColumnLocalTime
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

fun Grid<PedidoMesaCredito>.colnumPedido() = addColumnInt(PedidoMesaCredito::pedido) {
  setHeader("Pedido")
  width = "7em"
}

fun Grid<PedidoMesaCredito>.colDataHoraPedido() = addColumnLocalDateTime(PedidoMesaCredito::dataHoraStatus) {
  setHeader("Entrada no status")
}

fun Grid<PedidoMesaCredito>.colNome() = addColumnString(PedidoMesaCredito::nome) {
  setHeader("Nome")
}

fun Grid<PedidoMesaCredito>.colAnalista() = addColumnString(PedidoMesaCredito::analistaName) {
  setHeader("Analista")
}

fun Grid<PedidoMesaCredito>.colFilial() = addColumnString(PedidoMesaCredito::filial) {
  setHeader("Filial")
}

fun Grid<PedidoMesaCredito>.colValor() = addColumnDouble(PedidoMesaCredito::valor) {
  setHeader("Valor")
}

fun Grid<PedidoMesaCredito>.colParcelasDesc() = addColumnString(PedidoMesaCredito::parcelaDesc) {
  setHeader("Parcela")
}

//Campos de filtro
fun (@VaadinDsl HasComponents).cliente(block: (@VaadinDsl TextField).() -> Unit = {}) = textField("Cliente") {
  this.valueChangeMode = ValueChangeMode.TIMEOUT
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  this.isAutofocus = true
  block()
}

fun (@VaadinDsl HasComponents).pedido(block: (@VaadinDsl IntegerField).() -> Unit = {}) = integerField("Pedido") {
  this.valueChangeMode = ValueChangeMode.TIMEOUT
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  block()
}



