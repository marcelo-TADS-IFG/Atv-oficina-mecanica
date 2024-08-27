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

import oficina.models.Marca;

public class MarcaDAO {

    private static final Logger logger = Logger.getLogger(MarcaDAO.class.getName());

    public boolean salvar(Marca marca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO marca (nome_marca) values (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, marca.getNome_marca());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Marca salva com sucesso: {0}", marca.getNome_marca());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar a marca: " + marca.getNome_marca(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Marca marca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String alterar = "UPDATE marca SET nome_marca = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(alterar);
            preparedStatement.setString(1, marca.getNome_marca());
            preparedStatement.setInt(2, marca.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Marca alterada com sucesso: {0}", marca.getNome_marca());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar a marca: " + marca.getNome_marca(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idMarca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM marca WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idMarca);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Marca deletada com sucesso: ID {0}", idMarca);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar a marca com ID: " + idMarca, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Marca buscarMarca(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM marca WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Marca marca = new Marca();
                marca.setId(resultSet.getInt("id"));
                marca.setNome_marca(resultSet.getString("nome_marca"));
                logger.log(Level.INFO, "Marca encontrada: {0}", marca.getNome_marca());
                return marca;
            }
            logger.log(Level.WARNING, "Marca com ID {0} não encontrada", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar a marca com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Marca> buscarMarcasPorNome(String nome) {
        List<Marca> marcas = new ArrayList<>();
        String sql = "SELECT * FROM marca WHERE nome_marca LIKE ?";
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nome + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Marca marca = new Marca();
                marca.setId(resultSet.getInt("id"));
                marca.setNome_marca(resultSet.getString("nome_marca"));
                marcas.add(marca);
            }
            logger.log(Level.INFO, "Marcas encontradas com sucesso para o nome: {0}", nome);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar marcas por nome: " + nome, e);
            Notification notification = new Notification(
                    "Erro ao buscar marcas. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
        return marcas;
    }

    public List<Marca> buscarTodas() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM marca";
            List<Marca> lista = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Marca marca = new Marca();
                marca.setId(resultSet.getInt("id"));
                marca.setNome_marca(resultSet.getString("nome_marca"));
                lista.add(marca);
            }
            logger.log(Level.INFO, "Todas as marcas foram buscadas com sucesso");
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todas as marcas", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }
}
