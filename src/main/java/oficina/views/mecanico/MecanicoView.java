package oficina.views.mecanico;

import oficina.controllers.MecanicoController;
import oficina.models.Mecanico;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import oficina.views.MainLayout;

import java.util.List;

@PageTitle("Mecânicos")
@Route(value = "mecanicos", layout = MainLayout.class)
public class MecanicoView extends Composite<VerticalLayout> {

    private MecanicoController mecanicoController;
    private Grid<Mecanico> gridConsulta;

    public MecanicoView() {
        mecanicoController = new MecanicoController();

        Tab tabGerenciar = new Tab("Gerenciar Mecânico");
        Tab tabConsultar = new Tab("Consultar Mecânicos");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3("Gerenciar Mecânico");
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldNome = new TextField("Nome");
        TextField textFieldEndereco = new TextField("Endereço");
        TextField textFieldCPF = new TextField("CPF");
        TextField textFieldCidade = new TextField("Cidade");
        TextField textFieldTelefone = new TextField("Telefone");
        formLayout2Col.setWidth("100%");
        formLayout2Col.add(textFieldID, textFieldNome, textFieldEndereco, textFieldCPF, textFieldCidade, textFieldTelefone);

        // Adicionando os botões
        Button btnSalvar = new Button("Salvar");
        btnSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnAlterar = new Button("Alterar");
        btnAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button btnExcluir = new Button("Excluir");
        btnExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnPesquisar = new Button("Pesquisar");

        HorizontalLayout buttonLayout = new HorizontalLayout(btnSalvar, btnAlterar, btnExcluir, btnPesquisar);

        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");

        layoutColumn2.add(h3, formLayout2Col, buttonLayout);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Mecânicos");
        gridConsulta = new Grid<>(Mecanico.class);
        layoutConsulta.add(h3Consulta, gridConsulta);
        layoutConsulta.setVisible(false);

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        getContent().add(tabs, layoutColumn2, layoutConsulta);

        tabs.addSelectedChangeListener(event -> {
            boolean isGerenciarTabSelected = tabs.getSelectedTab() == tabGerenciar;
            layoutColumn2.setVisible(isGerenciarTabSelected);
            layoutConsulta.setVisible(!isGerenciarTabSelected);
            if (!isGerenciarTabSelected) {
                atualizarGridConsulta();
            }
        });

        gridConsulta.addItemDoubleClickListener(event -> {
            Mecanico mecanico = event.getItem();
            if (mecanico != null) {
                textFieldID.setValue(String.valueOf(mecanico.getId()));
                textFieldNome.setValue(mecanico.getNome());
                textFieldEndereco.setValue(mecanico.getEndereco());
                textFieldCPF.setValue(mecanico.getCpf());
                textFieldCidade.setValue(mecanico.getCidade());
                textFieldTelefone.setValue(mecanico.getTelefone());
                tabs.setSelectedTab(tabGerenciar); // Alterna para a aba "Gerenciar Mecânicos"
            }
        });
       
    }

    private void atualizarGridConsulta() {
        List<Mecanico> mecanicos = mecanicoController.buscarTodosMecanicos();
        gridConsulta.setItems(mecanicos);
        gridConsulta.removeAllColumns();
        gridConsulta.addColumn(Mecanico::getId).setHeader("ID");
        gridConsulta.addColumn(Mecanico::getNome).setHeader("Nome");
        gridConsulta.addColumn(Mecanico::getEndereco).setHeader("Endereço");
        gridConsulta.addColumn(Mecanico::getCpf).setHeader("CPF");
        gridConsulta.addColumn(Mecanico::getCidade).setHeader("Cidade");
        gridConsulta.addColumn(Mecanico::getTelefone).setHeader("Telefone");
    }

    
}
