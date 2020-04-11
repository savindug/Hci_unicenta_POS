
package com.openbravo.pos.voucher;

import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.QBFBuilder;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.reports.ReportFields;
import com.openbravo.pos.reports.ReportFieldsArray;
// import com.openbravo.pos.util.FontUtil;
import com.openbravo.pos.util.JRViewer400;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JDialogReportPanel extends javax.swing.JDialog {


    private JRViewer400 reportviewer = null;    
    private JasperReport jr = null;
    private AppView m_App;
    private List<String> paramnames = new ArrayList<>();
    private List<Datas> fielddatas = new ArrayList<>();
    private List<String> fieldnames = new ArrayList<>();
    private String sentence;

    private JDialogReportPanel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    private JDialogReportPanel(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }
    
    public static JDialogReportPanel getDialog(Component parent,AppView _App,
            VoucherInfo voucherInfo,BufferedImage image) {
        Window window = getWindow(parent);
        
        JDialogReportPanel myMsg;
        if (window instanceof Frame) { 
            myMsg = new JDialogReportPanel((Frame) window, true);
        } else {
            myMsg = new JDialogReportPanel((Dialog) window, true);
        }
        myMsg.init(_App,voucherInfo,image);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }
    
     protected BaseSentence getSentence() {
        return new StaticSentence(m_App.getSession()
            , new QBFBuilder(sentence, paramnames.toArray(new String[paramnames.size()]))
            , null
            , new SerializerReadBasic(fielddatas.toArray(new Datas[fielddatas.size()])));
    }
     
     
     protected ReportFields getReportFields() {
        return new ReportFieldsArray(fieldnames.toArray(new String[fieldnames.size()]));
    } 
     
//    protected abstract String getResourceBundle();    
    
      private void launchreport(VoucherInfo voucherInfo,BufferedImage image) {     
        
        if (jr != null) {
            try {     
                
                String res = "com/openbravo/reports/voucher_messages";//getResourceBundle(); 
//                String res = getResourceBundle();                  
                
                Map reportparams = new HashMap();
                reportparams.put("CUSTOMER_NAME", voucherInfo.getCustomerName());
                reportparams.put("LOGO", image);
                reportparams.put("CODE", voucherInfo.getVoucherNumber());
                reportparams.put("ISSUED", new Date());
                reportparams.put("VALUE", voucherInfo.getAmount());

                if (res != null) {
                      reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(res));
                }                
              
                JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new  JREmptyDataSource());    

                reportviewer.loadJasperPrint(jp);     
                
            } catch (MissingResourceException e) {    
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadresourcedata"), e);
                msg.show(this);
            } catch (JRException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfillreport"), e);
                msg.show(this);
            } 
        }
    }
      
   

    private void init(AppView _App,VoucherInfo voucherInfo,BufferedImage image) {
        m_App =_App;
        initComponents();
        
         reportviewer = new JRViewer400(null);                                
        jPanel4.add(reportviewer, BorderLayout.CENTER);
        
        try {     
//            jr = JasperCompileManager.compileReport("reports" +  "/com/openbravo/reports/voucher" + ".jrxml");   

                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(getReport() + ".jrxml"));            
                jr = JasperCompileManager.compileReport(jd);              
            
        } catch (JRException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
            jr = null;
        }  
    
        launchreport(voucherInfo,image);

        getRootPane().setDefaultButton(jcmdOK);

    }
      
    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jcmdOK = new javax.swing.JButton();
        jcmdCancel = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("form.customertitle")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel3.setBackground(new java.awt.Color(28, 35, 49));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBackground(new java.awt.Color(28, 35, 49));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel8.setBackground(new java.awt.Color(28, 35, 49));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(28, 35, 49));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jcmdOK.setBackground(new java.awt.Color(55, 71, 79));
        jcmdOK.setForeground(new java.awt.Color(255, 255, 255));
        jcmdOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/ok.png"))); // NOI18N
        jcmdOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        jcmdOK.setBorder(null);
        jcmdOK.setEnabled(false);
        jcmdOK.setFocusPainted(false);
        jcmdOK.setFocusable(false);
        jcmdOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdOK.setRequestFocusEnabled(false);
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdOK);

        jcmdCancel.setBackground(new java.awt.Color(55, 71, 79));
        jcmdCancel.setForeground(new java.awt.Color(255, 255, 255));
        jcmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/cancel.png"))); // NOI18N
        jcmdCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        jcmdCancel.setBorder(null);
        jcmdCancel.setFocusPainted(false);
        jcmdCancel.setFocusable(false);
        jcmdCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdCancel.setRequestFocusEnabled(false);
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdCancel);

        jPanel8.add(jPanel1, java.awt.BorderLayout.LINE_END);

        jButton1.setBackground(new java.awt.Color(55, 71, 79));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("jButton1");
        jButton1.setBorder(null);
        jPanel8.add(jButton1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel8, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(837, 687));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed


        dispose();
        
    }//GEN-LAST:event_jcmdOKActionPerformed

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed

        dispose();
        
    }//GEN-LAST:event_jcmdCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JButton jcmdCancel;
    private javax.swing.JButton jcmdOK;
    // End of variables declaration//GEN-END:variables

    private String getReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
