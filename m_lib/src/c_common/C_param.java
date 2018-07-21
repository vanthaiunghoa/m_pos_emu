/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

// For GSON library (JSON parse)
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * C_param is the module which gets parameters from the parameter file
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_param {

    ///////////////////////////////////////////////////////

    /**
     * Name of the module in the parameter file
     */
    private String module_name;
    /**
     * Protocol version of the module in the parameter file
     */
    private String module_version;
    /**
     * Type of module (Client/server/appli)
     */
    private String module_type;
    /**
     * IP Port for this module 
     */
    private int ip_port;

    /**
     * Filename of the configuration file
     */
    private final String conf_file;
 
    /**
     * Type of the reader : "virtual" or "pcsc"
     */
    private String reader_type;
  
    ///////////////////////////////////////////////////////

    /**
     * Constructor
     * @param name Name of the module
     * @param the_file Name of the parameter file
     */
    C_param(String name, String the_file) {
        this.module_name = name;
        conf_file = the_file;
    }

    /**
     * Returns the name of the parameter filename
     * @return String equals to the parameter filename
     */
    public String getFilename() {
        return conf_file;
    }

    /**
     * Returns the type of the reader : pscs or virtual
     * @return String containing the type of reader
     */
    public String getReader_type() {
        return reader_type;
    }

    /**
     * Set the type of the reader
     * @param reader_type String containing the type of reader : "virtual" or "pcsc"
     */
    public void setReader_type(String reader_type) {
        this.reader_type = reader_type;
    }
    
    /**
     * Return the value of a parameter form the parameter file
     * @return Class C_param containing all the parameters of the file
     */
    public C_param ReadConfigFile() {
        BufferedReader bufferedReader = null;
        
        try {
            bufferedReader = new BufferedReader(new FileReader(conf_file));        
        } catch(FileNotFoundException e) {
            String msg = "Configuration File not found ! - error " + e;
            C_logger_stdout.LogError(module_name, msg);
        }
        
        // Parse the JSON file
        Gson gson = new Gson();
        return gson.fromJson(bufferedReader, C_param.class);
    }

    /**
     * Return the module name
     * @return String containing the name of the module
     */
    public String GetModuleName()
    {
        if (module_name == null)
            module_name = "m_xxx";
        
        return module_name;
    }    
    
    /**
     * Returns the version of the module
     * @return String containing the version in X.Y.Z format
     */
    public String GetVersion()
    {
        if (module_version == null)
            module_version = "X.Y.Z";
        
        return module_version;
    }       
    
    /**
     * Returns the type of the module
     * @return String containing the type of module : "server", "client", "appli"
     */
    public String getModule_type() {
        if (module_type == null)
            module_type = "server";
        
        return module_type;
    }

    /**
     * Returns the IP port on which the module is listening
     * @return Integrer value containing the IP port
     */
    public int getIp_port() {
        return ip_port;
    }
    
}
