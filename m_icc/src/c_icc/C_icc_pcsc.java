package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_conv;
import c_common.C_err;
import c_common.C_logger_stdout;
import com.payneteasy.tlv.BerTag;
import com.payneteasy.tlv.BerTlv;
import com.payneteasy.tlv.BerTlvParser;
import com.payneteasy.tlv.BerTlvs;
import com.payneteasy.tlv.HexUtil;
import java.util.ArrayList;
import java.util.List;
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
    private int m_aid_nb;
    private byte[] m_selectedAid;
    
    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructor of the module
     * @param name Name of the module
     */
    C_icc_pcsc(String name) {
        super(name);
        nReaderIndex = 0;        
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PUBLIC FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////
    
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

        // Deactivate T0 and T1 GetResponse as it sends the same CLA as 
        // the command. While GetResponse is always with CLA=0x00
	System.setProperty("sun.security.smartcardio.t0GetResponse", "false");
	System.setProperty("sun.security.smartcardio.t1GetResponse", "false");
        
        // Create reader Factory
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
	CardTerminals cardTerminals = terminalFactory.terminals();
        
        // Connect to reader and get its name
        try {
            m_terminal = cardTerminals.list().get(0);
            readerName = m_terminal.toString();
            nReaderIndex++;
        } catch (CardException ex) {
            nRet = C_err.Icc.ERR_ICC_NO_READER;
        }
               
        return nRet;
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
     * Disconnect from the card
     */
    @Override
    public void IccDisconnect() {
        try {
            C_logger_stdout.LogInfo(moduleName, "Card disconnect");
            m_card.disconnect(true);
        } catch (CardException ex) {
            Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Reset the smart-card (disconnect and reconnect)
     * @param readerId Index of the reader
     */
    @Override
    public void IccResetCard(int readerId) {
        try {
            // Reset the card before final select
            C_logger_stdout.LogInfo(moduleName, "Card reset");
            m_card.disconnect(true);
            // Connect to the card using every protocol ("T=0", "T=1" or "*" for both)
            m_card = m_terminal.connect("*");
            m_channel = m_card.getBasicChannel();
        } catch (CardException ex) {
            Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the ATR of the connected card
     * @return String containing the card's ATR
     */
    @Override
    public String IccGetATR() {
        String atrValue;
        
        // Return the card ATR
        ATR cardATR = m_card.getATR();
        atrValue = C_conv.bytesToHex(cardATR.getBytes());
                
        return atrValue;
    }

    /**
     * Performs the smart-card EMV selection based on provided AID
     * This function will perform all the SELECT commands to the smart-card
     * @return String containing the selected AID
     */
    @Override
    public String IccPerformSelection() {
        C_aid myAid;
        m_selectedAid = null;
        
        // First try PSE selection
        myAid = IccPerformPseSelection();
        if (myAid == null) {
            // PSE is not present (or empty), so try explicit selection
            myAid = IccPerformExplicitSelection();
        }
        m_selectedAid = myAid.m_aid;
        
        // Perform final selection
        
        // Check number of AIDs in common
        C_logger_stdout.LogInfo(moduleName, "nbAid=" + m_aid_nb);
        
        // Perform Final selection
        if (m_aid_nb > 0) {
            C_logger_stdout.LogInfo(moduleName, "EMV - Final Select");
            ResponseAPDU rspIcc = IccSendSelectCommand(m_selectedAid);
        }
                
        return C_conv.bytesToHex(myAid.m_aid);
    }
    
    
    /**
     * Performs the smart-card EMV PSE selection based on PSE AID
     * PSE is : 1PAY.SYS.DDF01 (31 50 41 59 2E 53 59 53 2E 44 44 46 30 31)
     * This function will perform the SELECT PSE command to the smart-card, followed by a serie of ReadRecord commands
     * Until we get an error 
     * @return String containing the selected AID
     */
    private C_aid IccPerformPseSelection() {
        
        C_aid myAid = null;
        
        // List of AID
        byte[] valuePSE= {(byte)0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31};
        C_aid aidPse = new C_aid ("PSE", valuePSE, (byte)0xFE, (byte)0x01);
        
        // Start selection
        C_logger_stdout.LogInfo(moduleName, "EMV - Start PSE Selection");
        ResponseAPDU rspIcc = IccSendSelectCommand(aidPse.m_aid);
        if(rspIcc.getSW() == 0x9000)
        {
            // a PSE is present
            // Parse READ-RECORDS => tag 88 in template A5 contains SFI
            // And first record to read. Then read until an error occurs (6A83)
            m_aid_nb = 0;
            byte[] valueCB2= {(byte)0xA0, 0x00, 0x00, 0x00, 0x42, 0x20, 0x10};
            myAid = new C_aid ("CB2", valueCB2, (byte)0xFE, (byte)0x01);
            
            // Parse the response : BER TLV format
            byte[] sfi_to_read = IccGetBerTlvTagValueInBytes(rspIcc.getBytes(), 0x88);            
            byte sfi = GetSfiValue(sfi_to_read[0]);
            
            byte record = 1;
            int ret = 0x9000;
            while (ret == 0x9000) {
                C_logger_stdout.LogInfo(moduleName, "SFI=" + sfi + " - record=" + record);
                rspIcc = IccSendReadRecordCommand(record, sfi);
                ret = rspIcc.getSW();
                if (ret == 0x9000) {
                    record++;
                    m_aid_nb++;
                    
                }
            }
        }
        
        return myAid;
    }

    /**
     * Performs the smart-card EMV Explicit selection based on provided AID
     * This function will perform all the SELECT  commands to the smart-card
     * @return String containing the selected AID
     */
    private C_aid IccPerformExplicitSelection() {
        int aid_nb = 0;
        int selectedAidIndex = -1;
        long prio = 0;
        
        // List of AID
        byte[] valueCB = {(byte)0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10};
        byte[] valueMC = {(byte)0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10};
        byte[] valueV  = {(byte)0xA0, 0x00, 0x00, 0x00, 0x03, 0x10, 0x10};
        
        ArrayList<C_aid> aidList = new ArrayList<>(C_aid.AID_MAX_NUMBER);
        aidList.add(new C_aid ("CB",        valueCB , (byte)0xFE, (byte)0x01));
        aidList.add(new C_aid ("MasterCard",valueMC , (byte)0x7F, (byte)0x01));
        aidList.add(new C_aid ("VISA",      valueV  , (byte)0x7F, (byte)0x01));
        
        // Start selection
        C_logger_stdout.LogInfo(moduleName, "EMV - Start Explicit Selection");
        for (int i = 0; i < aidList.size(); i++) {

            ResponseAPDU rspIcc = IccSendSelectCommand(aidList.get(i).m_aid);
            if (rspIcc.getSW() == 0x9000)
            {
                aid_nb++;
                long aid_prio = C_conv.getUnsigned(aidList.get(i).m_priority & 0x000000FF);
                if (aid_prio > prio) {
                    prio = aid_prio;
                    m_selectedAid = aidList.get(i).m_aid;
                    selectedAidIndex = i;
                }
            }
        }

        // Check number of AIDs in common
        C_logger_stdout.LogInfo(moduleName, "nbAid=" + aid_nb);

        // Reset card before final select
        IccResetCard(0);
        
        return aidList.get(selectedAidIndex);
    }
    
    /**
     * Sends a SELECT (INS=A4) command to the smart card
     * @param aid byte[] containing the AID to select
     * @return ResponseAPDU containing the response
     */
    private ResponseAPDU IccSendSelectCommand(byte[] aid) {
        ResponseAPDU rsp;
        // Construct APDU with CLA INS P1 P2 + BODY
        CommandAPDU cmd = new CommandAPDU(0x00, (int)0xA4, 0x04, 0x00, aid);
        
        // Send Select command
        rsp = IccSendApduCommand(cmd);
   
        return rsp;
    }

    /**
     * Send a READ RECORD command to the smart card (INS = B2)
     * @param p1 P1 of the APDU header - SFI (file) to read
     * @param p2 P2 of the APDU header - record to read
     * @return ResponseAPDU command containing the smart card response
     */
    private ResponseAPDU IccSendReadRecordCommand(byte p1, byte p2) {
        ResponseAPDU rsp;
        byte[] cmd_apdu = {0x00, (byte)0xB2, 0x01, 0x0C, 0x00};
        cmd_apdu[2] = p1;
        cmd_apdu[3] = p2;
        // Construct APDU with CLA INS P1 P2 + BODY
        CommandAPDU cmd = new CommandAPDU(cmd_apdu);
        
        // Send Select command
        rsp = IccSendApduCommand(cmd);
   
        return rsp;
    }

    /**
     * Send a GPO (GET PROCESSING OPTION) command to the smart card (INS = A8)
     * @return ResponseAPDU command containing the smart card response
     */
    private ResponseAPDU IccSendGpoCommand() {
        ResponseAPDU rsp;
        byte[] body_apdu = {(byte)0x83, 0x00};
        int CLA_GPO = 0x80;
        int INS_GPO = 0xA8;
        
        // Construct APDU with CLA INS P1 P2 + BODY
        CommandAPDU cmd = new CommandAPDU(CLA_GPO, INS_GPO, 0x00, 0x00, body_apdu);
        
        // Send Select command
        rsp = IccSendApduCommand(cmd);
   
        return rsp;
    }
       
    /**
     * Reads all the data in an EMV smart card (read BER TLV tags content)
     * 
     *
     * <p>AFL is composed of several SFI (files) wich are 4 bytes each</p>    
     * <p>Each SFI has to be read from left to right</p>
     * <p>BYTE 1 : XXXXX000 = XXXXX = SFI</p>
     * <p>BYTE 2 : XXXXXXXX = First record to read for that SFI</p>
     * <p>BYTE 3 : XXXXXXXX = Last record to read for that SFI</p>
     * <p>BYTE 4 : XXXXXXXX = Number of records for SDA</p>
     * <p> </p>
     * <p>Example : AFL = 94 0C 08 01 02 01 10 01 03 00 18 01 02 01 </p>
     * <p>3 SFI : 08 (file 1), 10 (file 2), 18 (file 3)</p>
     * <p>Records 1 and 2 for SFI 1</p>
     * <p>Records 1, 2 and 3 for SFI 2</p>
     * <p>Records 1 and 2 for SFI 3</p>
     * <p> </p>
     * <p>Second perform READ RECORD commands according to AFL</p>
     * <p>00 B2 P1 P2 00 </p>
     * <p>- P1 = record number in the SFI</p>
     * <p>- P2 = XXXXX100 : XXXXX = SFI</p>
     * <p> </p>
     * <p>Example: to read the record 2 of file 3 (00011100)</p>
     * <p>- 00 B2 02 1C 02</p>
     * @return String containing the card PAN
     */
    @Override
    public String IccReadCard() {
        String str_pan = "";
                
        // First perform a GPO with empty PDOL
        ResponseAPDU rsp;
        
        // Construct APDU with CLA INS P1 P2 + BODY
        C_logger_stdout.LogInfo(moduleName, "EMV - GPO");
        rsp = IccSendGpoCommand();
        if (rsp.getSW() != 0x9000) {
            return str_pan;
        }
        
        // Get AIP
        BerTlv aip = IccGetBerTlvTagValue(rsp.getBytes(), 0x82);
        C_logger_stdout.LogInfo(moduleName, "EMV - aip=" + aip.getHexValue());

        // Get AFL
        BerTlv afl = IccGetBerTlvTagValue(rsp.getBytes(), 0x94);
        byte[] byte_afl = afl.getBytesValue();
        String str_afl = afl.getHexValue();
        C_logger_stdout.LogInfo(moduleName, "EMV - afl=" + str_afl);

        // Each SFI is 4 bytes (so divide by 2 for ascii characters)
        int nb_files = str_afl.length() / 2;
          
        // Loop in all SFI
        int i = 0;
        BerTlvParser data = new BerTlvParser();
        while (i < nb_files) {
            // Get data
            byte sfi = (byte)(byte_afl[i++] | 0x04);
            byte start_record = byte_afl[i++];
            byte end_record = byte_afl[i++];
            i++;
            
            // Loop in all records
            for (byte record = start_record; record <= end_record; record++) {
                C_logger_stdout.LogInfo(moduleName, "SFI=" + sfi + " - record=" + record);
                rsp = IccSendReadRecordCommand(record, sfi);

                // Display Cardholder name
                BerTlv cardholder_name = IccGetBerTlvTagValue(rsp.getBytes(), 0x5F, 0x20);
                if (cardholder_name != null) {
                    C_logger_stdout.LogInfo(moduleName, "EMV - Name=" + cardholder_name.getTextValue());
                }

                // Display PAN
                BerTlv pan = IccGetBerTlvTagValue(rsp.getBytes(), 0x5A);
                if (pan != null) {
                    str_pan = pan.getHexValue();
                    C_logger_stdout.LogInfo(moduleName, "EMV - PAN=" + str_pan);
                }
            }
        }       
        
        return str_pan;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // PRIVATE FUNCTIONS
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sends a command to the smart card
     * This command performs an automatic GetResponse command if SW=0x61XX
     * @param command CommandAPDU containing the APDU command to send
     * @return ResponseAPDU containing the smart card response
     */
    private ResponseAPDU IccSendApduCommand(CommandAPDU command) {
        ResponseAPDU answer = null;
        try {
            C_logger_stdout.LogInfo(moduleName, "IccCmd=" + HexUtil.toHexString(command.getBytes()));
            answer = m_channel.transmit(command);
            
            if (answer.getSW1() == 0x61) {
                // Perform a GetResponse command
                answer = m_channel.transmit(new CommandAPDU(0x00, 0xC0, 0x00, 0x00, answer.getSW2()));
            }
            if (answer.getSW1() == 0x6C) {
                // Perform a GetResponse command
                byte[] newCmd = command.getBytes();
                newCmd[4] = (byte)answer.getSW2();
                answer = m_channel.transmit(new CommandAPDU(newCmd));
            }
            C_logger_stdout.LogInfo(moduleName, "IccRsp=" + HexUtil.toHexString(answer.getBytes()));
        } catch (CardException ex) {
            Logger.getLogger(C_icc_pcsc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;                
    }
    
    /**
     * Returns the SFI value to use according to red SFI
     * @param sfi SFI red from AFL or PSE responses
     * @return SFI to use for ReadRecords (XXXXX100)
     */
    private byte GetSfiValue(byte sfi) {
        int retSfi = sfi << 3;
        retSfi = retSfi | 0x04;
        
        return (byte)retSfi;
    }
    
    /**
     * Return the byte buffer containing the value of a BER TLV tag
     * @param buffer BER TLV input buffer to parse
     * @param tag tag to find (only for one byte tag)
     * @return byte[] containing the value of the tag
     */    
    private byte[] IccGetBerTlvTagValueInBytes(byte[] buffer, int tag) {
        return IccGetBerTlvTagValueInBytes(buffer, tag, 0);
    }

    /**
     * Return the byte buffer containing the value of a BER TLV tag
     * @param buffer BER TLV input buffer to parse
     * @param tag1 First byte of the tag to find
     * @param tag2 Second byte of the tag to find
     * @return byte[] containing the value of the tag
     */
    private byte[] IccGetBerTlvTagValueInBytes(byte[] buffer, int tag1, int tag2) {
        BerTlv tag_val = IccGetBerTlvTagValue(buffer, tag1, tag2);            
        byte[] tag_value = tag_val.getBytesValue();        
        return tag_value;
    }

    /**
     * Return a BerTlv containing the value of a BER TLV tag
     * @param buffer BER TLV input buffer to parse
     * @param tag tag to find (only for one byte tag)
     * @return BerTlv containing the value of the tag
     */    
    private BerTlv IccGetBerTlvTagValue(byte[] buffer, int tag) {
        return IccGetBerTlvTagValue(buffer, tag, 0);
    }
    
    /**
     * Return a BerTlv containing the value of a BER TLV tag
     * @param buffer BER TLV input buffer to parse
     * @param tag1 First byte of the tag to find
     * @param tag2 Second byte of the tag to find
     * @return BerTlv containing the value of the tag
     */
    private BerTlv IccGetBerTlvTagValue(byte[] buffer, int tag1, int tag2) {
        // Parse the response : BER TLV format
        BerTlvParser parser = new BerTlvParser();
        BerTlvs tlvs = parser.parse(buffer, 0, buffer.length);
  
        // Get SFI to read
        BerTag tag;
        if (tag2 != 0) {
            tag = new BerTag(tag1, tag2);
        } else {
            tag = new BerTag(tag1);            
        }
        BerTlv tag_val = tlvs.find(tag);
        
        return tag_val;
    }   
    
}
