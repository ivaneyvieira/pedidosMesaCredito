package br.com.astrosoft.pedidosMesaCredito.view.main

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.framework.view.SubWindowPDF
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.framework.view.tabGrid
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
import br.com.astrosoft.pedidosMesaCredito.model.beans.PedidoMesaCredito
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario
import br.com.astrosoft.pedidosMesaCredito.model.beans.StatusCrediario.*
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.view.layout.PedidoMesaCreditoLayout
import br.com.astrosoft.pedidosMesaCredito.viewmodel.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant.LUMO_SMALL
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(layout = PedidoMesaCreditoLayout::class)
@PageTitle(AppConfig.title)
@HtmlImport("frontend://styles/shared-styles.html")
class PedidoMesaCreditoView : ViewLayout<PedidoMesaCreditoViewModel>(), IPedidoMesaCreditoView {
  private lateinit var labelStatus: Label
  private lateinit var statusBar: HorizontalLayout
  private var userSaci: UserSaci? = null
  private lateinit var thread: FeederThread
  private var tabMain: TabSheet
  private val gridAberto = PainelGridAberto(this) { viewModel.updateGridAberto() }
  private val gridAnalise = PainelGridAnalise(this) { viewModel.updateGridAnalise() }
  private val gridAprovado = PainelGridAprovado(this) { viewModel.updateGridAprovado() }
  private val gridReprovado = PainelGridReprovado(this) { viewModel.updateGridReprovado() }
  private val gridPendente = PainelGridPendente(this) { viewModel.updateGridPendente() }
  private val gridContrato = PainelGridContrato(this) { viewModel.updateGridContrato() }
  override val viewModel: PedidoMesaCreditoViewModel = PedidoMesaCreditoViewModel(this)

  override fun isAccept() = true

  override fun userSaci(): UserSaci? {
    if (userSaci == null) {
      val user = AppConfig.userSaci
      userSaci = user as UserSaci?
    }
    return userSaci
  }

  override fun showContratoPdf(report: ByteArray) {
    SubWindowPDF("contrato", report).open()
  }

  override fun pesquisaCapacitor(pedidoMesaCredito: PedidoMesaCredito?) {
    viewModel.pesquisaCapacitor(pedidoMesaCredito)
  }

  fun access(runnable: (UI) -> Unit) {
    ui.ifPresent { ui ->
      ui.access {
        runnable(ui)
      }
    }
  }

  override fun openLink(link: String) {
    access { ui ->
      ui.page.open(link, "_blank")
    }
  }

  fun animateOn(numPontos : Int) {
    access {
      labelStatus.text = "Processando " + "".rpad(numPontos, ".")
    }
  }

  val threadAnimate = ThreadCancel(onCommand = {
    animateOn(1)
    Thread.sleep(500)
    animateOn(2)
    Thread.sleep(500)
    animateOn(3)
    Thread.sleep(500)
  }, onStart = {
    access {
      this.statusBar.isVisible = true
    }
  }, onStop = {
    access {
      this.statusBar.isVisible = false
    }
  })

  override fun startAnimate() {
    threadAnimate.start()
  }

  override fun stopAnimate() {
    threadAnimate.cancel()
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
      tabGrid(TAB_CONTRATO, gridContrato)
    }
    statusBar = horizontalLayout {
      labelStatus = label("Processando ...")
      isVisible = false
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

  override fun updateGridContrato(itens: List<Contrato>) {
    gridContrato.updateGrid(itens)
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
  override val filtroContrato: IFiltroContrato
    get() = gridContrato.filterBar as IFiltroContrato

  override fun marcaStatusCrediario(pedidoMesaCredito: PedidoMesaCredito?, status: StatusCrediario) {
    marcaUsuario(pedidoMesaCredito) { user, pedido ->
      viewModel.marcaStatusCrediario(pedido, status, user)
    }
  }

  override fun imprimirContrato(contrato: Contrato?) {
    viewModel.imprimirContrato(contrato)
  }

  override fun selectTab(status: StatusCrediario) {
    when (status) {
      ABERTO    -> tabMain.selectedIndex = 0
      ANALISE   -> tabMain.selectedIndex = 1
      PENDENTE  -> tabMain.selectedIndex = 2
      APROVADO  -> tabMain.selectedIndex = 3
      REPROVADO -> tabMain.selectedIndex = 4
      CONTRATO  -> tabMain.selectedIndex = 5
    }
  }

  private fun marcaUsuario(pedidoMesaCredito: PedidoMesaCredito?, action: (UserSaci, PedidoMesaCredito) -> Unit) {
    if (pedidoMesaCredito == null) showError("Pedido não selecionado")
    else {
      val userSaci = AppConfig.userSaci as UserSaci
      val form = FormUsuario(userSaci.login)

      showForm("Senha do Usuário", form) {
        val senha = form.usuario.senha ?: "#######"
        if (senha == userSaci.senha) action(userSaci, pedidoMesaCredito)
        else showError("A senha não confere")
      }
    }
  }

  private fun desmarcaUsuario(pedidoMesaCredito: PedidoMesaCredito?, action: (PedidoMesaCredito) -> Unit) {
    if (pedidoMesaCredito == null) showError("Pedido não selecionado")
    else action(pedidoMesaCredito)
  }

  companion object {
    const val TAB_ABERTO: String = "Aberto"
    const val TAB_ANALISE: String = "Em Analise"
    const val TAB_APROVADO: String = "Aprovado"
    const val TAB_REPROVADO: String = "Reprovado"
    const val TAB_PENDENTE: String = "Pendente"
    const val TAB_CONTRATO: String = "Contratos"
  }

  inner class FeederThread(private val ui: UI, val viewModel: PedidoMesaCreditoViewModel) : Thread() {
    override fun run() {
      try {
        while (true) {
          sleep(10000)
          ui.access {
            viewModel.updateGridAberto()
          }
        }
      } catch (e: InterruptedException) {
        e.printStackTrace()
      }
    }
  }
}

class ThreadCancel(val onStart: () -> Unit = {}, val onCommand: () -> Unit = {}, val onStop: () -> Unit = {}) :
        Thread() {
  private var canCancel: Boolean = false

  override fun run() {
    canCancel = true
    onStart()
    while (canCancel) {
      onCommand()
    }
    onStop()
  }

  fun cancel() {
    canCancel = false
  }
}

class FormUsuario(val username: String) : FormLayout() {
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

