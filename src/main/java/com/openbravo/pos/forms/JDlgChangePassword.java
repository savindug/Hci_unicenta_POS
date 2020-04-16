//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta
//    https://unicenta.com
//
//    This file is part of uniCenta oPOS
//
//    uniCenta oPOS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//   uniCenta oPOS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.forms;

import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.pos.util.Hashcypher;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JFrame;

/**
 *
 * @author adrianromero
 */
public class JDlgChangePassword extends javax.swing.JDialog {
    
    private String m_sOldPassword;
    private String m_sNewPassword;
    
    /** Creates new form ChangePassword */
    private JDlgChangePassword(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    /** Creates new form ChangePassword */
    private JDlgChangePassword(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }    

    private String init(String sOldPassword) {
        
        initComponents();

        getRootPane().setDefaultButton(jcmdOK);   
   
        m_sOldPassword = sOldPassword;
        m_sNewPassword = null;
        
        //show();
        setVisible(true);
        
        return m_sNewPassword;
    }
    
    
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window)parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    /**
     *
     * @param parent
     * @param sOldPassword
     * @return
     */
    public static String showMessage(Component parent, String sOldPassword) {
         
        Window window = getWindow(parent);      
        
        JDlgChangePassword myMsg;
        if (window instanceof Frame) { 
            myMsg = new JDlgChangePassword((Frame) window, true);
        } else {
            myMsg = new JDlgChangePassword((Dialog) window, true);
        }
        return myMsg.init(sOldPassword);
    }    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jcmdCancel = new javax.swing.JButton();
        jcmdOK = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtPasswordOld = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jtxtPasswordNew = new javax.swing.JPasswordField();
        jtxtPasswordRepeat = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("title.changepassword")); // NOI18N
        setBackground(new java.awt.Color(28, 35, 49));
        setResizable(false);

        jPanel2.setBackground(new java.awt.Color(28, 35, 49));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jcmdCancel.setBackground(new java.awt.Color(55, 71, 79));
        jcmdCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdCancel.setForeground(new java.awt.Color(255, 255, 255));
        jcmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cancel.png"))); // NOI18N
        jcmdCancel.setText(AppLocal.getIntString("button.cancel")); // NOI18N
        jcmdCancel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jcmdCancel.setPreferredSize(new java.awt.Dimension(110, 45));
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });
        jPanel2.add(jcmdCancel);

        jcmdOK.setBackground(new java.awt.Color(55, 71, 79));
        jcmdOK.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdOK.setForeground(new java.awt.Color(255, 255, 255));
        jcmdOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        jcmdOK.setText(AppLocal.getIntString("button.OK")); // NOI18N
        jcmdOK.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jcmdOK.setPreferredSize(new java.awt.Dimension(110, 45));
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });
        jPanel2.add(jcmdOK);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel1.setBackground(new java.awt.Color(28, 35, 49));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel1.setBackground(new java.awt.Color(28, 35, 49));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(AppLocal.getIntString("label.passwordold")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(150, 30));

        jtxtPasswordOld.setBackground(new java.awt.Color(55, 71, 79));
        jtxtPasswordOld.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtPasswordOld.setForeground(new java.awt.Color(255, 255, 255));
        jtxtPasswordOld.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel2.setBackground(new java.awt.Color(28, 35, 49));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(AppLocal.getIntString("label.passwordnew")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(150, 30));

        jtxtPasswordNew.setBackground(new java.awt.Color(55, 71, 79));
        jtxtPasswordNew.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jtxtPasswordNew.setForeground(new java.awt.Color(255, 255, 255));
        jtxtPasswordNew.setPreferredSize(new java.awt.Dimension(0, 30));

        jtxtPasswordRepeat.setBackground(new java.awt.Color(55, 71, 79));
        jtxtPasswordRepeat.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtPasswordRepeat.setForeground(new java.awt.Color(255, 255, 255));
        jtxtPasswordRepeat.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabel3.setBackground(new java.awt.Color(28, 35, 49));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText(AppLocal.getIntString("label.passwordrepeat")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtPasswordOld, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtPasswordNew, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtPasswordRepeat, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(88, 88, 88))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtPasswordOld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtPasswordNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtPasswordRepeat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(416, 234));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed

        dispose();
    }//GEN-LAST:event_jcmdCancelActionPerformed

    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed

        if (new String(jtxtPasswordNew.getPassword()).equals(new String(jtxtPasswordRepeat.getPassword()))) {
            if (Hashcypher.authenticate(new String(jtxtPasswordOld.getPassword()), m_sOldPassword)) {
                m_sNewPassword = Hashcypher.hashString(new String(jtxtPasswordNew.getPassword()));
                dispose();
            } else {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.BadPassword")));
            }
        } else {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.changepassworddistinct")));
        }
    }//GEN-LAST:event_jcmdOKActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jcmdCancel;
    private javax.swing.JButton jcmdOK;
    private javax.swing.JPasswordField jtxtPasswordNew;
    private javax.swing.JPasswordField jtxtPasswordOld;
    private javax.swing.JPasswordField jtxtPasswordRepeat;
    // End of variables declaration//GEN-END:variables
    
}
