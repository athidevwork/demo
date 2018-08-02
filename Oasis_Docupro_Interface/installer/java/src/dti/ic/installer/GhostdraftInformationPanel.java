package dti.ic.installer;

import com.zerog.ia.api.pub.CustomCodePanel;
import com.zerog.ia.api.pub.CustomCodePanelProxy;
import com.zerog.ia.api.pub.ReplayVariableService;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for user to input Ghostdraft Configuration Information.
 *
 * <p>
 * (C) 2018 Delphi Technology, inc. (dti)
 * </p>
 * Date: Jul 12, 2018
 * 
 * @author Athi Muthu
 */

/**
 * 
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class GhostdraftInformationPanel extends CustomCodePanel {

    @Override
    public String getTitle() {
        return "OASIS Ghostdraft Configuration Information";
    }

    @Override
    public boolean okToContinue() {
        if (!validateData()) {
            return false;
        }

        keepCurrentSetting();
        return super.okToContinue();
    }

    @Override
    public boolean okToGoPrevious() {
        keepCurrentSetting();
        return super.okToGoPrevious();
    }

    /**
     * Cache user input data
     * 
     * @return
     */
    private void keepCurrentSetting() {
        String gdInstance = m_ghostdraftInstanceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_GHOSTDRAFT_INSTANCE, gdInstance);

        String gdDocumentService = m_ghostdraftDocServiceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_GHOSTDRAFT_DOC_SERVICE, gdDocumentService);

        String gdTemplateService = m_ghostdraftTemplateServiceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_GHOSTDRAFT_TEMPLATE_SERVICE, gdTemplateService);

        // Register variables to be recorded in the response file with a given title.
        ReplayVariableService replay = (ReplayVariableService) customCodePanelProxy.getService(ReplayVariableService.class);
        String[] severalVariables = new String[] { InstallConstants.VAR_GHOSTDRAFT_INSTANCE,
                                                   InstallConstants.VAR_GHOSTDRAFT_DOC_SERVICE,
                                                   InstallConstants.VAR_GHOSTDRAFT_TEMPLATE_SERVICE
                                                 };

        replay.register(severalVariables, "Ghostdraft Configuration Information");
    }

    /**
     * Validate user input date
     * 
     * @return boolean
     */
    private boolean validateData() {
        String gdInstance = m_ghostdraftInstanceTextField.getText().trim();
        if (InstallUtil.isBlank(gdInstance)) {
            JOptionPane.showMessageDialog(this, "Ghostdraft Instance is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String gdDocSvc = m_ghostdraftDocServiceTextField.getText().trim();
        if (InstallUtil.isBlank(gdDocSvc)) {
            JOptionPane.showMessageDialog(this, "Ghostdraft Document Service Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String gdTempSvc = m_ghostdraftTemplateServiceTextField.getText().trim();
        if (InstallUtil.isBlank(gdTempSvc)) {
            JOptionPane.showMessageDialog(this, "Ghostdraft Template Service Name  is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public boolean setupUI(CustomCodePanelProxy customCodePanelProxy) {
        if (m_isSetup) {
            removeAll();
        }

        m_font = getFont();
        m_backgroundColor = getBackground();

        initializeComponent();
        initializeSetting();

        m_isSetup = true;

        return true;
    }

    /**
     * Component initialization
     * 
     * @return boolean
     */
    private void initializeComponent() {
        setLayout(new FlowLayout());
        add(getDataSourceCreationPanel());

        // fresh this panel
        validate();
        repaint();
    }

    /**
     * Get the core panel to be integrated with InstallAnywhere panel
     * 
     * @return JPanel
     */
    private JPanel getDataSourceCreationPanel() {
        m_descText = new JTextArea(2, 10);
        m_descText.setText("Enter the requested information for Ghostdraft Configuration.");
        m_descText.setFont(m_font);
        m_descText.setBackground(m_backgroundColor);
        m_descText.setLineWrap(true);
        m_descText.setWrapStyleWord(true);
        m_descText.setFocusable(false);

        m_ghostdraftInstanceLabel = new JLabel("Ghostdraft Instance Name:");
        m_ghostdraftInstanceLabel.setFont(m_font);
        m_ghostdraftInstanceTextField = new JTextField("");
        m_ghostdraftInstanceTextField.setFont(m_font);
        m_ghostdraftInstanceTextField.setToolTipText("What is the instance name for the Ghostdraft?");

        m_ghostdraftDocServiceLabel = new JLabel("Database Document Service Name:");
        m_ghostdraftDocServiceLabel.setFont(m_font);
        m_ghostdraftDocServiceTextField = new JTextField("");
        m_ghostdraftDocServiceTextField.setFont(m_font);
        m_ghostdraftDocServiceTextField.setToolTipText("What is the document service mapping for Ghostdraft?");

        //m_ghostdraftDocServiceTextField.addActionListener(new GhostdraftInformationPanelListener());

        m_ghostdraftTemplateServiceLabel = new JLabel("Ghostdraft Template Service Name (Typically it is the same as document service name):");
        m_ghostdraftTemplateServiceLabel.setFont(m_font);
        m_ghostdraftTemplateServiceTextField = new JTextField("");
        m_ghostdraftTemplateServiceTextField.setFont(m_font);
        m_ghostdraftTemplateServiceTextField.setToolTipText("What is the template service mapping for Ghostdraft?");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(m_backgroundColor);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 3);

        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(m_ghostdraftInstanceLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(m_ghostdraftInstanceTextField, c);

        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(m_ghostdraftDocServiceLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(m_ghostdraftDocServiceTextField, c);

        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(m_ghostdraftTemplateServiceLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        mainPanel.add(m_ghostdraftTemplateServiceTextField, c);

        c.gridx = 0;
        c.gridy = 10;
        mainPanel.add(new JLabel("       "), c);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(m_backgroundColor);
        topPanel.add(m_descText, BorderLayout.NORTH);
        topPanel.add(new JLabel("      "), BorderLayout.CENTER);

        JPanel cPanel = new JPanel(new BorderLayout());
        cPanel.setBackground(m_backgroundColor);
        cPanel.add(mainPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(cPanel);
        // A common operation to want to do is to set the background color that will be used if the main viewport view is smaller than the viewport,
        // or is not opaque. This can be accomplished by setting the background color of the viewport, via scrollPane.getViewport().setBackground().
        // The reason for setting the color of the viewport and not the scrollpane is that by default JViewport is opaque which, among other things,
        // means it will completely fill in its background using its background color. Therefore when JScrollPane draws its background the viewport will
        // usually draw over it.
        scrollPane.getViewport().setBackground(m_backgroundColor);
        scrollPane.setBackground(m_backgroundColor);

        JPanel customPanel = new JPanel(new BorderLayout());
        customPanel.setBackground(m_backgroundColor);
        customPanel.add(topPanel, BorderLayout.NORTH);
        customPanel.add(scrollPane, BorderLayout.CENTER);
        customPanel.setPreferredSize(new Dimension(370, 280));

        return customPanel;
    }

    /**
     * Provide default values from the configDataSource configuration for schema owner too but leave User and Password blank.
     * 
     * @return
     */
    private void initializeSetting() {
        String gdInstance = customCodePanelProxy.substitute(InstallConstants.VAR_GHOSTDRAFT_INSTANCE).trim();
        if (!InstallUtil.isBlank(gdInstance))
            m_ghostdraftInstanceTextField.setText(gdInstance);

        String gdDocSvc = customCodePanelProxy.substitute(InstallConstants.VAR_GHOSTDRAFT_DOC_SERVICE).trim();
        if (!InstallUtil.isBlank(gdDocSvc))
            m_ghostdraftDocServiceTextField.setText(gdDocSvc);

        String gdTempSvc = customCodePanelProxy.substitute(InstallConstants.VAR_GHOSTDRAFT_TEMPLATE_SERVICE).trim();
        if (!InstallUtil.isBlank(gdTempSvc))
            m_ghostdraftTemplateServiceTextField.setText(gdTempSvc);
    }

    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("Ghostdraft Configuration Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        GhostdraftInformationPanel newContentPane = new GhostdraftInformationPanel();
        newContentPane.setupUI(customCodePanelProxy);
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private static final long serialVersionUID = -7528436312452067512L;

    private boolean m_isSetup = false;

    private Font m_font;

    private Color m_backgroundColor;

    private JTextArea m_descText;

    private JLabel m_ghostdraftInstanceLabel;

    private JTextField m_ghostdraftInstanceTextField;

    private JLabel m_ghostdraftDocServiceLabel;

    private JTextField m_ghostdraftDocServiceTextField;

    private JLabel m_ghostdraftTemplateServiceLabel;

    private JTextField m_ghostdraftTemplateServiceTextField;
}
