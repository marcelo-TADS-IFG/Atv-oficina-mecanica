package oficina.models;

public class Mecanico extends Pessoa {

    public Mecanico(int id, String nome, String endereco, String cpf, String cidade, String telefone) {
        super(); // Chama o construtor da classe Pessoa, se necessário
        setId(id);
        setNome(nome);
        setEndereco(endereco);
        setCpf(cpf);
        setCidade(cidade);
        setTelefone(telefone);

    }

}
