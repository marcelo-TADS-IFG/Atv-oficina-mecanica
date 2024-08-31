package oficina.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OS {
    private int id;
    private String numero_os;
    private LocalDateTime data_abertura_os;
    private LocalDateTime data_encerramento_os;
    private BigDecimal valor_total;
    private Mecanico mecanico;
    private Cliente cliente;
    private Veiculo veiculo;
    private Peca peca;
    private Servico servico;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero_os() {
        return numero_os;
    }

    public void setNumero_os(String numero_os) {
        this.numero_os = numero_os;
    }

    public LocalDateTime getData_abertura_os() {
        return data_abertura_os;
    }

    public void setData_abertura_os(LocalDateTime data_abertura_os) {
        this.data_abertura_os = data_abertura_os;
    }

    public LocalDateTime getData_encerramento_os() {
        return data_encerramento_os;
    }

    public void setData_encerramento_os(LocalDateTime data_encerramento_os) {
        this.data_encerramento_os = data_encerramento_os;
    }

    public BigDecimal getValor_total() {
        return valor_total;
    }

    public void setValor_total(BigDecimal valor_total) {
        this.valor_total = valor_total;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public void setMecanico(Mecanico mecanico) {
        this.mecanico = mecanico;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Object getPecas() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPecas'");
    }

    public Object getServicos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getServicos'");
    }

}
