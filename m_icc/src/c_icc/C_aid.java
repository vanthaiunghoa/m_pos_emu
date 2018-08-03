/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c_icc;

/**
 * C_aid is a little class for AID type management
 *  
 * @author      Bulent Alacahan
 * @version     %I%, %G%
 * @since       0.1
 */
public class C_aid {
    public static final int AID_MAX_NUMBER = 64;
    
    public String m_name;
    public byte[] m_aid;
    public int m_length;
    public byte m_priority;
    public byte m_asi;
    
    public C_aid(String name, byte[] aid, int aid_len, byte prio, byte asi) {
        m_name = name;
        m_aid = aid;
        m_length = aid_len;
        m_priority = prio;
        m_asi = asi;
    }
}
