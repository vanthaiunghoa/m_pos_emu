package c_ecr;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_module;
import c_common.C_logger_stdout;

/**
 * C_ecr is the module for the ECR protocol. This class derives from c_module
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public abstract class C_ecr extends C_module {
      
    /**
     * Constructor of the module
     * @param name Name of the module
     */
    C_ecr(String name){
        super(name);
    }
        
    /**
     * Initialize the ICC module
     */
    @Override
    public void initModule() {
        C_logger_stdout.LogInfo(moduleName, "Module Initialization - Start");        
    }
    
    /**
     * Return a command received from the ECR
     * @return String containing the ECR command
     */
    public abstract String WaitEcrCommand();

    /**
     * Send a response to the ECR
     * @param response Response to send to the ECR
     * @return Array of byte containing the answer to send to the ECR
     */
    public abstract byte[] AnswerToEcr(String response);
    
    /**
     * This method is the command interpreter which will call the correct module
     * to execute the command
     *
     * @param command String containing the command to execute
     * @return String containing the answer to this command
     */
    public abstract String executeCommand(String command);    
}
