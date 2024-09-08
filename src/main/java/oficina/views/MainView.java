package oficina.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Página Principal")
public class MainView extends VerticalLayout {

    public MainView() {
        // Cria a imagem
        //Image image = new Image("https://example.com/caminho-da-imagem.jpg", "Descrição da imagem");
        Image image = new Image("icons/mustangVermelho.jpg", "Ferrare");

        image.setWidth("100%");
        image.setHeight("100%");
        //image.setWidth("200px"); // Define a largura da imagem
        
        // Adiciona a imagem e o texto ao layout
        add(image);
    }
}
