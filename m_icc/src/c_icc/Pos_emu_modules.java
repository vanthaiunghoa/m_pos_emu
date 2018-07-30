package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_logger_stdout;
import c_common.C_err;
import com.payneteasy.tlv.HexUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;


/**
 *
 * @author balacahan
 */
public class Pos_emu_modules {

    public static final int CLS_GPO = 0x80; 
    public static final int INS_GPO = 0xA8; 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws CardException {
        // Icc type
        C_icc.SmartCardManagementType smartCardType = C_icc.SmartCardManagementType.SMARTCARD_PCSC;
        // Create ICC module
        C_icc m_icc;
        C_err.Icc retIcc;
        String response;

        /* TO REMOVE TODO FOR TEST */
        CardTerminal m_terminal;
        Card m_card;
        CardChannel m_channel = null;

        ResponseAPDU answer = null;
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
	CardTerminals cardTerminals = terminalFactory.terminals();

        m_terminal = cardTerminals.list().get(0);
        String readerName = m_terminal.toString();
        try {
            m_card = m_terminal.connect("*");
            m_channel = m_card.getBasicChannel();
        } catch (CardException ex) {
            Logger.getLogger(Pos_emu_modules.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] valueCB = {(byte)0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10};
        byte[] valueMC = {(byte)0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10};
        byte[] valueV  = {(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10};
           
        
        CommandAPDU cmd = new CommandAPDU(0x00, (byte)0xA4, 0x04, 0x00, valueV);
        C_logger_stdout.LogInfo("TEST", "IccCmd=" + HexUtil.toHexString(cmd.getBytes()));
        answer = m_channel.transmit(cmd);
        C_logger_stdout.LogInfo("TEST", "IccRsp=" + HexUtil.toHexString(answer.getBytes()));

        byte[] trame = {(byte)0x83, 0x00};
        
        cmd = new CommandAPDU(CLS_GPO,INS_GPO, 0, 0, trame, 0x0C);
        
        int cla = cmd.getCLA();
        
        C_logger_stdout.LogInfo("TEST", "IccCmd=" + HexUtil.toHexString(cmd.getBytes()));
        answer = m_channel.transmit(cmd);
        C_logger_stdout.LogInfo("TEST", "IccRsp=" + HexUtil.toHexString(answer.getBytes()));

        return;
        /* */
        
        /*
        // According to the parameter, use PC/SC or virtual Smart-Card
        if (smartCardType == C_icc.SmartCardManagementType.SMARTCARD_PCSC) {
            m_icc = new C_icc_pcsc("m_icc");
        } else {
            m_icc = new C_icc_virtual("m_icc");
        }
        
        // Initialize module
        String module_name = m_icc.getModuleName();
        C_logger_stdout.LogInfo(module_name, "Module Created");

        m_icc.initModule();
        C_logger_stdout.LogInfo(module_name, "Module Initialization Done");
        
        retIcc = m_icc.IccConnectReader(null);
        if (C_err.Icc.ERR_ICC_OK == retIcc) {
            C_logger_stdout.LogInfo(module_name, "Reader Connected : " + m_icc.IccGetReaderName());
            
            retIcc = m_icc.IccConnectSmartCard();
            if (C_err.Icc.ERR_ICC_OK == retIcc) {
                C_logger_stdout.LogInfo(module_name, "Card Connected - ATR=" + m_icc.IccGetATR());
                
                // Perform selection
                response = m_icc.IccPerformSelection();
                if (response == null) {
                    C_logger_stdout.LogInfo(module_name, "No AID in common");
                } else {
                    C_logger_stdout.LogInfo(module_name, "Selected AID is " + response);    
                    
                    // Perform card reading
                    response = m_icc.IccReadCard();
                    
                    // Disconnect
                    m_icc.IccDisconnect();
                }
            } else {
                if (C_err.Icc.ERR_ICC_NO_CARD == retIcc) {
                    C_logger_stdout.LogWarning(module_name, "No card present");
                } else {
                    C_logger_stdout.LogError(module_name, "Error problem connecting card");
                }
            }
                
        } else {
            C_logger_stdout.LogError(module_name, "Problem connecting to reader");
        }
        */
    }    
}
