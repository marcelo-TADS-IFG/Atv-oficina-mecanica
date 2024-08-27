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

import oficina.models.Servico;

public class ServicoDAO {

    private static final Logger logger = Logger.getLogger(ServicoDAO.class.getName());

    public boolean salvar(Servico servico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO servico (descricao_servico, valor_servico) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, servico.getDescricao_servico());
            preparedStatement.setBigDecimal(2, servico.getValor_servico());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Serviço salvo com sucesso: {0}", servico.getDescricao_servico());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar o serviço: " + servico.getDescricao_servico(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Servico servico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String alterar = "UPDATE servico SET descricao_servico = ?, valor_servico = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(alterar);
            preparedStatement.setString(1, servico.getDescricao_servico());
            preparedStatement.setBigDecimal(2, servico.getValor_servico());
            preparedStatement.setInt(3, servico.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Serviço alterado com sucesso: {0}", servico.getDescricao_servico());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar o serviço: " + servico.getDescricao_servico(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idServico) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM servico WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idServico);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Serviço deletado com sucesso: ID {0}", idServico);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar o serviço com ID: " + idServico, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Servico buscarServico(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM servico WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Servico servico = new Servico();
                servico.setId(resultSet.getInt("id"));
                servico.setDescricao_servico(resultSet.getString("descricao_servico"));
                servico.setValor_servico(resultSet.getBigDecimal("valor_servico"));
                logger.log(Level.INFO, "Serviço encontrado: {0}", servico.getDescricao_servico());
                return servico;
            }
            logger.log(Level.WARNING, "Serviço com ID {0} não encontrado", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar o serviço com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Servico> buscarServicosPorDescricao(String descricao) {
        List<Servico> servicos = new ArrayList<>();
        String sql = "SELECT * FROM servico WHERE descricao_servico LIKE ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, descricao + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Servico servico = new Servico();
                servico.setId(resultSet.getInt("id"));
                servico.setDescricao_servico(resultSet.getString("descricao_servico"));
                servico.setValor_servico(resultSet.getBigDecimal("valor_servico"));
                servicos.add(servico);
            }
            logger.log(Level.INFO, "Serviços encontrados com sucesso para a descrição: {0}", descricao);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviços por descrição: " + descricao, e);
            Notification notification = new Notification(
                    "Erro ao buscar serviços. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return servicos;
    }

    public List<Servico> buscarTodos() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM servico";
            List<Servico> lista = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Servico servico = new Servico();
                servico.setId(resultSet.getInt("id"));
                servico.setDescricao_servico(resultSet.getString("descricao_servico"));
                servico.setValor_servico(resultSet.getBigDecimal("valor_servico"));
                lista.add(servico);
            }
            logger.log(Level.INFO, "Todos os serviços foram buscados com sucesso");
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os serviços", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }
}
