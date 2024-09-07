package oficina.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import oficina.models.Cliente;
import oficina.models.Mecanico;
import oficina.models.OS;
import oficina.models.Peca;
import oficina.models.Servico;
import oficina.models.Veiculo;
import oficina.controllers.MecanicoController;
import oficina.controllers.ClienteController;
import oficina.controllers.VeiculoController;
import oficina.controllers.PecaController;
import oficina.controllers.ServicoController;

public class OSDAO {

    private static final Logger logger = Logger.getLogger(OSDAO.class.getName());

    private MecanicoController mecanicoController = new MecanicoController();
    private ClienteController clienteController = new ClienteController();
    private VeiculoController veiculoController = new VeiculoController();
    private PecaController pecaController = new PecaController();
    private ServicoController servicoController = new ServicoController();

    public boolean salvar(OS os) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Iniciar transação

            // Inserir na tabela 'os'
            String insertOS = "INSERT INTO os (numero_os, data_abertura_os, data_encerramento_os, valor_total, id_mecanico, id_cliente, id_veiculo) VALUES (?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertOS, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, os.getNumero_os());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(os.getData_abertura_os()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(os.getData_encerramento_os()));
            preparedStatement.setBigDecimal(4, os.getValor_total());
            preparedStatement.setInt(5, os.getMecanico().getId());
            preparedStatement.setInt(6, os.getCliente().getId());
            preparedStatement.setInt(7, os.getVeiculo().getId());
            int resultado = preparedStatement.executeUpdate();

            if (resultado > 0) {
                // Obter o ID gerado para a nova ordem de serviço
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idOS = generatedKeys.getInt(1);

                    // Inserir na tabela 'os_peca'
                    if (os.getPecas() != null) {
                        String insertOSPeca = "INSERT INTO os_peca (id_os, id_peca) VALUES (?, ?)";
                        preparedStatement = connection.prepareStatement(insertOSPeca);
                        for (Peca peca : os.getPecas()) {
                            preparedStatement.setInt(1, idOS);
                            preparedStatement.setInt(2, peca.getId());
                            preparedStatement.addBatch();
                        }
                        preparedStatement.executeBatch();
                    }

                    // Inserir na tabela 'os_servico'
                    if (os.getServicos() != null) {
                        String insertOSServico = "INSERT INTO os_servico (id_os, id_servico) VALUES (?, ?)";
                        preparedStatement = connection.prepareStatement(insertOSServico);
                        for (Servico servico : os.getServicos()) {
                            preparedStatement.setInt(1, idOS);
                            preparedStatement.setInt(2, servico.getId());
                            preparedStatement.addBatch();
                        }
                        preparedStatement.executeBatch();
                    }

                    connection.commit(); // Confirmar transação
                    logger.log(Level.INFO, "Ordem de serviço salva com sucesso: {0}", os.getNumero_os());
                    return true;
                } else {
                    connection.rollback(); // Reverter transação se falhar
                    logger.log(Level.SEVERE, "Erro ao obter o ID gerado para a ordem de serviço: " + os.getNumero_os());
                    return false;
                }
            } else {
                connection.rollback(); // Reverter transação se falhar
                logger.log(Level.SEVERE, "Erro ao salvar a ordem de serviço: " + os.getNumero_os());
                return false;
            }
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Reverter transação em caso de erro
                }
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao reverter transação: " + os.getNumero_os(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao salvar a ordem de serviço: " + os.getNumero_os(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException closeEx) {
                logger.log(Level.SEVERE, "Erro ao fechar os recursos", closeEx);
            }
        }
    }

    public boolean alterarOS(OS os) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); // Iniciar transação

            // Atualizar na tabela 'os'
            String updateOS = "UPDATE os SET numero_os = ?, data_abertura_os = ?, data_encerramento_os = ?, valor_total = ?, id_mecanico = ?, id_cliente = ?, id_veiculo = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(updateOS);
            preparedStatement.setString(1, os.getNumero_os());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(os.getData_abertura_os()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(os.getData_encerramento_os()));
            preparedStatement.setBigDecimal(4, os.getValor_total());
            preparedStatement.setInt(5, os.getMecanico().getId());
            preparedStatement.setInt(6, os.getCliente().getId());
            preparedStatement.setInt(7, os.getVeiculo().getId());
            preparedStatement.setInt(8, os.getId());
            int resultado = preparedStatement.executeUpdate();

            if (resultado > 0) {
                // Excluir entradas antigas nas tabelas 'os_peca' e 'os_servico'
                String deleteOSPeca = "DELETE FROM os_peca WHERE id_os = ?";
                preparedStatement = connection.prepareStatement(deleteOSPeca);
                preparedStatement.setInt(1, os.getId());
                preparedStatement.executeUpdate();

                String deleteOSServico = "DELETE FROM os_servico WHERE id_os = ?";
                preparedStatement = connection.prepareStatement(deleteOSServico);
                preparedStatement.setInt(1, os.getId());
                preparedStatement.executeUpdate();

                // Inserir novas entradas na tabela 'os_peca'
                if (os.getPecas() != null && !os.getPecas().isEmpty()) {
                    String insertOSPeca = "INSERT INTO os_peca (id_os, id_peca) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(insertOSPeca);
                    for (Peca peca : os.getPecas()) {
                        if (peca != null && peca.getId() != 0) { // Verificar se a peça e o ID são válidos
                            preparedStatement.setInt(1, os.getId());
                            preparedStatement.setInt(2, peca.getId());
                            preparedStatement.addBatch();
                        }
                    }
                    preparedStatement.executeBatch();
                }

                // Inserir novas entradas na tabela 'os_servico'
                if (os.getServicos() != null && !os.getServicos().isEmpty()) {
                    String insertOSServico = "INSERT INTO os_servico (id_os, id_servico) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(insertOSServico);
                    for (Servico servico : os.getServicos()) {
                        if (servico != null && servico.getId() != 0) { // Verificar se o serviço e o ID são válidos
                            preparedStatement.setInt(1, os.getId());
                            preparedStatement.setInt(2, servico.getId());
                            preparedStatement.addBatch();
                        }
                    }
                    preparedStatement.executeBatch();
                }

                connection.commit(); // Confirmar transação
                logger.log(Level.INFO, "Ordem de serviço alterada com sucesso: {0}", os.getNumero_os());
                return true;
            } else {
                connection.rollback(); // Reverter transação se falhar
                logger.log(Level.SEVERE, "Erro ao atualizar a ordem de serviço: " + os.getNumero_os());
                return false;
            }
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Reverter transação em caso de erro
                }
            } catch (SQLException rollbackEx) {
                logger.log(Level.SEVERE, "Erro ao reverter transação: " + os.getNumero_os(), rollbackEx);
            }
            logger.log(Level.SEVERE, "Erro ao alterar a ordem de serviço: " + os.getNumero_os(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException closeEx) {
                logger.log(Level.SEVERE, "Erro ao fechar os recursos", closeEx);
            }
        }
    }

    public boolean deletar(int idOS) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM os WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idOS);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Ordem de serviço deletada com sucesso: ID {0}", idOS);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar a ordem de serviço com ID: " + idOS, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public OS buscarOS(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM os WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                OS os = new OS();
                os.setId(resultSet.getInt("id"));
                os.setNumero_os(resultSet.getString("numero_os"));
                os.setData_abertura_os(resultSet.getTimestamp("data_abertura_os").toLocalDateTime());
                os.setData_encerramento_os(resultSet.getTimestamp("data_encerramento_os").toLocalDateTime());
                os.setValor_total(resultSet.getBigDecimal("valor_total"));
                // os.setMecanico(new
                // MecanicoDAO().buscarMecanico(resultSet.getInt("mecanico_id")));
                mecanicoController.buscarMecanicoPorId(resultSet.getInt("id_mecanico"));
                clienteController.buscarClientePorId(resultSet.getInt("id_cliente"));
                veiculoController.buscarVeiculoPorId(resultSet.getInt("id_veiculo"));
                pecaController.buscarPecaPorId(resultSet.getInt("id_peca"));
                servicoController.buscarServicoPorId(resultSet.getInt("id_servico"));
                logger.log(Level.INFO, "Ordem de serviço encontrada: {0}", os.getNumero_os());
                return os;
            }
            logger.log(Level.WARNING, "Ordem de serviço com ID {0} não encontrada", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar a ordem de serviço com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<OS> buscarTodas() {
        List<OS> lista = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            // Criar o PreparedStatement com ResultSet que permite rolagem
            String select = "SELECT os.*, os_peca.id_peca, " +
                    "os_servico.id_servico " +
                    "FROM os " +
                    "LEFT JOIN os_peca ON os.id = os_peca.id_os " +
                    "LEFT JOIN os_servico ON os.id = os_servico.id_os";
            PreparedStatement preparedStatement = connection.prepareStatement(select, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                OS os = new OS();
                os.setId(resultSet.getInt("id"));
                os.setNumero_os(resultSet.getString("numero_os"));
                os.setData_abertura_os(resultSet.getTimestamp("data_abertura_os").toLocalDateTime());
                os.setData_encerramento_os(resultSet.getTimestamp("data_encerramento_os").toLocalDateTime());
                os.setValor_total(resultSet.getBigDecimal("valor_total"));

                // Carregar e definir mecânico, cliente e veículo
                os.setMecanico(mecanicoController.buscarMecanicoPorId(resultSet.getInt("id_mecanico")));
                os.setCliente(clienteController.buscarClientePorId(resultSet.getInt("id_cliente")));
                os.setVeiculo(veiculoController.buscarVeiculoPorId(resultSet.getInt("id_veiculo")));

                // Carregar e definir peças e serviços
                List<Peca> pecas = new ArrayList<>();
                List<Servico> servicos = new ArrayList<>();

                int currentOSId = resultSet.getInt("id");
                do {
                    if (resultSet.getInt("id") != currentOSId) {
                        resultSet.previous(); // Retorna ao registro anterior para não pular a próxima OS
                        break;
                    }

                    int idPeca = resultSet.getInt("id_peca");
                    if (!resultSet.wasNull()) {
                        Peca peca = pecaController.buscarPecaPorId(idPeca);
                        pecas.add(peca);
                    }

                    int idServico = resultSet.getInt("id_servico");
                    if (!resultSet.wasNull()) {
                        Servico servico = servicoController.buscarServicoPorId(idServico);
                        servicos.add(servico);
                    }
                } while (resultSet.next());

                os.setPecas(pecas);
                os.setServicos(servicos);

                lista.add(os);
            }

            logger.log(Level.INFO, "Todas as ordens de serviço foram buscadas com sucesso");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todas as ordens de serviço", e);
            Notification notification = new Notification(
                    "Erro ao buscar todas. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return lista;
    }

    public OS buscarOSPorNumero(String numeroOS) {
        OS os = null;

        String sql = "SELECT * FROM os WHERE numero_os = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, numeroOS);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    os = new OS();
                    os.setId(rs.getInt("id"));
                    os.setNumero_os(rs.getString("numero_os"));
                    os.setData_abertura_os(rs.getTimestamp("data_abertura_os").toLocalDateTime());
                    os.setData_encerramento_os(rs.getTimestamp("data_encerramento_os") != null
                            ? rs.getTimestamp("data_encerramento_os").toLocalDateTime()
                            : null);
                    os.setValor_total(rs.getBigDecimal("valor_total"));

                    // Buscar mecânico, cliente e veículo
                    os.setMecanico(buscarMecanicoPorId(rs.getInt("id_mecanico")));
                    os.setCliente(buscarClientePorId(rs.getInt("id_cliente")));
                    os.setVeiculo(buscarVeiculoPorId(rs.getInt("id_veiculo")));

                    // Buscar todas as peças relacionadas à OS
                    List<Peca> pecas = buscarPecasPorOsId(os.getId());
                    os.setPecas(pecas);

                    // Buscar todos os serviços relacionados à OS
                    List<Servico> servicos = buscarServicosPorOsId(os.getId());
                    os.setServicos(servicos);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar OS por número: " + numeroOS, e);
            e.printStackTrace();
        }

        return os;
    }

    private Mecanico buscarMecanicoPorId(int idMecanico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM mecanico WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, idMecanico);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Mecanico mecanico = new Mecanico(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("endereco"),
                        resultSet.getString("cpf"),
                        resultSet.getString("cidade"),
                        resultSet.getString("telefone"));
                logger.log(Level.INFO, "Mecânico encontrado: {0}", mecanico.getNome());
                return mecanico;
            }
            logger.log(Level.WARNING, "Mecânico com ID {0} não encontrado", idMecanico);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o mecânico com ID: " + idMecanico, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }

    }

    private Cliente buscarClientePorId(int idCliente) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM cliente WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, idCliente);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("endereco"),
                        resultSet.getString("cpf"),
                        resultSet.getString("cidade"),
                        resultSet.getString("telefone"));
                logger.log(Level.INFO, "Cliente encontrado: {0}", cliente.getNome());
                return cliente;
            }
            logger.log(Level.WARNING, "Cliente com ID {0} não encontrado", idCliente);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o cliente com ID: " + idCliente, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    private Veiculo buscarVeiculoPorId(int idVeiculo) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM veiculo WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            preparedStatement.setInt(1, idVeiculo);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultSet.getInt("id"));
                veiculo.setDescricao_veiculo(resultSet.getString("descricao_veiculo"));
                veiculo.setPlaca(resultSet.getString("placa"));
                veiculo.setAno_modelo(resultSet.getString("ano_modelo"));

                Cliente cliente = clienteController.buscarClientePorId(resultSet.getInt("id_cliente"));
                veiculo.setCliente(cliente);

                logger.log(Level.INFO, "Veículo encontrado: {0}", veiculo.getDescricao_veiculo());
                return veiculo;
            }
            logger.log(Level.WARNING, "Veículo com ID {0} não encontrado", idVeiculo);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o veículo com ID: " + idVeiculo, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    private List<Peca> buscarPecasPorOsId(int idOs) {
        List<Peca> pecas = new ArrayList<>();

        String sql = "SELECT p.* FROM os_peca op " +
                "JOIN peca p ON op.id_peca = p.id " +
                "WHERE op.id_os = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idOs);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Peca peca = new Peca();
                    peca.setId(resultSet.getInt("id"));
                    peca.setDescricao(resultSet.getString("descricao"));
                    peca.setPreco(resultSet.getBigDecimal("preco"));
                    pecas.add(peca);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar peças para a OS com ID: " + idOs, e);
            Notification notification = new Notification(
                    "Erro ao buscar peças. Verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }

        return pecas;
    }

    private List<Servico> buscarServicosPorOsId(int idOs) {
        List<Servico> servicos = new ArrayList<>();

        String sql = "SELECT s.* FROM os_servico os " +
                "JOIN servico s ON os.id_servico = s.id " +
                "WHERE os.id_os = ?";

        try (Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, idOs);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Servico servico = new Servico();
                    servico.setId(resultSet.getInt("id"));
                    servico.setDescricao_servico(resultSet.getString("descricao_servico"));
                    servico.setValor_servico(resultSet.getBigDecimal("valor_servico"));
                    servicos.add(servico);
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviços para a OS com ID: " + idOs, e);
            Notification notification = new Notification(
                    "Erro ao buscar serviços. Verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }

        return servicos;
    }

}
