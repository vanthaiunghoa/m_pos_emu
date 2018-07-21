/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

/**
 * C_logger_stdout uses the c_logger module to display logs on stdout (computers screen)
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_logger_stdout extends C_logger{
    
    /**
     * Log an information message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogInfo(String moduleName, String messageToLog) {
        String msg = C_logger.LogInfo(moduleName, messageToLog);        
        System.out.print(msg);
        return msg;
    }
    
    /** 
     * Log an information message when no type is specified
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String Log(String moduleName, String messageToLog) {
        String msg = C_logger.Log(moduleName, messageToLog);        
        System.out.print(msg);
        return msg;
    }
    
    /**
     * Log an error message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogError(String moduleName, String messageToLog) {
        String msg = C_logger.LogError(moduleName, messageToLog);        
        System.out.print(msg);
        return msg;
    }

    /**
     * Log a warning message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogWarning(String moduleName, String messageToLog) {
        String msg = C_logger.LogWarning(moduleName, messageToLog);        
        System.out.print(msg);
        return msg;
    }
    
}
