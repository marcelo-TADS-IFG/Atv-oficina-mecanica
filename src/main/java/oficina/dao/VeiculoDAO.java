package oficina.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import oficina.models.Veiculo;
import oficina.models.Cliente;
import oficina.controllers.ClienteController;

public class VeiculoDAO {

    private static final Logger logger = Logger.getLogger(VeiculoDAO.class.getName());
    private ClienteDAO clienteDAO = new ClienteDAO();
    private ClienteController clienteController = new ClienteController();

    public boolean salvar(Veiculo veiculo) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO veiculo (descricao_veiculo, placa, ano_modelo, id_cliente) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, veiculo.getDescricao_veiculo());
            preparedStatement.setString(2, veiculo.getPlaca());
            preparedStatement.setString(3, veiculo.getAno_modelo());
            preparedStatement.setInt(4, veiculo.getCliente().getId());

            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Veículo salvo com sucesso: {0}", veiculo.getDescricao_veiculo());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar o veículo: " + veiculo.getDescricao_veiculo(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Veiculo veiculo) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String update = "UPDATE veiculo SET descricao_veiculo = ?, placa = ?, ano_modelo = ?, id_cliente = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, veiculo.getDescricao_veiculo());
            preparedStatement.setString(2, veiculo.getPlaca());
            preparedStatement.setString(3, veiculo.getAno_modelo());
            preparedStatement.setInt(4, veiculo.getCliente().getId());
            preparedStatement.setInt(5, veiculo.getId());

            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Veículo alterado com sucesso: {0}", veiculo.getDescricao_veiculo());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar o veículo: " + veiculo.getDescricao_veiculo(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idVeiculo) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM veiculo WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idVeiculo);

            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Veículo deletado com sucesso: ID {0}", idVeiculo);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar o veículo com ID: " + idVeiculo, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Veiculo buscarVeiculo(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM veiculo WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            preparedStatement.setInt(1, id);
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
            logger.log(Level.WARNING, "Veículo com ID {0} não encontrado", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o veículo com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Veiculo> buscarPorDescricao(String descricao) {
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String query = "SELECT * FROM veiculo WHERE descricao_veiculo LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + descricao + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultSet.getInt("id"));
                veiculo.setDescricao_veiculo(resultSet.getString("descricao_veiculo"));
                veiculo.setPlaca(resultSet.getString("placa"));
                veiculo.setAno_modelo(resultSet.getString("ano_modelo"));

                Cliente cliente = clienteDAO.buscarCliente(resultSet.getInt("id_cliente"));
                veiculo.setCliente(cliente);

                veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar veículos por descrição: " + descricao, e);
        }
        return veiculos;
    }

    public List<Veiculo> buscarTodos() {
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM veiculo";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setId(resultSet.getInt("id"));
                veiculo.setDescricao_veiculo(resultSet.getString("descricao_veiculo"));
                veiculo.setPlaca(resultSet.getString("placa"));
                veiculo.setAno_modelo(resultSet.getString("ano_modelo"));

                Cliente cliente = clienteController.buscarClientePorId(resultSet.getInt("id_cliente"));
                veiculo.setCliente(cliente);

                veiculos.add(veiculo);
            }
            logger.log(Level.INFO, "Todos os veículos foram buscados com sucesso");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os veículos", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return veiculos;
    }
}
