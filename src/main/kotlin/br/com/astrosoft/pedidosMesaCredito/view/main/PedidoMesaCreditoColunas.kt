package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.framework.view.addColumnDouble
import br.com.astrosoft.framework.view.addColumnInt
import br.com.astrosoft.framework.view.addColumnLocalDateTime
import br.com.astrosoft.framework.view.addColumnString
import br.com.astrosoft.framework.view.right
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
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

fun Grid<PedidoMesaCredito>.colNumPedido() = addColumnInt(PedidoMesaCredito::pedido) {
  setHeader("Pedido")
  width = "5em"
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colCodigo() = addColumnInt(PedidoMesaCredito::custno) {
  setHeader("Código")
  width = "5em"
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colDataHoraPedido() = addColumnLocalDateTime(PedidoMesaCredito::dataHoraStatus) {
  setHeader("Entrada no status")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colNome() = addColumnString(PedidoMesaCredito::nome) {
  setHeader("Nome")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colAnalista() = addColumnString(PedidoMesaCredito::analistaName) {
  setHeader("Analista")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colLimiteDisponivel() = addColumnDouble(PedidoMesaCredito::limiteDisponivel) {
  setHeader("Limite Disponível")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colStatus() = addColumnString(PedidoMesaCredito::statusPedidoStr) {
  setHeader("Status")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colStatusCrediario() = addColumnString(PedidoMesaCredito::statusCrediarioStr) {
  setHeader("Status Analise")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colFilial() = addColumnString(PedidoMesaCredito::filial) {
  setHeader("Filial")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colValor() = addColumnDouble(PedidoMesaCredito::valor) {
  setHeader("Valor")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colEntrada() = addColumnDouble(PedidoMesaCredito::entrada) {
  setHeader("Entrada")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colParcelasTotal() = addColumnDouble(PedidoMesaCredito::parcelaTotal) {
  setHeader("Total Parcelas")
  isSortable = false
}

fun Grid<PedidoMesaCredito>.colParcelasDesc() = addColumnString(PedidoMesaCredito::parcelaDesc) {
  setHeader("Parcela")
  isSortable = false
}

//Colunas Contrato
fun Grid<Contrato>.contratoNumero() = addColumnString(Contrato::numeroContrato) {
  setHeader("Contrato")
  isSortable = false
}

fun Grid<Contrato>.contratoCodigo() = addColumnString(Contrato::codigoCliente) {
  setHeader("Código")
  isSortable = false
}

fun Grid<Contrato>.contratoCliente() = addColumnString(Contrato::nomeCliente) {
  setHeader("Cliente")
  isSortable = false
}

fun Grid<Contrato>.contratoData() = addColumnString(Contrato::dataContrato) {
  setHeader("Data")
  isSortable = false
}

fun Grid<Contrato>.contratoTotal() = addColumnString(Contrato::totalAvista) {
  setHeader("Total")
  right()
  isSortable = false
}

fun Grid<Contrato>.contratoAnalista() = addColumnString(Contrato::analistaNome) {
  setHeader("Analista")
  isSortable = false
}

//Campos de filtro
fun (@VaadinDsl HasComponents).cliente(block: (@VaadinDsl TextField).() -> Unit = {}) = textField("Cliente") {
  this.valueChangeMode = ValueChangeMode.TIMEOUT
  addThemeVariants(TextFieldVariant.LUMO_SMALL)
  this.isAutofocus = true
  block()
}

fun (@VaadinDsl HasComponents).analista(block: (@VaadinDsl TextField).() -> Unit = {}) = textField("Analista") {
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



