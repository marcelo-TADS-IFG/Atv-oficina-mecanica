package oficina.controllers;

import java.util.List;

import oficina.dao.VeiculoDAO;
import oficina.models.Veiculo;

public class VeiculoController {

    private VeiculoDAO veiculoDAO;

    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    public boolean adicionarVeiculo(Veiculo veiculo) {
        return veiculoDAO.salvar(veiculo);
    }

    public boolean atualizarVeiculo(Veiculo veiculo) {
        return veiculoDAO.alterar(veiculo);
    }

    public boolean deletarVeiculo(int id) {
        return veiculoDAO.deletar(id);
    }

    public Veiculo buscarVeiculoPorId(int id) {
        return veiculoDAO.buscarVeiculo(id);
    }

    public List<Veiculo> buscarVeiculosPorDescricao(String descricao) {
        return veiculoDAO.buscarPorDescricao(descricao);
    }

    public List<Veiculo> buscarTodosVeiculos() {
        return veiculoDAO.buscarTodos();
    }
}
