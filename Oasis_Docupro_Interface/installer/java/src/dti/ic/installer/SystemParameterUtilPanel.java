package dti.ic.installer;

import com.zerog.ia.api.pub.*;
import com.zerog.ia.auto.project.actions.CustomCode;
import oracle.jdbc.OracleTypes;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Panel for user to input System Parameter Util Configuration Information.
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
public class SystemParameterUtilPanel extends CustomCodePanel {

    protected CustomError m_error;

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
        String formsEngine = m_formsEngineTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_FORMS_ENGINE, formsEngine);

        String xmlDir = m_xmlDirTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_XML_DIR, xmlDir);

        // Register variables to be recorded in the response file with a given title.
        ReplayVariableService replay = (ReplayVariableService) customCodePanelProxy.getService(ReplayVariableService.class);
        String[] severalVariables = new String[] { InstallConstants.VAR_FORMS_ENGINE,
                                                   InstallConstants.VAR_XML_DIR
                                                 };

        replay.register(severalVariables, "Ghostdraft Configuration Information");
    }

    /**
     * Validate user input data
     * 
     * @return boolean
     */
    private boolean validateData() {
        String formsEngine = m_formsEngineTextField.getText().trim();
        if (InstallUtil.isBlank(formsEngine)) {
            JOptionPane.showMessageDialog(this, "Forms Engine used in solution is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String xmlDir = m_xmlDirTextField.getText().trim();
        if (InstallUtil.isBlank(xmlDir)) {
            JOptionPane.showMessageDialog(this, "OS_XML_DIRECTORY is required.", "Error", JOptionPane.ERROR_MESSAGE);
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
        m_error = (CustomError) customCodePanelProxy.getService(CustomError.class);

        m_descText = new JTextArea(2, 10);
        m_descText.setText("Enter the requested information for System Parameters Configuration.");
        m_descText.setFont(m_font);
        m_descText.setBackground(m_backgroundColor);
        m_descText.setLineWrap(true);
        m_descText.setWrapStyleWord(true);
        m_descText.setFocusable(false);

        m_formsEngineLabel = new JLabel("Third-Party Forms Engine:");
        m_formsEngineLabel.setFont(m_font);
        /*String userChoice = (customCodePanelProxy.getVariable(InstallConstants.VAR_FORMS_ENGINE_CHOSEN).toString());
        String formsEngineInSol = "";
        if (userChoice.contains("Ghostdraft"))
            formsEngineInSol = "Ghostdraft";
        else
            formsEngineInSol = "Eloquence";
        m_error.appendMessage("forms Engine used in solution = " + formsEngineInSol + "\n");*/
        m_formsEngineTextField = new JTextField(customCodePanelProxy.getVariable(InstallConstants.VAR_FORMS_ENGINE).toString());
        m_formsEngineTextField.setFont(m_font);
        m_formsEngineTextField.setToolTipText("What is the forms engine used in the environment?");

        Connection connection = getConnection();
        GetSystemParameterUtilConfig spuConfig = new GetSystemParameterUtilConfig();
        m_xmlDirLabel = new JLabel("OS_XML_DIRECTORY:");
        m_xmlDirLabel.setFont(m_font);
        String spuData = InstallUtil.getSystemParameterUtilData(connection, m_error, "OS_XML_DIRECTORY");
        if (spuData.equals("")) {
            spuData = InstallUtil.getSystemParameterUtilData(connection, m_error, "OS_DOC_DIRECTORY");
            if (spuData.equals(""))
                m_xmlDirTextField = new JTextField("");
            else
                m_xmlDirTextField = new JTextField(spuData + "\\XML");
        }
        else
            m_xmlDirTextField = new JTextField(spuData);
        m_xmlDirTextField.setFont(m_font);
        m_xmlDirTextField.setToolTipText("What is the value for OS_XML_DIRECTORY?");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(m_backgroundColor);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 3);

        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(m_formsEngineLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(m_formsEngineTextField, c);

        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(m_xmlDirLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(m_xmlDirTextField, c);

        c.gridx = 0;
        c.gridy = 5;
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

    private Connection getConnection() {
        // Load the JDBC driver
        String driverName = "oracle.jdbc.driver.OracleDriver";
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create a connection to the database
        String serverName = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_NAME);
        String portNumber = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_PORT);
        String sid = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SID_NAME);
        String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
        String username = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_NAME);
        String password = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Provide default values from the configDataSource configuration for schema owner too but leave User and Password blank.
     * 
     * @return
     */
    private void initializeSetting() {
        String formsEngine = customCodePanelProxy.substitute(InstallConstants.VAR_FORMS_ENGINE).trim();
        if (!InstallUtil.isBlank(formsEngine)) {
            String formsEngineInSol = "";
            if (formsEngine.contains("Ghostdraft"))
                formsEngineInSol = "Ghostdraft";
            else
                formsEngineInSol = "Eloquence";
            m_formsEngineTextField.setText(formsEngineInSol);
            customCodePanelProxy.setVariable(InstallConstants.VAR_FORMS_ENGINE, formsEngineInSol);
        }

        String xmlDir = customCodePanelProxy.substitute(InstallConstants.VAR_XML_DIR).trim();
        if (!InstallUtil.isBlank(xmlDir))
            m_xmlDirTextField.setText(xmlDir);
    }

    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("System Parameters Util Configuration Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        SystemParameterUtilPanel newContentPane = new SystemParameterUtilPanel();
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

    private JLabel m_formsEngineLabel;

    private JTextField m_formsEngineTextField;

    private JLabel m_xmlDirLabel;

    private JTextField m_xmlDirTextField;
}
