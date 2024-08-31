package oficina.controllers;

import java.util.List;

import oficina.dao.OSDAO;
import oficina.models.OS;

public class OSController {

    private OSDAO osDAO;

    public OSController() {
        this.osDAO = new OSDAO();
    }

    public boolean adicionarOS(OS os) {
        return osDAO.salvar(os);
    }

    public boolean atualizarOS(OS os) {
        return osDAO.alterarOS(os);
    }

    public boolean deletarOS(int id) {
        return osDAO.deletar(id);
    }

    public OS buscarOSPorId(int id) {
        return osDAO.buscarOS(id);
    }

    /*
     * public List<OS> buscarOSPorNumero(String numero_os) {
     * return osDAO.buscarOS(numero_os);
     * }
     */

    public List<OS> buscarTodasOS() {
        return osDAO.buscarTodas();
    }
}
