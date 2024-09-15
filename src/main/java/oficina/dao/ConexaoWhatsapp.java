package oficina.dao;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import oficina.models.Cliente;
import oficina.models.OS;

public class ConexaoWhatsapp {
  public static final String ACCOUNT_SID = "--------------------------------";
  public static final String AUTH_TOKEN = "2cf5267b05946c79296934d6d3c02ec8";

  public static void EnviarMensagem(Cliente cliente, OS os) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

    // Garante que o número do cliente está formatado corretamente para o Twilio
    String numeroCliente = "whatsapp:+55" + cliente.getTelefone();
    
    try {
      Message message = Message.creator(
          new com.twilio.type.PhoneNumber(numeroCliente), // Número do cliente
          new com.twilio.type.PhoneNumber("whatsapp:+14155238886"), // Sandbox do Twilio
          "Sua OS foi finalizada. O número dela é " + os.getNumero_os() // Mensagem enviada
      ).create();
  
      System.out.println("Mensagem enviada com SID: " + message.getSid());
    } catch (Exception e) {
      System.err.println("Erro ao enviar mensagem: " + e.getMessage());
      throw e; // Para que o erro possa ser tratado onde o método foi chamado
    }
  }
}
