package oficina.views.serviços;

import java.math.BigDecimal;
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

import oficina.controllers.ServicoController;
import oficina.models.Servico;
import oficina.views.MainLayout;

@PageTitle("Serviços")
@Route(value = "servico", layout = MainLayout.class)
public class ServiçosView extends Composite<VerticalLayout> {

    private ServicoController servicoController;
    private Grid<Servico> gridConsulta;

    public ServiçosView() {
        servicoController = new ServicoController();

        Tab tabGerenciar = new Tab("Gerenciar Serviço");
        Tab tabConsultar = new Tab("Consultar Serviços");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldDescricao = new TextField("Descrição Serviço");
        TextField textFieldValor = new TextField("Valor Serviço");
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
        h3.setText("Gerenciar Serviços");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        textFieldDescricao.setWidth("56%");
        textFieldValor.setWidth("56%");
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
        formLayout2Col.add(textFieldID, textFieldDescricao, textFieldValor);
        layoutRow.add(buttonSalvar, buttonAlterar, buttonExcluir, buttonPesquisar);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Serviços");
        gridConsulta = new Grid<>(Servico.class);
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
        buttonSalvar.addClickListener(event -> salvarServico(textFieldID, textFieldDescricao, textFieldValor));
        buttonAlterar.addClickListener(event -> alterarServico(textFieldID, textFieldDescricao, textFieldValor));
        buttonExcluir.addClickListener(event -> excluirServico(textFieldID, textFieldDescricao, textFieldValor));
        buttonPesquisar.addClickListener(event -> pesquisarServico(textFieldID, textFieldDescricao, textFieldValor));

        // Configura o clique duplo no grid
        gridConsulta.addItemDoubleClickListener(event -> {
            Servico servico = event.getItem();
            if (servico != null) {
                textFieldID.setValue(String.valueOf(servico.getId()));
                textFieldDescricao.setValue(servico.getDescricao_servico());
                textFieldValor.setValue(String.valueOf(servico.getValor_servico()));
                tabs.setSelectedTab(tabGerenciar);
                Notification.show("Serviço selecionado.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
    }

    private void salvarServico(TextField textFieldID, TextField textFieldDescricao, TextField textFieldValor) {
        try {
            String descricao = textFieldDescricao.getValue().trim();
            String valorText = textFieldValor.getValue().trim();

            if (!descricao.isEmpty() && !valorText.isEmpty()) {
                BigDecimal valor = new BigDecimal(valorText); // Corrigido aqui

                Servico servico = new Servico();
                servico.setDescricao_servico(descricao);
                servico.setValor_servico(valor);

                if (servicoController.adicionarServico(servico)) {
                    Notification.show("Serviço salvo com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldID.clear();
                    textFieldDescricao.clear();
                    textFieldValor.clear();
                    textFieldID.focus();
                    atualizarGridConsulta();
                } else {
                    Notification.show("Erro ao salvar o serviço.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Descrição e valor não podem ser vazios.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (NumberFormatException e) {
            Notification.show("Valor deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Erro inesperado ao salvar o serviço.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarServico(TextField textFieldID, TextField textFieldDescricao, TextField textFieldValor) {
        String idText = textFieldID.getValue();
        String descricao = textFieldDescricao.getValue().trim();
        String valorText = textFieldValor.getValue().trim();

        if (!idText.isEmpty() && !descricao.isEmpty() && !valorText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);
                BigDecimal valor;

                try {
                    valor = new BigDecimal(valorText);
                } catch (NumberFormatException e) {
                    Notification.show("Valor deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Alteração",
                        "Tem certeza que deseja alterar o serviço?",
                        "Confirmar",
                        eventConfirm -> {
                            Servico servico = new Servico();
                            servico.setId(id);
                            servico.setDescricao_servico(descricao);
                            servico.setValor_servico(valor);

                            if (servicoController.atualizarServico(servico)) {
                                Notification.show("Serviço alterado com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldDescricao.clear();
                                textFieldValor.clear();
                                textFieldID.focus();
                                atualizarGridConsulta();
                            } else {
                                Notification.show("Erro ao alterar o serviço.", 3000, Notification.Position.MIDDLE)
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

    private void excluirServico(TextField textFieldID, TextField textFieldDescricao, TextField textFieldValor) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            int id = Integer.parseInt(idText);

            ConfirmDialog dialog = new ConfirmDialog(
                    "Confirmar Exclusão",
                    "Tem certeza que deseja excluir o serviço?",
                    "Confirmar",
                    eventConfirm -> {
                        if (servicoController.deletarServico(id)) {
                            Notification.show("Serviço excluído com sucesso.", 3000, Notification.Position.MIDDLE)
                                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            textFieldID.clear();
                            textFieldDescricao.clear();
                            textFieldValor.clear();
                            textFieldID.focus();
                            atualizarGridConsulta();
                        } else {
                            Notification.show("Serviço não encontrado.", 3000, Notification.Position.MIDDLE)
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

    private void pesquisarServico(TextField textFieldID, TextField textFieldDescricao, TextField textFieldValor) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                Servico servico = servicoController.buscarServicoPorId(id);

                if (servico != null) {
                    textFieldDescricao.setValue(servico.getDescricao_servico());
                    textFieldValor.setValue(String.valueOf(servico.getValor_servico()));
                    Notification.show("Serviço encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldDescricao.focus();
                } else {
                    textFieldDescricao.clear();
                    textFieldValor.clear();
                    textFieldID.focus();
                    Notification.show("Serviço não encontrado.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (NumberFormatException e) {
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisa(textFieldID, textFieldDescricao);
        }
    }

    private void abrirDialogoDePesquisa(TextField textFieldID, TextField textFieldDescricao) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        ComboBox<Servico> comboBox = new ComboBox<>("Buscar Serviço");
        comboBox.setPlaceholder("Digite a descrição do serviço");
        comboBox.setItemLabelGenerator(Servico::getDescricao_servico);

        // Configura o comboBox para atualizar a lista conforme o usuário digita
        comboBox.addCustomValueSetListener(event -> {
            String descricaoServico = event.getDetail().toLowerCase(); // Converte para minúsculas para busca
                                                                       // case-insensitive
            List<Servico> servicosFiltrados = servicoController.buscarServicosPorDescricao(descricaoServico);
            comboBox.setItems(servicosFiltrados);
        });

        Button confirmarButton = new Button("Confirmar", e -> {
            Servico servicoSelecionado = comboBox.getValue();
            if (servicoSelecionado != null) {
                textFieldID.setValue(String.valueOf(servicoSelecionado.getId()));
                textFieldDescricao.setValue(servicoSelecionado.getDescricao_servico());
                Notification
                        .show("Serviço selecionado: " + servicoSelecionado.getDescricao_servico(), 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Nenhum serviço selecionado.", 3000, Notification.Position.MIDDLE)
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
        List<Servico> servicos = servicoController.buscarTodosServicos();
        if (servicos == null || servicos.isEmpty()) {
            gridConsulta.setItems(Collections.emptyList());
        } else {
            gridConsulta.setItems(servicos);
        }
    }
}
