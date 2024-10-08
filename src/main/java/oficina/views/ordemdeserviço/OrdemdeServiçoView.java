package oficina.views.ordemdeserviço;

import oficina.models.OS;
import oficina.controllers.OSController;

import oficina.models.Veiculo;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox; // Importação adicionada
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import oficina.views.MainLayout;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import oficina.controllers.ClienteController;
import oficina.models.Cliente;
import oficina.controllers.MecanicoController;
import oficina.models.Mecanico;
import oficina.controllers.VeiculoController;
import oficina.dao.ConexaoWhatsapp;
import oficina.controllers.PecaController;
import oficina.models.Peca;
import oficina.controllers.ServicoController;
import oficina.models.Servico;

import com.vaadin.flow.component.html.Paragraph;

@PageTitle("Ordem de Serviço")
@Route(value = "Ordem de Servico", layout = MainLayout.class)
public class OrdemdeServiçoView extends Composite<VerticalLayout> {

    private PecaController pecaController;
    private ServicoController servicoController;
    private ClienteController clienteController;
    private MecanicoController mecanicoController;
    private VeiculoController veiculoController;

    private OSController osController;

    private Grid<OS> gridConsultaOS;

    MultiSelectComboBox<Peca> comboBoxPecas = new MultiSelectComboBox(); // Alterado para MultiSelectComboBox
    MultiSelectComboBox<Servico> comboBoxServicos = new MultiSelectComboBox();

    TextField textFieldValorTotal = new TextField();

    public OrdemdeServiçoView() {

        clienteController = new ClienteController();
        mecanicoController = new MecanicoController();
        veiculoController = new VeiculoController();
        pecaController = new PecaController();
        servicoController = new ServicoController();
        osController = new OSController();

        Tab tabGerenciar = new Tab("Gerenciar Ordem de Servico");
        Tab tabConsultar = new Tab("Consultar Ordem de Servicos");
        Tabs tabs = new Tabs(tabGerenciar, tabConsultar);

        VerticalLayout layoutConsulta = new VerticalLayout();
        H3 h3Consulta = new H3("Consultar Ordens de serviços");
        gridConsultaOS = new Grid<>(OS.class);
        layoutConsulta.add(h3Consulta, gridConsultaOS);
        layoutConsulta.setVisible(false);
        VerticalLayout layoutColumn2 = new VerticalLayout();

        getContent().add(tabs, layoutColumn2, layoutConsulta);

        tabs.addSelectedChangeListener(event -> {
            boolean isGerenciarTabSelected = tabs.getSelectedTab() == tabGerenciar;
            layoutColumn2.setVisible(isGerenciarTabSelected);
            layoutConsulta.setVisible(!isGerenciarTabSelected);
            if (!isGerenciarTabSelected) {
                atualizarGridConsulta();
            }
        });

        H3 h3 = new H3();
        FormLayout formLayout2Col = new FormLayout();
        TextField textFieldID = new TextField();
        TextField textFieldNumeroOS = new TextField();
        DateTimePicker dateTimeAberturaOS = new DateTimePicker();
        DateTimePicker dateTimePickerEncerramentoOS = new DateTimePicker();

        ComboBox<Cliente> comboBoxCliente = new ComboBox();
        ComboBox<Mecanico> comboBoxMecanico = new ComboBox();
        ComboBox<Veiculo> comboBoxVeiculo = new ComboBox();

        comboBoxPecas.addValueChangeListener(event -> calcularValorTotal());
        comboBoxServicos.addValueChangeListener(event -> calcularValorTotal());
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonPrimarySalvar = new Button();
        Button buttonPrimaryAlterar = new Button();
        Button buttonPrimaryExcluir = new Button();
        Button buttonSecondaryPesquisar = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        h3.setText("Gerenciar OS");
        h3.setWidth("100%");
        formLayout2Col.setWidth("100%");
        textFieldID.setLabel("ID");
        textFieldID.setReadOnly(true);
        textFieldNumeroOS.setLabel("Numero OS");
        textFieldNumeroOS.setReadOnly(true);
        dateTimeAberturaOS.setLabel("Data Abertura OS");
        dateTimeAberturaOS.setWidth("min-content");
        dateTimePickerEncerramentoOS.setLabel("Data encerramento OS");
        dateTimePickerEncerramentoOS.setWidth("min-content");
        textFieldValorTotal.setLabel("Valor Total");
        textFieldValorTotal.setReadOnly(true);
        comboBoxCliente.setLabel("Cliente");
        comboBoxCliente.setWidth("min-content");
        setComboBoxClientes(comboBoxCliente);
        comboBoxMecanico.setLabel("Mecânico");
        comboBoxMecanico.setWidth("min-content");
        setComboBoxMecanicos(comboBoxMecanico);
        comboBoxVeiculo.setLabel("Veículo");
        comboBoxVeiculo.setWidth("min-content");
        setComboBoxVeiculos(comboBoxVeiculo);
        comboBoxPecas.setLabel("Peças");
        comboBoxPecas.setWidth("min-content");
        setMultiSelectComboBoxPecas(comboBoxPecas);
        comboBoxServicos.setLabel("Serviços");
        comboBoxServicos.setWidth("min-content");
        setMultiSelectComboBoxServicos(comboBoxServicos);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        buttonPrimarySalvar.setText("Salvar");
        buttonPrimarySalvar.setWidth("min-content");
        buttonPrimarySalvar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimaryAlterar.setText("Alterar");
        buttonPrimaryAlterar.setWidth("min-content");
        buttonPrimaryAlterar.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        buttonPrimaryExcluir.setText("Excluir");
        buttonPrimaryExcluir.setWidth("min-content");
        buttonPrimaryExcluir.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonSecondaryPesquisar.setText("Pesquisar");
        buttonSecondaryPesquisar.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(textFieldID);
        formLayout2Col.add(textFieldNumeroOS);
        formLayout2Col.add(textFieldValorTotal);
        formLayout2Col.add(comboBoxCliente);
        formLayout2Col.add(dateTimeAberturaOS);
        formLayout2Col.add(dateTimePickerEncerramentoOS);
        formLayout2Col.add(comboBoxMecanico);
        formLayout2Col.add(comboBoxVeiculo);
        formLayout2Col.add(comboBoxPecas);
        formLayout2Col.add(comboBoxServicos);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonPrimarySalvar);
        layoutRow.add(buttonPrimaryAlterar);
        layoutRow.add(buttonPrimaryExcluir);
        layoutRow.add(buttonSecondaryPesquisar);
        // getContent().add();

        buttonSecondaryPesquisar.addClickListener(
                event -> pesquisarOS(textFieldID, textFieldNumeroOS, dateTimeAberturaOS, dateTimePickerEncerramentoOS,
                        textFieldValorTotal, comboBoxMecanico, comboBoxCliente, comboBoxVeiculo, comboBoxPecas,
                        comboBoxServicos));

        gridConsultaOS.addItemDoubleClickListener(event -> {
            OS osSelecionada = event.getItem();
            if (osSelecionada != null) {
                textFieldID.setValue(String.valueOf(osSelecionada.getId()));
                textFieldNumeroOS.setValue(osSelecionada.getNumero_os());
                dateTimeAberturaOS.setValue(osSelecionada.getData_abertura_os());
                dateTimePickerEncerramentoOS.setValue(osSelecionada.getData_encerramento_os());
                textFieldValorTotal.setValue(String.valueOf(osSelecionada.getValor_total()));
                comboBoxMecanico.setValue(osSelecionada.getMecanico());
                comboBoxCliente.setValue(osSelecionada.getCliente());
                comboBoxVeiculo.setValue(osSelecionada.getVeiculo());

                comboBoxPecas.deselectAll();
                comboBoxPecas.setValue(new HashSet<>(osSelecionada.getPecas()));

                comboBoxServicos.deselectAll();
                comboBoxServicos.setValue(new HashSet<>(osSelecionada.getServicos()));

                tabs.setSelectedTab(tabGerenciar);
                Notification.show("Ordem de Serviço Selecionada.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        buttonPrimarySalvar.addClickListener(event -> {
            try {
                // Obtém os valores dos campos
                LocalDateTime dataAbertura = dateTimeAberturaOS.getValue();
                LocalDateTime dataEncerramento = dateTimePickerEncerramentoOS.getValue();
                String valorTotal = textFieldValorTotal.getValue();
                Mecanico mecanico = comboBoxMecanico.getValue();
                Cliente cliente = comboBoxCliente.getValue();
                Veiculo veiculo = comboBoxVeiculo.getValue();
                List<Peca> pecas = new ArrayList<>(comboBoxPecas.getSelectedItems());
                List<Servico> servicos = new ArrayList<>(comboBoxServicos.getSelectedItems());

                // Verifica se algum campo está vazio
                if (dataAbertura == null || dataEncerramento == null || valorTotal.isEmpty() ||
                        mecanico == null || cliente == null || veiculo == null || pecas.isEmpty()
                        || servicos.isEmpty()) {
                    Notification
                            .show("Por favor, preencha todos os campos obrigatórios.", 5000,
                                    Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                // Gera o número da OS no formato desejado
                String numeroOS = dataAbertura.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

                // Cria uma nova instância de OS e define os atributos principais
                OS novaOS = new OS();
                novaOS.setNumero_os(numeroOS); // Define o número da OS automaticamente
                novaOS.setData_abertura_os(dataAbertura);
                novaOS.setData_encerramento_os(dataEncerramento);
                novaOS.setValor_total(new BigDecimal(valorTotal));
                novaOS.setMecanico(mecanico);
                novaOS.setCliente(cliente);
                novaOS.setVeiculo(veiculo);
                novaOS.setPecas(pecas);
                novaOS.setServicos(servicos);

                // Chama o método do controller para salvar a nova ordem de serviço
                boolean sucesso = osController.adicionarOS(novaOS);

                if (sucesso) {
                    Notification.show("Ordem de Serviço salva com sucesso.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // Envia a mensagem via WhatsApp
                    try {
                        ConexaoWhatsapp.EnviarMensagem(cliente, novaOS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Notification.show("Erro ao enviar a mensagem: " + e.getMessage(), 5000,
                                Notification.Position.MIDDLE);
                    }

                    // Limpa os campos do formulário após salvar
                    textFieldNumeroOS.clear();
                    dateTimeAberturaOS.clear();
                    dateTimePickerEncerramentoOS.clear();
                    textFieldValorTotal.clear();
                    comboBoxMecanico.clear();
                    comboBoxCliente.clear();
                    comboBoxVeiculo.clear();
                    comboBoxPecas.clear();
                    comboBoxServicos.clear();
                    textFieldID.focus();

                    // Atualiza o grid de consulta para refletir as novas informações
                    atualizarGridConsulta();
                } else {
                    Notification.show("Erro ao salvar a Ordem de Serviço.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (Exception e) {
                Notification.show("Erro inesperado ao salvar a Ordem de Serviço.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        buttonPrimaryAlterar.addClickListener(event -> {
            String textFieldIdOS = textFieldID.getValue().trim();
            String valorTotalStr = textFieldValorTotal.getValue().trim();
            LocalDateTime dataAbertura = dateTimeAberturaOS.getValue();
            LocalDateTime dataEncerramento = dateTimePickerEncerramentoOS.getValue();
            Mecanico mecanico = comboBoxMecanico.getValue();
            Cliente cliente = comboBoxCliente.getValue();
            Veiculo veiculo = comboBoxVeiculo.getValue();

            // Obtendo as listas de peças e serviços selecionados
            List<Peca> pecasSelecionadas = new ArrayList<>(comboBoxPecas.getSelectedItems());
            List<Servico> servicosSelecionados = new ArrayList<>(comboBoxServicos.getSelectedItems());

            // Construindo a mensagem de erro
            StringBuilder mensagemErro = new StringBuilder("Os seguintes campos precisam ser preenchidos:\n");

            boolean temErro = false;

            if (textFieldIdOS.isEmpty()) {
                mensagemErro.append("- ID da OS\n");
                temErro = true;
            }

            if (valorTotalStr.isEmpty()) {
                mensagemErro.append("- Valor total\n");
                temErro = true;
            }

            if (dataAbertura == null) {
                mensagemErro.append("- Data de abertura\n");
                temErro = true;
            }

            if (dataEncerramento == null) {
                mensagemErro.append("- Data de encerramento\n");
                temErro = true;
            }

            if (mecanico == null) {
                mensagemErro.append("- Mecânico\n");
                temErro = true;
            }

            if (cliente == null) {
                mensagemErro.append("- Cliente\n");
                temErro = true;
            }

            if (veiculo == null) {
                mensagemErro.append("- Veículo\n");
                temErro = true;
            }

            if (pecasSelecionadas.isEmpty()) {
                mensagemErro.append("- Pelo menos uma peça\n");
                temErro = true;
            }

            if (servicosSelecionados.isEmpty()) {
                mensagemErro.append("- Pelo menos um serviço\n");
                temErro = true;
            }

            if (temErro) {
                Notification.show(mensagemErro.toString(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                BigDecimal valorTotal = new BigDecimal(valorTotalStr);

                ConfirmDialog dialog = new ConfirmDialog(
                        "Confirmar Alteração",
                        "Tem certeza que deseja alterar a ordem de serviço?",
                        "Confirmar",
                        eventConfirm -> {
                            try {
                                // Obter a OS existente com base no ID (ou uma outra abordagem para recuperar a
                                // OS existente)
                                int idOS = Integer.parseInt(textFieldIdOS);

                                OS osSelecionada = osController.buscarOSPorId(idOS);

                                if (osSelecionada != null) {
                                    // Manter o numero_os original
                                    osSelecionada.setData_abertura_os(dataAbertura);
                                    osSelecionada.setData_encerramento_os(dataEncerramento);
                                    osSelecionada.setValor_total(valorTotal);
                                    osSelecionada.setMecanico(mecanico);
                                    osSelecionada.setCliente(cliente);
                                    osSelecionada.setVeiculo(veiculo);
                                    osSelecionada.setPecas(pecasSelecionadas);
                                    osSelecionada.setServicos(servicosSelecionados);

                                    // Chama o método do controller para alterar a ordem de serviço
                                    boolean sucesso = osController.atualizarOS(osSelecionada);

                                    if (sucesso) {
                                        Notification.show("Ordem de Serviço alterada com sucesso.", 3000,
                                                Notification.Position.MIDDLE)
                                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                        // Limpar campos
                                        textFieldNumeroOS.clear();
                                        textFieldID.clear();
                                        dateTimeAberturaOS.clear();
                                        dateTimePickerEncerramentoOS.clear();
                                        textFieldValorTotal.clear();
                                        comboBoxMecanico.clear();
                                        comboBoxCliente.clear();
                                        comboBoxVeiculo.clear();
                                        comboBoxPecas.clear();
                                        comboBoxServicos.clear();
                                        textFieldID.focus();
                                        atualizarGridConsulta();
                                    } else {
                                        Notification.show("Erro ao alterar a Ordem de Serviço.", 3000,
                                                Notification.Position.MIDDLE)
                                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    }
                                } else {
                                    Notification.show("Ordem de Serviço não encontrada.", 3000,
                                            Notification.Position.MIDDLE)
                                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                }
                            } catch (Exception e) {
                                Notification.show("Erro inesperado ao alterar a Ordem de Serviço.", 3000,
                                        Notification.Position.MIDDLE)
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
                Notification.show("Valor total deve ser um número válido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        buttonPrimaryExcluir.addClickListener(event -> {
            String textFieldIdOS = textFieldID.getValue().trim();

            // Verifica se o campo ID não está vazio
            if (!textFieldIdOS.isEmpty()) {
                try {
                    // Converte o ID para um número
                    int idOS = Integer.parseInt(textFieldIdOS);

                    ConfirmDialog dialog = new ConfirmDialog(
                            "Confirmar Exclusão",
                            "Tem certeza que deseja excluir a ordem de serviço?",
                            "Excluir",
                            eventConfirm -> {
                                try {
                                    // Chama o método do controller para excluir a ordem de serviço
                                    boolean sucesso = osController.deletarOS(idOS);

                                    if (sucesso) {
                                        Notification.show("Ordem de Serviço excluída com sucesso.", 3000,
                                                Notification.Position.MIDDLE)
                                                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                        // Limpar campos
                                        textFieldID.clear();
                                        textFieldNumeroOS.clear();
                                        dateTimeAberturaOS.clear();
                                        dateTimePickerEncerramentoOS.clear();
                                        textFieldValorTotal.clear();
                                        comboBoxMecanico.clear();
                                        comboBoxCliente.clear();
                                        comboBoxVeiculo.clear();
                                        comboBoxPecas.clear();
                                        comboBoxServicos.clear();
                                        comboBoxCliente.focus();
                                        atualizarGridConsulta();
                                    } else {
                                        Notification.show("Erro ao excluir a Ordem de Serviço.", 3000,
                                                Notification.Position.MIDDLE)
                                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    }
                                } catch (Exception e) {
                                    Notification.show("Erro inesperado ao excluir a Ordem de Serviço.", 3000,
                                            Notification.Position.MIDDLE)
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
                    // Notifica o usuário se o ID não for um número válido
                    Notification
                            .show("ID da Ordem de Serviço deve ser um número válido.", 3000,
                                    Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("ID da Ordem de Serviço não pode estar vazio.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

    }

    private void pesquisarOS(TextField textFieldID, TextField textFieldNumeroOS,
            DateTimePicker dateTimePickerDataAbertura,
            DateTimePicker dateTimePickerDataEncerramento, TextField textFieldValorTotal,
            ComboBox<Mecanico> comboBoxMecanico, ComboBox<Cliente> comboBoxCliente,
            ComboBox<Veiculo> comboBoxVeiculo, MultiSelectComboBox<Peca> comboBoxPecas,
            MultiSelectComboBox<Servico> comboBoxServicos) {

        String numeroOSText = textFieldNumeroOS.getValue();

        if (!numeroOSText.isEmpty()) {
            try {
                // Pesquisar a OS pelo número da OS
                OS os = osController.buscarOSPorNumero(numeroOSText);

                if (os != null) {
                    // Usar diretamente as datas como LocalDateTime se já estiverem nesse formato
                    LocalDateTime dataAbertura = os.getData_abertura_os();
                    LocalDateTime dataEncerramento = os.getData_encerramento_os();

                    // Preencher os campos com os dados da OS encontrada
                    textFieldID.setValue(String.valueOf(os.getId()));
                    textFieldNumeroOS.setValue(os.getNumero_os());
                    dateTimePickerDataAbertura.setValue(dataAbertura);
                    dateTimePickerDataEncerramento.setValue(dataEncerramento);
                    textFieldValorTotal.setValue(os.getValor_total().toString());
                    comboBoxMecanico.setValue(os.getMecanico());
                    comboBoxCliente.setValue(os.getCliente());
                    comboBoxVeiculo.setValue(os.getVeiculo());

                    // Preencher pecas e servicos
                    comboBoxPecas.setItems(os.getPecas());
                    comboBoxServicos.setItems(os.getServicos());

                    Notification.show("Ordem de Serviço encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    dateTimePickerDataAbertura.focus();
                } else {
                    // Limpar os campos caso a OS não seja encontrada
                    limparCamposOS(textFieldID, textFieldNumeroOS, dateTimePickerDataAbertura,
                            dateTimePickerDataEncerramento, textFieldValorTotal, comboBoxMecanico,
                            comboBoxCliente, comboBoxVeiculo, comboBoxPecas,
                            comboBoxServicos);
                    textFieldNumeroOS.focus();
                    Notification.show("Ordem de Serviço não encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (Exception e) {
                Notification.show("Erro ao buscar ordem de serviço.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            abrirDialogoDePesquisaOS(textFieldID, textFieldNumeroOS, dateTimePickerDataAbertura,
                    dateTimePickerDataEncerramento,
                    textFieldValorTotal, comboBoxMecanico, comboBoxCliente, comboBoxVeiculo, comboBoxPecas,
                    comboBoxServicos);
        }
    }

    private void abrirDialogoDePesquisaOS(TextField textFieldId, TextField textFieldNumeroOS,
            DateTimePicker dateTimePickerDataAbertura,
            DateTimePicker dateTimePickerDataEncerramento, TextField textFieldValorTotal,
            ComboBox<Mecanico> comboBoxMecanico, ComboBox<Cliente> comboBoxCliente,
            ComboBox<Veiculo> comboBoxVeiculo, MultiSelectComboBox<Peca> comboBoxPecas,
            MultiSelectComboBox<Servico> comboBoxServicos) {

        // Criar a caixa de diálogo para a pesquisa
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        // Campo para buscar OS pelo número exato
        TextField campoNumeroOS = new TextField("Buscar OS");
        campoNumeroOS.setPlaceholder("Digite o número da OS");

        // Botão de confirmar seleção
        Button confirmarButton = new Button("Confirmar", e -> {
            String numeroOS = campoNumeroOS.getValue();

            if (numeroOS != null && !numeroOS.isEmpty()) {
                // Buscar OS pelo número fornecido pelo usuário
                OS osSelecionada = osController.buscarOSPorNumero(numeroOS);

                if (osSelecionada != null) {
                    // Preencher os campos com os dados da OS encontrada
                    textFieldId.setValue(String.valueOf(osSelecionada.getId()));
                    textFieldNumeroOS.setValue(osSelecionada.getNumero_os());
                    dateTimePickerDataAbertura.setValue(osSelecionada.getData_abertura_os());
                    dateTimePickerDataEncerramento.setValue(osSelecionada.getData_encerramento_os());
                    textFieldValorTotal.setValue(osSelecionada.getValor_total().toString());
                    comboBoxMecanico.setValue(osSelecionada.getMecanico());
                    comboBoxCliente.setValue(osSelecionada.getCliente());
                    comboBoxVeiculo.setValue(osSelecionada.getVeiculo());

                    comboBoxPecas.deselectAll();
                    comboBoxPecas.setValue(new HashSet<>(osSelecionada.getPecas()));

                    comboBoxServicos.deselectAll();
                    comboBoxServicos.setValue(new HashSet<>(osSelecionada.getServicos()));

                    Notification.show("Ordem de Serviço selecionada: " + osSelecionada.getNumero_os(), 3000,
                            Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    Notification.show("Nenhuma ordem de serviço encontrada.", 3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Por favor, insira um número de OS válido.", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            dialog.close();
        });

        // Botão de cancelar
        Button cancelarButton = new Button("Cancelar", e -> dialog.close());

        // Layout da caixa de diálogo
        VerticalLayout layout = new VerticalLayout(campoNumeroOS, confirmarButton, cancelarButton);
        dialog.add(layout);

        dialog.open();
    }

    private void limparCamposOS(TextField textFieldID, TextField textFieldNumeroOS,
            DateTimePicker dateTimePickerAberturaOS,
            DateTimePicker dateTimePickerEncerramentoOS, TextField textFieldValorTotal,
            ComboBox<Mecanico> comboBoxMecanico,
            ComboBox<Cliente> comboBoxCliente, ComboBox<Veiculo> comboBoxVeiculo,
            MultiSelectComboBox<Peca> comboBoxPecas,
            MultiSelectComboBox<Servico> comboBoxServicos) {
        // Limpar todos os campos
        textFieldID.clear();
        textFieldNumeroOS.clear();
        dateTimePickerAberturaOS.clear();
        dateTimePickerEncerramentoOS.clear();
        textFieldValorTotal.clear();
        comboBoxMecanico.clear();
        comboBoxCliente.clear();
        comboBoxVeiculo.clear();
        comboBoxPecas.clear();
        comboBoxServicos.clear();

        // Focar no campo ID
        textFieldID.focus();
    }

    private void atualizarGridConsulta() {
        List<OS> listaOs = osController.buscarTodasOS();
        gridConsultaOS.setItems(listaOs);
        gridConsultaOS.removeAllColumns();
        gridConsultaOS.addColumn(OS::getId).setHeader("ID");
        gridConsultaOS.addColumn(OS::getNumero_os).setHeader("Numero OS");
        gridConsultaOS.addColumn(OS::getData_abertura_os).setHeader("Data Abertura");
        gridConsultaOS.addColumn(OS::getData_encerramento_os).setHeader("Data Encerramento");
        gridConsultaOS.addColumn(OS::getValor_total).setHeader("Valor Total");
        gridConsultaOS.addColumn(OS -> OS.getMecanico().getNome()).setHeader("Mecanico");
        gridConsultaOS.addColumn(OS -> OS.getCliente().getNome()).setHeader("Cliente");
        gridConsultaOS.addColumn(OS -> OS.getVeiculo().getDescricao_veiculo()).setHeader("Veículo");

        // Coluna de Peças com Dialog ao clicar
        gridConsultaOS.addComponentColumn(OS -> {
            Button pecasButton = new Button("Ver Peças");

            // Listener de click para abrir um Dialog com a lista de peças numerada
            pecasButton.addClickListener(e -> {
                Dialog pecasDialog = new Dialog();
                pecasDialog.add(new Paragraph("Peças utilizadas na OS:"));

                // Iterando e numerando manualmente as peças
                List<String> pecasList = OS.getPecas().stream()
                        .map(Peca::getDescricao)
                        .distinct()
                        .collect(Collectors.toList());
                for (int i = 0; i < pecasList.size(); i++) {
                    String numeracao = (i + 1) + ". " + pecasList.get(i); // Adicionando o número
                    pecasDialog.add(new Paragraph(numeracao)); // Adicionando como Paragraph
                }
                pecasDialog.setWidth("600px");
                pecasDialog.setHeight("300px");
                pecasDialog.open();
            });

            return pecasButton;
        }).setHeader("Peças");

        // Coluna de Serviços com Dialog ao clicar
        gridConsultaOS.addComponentColumn(OS -> {
            Button servicosButton = new Button("Ver Serviços");

            // Listener de click para abrir um Dialog com a lista de serviços numerada
            servicosButton.addClickListener(e -> {
                Dialog servicosDialog = new Dialog();
                servicosDialog.add(new Paragraph("Serviços realizados na OS:"));

                // Iterando e numerando manualmente os serviços
                List<String> servicosList = OS.getServicos().stream()
                        .map(Servico::getDescricao_servico)
                        .distinct()
                        .collect(Collectors.toList());
                for (int i = 0; i < servicosList.size(); i++) {
                    String numeracao = (i + 1) + ". " + servicosList.get(i); // Adicionando o número
                    servicosDialog.add(new Paragraph(numeracao)); // Adicionando como Paragraph
                }
                servicosDialog.setWidth("600px");
                servicosDialog.setHeight("300px");
                servicosDialog.open();
            });

            return servicosButton;
        }).setHeader("Serviços");
    }

    // record SampleItem(String value, String label, Boolean disabled) {
    // }

    private void setComboBoxClientes(ComboBox<Cliente> comboBox) {
        List<Cliente> clientes = clienteController.buscarTodosClientes();
        comboBox.setItems(clientes);
        comboBox.setItemLabelGenerator(Cliente::getNome);
    }

    private void setComboBoxMecanicos(ComboBox<Mecanico> comboBox) {
        List<Mecanico> mecanicos = mecanicoController.buscarTodosMecanicos();
        comboBox.setItems(mecanicos);
        comboBox.setItemLabelGenerator(Mecanico::getNome);
    }

    private void setComboBoxVeiculos(ComboBox<Veiculo> comboBox) {
        List<Veiculo> veiculos = veiculoController.buscarTodosVeiculos();
        comboBox.setItems(veiculos);
        comboBox.setItemLabelGenerator(Veiculo::getDescricao_veiculo);
    }

    private void setMultiSelectComboBoxPecas(MultiSelectComboBox<Peca> comboBoxPecas) {
        List<Peca> pecas = pecaController.listarTodasPecas();
        comboBoxPecas.setItems(pecas);
        comboBoxPecas.setItemLabelGenerator(Peca::getDescricao); // Supondo que Peca tenha um método getDescricao()
    }

    private void setMultiSelectComboBoxServicos(MultiSelectComboBox<Servico> comboBoxServico) {
        List<Servico> servicos = servicoController.buscarTodosServicos();
        comboBoxServico.setItems(servicos);
        comboBoxServico.setItemLabelGenerator(Servico::getDescricao_servico);
    }

    private void calcularValorTotal() {
        BigDecimal valorTotal = BigDecimal.ZERO;

        // Somando o valor das peças selecionadas
        for (Peca peca : comboBoxPecas.getValue()) {
            valorTotal = valorTotal.add(peca.getPreco()); // Supondo que Peca tenha o método getValor()
        }

        // Somando o valor dos serviços selecionados
        for (Servico servico : comboBoxServicos.getValue()) {
            valorTotal = valorTotal.add(servico.getValor_servico()); // Supondo que Servico tenha o método getValor()
        }

        // Atualizando o campo textFieldValorTotal
        textFieldValorTotal.setValue(valorTotal.toString());
    }

}