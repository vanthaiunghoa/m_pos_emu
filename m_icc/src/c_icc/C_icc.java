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
    public C_icc(String name){
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
     * Check if card present or not
     * @return  Boolean = true if card is present, = false is absent
     */
    public abstract boolean IccIsCardPresent();

    /**
     * Set card presence (in fact, insert or remove card virtually; not used for PCSC card)
     * @param pres =true if card is inserted, =false if card is removed
     */
    public abstract void IccSetCardPresent(boolean pres);
    
    /**
     * Connects to a smart-card
     * @return  Index on the smart-card, if negative, an error occured
     */
    public abstract C_err.Icc IccConnectSmartCard();
    
    /**
     * Disconnect from the card
     */
    public abstract void IccDisconnect();
    
    /**
     * Reset the smart-card (disconnect and reconnect)
     * @param readerId Index of the reader
     */
    public abstract void IccResetCard(int readerId);

    /**
     * Returns the ATR of the connected card
     * @return String containing the card's ATR
     */
    public abstract String IccGetATR();
    
    /**
     * <p>Performs the smart-card EMV selection based on provided AID</p>
     * This function will perform all the SELECT commands to the smart-card
     * @return String containing the selected AID
     */
    public abstract String IccPerformSelection();
    
    /**
     * Reads all the data in an EMV smart card (read BER TLV tags content)
     * @return String containing the card PAN
     */
    public abstract String IccReadCard();
    
    /**
     * Provide the PIN code to the card
     * @param strPin String containing the PIN code to present to the card
     * @return Status of the PIN entry
     */
    public abstract C_err.Icc IccPinVerify(String strPin);
}
