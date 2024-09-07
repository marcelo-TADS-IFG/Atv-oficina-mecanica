package oficina.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import oficina.models.Cliente;

public class ClienteDAO {

    private static final Logger logger = Logger.getLogger(ClienteDAO.class.getName());

    public boolean salvar(Cliente cliente) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO cliente (nome, endereco, cpf, cidade, telefone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, cliente.getNome());
            preparedStatement.setString(2, cliente.getEndereco());
            preparedStatement.setString(3, cliente.getCpf());
            preparedStatement.setString(4, cliente.getCidade());
            preparedStatement.setString(5, cliente.getTelefone());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Cliente salvo com sucesso: {0}", cliente.getNome());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar o cliente: " + cliente.getNome(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Cliente cliente) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String update = "UPDATE cliente SET nome = ?, endereco = ?, cpf = ?, cidade = ?, telefone = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, cliente.getNome());
            preparedStatement.setString(2, cliente.getEndereco());
            preparedStatement.setString(3, cliente.getCpf());
            preparedStatement.setString(4, cliente.getCidade());
            preparedStatement.setString(5, cliente.getTelefone());
            preparedStatement.setInt(6, cliente.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Cliente alterado com sucesso: {0}", cliente.getNome());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar o cliente: " + cliente.getNome(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idCliente) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM cliente WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idCliente);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Cliente deletado com sucesso: ID {0}", idCliente);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar o cliente com ID: " + idCliente, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Cliente buscarCliente(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM cliente WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, id);
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
            logger.log(Level.WARNING, "Cliente com ID {0} não encontrado", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o cliente com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE LOWER(TRIM(nome)) LIKE LOWER(TRIM(?))";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome.trim().toLowerCase() + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("endereco"),
                        resultSet.getString("cpf"),
                        resultSet.getString("cidade"),
                        resultSet.getString("telefone"));
                clientes.add(cliente);
            }
            logger.log(Level.INFO, "Clientes encontrados com sucesso para o nome: {0}", nome);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar clientes por nome: " + nome, e);
            Notification notification = new Notification(
                    "Erro ao buscar clientes. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return clientes;
    }

    public List<Cliente> buscarTodos() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM cliente";
            List<Cliente> lista = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getInt("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("endereco"),
                        resultSet.getString("cpf"),
                        resultSet.getString("cidade"),
                        resultSet.getString("telefone"));
                lista.add(cliente);
            }
            logger.log(Level.INFO, "Todos os clientes foram buscados com sucesso");
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os clientes", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }
}
