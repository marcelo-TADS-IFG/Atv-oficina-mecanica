package oficina.views.veiculo;

import oficina.controllers.ClienteController;
import oficina.controllers.VeiculoController;
import oficina.models.Cliente;
import oficina.models.Veiculo;
import oficina.views.MainLayout;

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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.List;

@PageTitle("Veículo")
@Route(value = "veiculos", layout = MainLayout.class)
public class VeiculoView extends Composite<VerticalLayout> {

    private VeiculoController veiculoController;
    private ClienteController clienteController;
    private Grid<Veiculo> gridConsulta;
    private ComboBox<Cliente> comboBoxCliente;

    public VeiculoView() {
        veiculoController = new VeiculoController();
        clienteController = new ClienteController();

        Tab tabGerenciar = new Tab("Gerenciar Veículo");
        Tab tabConsultar = new Tab("Consultar Veículos");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutGerenciar = new VerticalLayout();
        H3 h3Gerenciar = new H3("Gerenciar Veículos");
        FormLayout formLayout = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldDescricao = new TextField("Descrição");
        TextField textFieldPlaca = new TextField("Placa");
        TextField textFieldAnoModelo = new TextField("Ano Modelo");
        comboBoxCliente = new ComboBox<>("Cliente");
        comboBoxCliente.setWidth("min-content");
        setComboBoxClientes(comboBoxCliente);
        HorizontalLayout layoutBotoes = new HorizontalLayout();

        Button buttonSalvar = new Button("Salvar",
                event -> salvarVeiculo(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo,
                        comboBoxCliente));
        buttonSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button buttonAlterar = new Button("Alterar",
                event -> alterarVeiculo(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo,
                        comboBoxCliente));
        buttonAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button buttonExcluir = new Button("Excluir",
                event -> excluirVeiculo(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo,
                        comboBoxCliente));
        buttonExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button buttonPesquisar = new Button("Pesquisar",
                event -> pesquisarVeiculo(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo,
                        comboBoxCliente));

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        layoutGerenciar.setWidth("100%");
        layoutGerenciar.setMaxWidth("800px");
        layoutGerenciar.setHeight("min-content");

        formLayout.setWidth("100%");
        formLayout.add(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo, comboBoxCliente);

        layoutBotoes.addClassName(Gap.MEDIUM);
        layoutBotoes.setWidth("100%");
        layoutBotoes.getStyle().set("flex-grow", "1");
        layoutBotoes.add(buttonSalvar, buttonAlterar, buttonExcluir, buttonPesquisar);

        layoutGerenciar.add(h3Gerenciar, formLayout, layoutBotoes);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Veículos");
        gridConsulta = new Grid<>(Veiculo.class);
        layoutConsulta.add(h3Consulta, gridConsulta);
        layoutConsulta.setVisible(false);

        getContent().add(tabs, layoutGerenciar, layoutConsulta);

        tabs.addSelectedChangeListener(event -> {
            boolean isGerenciarTabSelected = tabs.getSelectedTab() == tabGerenciar;
            layoutGerenciar.setVisible(isGerenciarTabSelected);
            layoutConsulta.setVisible(!isGerenciarTabSelected);
            if (!isGerenciarTabSelected) {
                atualizarGridConsulta();
            }
        });

        gridConsulta.addItemDoubleClickListener(event -> {
            Veiculo veiculo = event.getItem();
            if (veiculo != null) {
                textFieldID.setValue(String.valueOf(veiculo.getId()));
                textFieldDescricao.setValue(veiculo.getDescricao_veiculo());
                textFieldPlaca.setValue(veiculo.getPlaca());
                textFieldAnoModelo.setValue(veiculo.getAno_modelo());
                comboBoxCliente.setValue(veiculo.getCliente());
                tabs.setSelectedTab(tabGerenciar); // Alterna para a aba "Gerenciar Veículo"
                Notification.show("Veículo Selecionado.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
    }

    private void setComboBoxClientes(ComboBox<Cliente> comboBox) {
        List<Cliente> clientes = clienteController.buscarTodosClientes();
        comboBox.setItems(clientes);
        comboBox.setItemLabelGenerator(Cliente::getNome);
    }

    private void salvarVeiculo(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPlaca,
            TextField textFieldAnoModelo, ComboBox<Cliente> comboBoxCliente) {
        try {
            // Obter e limpar os valores dos campos
            String descricao = textFieldDescricao.getValue().trim();
            String placa = textFieldPlaca.getValue().trim();
            String anoModelo = textFieldAnoModelo.getValue().trim();
            Cliente cliente = comboBoxCliente.getValue();

            // Verificar se os campos obrigatórios não estão vazios
            if (!descricao.isEmpty() && !placa.isEmpty() && !anoModelo.isEmpty() && cliente != null) {
                // Converter ID, assumindo que é um campo numérico
                int id = 0;
                if (!textFieldID.getValue().trim().isEmpty()) {
                    id = Integer.parseInt(textFieldID.getValue().trim());
                }

                // Criar o objeto Veiculo e definir seus atributos
                Veiculo veiculo = new Veiculo();
                veiculo.setId(id);
                veiculo.setDescricao_veiculo(descricao);
                veiculo.setPlaca(placa);
                veiculo.setAno_modelo(anoModelo);
                veiculo.setCliente(cliente); // Associar o cliente ao veículo

                // Adicionar o veículo usando o controlador
                if (veiculoController.adicionarVeiculo(veiculo)) {
                    Notification.show("Veículo salvo com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    // Limpar os campos
                    textFieldID.clear();
                    textFieldDescricao.clear();
                    textFieldPlaca.clear();
                    textFieldAnoModelo.clear();
                    comboBoxCliente.clear();
                    textFieldID.focus();
                } else {
                    Notification.show("Erro ao salvar o veículo.", 3000, Notification.Position.MIDDLE)
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
            Notification.show("Erro inesperado ao salvar o veículo.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarVeiculo(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPlaca,
                            TextField textFieldAnoModelo, ComboBox<Cliente> comboBoxCliente) {
    String idText = textFieldID.getValue();
    String descricao = textFieldDescricao.getValue().trim();
    String placa = textFieldPlaca.getValue().trim();
    String anoModelo = textFieldAnoModelo.getValue().trim();
    Cliente cliente = comboBoxCliente.getValue();

    if (!descricao.isEmpty() && !placa.isEmpty() && !anoModelo.isEmpty() && cliente != null) {
        try {
            int id = Integer.parseInt(idText);  // Converter ID para inteiro

            ConfirmDialog dialog = new ConfirmDialog(
                    "Confirmar Alteração",
                    "Tem certeza que deseja alterar o veículo?",
                    "Confirmar",
                    eventConfirm -> {
                        // Criar o objeto Veiculo e definir seus atributos
                        Veiculo veiculo = new Veiculo();
                        veiculo.setId(id);
                        veiculo.setDescricao_veiculo(descricao);
                        veiculo.setPlaca(placa);
                        veiculo.setAno_modelo(anoModelo);
                        veiculo.setCliente(cliente); // Associar o cliente ao veículo

                        // Atualizar o veículo usando o controlador
                        if (veiculoController.atualizarVeiculo(veiculo)) {
                            Notification.show("Veículo alterado com sucesso.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            // Limpar os campos
                            textFieldID.clear();
                            textFieldDescricao.clear();
                            textFieldPlaca.clear();
                            textFieldAnoModelo.clear();
                            comboBoxCliente.clear();
                            textFieldID.focus();
                        } else {
                            Notification.show("Erro ao alterar o veículo.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    },
                    "Cancelar",
                    eventCancel -> {
                        Notification.show("Alteração cancelada.", 3000, Notification.Position.MIDDLE)
                                .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                    });

            dialog.open();  // Abrir o diálogo de confirmação
        } catch (NumberFormatException e) {
            Notification.show("ID deve ser um número inteiro válido.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    } else {
        Notification.show("Preencha todos os campos corretamente.", 3000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}


    private void excluirVeiculo(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPlaca,
            TextField textFieldAnoModelo, ComboBox<Cliente> comboBoxCliente) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Exclusão",
                        "Tem certeza que deseja excluir o veículo?",
                        "Confirmar",
                        eventConfirm -> {
                            if (veiculoController.deletarVeiculo(id)) {
                                Notification.show("Veículo excluído com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                // Limpar os campos
                                textFieldID.clear();
                                textFieldDescricao.clear();
                                textFieldPlaca.clear();
                                textFieldAnoModelo.clear();
                                comboBoxCliente.clear();
                                textFieldID.focus();
                            } else {
                                Notification.show("Erro ao excluir o veículo.", 3000, Notification.Position.MIDDLE)
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
                    .show("O campo ID deve ser preenchido para excluir um veículo.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void pesquisarVeiculo(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPlaca,
            TextField textFieldAnoModelo, ComboBox<Cliente> comboBoxCliente) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                Veiculo veiculo = veiculoController.buscarVeiculoPorId(id);

                if (veiculo != null) {
                    textFieldDescricao.setValue(veiculo.getDescricao_veiculo());
                    textFieldPlaca.setValue(veiculo.getPlaca());
                    textFieldAnoModelo.setValue(String.valueOf(veiculo.getAno_modelo()));
                    comboBoxCliente.setValue(veiculo.getCliente());

                    Notification.show("Veículo encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldDescricao.focus();
                } else {
                    textFieldDescricao.clear();
                    textFieldPlaca.clear();
                    textFieldAnoModelo.clear();
                    comboBoxCliente.clear();
                    textFieldID.focus();
                    Notification.show("Veículo não encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (NumberFormatException e) {
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisaVeiculo(textFieldID, textFieldDescricao, textFieldPlaca, textFieldAnoModelo,
                    comboBoxCliente);
        }
    }

    private void abrirDialogoDePesquisaVeiculo(TextField textFieldID, TextField textFieldDescricao,
            TextField textFieldPlaca, TextField textFieldAnoModelo,
            ComboBox<Cliente> comboBoxCliente) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        ComboBox<Veiculo> comboBox = new ComboBox<>("Buscar Veículo");
        comboBox.setPlaceholder("Digite a descrição do veículo");
        comboBox.setItemLabelGenerator(Veiculo::getDescricao_veiculo);

        // Configura o comboBox para atualizar a lista conforme o usuário digita
        comboBox.addCustomValueSetListener(event -> {
            String descricaoVeiculo = event.getDetail().toLowerCase(); // Converte para minúsculas para busca
                                                                       // case-insensitive
            List<Veiculo> veiculosFiltrados = veiculoController.buscarVeiculosPorDescricao(descricaoVeiculo);
            comboBox.setItems(veiculosFiltrados);
        });

        Button confirmarButton = new Button("Confirmar", e -> {
            Veiculo veiculoSelecionado = comboBox.getValue();
            if (veiculoSelecionado != null) {
                textFieldID.setValue(String.valueOf(veiculoSelecionado.getId()));
                textFieldDescricao.setValue(veiculoSelecionado.getDescricao_veiculo());
                textFieldPlaca.setValue(veiculoSelecionado.getPlaca());
                textFieldAnoModelo.setValue(String.valueOf(veiculoSelecionado.getAno_modelo()));
                comboBoxCliente.setValue(veiculoSelecionado.getCliente());

                Notification.show("Veículo selecionado: " + veiculoSelecionado.getDescricao_veiculo(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Nenhum veículo selecionado.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            dialog.close();
        });

        Button cancelarButton = new Button("Cancelar", e -> dialog.close());

        VerticalLayout layout = new VerticalLayout(comboBox, confirmarButton, cancelarButton);
        dialog.add(layout);

        dialog.open();
    }

    private void atualizarGridConsulta() {
        List<Veiculo> veiculos = veiculoController.buscarTodosVeiculos();
        gridConsulta.setItems(veiculos);
        gridConsulta.removeAllColumns();
        gridConsulta.addColumn(Veiculo::getId).setHeader("ID");
        gridConsulta.addColumn(Veiculo::getDescricao_veiculo).setHeader("Descrição");
        gridConsulta.addColumn(Veiculo::getPlaca).setHeader("Placa");
        gridConsulta.addColumn(Veiculo::getAno_modelo).setHeader("Ano Modelo");
        gridConsulta.addColumn(veiculo -> veiculo.getCliente().getNome()).setHeader("Cliente");
    }
}