package oficina.controllers;

import java.math.BigDecimal;
import java.util.List;

import oficina.dao.PecaDAO;
import oficina.models.Peca;
import oficina.models.Marca;

public class PecaController {

    private PecaDAO pecaDAO;

    public PecaController() {
        this.pecaDAO = new PecaDAO();
    }

    public boolean salvarPeca(String descricao, BigDecimal preco, Marca marca) {
        Peca peca = new Peca();
        peca.setDescricao(descricao);
        peca.setPreco(preco);
        peca.setMarca(marca);
        return pecaDAO.salvar(peca);
    }

    public boolean alterarPeca(int id, String descricao, BigDecimal preco, Marca marca) {
        Peca peca = new Peca();
        peca.setId(id);
        peca.setDescricao(descricao);
        peca.setPreco(preco);
        peca.setMarca(marca);
        return pecaDAO.alterar(peca);
    }

    public boolean deletarPeca(int id) {
        return pecaDAO.deletar(id);
    }

    public Peca buscarPecaPorId(int id) {
        return pecaDAO.buscarPeca(id);
    }

    public List<Peca> buscarPecaPorNome(String nome) {
        return pecaDAO.buscarPorNome(nome);
    }

    public List<Peca> listarTodasPecas() {
        return pecaDAO.buscarTodas();
    }
}
