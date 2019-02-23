/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

/**
 * C_err is a class which contains all the error codes 
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_err {
    
    /**
     * ICC module error codes
     */
    public static enum Icc
    {
        ERR_ICC_OK,
        ERR_ICC_NO_READER,
        ERR_ICC_NO_CARD,
        ERR_ICC_CARD_CONNECTION,
        ERR_ICC_WRONG_PIN
    }
    
    /**
     * Configuration file error codes
     */
    public static enum ConfigFile
    {
        ERR_CONF_FILE_OK,
        ERR_CONF_FILE_NOT_FOUND,
        ERR_CONF_FILE_INVALID_PARAMETER
    }
    
    /**
     * ECR module error codes
     */
    public static enum Ecr
    {
        ERR_ECR_OK
    }
}
