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
public class EloquenceInformationPanel extends CustomCodePanel {

    @Override
    public String getTitle() {
        return "OASIS Eloquence Configuration Information";
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
        String hostIP = m_outputWSHostTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_HOST, hostIP);

        String dbPort = m_outputWSPortTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_PORT, dbPort);

        String ewebhostIP = m_EloquenceWebHostTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_WEB_HOST, ewebhostIP);

        String ewebPort = m_EloquenceWebPortTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_WEB_PORT, ewebPort);

        String intForms = m_interactiveFormsTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_INTERACTIVE_FORM, intForms);

        String inputResourceLocator = m_inputResourceLocatorTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_INPUT_RESOURCE_LOC, inputResourceLocator);

        String defaultDeviceName = m_defaultDeviceNameTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_DEFAULT_DEVICE_NAME, defaultDeviceName);

        String mapFileName = m_mapFileNameTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_MAP_FILE_NAME, mapFileName);

        String configPath = m_configPathTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_CONFIG_PATH, configPath);

        String previewArchivePath = m_previewArchivePathTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_ARCHIVE_PATH, previewArchivePath);

        String previewGeneralCollection = m_previewGeneralCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_COLLECTION, previewGeneralCollection);

        String previewGeneralEntity = m_previewGeneralEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_ENTITY, previewGeneralEntity);

        String previewLocalDevice = m_previewLocalDeviceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_LOCAL_DEVICE, previewLocalDevice);

        String previewLogLevel = m_previewLogLevelTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_LOG_LEVEL, previewLogLevel);

        String previewNetworkDevice = m_previewNetworkDeviceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_NETWORK_DEVICE, previewNetworkDevice);

        String previewVariableSet = m_previewVariableSetTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PREVIEW_VARIABLE_SET, previewVariableSet);

        String archivePath = m_archivePathTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_ARCHIVE_PATH, archivePath);

        String generalCollection = m_generalCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_GENERAL_COLLECTION, generalCollection);

        String generalEntity = m_generalEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_GENERAL_ENTITY, generalEntity);

        String localDevice = m_localDeviceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_LOCAL_DEVICE, localDevice);

        String logLevel = m_logLevelTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_LOG_LEVEL, logLevel);

        String networkDevice = m_networkDeviceTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_NETWORK_DEVICE, networkDevice);

        String variableSet = m_variableSetTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_VARIABLE_SET, variableSet);

        String cleanupAfterPreview = m_cleanupAfterPreviewLTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_CLEANUP_AFTER_PREVIEW, cleanupAfterPreview);

        String pmsCollection = m_pmsCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PMS_COLLECTION, pmsCollection);

        String pmsEntity = m_pmsEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_PMS_ENTITY, pmsEntity);

        String fmsCollection = m_fmsCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_FMS_COLLECTION, fmsCollection);

        String fmsEntity = m_fmsEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_FMS_ENTITY, fmsEntity);

        String cmsCollection = m_cmsCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_CMS_COLLECTION, cmsCollection);

        String cmsEntity = m_cmsEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_CMS_ENTITY, cmsEntity);

        String rmsCollection = m_rmsCollectionTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_RMS_COLLECTION, rmsCollection);

        String rmsEntity = m_rmsEntityTextField.getText().trim();
        customCodePanelProxy.setVariable(InstallConstants.VAR_ELOQUENCE_RMS_ENTITY, rmsEntity);

        // Register variables to be recorded in the response file with a given title.
        ReplayVariableService replay = (ReplayVariableService) customCodePanelProxy.getService(ReplayVariableService.class);
        String[] severalVariables = new String[] { InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_HOST,
                                                   InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_PORT,
                                                   InstallConstants.VAR_ELOQUENCE_WEB_HOST,
                                                   InstallConstants.VAR_ELOQUENCE_WEB_PORT,
                                                   InstallConstants.VAR_ELOQUENCE_INTERACTIVE_FORM,
                                                   InstallConstants.VAR_ELOQUENCE_INPUT_RESOURCE_LOC,
                                                   InstallConstants.VAR_ELOQUENCE_DEFAULT_DEVICE_NAME,
                                                   InstallConstants.VAR_ELOQUENCE_MAP_FILE_NAME,
                                                   InstallConstants.VAR_ELOQUENCE_CONFIG_PATH,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_ARCHIVE_PATH,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_ENTITY,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_LOCAL_DEVICE,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_LOG_LEVEL,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_NETWORK_DEVICE,
                                                   InstallConstants.VAR_ELOQUENCE_PREVIEW_VARIABLE_SET,
                                                   InstallConstants.VAR_ELOQUENCE_ARCHIVE_PATH,
                                                   InstallConstants.VAR_ELOQUENCE_GENERAL_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_GENERAL_ENTITY,
                                                   InstallConstants.VAR_ELOQUENCE_LOCAL_DEVICE,
                                                   InstallConstants.VAR_ELOQUENCE_LOG_LEVEL,
                                                   InstallConstants.VAR_ELOQUENCE_NETWORK_DEVICE,
                                                   InstallConstants.VAR_ELOQUENCE_VARIABLE_SET,
                                                   InstallConstants.VAR_ELOQUENCE_CLEANUP_AFTER_PREVIEW,
                                                   InstallConstants.VAR_ELOQUENCE_PMS_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_PMS_ENTITY,
                                                   InstallConstants.VAR_ELOQUENCE_FMS_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_FMS_ENTITY,
                                                   InstallConstants.VAR_ELOQUENCE_CMS_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_CMS_ENTITY,
                                                   InstallConstants.VAR_ELOQUENCE_RMS_COLLECTION,
                                                   InstallConstants.VAR_ELOQUENCE_RMS_ENTITY
                                                 };

        replay.register(severalVariables, "Eloquence Configuration Information");
    }

    /**
     * Validate user input date
     * 
     * @return boolean
     */
    private boolean validateData() {
        String outputWSHost = m_outputWSHostTextField.getText().trim();
        if (InstallUtil.isBlank(outputWSHost)) {
            JOptionPane.showMessageDialog(this, "Output WS Host Name(IP Address) is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String OutputWSPort = m_outputWSPortTextField.getText().trim();
        if (InstallUtil.isBlank(OutputWSPort)) {
            JOptionPane.showMessageDialog(this, "Output WS Port is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InstallUtil.isInteger(OutputWSPort)) {
            JOptionPane.showMessageDialog(this, "Output WS Port '" + OutputWSPort + "' is not an integer number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String webHost = m_EloquenceWebHostTextField.getText().trim();
        if (InstallUtil.isBlank(webHost)) {
            JOptionPane.showMessageDialog(this, "Web Host Name(IP Address) is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String webPort = m_EloquenceWebPortTextField.getText().trim();
        if (InstallUtil.isBlank(webPort)) {
            JOptionPane.showMessageDialog(this, "Web Port is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InstallUtil.isInteger(webPort)) {
            JOptionPane.showMessageDialog(this, "Web Port '" + webPort + "' is not an integer number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String intForms = m_interactiveFormsTextField.getText().trim();
        if (InstallUtil.isBlank(intForms)) {
            JOptionPane.showMessageDialog(this, "Interactive Forms is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String inputResource = m_inputResourceLocatorTextField.getText().trim();
        if (InstallUtil.isBlank(inputResource)) {
            JOptionPane.showMessageDialog(this, "Input Resource Locator is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String deviceName = m_defaultDeviceNameTextField.getText().trim();
        if (InstallUtil.isBlank(deviceName)) {
            JOptionPane.showMessageDialog(this, "Default Device Name  is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String mapFileName = m_mapFileNameTextField.getText().trim();
        if (InstallUtil.isBlank(mapFileName)) {
            JOptionPane.showMessageDialog(this, "Map File Name  is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String configPath = m_configPathTextField.getText().trim();
        if (InstallUtil.isBlank(configPath)) {
            JOptionPane.showMessageDialog(this, "Config Path  is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewArchivePath = m_previewArchivePathTextField.getText().trim();
        if (InstallUtil.isBlank(previewArchivePath)) {
            JOptionPane.showMessageDialog(this, "Preview Archive Path is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewGeneralCollection = m_previewGeneralCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(previewGeneralCollection)) {
            JOptionPane.showMessageDialog(this, "Preview General Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewGeneralEntity = m_previewGeneralEntityTextField.getText().trim();
        if (InstallUtil.isBlank(previewGeneralEntity)) {
            JOptionPane.showMessageDialog(this, "Preview General Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewLocalDevice = m_previewLocalDeviceTextField.getText().trim();
        if (InstallUtil.isBlank(previewLocalDevice)) {
            JOptionPane.showMessageDialog(this, "Preview Local Device is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewLogLevel = m_previewLogLevelTextField.getText().trim();
        if (InstallUtil.isBlank(previewLogLevel)) {
            JOptionPane.showMessageDialog(this, "Preview Log Level is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewNetworkDevice = m_previewNetworkDeviceTextField.getText().trim();
        if (InstallUtil.isBlank(previewNetworkDevice)) {
            JOptionPane.showMessageDialog(this, "Preview Network Device is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String previewVariableSet = m_previewVariableSetTextField.getText().trim();
        if (InstallUtil.isBlank(previewVariableSet)) {
            JOptionPane.showMessageDialog(this, "Preview Variable Set is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String archivePath = m_archivePathTextField.getText().trim();
        if (InstallUtil.isBlank(archivePath)) {
            JOptionPane.showMessageDialog(this, "Archive Path is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String generalCollection = m_generalCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(generalCollection)) {
            JOptionPane.showMessageDialog(this, "General Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String generalEntity = m_generalEntityTextField.getText().trim();
        if (InstallUtil.isBlank(generalEntity)) {
            JOptionPane.showMessageDialog(this, "General Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String localDevice = m_localDeviceTextField.getText().trim();
        if (InstallUtil.isBlank(localDevice)) {
            JOptionPane.showMessageDialog(this, "Local Device is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String logLevel = m_logLevelTextField.getText().trim();
        if (InstallUtil.isBlank(logLevel)) {
            JOptionPane.showMessageDialog(this, "Log Level is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String networkDevice = m_networkDeviceTextField.getText().trim();
        if (InstallUtil.isBlank(networkDevice)) {
            JOptionPane.showMessageDialog(this, "Network Device is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String variableSet = m_variableSetTextField.getText().trim();
        if (InstallUtil.isBlank(variableSet)) {
            JOptionPane.showMessageDialog(this, "Variable Set is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String cleanupAfterPreview = m_cleanupAfterPreviewLTextField.getText().trim();
        if (InstallUtil.isBlank(cleanupAfterPreview)) {
            JOptionPane.showMessageDialog(this, "Cleanup after Preview is required (true|false).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String pmsCollection = m_pmsCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(pmsCollection)) {
            JOptionPane.showMessageDialog(this, "PMS Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String pmsEntity = m_pmsEntityTextField.getText().trim();
        if (InstallUtil.isBlank(pmsEntity)) {
            JOptionPane.showMessageDialog(this, "PMS Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String fmsCollection = m_fmsCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(fmsCollection)) {
            JOptionPane.showMessageDialog(this, "FMS Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String fmsEntity = m_fmsEntityTextField.getText().trim();
        if (InstallUtil.isBlank(fmsEntity)) {
            JOptionPane.showMessageDialog(this, "FMS Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String cmsCollection = m_cmsCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(cmsCollection)) {
            JOptionPane.showMessageDialog(this, "CMS Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String cmsEntity = m_cmsEntityTextField.getText().trim();
        if (InstallUtil.isBlank(cmsEntity)) {
            JOptionPane.showMessageDialog(this, "CMS Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String rmsCollection = m_rmsCollectionTextField.getText().trim();
        if (InstallUtil.isBlank(rmsCollection)) {
            JOptionPane.showMessageDialog(this, "RMS Collection is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String rmsEntity = m_rmsEntityTextField.getText().trim();
        if (InstallUtil.isBlank(rmsEntity)) {
            JOptionPane.showMessageDialog(this, "RMS Entity is required.", "Error", JOptionPane.ERROR_MESSAGE);
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
        m_descText.setText("Enter the requested information for Eloquence Configuration.");
        m_descText.setFont(m_font);
        m_descText.setBackground(m_backgroundColor);
        m_descText.setLineWrap(true);
        m_descText.setWrapStyleWord(true);
        m_descText.setFocusable(false);

        m_outputWSHostLabel = new JLabel("Output WS Host:");
        m_outputWSHostLabel.setFont(m_font);
        m_outputWSHostTextField = new JTextField("");
        m_outputWSHostTextField.setFont(m_font);
        m_outputWSHostTextField.setToolTipText("What is the host name for the Eloquence Output WS?");

        m_outputWSPortLabel = new JLabel("Output WS Port:");
        m_outputWSPortLabel.setFont(m_font);
        m_outputWSPortTextField = new JTextField("8080");
        m_outputWSPortTextField.setFont(m_font);
        m_outputWSPortTextField.setToolTipText("What is the port for the Eloquence Output WS?");

        m_EloquenceWebHostLabel = new JLabel("Eloquence Web Host:");
        m_EloquenceWebHostLabel.setFont(m_font);
        m_EloquenceWebHostTextField = new JTextField("");
        m_EloquenceWebHostTextField.setFont(m_font);
        m_EloquenceWebHostTextField.setToolTipText("What is the host name for the Eloquence Web?");

        m_EloquenceWebPortLabel = new JLabel("Eloquence Web Port:");
        m_EloquenceWebPortLabel.setFont(m_font);
        m_EloquenceWebPortTextField = new JTextField("8280");
        m_EloquenceWebPortTextField.setFont(m_font);
        m_EloquenceWebPortTextField.setToolTipText("What is the port for the Eloquence Web?");

        m_interactiveFormsLabel = new JLabel("Interactive Forms Folder:");
        m_interactiveFormsLabel.setFont(m_font);
        m_interactiveFormsTextField = new JTextField("interactive_forms");
        m_interactiveFormsTextField.setFont(m_font);
        m_interactiveFormsTextField.setToolTipText("What is the folder name for the Eloquence interactive forms?");

        m_inputResourceLocatorLabel = new JLabel("Input Resource Locator:");
        m_inputResourceLocatorLabel.setFont(m_font);
        m_inputResourceLocatorTextField = new JTextField("input_files_resource_locator");
        m_inputResourceLocatorTextField.setFont(m_font);
        m_inputResourceLocatorTextField.setToolTipText("What is the input resource locator for Eloquence?");

        m_defaultDeviceNameLabel = new JLabel("Default Device Name:");
        m_defaultDeviceNameLabel.setFont(m_font);
        m_defaultDeviceNameTextField = new JTextField("SaveToOASIS");
        m_defaultDeviceNameTextField.setFont(m_font);
        m_defaultDeviceNameTextField.setToolTipText("What is the default device name for Eloquence?");

        m_mapFileNameLabel = new JLabel("Map File Name:");
        m_mapFileNameLabel.setFont(m_font);
        m_mapFileNameTextField = new JTextField("xml_var_set.xml");
        m_mapFileNameTextField.setFont(m_font);
        m_mapFileNameTextField.setToolTipText("What is the map file name for Eloquence?");

        m_configPathLabel = new JLabel("Config Path:");
        m_configPathLabel.setFont(m_font);
        m_configPathTextField = new JTextField("dti\\Eloquence");
        m_configPathTextField.setFont(m_font);
        m_configPathTextField.setToolTipText("What is the config path for Eloquence?");

        m_previewArchivePathLabel = new JLabel("Preview Archive Path:");
        m_previewArchivePathLabel.setFont(m_font);
        m_previewArchivePathTextField = new JTextField("E:/data/webdav_delphidev/archive/preview");
        m_previewArchivePathTextField.setFont(m_font);
        m_previewArchivePathTextField.setToolTipText("What is the output preview archive path for Eloquence?");

        m_previewGeneralCollectionLabel = new JLabel("Output Preview General Collection:");
        m_previewGeneralCollectionLabel.setFont(m_font);
        m_previewGeneralCollectionTextField = new JTextField("Output_Preview_General_Collection");
        m_previewGeneralCollectionTextField.setFont(m_font);
        m_previewGeneralCollectionTextField.setToolTipText("What is the output preview General Collection for Eloquence?");

        m_previewGeneralEntityLabel = new JLabel("Output Preview General Entity:");
        m_previewGeneralEntityLabel.setFont(m_font);
        m_previewGeneralEntityTextField = new JTextField("Output_Preview_General_Entity");
        m_previewGeneralEntityTextField.setFont(m_font);
        m_previewGeneralEntityTextField.setToolTipText("What is the output preview General Entity for Eloquence?");

        m_previewLocalDeviceLabel = new JLabel("Output Preview Local Device:");
        m_previewLocalDeviceLabel.setFont(m_font);
        m_previewLocalDeviceTextField = new JTextField("Output_Preview_Local_Device");
        m_previewLocalDeviceTextField.setFont(m_font);
        m_previewLocalDeviceTextField.setToolTipText("What is the output preview local device for Eloquence?");

        m_previewLogLevelLabel = new JLabel("Output Preview Log Level:");
        m_previewLogLevelLabel.setFont(m_font);
        m_previewLogLevelTextField = new JTextField("debug");
        m_previewLogLevelTextField.setFont(m_font);
        m_previewLogLevelTextField.setToolTipText("What is the output preview log level for Eloquence?");

        m_previewNetworkDeviceLabel = new JLabel("Output Preview Network Device Name:");
        m_previewNetworkDeviceLabel.setFont(m_font);
        m_previewNetworkDeviceTextField = new JTextField("Output_Preview_Network_Device");
        m_previewNetworkDeviceTextField.setFont(m_font);
        m_previewNetworkDeviceTextField.setToolTipText("What is the output preview network device name for Eloquence?");

        m_previewVariableSetLabel = new JLabel("Output Preview Variable Set Name:");
        m_previewVariableSetLabel.setFont(m_font);
        m_previewVariableSetTextField = new JTextField("Output_Preview_Variable_Set");
        m_previewVariableSetTextField.setFont(m_font);
        m_previewVariableSetTextField.setToolTipText("What is the output preview variable set name for Eloquence?");

        m_archivePathLabel = new JLabel("Archive Path:");
        m_archivePathLabel.setFont(m_font);
        m_archivePathTextField = new JTextField("E:/data/webdav_delphidev/archive");
        m_archivePathTextField.setFont(m_font);
        m_archivePathTextField.setToolTipText("What is the output archive path for Eloquence?");

        m_generalCollectionLabel = new JLabel("Output General Collection:");
        m_generalCollectionLabel.setFont(m_font);
        m_generalCollectionTextField = new JTextField("Output_General_Collection");
        m_generalCollectionTextField.setFont(m_font);
        m_generalCollectionTextField.setToolTipText("What is the output General Collection for Eloquence?");

        m_generalEntityLabel = new JLabel("Output General Entity:");
        m_generalEntityLabel.setFont(m_font);
        m_generalEntityTextField = new JTextField("Output_General_Entity");
        m_generalEntityTextField.setFont(m_font);
        m_generalEntityTextField.setToolTipText("What is the output General Entity for Eloquence?");

        m_localDeviceLabel = new JLabel("Output Local Device:");
        m_localDeviceLabel.setFont(m_font);
        m_localDeviceTextField = new JTextField("Output_Local_Device");
        m_localDeviceTextField.setFont(m_font);
        m_localDeviceTextField.setToolTipText("What is the output local device for Eloquence?");

        m_logLevelLabel = new JLabel("Output Log Level:");
        m_logLevelLabel.setFont(m_font);
        m_logLevelTextField = new JTextField("debug");
        m_logLevelTextField.setFont(m_font);
        m_logLevelTextField.setToolTipText("What is the log level for Eloquence?");

        m_networkDeviceLabel = new JLabel("Output Network Device Name:");
        m_networkDeviceLabel.setFont(m_font);
        m_networkDeviceTextField = new JTextField("Output_Network_Device");
        m_networkDeviceTextField.setFont(m_font);
        m_networkDeviceTextField.setToolTipText("What is the output network device name for Eloquence?");

        m_variableSetLabel = new JLabel("Output Variable Set Name:");
        m_variableSetLabel.setFont(m_font);
        m_variableSetTextField = new JTextField("Output_Variable_Set");
        m_variableSetTextField.setFont(m_font);
        m_variableSetTextField.setToolTipText("What is the output variable set name for Eloquence?");

        m_cleanupAfterPreviewLabel = new JLabel("Cleanup after Preview:");
        m_cleanupAfterPreviewLabel.setFont(m_font);
        m_cleanupAfterPreviewLTextField = new JTextField("true");
        m_cleanupAfterPreviewLTextField.setFont(m_font);
        m_cleanupAfterPreviewLTextField.setToolTipText("What is the cleanup after preview flag for Eloquence?");

        m_pmsCollectionLabel = new JLabel("PMS Collection:");
        m_pmsCollectionLabel.setFont(m_font);
        m_pmsCollectionTextField = new JTextField("PMS_Collection");
        m_pmsCollectionTextField.setFont(m_font);
        m_pmsCollectionTextField.setToolTipText("What is the PMS Collection for Eloquence?");

        m_pmsEntityLabel = new JLabel("PMS Entity:");
        m_pmsEntityLabel.setFont(m_font);
        m_pmsEntityTextField = new JTextField("PMS_Entity");
        m_pmsEntityTextField.setFont(m_font);
        m_pmsEntityTextField.setToolTipText("What is the PMS Entity for Eloquence?");

        m_fmsCollectionLabel = new JLabel("FMS Collection:");
        m_fmsCollectionLabel.setFont(m_font);
        m_fmsCollectionTextField = new JTextField("FMS_Collection");
        m_fmsCollectionTextField.setFont(m_font);
        m_fmsCollectionTextField.setToolTipText("What is the FMS Collection for Eloquence?");

        m_fmsEntityLabel = new JLabel("FMS Entity:");
        m_fmsEntityLabel.setFont(m_font);
        m_fmsEntityTextField = new JTextField("FMS_Entity");
        m_fmsEntityTextField.setFont(m_font);
        m_fmsEntityTextField.setToolTipText("What is the FMS Entity for Eloquence?");

        m_cmsCollectionLabel = new JLabel("CMS Collection:");
        m_cmsCollectionLabel.setFont(m_font);
        m_cmsCollectionTextField = new JTextField("CMS_Collection");
        m_cmsCollectionTextField.setFont(m_font);
        m_cmsCollectionTextField.setToolTipText("What is the CMS Collection for Eloquence?");

        m_cmsEntityLabel = new JLabel("CMS Entity:");
        m_cmsEntityLabel.setFont(m_font);
        m_cmsEntityTextField = new JTextField("CMS_Entity");
        m_cmsEntityTextField.setFont(m_font);
        m_cmsEntityTextField.setToolTipText("What is the CMS Entity for Eloquence?");

        m_rmsCollectionLabel = new JLabel("RMS Collection:");
        m_rmsCollectionLabel.setFont(m_font);
        m_rmsCollectionTextField = new JTextField("RMS_Collection");
        m_rmsCollectionTextField.setFont(m_font);
        m_rmsCollectionTextField.setToolTipText("What is the RMS Collection for Eloquence?");

        m_rmsEntityLabel = new JLabel("RMS Entity:");
        m_rmsEntityLabel.setFont(m_font);
        m_rmsEntityTextField = new JTextField("RMS_Entity");
        m_rmsEntityTextField.setFont(m_font);
        m_rmsEntityTextField.setToolTipText("What is the RMS Entity for Eloquence?");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(m_backgroundColor);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 3);

        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(m_outputWSHostLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(m_outputWSHostTextField, c);

        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(m_outputWSPortLabel, c);
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(m_outputWSPortTextField, c);

        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(m_EloquenceWebHostLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        mainPanel.add(m_EloquenceWebHostTextField, c);

        c.gridx = 0;
        c.gridy = 6;
        mainPanel.add(m_EloquenceWebPortLabel, c);
        c.gridx = 0;
        c.gridy = 7;
        mainPanel.add(m_EloquenceWebPortTextField, c);

        c.gridx = 0;
        c.gridy = 8;
        mainPanel.add(m_interactiveFormsLabel, c);
        c.gridx = 0;
        c.gridy = 9;
        mainPanel.add(m_interactiveFormsTextField, c);

        c.gridx = 0;
        c.gridy = 10;
        mainPanel.add(m_inputResourceLocatorLabel, c);
        c.gridx = 0;
        c.gridy = 11;
        mainPanel.add(m_inputResourceLocatorTextField, c);

        c.gridx = 0;
        c.gridy = 12;
        mainPanel.add(m_defaultDeviceNameLabel, c);
        c.gridx = 0;
        c.gridy = 13;
        mainPanel.add(m_defaultDeviceNameTextField, c);

        c.gridx = 0;
        c.gridy = 14;
        mainPanel.add(m_mapFileNameLabel, c);
        c.gridx = 0;
        c.gridy = 15;
        mainPanel.add(m_mapFileNameTextField, c);

        c.gridx = 0;
        c.gridy = 16;
        mainPanel.add(m_configPathLabel, c);
        c.gridx = 0;
        c.gridy = 17;
        mainPanel.add(m_configPathTextField, c);

        c.gridx = 0;
        c.gridy = 18;
        mainPanel.add(m_previewArchivePathLabel, c);
        c.gridx = 0;
        c.gridy = 19;
        mainPanel.add(m_previewArchivePathTextField, c);

        c.gridx = 0;
        c.gridy = 20;
        mainPanel.add(m_previewGeneralCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 21;
        mainPanel.add(m_previewGeneralCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 22;
        mainPanel.add(m_previewGeneralEntityLabel, c);
        c.gridx = 0;
        c.gridy = 23;
        mainPanel.add(m_previewGeneralEntityTextField, c);

        c.gridx = 0;
        c.gridy = 24;
        mainPanel.add(m_previewLocalDeviceLabel, c);
        c.gridx = 0;
        c.gridy = 25;
        mainPanel.add(m_previewLocalDeviceTextField, c);

        c.gridx = 0;
        c.gridy = 26;
        mainPanel.add(m_previewLogLevelLabel, c);
        c.gridx = 0;
        c.gridy = 27;
        mainPanel.add(m_previewLogLevelTextField, c);

        c.gridx = 0;
        c.gridy = 28;
        mainPanel.add(m_previewNetworkDeviceLabel, c);
        c.gridx = 0;
        c.gridy = 29;
        mainPanel.add(m_previewNetworkDeviceTextField, c);

        c.gridx = 0;
        c.gridy = 30;
        mainPanel.add(m_previewVariableSetLabel, c);
        c.gridx = 0;
        c.gridy = 31;
        mainPanel.add(m_previewVariableSetTextField, c);

        c.gridx = 0;
        c.gridy = 32;
        mainPanel.add(m_archivePathLabel, c);
        c.gridx = 0;
        c.gridy = 33;
        mainPanel.add(m_archivePathTextField, c);

        c.gridx = 0;
        c.gridy = 34;
        mainPanel.add(m_generalCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 35;
        mainPanel.add(m_generalCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 36;
        mainPanel.add(m_generalEntityLabel, c);
        c.gridx = 0;
        c.gridy = 37;
        mainPanel.add(m_generalEntityTextField, c);

        c.gridx = 0;
        c.gridy = 38;
        mainPanel.add(m_localDeviceLabel, c);
        c.gridx = 0;
        c.gridy = 39;
        mainPanel.add(m_localDeviceTextField, c);

        c.gridx = 0;
        c.gridy = 40;
        mainPanel.add(m_logLevelLabel, c);
        c.gridx = 0;
        c.gridy = 41;
        mainPanel.add(m_logLevelTextField, c);

        c.gridx = 0;
        c.gridy = 42;
        mainPanel.add(m_networkDeviceLabel, c);
        c.gridx = 0;
        c.gridy = 43;
        mainPanel.add(m_networkDeviceTextField, c);

        c.gridx = 0;
        c.gridy = 44;
        mainPanel.add(m_variableSetLabel, c);
        c.gridx = 0;
        c.gridy = 45;
        mainPanel.add(m_variableSetTextField, c);

        c.gridx = 0;
        c.gridy = 46;
        mainPanel.add(m_cleanupAfterPreviewLabel, c);
        c.gridx = 0;
        c.gridy = 47;
        mainPanel.add(m_cleanupAfterPreviewLTextField, c);

        c.gridx = 0;
        c.gridy = 48;
        mainPanel.add(m_pmsCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 49;
        mainPanel.add(m_pmsCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 50;
        mainPanel.add(m_pmsEntityLabel, c);
        c.gridx = 0;
        c.gridy = 51;
        mainPanel.add(m_pmsEntityTextField, c);

        c.gridx = 0;
        c.gridy = 52;
        mainPanel.add(m_fmsCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 53;
        mainPanel.add(m_fmsCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 54;
        mainPanel.add(m_fmsEntityLabel, c);
        c.gridx = 0;
        c.gridy = 55;
        mainPanel.add(m_fmsEntityTextField, c);

        c.gridx = 0;
        c.gridy = 56;
        mainPanel.add(m_cmsCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 57;
        mainPanel.add(m_cmsCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 58;
        mainPanel.add(m_cmsEntityLabel, c);
        c.gridx = 0;
        c.gridy = 59;
        mainPanel.add(m_cmsEntityTextField, c);

        c.gridx = 0;
        c.gridy = 60;
        mainPanel.add(m_rmsCollectionLabel, c);
        c.gridx = 0;
        c.gridy = 61;
        mainPanel.add(m_rmsCollectionTextField, c);

        c.gridx = 0;
        c.gridy = 62;
        mainPanel.add(m_rmsEntityLabel, c);
        c.gridx = 0;
        c.gridy = 63;
        mainPanel.add(m_rmsEntityTextField, c);

        c.gridx = 0;
        c.gridy = 67;
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
        String outputWSHost = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_HOST).trim();
        if (!InstallUtil.isBlank(outputWSHost))
            m_outputWSHostTextField.setText(outputWSHost);

        String outputWSPort = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_OUTPUT_WS_PORT).trim();
        if (!InstallUtil.isBlank(outputWSPort))
            m_outputWSPortTextField.setText(outputWSPort);

        String webHost = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_WEB_HOST).trim();
        if (!InstallUtil.isBlank(webHost))
            m_EloquenceWebHostTextField.setText(webHost);

        String webPort = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_WEB_PORT).trim();
        if (!InstallUtil.isBlank(webPort))
            m_EloquenceWebPortTextField.setText(webPort);

        String intForms = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_INTERACTIVE_FORM).trim();
        if (!InstallUtil.isBlank(intForms))
            m_interactiveFormsTextField.setText(intForms);

        String inputResLoc = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_INPUT_RESOURCE_LOC).trim();
        if (!InstallUtil.isBlank(inputResLoc))
            m_inputResourceLocatorTextField.setText(inputResLoc);

        String defaultDevName = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_DEFAULT_DEVICE_NAME).trim();
        if (!InstallUtil.isBlank(defaultDevName))
            m_defaultDeviceNameTextField.setText(defaultDevName);

        String mapFileName = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_MAP_FILE_NAME).trim();
        if (!InstallUtil.isBlank(mapFileName))
            m_mapFileNameTextField.setText(mapFileName);

        String configPath = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_CONFIG_PATH).trim();
        if (!InstallUtil.isBlank(configPath))
            m_configPathTextField.setText(configPath);

        String previewArchivePath = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_ARCHIVE_PATH).trim();
        if (!InstallUtil.isBlank(previewArchivePath))
            m_previewArchivePathTextField.setText(previewArchivePath);

        String previewGeneralCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_COLLECTION).trim();
        if (!InstallUtil.isBlank(previewGeneralCollection))
            m_previewGeneralCollectionTextField.setText(previewGeneralCollection);

        String previewGeneralEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_GENERAL_ENTITY).trim();
        if (!InstallUtil.isBlank(previewGeneralEntity))
            m_previewGeneralEntityTextField.setText(previewGeneralEntity);

        String previewLocalDevice = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_LOCAL_DEVICE).trim();
        if (!InstallUtil.isBlank(previewLocalDevice))
            m_previewLocalDeviceTextField.setText(previewLocalDevice);

        String previewLogLevel = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_LOG_LEVEL).trim();
        if (!InstallUtil.isBlank(previewLogLevel))
            m_previewLogLevelTextField.setText(previewLogLevel);

        String previewNetworkDevice = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_NETWORK_DEVICE).trim();
        if (!InstallUtil.isBlank(previewNetworkDevice))
            m_previewNetworkDeviceTextField.setText(previewNetworkDevice);

        String previewVariableSet = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PREVIEW_VARIABLE_SET).trim();
        if (!InstallUtil.isBlank(previewVariableSet))
            m_previewVariableSetTextField.setText(previewVariableSet);

        String archivePath = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_ARCHIVE_PATH).trim();
        if (!InstallUtil.isBlank(archivePath))
            m_archivePathTextField.setText(archivePath);

        String generalCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_GENERAL_COLLECTION).trim();
        if (!InstallUtil.isBlank(generalCollection))
            m_generalCollectionTextField.setText(generalCollection);

        String generalEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_GENERAL_ENTITY).trim();
        if (!InstallUtil.isBlank(generalEntity))
            m_generalEntityTextField.setText(generalEntity);

        String localDevice = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_LOCAL_DEVICE).trim();
        if (!InstallUtil.isBlank(localDevice))
            m_localDeviceTextField.setText(localDevice);

        String logLevel = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_LOG_LEVEL).trim();
        if (!InstallUtil.isBlank(logLevel))
            m_logLevelTextField.setText(logLevel);

        String networkDevice = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_NETWORK_DEVICE).trim();
        if (!InstallUtil.isBlank(networkDevice))
            m_networkDeviceTextField.setText(networkDevice);

        String variableSet = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_VARIABLE_SET).trim();
        if (!InstallUtil.isBlank(variableSet))
            m_variableSetTextField.setText(variableSet);

        String cleanupAfterPreview = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_CLEANUP_AFTER_PREVIEW).trim();
        if (!InstallUtil.isBlank(cleanupAfterPreview))
            m_cleanupAfterPreviewLTextField.setText(cleanupAfterPreview);

        String pmsCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PMS_COLLECTION).trim();
        if (!InstallUtil.isBlank(pmsCollection))
            m_pmsCollectionTextField.setText(pmsCollection);

        String pmsEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_PMS_ENTITY).trim();
        if (!InstallUtil.isBlank(pmsEntity))
            m_pmsEntityTextField.setText(pmsEntity);

        String fmsCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_FMS_COLLECTION).trim();
        if (!InstallUtil.isBlank(fmsCollection))
            m_pmsCollectionTextField.setText(fmsCollection);

        String fmsEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_FMS_ENTITY).trim();
        if (!InstallUtil.isBlank(fmsEntity))
            m_pmsEntityTextField.setText(fmsEntity);

        String cmsCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_CMS_COLLECTION).trim();
        if (!InstallUtil.isBlank(cmsCollection))
            m_pmsCollectionTextField.setText(cmsCollection);

        String cmsEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_CMS_ENTITY).trim();
        if (!InstallUtil.isBlank(cmsEntity))
            m_pmsEntityTextField.setText(cmsEntity);

        String rmsCollection = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_RMS_COLLECTION).trim();
        if (!InstallUtil.isBlank(rmsCollection))
            m_pmsCollectionTextField.setText(rmsCollection);

        String rmsEntity = customCodePanelProxy.substitute(InstallConstants.VAR_ELOQUENCE_RMS_ENTITY).trim();
        if (!InstallUtil.isBlank(rmsEntity))
            m_pmsEntityTextField.setText(rmsEntity);
    }

    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("Eloquence Configuration Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        EloquenceInformationPanel newContentPane = new EloquenceInformationPanel();
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

    private JLabel m_outputWSHostLabel;

    private JTextField  m_outputWSHostTextField;

    private JLabel m_outputWSPortLabel;

    private JTextField  m_outputWSPortTextField;

    private JLabel m_EloquenceWebHostLabel;

    private JTextField m_EloquenceWebHostTextField;

    private JLabel m_EloquenceWebPortLabel;

    private JTextField m_EloquenceWebPortTextField;

    private JLabel m_interactiveFormsLabel;

    private JTextField m_interactiveFormsTextField;

    private JLabel m_inputResourceLocatorLabel;

    private JTextField m_inputResourceLocatorTextField;

    private JLabel m_defaultDeviceNameLabel;

    private JTextField m_defaultDeviceNameTextField;

    private JLabel m_mapFileNameLabel;

    private JTextField m_mapFileNameTextField;

    private JLabel m_configPathLabel;

    private JTextField m_configPathTextField;

    private JLabel m_previewArchivePathLabel;

    private JTextField m_previewArchivePathTextField;

    private JLabel m_previewGeneralCollectionLabel;

    private JTextField m_previewGeneralCollectionTextField;

    private JLabel m_previewGeneralEntityLabel;

    private JTextField m_previewGeneralEntityTextField;

    private JLabel m_previewLocalDeviceLabel;

    private JTextField m_previewLocalDeviceTextField;

    private JLabel m_previewLogLevelLabel;

    private JTextField m_previewLogLevelTextField;

    private JLabel m_previewNetworkDeviceLabel;

    private JTextField m_previewNetworkDeviceTextField;

    private JLabel m_previewVariableSetLabel;

    private JTextField m_previewVariableSetTextField;

    private JLabel m_archivePathLabel;

    private JTextField m_archivePathTextField;

    private JLabel m_generalCollectionLabel;

    private JTextField m_generalCollectionTextField;

    private JLabel m_generalEntityLabel;

    private JTextField m_generalEntityTextField;

    private JLabel m_localDeviceLabel;

    private JTextField m_localDeviceTextField;

    private JLabel m_logLevelLabel;

    private JTextField m_logLevelTextField;

    private JLabel m_networkDeviceLabel;

    private JTextField m_networkDeviceTextField;

    private JLabel m_variableSetLabel;

    private JTextField m_variableSetTextField;

    private JLabel m_cleanupAfterPreviewLabel;

    private JTextField m_cleanupAfterPreviewLTextField;

    private JLabel m_pmsCollectionLabel;

    private JTextField m_pmsCollectionTextField;

    private JLabel m_pmsEntityLabel;

    private JTextField m_pmsEntityTextField;

    private JLabel m_fmsCollectionLabel;

    private JTextField m_fmsCollectionTextField;

    private JLabel m_fmsEntityLabel;

    private JTextField m_fmsEntityTextField;

    private JLabel m_cmsCollectionLabel;

    private JTextField m_cmsCollectionTextField;

    private JLabel m_cmsEntityLabel;

    private JTextField m_cmsEntityTextField;

    private JLabel m_rmsCollectionLabel;

    private JTextField m_rmsCollectionTextField;

    private JLabel m_rmsEntityLabel;

    private JTextField m_rmsEntityTextField;
}
