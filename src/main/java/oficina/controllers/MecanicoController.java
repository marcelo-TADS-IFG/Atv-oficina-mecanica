package oficina.controllers;

import java.util.List;

import oficina.dao.MecanicoDAO;
import oficina.models.Mecanico;

public class MecanicoController {

    private MecanicoDAO mecanicoDAO;

    public MecanicoController() {
        this.mecanicoDAO = new MecanicoDAO();
    }

    public boolean adicionarMecanico(Mecanico mecanico) {
        return mecanicoDAO.salvar(mecanico);
    }

    public boolean atualizarMecanico(Mecanico mecanico) {
        return mecanicoDAO.alterar(mecanico);
    }

    public boolean deletarMecanico(int id) {
        return mecanicoDAO.deletar(id);
    }

    public Mecanico buscarMecanicoPorId(int id) {
        return mecanicoDAO.buscarMecanico(id);
    }

    public List<Mecanico> buscarMecanicosPorNome(String nome) {
        return mecanicoDAO.buscarMecanicosPorNome(nome);
    }

    public List<Mecanico> buscarTodosMecanicos() {
        return mecanicoDAO.buscarTodos();
    }
}
