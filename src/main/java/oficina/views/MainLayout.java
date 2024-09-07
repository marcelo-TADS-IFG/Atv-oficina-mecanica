package oficina.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import oficina.views.cliente.ClienteView;
import oficina.views.marca.MarcaView;
import oficina.views.mecanico.MecanicoView;
import oficina.views.ordemdeserviço.OrdemdeServiçoView;
import oficina.views.pecas.PecasView;
import oficina.views.serviços.ServiçosView;
import oficina.views.veiculo.VeiculoView;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Oficina-Mecanica");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Cliente", ClienteView.class, LineAwesomeIcon.USER.create()));
        nav.addItem(new SideNavItem("Mecanico", MecanicoView.class, LineAwesomeIcon.USER_SHIELD_SOLID.create()));
        nav.addItem(new SideNavItem("Veiculo", VeiculoView.class, LineAwesomeIcon.CAR_CRASH_SOLID.create()));
        nav.addItem(new SideNavItem("Marca", MarcaView.class, LineAwesomeIcon.ACCUSOFT.create()));
        nav.addItem(new SideNavItem("Pecas", PecasView.class, LineAwesomeIcon.AMBULANCE_SOLID.create()));
        nav.addItem(new SideNavItem("Serviços", ServiçosView.class, LineAwesomeIcon.COG_SOLID.create()));
        nav.addItem(
                new SideNavItem("Ordem de Serviço", OrdemdeServiçoView.class, LineAwesomeIcon.USER_COG_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    /*@Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }*/

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        if (getCurrentPageTitle().isEmpty()) {
            UI.getCurrent().navigate(ClienteView.class); // Redireciona para uma página padrão
        } else {
            viewTitle.setText(getCurrentPageTitle());
        }
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}


