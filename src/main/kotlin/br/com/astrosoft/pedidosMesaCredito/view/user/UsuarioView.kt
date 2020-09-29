package br.com.astrosoft.pedidosMesaCredito.view.user

import br.com.astrosoft.AppConfig
import br.com.astrosoft.framework.view.ViewLayout
import br.com.astrosoft.pedidosMesaCredito.model.beans.UserSaci
import br.com.astrosoft.pedidosMesaCredito.view.layout.PedidoMesaCreditoLayout
import br.com.astrosoft.pedidosMesaCredito.view.user.UserCrudFormFactory.Companion.TITLE
import br.com.astrosoft.pedidosMesaCredito.viewmodel.IUsuarioView
import br.com.astrosoft.pedidosMesaCredito.viewmodel.UsuarioViewModel
import com.github.mvysny.karibudsl.v10.alignSelf
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.comboBox
import com.github.mvysny.karibudsl.v10.formLayout
import com.github.mvysny.karibudsl.v10.getColumnBy
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.hr
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR
import com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY
import com.vaadin.flow.component.combobox.ComboBox.ItemFilter
import com.vaadin.flow.component.grid.ColumnTextAlign
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS
import com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT
import com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
import com.vaadin.flow.component.icon.VaadinIcon.CHECK_CIRCLE_O
import com.vaadin.flow.component.icon.VaadinIcon.CIRCLE_THIN
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.renderer.TemplateRenderer
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.claspina.confirmdialog.ConfirmDialog
import org.vaadin.crudui.crud.CrudOperation
import org.vaadin.crudui.crud.CrudOperation.ADD
import org.vaadin.crudui.crud.CrudOperation.DELETE
import org.vaadin.crudui.crud.CrudOperation.READ
import org.vaadin.crudui.crud.CrudOperation.UPDATE
import org.vaadin.crudui.crud.impl.GridCrud
import org.vaadin.crudui.form.AbstractCrudFormFactory
import org.vaadin.gatanaso.MultiselectComboBox

@Route(layout = PedidoMesaCreditoLayout::class)
@PageTitle(TITLE)
class UsuarioView: ViewLayout<UsuarioViewModel>(), IUsuarioView {
  override val viewModel = UsuarioViewModel(this)
  
  override fun isAccept() = AppConfig.userSaci?.roles()
    ?.contains("ADMIN") == true
  
  init {
    form("Editor de usuários")
    setSizeFull()
    val crud: GridCrud<UserSaci> = gridCrud()
    // layout configuration
    setSizeFull()
    this.add(crud)
    // logic configuration
    setOperation(crud)
  }
  
  private fun gridCrud(): GridCrud<UserSaci> {
    val crud: GridCrud<UserSaci> = GridCrud(UserSaci::class.java)
    crud.grid
      .setColumns(UserSaci::no.name, UserSaci::login.name, UserSaci::funcionario.name ,UserSaci::storeno.name, UserSaci::name.name)
    crud.grid.getColumnBy(UserSaci::storeno)
      .setHeader("Loja")
    
    crud.grid.addThemeVariants(LUMO_COMPACT, LUMO_ROW_STRIPES, LUMO_COLUMN_BORDERS)
    
    crud.crudFormFactory = UserCrudFormFactory(viewModel)
    crud.setSizeFull()
    return crud
  }
  
  private fun setOperation(crud: GridCrud<UserSaci>) {
    crud.setOperations(
      {viewModel.findAll()},
      {user: UserSaci -> viewModel.add(user)},
      {user: UserSaci? -> viewModel.update(user)},
      {user: UserSaci? -> viewModel.delete(user)})
  }
  
  private fun Grid<UserSaci>.addColumnBool(caption: String, value: UserSaci.() -> Boolean) {
    val column = this.addComponentColumn {bean ->
      if(bean.value()) CHECK_CIRCLE_O.create()
      else CIRCLE_THIN.create()
    }
    column.setHeader(caption)
    column.textAlign = ColumnTextAlign.CENTER
  }
}

class UserCrudFormFactory(private val viewModel: UsuarioViewModel): AbstractCrudFormFactory<UserSaci>() {
  private lateinit var comboAbreviacao: MultiselectComboBox<String>
  
  override fun buildNewForm(operation: CrudOperation?,
                            domainObject: UserSaci?,
                            readOnly: Boolean,
                            cancelButtonClickListener: ComponentEventListener<ClickEvent<Button>>?,
                            operationButtonClickListener: ComponentEventListener<ClickEvent<Button>>?): Component {
    val binder = Binder<UserSaci>(UserSaci::class.java)
    return VerticalLayout().apply {
      isSpacing = false
      isMargin = false
      formLayout {
        if(operation in listOf(READ, DELETE, UPDATE))
          integerField("Número") {
            isReadOnly = true
            binder.bind(this, UserSaci::no.name)
          }
        if(operation in listOf(ADD, READ, DELETE, UPDATE))
          comboBox<UserSaci>("Login") {
            val allUser = viewModel.findAllUser()
            val filter: ItemFilter<UserSaci> =
              ItemFilter {user: UserSaci, filterString: String ->
                user.login
                  .contains(filterString, ignoreCase = true)
                || user.name
                  .contains(filterString, ignoreCase = true)
                || user.no == filterString.toIntOrNull() ?: 0
              }
            this.setItems(filter, allUser)
            this.setItemLabelGenerator(UserSaci::login)
            this.setRenderer(TemplateRenderer.of<UserSaci>("<div>[[item.login]]<br><small>[[item.nome]]</small></div>")
                               .withProperty("login") {
                                 it.login
                               }
                               .withProperty("nome") {user ->
                                 "${user.no} - ${user.name}"
                               })
            binder.bind(this, {bean ->
              bean
            }, {bean, field ->
                          bean.no = field.no
                          bean.name = field.name
                          bean.login = field.login
                        })
          }
        if(operation in listOf(READ, DELETE, UPDATE))
          textField("Nome") {
            isReadOnly = true
            binder.bind(this, UserSaci::name.name)
          }
        if(operation in listOf(ADD, READ, DELETE, UPDATE)) {
          textField("Funcionario"){
            isReadOnly = false
            binder.bind(this, UserSaci::funcionario.name)
          }
          integerField("Loja") {
            isReadOnly = false
            binder.bind(this, UserSaci::storeno.name)
          }
        }
      }
      hr()
      horizontalLayout {
        this.setWidthFull()
        this.justifyContentMode = JustifyContentMode.END
        button("Confirmar") {
          alignSelf = END
          addThemeVariants(LUMO_PRIMARY)
          addClickListener {
            binder.writeBean(domainObject)
            operationButtonClickListener?.onComponentEvent(it)
          }
        }
        button("Cancelar") {
          alignSelf = END
          addThemeVariants(LUMO_ERROR)
          addClickListener(cancelButtonClickListener)
        }
      }
      
      binder.readBean(domainObject)
    }
  }
  
  override fun buildCaption(operation: CrudOperation?, domainObject: UserSaci?): String {
    return operation?.let {crudOperation ->
      when(crudOperation) {
        READ   -> "Consulta"
        ADD    -> "Adiciona"
        UPDATE -> "Atualiza"
        DELETE -> "Remove"
      }
    } ?: "Erro"
  }
  
  override fun showError(operation: CrudOperation?, e: Exception?) {
    ConfirmDialog.createError()
      .withCaption("Erro do aplicativo")
      .withMessage(e?.message ?: "Erro desconhecido")
      .open()
  }
  
  companion object {
    const val TITLE = "Usuário"
  }
}