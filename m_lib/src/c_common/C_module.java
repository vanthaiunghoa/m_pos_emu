/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

/**
 * C_module is the abstract base class for all modules in the pos_emu_modules package
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public abstract class C_module {

    /**
     * Name of the parameter file
     */
    private final String conf_file = "./param.json";
    
    /**
     * Name of the module
     */
    protected String moduleName;
    /**
     * Version of the module
     */
    protected String moduleVersion;
    /**
     * IP Port of the module
     */
    protected int modulePort;
    /**
     * Type of the module
     */
    protected String moduleType;
    /**
     * Parameter class
     */
    private C_param m_param;

    /**
     * Constructor of c_module class
     * @param name Name of the module
     */
    public C_module(String name) {
        m_param = new C_param(name, conf_file);
        m_param = m_param.ReadConfigFile();
                
        // Check if module name and parameter file are OK
        moduleName = m_param.GetModuleName();
        if (!moduleName.equals(name)) {
            C_logger_stdout.LogError(moduleName, "Module name inconsistant with parameter file");
        }
        moduleVersion = m_param.GetVersion();
        modulePort = m_param.getIp_port();
        moduleType = m_param.getModule_type();
    }
    
    /**
     * Abstract method which initialize the module. The implementation is in the derived classes (the module itself)
     */
    public abstract void initModule();
    
    /**
     * Returns the name of the module
     * @return      String containing the name of the module
     * @see         #setModuleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Set the name of the module
     * @param moduleName        String containing the name of the module to set
     * @see                     #getModuleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Returns the version of the module
     * @return      String containing the version of the module : format is X.Y.Z
     * @see         #setModuleVersion
     */
    public String getModuleVersion() {
        return moduleVersion;
    }

    /**
     * Set the version of the module
     * @param moduleVersion     String containing the version of the module to set (X.Y.Z)
     * @see                     #getModuleVersion
     */
    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    /**
     * Returns the port of the module
     * @return      Int containing the port of the module (IP Port)
     * @see         #setModulePort
     */
    public int getModulePort() {
        return modulePort;
    }

    /**
     * Set the port of the module
     * @param modulePort        Int containing the port of the module to set
     * @see                     #getModulePort
     */
    public void setModulePort(int modulePort) {
        this.modulePort = modulePort;
    }

    /**
     * Returns the type of the module (service or client)
     * @return      String containing the type of the module (service or client)
     * @see         #setModuleType
     */
    public String getModuleType() {
        return moduleType;
    }

    /**
     * Set the type of the module
     * @param moduleType        String containing the type of the module to set (service or client)
     * @see                     #getModulePort
     */
    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }
    
}
