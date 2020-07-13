package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.tabGrid
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.NOVO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPOVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.view.layout.PedidoMesaCreditoLayout
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAnalise
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroNovo
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroReprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import br.com.astrosoft.pedidosMesaCredito.viewmodel.PedidoMesaCreditoViewModel
import br.com.astrosoft.pedidosMesaCredito.viewmodel.SenhaUsuario
import com.github.mvysny.karibudsl.v10.TabSheet
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.tabSheet
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_SMALL
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = PedidoMesaCreditoLayout::class)
@PageTitle(AppConfig.title)
@HtmlImport("frontend://styles/shared-styles.html")
class PedidoMesaCreditoView: ViewLayout<PedidoMesaCreditoViewModel>(), IPedidoMesaCreditoView {
  private var tabMain: TabSheet
  private val gridNovo = PainelGridNovo(this) {viewModel.updateGridNovo()}
  private val gridAnalise = PainelGridAnalise(this) {viewModel.updateGridAnalise()}
  private val gridAprovado = PainelGridAprovado(this) {viewModel.updateGridAprovado()}
  private val gridReprovado = PainelGridReprovado(this) {viewModel.updateGridReprovado()}
  override val viewModel: PedidoMesaCreditoViewModel = PedidoMesaCreditoViewModel(this)
  
  override fun isAccept() = true
  
  init {
    tabMain = tabSheet {
      setSizeFull()
      tabGrid(TAB_NOVO, gridNovo)
      tabGrid(TAB_ANALISE, gridAnalise)
      tabGrid(TAB_APROVADO, gridAprovado)
      tabGrid(TAB_REPROVADO, gridReprovado)
    }
    viewModel.updateGridNovo()
  }
  
  override fun updateGridNovo(itens: List<PedidoMesaCredito>) {
    gridNovo.updateGrid(itens)
  }
  
  override fun updateGridAnalise(itens: List<PedidoMesaCredito>) {
    gridAnalise.updateGrid(itens)
  }
  
  override fun updateGridAprovado(itens: List<PedidoMesaCredito>) {
    gridAprovado.updateGrid(itens)
  }
  
  override fun updateGridReprovado(itens: List<PedidoMesaCredito>) {
    gridReprovado.updateGrid(itens)
  }
  
  override val filtroNovo: IFiltroNovo
    get() = gridNovo.filterBar as IFiltroNovo
  override val filtroAnalise: IFiltroAnalise
    get() = gridAnalise.filterBar as IFiltroAnalise
  override val filtroAprovado: IFiltroAprovado
    get() = gridAprovado.filterBar as IFiltroAprovado
  override val filtroReprovado: IFiltroReprovado
    get() = gridReprovado.filterBar as IFiltroReprovado
  
  override fun marcaStatusCrediario(pedidoMesaCredito: PedidoMesaCredito?, status: StatusCrediario) {
    marcaUsuario(pedidoMesaCredito) {user, pedido ->
      viewModel.marcaStatusCrediario(pedido, status, user)
    }
  }
  
  override fun selectTab(status: StatusCrediario) {
    when(status){
      NOVO     -> tabMain.selectedIndex = 0
      ANALISE  -> tabMain.selectedIndex = 1
      APROVADO -> tabMain.selectedIndex = 2
      REPOVADO -> tabMain.selectedIndex = 3
    }
  }
  
  private fun marcaUsuario(pedidoMesaCredito: PedidoMesaCredito?, action: (UserSaci, PedidoMesaCredito) -> Unit) {
    if(pedidoMesaCredito == null)
      showError("Pedido não selecionado")
    else {
      val userSaci = AppConfig.userSaci as UserSaci
      val form = FormUsuario(userSaci.login)
      
      showForm("Senha do Usuário", form) {
        val senha = form.usuario.senha ?: "#######"
        if(senha == userSaci.senha)
          action(userSaci, pedidoMesaCredito)
        else
          showError("A senha não confere")
      }
    }
  }
  
  private fun desmarcaUsuario(pedidoMesaCredito: br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito?,
                              action: (br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito) -> Unit) {
    if(pedidoMesaCredito == null)
      showError("Pedido não selecionado")
    else
      action(pedidoMesaCredito)
  }
  

  
  companion object {
    const val TAB_NOVO: String = "Novos"
    const val TAB_ANALISE: String = "Em Analise"
    const val TAB_APROVADO: String = "Aprovado"
    const val TAB_REPROVADO: String = "Reprovado"
  }
}

class FormUsuario(val username: String): FormLayout() {
  private val binder = Binder<SenhaUsuario>(SenhaUsuario::class.java)
  
  init {
    textField("Nome") {
      isEnabled = false
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaUsuario::nome)
    }
    
    passwordField("Senha") {
      addThemeVariants(LUMO_SMALL)
      bind(binder).bind(SenhaUsuario::senha)
      this.isAutofocus = true
    }
    binder.bean = SenhaUsuario(username, "")
  }
  
  val usuario
    get() = binder.bean
}

