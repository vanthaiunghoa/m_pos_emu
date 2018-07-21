package c_icc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import c_common.C_logger_stdout;
import c_common.C_err;

/**
 *
 * @author balacahan
 */
public class Pos_emu_modules {
  
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Icc type
        C_icc.SmartCardManagementType smartCardType = C_icc.SmartCardManagementType.SMARTCARD_VIRTUAL;
        // Create ICC module
        C_icc m_icc;
        C_err.Icc retIcc;
        
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
            
            retIcc = m_icc.IccConnectSmartCard(1);
            if (C_err.Icc.ERR_ICC_OK == retIcc) {
                C_logger_stdout.LogInfo(module_name, "Card Connected - ATR=" + m_icc.IccGetATR(1));
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
        
    }
    
}
