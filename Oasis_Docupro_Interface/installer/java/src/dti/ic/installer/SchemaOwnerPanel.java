package dti.ic.installer;

import com.zerog.ia.api.pub.CustomCodePanel;
import com.zerog.ia.api.pub.CustomCodePanelProxy;
import com.zerog.ia.api.pub.ReplayVariableService;

import javax.swing.*;

import java.awt.*;

/**
 * Panel for user to input OASIS Oracle Database Schema Owner Information.
 * <p/>
 * OASIS Oracle Database Schema Owner will be used create an index on the TDES_SCRIPT_STATE.STATE_ID column.
 * <p/>
 * <p>
 * (C) 2013 Delphi Technology, inc. (dti)
 * </p>
 * Date: Jun 05, 2013
 * 
 * @author Toby Wang
 */
/**
 * 
 * Revision Date Revised By Description
 * ---------------------------------------------------
 * ---------------------------------------------------
 */
public class SchemaOwnerPanel extends CustomCodePanel {

    @Override
    public String getTitle() {
        return "OASIS Database Schema Owner Information";
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
        String hostIP = m_hostIPAddressTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_DB_SCHEMA_SERVER_NAME, hostIP);

        String dbSidName = m_databaseInstanceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_DB_SCHEMA_SID_NAME, dbSidName);

        String dbPort = m_databasePortTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_DB_SCHEMA_SERVER_PORT, dbPort);

        String dbUser = m_databaseUserNameTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_DB_SCHEMA_USER_NAME, dbUser);

        String dbPassword = new String(m_databasePasswordField.getPassword());
        customCodePanelProxy.setVariable(InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD, dbPassword);

        // Register variables to be recorded in the response file with a given title.
        ReplayVariableService replay = (ReplayVariableService) customCodePanelProxy.getService(ReplayVariableService.class);
        String[] severalVariables = new String[] { InstallConstants.VAR_DB_SCHEMA_SERVER_NAME, InstallConstants.VAR_DB_SCHEMA_SERVER_PORT,
                InstallConstants.VAR_DB_SCHEMA_SID_NAME, InstallConstants.VAR_DB_SCHEMA_USER_NAME, InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD };

        replay.register(severalVariables, "OASIS Database Schema Owner Information");
    }

    /**
     * Validate user input date
     * 
     * @return boolean
     */
    private boolean validateData() {
        String hostIP = m_hostIPAddressTextField.getText().trim();
        if (InstallUtil.isBlank(hostIP)) {
            JOptionPane.showMessageDialog(this, "Database Host Name(IP Address) is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String dbSidName = m_databaseInstanceTextField.getText().trim();
        if (InstallUtil.isBlank(dbSidName)) {
            JOptionPane.showMessageDialog(this, "Database Instance Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String dbPort = m_databasePortTextField.getText().trim();
        if (InstallUtil.isBlank(dbPort)) {
            JOptionPane.showMessageDialog(this, "Database Port is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InstallUtil.isInteger(dbPort)) {
            JOptionPane.showMessageDialog(this, "Database Port '" + dbPort + "' is not an integer number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String dbUser = m_databaseUserNameTextField.getText().trim();
        if (InstallUtil.isBlank(dbUser)) {
            JOptionPane.showMessageDialog(this, "Database User Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String password = new String(m_databasePasswordField.getPassword());
        if (InstallUtil.isBlank(password)) {
            JOptionPane.showMessageDialog(this, "Password is required.", "Error", JOptionPane.ERROR_MESSAGE);
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
        m_descText.setText("Enter the requested information for OASIS Oracle Database Schema Owner.");
        m_descText.setFont(m_font);
        m_descText.setBackground(m_backgroundColor);
        m_descText.setLineWrap(true);
        m_descText.setWrapStyleWord(true);
        m_descText.setFocusable(false);

        m_hostIPAddressLabel = new JLabel("Database Host Name(IP Address):");
        m_hostIPAddressLabel.setFont(m_font);
        m_hostIPAddressTextField = new JTextField("");
        m_hostIPAddressTextField.setFont(m_font);
        m_hostIPAddressTextField.setToolTipText("What is the name or IP address of the database server?");

        m_databaseInstanceLabel = new JLabel("Database Instance Name:");
        m_databaseInstanceLabel.setFont(m_font);
        m_databaseInstanceTextField = new JTextField("");
        m_databaseInstanceTextField.setFont(m_font);
        m_databaseInstanceTextField.setToolTipText("What is the name of the database you would like to connect to?");

        m_databasePortLabel = new JLabel("Database Port:");
        m_databasePortLabel.setFont(m_font);
        m_databasePortTextField = new JTextField("1521");
        m_databasePortTextField.setFont(m_font);
        m_databasePortTextField.setToolTipText("What is the port on the database server used to connect to the database?");

        m_databaseUserNameLabel = new JLabel("Database User Name:");
        m_databaseUserNameLabel.setFont(m_font);
        m_databaseUserNameTextField = new JTextField("");
        m_databaseUserNameTextField.setFont(m_font);
        m_databaseUserNameTextField.setToolTipText("What database account user name do you want to use to create database connections?");

        m_databasePasswordLabel = new JLabel("Password:");
        m_databasePasswordLabel.setFont(m_font);
        m_databasePasswordField = new JPasswordField("");
        m_databasePasswordField.setFont(m_font);
        m_databasePasswordField.setToolTipText("What is the database account password to use to create database connections?");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(m_backgroundColor);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 3);

        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(m_hostIPAddressLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(m_hostIPAddressTextField, c);

        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(m_databasePortLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(m_databasePortTextField, c);

        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(m_databaseInstanceLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        mainPanel.add(m_databaseInstanceTextField, c);

        c.gridx = 0;
        c.gridy = 6;
        mainPanel.add(m_databaseUserNameLabel, c);
        c.gridx = 0;
        c.gridy = 7;
        mainPanel.add(m_databaseUserNameTextField, c);

        c.gridx = 0;
        c.gridy = 8;
        mainPanel.add(m_databasePasswordLabel, c);
        c.gridx = 0;
        c.gridy = 9;
        mainPanel.add(m_databasePasswordField, c);
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
        String hostIP = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_NAME).trim();
        if (!InstallUtil.isBlank(hostIP))
            m_hostIPAddressTextField.setText(hostIP);

        String dbSidName = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SID_NAME).trim();
        if (!InstallUtil.isBlank(dbSidName))
            m_databaseInstanceTextField.setText(dbSidName);

        String dbPort = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_SERVER_PORT).trim();
        if (!InstallUtil.isBlank(dbPort))
            m_databasePortTextField.setText(dbPort);

        String dbUser = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_NAME).trim();
        if (!InstallUtil.isBlank(dbUser))
            m_databaseUserNameTextField.setText(dbUser);

        String dbPassword = customCodePanelProxy.substitute(InstallConstants.VAR_DB_SCHEMA_USER_PASSWORD);
        if (!InstallUtil.isBlank(dbPassword))
            m_databasePasswordField.setText(dbPassword);
    }

    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("Data Source Creation Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        SchemaOwnerPanel newContentPane = new SchemaOwnerPanel();
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

    private JLabel m_hostIPAddressLabel;

    private JTextField m_hostIPAddressTextField;

    private JLabel m_databaseInstanceLabel;

    private JTextField m_databaseInstanceTextField;

    private JLabel m_databasePortLabel;

    private JTextField m_databasePortTextField;

    private JLabel m_databaseUserNameLabel;

    private JTextField m_databaseUserNameTextField;

    private JLabel m_databasePasswordLabel;

    private JPasswordField m_databasePasswordField;
}
