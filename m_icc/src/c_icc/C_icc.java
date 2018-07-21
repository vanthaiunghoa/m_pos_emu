package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_module;
import c_common.C_logger_stdout;
import c_common.C_err;

/**
 * C_icc is the module for Smart Card management. This class derives from c_module
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public abstract class C_icc extends C_module {

     /**
     * Smart-card management type
     *  SMARTCARD_VIRTUAL       There is neither physical reader, nor physical card, it is a virtual card
     *  SMARTCARD_PCSC          There is at least of reader and a card, accessed using PC/SC protocol
     */
    public static enum SmartCardManagementType
    {
        SMARTCARD_VIRTUAL,
        SMARTCARD_PCSC
    }
    
    /**
     * Smart-Card management Type
     */
    private SmartCardManagementType eSmartCardReaderType = SmartCardManagementType.SMARTCARD_VIRTUAL;  
    
    /**
     * Constructor of the module
     * @param name Name of the module
     */
    C_icc(String name){
        super(name);
    }

    /**
     * Returns the type of smart card management
     * @return Enum containing the type of smartcard management (virtual or real using PCSC)
     * @see #eSmartCardReaderType
     */
    public SmartCardManagementType getSmartCardReaderType() {
        return eSmartCardReaderType;
    }

    /**
     * Sets the type of smart card management
     * @param eSmartCardReaderType Type of smart-card management
     * @see #eSmartCardReaderType
     */
    public void setSmartCardReaderType(SmartCardManagementType eSmartCardReaderType) {
        this.eSmartCardReaderType = eSmartCardReaderType;
    }
        
    /**
     * Initialize the ICC module
     */
    @Override
    public void initModule() {
        C_logger_stdout.LogInfo(moduleName, "Module Initialization - Start");

        C_logger_stdout.LogInfo(moduleName, "Module Initialization - End");
    }
    
    /**
     * Returns the name of the connected reader
     * @return String containing the name of the connected reader
     */
    public abstract String IccGetReaderName();
    
    /**
     * Connects to a smart-card reader
     * @param szReaderName String containing the name of the reader to connect
     * @return  Index on the reader in use, if negative, an error occured
     */
    public abstract C_err.Icc IccConnectReader(String szReaderName);

    /**
     * Connects to a smart-card
     * @param readerId Identifier of the reader to use (integer)
     * @return  Index on the smart-card, if negative, an error occured
     */
    public abstract C_err.Icc IccConnectSmartCard(int readerId);
    
    /**
     * Returns the ATR of the connected card
     * @param readerId Index of the reader
     * @return String containing the card's ATR
     */
    public abstract String IccGetATR(int readerId);
    
    /**
     * Converts a byte array to a HEX string
     * @param bytes array to convert
     * @return String containing the array displayable
     */
    private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }    
}
