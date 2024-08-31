package oficina.models;

import java.math.BigDecimal;

public class Servico {
    private int id;
    private String descricao_servico;
    private BigDecimal valor_servico;
    private int quantidade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao_servico() {
        return descricao_servico;
    }

    public void setDescricao_servico(String descricao_servico) {
        this.descricao_servico = descricao_servico;
    }

    public BigDecimal getValor_servico() {
        return valor_servico;
    }

    public void setValor_servico(BigDecimal valor_servico) {
        this.valor_servico = valor_servico;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

}
