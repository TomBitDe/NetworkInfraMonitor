package util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 * Helper methods for faces messages.
 */
public class MsgUtils {
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(MsgUtils.class);

    /**
     * Creating an instance is not allowed.
     */
    private MsgUtils() {
    }

    /**
     * Clone is not allowed.
     *
     * @return never return something
     *
     * @throws CloneNotSupportedException in any case
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
    }

    /**
     * Show a fatal message.
     *
     * @param msg the text to display
     */
    public static void showFatalMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        LOG.fatal(msg);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal", msg));
    }

    /**
     * Show an error message.
     *
     * @param msg the text to display
     */
    public static void showErrorMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        LOG.error(msg);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
    }

    /**
     * Show a warning message.
     *
     * @param msg the text to display
     */
    public static void showWarningMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        LOG.warn(msg);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning", msg));
    }

    /**
     * Show a message.
     *
     * @param msg the text to display
     */
    public static void showMessage(String msg) {
        FacesContext context = FacesContext.getCurrentInstance();

        LOG.info(msg);
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", msg));
    }
}
