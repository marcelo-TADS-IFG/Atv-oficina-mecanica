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

import oficina.models.Mecanico;

public class MecanicoDAO {

    private static final Logger logger = Logger.getLogger(MecanicoDAO.class.getName());

    public boolean salvar(Mecanico mecanico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO mecanico (nome, endereco, cpf, cidade, telefone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, mecanico.getNome());
            preparedStatement.setString(2, mecanico.getEndereco());
            preparedStatement.setString(3, mecanico.getCpf());
            preparedStatement.setString(4, mecanico.getCidade());
            preparedStatement.setString(5, mecanico.getTelefone());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Mecânico salvo com sucesso: {0}", mecanico.getNome());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar o mecânico: " + mecanico.getNome(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Mecanico mecanico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String update = "UPDATE mecanico SET nome = ?, endereco = ?, cpf = ?, cidade = ?, telefone = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, mecanico.getNome());
            preparedStatement.setString(2, mecanico.getEndereco());
            preparedStatement.setString(3, mecanico.getCpf());
            preparedStatement.setString(4, mecanico.getCidade());
            preparedStatement.setString(5, mecanico.getTelefone());
            preparedStatement.setInt(6, mecanico.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Mecânico alterado com sucesso: {0}", mecanico.getNome());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar o mecânico: " + mecanico.getNome(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idMecanico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM mecanico WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idMecanico);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Mecânico deletado com sucesso: ID {0}", idMecanico);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar o mecânico com ID: " + idMecanico, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Mecanico buscarMecanico(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM mecanico WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Mecanico mecanico = new Mecanico(
                    resultSet.getInt("id"),
                    resultSet.getString("nome"),
                    resultSet.getString("endereco"),
                    resultSet.getString("cpf"),
                    resultSet.getString("cidade"),
                    resultSet.getString("telefone")
                );
                logger.log(Level.INFO, "Mecânico encontrado: {0}", mecanico.getNome());
                return mecanico;
            }
            logger.log(Level.WARNING, "Mecânico com ID {0} não encontrado", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o mecânico com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Mecanico> buscarMecanicosPorNome(String nome) {
        List<Mecanico> mecanicos = new ArrayList<>();
        String sql = "SELECT * FROM mecanico WHERE nome LIKE ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Mecanico mecanico = new Mecanico(
                    resultSet.getInt("id"),
                    resultSet.getString("nome"),
                    resultSet.getString("endereco"),
                    resultSet.getString("cpf"),
                    resultSet.getString("cidade"),
                    resultSet.getString("telefone")
                );
                mecanicos.add(mecanico);
            }
            logger.log(Level.INFO, "Mecânicos encontrados com sucesso para o nome: {0}", nome);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mecânicos por nome: " + nome, e);
            Notification notification = new Notification(
                    "Erro ao buscar mecânicos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return mecanicos;
    }

    public List<Mecanico> buscarTodos() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String select = "SELECT * FROM mecanico";
            List<Mecanico> lista = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(select);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Mecanico mecanico = new Mecanico(
                    resultSet.getInt("id"),
                    resultSet.getString("nome"),
                    resultSet.getString("endereco"),
                    resultSet.getString("cpf"),
                    resultSet.getString("cidade"),
                    resultSet.getString("telefone")
                );
                lista.add(mecanico);
            }
            logger.log(Level.INFO, "Todos os mecânicos foram buscados com sucesso");
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os mecânicos", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }
}
