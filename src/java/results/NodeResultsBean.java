package results;

import config.MonitorConfigurationBean;
import destination.Destination;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import monitor.Monitor;
import org.apache.log4j.Logger;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 */
@ManagedBean(name = "NodeResultsBean")
@SessionScoped
public class NodeResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(NodeResultsBean.class);

    private static final String MONITOR_TYPE = "monitor";
    private static final String DESTINATION_TYPE = "destination";

    @ManagedProperty("#{MonitorConfigurationBean}")
    private MonitorConfigurationBean configuration;

    private TreeNode root;

    /**
     * Creates a new instance of NodeResultsBean
     */
    public NodeResultsBean() {
    }

    /**
     * Get the current configuration; needed because configuration is a @ManagedProperty
     *
     * @return the bean
     */
    public MonitorConfigurationBean getConfiguration() {
        return configuration;
    }

    /**
     * Set the current configuration; needed because configuration is a @ManagedProperty
     *
     * @param configuration the bean
     */
    public void setConfiguration(MonitorConfigurationBean configuration) {
        this.configuration = configuration;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public String getMONITOR_TYPE() {
        return MONITOR_TYPE;
    }

    public String getDESTINATION_TYPE() {
        return DESTINATION_TYPE;
    }

    @PostConstruct
    public void init() {
        root = createTree();
    }

    /**
     * Buildup the tree to display
     *
     * @return the root node of the tree
     */
    private TreeNode createTree() {
        LOG.debug("-->");

        root = new DefaultTreeNode("Root", null);
        NodeResultsDocument document;

        for (Monitor monitor : configuration.getRunningMonitors()) {
            document = new NodeResultsDocument(buildMonitorNodeText(monitor), buildSummary(monitor), Integer.toString(monitor.getInterval()));
            TreeNode monitorNode = new DefaultTreeNode(MONITOR_TYPE,
                                                       document,
                                                       root);

            for (Destination destination : monitor.getDestinations()) {
                document = new NodeResultsDocument(destination.getInetAddr(), destination.getLastProbe(),
                                                   destination.getProbeResult() ? 1 : 0, destination.getQuality(),
                                                   destination.getProbe());
                TreeNode destinationNode = new DefaultTreeNode(DESTINATION_TYPE,
                                                               document,
                                                               monitorNode);
            }
        }

        LOG.debug("<--");
        return root;
    }

    public void update() {
        LOG.debug("-->");

        List<String> expandedList = new ArrayList<>();
        NodeResultsDocument document;

        // Create a list of expanded nodes (monitor nodes only)
        for (TreeNode node : root.getChildren()) {
            if (node.isExpanded()) {
                String monitorId = ((NodeResultsDocument) node.getData()).getMonitorId();
                LOG.debug("Expanded " + monitorId);
                expandedList.add(monitorId);
            }
        }

        String monitorNodeText;

        root.getChildren().clear();
        for (Monitor monitor : configuration.getRunningMonitors()) {
            monitorNodeText = buildMonitorNodeText(monitor);
            document = new NodeResultsDocument(monitorNodeText, buildSummary(monitor), Integer.toString(monitor.getInterval()));
            TreeNode monitorNode = new DefaultTreeNode(MONITOR_TYPE,
                                                       document,
                                                       root);

            if (expandedList.contains(monitorNodeText)) {
                monitorNode.setExpanded(true);
            }

            for (Destination destination : monitor.getDestinations()) {
                document = new NodeResultsDocument(destination.getInetAddr(), destination.getLastProbe(),
                                                   destination.getProbeResult() ? 1 : 0, destination.getQuality(),
                                                   destination.getProbe());
                TreeNode destinationNode = new DefaultTreeNode(DESTINATION_TYPE,
                                                               document,
                                                               monitorNode);
            }
        }

        LOG.debug("<--");
    }

    private String buildMonitorNodeText(Monitor monitor) {
        if (monitor.getComment().isEmpty() || commentExists(monitor)) {
            return monitor.getStart() + " - " + monitor.getEnd();
        }
        else {
            return monitor.getComment();
        }
    }

    private boolean commentExists(Monitor monitor) {
        for (Monitor item : configuration.getRunningMonitors()) {
            if (!item.equals(monitor)) {
                if (item.getComment().equals(monitor.getComment())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int buildSummary(Monitor monitor) {
        for (Destination destination : monitor.getDestinations()) {
            if (destination.getProbeResult() == false) {
                return 0;
            }
        }
        return 1;
    }

    public void nodeExpand(NodeExpandEvent event) {
        LOG.debug("Expand " + event.getTreeNode().getData().toString());
        event.getTreeNode().setExpanded(true);
    }

    public void nodeCollapse(NodeCollapseEvent event) {
        LOG.debug("Collapse " + event.getTreeNode().getData().toString());
        event.getTreeNode().setExpanded(false);
    }
}
