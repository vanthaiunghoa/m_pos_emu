package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_err;

/**
 * C_icc_virtual is the module for virtual Smart-Card management (no real card and reader). This class derives from c_icc
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_icc_virtual extends C_icc {

    // Constant for virtual informations
    private final String CARD_ATR = "3B630000112233";
    private final String READER_NAME = "Virtual Reader";
    private final String SELECTED_AID = "A0000000421010";
    private final String VIRTUAL_CARD_PAN_NATIXIS = "4975431234567898";
    private final String CARD_PIN_CODE = "6789";

    /**
     * Index of the reader currently in use
     */
    private int nReaderIndex = 0;
    /**
     * String containing the name of the reader currently connected
     */
    private String readerName = "No reader";
       
    /**
     * Constructor of the module
     * @param name Name of the module
     */
    public C_icc_virtual(String name) {
        super(name);
        nReaderIndex = 0;        
    }
    
    /**
     * Returns the name of the connected reader
     * @return String containing the name of the connected reader
     */
    @Override
    public String IccGetReaderName() {
        return readerName;
    }
    
    /**
     * Connects to a smart-card reader
     * @param szReaderName String containing the name of the reader to connect
     * @return  Index on the reader in use, if negative, an error occured
     */
    @Override
    public C_err.Icc IccConnectReader(String szReaderName) {
        C_err.Icc nRet = C_err.Icc.ERR_ICC_OK;
        
        readerName = READER_NAME;
        nReaderIndex++;
        
        return nRet;
    }

    /**
     * Disconnect from the card
     */
    @Override
    public void IccDisconnect() {
        
    }
    
    /**
     * Reset the smart-card (disconnect and reconnect)
     * @param readerId Index of the reader
     */
    @Override
    public void IccResetCard(int readerId) {
    }    
    
    /**
     * Connects to a smart-card
     * @return  Index on the smart-card, if negative, an error occured
     */
    @Override
    public C_err.Icc IccConnectSmartCard() {
        C_err.Icc nRet = C_err.Icc.ERR_ICC_OK;
        
        // Check if reader is connected
        if (nReaderIndex < 1) {
            return C_err.Icc.ERR_ICC_NO_READER;
        }
               
        return nRet;
    }

    /**
     * Returns the ATR of the connected card
     * @return String containing the card's ATR
     */
    @Override
    public String IccGetATR() {
        String atrValue = CARD_ATR;
        return atrValue;
    }   
    
    /**
     * Performs the smart-card EMV selection based on provided AID
     * This function will perform all the SELECT commands to the smart-card
     * @return String containing the selected AID
     */
    @Override
    public String IccPerformSelection() {
        String selectedAID = SELECTED_AID;
        return selectedAID;
    }    

    /**
     * Reads all the data in an EMV smart card (read BER TLV tags content)
     * @return String containing the card PAN
     */
    @Override
    public String IccReadCard() {
        String pan = VIRTUAL_CARD_PAN_NATIXIS;
        return pan;
    }
    
    /**
     * Provide the PIN code to the card
     * @param strPin String containing the PIN code to present to the card
     * @return Status of the PIN entry
     */
    @Override
    public C_err.Icc IccPinVerify(String strPin) {
        C_err.Icc nRet = C_err.Icc.ERR_ICC_OK;
        
        // Check if PIN code is correct
        if (strPin.equals(CARD_PIN_CODE) == false) {
            nRet =  C_err.Icc.ERR_ICC_WRONG_PIN;
        }
               
        return nRet;
    }    
}
