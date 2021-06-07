package br.com.astrosoft.pedidosMesaCredito.view.layout

import com.vaadin.flow.router.*
import javax.servlet.http.HttpServletResponse

@ParentLayout(PedidoMesaCreditoLayout::class)
class CustomNotFoundTarget : RouteNotFoundError() {
  override fun setErrorParameter(event: BeforeEnterEvent, parameter: ErrorParameter<NotFoundException>): Int {
    element.text = "My custom not found class!"
    return HttpServletResponse.SC_NOT_FOUND
  }
}