package oficina.views.mecanico;

import oficina.controllers.MecanicoController;
import oficina.models.Mecanico;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
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
        EmailField emailFieldCpf = new EmailField("CPF");
        TextField textFieldCidade = new TextField("Cidade");
        TextField textFieldTelefone = new TextField("Telefone");
        formLayout2Col.setWidth("100%");
        formLayout2Col.add(textFieldID, textFieldNome, textFieldEndereco, emailFieldCpf, textFieldCidade,
                textFieldTelefone);

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
                emailFieldCpf.setValue(mecanico.getCpf());
                textFieldCidade.setValue(mecanico.getCidade());
                textFieldTelefone.setValue(mecanico.getTelefone());
                tabs.setSelectedTab(tabGerenciar); // Alterna para a aba "Gerenciar Mecânicos"
            }
        });

        btnSalvar.addClickListener(event -> salvarMecanico(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));

        btnAlterar.addClickListener(event -> alterarMecanico(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));

        btnExcluir.addClickListener(event -> excluirMecanico(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));

        btnPesquisar.addClickListener(event -> pesquisarMecanico(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));

    }

    private void salvarMecanico(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {
        try {
            // Obter e limpar os valores dos campos
            String nome = textFieldNome.getValue().trim();
            String endereco = textFieldEndereco.getValue().trim();
            String cpf = emailFieldCpf.getValue().trim();
            String cidade = textFieldCidade.getValue().trim();
            String telefone = textFieldTelefone.getValue().trim();

            // Verificar se os campos obrigatórios não estão vazios
            if (!nome.isEmpty() && !endereco.isEmpty() && !cpf.isEmpty() && !cidade.isEmpty() && !telefone.isEmpty()) {
                // Converter ID, assumindo que é um campo numérico
                // int id = Integer.parseInt(textFieldID.getValue().trim());

                // Criar o objeto Cliente
                Mecanico mecanico = new Mecanico(0, nome, endereco, cpf, cidade, telefone);

                // Adicionar o cliente usando o controlador
                if (mecanicoController.adicionarMecanico(mecanico)) {
                    Notification.show("Mecânico salvo com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    // Limpar os campos
                    textFieldID.clear();
                    textFieldNome.clear();
                    textFieldEndereco.clear();
                    emailFieldCpf.clear();
                    textFieldCidade.clear();
                    textFieldTelefone.clear();
                    textFieldID.focus();
                } else {
                    Notification.show("Erro ao salvar o mecânico.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Todos os campos devem ser preenchidos.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (NumberFormatException e) {
            Notification.show("ID deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Erro inesperado ao salvar o mecânico.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarMecanico(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {
        String idText = textFieldID.getValue();
        String nome = textFieldNome.getValue().trim();
        String endereco = textFieldEndereco.getValue().trim();
        String cpf = emailFieldCpf.getValue().trim();
        String cidade = textFieldCidade.getValue().trim();
        String telefone = textFieldTelefone.getValue().trim();

        if (!nome.isEmpty() && !endereco.isEmpty() && !cpf.isEmpty() && !cidade.isEmpty()
                && !telefone.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Alteração",
                        "Tem certeza que deseja alterar o mecânico?",
                        "Confirmar",
                        eventConfirm -> {
                            Mecanico mecanico = new Mecanico(id, nome, endereco, cpf, cidade, telefone);

                            if (mecanicoController.atualizarMecanico(mecanico)) {
                                Notification.show("Mecânico alterado com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldNome.clear();
                                textFieldEndereco.clear();
                                emailFieldCpf.clear();
                                textFieldCidade.clear();
                                textFieldTelefone.clear();
                                textFieldID.focus();
                            } else {
                                Notification.show("Erro ao alterar o mecânico.", 3000, Notification.Position.MIDDLE)
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
                Notification.show("ID deve ser um número inteiro válido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Preencha todos os campos corretamente.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void excluirMecanico(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Exclusão",
                        "Tem certeza que deseja excluir o mecânico?",
                        "Confirmar",
                        eventConfirm -> {
                            if (mecanicoController.deletarMecanico(id)) {
                                Notification.show("Mecânico excluído com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldNome.clear();
                                textFieldEndereco.clear();
                                emailFieldCpf.clear();
                                textFieldCidade.clear();
                                textFieldTelefone.clear();
                                textFieldID.focus();
                            } else {
                                Notification.show("Erro ao excluir o mecânico.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                            }
                        },
                        "Cancelar",
                        eventCancel -> {
                            Notification.show("Exclusão cancelada.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                        });

                dialog.open();
            } catch (NumberFormatException e) {
                Notification.show("ID deve ser um número inteiro válido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification
                    .show("O campo ID deve ser preenchido para excluir um mecânico.", 3000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void pesquisarMecanico(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                Mecanico mecanico = mecanicoController.buscarMecanicoPorId(id);

                if (mecanico != null) {
                    textFieldNome.setValue(mecanico.getNome());
                    textFieldEndereco.setValue(mecanico.getEndereco());
                    emailFieldCpf.setValue(mecanico.getCpf());
                    textFieldCidade.setValue(mecanico.getCidade());
                    textFieldTelefone.setValue(mecanico.getTelefone());
                    Notification.show("Mecânico encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldNome.focus();
                } else {
                    // Limpar todos os campos caso o mecânico não seja encontrado
                    textFieldNome.clear();
                    textFieldEndereco.clear();
                    emailFieldCpf.clear();
                    textFieldCidade.clear();
                    textFieldTelefone.clear();
                    textFieldID.focus();
                    Notification.show("Mecânico não encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (NumberFormatException e) {
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisaMecanico(textFieldID, textFieldNome, textFieldEndereco, emailFieldCpf,
                    textFieldCidade, textFieldTelefone);
        }
    }

    private void abrirDialogoDePesquisaMecanico(TextField textFieldID, TextField textFieldNome,
            TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {

        Dialog dialog = new Dialog();
        dialog.setWidth("800px"); // Aumentei a largura para acomodar mais colunas

        // Campo de busca
        TextField searchField = new TextField("Buscar Mecânico");
        searchField.setPlaceholder("Digite o nome do mecânico");
        searchField.setWidthFull();

        // Grid para exibir os mecânicos
        Grid<Mecanico> grid = new Grid<>(Mecanico.class, false);
        grid.addColumn(Mecanico::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Mecanico::getNome).setHeader("Nome").setAutoWidth(true);
        grid.addColumn(Mecanico::getEndereco).setHeader("Endereço").setAutoWidth(true);
        grid.addColumn(Mecanico::getCpf).setHeader("CPF").setAutoWidth(true);
        grid.addColumn(Mecanico::getCidade).setHeader("Cidade").setAutoWidth(true);
        grid.addColumn(Mecanico::getTelefone).setHeader("Telefone").setAutoWidth(true);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Definir altura do grid manualmente
        grid.setHeight("400px"); // Aumentei a altura para acomodar mais linhas

        // Listener para buscar e exibir os mecânicos conforme o usuário digita
        searchField.addValueChangeListener(event -> {
            String nomeMecanico = event.getValue().trim().toLowerCase();
            if (!nomeMecanico.isEmpty()) {
                List<Mecanico> mecanicosFiltrados = mecanicoController.buscarMecanicosPorNome(nomeMecanico);
                grid.setItems(mecanicosFiltrados);
            } else {
                grid.setItems(); // Limpa o grid se o campo de busca estiver vazio
            }
        });

        // Evento de duplo clique para selecionar um mecânico
        grid.addItemDoubleClickListener(event -> {
            Mecanico mecanicoSelecionado = event.getItem();
            if (mecanicoSelecionado != null) {
                textFieldID.setValue(String.valueOf(mecanicoSelecionado.getId()));
                textFieldNome.setValue(mecanicoSelecionado.getNome());
                textFieldEndereco.setValue(mecanicoSelecionado.getEndereco());
                emailFieldCpf.setValue(mecanicoSelecionado.getCpf());
                textFieldCidade.setValue(mecanicoSelecionado.getCidade());
                textFieldTelefone.setValue(mecanicoSelecionado.getTelefone());
                Notification.show("Mecânico selecionado: " + mecanicoSelecionado.getNome(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close(); // Fecha o diálogo após a seleção
            }
        });

        // Botões de ação
        Button confirmarButton = new Button("Confirmar", e -> {
            Mecanico mecanicoSelecionado = grid.asSingleSelect().getValue();
            if (mecanicoSelecionado != null) {
                textFieldID.setValue(String.valueOf(mecanicoSelecionado.getId()));
                textFieldNome.setValue(mecanicoSelecionado.getNome());
                textFieldEndereco.setValue(mecanicoSelecionado.getEndereco());
                emailFieldCpf.setValue(mecanicoSelecionado.getCpf());
                textFieldCidade.setValue(mecanicoSelecionado.getCidade());
                textFieldTelefone.setValue(mecanicoSelecionado.getTelefone());
                Notification.show("Mecânico selecionado: " + mecanicoSelecionado.getNome(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close(); // Fecha o diálogo após a confirmação
            } else {
                Notification.show("Nenhum mecânico selecionado.", 3000, Notification.Position.MIDDLE)
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
