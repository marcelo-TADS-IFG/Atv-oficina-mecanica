package oficina.views.cliente;

import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import oficina.controllers.ClienteController;
import oficina.models.Cliente;
import oficina.views.MainLayout;

@PageTitle("Cliente")
@Route(value = "cliente", layout = MainLayout.class)
public class ClienteView extends Composite<VerticalLayout> {

    private ClienteController clienteController;
    private Grid<Cliente> gridConsulta;

    public ClienteView() {
        clienteController = new ClienteController();

        // Create tabs
        Tab tabGerenciar = new Tab("Gerenciar Cliente");
        Tab tabConsultar = new Tab("Consultar Cliente");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        // Create layout for managing client
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3("Gerenciar Cliente");
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldNome = new TextField("Nome");
        TextField textFieldEndereco = new TextField("Endereco");
        EmailField emailFieldCpf = new EmailField("CPF");
        TextField textFieldCidade = new TextField("Cidade");
        TextField textFieldTelefone = new TextField("Telefone");
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonSalvar = new Button("Salvar");
        Button buttonAlterar = new Button("Alterar");
        Button buttonExcluir = new Button("Excluir");
        Button buttonPesquisar = new Button("Pesquisar");

        // Configure components
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        formLayout2Col.setWidth("100%");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");

        // Configure buttons
        buttonSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        buttonExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonPesquisar.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        // Add components to layout
        getContent().add(tabs, layoutColumn2);
        layoutColumn2.add(h3, formLayout2Col, layoutRow);
        formLayout2Col.add(textFieldID, textFieldNome, textFieldEndereco, emailFieldCpf, textFieldCidade,
                textFieldTelefone);
        layoutRow.add(buttonSalvar, buttonAlterar, buttonExcluir, buttonPesquisar);

        // Create layout for consulting client
        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Cliente");
        gridConsulta = new Grid<>(Cliente.class);
        layoutConsulta.add(h3Consulta, gridConsulta);
        layoutConsulta.setVisible(false);

        // Add consulting layout to main content
        getContent().add(layoutConsulta);

        // Tabs selection change event
        tabs.addSelectedChangeListener(event -> {
            boolean isGerenciarTabSelected = tabs.getSelectedTab() == tabGerenciar;
            layoutColumn2.setVisible(isGerenciarTabSelected);
            layoutConsulta.setVisible(!isGerenciarTabSelected);
            if (!isGerenciarTabSelected) {
                atualizarGridConsulta();
            }
        });

        // Button click events
        buttonSalvar.addClickListener(event -> salvarCliente(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));
        buttonAlterar.addClickListener(event -> alterarCliente(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));
        buttonExcluir.addClickListener(event -> excluirCliente(textFieldID, textFieldNome, textFieldEndereco,
                emailFieldCpf, textFieldCidade, textFieldTelefone));
        buttonPesquisar.addClickListener(event -> pesquisarCliente(textFieldNome));

        // Double-click event on grid
        gridConsulta.addItemDoubleClickListener(event -> {
            Cliente cliente = event.getItem();
            if (cliente != null) {
                textFieldID.setValue(String.valueOf(cliente.getId()));
                textFieldNome.setValue(cliente.getNome());
                textFieldEndereco.setValue(cliente.getEndereco());
                emailFieldCpf.setValue(cliente.getCpf());
                textFieldCidade.setValue(cliente.getCidade());
                textFieldTelefone.setValue(cliente.getTelefone());
                tabs.setSelectedTab(tabGerenciar);
                Notification.show("Cliente selecionado.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
    }

    private void salvarCliente(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
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
                int id = Integer.parseInt(textFieldID.getValue().trim());

                // Criar o objeto Cliente
                Cliente cliente = new Cliente(id, nome, endereco, cpf, cidade, telefone);

                // Adicionar o cliente usando o controlador
                if (clienteController.adicionarCliente(cliente)) {
                    Notification.show("Cliente salvo com sucesso.", 3000, Notification.Position.MIDDLE)
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
                    Notification.show("Erro ao salvar o cliente.", 3000, Notification.Position.MIDDLE)
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
            Notification.show("Erro inesperado ao salvar o cliente.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarCliente(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
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
                        "Tem certeza que deseja alterar o cliente?",
                        "Confirmar",
                        eventConfirm -> {
                            Cliente cliente = new Cliente(id, nome, endereco, cpf, cidade, telefone);

                            if (clienteController.atualizarCliente(cliente)) {
                                Notification.show("Cliente alterado com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldNome.clear();
                                textFieldEndereco.clear();
                                emailFieldCpf.clear();
                                textFieldCidade.clear();
                                textFieldTelefone.clear();
                                textFieldID.focus();
                            } else {
                                Notification.show("Erro ao alterar o cliente.", 3000, Notification.Position.MIDDLE)
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

    private void excluirCliente(TextField textFieldID, TextField textFieldNome, TextField textFieldEndereco,
            EmailField emailFieldCpf, TextField textFieldCidade, TextField textFieldTelefone) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            int id;
            try {
                id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Exclusão",
                        "Tem certeza que deseja excluir o cliente?",
                        "Confirmar",
                        eventConfirm -> {
                            if (clienteController.deletarCliente(id)) {
                                Notification.show("Cliente excluído com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldNome.clear();
                                textFieldEndereco.clear();
                                emailFieldCpf.clear();
                                textFieldCidade.clear();
                                textFieldTelefone.clear();
                                textFieldID.focus();
                                atualizarGridConsulta();
                            } else {
                                Notification.show("Cliente não encontrado.", 3000, Notification.Position.MIDDLE)
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
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Preencha o campo ID.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void pesquisarCliente(TextField textFieldNome) {
        List<Cliente> clientes = clienteController.buscarClientesPorNome(textFieldNome.getValue());
        gridConsulta.setItems(clientes);
    }

    private void atualizarGridConsulta() {
        List<Cliente> clientes = clienteController.buscarTodosClientes();
        gridConsulta.setItems(clientes);
    }
}
