package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_conv;
import c_common.C_err;
import c_common.C_logger_stdout;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 * C_icc_pcsc is the module for real Smart-Card management using PC/SC. This class derives from c_icc
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_icc_pcsc extends C_icc {
    
    /**
     * Index of the reader currently in use
     */
    private int nReaderIndex = 0;
    
    private CardTerminal m_terminal;
    private Card m_card;
    private CardChannel m_channel;
    private String readerName = "No reader";
       
    /**
     * Constructor of the module
     * @param name Name of the module
     */
    C_icc_pcsc(String name) {
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
        
        // Create reader Factory
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
	CardTerminals cardTerminals = terminalFactory.terminals();
        
        // Connect to reader and get its name
        try {
            m_terminal = cardTerminals.list().get(0);
            readerName = m_terminal.toString();
            nReaderIndex++;
        } catch (CardException ex) {
//            Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
            nRet = C_err.Icc.ERR_ICC_NO_READER;
        }
               
        return nRet;
    }

    /**
     * Connects to a smart-card
     * @param readerId Identifier of the reader to use (integer)
     * @return  Index on the smart-card, if negative, an error occured
     */
    @Override
    public C_err.Icc IccConnectSmartCard(int readerId) {
        C_err.Icc nRet = C_err.Icc.ERR_ICC_OK;
        
        // Check if reader is connected
        if (nReaderIndex < 1) {
            return C_err.Icc.ERR_ICC_NO_READER;
        }
        
        try {
            // Connect to the card using every protocol ("T=0", "T=1" or "*" for both)
            m_card = m_terminal.connect("*");
            m_channel = m_card.getBasicChannel();
        } catch (CardException ex) {
            try {
                // Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
                // Check if card present or if other error
                if (m_terminal.isCardPresent() == true) {
                    nRet = C_err.Icc.ERR_ICC_CARD_CONNECTION;
                } else {                
                    nRet = C_err.Icc.ERR_ICC_NO_CARD;
                }
            } catch (CardException ex1) {
                nRet = C_err.Icc.ERR_ICC_CARD_CONNECTION;
            }
        }
        
        return nRet;
    }

    /**
     * Returns the ATR of the connected card
     * @param readerId Index of the reader
     * @return String containing the card's ATR
     */
    @Override
    public String IccGetATR(int readerId) {
        String atrValue;
        
        // Return the card ATR
        ATR cardATR = m_card.getATR();
        atrValue = C_conv.bytesToHex(cardATR.getBytes());
                
        return atrValue;
    }

    /**
     * Performs the smart-card EMV selection based on provided AID
     * This function will perform all the SELECT commands to the smart-card
     * @param readerId Index of the reader
     * @return String containing the selected AID
     */
    @Override
    public String IccPerformSelection(int readerId) {
        String selectedAID = null;
        int aid_nb = 0;
        
        // List of AID
        byte[] valueCB = {(byte)0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10};
        byte[] valueMC = {(byte)0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10};
        byte[] valueV  = {(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10};
        
        ArrayList<C_aid> aidList = new ArrayList<>(C_aid.AID_MAX_NUMBER);
        aidList.add(new C_aid ("CB",        valueCB , (byte)0xFE, (byte)0x01));
        aidList.add(new C_aid ("MasterCard",valueMC , (byte)0x7F, (byte)0x01));
        aidList.add(new C_aid ("VISA",      valueV  , (byte)0x7F, (byte)0x01));
        
        // Start selection
        for (int i = 0; i < aidList.size(); i++) {
            ResponseAPDU rspIcc = IccSendSelectCommand(aidList.get(i));
            if(rspIcc.getSW() == 0x9000)
            {
                selectedAID = aidList.get(i).m_name;
                aid_nb++;
            }
        }

        // Check number of AIDs in common
        C_logger_stdout.LogInfo(moduleName, "nbAid=" + aid_nb);
        
        return selectedAID;
    }
    
    /**
     * Sends a SELECT (INS=A4) command to the smart card
     * @param aid Parameter of C_aid type containing the AID to select
     * @return ResponseAPDU containing the response
     */
    private ResponseAPDU IccSendSelectCommand(C_aid aid) {
        ResponseAPDU rsp;
        // Construct APDU with CLA INS P1 P2 + BODY
        CommandAPDU cmd = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, aid.m_aid);
        
        // Send Select command
        rsp = IccSendApduCommand(cmd);
   
        return rsp;
    }
    
    /**
     * Sends a command to the smart card
     * @param command CommandAPDU containing the APDU command to send
     * @return ResponseAPDU containing the smart card response
     */
    private ResponseAPDU IccSendApduCommand(CommandAPDU command) {
        ResponseAPDU answer = null;
        try {
            C_logger_stdout.LogInfo(moduleName, "IccCmd=" + command.toString());
            answer = m_channel.transmit(command);
            C_logger_stdout.LogInfo(moduleName, "IccRsp=" + answer.toString());
        } catch (CardException ex) {
            Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;                
    }
}
