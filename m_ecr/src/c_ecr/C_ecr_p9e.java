/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_ecr;

import c_common.C_conv;
import c_common.C_crc16;
import c_common.C_logger_stdout;
import c_common.C_tcp_server;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * C_ecr_p9e is the protocol 9 extended implementation of the ECR protocol. This
 * class derives from C_ecr
 *
 * @author Bulent Alacahan
 * @version %I%, %G%
 * @since 0.1
 */
public class C_ecr_p9e extends C_ecr {

    private C_tcp_server m_server;
    
    // P9 Instruction list
    private final byte INS_F06_LIST_SOFTWARE    = 0x06;
    private final byte INS_F07_SPECIFIC_OP      = 0x07;
    
    /**
     * Constructor of the module
     *
     * @param name Name of the module
     */
    C_ecr_p9e(String name) {
        super(name);
    }
     
    /**
     * Initialize the ICC module
     */
    @Override
    public void initModule() {
        super.initModule();
        m_server = new C_tcp_server(moduleName, modulePort);
        C_logger_stdout.LogInfo(moduleName, "Module Initialization - End");
    }    
    
    /**
     * Return a command received from the ECR
     *
     * @return String containing the ECR command
     */
    @Override
    public String WaitEcrCommand() {
        String request = "";

        // Start the TCP server
        m_server.StartTCPServer();
        try {
            // Wait the command from the ECR
            request = m_server.WaitTcpMessage();

        } catch (Exception ex) {
            Logger.getLogger(C_ecr_p9e.class.getName()).log(Level.SEVERE, null, ex);
        }

        return request;
    }
   
    /**
     * Send a response to the ECR
     * @param response Response to send to the ECR
     * @return String containing the encapsulated response
     */
    @Override
    public byte[] AnswerToEcr(String response) {
        byte[] p9resp;
        
        // P9 transport protocol
        p9resp = addP9Encapsulation(response);
        
        // Send response to the ECR
        m_server.SendTcpResponse(p9resp);
        return p9resp;
    }
    
    /**
     * This method is the command interpreter which will call the correct module
     * to execute the command
     *
     * @param command String containing the command to execute
     * @return String containing the answer to this command
     */
    @Override
    public String executeCommand(String command) {
        String answer;
        byte ins = getInstruction(command);

        switch (ins) {
            case INS_F06_LIST_SOFTWARE:
                answer = "061234";
                break;
            default:
                answer = "ERROR";
                break;
        }

        return answer;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Private functions
    
    /**
     * Provide the P9 instruction from a received command
     *
     * @param command String with the command
     * @return byte containing the instruction to execute
     */
    private byte getInstruction(String command) {
        String strIns;
        byte ins;
        byte[] bytes;

        strIns = command.substring(1, 3);
        bytes = C_conv.hexStringToByteArray(strIns);
        ins = bytes[0];

        return ins;
    }

    /**
     * This method adds the P9 transport encapsulation to a given frame
     * Fram ABCDEF becomes 01 ABCDEF 10 03 CRC16
     * @param message String containing the message to encapsulate
     * @return String in P9 format (including P9 transport layer encapsulation)
     */
    private byte[] addP9Encapsulation(String message) {
        String crc;
        ByteBuffer temp = ByteBuffer.allocate(C_conv.MAX_BUFFER_SIZE);
        int index = 1;
        
        // Header
        temp.put((byte)0x01);
        
        // Add the message
        temp.put(message.getBytes());
        index += message.length();
        
        // Add the ending characters
        temp.put((byte)0x10);
        temp.put((byte)0x03);

        // Add the CRC
        crc = C_crc16.computeCRC16(message);
        temp.put(crc.getBytes());      
        index += 4;
        
        // Copy buffer to a correct size buffer
        ByteBuffer tmp = C_conv.byteBufferCopy(temp, index);
    
        return tmp.array();
    }
    
}
