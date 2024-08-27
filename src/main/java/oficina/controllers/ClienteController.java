package oficina.controllers;

import java.util.List;

import oficina.dao.ClienteDAO;
import oficina.models.Cliente;

public class ClienteController {

    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public boolean adicionarCliente(Cliente cliente) {
        return clienteDAO.salvar(cliente);
    }

    public boolean atualizarCliente(Cliente cliente) {
        return clienteDAO.alterar(cliente);
    }

    public boolean deletarCliente(int id) {
        return clienteDAO.deletar(id);
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.buscarCliente(id);
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.buscarClientesPorNome(nome);
    }

    public List<Cliente> buscarTodosClientes() {
        return clienteDAO.buscarTodos();
    }
}
