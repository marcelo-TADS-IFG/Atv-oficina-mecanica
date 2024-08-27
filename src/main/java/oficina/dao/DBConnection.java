package oficina.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static DBConnection instance;
    private Connection conn;
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    private String url = "jdbc:mysql://localhost:3306/oficina_mecanica?useSSL=false&serverTimezone=UTC";
    private String usuario = "root";
    private String senha = "Ahorsewithnoname1992";

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, usuario, senha);
        } catch (ClassNotFoundException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao conectar ao banco de dados.", ex);
            throw new SQLException("Erro ao conectar ao banco de dados.", ex);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }

        return instance;

    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar a conexão com o banco de dados.", ex);
        }
    }
}
