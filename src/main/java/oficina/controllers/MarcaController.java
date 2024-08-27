package oficina.controllers;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import oficina.dao.MarcaDAO;
import oficina.models.Marca;

public class MarcaController {

    private static final Logger logger = Logger.getLogger(MarcaController.class.getName());
    private MarcaDAO marcaDAO;

    public MarcaController() {
        this.marcaDAO = new MarcaDAO();
    }

    public boolean salvarMarca(String nomeMarca) {
        Marca marca = new Marca();
        marca.setNome_marca(nomeMarca);
        boolean resultado = marcaDAO.salvar(marca);
        if (resultado) {
            logger.log(Level.INFO, "Marca salva com sucesso: {0}", nomeMarca);
        } else {
            logger.log(Level.SEVERE, "Erro ao salvar a marca: {0}", nomeMarca);
        }
        return resultado;
    }

    public boolean alterarMarca(int id, String nomeMarca) {
        Marca marca = new Marca();
        marca.setId(id);
        marca.setNome_marca(nomeMarca);
        boolean resultado = marcaDAO.alterar(marca);
        if (resultado) {
            logger.log(Level.INFO, "Marca alterada com sucesso: ID {0}, Nome: {1}", new Object[] { id, nomeMarca });
        } else {
            logger.log(Level.SEVERE, "Erro ao alterar a marca: ID {0}, Nome: {1}", new Object[] { id, nomeMarca });
        }
        return resultado;
    }

    public boolean deletarMarca(int id) {
        boolean resultado = marcaDAO.deletar(id);
        if (resultado) {
            logger.log(Level.INFO, "Marca deletada com sucesso: ID {0}", id);
        } else {
            logger.log(Level.SEVERE, "Erro ao deletar a marca com ID: {0}", id);
        }
        return resultado;
    }

    public Marca buscarMarcaPorId(int id) {
        Marca marca = marcaDAO.buscarMarca(id);
        if (marca != null) {
            logger.log(Level.INFO, "Marca encontrada: ID {0}, Nome: {1}", new Object[] { id, marca.getNome_marca() });
        } else {
            logger.log(Level.WARNING, "Marca não encontrada para o ID: {0}", id);
        }
        return marca;
    }

    public List<Marca> listarTodasMarcas() {
        List<Marca> marcas = marcaDAO.buscarTodas();
        if (marcas != null && !marcas.isEmpty()) {
            logger.log(Level.INFO, "Todas as marcas foram listadas com sucesso");
        } else {
            logger.log(Level.WARNING, "Nenhuma marca encontrada");
        }
        return marcas;
    }

    public List<Marca> buscarMarcasPorNome(String nome) {
        List<Marca> marcas = marcaDAO.buscarMarcasPorNome(nome);
        if (marcas != null && !marcas.isEmpty()) {
            logger.log(Level.INFO, "Marcas encontradas com sucesso para o nome: {0}", nome);
        } else {
            logger.log(Level.WARNING, "Nenhuma marca encontrada para o nome: {0}", nome);
        }
        return marcas;
    }

}
