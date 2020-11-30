package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.tabGrid
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ABERTO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.ANALISE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.APROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.PENDENTE
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.REPROVADO
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.view.layout.PedidoMesaCreditoLayout
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAberto
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAnalise
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroAprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroPendente
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IFiltroReprovado
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IPedidoMesaCreditoView
import br.com.astrosoft.pedidosMesaCredito.viewmodel.PedidoMesaCreditoViewModel
import br.com.astrosoft.pedidosMesaCredito.viewmodel.SenhaUsuario
import com.github.mvysny.karibudsl.v10.TabSheet
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.tabSheet
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
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
  var userSaci : UserSaci? = null
  lateinit var thread: FeederThread
  private var tabMain: TabSheet
  private val gridAberto = PainelGridAberto(this) {viewModel.updateGridAberto()}
  private val gridAnalise = PainelGridAnalise(this) {viewModel.updateGridAnalise()}
  private val gridAprovado = PainelGridAprovado(this) {viewModel.updateGridAprovado()}
  private val gridReprovado = PainelGridReprovado(this) {viewModel.updateGridReprovado()}
  private val gridPendente = PainelGridPendente(this) {viewModel.updateGridPendente()}
  override val viewModel: PedidoMesaCreditoViewModel = PedidoMesaCreditoViewModel(this)
  
  override fun isAccept() = true
  
  override fun userSaci(): UserSaci? {
    if (userSaci == null){
      val user = AppConfig.userSaci
      userSaci = user as UserSaci?
    }
    return userSaci
  }
  
  override fun onAttach(attachEvent: AttachEvent) {
    thread = FeederThread(attachEvent.ui, viewModel)
    thread.start()
  }
  
  override fun onDetach(detachEvent: DetachEvent?) {
    thread.interrupt()
  }
  
  init {
    tabMain = tabSheet {
      setSizeFull()
      tabGrid(TAB_ABERTO, gridAberto)
      tabGrid(TAB_ANALISE, gridAnalise)
      tabGrid(TAB_PENDENTE, gridPendente)
      tabGrid(TAB_APROVADO, gridAprovado)
      tabGrid(TAB_REPROVADO, gridReprovado)
    }
    viewModel.updateGridAberto()
  }
  
  override fun updateGridAberto(itens: List<PedidoMesaCredito>) {
    gridAberto.updateGrid(itens)
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
  
  override fun updateGridPendente(itens: List<PedidoMesaCredito>) {
    gridPendente.updateGrid(itens)
  }
  
  override val filtroAberto: IFiltroAberto
    get() = gridAberto.filterBar as IFiltroAberto
  override val filtroAnalise: IFiltroAnalise
    get() = gridAnalise.filterBar as IFiltroAnalise
  override val filtroAprovado: IFiltroAprovado
    get() = gridAprovado.filterBar as IFiltroAprovado
  override val filtroReprovado: IFiltroReprovado
    get() = gridReprovado.filterBar as IFiltroReprovado
  override val filtroPendente: IFiltroPendente
    get() = gridPendente.filterBar as IFiltroPendente
  
  override fun marcaStatusCrediario(pedidoMesaCredito: PedidoMesaCredito?, status: StatusCrediario) {
    //TODO Fazer teste de status válidos
    marcaUsuario(pedidoMesaCredito) {user, pedido ->
      viewModel.marcaStatusCrediario(pedido, status, user)
    }
  }
  
  override fun selectTab(status: StatusCrediario) {
    when(status) {
      ABERTO -> tabMain.selectedIndex = 0
      ANALISE -> tabMain.selectedIndex = 1
      PENDENTE -> tabMain.selectedIndex = 2
      APROVADO -> tabMain.selectedIndex = 3
      REPROVADO -> tabMain.selectedIndex = 4
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
  
  private fun desmarcaUsuario(pedidoMesaCredito: PedidoMesaCredito?,
                              action: (PedidoMesaCredito) -> Unit) {
    if(pedidoMesaCredito == null)
      showError("Pedido não selecionado")
    else
      action(pedidoMesaCredito)
  }
  
  companion object {
    const val TAB_ABERTO: String = "Aberto"
    const val TAB_ANALISE: String = "Em Analise"
    const val TAB_APROVADO: String = "Aprovado"
    const val TAB_REPROVADO: String = "Reprovado"
    const val TAB_PENDENTE: String = "Pendente"
  }
  
  inner class FeederThread(private val ui: UI, val viewModel: PedidoMesaCreditoViewModel):
    Thread() {
    override fun run() {
      try {
        while(true) {
          sleep(10000)
          
          ui.access {
            viewModel.updateGridAberto()
          }
        }
      } catch(e: InterruptedException) {
        e.printStackTrace()
      }
    }
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

