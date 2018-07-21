/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * C_logger is the module which provides logger functions (log/traces)
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_logger {
    
    /**
     * Levels of log
     *  LOG_LEVEL_NONE          Nothing is logged
     *  LOG_LEVEL_ERR           Only error messages are logged
     *  LOG_LEVEL_WARNING       Error messages and Warnings are logged
     *  LOG_LEVEL_INFO          Info, Warning and Error messages are logged
     *  LOG_LEVEL_ALL           Everything is logged
     */
    public static enum LogType
    {
        LOG_LEVEL_NONE,
        LOG_LEVEL_ERR,
        LOG_LEVEL_WARNING,
        LOG_LEVEL_INFO,
        LOG_LEVEL_ALL
    }
    
    private static final String LOGGER_VERSION = "0.0.1";

    /**
     * Returns the version of the logger (X.Y.Z)
     * @return String containing the version of the logger
     */
    public static String getLoggerVersion() {
        return LOGGER_VERSION;
    }

    /**
     * Log an information message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogInfo(String moduleName, String messageToLog) {
        return CreateLogString(moduleName, messageToLog, LogType.LOG_LEVEL_INFO);
    }
    
    /** 
     * Log an information message when no type is specified
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String Log(String moduleName, String messageToLog) {
        return LogInfo(moduleName, messageToLog);
    }
    
    /**
     * Log an error message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogError(String moduleName, String messageToLog) {
        return CreateLogString(moduleName, messageToLog, LogType.LOG_LEVEL_ERR);
    }

    /**
     * Log a warning message
     * @param moduleName Name of the module we are logging
     * @param messageToLog Message to log
     * @return String containing the log message
     */
    public static String LogWarning(String moduleName, String messageToLog) {
        return CreateLogString(moduleName, messageToLog, LogType.LOG_LEVEL_WARNING);
    }
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a string with logging information : moduleName | date/time | log Level | Log Message
     * @param moduleName Name of the module currently logged
     * @param messageToLog Log message 
     * @param logType Type of the Log : ERROR, WARN, INFO
     * @return String with the logging information
     */
    private static String CreateLogString(String moduleName, String messageToLog, LogType logType) {
        String logMessage;
        String strType;
        
        // Add date/time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        logMessage = dtf.format(now) + " : ";
        
        // Add module name
        logMessage += moduleName + " : ";
        
        // Add log level
        strType = "INFO";
        if (logType == LogType.LOG_LEVEL_ERR) {
            strType = "*ERR";
        } else if (logType == LogType.LOG_LEVEL_WARNING) {
            strType = "WARN";
        }
        logMessage += strType+": ";
        
        // Add the message to log
        logMessage += messageToLog + "\n";
        
        // Display the log
        return logMessage;
    }

}
