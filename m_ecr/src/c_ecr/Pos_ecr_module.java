/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_ecr;

import c_common.C_conv;
import c_common.C_crc16;
import c_common.C_logger_stdout;
import c_common.C_err;
import java.util.Arrays;

/**
 *
 * @author balacahan
 */
public class Pos_ecr_module {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        C_err.Ecr retEcr = C_err.Ecr.ERR_ECR_OK;
        C_ecr m_ecr;
        String command;
        String response;
        byte[] rsp = new byte[2048];
        
        /* TEST TO REMOVE TODO */
        /*
        String answer = "3036303030313134557838323038313528AD303538313135CBD2";
        answer += "03";
        String hexanswer = C_conv.toHex(answer);
        rsp = C_conv.hexStringToByteArray(answer);
        String crc = C_crc16.computeCRC16(rsp.toString());
        C_logger_stdout.LogInfo("TEST", "TRAME=" + answer);
        C_logger_stdout.LogInfo("TEST", "CRC=" + crc);
        return;
        */
        /* */
        
        
        // Create instance of ECR class
        m_ecr = new C_ecr_p9e("m_ecr");

        // Initialize module
        String module_name = m_ecr.getModuleName();
        C_logger_stdout.LogInfo(module_name, "Module Created");
        m_ecr.initModule();

        // Wait for ECR command
        command = m_ecr.WaitEcrCommand();
        C_logger_stdout.LogInfo(module_name, "Received:" + command);

        response = m_ecr.executeCommand(command);
        C_logger_stdout.LogInfo(module_name, "Answer:" + response);

        rsp = m_ecr.AnswerToEcr(response);
        C_logger_stdout.LogInfo(module_name, "Answer:" + Arrays.toString(rsp));

    }

}
