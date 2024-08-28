package oficina.models;

public class Veiculo {
    private int id;
    private String descricao_veiculo;
    private String placa;
    private String ano_modelo;
    private Cliente cliente;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao_veiculo() {
        return descricao_veiculo;
    }

    public void setDescricao_veiculo(String descricao_veiculo) {
        this.descricao_veiculo = descricao_veiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getAno_modelo() {
        return ano_modelo;
    }

    public void setAno_modelo(String ano_modelo) {
        this.ano_modelo = ano_modelo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

}
