//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2017 uniCenta & previous Openbravo POS works
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

package com.openbravo.pos.sales;

import com.openbravo.data.loader.Session;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author JG uniCenta
 */
public class JMooringDetails extends javax.swing.JDialog {

        private Connection con;  
        private ResultSet rs;
        private Statement stmt;
        private String ID;
        private String SQL;
        private String vesselName = "";
        private Integer vesselSize;
        private Integer vesselDays;
        private Boolean vesselPower;
        private boolean create = false;


    private JMooringDetails(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

 
    private JMooringDetails(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private void init(Session s) {

        initComponents();
        setTitle("Select Vessel details");
        
         try{
            con=s.getConnection(); 
            stmt = (Statement) con.createStatement();          
        
        SQL = "SELECT * FROM moorers";     
        rs = stmt.executeQuery(SQL);
       
        jTableSelector.setModel(DbUtils.resultSetToTableModel(rs));
        jTableSelector.getColumnModel().getColumn(0).setPreferredWidth(200);
        jTableSelector.getColumnModel().getColumn(1).setPreferredWidth(40);
        jTableSelector.getColumnModel().getColumn(2).setPreferredWidth(40);
        jTableSelector.getColumnModel().getColumn(3).setPreferredWidth(40);
        jTableSelector.setRowSelectionAllowed(true);
        jTableSelector.getTableHeader().setReorderingAllowed(true);

        
           } catch (Exception e) {                
       }        
        

    }

    /**
     *
     * @param parent
     * @param s
     * @return
     */
    public static JMooringDetails getMooringDetails(Component parent, Session s) {
        Window window = SwingUtilities.getWindowAncestor(parent);
       // m_oticket = new ticket();
        JMooringDetails myMsg;
        if (window instanceof Frame) {
            myMsg = new JMooringDetails((Frame) window, true);
        } else {
            myMsg = new JMooringDetails((Dialog) window, true);
        }
        myMsg.init(s);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        
        
        return myMsg;
    }

    /**
     *
     * @return
     */
    public boolean isCreate() {
        return create;
    }

    /**
     *
     * @return
     */
    public String getVesselName() {
        return vesselName;
    }

    /**
     *
     * @return
     */
    public Integer getVesselSize() {
        return vesselSize;
    }

    /**
     *
     * @return
     */
    public Integer getVesselDays() {
        return vesselDays;
    }

    /**
     *
     * @return
     */
    public Boolean getVesselPower() {
        return vesselPower;
    } 

   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnCreateTicket = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSelector = new javax.swing.JTable();
        jText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(28, 35, 49));

        jbtnCreateTicket.setBackground(new java.awt.Color(55, 71, 79));
        jbtnCreateTicket.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnCreateTicket.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jbtnCreateTicket.setText(bundle.getString("label.mooringcreatebtn")); // NOI18N
        jbtnCreateTicket.setBorder(null);
        jbtnCreateTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCreateTicketActionPerformed(evt);
            }
        });

        jTableSelector.setBackground(new java.awt.Color(28, 35, 49));
        jTableSelector.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTableSelector.setForeground(new java.awt.Color(255, 255, 255));
        jTableSelector.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Vessel Name", "Size", "Days", "Power"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableSelector.setRowHeight(25);
        jTableSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSelectorMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableSelector);
        if (jTableSelector.getColumnModel().getColumnCount() > 0) {
            jTableSelector.getColumnModel().getColumn(1).setResizable(false);
            jTableSelector.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTableSelector.getColumnModel().getColumn(2).setResizable(false);
            jTableSelector.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableSelector.getColumnModel().getColumn(3).setResizable(false);
            jTableSelector.getColumnModel().getColumn(3).setPreferredWidth(70);
        }

        jText.setBackground(new java.awt.Color(75, 81, 93));
        jText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jText.setForeground(new java.awt.Color(255, 255, 255));
        jText.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jText.setEnabled(false);
        jText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(28, 35, 49));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(bundle.getString("label.mooringscreatefor")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jText, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jbtnCreateTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 331, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jbtnCreateTicket))
                .addContainerGap(248, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(879, 537));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnCreateTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCreateTicketActionPerformed
        // use the passed parameters to add line to the ticket
        
        if (vesselName.equals("")){
        this.create = false;
        dispose(); 
        }else{
        this.create = true;   
        dispose();
        }
        
    }//GEN-LAST:event_jbtnCreateTicketActionPerformed

    private void jTableSelectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSelectorMouseClicked

        try{
         int row = jTableSelector.getSelectedRow();  
         vesselName =(jTableSelector.getModel().getValueAt(row, 0).toString());
         SQL = "SELECT * FROM moorers WHERE VESSELNAME ='" + vesselName +"';";
         rs = stmt.executeQuery(SQL);
         if (rs.next()){
             vesselDays=rs.getInt("DAYS");
             vesselSize=rs.getInt("SIZE");
             vesselPower=rs.getBoolean("POWER");            
             jText.setText(vesselName);
         }
        }catch (Exception e){
        JOptionPane.showMessageDialog(null, e);
        }
        
        
    }//GEN-LAST:event_jTableSelectorMouseClicked

    private void jTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableSelector;
    private javax.swing.JTextField jText;
    private javax.swing.JButton jbtnCreateTicket;
    // End of variables declaration//GEN-END:variables

}
