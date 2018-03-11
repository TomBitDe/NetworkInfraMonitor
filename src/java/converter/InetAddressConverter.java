package converter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Converter for InetAddresses.
 */
@FacesConverter("InetAddressConverter")
public class InetAddressConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.isEmpty()) {
            return null;
        }

        try {
            return InetAddress.getByName(submittedValue);
        }
        catch (UnknownHostException uhex) {
            throw new ConverterException(new FacesMessage(submittedValue + " is not a valid HostName"));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }

        if (modelValue instanceof InetAddress) {
            return ((InetAddress) modelValue).getHostAddress();
        }
        else {
            throw new ConverterException(new FacesMessage(modelValue + " is not a valid InetAddress"));
        }
    }

}
