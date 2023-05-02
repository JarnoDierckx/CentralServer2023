import com.fooditsolutions.util.model.Client;
import com.fooditsolutions.web.bean.ManageContractBean;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;

@Named
@FacesConverter(value = "clientConverter", managed = true)
public class clientConverter implements Converter<Client> {

    @Inject
    private ManageContractBean manageContractBean;

    @Override
    public Client getAsObject(FacesContext context, UIComponent component, String value) {
        Client[] clients= manageContractBean.getClients();
        int id = Integer.parseInt(value);
        for (Client client : clients) {
            if (client.getDBB_ID().equals(BigDecimal.valueOf(id))) {
                return client;
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Client value) {
        if (value != null) {
            return String.valueOf(((Client) value).getDBB_ID());
        }
        return null;
    }
}