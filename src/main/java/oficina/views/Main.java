package oficina.views;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import oficina.controllers.OSController;
import oficina.models.Cliente;
import oficina.models.Mecanico;
import oficina.models.OS;
import oficina.models.Peca;
import oficina.models.Servico;
import oficina.models.Veiculo;

public class Main {
    public static void main(String[] args) {
        // Criando uma instância de OS para testar
        OS os = new OS();
        os.setId(28); // Defina um ID existente no banco de dados para testar a alteração
        os.setNumero_os("OS-001");
        os.setData_abertura_os(LocalDateTime.now());
        os.setData_encerramento_os(LocalDateTime.now().plusDays(3));
        os.setValor_total(new BigDecimal("1900.00"));
        
        // Defina o mecânico, cliente e veículo com IDs válidos
        Mecanico mecanico = new Mecanico(0, null, null, null, null, null);
        mecanico.setId(3); // ID existente no banco
        os.setMecanico(mecanico);

        Cliente cliente = new Cliente(0, null, null, null, null, null);
        cliente.setId(10); // ID existente no banco
        os.setCliente(cliente);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(5); // ID existente no banco
        os.setVeiculo(veiculo);

        // Adicionando peças para testar
        List<Peca> pecas = new ArrayList<>();
        Peca peca1 = new Peca();
        peca1.setId(5); // Certifique-se de que esta peça exista no banco de dados
        pecas.add(peca1);

        Peca peca2 = new Peca();
        peca2.setId(7); // Certifique-se de que esta peça exista no banco de dados
        pecas.add(peca2);

        os.setPecas(pecas);

        // Adicionando serviços para testar
        List<Servico> servicos = new ArrayList<>();
        Servico servico1 = new Servico();
        servico1.setId(1); // Certifique-se de que este serviço exista no banco de dados
        Servico servico2 = new Servico();
        servico2.setId(4);
        servicos.add(servico1);
        servicos.add(servico2);

        os.setServicos(servicos);

        // Testando a alteração da OS no banco de dados
        OSController osController = new OSController();
        boolean sucesso = osController.atualizarOS(os);

        if (sucesso) {
            System.out.println("Ordem de serviço alterada com sucesso!");
        } else {
            System.out.println("Erro ao alterar a ordem de serviço.");
        }
    }
}

