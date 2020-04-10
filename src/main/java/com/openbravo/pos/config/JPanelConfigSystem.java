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

import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppConfig;
import java.awt.Component;
        
        

/**
 *
 * @author JG uniCenta
 */
public class JPanelConfigSystem extends javax.swing.JPanel implements PanelConfig {
    
    private DirtyManager dirty = new DirtyManager();
    
    /** Creates new form JPanelConfigDatabase */
    public JPanelConfigSystem() {
        
        initComponents();
        
        jTextAutoLogoffTime.getDocument().addDocumentListener(dirty);
        jchkInstance.addActionListener(dirty);
        jchkTextOverlay.addActionListener(dirty);
        jchkAutoLogoff.addActionListener(dirty);
        jchkAutoLogoffToTables.addActionListener(dirty);
        jchkShowCustomerDetails.addActionListener(dirty);
        jchkShowWaiterDetails.addActionListener(dirty);
        jCustomerColour1.addActionListener(dirty);
        jWaiterColour1.addActionListener(dirty);
        jTableNameColour1.addActionListener(dirty);
        jTaxIncluded.addActionListener(dirty);
        jCheckPrice00.addActionListener(dirty);          
        jMoveAMountBoxToTop.addActionListener(dirty);
        jCloseCashbtn.addActionListener(dirty);

        jchkautoRefreshTableMap.addActionListener(dirty);
        jTxtautoRefreshTimer.getDocument().addDocumentListener(dirty);       

        jchkSCOnOff.addActionListener(dirty);
        jchkSCRestaurant.addActionListener(dirty);        
        jTextSCRate.getDocument().addDocumentListener(dirty);   
        
        jchkPriceUpdate.addActionListener(dirty);
        jchkBarcodetype.addActionListener(dirty);
        jchkTransBtn.addActionListener(dirty);        
        
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
     
        String timerCheck =(config.getProperty("till.autotimer"));
        if (timerCheck == null){
            config.setProperty("till.autotimer","100");
        }                
        jTextAutoLogoffTime.setText(config.getProperty("till.autotimer"));

        String autoRefreshtimerCheck =(config.getProperty("till.autoRefreshTimer"));
        if (autoRefreshtimerCheck == null){
            config.setProperty("till.autoRefreshTimer","5");
        }                
        jTxtautoRefreshTimer.setText(config.getProperty("till.autoRefreshTimer"));        

        jchkInstance.setSelected(Boolean.parseBoolean(config.getProperty("machine.uniqueinstance"))); 
        jchkShowCustomerDetails.setSelected(Boolean.parseBoolean(config.getProperty("table.showcustomerdetails")));
        jchkShowWaiterDetails.setSelected(Boolean.parseBoolean(config.getProperty("table.showwaiterdetails")));
        jchkTextOverlay.setSelected(Boolean.parseBoolean(config.getProperty("payments.textoverlay")));        
        jchkAutoLogoff.setSelected(Boolean.parseBoolean(config.getProperty("till.autoLogoff")));    
        jchkAutoLogoffToTables.setSelected(Boolean.parseBoolean(config.getProperty("till.autoLogoffrestaurant")));           
        jTaxIncluded.setSelected(Boolean.parseBoolean(config.getProperty("till.taxincluded")));
        jCheckPrice00.setSelected(Boolean.parseBoolean(config.getProperty("till.pricewith00")));        
        jMoveAMountBoxToTop.setSelected(Boolean.parseBoolean(config.getProperty("till.amountattop")));  
        jCloseCashbtn.setSelected(Boolean.parseBoolean(config.getProperty("screen.600800")));
        jchkautoRefreshTableMap.setSelected(Boolean.parseBoolean(config.getProperty("till.autoRefreshTableMap")));  
        
        jchkPriceUpdate.setSelected(AppConfig.getInstance().getBoolean("db.prodpriceupdate"));
        jchkBarcodetype.setSelected(Boolean.parseBoolean(config.getProperty("machine.barcodetype")));        
        
/** Added: JG 23 July 13 */      
        String SCCheck =(config.getProperty("till.SCRate"));
        if (SCCheck == null){
            config.setProperty("till.SCRate","0");
        }                
        jTextSCRate.setText(config.getProperty("till.SCRate"));
        jchkSCOnOff.setSelected(Boolean.parseBoolean(config.getProperty("till.SCOnOff")));    
        jchkSCRestaurant.setSelected(Boolean.parseBoolean(config.getProperty("till.SCRestaurant")));
        
        if (jchkSCOnOff.isSelected()){
                jchkSCRestaurant.setVisible(true);
                jLabelSCRate.setVisible(true);
                jTextSCRate.setVisible(true);
                jLabelSCRatePerCent.setVisible(true);
        }else{    
                jchkSCRestaurant.setVisible(false);
                jLabelSCRate.setVisible(false);
                jTextSCRate.setVisible(false);
                jLabelSCRatePerCent.setVisible(false);
        }                       
        
        if (jchkAutoLogoff.isSelected()){
                jchkAutoLogoffToTables.setVisible(true);
                jLabelInactiveTime.setVisible(true);
                jLabelTimedMessage.setVisible(true);
                jTextAutoLogoffTime.setVisible(true);
        }else{    
                jchkAutoLogoffToTables.setVisible(false);
                jLabelInactiveTime.setVisible(false);
                jLabelTimedMessage.setVisible(false);
                jTextAutoLogoffTime.setVisible(false);
        }
        
        if (jchkautoRefreshTableMap.isSelected()){
                jLblautoRefresh.setVisible(true);
                jLabelInactiveTime1.setVisible(true);
                jTxtautoRefreshTimer.setVisible(true);
        }else{    
                jLblautoRefresh.setVisible(false);
                jLabelInactiveTime1.setVisible(false);
                jTxtautoRefreshTimer.setVisible(false);
        }        

        if (config.getProperty("table.customercolour")==null){
            jCustomerColour1.setText("blue");
        }else{
            jCustomerColour1.setText(config.getProperty("table.customercolour"));
        }
        if (config.getProperty("table.waitercolour")==null){
            jWaiterColour1.setText("red");
        }else{
            jWaiterColour1.setText(config.getProperty("table.waitercolour"));
        }
        if (config.getProperty("table.tablecolour")==null){                
            jTableNameColour1.setText("black");      
        }else{
            jTableNameColour1.setText((config.getProperty("table.tablecolour")));  
        }
        
        jchkTransBtn.setSelected(Boolean.parseBoolean(config.getProperty("table.transbtn")));                
       
        dirty.setDirty(false);
    }
   
    /**
     *
     * @param config
     */
    @Override
    public void saveProperties(AppConfig config) {
        
        config.setProperty("till.autotimer",jTextAutoLogoffTime.getText());
        config.setProperty("machine.uniqueinstance", Boolean.toString(jchkInstance.isSelected()));
        config.setProperty("table.showcustomerdetails", Boolean.toString(jchkShowCustomerDetails.isSelected()));
        config.setProperty("table.showwaiterdetails", Boolean.toString(jchkShowWaiterDetails.isSelected()));        
        config.setProperty("payments.textoverlay", Boolean.toString(jchkTextOverlay.isSelected()));         
        config.setProperty("till.autoLogoff", Boolean.toString(jchkAutoLogoff.isSelected()));                 
        config.setProperty("till.autoLogoffrestaurant", Boolean.toString(jchkAutoLogoffToTables.isSelected()));                        
        config.setProperty("table.customercolour",jCustomerColour1.getText());
        config.setProperty("table.waitercolour",jWaiterColour1.getText());
        config.setProperty("table.tablecolour",jTableNameColour1.getText());         
        config.setProperty("till.taxincluded",Boolean.toString(jTaxIncluded.isSelected()));                     
        config.setProperty("till.pricewith00",Boolean.toString(jCheckPrice00.isSelected()));                         
        config.setProperty("till.amountattop",Boolean.toString(jMoveAMountBoxToTop.isSelected()));         
        config.setProperty("screen.600800",Boolean.toString(jCloseCashbtn.isSelected())); 
        config.setProperty("till.autoRefreshTableMap", Boolean.toString(jchkautoRefreshTableMap.isSelected()));                 
        config.setProperty("till.autoRefreshTimer", jTxtautoRefreshTimer.getText());

        config.setProperty("till.SCOnOff",Boolean.toString(jchkSCOnOff.isSelected()));
        config.setProperty("till.SCRate",jTextSCRate.getText());
        config.setProperty("till.SCRestaurant",Boolean.toString(jchkSCRestaurant.isSelected()));

        config.setProperty("db.prodpriceupdate", Boolean.toString(jchkPriceUpdate.isSelected()));        
        config.setProperty("machine.barcodetype", Boolean.toString(jchkBarcodetype.isSelected()));
        
        config.setProperty("table.transbtn", Boolean.toString(jchkTransBtn.isSelected()));
        
        dirty.setDirty(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jchkInstance = new javax.swing.JCheckBox();
        jLabelInactiveTime = new javax.swing.JLabel();
        jTextAutoLogoffTime = new javax.swing.JTextField();
        jLabelTimedMessage = new javax.swing.JLabel();
        jchkAutoLogoff = new javax.swing.JCheckBox();
        jchkAutoLogoffToTables = new javax.swing.JCheckBox();
        jchkShowCustomerDetails = new javax.swing.JCheckBox();
        jchkShowWaiterDetails = new javax.swing.JCheckBox();
        jLabelTableNameTextColour = new javax.swing.JLabel();
        jCheckPrice00 = new javax.swing.JCheckBox();
        jTaxIncluded = new javax.swing.JCheckBox();
        jCloseCashbtn = new javax.swing.JCheckBox();
        jMoveAMountBoxToTop = new javax.swing.JCheckBox();
        jchkTextOverlay = new javax.swing.JCheckBox();
        jchkautoRefreshTableMap = new javax.swing.JCheckBox();
        jLabelInactiveTime1 = new javax.swing.JLabel();
        jTxtautoRefreshTimer = new javax.swing.JTextField();
        jLblautoRefresh = new javax.swing.JLabel();
        jchkSCOnOff = new javax.swing.JCheckBox();
        jLabelSCRate = new javax.swing.JLabel();
        jTextSCRate = new javax.swing.JTextField();
        jLabelSCRatePerCent = new javax.swing.JLabel();
        jchkSCRestaurant = new javax.swing.JCheckBox();
        jCustomerColour1 = new com.alee.extended.colorchooser.WebColorChooserField();
        jWaiterColour1 = new com.alee.extended.colorchooser.WebColorChooserField();
        jTableNameColour1 = new com.alee.extended.colorchooser.WebColorChooserField();
        jchkPriceUpdate = new javax.swing.JCheckBox();
        jchkBarcodetype = new javax.swing.JCheckBox();
        jchkTransBtn = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(28, 35, 49));
        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setPreferredSize(new java.awt.Dimension(700, 500));

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.configOptionStartup")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(250, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(bundle.getString("label.configOptionKeypad")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(250, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText(bundle.getString("label.configOptionLogOff")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText(bundle.getString("label.configOptionRestaurant")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(250, 30));

        jchkInstance.setBackground(new java.awt.Color(28, 35, 49));
        jchkInstance.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkInstance.setForeground(new java.awt.Color(255, 255, 255));
        jchkInstance.setText(bundle.getString("label.instance")); // NOI18N
        jchkInstance.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkInstance.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkInstance.setPreferredSize(new java.awt.Dimension(250, 25));

        jLabelInactiveTime.setBackground(new java.awt.Color(255, 255, 255));
        jLabelInactiveTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelInactiveTime.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInactiveTime.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelInactiveTime.setText(bundle.getString("label.autolofftime")); // NOI18N
        jLabelInactiveTime.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelInactiveTime.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelInactiveTime.setPreferredSize(new java.awt.Dimension(100, 30));

        jTextAutoLogoffTime.setBackground(new java.awt.Color(75, 81, 93));
        jTextAutoLogoffTime.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextAutoLogoffTime.setForeground(new java.awt.Color(255, 255, 255));
        jTextAutoLogoffTime.setText("0");
        jTextAutoLogoffTime.setMaximumSize(new java.awt.Dimension(0, 25));
        jTextAutoLogoffTime.setMinimumSize(new java.awt.Dimension(0, 0));
        jTextAutoLogoffTime.setPreferredSize(new java.awt.Dimension(0, 30));

        jLabelTimedMessage.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelTimedMessage.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTimedMessage.setText(bundle.getString("label.autologoffzero")); // NOI18N
        jLabelTimedMessage.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelTimedMessage.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelTimedMessage.setPreferredSize(new java.awt.Dimension(200, 30));

        jchkAutoLogoff.setBackground(new java.awt.Color(28, 35, 49));
        jchkAutoLogoff.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkAutoLogoff.setForeground(new java.awt.Color(255, 255, 255));
        jchkAutoLogoff.setText(bundle.getString("label.autologonoff")); // NOI18N
        jchkAutoLogoff.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkAutoLogoff.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkAutoLogoff.setPreferredSize(new java.awt.Dimension(200, 30));
        jchkAutoLogoff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkAutoLogoffActionPerformed(evt);
            }
        });

        jchkAutoLogoffToTables.setBackground(new java.awt.Color(28, 35, 49));
        jchkAutoLogoffToTables.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkAutoLogoffToTables.setForeground(new java.awt.Color(255, 255, 255));
        jchkAutoLogoffToTables.setText(bundle.getString("label.autoloffrestaurant")); // NOI18N
        jchkAutoLogoffToTables.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkAutoLogoffToTables.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkAutoLogoffToTables.setPreferredSize(new java.awt.Dimension(0, 30));
        jchkAutoLogoffToTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkAutoLogoffToTablesActionPerformed(evt);
            }
        });

        jchkShowCustomerDetails.setBackground(new java.awt.Color(28, 35, 49));
        jchkShowCustomerDetails.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkShowCustomerDetails.setForeground(new java.awt.Color(255, 255, 255));
        jchkShowCustomerDetails.setText(bundle.getString("label.tableshowcustomerdetails")); // NOI18N
        jchkShowCustomerDetails.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkShowCustomerDetails.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkShowCustomerDetails.setPreferredSize(new java.awt.Dimension(350, 30));
        jchkShowCustomerDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkShowCustomerDetailsActionPerformed(evt);
            }
        });

        jchkShowWaiterDetails.setBackground(new java.awt.Color(28, 35, 49));
        jchkShowWaiterDetails.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkShowWaiterDetails.setForeground(new java.awt.Color(255, 255, 255));
        jchkShowWaiterDetails.setText(bundle.getString("label.tableshowwaiterdetails")); // NOI18N
        jchkShowWaiterDetails.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkShowWaiterDetails.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkShowWaiterDetails.setPreferredSize(new java.awt.Dimension(350, 30));

        jLabelTableNameTextColour.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTableNameTextColour.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelTableNameTextColour.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTableNameTextColour.setText(bundle.getString("label.textclourtablename")); // NOI18N
        jLabelTableNameTextColour.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelTableNameTextColour.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelTableNameTextColour.setPreferredSize(new java.awt.Dimension(350, 30));

        jCheckPrice00.setBackground(new java.awt.Color(28, 35, 49));
        jCheckPrice00.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCheckPrice00.setForeground(new java.awt.Color(255, 255, 255));
        jCheckPrice00.setText(bundle.getString("label.pricewith00")); // NOI18N
        jCheckPrice00.setToolTipText("");
        jCheckPrice00.setMaximumSize(new java.awt.Dimension(0, 25));
        jCheckPrice00.setMinimumSize(new java.awt.Dimension(0, 0));
        jCheckPrice00.setPreferredSize(new java.awt.Dimension(250, 25));
        jCheckPrice00.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckPrice00ActionPerformed(evt);
            }
        });

        jTaxIncluded.setBackground(new java.awt.Color(28, 35, 49));
        jTaxIncluded.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTaxIncluded.setForeground(new java.awt.Color(255, 255, 255));
        jTaxIncluded.setText(bundle.getString("label.taxincluded")); // NOI18N
        jTaxIncluded.setMaximumSize(new java.awt.Dimension(0, 25));
        jTaxIncluded.setMinimumSize(new java.awt.Dimension(0, 0));
        jTaxIncluded.setPreferredSize(new java.awt.Dimension(250, 25));

        jCloseCashbtn.setBackground(new java.awt.Color(28, 35, 49));
        jCloseCashbtn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCloseCashbtn.setForeground(new java.awt.Color(255, 255, 255));
        jCloseCashbtn.setText(bundle.getString("message.systemclosecash")); // NOI18N
        jCloseCashbtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jCloseCashbtn.setMaximumSize(new java.awt.Dimension(0, 25));
        jCloseCashbtn.setMinimumSize(new java.awt.Dimension(0, 0));
        jCloseCashbtn.setPreferredSize(new java.awt.Dimension(250, 25));

        jMoveAMountBoxToTop.setBackground(new java.awt.Color(28, 35, 49));
        jMoveAMountBoxToTop.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jMoveAMountBoxToTop.setForeground(new java.awt.Color(255, 255, 255));
        jMoveAMountBoxToTop.setText(bundle.getString("label.inputamount")); // NOI18N
        jMoveAMountBoxToTop.setMaximumSize(new java.awt.Dimension(0, 25));
        jMoveAMountBoxToTop.setMinimumSize(new java.awt.Dimension(0, 0));
        jMoveAMountBoxToTop.setPreferredSize(new java.awt.Dimension(250, 25));

        jchkTextOverlay.setBackground(new java.awt.Color(28, 35, 49));
        jchkTextOverlay.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkTextOverlay.setForeground(new java.awt.Color(255, 255, 255));
        jchkTextOverlay.setText(bundle.getString("label.currencybutton")); // NOI18N
        jchkTextOverlay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jchkTextOverlay.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkTextOverlay.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkTextOverlay.setPreferredSize(new java.awt.Dimension(250, 25));

        jchkautoRefreshTableMap.setBackground(new java.awt.Color(28, 35, 49));
        jchkautoRefreshTableMap.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkautoRefreshTableMap.setForeground(new java.awt.Color(255, 255, 255));
        jchkautoRefreshTableMap.setText(bundle.getString("label.autoRefreshTableMap")); // NOI18N
        jchkautoRefreshTableMap.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkautoRefreshTableMap.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkautoRefreshTableMap.setPreferredSize(new java.awt.Dimension(200, 30));
        jchkautoRefreshTableMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkautoRefreshTableMapActionPerformed(evt);
            }
        });

        jLabelInactiveTime1.setBackground(new java.awt.Color(255, 255, 255));
        jLabelInactiveTime1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelInactiveTime1.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInactiveTime1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelInactiveTime1.setText(bundle.getString("label.autolofftime")); // NOI18N
        jLabelInactiveTime1.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelInactiveTime1.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelInactiveTime1.setPreferredSize(new java.awt.Dimension(100, 30));

        jTxtautoRefreshTimer.setBackground(new java.awt.Color(75, 81, 93));
        jTxtautoRefreshTimer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTxtautoRefreshTimer.setForeground(new java.awt.Color(255, 255, 255));
        jTxtautoRefreshTimer.setText("0");
        jTxtautoRefreshTimer.setMaximumSize(new java.awt.Dimension(0, 25));
        jTxtautoRefreshTimer.setMinimumSize(new java.awt.Dimension(0, 0));
        jTxtautoRefreshTimer.setPreferredSize(new java.awt.Dimension(0, 30));

        jLblautoRefresh.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLblautoRefresh.setForeground(new java.awt.Color(255, 255, 255));
        jLblautoRefresh.setText(bundle.getString("label.autoRefreshTableMapTimer")); // NOI18N
        jLblautoRefresh.setMaximumSize(new java.awt.Dimension(0, 25));
        jLblautoRefresh.setMinimumSize(new java.awt.Dimension(0, 0));
        jLblautoRefresh.setPreferredSize(new java.awt.Dimension(200, 30));

        jchkSCOnOff.setBackground(new java.awt.Color(28, 35, 49));
        jchkSCOnOff.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkSCOnOff.setForeground(new java.awt.Color(255, 255, 255));
        jchkSCOnOff.setText(bundle.getString("label.SCOnOff")); // NOI18N
        jchkSCOnOff.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkSCOnOff.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkSCOnOff.setPreferredSize(new java.awt.Dimension(0, 25));
        jchkSCOnOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkSCOnOffActionPerformed(evt);
            }
        });

        jLabelSCRate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelSCRate.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSCRate.setText(bundle.getString("label.SCRate")); // NOI18N
        jLabelSCRate.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelSCRate.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelSCRate.setPreferredSize(new java.awt.Dimension(190, 30));

        jTextSCRate.setBackground(new java.awt.Color(75, 81, 93));
        jTextSCRate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextSCRate.setForeground(new java.awt.Color(255, 255, 255));
        jTextSCRate.setText("0");
        jTextSCRate.setMaximumSize(new java.awt.Dimension(0, 25));
        jTextSCRate.setMinimumSize(new java.awt.Dimension(0, 0));
        jTextSCRate.setPreferredSize(new java.awt.Dimension(0, 30));
        jTextSCRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextSCRateActionPerformed(evt);
            }
        });

        jLabelSCRatePerCent.setBackground(new java.awt.Color(28, 35, 49));
        jLabelSCRatePerCent.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabelSCRatePerCent.setForeground(new java.awt.Color(255, 255, 255));
        jLabelSCRatePerCent.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSCRatePerCent.setText(bundle.getString("label.SCZero")); // NOI18N
        jLabelSCRatePerCent.setMaximumSize(new java.awt.Dimension(0, 25));
        jLabelSCRatePerCent.setMinimumSize(new java.awt.Dimension(0, 0));
        jLabelSCRatePerCent.setPreferredSize(new java.awt.Dimension(0, 30));

        jchkSCRestaurant.setBackground(new java.awt.Color(28, 35, 49));
        jchkSCRestaurant.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkSCRestaurant.setForeground(new java.awt.Color(255, 255, 255));
        jchkSCRestaurant.setText(bundle.getString("label.SCRestaurant")); // NOI18N
        jchkSCRestaurant.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkSCRestaurant.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkSCRestaurant.setPreferredSize(new java.awt.Dimension(0, 25));

        jCustomerColour1.setBackground(new java.awt.Color(75, 81, 93));
        jCustomerColour1.setForeground(new java.awt.Color(0, 153, 255));
        jCustomerColour1.setToolTipText(bundle.getString("tooltip.prodhtmldisplayColourChooser")); // NOI18N
        jCustomerColour1.setColorDisplayType(com.alee.extended.colorchooser.ColorDisplayType.hex);
        jCustomerColour1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jCustomerColour1.setPreferredSize(new java.awt.Dimension(200, 30));

        jWaiterColour1.setBackground(new java.awt.Color(75, 81, 93));
        jWaiterColour1.setForeground(new java.awt.Color(0, 153, 255));
        jWaiterColour1.setToolTipText(bundle.getString("tooltip.prodhtmldisplayColourChooser")); // NOI18N
        jWaiterColour1.setColorDisplayType(com.alee.extended.colorchooser.ColorDisplayType.hex);
        jWaiterColour1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jWaiterColour1.setPreferredSize(new java.awt.Dimension(200, 30));

        jTableNameColour1.setBackground(new java.awt.Color(75, 81, 93));
        jTableNameColour1.setForeground(new java.awt.Color(0, 153, 255));
        jTableNameColour1.setToolTipText(bundle.getString("tooltip.prodhtmldisplayColourChooser")); // NOI18N
        jTableNameColour1.setColorDisplayType(com.alee.extended.colorchooser.ColorDisplayType.hex);
        jTableNameColour1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTableNameColour1.setPreferredSize(new java.awt.Dimension(200, 30));

        jchkPriceUpdate.setBackground(new java.awt.Color(28, 35, 49));
        jchkPriceUpdate.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkPriceUpdate.setForeground(new java.awt.Color(255, 255, 255));
        jchkPriceUpdate.setText(bundle.getString("label.priceupdate")); // NOI18N
        jchkPriceUpdate.setToolTipText(bundle.getString("tooltip.priceupdate")); // NOI18N
        jchkPriceUpdate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jchkPriceUpdate.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkPriceUpdate.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkPriceUpdate.setPreferredSize(new java.awt.Dimension(250, 25));

        jchkBarcodetype.setBackground(new java.awt.Color(28, 35, 49));
        jchkBarcodetype.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkBarcodetype.setForeground(new java.awt.Color(255, 255, 255));
        jchkBarcodetype.setText(bundle.getString("label.barcodetype")); // NOI18N
        jchkBarcodetype.setToolTipText(bundle.getString("tooltip.barcodetype")); // NOI18N
        jchkBarcodetype.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jchkBarcodetype.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkBarcodetype.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkBarcodetype.setPreferredSize(new java.awt.Dimension(250, 25));

        jchkTransBtn.setBackground(new java.awt.Color(28, 35, 49));
        jchkTransBtn.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jchkTransBtn.setForeground(new java.awt.Color(255, 255, 255));
        jchkTransBtn.setText(bundle.getString("label.tabletransbutton")); // NOI18N
        jchkTransBtn.setMaximumSize(new java.awt.Dimension(0, 25));
        jchkTransBtn.setMinimumSize(new java.awt.Dimension(0, 0));
        jchkTransBtn.setPreferredSize(new java.awt.Dimension(350, 30));
        jchkTransBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchkTransBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(jchkSCOnOff, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jchkautoRefreshTableMap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabelInactiveTime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jchkShowWaiterDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabelTableNameTextColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jchkShowCustomerDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jchkTransBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(50, 50, 50))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabelSCRate, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabelSCRatePerCent, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextSCRate, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jchkSCRestaurant, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTxtautoRefreshTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLblautoRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTableNameColour1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jWaiterColour1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jCustomerColour1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(15, 15, 15)
                                        .addComponent(jchkAutoLogoffToTables, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jchkAutoLogoff, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelInactiveTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(100, 100, 100)
                                        .addComponent(jTextAutoLogoffTime, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabelTimedMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jchkInstance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTaxIncluded, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jchkTextOverlay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jchkPriceUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(150, 150, 150)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckPrice00, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jMoveAMountBoxToTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCloseCashbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jchkBarcodetype, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTaxIncluded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckPrice00, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkInstance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jMoveAMountBoxToTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkTextOverlay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCloseCashbtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkPriceUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkBarcodetype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkAutoLogoff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInactiveTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextAutoLogoffTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTimedMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jchkAutoLogoffToTables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkautoRefreshTableMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInactiveTime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxtautoRefreshTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblautoRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jchkSCOnOff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSCRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextSCRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSCRatePerCent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jchkSCRestaurant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkShowCustomerDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCustomerColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchkShowWaiterDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jWaiterColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTableNameTextColour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTableNameColour1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jchkTransBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCustomerColour1.getAccessibleContext().setAccessibleName("colourChooser");
        jWaiterColour1.getAccessibleContext().setAccessibleName("colourChooser1");
        jTableNameColour1.getAccessibleContext().setAccessibleName("colourChooser2");
    }// </editor-fold>//GEN-END:initComponents

    private void jchkAutoLogoffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkAutoLogoffActionPerformed
        if (jchkAutoLogoff.isSelected()){
                jchkAutoLogoffToTables.setVisible(true);
                jLabelInactiveTime.setVisible(true);
                jLabelTimedMessage.setVisible(true);
                jTextAutoLogoffTime.setVisible(true);
        }else{    
                jchkAutoLogoffToTables.setVisible(false);
                jLabelInactiveTime.setVisible(false);
                jLabelTimedMessage.setVisible(false);
                jTextAutoLogoffTime.setVisible(false);
        }
    }//GEN-LAST:event_jchkAutoLogoffActionPerformed

    private void jCheckPrice00ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckPrice00ActionPerformed

    }//GEN-LAST:event_jCheckPrice00ActionPerformed

    private void jchkAutoLogoffToTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkAutoLogoffToTablesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkAutoLogoffToTablesActionPerformed

    private void jchkShowCustomerDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkShowCustomerDetailsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkShowCustomerDetailsActionPerformed

    private void jchkautoRefreshTableMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkautoRefreshTableMapActionPerformed
        if (jchkautoRefreshTableMap.isSelected()){
                jLblautoRefresh.setVisible(true);
                jLabelInactiveTime1.setVisible(true);
                jTxtautoRefreshTimer.setVisible(true);
        }else{    
                jLblautoRefresh.setVisible(false);
                jLabelInactiveTime1.setVisible(false);
                jTxtautoRefreshTimer.setVisible(false);
        }  
    }//GEN-LAST:event_jchkautoRefreshTableMapActionPerformed

    private void jchkSCOnOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkSCOnOffActionPerformed
        if (jchkSCOnOff.isSelected()){
            jchkSCRestaurant.setVisible(true);
            jLabelSCRate.setVisible(true);
            jTextSCRate.setVisible(true);
            jLabelSCRatePerCent.setVisible(true);
        }else{
            jchkSCRestaurant.setVisible(false);
            jLabelSCRate.setVisible(false);
            jTextSCRate.setVisible(false);
            jLabelSCRatePerCent.setVisible(false);
        }
    }//GEN-LAST:event_jchkSCOnOffActionPerformed

    private void jTextSCRateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextSCRateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextSCRateActionPerformed

    private void jchkTransBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchkTransBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jchkTransBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckPrice00;
    private javax.swing.JCheckBox jCloseCashbtn;
    private com.alee.extended.colorchooser.WebColorChooserField jCustomerColour1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelInactiveTime;
    private javax.swing.JLabel jLabelInactiveTime1;
    private javax.swing.JLabel jLabelSCRate;
    private javax.swing.JLabel jLabelSCRatePerCent;
    private javax.swing.JLabel jLabelTableNameTextColour;
    private javax.swing.JLabel jLabelTimedMessage;
    private javax.swing.JLabel jLblautoRefresh;
    private javax.swing.JCheckBox jMoveAMountBoxToTop;
    private com.alee.extended.colorchooser.WebColorChooserField jTableNameColour1;
    private javax.swing.JCheckBox jTaxIncluded;
    private javax.swing.JTextField jTextAutoLogoffTime;
    private javax.swing.JTextField jTextSCRate;
    private javax.swing.JTextField jTxtautoRefreshTimer;
    private com.alee.extended.colorchooser.WebColorChooserField jWaiterColour1;
    private javax.swing.JCheckBox jchkAutoLogoff;
    private javax.swing.JCheckBox jchkAutoLogoffToTables;
    private javax.swing.JCheckBox jchkBarcodetype;
    private javax.swing.JCheckBox jchkInstance;
    private javax.swing.JCheckBox jchkPriceUpdate;
    private javax.swing.JCheckBox jchkSCOnOff;
    private javax.swing.JCheckBox jchkSCRestaurant;
    private javax.swing.JCheckBox jchkShowCustomerDetails;
    private javax.swing.JCheckBox jchkShowWaiterDetails;
    private javax.swing.JCheckBox jchkTextOverlay;
    private javax.swing.JCheckBox jchkTransBtn;
    private javax.swing.JCheckBox jchkautoRefreshTableMap;
    // End of variables declaration//GEN-END:variables
    
}
