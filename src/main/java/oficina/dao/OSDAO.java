package oficina.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import oficina.models.OS;
import oficina.models.Mecanico;
import oficina.models.Cliente;
import oficina.models.Veiculo;
import oficina.models.Peca;
import oficina.models.Servico;

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
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO os (numero_os, data_abertura_os, data_encerramento_os, valor_total, mecanico_id, cliente_id, veiculo_id, peca_id, servico_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, os.getNumero_os());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(os.getData_abertura_os()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(os.getData_encerramento_os()));
            preparedStatement.setBigDecimal(4, os.getValor_total());
            preparedStatement.setInt(5, os.getMecanico().getId());
            preparedStatement.setInt(6, os.getCliente().getId());
            preparedStatement.setInt(7, os.getVeiculo().getId());
            preparedStatement.setInt(8, os.getPeca().getId());
            preparedStatement.setInt(9, os.getServico().getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Ordem de serviço salva com sucesso: {0}", os.getNumero_os());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar a ordem de serviço: " + os.getNumero_os(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    /*public boolean salvar(OS os) {
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
                        String insertOSPeca = "INSERT INTO os_peca (id_os, id_peca, quantidade) VALUES (?, ?, ?)";
                        preparedStatement = connection.prepareStatement(insertOSPeca);
                        for (Peca peca : os.getPecas()) {
                            preparedStatement.setInt(1, idOS);
                            preparedStatement.setInt(2, peca.getId());
                            preparedStatement.setInt(3, peca.getQuantidade()); // Assumindo que 'Peca' tem um método 'getQuantidade()'
                            preparedStatement.addBatch();
                        }
                        preparedStatement.executeBatch();
                    }
    
                    // Inserir na tabela 'os_servico'
                    if (os.getServicos() != null) {
                        String insertOSServico = "INSERT INTO os_servico (id_os, id_servico, quantidade) VALUES (?, ?, ?)";
                        preparedStatement = connection.prepareStatement(insertOSServico);
                        for (Servico servico : os.getServicos()) {
                            preparedStatement.setInt(1, idOS);
                            preparedStatement.setInt(2, servico.getId());
                            preparedStatement.setInt(3, servico.getQuantidade()); // Assumindo que 'Servico' tem um método 'getQuantidade()'
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
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException closeEx) {
                logger.log(Level.SEVERE, "Erro ao fechar os recursos", closeEx);
            }
        }
    }*/
    

    public boolean alterar(OS os) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String update = "UPDATE os SET numero_os = ?, data_abertura_os = ?, data_encerramento_os = ?, valor_total = ?, mecanico_id = ?, cliente_id = ?, veiculo_id = ?, peca_id = ?, servico_id = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, os.getNumero_os());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(os.getData_abertura_os()));
            preparedStatement.setTimestamp(3, java.sql.Timestamp.valueOf(os.getData_encerramento_os()));
            preparedStatement.setBigDecimal(4, os.getValor_total());
            preparedStatement.setInt(5, os.getMecanico().getId());
            preparedStatement.setInt(6, os.getCliente().getId());
            preparedStatement.setInt(7, os.getVeiculo().getId());
            preparedStatement.setInt(8, os.getPeca().getId());
            preparedStatement.setInt(9, os.getServico().getId());
            preparedStatement.setInt(10, os.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Ordem de serviço alterada com sucesso: {0}", os.getNumero_os());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar a ordem de serviço: " + os.getNumero_os(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
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
                //os.setMecanico(new MecanicoDAO().buscarMecanico(resultSet.getInt("mecanico_id")));
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
            String select = "SELECT * FROM os";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                OS os = new OS();
                os.setId(resultSet.getInt("id"));
                os.setNumero_os(resultSet.getString("numero_os"));
                os.setData_abertura_os(resultSet.getTimestamp("data_abertura_os").toLocalDateTime());
                os.setData_encerramento_os(resultSet.getTimestamp("data_encerramento_os").toLocalDateTime());
                os.setValor_total(resultSet.getBigDecimal("valor_total"));
                //os.setMecanico(new MecanicoDAO().buscarMecanico(resultSet.getInt("mecanico_id")));
                mecanicoController.buscarMecanicoPorId(resultSet.getInt("id_mecanico")); 
                clienteController.buscarClientePorId(resultSet.getInt("id_cliente"));    
                veiculoController.buscarVeiculoPorId(resultSet.getInt("id_veiculo"));          
                pecaController.buscarPecaPorId(resultSet.getInt("id_peca"));     
                servicoController.buscarServicoPorId(resultSet.getInt("id_servico"));
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
}
