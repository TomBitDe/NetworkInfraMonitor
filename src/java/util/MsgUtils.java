package util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Helper methods for faces messages
 */
public class MsgUtils {
    /**
     * Show a fatal message
     *
     * @param msg the text to display
     */
    public static void showFatalMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", msg));
    }

    /**
     * Show an error message
     *
     * @param msg the text to display
     */
    public static void showErrorMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }

    /**
     * Show a warning message
     *
     * @param msg the text to display
     */
    public static void showWarningMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", msg));
    }

    /**
     * Show a message
     *
     * @param msg the text to display
     */
    public static void showMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", msg));
    }
}
