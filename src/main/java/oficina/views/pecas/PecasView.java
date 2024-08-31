package oficina.views.pecas;

import oficina.controllers.PecaController;
import oficina.models.Peca;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.grid.Grid;
import oficina.views.MainLayout;

import oficina.controllers.MarcaController;
import oficina.models.Marca;

import java.math.BigDecimal;
import java.util.List;

@PageTitle("Pecas")
@Route(value = "pecas", layout = MainLayout.class)
public class PecasView extends Composite<VerticalLayout> {

    private PecaController pecaController;
    private MarcaController marcaController;
    private Grid<Peca> gridConsulta;
    private ComboBox<Marca> comboBoxMarca;

    public PecasView() {
        pecaController = new PecaController();
        marcaController = new MarcaController();

        Tab tabGerenciar = new Tab("Gerenciar Peça");
        Tab tabConsultar = new Tab("Consultar Peças");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3("Gerenciar Peças");
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField("ID");
        TextField textFieldDescricao = new TextField("Descrição");
        TextField textFieldPreco = new TextField("Preço");
        comboBoxMarca = new ComboBox<>("Marca");
        comboBoxMarca.setWidth("min-content");
        setComboBoxMarcas(comboBoxMarca);
        HorizontalLayout layoutRow = new HorizontalLayout();

        Button buttonSalvar = new Button("Salvar",
                event -> salvarPeca(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca));
        buttonSalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button buttonAlterar = new Button("Alterar",
                event -> alterarPeca(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca));

        buttonAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button buttonExcluir = new Button("Excluir",
                event -> excluirPeca(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca));
        buttonExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button buttonPesquisar = new Button("Pesquisar",
                event -> pesquisarPeca(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca));

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);

        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");

        formLayout2Col.setWidth("100%");
        formLayout2Col.add(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca);

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow.add(buttonSalvar, buttonAlterar, buttonExcluir, buttonPesquisar);

        layoutColumn2.add(h3, formLayout2Col, layoutRow);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Peças");
        gridConsulta = new Grid<>(Peca.class);
        layoutConsulta.add(h3Consulta, gridConsulta);
        layoutConsulta.setVisible(false);

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
            Peca peca = event.getItem();
            if (peca != null) {
                textFieldID.setValue(String.valueOf(peca.getId()));
                textFieldDescricao.setValue(peca.getDescricao());
                textFieldPreco.setValue(String.valueOf(peca.getPreco()));
                comboBoxMarca.setValue(peca.getMarca());
                tabs.setSelectedTab(tabGerenciar); // Alterna para a aba "Gerenciar Peças"
                Notification.show("Peça Selecionada.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });
    }

    private void setComboBoxMarcas(ComboBox<Marca> comboBox) {
        List<Marca> marcas = marcaController.listarTodasMarcas();
        comboBox.setItems(marcas);
        comboBox.setItemLabelGenerator(Marca::getNome_marca);
    }

    private void atualizarGridConsulta() {
        List<Peca> pecas = pecaController.listarTodasPecas();
        gridConsulta.setItems(pecas);
        gridConsulta.removeAllColumns();
        gridConsulta.addColumn(Peca::getId).setHeader("ID");
        gridConsulta.addColumn(Peca::getDescricao).setHeader("Descrição");
        gridConsulta.addColumn(Peca::getPreco).setHeader("Preço");
        gridConsulta.addColumn(Peca -> Peca.getMarca().getNome_marca()).setHeader("Marca");

    }

    private void salvarPeca(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPreco,
            ComboBox<Marca> comboBoxMarca) {
        try {
            // Obtendo os valores dos campos
            String descricao = textFieldDescricao.getValue().trim();
            String precoStr = textFieldPreco.getValue().trim();
            Marca marcaSelecionada = comboBoxMarca.getValue();

            // Validando se a descrição não está vazia e se a marca foi selecionada
            if (!descricao.isEmpty() && marcaSelecionada != null) {
                BigDecimal preco;
                try {
                    preco = new BigDecimal(precoStr);
                } catch (NumberFormatException e) {
                    Notification.show("Preço deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // Salvando a peça usando o controller
                if (pecaController.salvarPeca(descricao, preco, marcaSelecionada)) {
                    Notification.show("Peça salva com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldID.clear();
                    textFieldDescricao.clear();
                    textFieldPreco.clear();
                    comboBoxMarca.clear();
                    textFieldID.focus();
                    atualizarGridConsulta(); // Atualizar a grid se necessário
                } else {
                    Notification.show("Erro ao salvar a peça.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification
                        .show("Descrição não pode ser vazia e marca deve ser selecionada.", 3000,
                                Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            Notification.show("Todos os campos devem ser preenchidos corretamente.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void alterarPeca(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPreco,
            ComboBox<Marca> comboBoxMarca) {
        String idText = textFieldID.getValue();
        String descricao = textFieldDescricao.getValue().trim();
        String precoStr = textFieldPreco.getValue().trim();
        Marca marcaSelecionada = comboBoxMarca.getValue();

        if (!idText.isEmpty() && !descricao.isEmpty() && marcaSelecionada != null) {
            try {
                int id = Integer.parseInt(idText);
                BigDecimal preco;

                try {
                    preco = new BigDecimal(precoStr);
                } catch (NumberFormatException e) {
                    Notification.show("Preço deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Alteração",
                        "Tem certeza que deseja alterar a peça?",
                        "Confirmar",
                        eventConfirm -> {
                            if (pecaController.alterarPeca(id, descricao, preco, marcaSelecionada)) {
                                Notification.show("Peça alterada com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldDescricao.clear();
                                textFieldPreco.clear();
                                comboBoxMarca.clear();
                                textFieldID.focus();
                                atualizarGridConsulta(); // Atualizar a grid se necessário
                            } else {
                                Notification.show("Erro ao alterar a peça.", 3000, Notification.Position.MIDDLE)
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

    private void excluirPeca(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPreco,
            ComboBox<Marca> comboBoxMarca) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Exclusão",
                        "Tem certeza que deseja excluir a peça?",
                        "Confirmar",
                        eventConfirm -> {
                            if (pecaController.deletarPeca(id)) {
                                Notification.show("Peça excluída com sucesso.", 3000, Notification.Position.MIDDLE)
                                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                textFieldID.clear();
                                textFieldDescricao.clear();
                                textFieldPreco.clear();
                                comboBoxMarca.clear();
                                textFieldID.focus();
                                atualizarGridConsulta(); // Atualizar a grid se necessário
                            } else {
                                Notification.show("Erro ao excluir a peça.", 3000, Notification.Position.MIDDLE)
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
                Notification.show("ID deve ser um número inteiro.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("ID não pode ser vazio.", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void pesquisarPeca(TextField textFieldID, TextField textFieldDescricao, TextField textFieldPreco,
            ComboBox<Marca> comboBoxMarca) {
        String idText = textFieldID.getValue();

        if (!idText.isEmpty()) {
            try {
                int id = Integer.parseInt(idText);

                // Pesquisar a peça pelo ID
                Peca peca = pecaController.buscarPecaPorId(id);

                if (peca != null) {
                    // Preencher os campos com os dados da peça encontrada
                    textFieldDescricao.setValue(peca.getDescricao());
                    textFieldPreco.setValue(String.valueOf(peca.getPreco()));
                    comboBoxMarca.setValue(peca.getMarca());

                    Notification.show("Peça encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    textFieldDescricao.focus();
                } else {
                    // Limpar os campos caso a peça não seja encontrada
                    limparCampos(textFieldDescricao, textFieldPreco, comboBoxMarca);
                    textFieldID.focus();
                    Notification.show("Peça não encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (NumberFormatException e) {
                Notification.show("ID inválido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisaPeca(textFieldID, textFieldDescricao, textFieldPreco, comboBoxMarca);
        }
    }

    private void limparCampos(TextField textFieldDescricao, TextField textFieldPreco, ComboBox<Marca> comboBoxMarca) {
        textFieldDescricao.clear();
        textFieldPreco.clear();
        comboBoxMarca.clear();
    }

    private void abrirDialogoDePesquisaPeca(TextField textFieldID, TextField textFieldDescricao,
            TextField textFieldPreco,
            ComboBox<Marca> comboBoxMarca) {
        // Criar a caixa de diálogo para a pesquisa
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        // ComboBox para buscar peças pelo nome
        ComboBox<Peca> comboBox = new ComboBox<>("Buscar Peça");
        comboBox.setPlaceholder("Digite o nome da peça");
        comboBox.setItemLabelGenerator(Peca::getDescricao);

        // Configura o comboBox para atualizar a lista conforme o usuário digita
        comboBox.addCustomValueSetListener(event -> {
            String nomePeca = event.getDetail().toLowerCase(); // Converte para minúsculas para busca case-insensitive
            List<Peca> pecasFiltradas = pecaController.buscarPecaPorNome(nomePeca);
            comboBox.setItems(pecasFiltradas);
        });

        // Botão de confirmar seleção
        Button confirmarButton = new Button("Confirmar", e -> {
            Peca pecaSelecionada = comboBox.getValue();
            if (pecaSelecionada != null) {
                textFieldID.setValue(String.valueOf(pecaSelecionada.getId()));
                textFieldDescricao.setValue(pecaSelecionada.getDescricao());
                textFieldPreco.setValue(String.valueOf(pecaSelecionada.getPreco()));
                comboBoxMarca.setValue(pecaSelecionada.getMarca());

                Notification.show("Peça selecionada: " + pecaSelecionada.getDescricao(), 3000,
                        Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Nenhuma peça selecionada.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            dialog.close();
        });

        // Botão de cancelar
        Button cancelarButton = new Button("Cancelar", e -> dialog.close());

        // Layout da caixa de diálogo
        VerticalLayout layout = new VerticalLayout(comboBox, confirmarButton, cancelarButton);
        dialog.add(layout);

        dialog.open();
    }

    private void mostrarPecasPorNome(String nome) {
        // Buscar peças pelo nome usando o pecaController
        List<Peca> pecasEncontradas = pecaController.buscarPecaPorNome(nome);

        if (pecasEncontradas != null && !pecasEncontradas.isEmpty()) {
            // Criar um diálogo para mostrar os resultados
            Dialog resultadoDialog = new Dialog();
            resultadoDialog.setWidth("600px");

            // Criar um Grid para exibir os detalhes das peças encontradas
            Grid<Peca> gridPecas = new Grid<>(Peca.class, false);
            gridPecas.addColumn(Peca::getId).setHeader("ID").setFlexGrow(0);
            gridPecas.addColumn(Peca::getDescricao).setHeader("Descrição");
            gridPecas.addColumn(peca -> String.format("%.2f", peca.getPreco())).setHeader("Preço");
            gridPecas.addColumn(peca -> peca.getMarca().getNome_marca()).setHeader("Marca");

            // Preencher o Grid com as peças encontradas
            gridPecas.setItems(pecasEncontradas);

            // Adicionar o Grid ao diálogo
            resultadoDialog.add(gridPecas);

            // Botão de fechar o diálogo
            Button fecharButton = new Button("Fechar", event -> resultadoDialog.close());
            resultadoDialog.add(new HorizontalLayout(fecharButton));

            // Abrir o diálogo
            resultadoDialog.open();
        } else {
            Notification.show("Nenhuma peça encontrada com o nome: " + nome, 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

}
