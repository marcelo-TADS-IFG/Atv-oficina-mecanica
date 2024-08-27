package oficina.controllers;

import java.util.List;

import oficina.dao.ServicoDAO;
import oficina.models.Servico;

public class ServicoController {

    private ServicoDAO servicoDAO;

    public ServicoController() {
        this.servicoDAO = new ServicoDAO();
    }

    public boolean adicionarServico(Servico servico) {
        return servicoDAO.salvar(servico);
    }

    public boolean atualizarServico(Servico servico) {
        return servicoDAO.alterar(servico);
    }

    public boolean deletarServico(int id) {
        return servicoDAO.deletar(id);
    }

    public Servico buscarServicoPorId(int id) {
        return servicoDAO.buscarServico(id);
    }

    public List<Servico> buscarServicosPorDescricao(String descricao) {
        return servicoDAO.buscarServicosPorDescricao(descricao);
    }

    public List<Servico> buscarTodosServicos() {
        return servicoDAO.buscarTodos();
    }
}
