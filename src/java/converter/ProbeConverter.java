package converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * Converter for prober name values.
 */
@FacesConverter("ProbeConverter")
public class ProbeConverter implements Converter {

    /**
     * Get the probers name as Object without suffix 'Probe'.<br>
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        if (submittedValue == null || submittedValue.isEmpty()) {
            return null;
        }

        return submittedValue.replace("Probe", "");
    }

    /**
     * Get the probers name as String without suffix 'Probe'.<br>
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
        if (modelValue == null) {
            return "";
        }

        if (modelValue instanceof String) {
            return ((String) modelValue).replace("Probe", "");
        }
        else {
            throw new ConverterException(new FacesMessage(modelValue + " is not a String"));
        }
    }

}
