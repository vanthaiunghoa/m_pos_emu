/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * C_tcp_server is the module which open a TCP/IP server and reads for an
 * incoming message
 *
 * @author Bulent Alacahan
 * @version %I%, %G%
 * @since 0.1
 */
public class C_tcp_server {

    private final int READ_BUFFER_SIZE = C_conv.MAX_BUFFER_SIZE;
    
    private ServerSocket inSocket;
    private Socket connectionSocket;
    private final String module_name;
    private final int internal_port;

    public C_tcp_server(String moduleName, int port) {
        module_name = moduleName;
        internal_port = port;
    }

    public void StartTCPServer() {
        C_logger_stdout.LogInfo(module_name, "Starting TCP-IP server : " + internal_port);

        // Create a socket on the predefined port
        try {
            inSocket = new ServerSocket(internal_port);
        } catch (IOException e) {
            C_logger_stdout.LogError(module_name, "Error Starting TCP server:" + e);
        }
    }

    public void RestartTCPServer() {
        try {
            inSocket.close();
            StartTCPServer();
        } catch (IOException e) {
            C_logger_stdout.LogError(module_name, "Error ReStarting TCP server:" + e);
        }
    }

    public String WaitTcpMessage() throws Exception {
        BufferedReader inFromClient;
        String receivedCommand = "";
        
        try {
            //wait, on welcoming socket for contact by client
            connectionSocket = inSocket.accept();
            //create input stream, attached to socket
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            //read in line from socket
            receivedCommand = readReceptionFrame(inFromClient);
            
        }// end if statement
        catch (IOException ex) {
            Logger.getLogger(C_tcp_server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return receivedCommand;
    }

    public void SendTcpResponse(byte[] rsp) {
        try {
            DataOutputStream outputStrem;
            
            //create output stream, attached to socket
            outputStrem = new DataOutputStream(connectionSocket.getOutputStream());
            
            // Send answer
            String frame = Arrays.toString(rsp);
            C_logger_stdout.LogInfo(module_name, frame);
            
            outputStrem.writeBytes(frame);
        } catch (IOException ex) {
            Logger.getLogger(C_tcp_server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    private String readReceptionFrame(BufferedReader in) {
        StringBuilder sb = new StringBuilder();
        char[] bytes = new char[READ_BUFFER_SIZE];
        int readByte;
        
        try {
            readByte = in.read(bytes, 0, READ_BUFFER_SIZE);
            if (readByte != -1 ) {
                sb.append(bytes);
            } else {
                C_logger_stdout.LogError(module_name, "Error reading reception buffer");
            }
        } catch (IOException ex) {
            Logger.getLogger(C_tcp_server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sb.length() == 0 ? null : sb.toString();
    }
    
}
