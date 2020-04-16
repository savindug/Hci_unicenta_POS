

package com.openbravo.pos.voucher;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.data.user.EditorRecord;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.CustomerInfo;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.customers.JDialogNewCustomer;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.util.ValidateBuilder;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VoucherEditor extends javax.swing.JPanel implements EditorRecord {

    private static final DateFormat m_simpledate = new SimpleDateFormat("MM-yy");
    private Object id;
    private final DataLogicCustomers dlCustomers;
    private final  DataLogicSystem dlSystem;
    private CustomerInfo customerInfo;
    private final AppView m_app;
    
    private final ComboBoxValModel m_ReasonModel;    

     public VoucherEditor(DirtyManager dirty,AppView app) {
        m_app = app;

        initComponents();
  
        dlCustomers = (DataLogicCustomers) app.getBean("com.openbravo.pos.customers.DataLogicCustomers");
        dlSystem= (DataLogicSystem) app.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_jNumber.getDocument().addDocumentListener(dirty);
        m_jCustomer.getDocument().addDocumentListener(dirty);
        m_jAmount.getDocument().addDocumentListener(dirty);
        jButtonPrint.setVisible(false);
        
        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(AppLocal.getIntString("cboption.find"));
        m_ReasonModel.add(AppLocal.getIntString("cboption.create"));              
        webCBCustomer.setModel(m_ReasonModel);   
        
        
        writeValueEOF();
    }
     
    @Override
    public void writeValueEOF() {
        id = null;
        m_jNumber.setText(null);
        m_jNumber.setEnabled(false);
        m_jCustomer.setText(null);
        m_jCustomer.setEnabled(false);
        m_jAmount.setText(null);
        m_jAmount.setEnabled(false);
//        jButtonPrint.setEnabled(false);
    }
    
    @Override
    public void writeValueInsert() {
        id = UUID.randomUUID().toString();
        m_jNumber.setText(generateVoucherNumber());
        m_jNumber.setEnabled(true);
        m_jCustomer.setText(null);
        m_jCustomer.setEnabled(true);
        m_jAmount.setText(null);
        m_jAmount.setEnabled(true);
//        jButtonPrint.setEnabled(false);
        jButtonPrint.setEnabled(true);        
    }
    
    @Override
    public void writeValueDelete(Object value) {

        try {
            Object[] attr = (Object[]) value;
            id = attr[0];
            m_jNumber.setText(Formats.STRING.formatValue(attr[1]));
            m_jNumber.setEnabled(false);
            customerInfo = dlCustomers.getCustomerInfo(attr[2].toString());
            m_jCustomer.setText(customerInfo.getName());
            m_jCustomer.setEnabled(false);
            m_jAmount.setText(Formats.CURRENCY.formatValue(attr[3]));
            m_jAmount.setEnabled(false);
//            jButtonPrint.setEnabled(false);
        } catch (BasicException ex) {
            Logger.getLogger(VoucherEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }    
    
    @Override
    public void writeValueEdit(Object value) {

        try {
            Object[] attr = (Object[]) value;
            id = attr[0];
            m_jNumber.setText(Formats.STRING.formatValue(attr[1]));
            m_jNumber.setEnabled(true);
            customerInfo = dlCustomers.getCustomerInfo(attr[2].toString());
            m_jCustomer.setText(customerInfo.getName());
            m_jCustomer.setEnabled(true);
            m_jAmount.setText(Formats.CURRENCY.formatValue(attr[3]));
            m_jAmount.setEnabled(true);
//            jButtonPrint.setEnabled(true);
        } catch (BasicException ex) {
            Logger.getLogger(VoucherEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object createValue() throws BasicException {
        
        Object[] attr = new Object[4];

        attr[0] = id;
        attr[1] = m_jNumber.getText();
        attr[2] = customerInfo.getId();
        attr[3] =  Formats.DOUBLE.parseValue(m_jAmount.getText());

        return attr;
    }    
     
    @Override
    public Component getComponent() {
        return this;
    }
    
    @Override
    public void refresh() {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jNumber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jCustomer = new javax.swing.JTextField();
        m_jAmount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButtonPrint = new javax.swing.JButton();
        webCBCustomer = new com.alee.laf.combobox.WebComboBox();

        setBackground(new java.awt.Color(28, 35, 49));
        setForeground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(AppLocal.getIntString("label.Number")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jNumber.setBackground(new java.awt.Color(75, 81, 93));
        m_jNumber.setForeground(new java.awt.Color(255, 255, 255));
        m_jNumber.setPreferredSize(new java.awt.Dimension(240, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText(AppLocal.getIntString("label.customer")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        m_jCustomer.setEditable(false);
        m_jCustomer.setBackground(new java.awt.Color(75, 81, 93));
        m_jCustomer.setForeground(new java.awt.Color(255, 255, 255));
        m_jCustomer.setPreferredSize(new java.awt.Dimension(240, 30));

        m_jAmount.setBackground(new java.awt.Color(75, 81, 93));
        m_jAmount.setForeground(new java.awt.Color(255, 255, 255));
        m_jAmount.setPreferredSize(new java.awt.Dimension(240, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText(AppLocal.getIntString("label.paymenttotal")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        jButtonPrint.setBackground(new java.awt.Color(55, 71, 79));
        jButtonPrint.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/printer24.png"))); // NOI18N
        jButtonPrint.setToolTipText(AppLocal.getIntString("button.print")); // NOI18N
        jButtonPrint.setBorder(null);
        jButtonPrint.setFocusPainted(false);
        jButtonPrint.setFocusable(false);
        jButtonPrint.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jButtonPrint.setPreferredSize(new java.awt.Dimension(80, 45));
        jButtonPrint.setRequestFocusEnabled(false);
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        webCBCustomer.setBackground(new java.awt.Color(75, 81, 93));
        webCBCustomer.setBorder(null);
        webCBCustomer.setForeground(new java.awt.Color(255, 255, 255));
        webCBCustomer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Find", "Create" }));
        webCBCustomer.setToolTipText(AppLocal.getIntString("tooltip.vouchercustomer")); // NOI18N
        webCBCustomer.setExpandIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/customer.png")));
        webCBCustomer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        webCBCustomer.setPreferredSize(new java.awt.Dimension(110, 45));
        webCBCustomer.setRound(3);
        webCBCustomer.setShadeWidth(3);
        webCBCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webCBCustomerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(187, 187, 187)
                        .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(m_jAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(webCBCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(webCBCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed

        try {
            VoucherInfo  voucherInfo = dlCustomers.getVoucherInfoAll(id.toString());
            BufferedImage image = dlSystem.getResourceAsImage("Window.Logo");
            
            if (voucherInfo!=null){
                JDialogReportPanel dialog = JDialogReportPanel.getDialog(this,m_app,voucherInfo,image);
                dialog.setVisible(true);
            }   
        } catch (BasicException ex) {
            Logger.getLogger(VoucherEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}//GEN-LAST:event_jButtonPrintActionPerformed

    private void webCBCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webCBCustomerActionPerformed

        if(webCBCustomer.getSelectedIndex() == 0){

            JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);
            finder.setVisible(true);
            customerInfo = finder.getSelectedCustomer() ;

            if (finder.getSelectedCustomer()!=null){
                m_jCustomer.setText(customerInfo.getName()); 
            }
        } else {

         JDialogNewCustomer dialog = JDialogNewCustomer.getDialog(this,m_app);
         dialog.setVisible(true);
       
           customerInfo=dialog.getSelectedCustomer();
            if (dialog.getSelectedCustomer()!=null){
                 m_jCustomer.setText(customerInfo.getName());  
            }
        }
    }//GEN-LAST:event_webCBCustomerActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField m_jAmount;
    private javax.swing.JTextField m_jCustomer;
    private javax.swing.JTextField m_jNumber;
    private com.alee.laf.combobox.WebComboBox webCBCustomer;
    // End of variables declaration//GEN-END:variables

    public boolean isDataValid() {
        ValidateBuilder validate = new ValidateBuilder(this);
        validate.setValidate(m_jNumber.getText(),ValidateBuilder.IS_NOT_EMPTY,
                AppLocal.getIntString("message.message.emptynumber"));
        validate.setValidate(m_jCustomer.getText(),ValidateBuilder.IS_NOT_EMPTY,
                AppLocal.getIntString("message.emptycustomer"));
        validate.setValidate(m_jAmount.getText(),ValidateBuilder.IS_DOUBLE,
                AppLocal.getIntString("message.numericamount"));
        return validate.getValid();
    }
    
    
    public String generateVoucherNumber(){
        String result="";

        try {
            result = "VO-";
            String date = m_simpledate.format(new Date());
            result = result + date;
            String lastNumber= (String)dlCustomers.getVoucherNumber().find(result);
            int newNumber = 1 ;

            if (lastNumber!=null){
               newNumber = Integer.parseInt(lastNumber) +1;
           }
            result = result + "-" + getNewNumber(newNumber);
            
            return result;
            
        } catch (BasicException ex) {
        }
        return result;
    }
    
    
    private String getNewNumber(int newNumber){
        String newNo = newNumber + "";
        String zero = "";
        for (int i=0;i< 3 - newNo.length();i++){
            zero = zero + "0";
        }
        return zero+newNo;
    }
}
