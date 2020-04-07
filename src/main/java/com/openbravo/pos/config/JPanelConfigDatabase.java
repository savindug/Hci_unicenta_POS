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

package com.openbravo.pos.config;

import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.Session;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DriverWrapper;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.DirectoryEvent;
import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * @author Jack Gerrard
 * @author adrianromero
 */
public class JPanelConfigDatabase extends javax.swing.JPanel implements PanelConfig {
    
    private final DirtyManager dirty = new DirtyManager();
    
    /** Creates new form JPanelConfigDatabase */
    public JPanelConfigDatabase() {
        
        initComponents();
        
        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));
        jcboDBDriver.addActionListener(dirty);
        jcboDBDriver.addItem("MySQL");
//        jcboDBDriver.addItem("PostgreSQL");
        
        jcboDBDriver.setSelectedIndex(0);
        
        multiDB.addActionListener(dirty);        
        
// primary DB        
        jtxtDbName.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        jtxtDbIP.getDocument().addDocumentListener(dirty);
        jtxtDbPort.getDocument().addDocumentListener(dirty);        
        jtxtDbPassword.getDocument().addDocumentListener(dirty);
        jtxtDbUser.getDocument().addDocumentListener(dirty);        

// secondary DB        
        jtxtDbName1.getDocument().addDocumentListener(dirty);        
        jtxtDbURL1.getDocument().addDocumentListener(dirty);
        jtxtDbIP1.getDocument().addDocumentListener(dirty);
        jtxtDbPort1.getDocument().addDocumentListener(dirty);        
        jtxtDbPassword1.getDocument().addDocumentListener(dirty);
        jtxtDbUser1.getDocument().addDocumentListener(dirty);        

    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasChanged() {
        return dirty.isDirty();
    }
    
    /**
     *
     * @return
     */
    @Override
    public Component getConfigComponent() {
        return this;
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void loadProperties(AppConfig config) {
        
        multiDB.setSelected(Boolean.parseBoolean(config.getProperty("db.multi")));                

        jcboDBDriver.setSelectedItem(config.getProperty("db.engine"));
        jtxtDbDriverLib.setText(config.getProperty("db.driverlib"));
        jtxtDbDriver.setText(config.getProperty("db.driver"));

// primary DB              
        jtxtDbName.setText(config.getProperty("db.name"));
        jtxtDbIP.setText(config.getProperty("db.ip"));
        jtxtDbPort.setText(config.getProperty("db.port"));        
        jtxtDbURL.setText(config.getProperty("db.URL"));
        String sDBUser = config.getProperty("db.user");
        String sDBPassword = config.getProperty("db.password");        
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }        
        jtxtDbUser.setText(sDBUser);
        jtxtDbPassword.setText(sDBPassword);   

// secondary DB        
        jtxtDbName1.setText(config.getProperty("db1.name"));
        jtxtDbIP1.setText(config.getProperty("db1.ip"));
        jtxtDbPort1.setText(config.getProperty("db1.port"));        
        jtxtDbURL1.setText(config.getProperty("db1.URL"));
        String sDBUser1 = config.getProperty("db1.user");
        String sDBPassword1 = config.getProperty("db1.password");        
        if (sDBUser1 != null && sDBPassword1 != null && sDBPassword1.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser1);
            sDBPassword1 = cypher.decrypt(sDBPassword1.substring(6));
        }        
        jtxtDbUser1.setText(sDBUser1);
        jtxtDbPassword1.setText(sDBPassword1);          
        
        dirty.setDirty(false);
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void saveProperties(AppConfig config) {

// multi-db        
        config.setProperty("db.multi",Boolean.toString(multiDB.isSelected()));
        
        config.setProperty("db.engine", comboValue(jcboDBDriver.getSelectedItem()));
        config.setProperty("db.driverlib", jtxtDbDriverLib.getText());
        config.setProperty("db.driver", jtxtDbDriver.getText());

// primary DB
        config.setProperty("db.name", jtxtDbName.getText());
        config.setProperty("db.ip", jtxtDbIP.getText());
        config.setProperty("db.port", jtxtDbPort.getText());        
        config.setProperty("db.URL", jtxtDbURL.getText());
        config.setProperty("db.user", jtxtDbUser.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());       
        config.setProperty("db.password", "crypt:" + 
                cypher.encrypt(new String(jtxtDbPassword.getPassword())));

// secondary DB        
        config.setProperty("db1.name", jtxtDbName1.getText());        
        config.setProperty("db1.ip", jtxtDbIP1.getText());
        config.setProperty("db1.port", jtxtDbPort1.getText());        
        config.setProperty("db1.URL", jtxtDbURL1.getText());        
        config.setProperty("db1.user", jtxtDbUser1.getText());
        cypher = new AltEncrypter("cypherkey" + jtxtDbUser1.getText());       
        config.setProperty("db1.password", "crypt:" + 
                cypher.encrypt(new String(jtxtDbPassword1.getPassword())));        

        dirty.setDirty(false);
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        webPopOver1 = new com.alee.extended.window.WebPopOver();
        jLabel6 = new javax.swing.JLabel();
        jcboDBDriver = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        jtxtDbDriverLib = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDbDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtDbUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDbPassword = new javax.swing.JPasswordField();
        jButtonTest = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jButtonTest1 = new javax.swing.JButton();
        jtxtDbPassword1 = new javax.swing.JPasswordField();
        jtxtDbUser1 = new javax.swing.JTextField();
        jtxtDbURL1 = new javax.swing.JTextField();
        jLblDbURL1 = new javax.swing.JLabel();
        jLblDbUser1 = new javax.swing.JLabel();
        jLblDbPassword1 = new javax.swing.JLabel();
        jLblDBName = new javax.swing.JLabel();
        jtxtDbName = new javax.swing.JTextField();
        jLblDbName1 = new javax.swing.JLabel();
        jtxtDbName1 = new javax.swing.JTextField();
        LblMultiDB = new com.alee.laf.label.WebLabel();
        multiDB = new com.alee.extended.button.WebSwitch();
        jLblDBIP = new javax.swing.JLabel();
        jtxtDbIP = new javax.swing.JTextField();
        jLblDbIP1 = new javax.swing.JLabel();
        jtxtDbIP1 = new javax.swing.JTextField();
        jLblDBPort = new javax.swing.JLabel();
        jtxtDbPort = new javax.swing.JTextField();
        jLblDBPort1 = new javax.swing.JLabel();
        jtxtDbPort1 = new javax.swing.JTextField();

        setBackground(new java.awt.Color(28, 35, 49));
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(900, 500));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel6.setText(bundle.getString("label.Database")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 196, -1, -1));

        jcboDBDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jcboDBDriver.setPreferredSize(new java.awt.Dimension(150, 30));
        jcboDBDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcboDBDriverActionPerformed(evt);
            }
        });
        add(jcboDBDriver, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 196, -1, -1));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 154, -1, 25));

        jtxtDbDriverLib.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriverLib.setPreferredSize(new java.awt.Dimension(500, 30));
        add(jtxtDbDriverLib, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 152, -1, -1));

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setText("  ");
        jbtnDbDriverLib.setToolTipText("");
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(80, 45));
        add(jbtnDbDriverLib, new org.netbeans.lib.awtextra.AbsoluteConstraints(649, 145, -1, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.DbDriver")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(324, 196, -1, -1));

        jtxtDbDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriver.setPreferredSize(new java.awt.Dimension(150, 30));
        jtxtDbDriver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtDbDriverActionPerformed(evt);
            }
        });
        add(jtxtDbDriver, new org.netbeans.lib.awtextra.AbsoluteConstraints(459, 197, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.DbURL")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 343, -1, -1));

        jtxtDbURL.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbURL.setPreferredSize(new java.awt.Dimension(275, 30));
        add(jtxtDbURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 343, -1, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, -1, -1));

        jtxtDbUser.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbUser.setPreferredSize(new java.awt.Dimension(150, 30));
        add(jtxtDbUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 380, -1, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.DbPassword")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 417, -1, -1));

        jtxtDbPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPassword.setPreferredSize(new java.awt.Dimension(150, 30));
        add(jtxtDbPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 417, -1, -1));

        jButtonTest.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButtonTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/database.png"))); // NOI18N
        jButtonTest.setText(bundle.getString("button.test")); // NOI18N
        jButtonTest.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.setPreferredSize(new java.awt.Dimension(110, 45));
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });
        add(jButtonTest, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 455, -1, -1));
        add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 137, 880, -1));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/uniCenta_logo_vert_100.png"))); // NOI18N
        jLabel5.setText(bundle.getString("message.DBDefault")); // NOI18N
        jLabel5.setToolTipText("");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel5.setPreferredSize(new java.awt.Dimension(889, 120));
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 6, 880, -1));

        jButtonTest1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButtonTest1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/database.png"))); // NOI18N
        jButtonTest1.setText(bundle.getString("button.test")); // NOI18N
        jButtonTest1.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jButtonTest1.setEnabled(false);
        jButtonTest1.setPreferredSize(new java.awt.Dimension(110, 45));
        jButtonTest1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTest1ActionPerformed(evt);
            }
        });
        add(jButtonTest1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 455, -1, -1));

        jtxtDbPassword1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPassword1.setEnabled(false);
        jtxtDbPassword1.setPreferredSize(new java.awt.Dimension(150, 30));
        add(jtxtDbPassword1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 417, -1, -1));

        jtxtDbUser1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbUser1.setEnabled(false);
        jtxtDbUser1.setPreferredSize(new java.awt.Dimension(150, 30));
        add(jtxtDbUser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 380, -1, -1));

        jtxtDbURL1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbURL1.setEnabled(false);
        jtxtDbURL1.setPreferredSize(new java.awt.Dimension(275, 30));
        add(jtxtDbURL1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 343, -1, -1));

        jLblDbURL1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbURL1.setText(AppLocal.getIntString("label.DbURL1")); // NOI18N
        jLblDbURL1.setEnabled(false);
        jLblDbURL1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDbURL1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 343, -1, -1));

        jLblDbUser1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbUser1.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLblDbUser1.setEnabled(false);
        jLblDbUser1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDbUser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 380, -1, -1));

        jLblDbPassword1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbPassword1.setText(AppLocal.getIntString("label.DbPassword")); // NOI18N
        jLblDbPassword1.setEnabled(false);
        jLblDbPassword1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDbPassword1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 417, -1, -1));

        jLblDBName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDBName.setText(AppLocal.getIntString("label.DbName")); // NOI18N
        jLblDBName.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDBName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 269, -1, -1));

        jtxtDbName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbName.setPreferredSize(new java.awt.Dimension(275, 30));
        add(jtxtDbName, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 269, -1, -1));

        jLblDbName1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbName1.setText(AppLocal.getIntString("label.DbName1")); // NOI18N
        jLblDbName1.setEnabled(false);
        jLblDbName1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDbName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 269, -1, -1));

        jtxtDbName1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbName1.setEnabled(false);
        jtxtDbName1.setPreferredSize(new java.awt.Dimension(275, 30));
        add(jtxtDbName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 269, -1, -1));

        LblMultiDB.setText(AppLocal.getIntString("label.multidb")); // NOI18N
        LblMultiDB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        LblMultiDB.setPreferredSize(new java.awt.Dimension(125, 30));
        add(LblMultiDB, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 233, -1, -1));

        multiDB.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        multiDB.setPreferredSize(new java.awt.Dimension(80, 30));
        multiDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiDBActionPerformed(evt);
            }
        });
        add(multiDB, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 233, -1, -1));

        jLblDBIP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDBIP.setText(AppLocal.getIntString("label.DbIP")); // NOI18N
        jLblDBIP.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDBIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 306, -1, -1));

        jtxtDbIP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbIP.setPreferredSize(new java.awt.Dimension(135, 30));
        add(jtxtDbIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 306, -1, -1));

        jLblDbIP1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDbIP1.setText(AppLocal.getIntString("label.IP1")); // NOI18N
        jLblDbIP1.setEnabled(false);
        jLblDbIP1.setPreferredSize(new java.awt.Dimension(125, 30));
        add(jLblDbIP1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 306, -1, -1));

        jtxtDbIP1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbIP1.setEnabled(false);
        jtxtDbIP1.setPreferredSize(new java.awt.Dimension(135, 30));
        add(jtxtDbIP1, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 306, -1, -1));

        jLblDBPort.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDBPort.setText(AppLocal.getIntString("label.DbPort")); // NOI18N
        jLblDBPort.setPreferredSize(new java.awt.Dimension(50, 30));
        add(jLblDBPort, new org.netbeans.lib.awtextra.AbsoluteConstraints(288, 306, -1, -1));

        jtxtDbPort.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPort.setPreferredSize(new java.awt.Dimension(70, 30));
        add(jtxtDbPort, new org.netbeans.lib.awtextra.AbsoluteConstraints(344, 306, 70, -1));

        jLblDBPort1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblDBPort1.setText(AppLocal.getIntString("label.DbPort1")); // NOI18N
        jLblDBPort1.setEnabled(false);
        jLblDBPort1.setPreferredSize(new java.awt.Dimension(50, 30));
        add(jLblDBPort1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 306, -1, -1));

        jtxtDbPort1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbPort1.setEnabled(false);
        jtxtDbPort1.setPreferredSize(new java.awt.Dimension(70, 30));
        add(jtxtDbPort1, new org.netbeans.lib.awtextra.AbsoluteConstraints(774, 306, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtDbDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtDbDriverActionPerformed

    }//GEN-LAST:event_jtxtDbDriverActionPerformed

    private void jcboDBDriverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcboDBDriverActionPerformed

        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
           
        
        if ("PostgreSQL".equals(jcboDBDriver.getSelectedItem())) {
            //TODO: Not good to hardcode DB library
            jtxtDbDriverLib.setText(new File(new File(dirname), "lib/postgresql-9.4-1208.jdbc4.jar").getAbsolutePath());
            jtxtDbDriver.setText("org.postgresql.Driver");
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/unicentaopos");            
        } else {
            //TODO: Not good to hardcode DB library
//            jtxtDbDriverLib.setText(new File(new File(dirname), "lib/mysql-connector-java-6.0.6.jar").getAbsolutePath());
//            jtxtDbDriver.setText("com.mysql.cj.jdbc.Driver");
            jtxtDbDriverLib.setText(new File(new File(dirname), "lib/mysql-connector-java-5.1.39.jar").getAbsolutePath());
            jtxtDbDriver.setText("com.mysql.jdbc.Driver");            
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/unicentaopos");
        }    
    }//GEN-LAST:event_jcboDBDriverActionPerformed

    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session =  new Session(url, user, password);
            Connection connection = session.getConnection();
            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databasesuccess"), 
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
/*
 * temp placeholder for:
 * Use for autocreate default unicentaopos schema
                int response = JOptionPane.showOptionDialog(null,
                        AppLocal.getIntString("message.databasecreate"),
                        "Create Database",
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE,
                        null, null, null);
                if (response == JOptionPane.YES_OPTION) {
                    String sql = "CREATE DATABASE IF NOT EXISTS unicentaopos";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.execute();
                }
*/                
            } else {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (Exception e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jButtonTestActionPerformed

    private void jButtonTest1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTest1ActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL1.getText();
            String user = jtxtDbUser1.getText();
            String password = new String(jtxtDbPassword1.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session =  new Session(url, user, password);
            Connection connection = session.getConnection();
            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databasesuccess"), 
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, 
                            AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (Exception e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jButtonTest1ActionPerformed

    private void multiDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiDBActionPerformed
        if (multiDB.isSelected()) {
            jLblDbName1.setEnabled(true);
            jtxtDbName1.setEnabled(true);
            jLblDbIP1.setEnabled(true);
            jtxtDbIP1.setEnabled(true);  
            jLblDBPort1.setEnabled(true);
            jtxtDbPort1.setEnabled(true);            
            jLblDbURL1.setEnabled(true);
            jtxtDbURL1.setEnabled(true);            
            jLblDbUser1.setEnabled(true);
            jtxtDbUser1.setEnabled(true);
            jLblDbPassword1.setEnabled(true);
            jtxtDbPassword1.setEnabled(true);
            jButtonTest1.setEnabled(true);
        } else {
            jLblDbName1.setEnabled(false);
            jtxtDbName1.setEnabled(false);
            jLblDbIP1.setEnabled(false);
            jtxtDbIP1.setEnabled(false);  
            jLblDBPort1.setEnabled(false);
            jtxtDbPort1.setEnabled(false);            
            jLblDbURL1.setEnabled(false);
            jtxtDbURL1.setEnabled(false);                        
            jLblDbUser1.setEnabled(false);
            jtxtDbUser1.setEnabled(false);
            jLblDbPassword1.setEnabled(false);
            jtxtDbPassword1.setEnabled(false);            
            jButtonTest1.setEnabled(false);            
        }

    }//GEN-LAST:event_multiDBActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.label.WebLabel LblMultiDB;
    private javax.swing.JButton jButtonTest;
    private javax.swing.JButton jButtonTest1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLblDBIP;
    private javax.swing.JLabel jLblDBName;
    private javax.swing.JLabel jLblDBPort;
    private javax.swing.JLabel jLblDBPort1;
    private javax.swing.JLabel jLblDbIP1;
    private javax.swing.JLabel jLblDbName1;
    private javax.swing.JLabel jLblDbPassword1;
    private javax.swing.JLabel jLblDbURL1;
    private javax.swing.JLabel jLblDbUser1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JComboBox jcboDBDriver;
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JTextField jtxtDbIP;
    private javax.swing.JTextField jtxtDbIP1;
    private javax.swing.JTextField jtxtDbName;
    private javax.swing.JTextField jtxtDbName1;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JPasswordField jtxtDbPassword1;
    private javax.swing.JTextField jtxtDbPort;
    private javax.swing.JTextField jtxtDbPort1;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbURL1;
    private javax.swing.JTextField jtxtDbUser;
    private javax.swing.JTextField jtxtDbUser1;
    private com.alee.extended.button.WebSwitch multiDB;
    private com.alee.extended.window.WebPopOver webPopOver1;
    // End of variables declaration//GEN-END:variables

     
}
