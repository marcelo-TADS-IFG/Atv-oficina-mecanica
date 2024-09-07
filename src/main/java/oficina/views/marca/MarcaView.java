package oficina.views.marca;

import java.util.Collections;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import oficina.controllers.MarcaController;
import oficina.models.Marca;
import oficina.views.MainLayout;

@PageTitle("Marca")
@Route(value = "marca", layout = MainLayout.class)
public class MarcaView extends Composite<VerticalLayout> {

    private MarcaController marcaController;
    private Grid<Marca> gridConsulta;

    public MarcaView() {
        marcaController = new MarcaController();

        Tab tabGerenciar = new Tab("Gerenciar Marca");
        Tab tabConsultar = new Tab("Consultar Marcas");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldNome = new TextField("Nome");
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonSalvar = new Button("Salvar");
        Button buttonAlterar = new Button("Alterar");
        Button buttonExcluir = new Button("Excluir");
        Button buttonPesquisar = new Button("Pesquisar");

        // Configuração inicial
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Gerenciar Marca");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        textFieldNome.setWidth("56%");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");

        // Configuração dos botões
        buttonSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        buttonExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Adiciona os componentes ao layout
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3, formLayout2Col, layoutRow);
        formLayout2Col.add(textFieldID, textFieldNome);
        layoutRow.add(buttonSalvar, buttonAlterar, buttonExcluir, buttonPesquisar);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Marca");
        gridConsulta = new Grid<>(Marca.class);
        layoutConsulta.add(h3Consulta, gridConsulta);
        layoutConsulta.setVisible(false);

        getContent().add(tabs, layoutColumn2, layoutConsulta);

        // Alterna entre as abas
        tabs.addSelectedChangeListener(event -> {
            boolean isGerenciarTabSelected = tabs.getSelectedTab() == tabGerenciar;
            layoutColumn2.setVisible(isGerenciarTabSelected);
            layoutConsulta.setVisible(!isGerenciarTabSelected);
            if (!isGerenciarTabSelected) {
                atualizarGridConsulta();
            }
        });

        // Ações dos botões
        buttonSalvar.addClickListener(event -> salvarMarca(textFieldID, textFieldNome));
        buttonAlterar.addClickListener(event -> alterarMarca(textFieldID, textFieldNome));
        buttonExcluir.addClickListener(event -> excluirMarca(textFieldID, textFieldNome));
        buttonPesquisar.addClickListener(event -> pesquisarMarca(textFieldID, textFieldNome));

        // Configura o clique duplo no grid
        gridConsulta.addItemDoubleClickListener(event -> {
            Marca marca = event.getItem();
            if (marca != null) {
                textFieldID.setValue(String.valueOf(marca.getId()));
                textFieldNome.setValue(marca.getNome_marca());
                tabs.setSelectedTab(tabGerenciar);
                Notification.show("Marca selecionada.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

    }

    private void salvarMarca(TextField textFieldID, TextField textFieldNome) {
        try {
            String nome = textFieldNome.getValue().trim();

            if (!nome.isEmpty()) {
                if (marcaController.salvarMarca(nome)) {
                    Notification.show("Marca salva com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldID.clear();
                    textFieldNome.clear();
                    textFieldID.focus();
                    atualizarGridConsulta();
                } else {
                    Notification.show("Erro ao salvar a marca.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Nome não pode ser vazio.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            Notification.show("Erro inesperado ao salvar a marca.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarMarca(TextField textFieldID, TextField textFieldNome) {
        String idText = textFieldID.getValue();
        String nome = textFieldNome.getValue().trim();

        if (!idText.isEmpty() && !nome.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Alteração",
                        "Tem certeza que deseja alterar a marca?",
                        "Confirmar",
                        eventConfirm -> {
                            if (marcaController.alterarMarca(id, nome)) {
                                Notification.show("Marca alterada com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldNome.clear();
                                textFieldID.focus();
                                atualizarGridConsulta();
                            } else {
                                Notification.show("Erro ao alterar a marca.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            }
                        },
                        "Cancelar",
                        eventCancel -> {
                            Notification.show("Alteração cancelada.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        });

                dialog.open();
            } catch (NumberFormatException e) {
                Notification.show("ID deve ser um número inteiro.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Preencha todos os campos corretamente.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void excluirMarca(TextField textFieldID, TextField textFieldNome) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            int id = Integer.parseInt(idText);

            ConfirmDialog dialog = new ConfirmDialog(
                    "Confirmar Exclusão",
                    "Tem certeza que deseja excluir a marca?",
                    "Confirmar",
                    eventConfirm -> {
                        if (marcaController.deletarMarca(id)) {
                            Notification.show("Marca excluída com sucesso.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            textFieldID.clear();
                            textFieldNome.clear();
                            textFieldID.focus(); // Foco volta para o campo ID
                            atualizarGridConsulta();
                        } else {
                            Notification.show("Marca não encontrada.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    },
                    "Cancelar",
                    eventCancel -> {
                        Notification.show("Exclusão cancelada.", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                    });

            dialog.open();
        } else {
            Notification.show("Preencha o campo ID.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void pesquisarMarca(TextField textFieldID, TextField textFieldNomeMarca) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                Marca marca = marcaController.buscarMarcaPorId(id);

                if (marca != null) {
                    textFieldNomeMarca.setValue(marca.getNome_marca());
                    Notification.show("Marca encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldNomeMarca.focus();
                } else {
                    // Limpar todos os campos caso a marca não seja encontrada
                    textFieldNomeMarca.clear();
                    textFieldID.focus();
                    Notification.show("Marca não encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (NumberFormatException e) {
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisaMarca(textFieldID, textFieldNomeMarca);
        }
    }

    private void abrirDialogoDePesquisaMarca(TextField textFieldID, TextField textFieldNomeMarca) {

        Dialog dialog = new Dialog();
        dialog.setWidth("600px"); // Ajuste conforme necessário

        // Campo de busca
        TextField searchField = new TextField("Buscar Marca");
        searchField.setPlaceholder("Digite o nome da marca");
        searchField.setWidthFull();

        // Grid para exibir as marcas
        Grid<Marca> grid = new Grid<>(Marca.class, false);
        grid.addColumn(Marca::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Marca::getNome_marca).setHeader("Nome da Marca").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Definir altura do grid manualmente
        grid.setHeight("300px"); // Ajuste conforme necessário

        // Listener para buscar e exibir as marcas conforme o usuário digita
        searchField.addValueChangeListener(event -> {
            String nomeMarca = event.getValue().trim().toLowerCase();
            if (!nomeMarca.isEmpty()) {
                List<Marca> marcasFiltradas = marcaController.buscarMarcasPorNome(nomeMarca);
                grid.setItems(marcasFiltradas);
            } else {
                grid.setItems(); // Limpa o grid se o campo de busca estiver vazio
            }
        });

        // Evento de duplo clique para selecionar uma marca
        grid.addItemDoubleClickListener(event -> {
            Marca marcaSelecionada = event.getItem();
            if (marcaSelecionada != null) {
                textFieldID.setValue(String.valueOf(marcaSelecionada.getId()));
                textFieldNomeMarca.setValue(marcaSelecionada.getNome_marca());
                Notification.show("Marca selecionada: " + marcaSelecionada.getNome_marca(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close(); // Fecha o diálogo após a seleção
            }
        });

        // Botões de ação
        Button confirmarButton = new Button("Confirmar", e -> {
            Marca marcaSelecionada = grid.asSingleSelect().getValue();
            if (marcaSelecionada != null) {
                textFieldID.setValue(String.valueOf(marcaSelecionada.getId()));
                textFieldNomeMarca.setValue(marcaSelecionada.getNome_marca());
                Notification.show("Marca selecionada: " + marcaSelecionada.getNome_marca(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close(); // Fecha o diálogo após a confirmação
            } else {
                Notification.show("Nenhuma marca selecionada.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelarButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(confirmarButton, cancelarButton);

        VerticalLayout layout = new VerticalLayout(searchField, grid, actions);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setPadding(true);

        // Atribuindo flexGrow para que o Grid ocupe o espaço restante
        layout.setFlexGrow(1, grid);

        dialog.add(layout);
        dialog.open();
    }

    private void atualizarGridConsulta() {
        List<Marca> marcas = marcaController.listarTodasMarcas();
        if (marcas == null || marcas.isEmpty()) {
            gridConsulta.setItems(Collections.emptyList());
        } else {
            gridConsulta.setItems(marcas);
        }
    }
}
