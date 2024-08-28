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

import oficina.models.Peca;
import oficina.models.Marca;
import oficina.dao.MarcaDAO;
import oficina.controllers.MarcaController;

public class PecaDAO {

    private MarcaDAO marcaDAO = new MarcaDAO();

    private static final Logger logger = Logger.getLogger(PecaDAO.class.getName());

    private MarcaController marcaController = new MarcaController();

    public boolean salvar(Peca peca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String insert = "INSERT INTO peca (descricao, preco, id_marca) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, peca.getDescricao()); // Aqui deve ser a descrição
            preparedStatement.setBigDecimal(2, peca.getPreco()); // Aqui deve ser o preço
            preparedStatement.setInt(3, peca.getMarca().getId()); // Aqui deve ser o ID da marca

            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Peça salva com sucesso: {0}", peca.getDescricao());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar a peça: " + peca.getDescricao(), e);
            Notification notification = new Notification(
                    "Erro ao salvar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean alterar(Peca peca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String update = "UPDATE peca SET descricao = ?, preco = ?, id_marca = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(update);
            preparedStatement.setString(1, peca.getDescricao());
            preparedStatement.setBigDecimal(2, peca.getPreco());
            preparedStatement.setInt(3, peca.getMarca().getId()); // Usar o ID da marca
            preparedStatement.setInt(4, peca.getId());
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Peça alterada com sucesso: {0}", peca.getDescricao());
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao alterar a peça: " + peca.getDescricao(), e);
            Notification notification = new Notification(
                    "Erro ao alterar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public boolean deletar(int idPeca) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String delete = "DELETE FROM peca WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, idPeca);
            int resultado = preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Peça deletada com sucesso: ID {0}", idPeca);
            return resultado > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar a peça com ID: " + idPeca, e);
            Notification notification = new Notification(
                    "Erro ao deletar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
    }

    public Peca buscarPeca(int id) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM peca WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Peca peca = new Peca();
                peca.setId(resultSet.getInt("id"));
                peca.setDescricao(resultSet.getString("descricao"));
                peca.setPreco(resultSet.getBigDecimal("preco"));

                marcaController.buscarMarcaPorId(resultSet.getInt("id_marca"));

                // Buscar a marca correspondente
                /*
                 * Marca marca = new Marca();
                 * marca.setId(resultSet.getInt("id_marca"));
                 * peca.setMarca(marca);
                 */

                logger.log(Level.INFO, "Peça encontrada: {0}", peca.getDescricao());
                return peca;
            }
            logger.log(Level.WARNING, "Peça com ID {0} não encontrada", id);
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar a peça com ID: " + id, e);
            Notification notification = new Notification(
                    "Erro ao buscar. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

    public List<Peca> buscarPorNome(String nome) {
        List<Peca> pecas = new ArrayList<>();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String query = "SELECT * FROM peca WHERE descricao LIKE ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + nome + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Peca peca = new Peca();
                peca.setId(resultSet.getInt("id"));
                peca.setDescricao(resultSet.getString("descricao"));
                peca.setPreco(resultSet.getBigDecimal("preco"));
                // Assumindo que você tenha um método para buscar a marca por ID
                Marca marca = marcaDAO.buscarMarca(resultSet.getInt("id_marca"));
                peca.setMarca(marca);
                pecas.add(peca);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar peças por nome: " + nome, e);
        }
        return pecas;
    }

    public List<Peca> buscarTodas() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String busca = "SELECT * FROM peca";
            List<Peca> lista = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement(busca);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Peca peca = new Peca();
                peca.setId(resultSet.getInt("id"));
                peca.setDescricao(resultSet.getString("descricao"));
                peca.setPreco(resultSet.getBigDecimal("preco"));
                peca.setMarca(marcaController.buscarMarcaPorId(resultSet.getInt("id_marca")));

                lista.add(peca);
            }
            logger.log(Level.INFO, "Todas as peças foram buscadas com sucesso");
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todas as peças", e);
            Notification notification = new Notification(
                    "Erro ao buscar todos. Por favor, verifique a mensagem a seguir: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return null;
        }
    }

}
