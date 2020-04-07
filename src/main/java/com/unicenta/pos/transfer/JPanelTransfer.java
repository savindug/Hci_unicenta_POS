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

package com.unicenta.pos.transfer;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Session;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.config.PanelConfig;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.DirectoryEvent;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import javax.swing.*;

/**
 *
 * @author JG uniCenta
 */
public class JPanelTransfer extends JPanel implements JPanelView {

    private DirtyManager dirty = new DirtyManager();
    private AppConfig config;
    private AppProperties m_props;
    private List<PanelConfig> m_panelconfig;
    
    private Connection con_source;
    private Connection con_target;

    private String sDB_source;
    private String sDB_target;
    
    private Session session_source;
    private Session session_target;

    private ResultSet rs;

    private Statement stmt_source;
    private Statement stmt_target;
    private String SQL;
    private PreparedStatement pstmt;

    private String ticketsnum;
    private String ticketsnumRefund;
    private String ticketsnumPayment;

    private String eScript = "";
    private String eScript1 = "";
    private String eScript2 = "";
    private String eScript3 = "";

    /**
     * Creates new form JPaneldbTransfer
     * @param oApp
     */
    public JPanelTransfer(AppView oApp) {
        this(oApp.getProperties());
    }

    /**
     *
     * @param props
     */
    public JPanelTransfer(AppProperties props) {

        initComponents();

        config = new AppConfig(props.getConfigFile());
        m_props = props;
        m_panelconfig = new ArrayList<>();
        config.load();
        
        for (PanelConfig c : m_panelconfig) {
            c.loadProperties(config);
        }

        txtDBDriverLib.getDocument().addDocumentListener(dirty);
        txtDBDriver.getDocument().addDocumentListener(dirty);
        txtDBURL.getDocument().addDocumentListener(dirty);
        txtDBPass.getDocument().addDocumentListener(dirty);
        txtDBUser.getDocument().addDocumentListener(dirty);

        jbtnDbDriverLib.addActionListener(new DirectoryEvent(txtDBDriverLib));

        cbTarget.addActionListener(dirty);
        
        cbTarget.addItem("MySQL");
    }

    /**
     *
     * @return
     */
    @SuppressWarnings("empty-statement")
    public Boolean createTargetDB() {

        if ((!"MySQL".equals(sDB_target)) && (!"PostgreSQL".equals(sDB_target))) {
            return (false);
        }

        eScript = "/com/openbravo/pos/scripts/" + sDB_target + "-create-transfer.sql";
//        eScript2 = "/com/openbravo/pos/scripts/" + sDB_target + "-DropFK.sql";
//        eScript3 = "/com/openbravo/pos/scripts/SQL-CreateFK.sql";

        if ("".equals(eScript)) {
            return (false);
        }
        // create a blank database to migrate into
        try {
            BatchSentence bsentence = new BatchSentenceResource(session_target, eScript);;
            bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
            bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
            bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));


            java.util.List l = bsentence.list();
            if (l.size() > 0) {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("migration.warning"), 
                                l.toArray(new Throwable[l.size()])));
            }

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("migration.warningnodefault"), 
                            e));
            session_source.close();
        } finally {
        }


        try {
            BatchSentence bsentence = new BatchSentenceResource(session_target, eScript2);
            java.util.List l = bsentence.list();
            if (l.size() > 0) {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("migration.warning"), 
                                l.toArray(new Throwable[l.size()])));
            }

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("migration.warningnofk"), 
                            e));
            session_source.close();
        } finally {
        }


        return (true);
    }

    /**
     *
     * @return
     */
    public Boolean addFKeys() {
        if ("".equals(eScript3)) {
            return (false);
        }
        try {
            BatchSentence bsentence = new BatchSentenceResource(session_target, eScript3);
            java.util.List l = bsentence.list();
            if (l.size() > 0) {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("migration.warning"), 
                                l.toArray(new Throwable[l.size()])));
            }

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("database.ScriptNotFound"), 
                            e));
            session_source.close();
        } finally {
        }
        return (true);

    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Configuration");
    }

    /**
     *
     * @return
     */
    public Boolean getTargetDetail() {

        String db_user2 = txtDBUser.getText();
        String db_url2 = txtDBURL.getText();
        char[] pass = txtDBPass.getPassword();
        String db_password2 = new String(pass);

        Properties connectionProps = new Properties();
        connectionProps.put("user", db_user2);
        connectionProps.put("password", db_password2);
        try {
            Class.forName(txtDBDriver.getText());

            ClassLoader cloader = new URLClassLoader(
                    new URL[]{new File(txtDBDriverLib.getText()).toURI().toURL()});
            DriverManager.registerDriver(
                    new DriverWrapper((Driver) Class.forName(txtDBDriver.getText(), 
                            true, cloader).newInstance()));
            con_target = (Connection) DriverManager.getConnection(
                    db_url2, db_user2, db_password2);

            session_target = new Session(db_url2, db_user2, db_password2);
            sDB_target = con_target.getMetaData().getDatabaseProductName();
            return (true);
        } catch (ClassNotFoundException 
                | MalformedURLException 
                | InstantiationException 
                | IllegalAccessException 
                | SQLException e) {
            
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("database.UnableToConnect"), 
                            e));
            return (false);
        }

    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

    
        String db_user = (m_props.getProperty("db.user"));
        String db_url = (m_props.getProperty("db.URL"));
        String db_password = (m_props.getProperty("db.password"));

        if (db_user != null 
                && db_password != null 
                && db_password.startsWith("crypt:")) {

            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        try {
            session_source = AppViewConnection.createSession(m_props);
            con_source  = DriverManager.getConnection(db_url, db_user, db_password);
            sDB_source = con_source.getMetaData().getDatabaseProductName();
        } catch (BasicException | SQLException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("database.UnableToConnect"), 
                            e));
            System.exit(0);
        }

    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        return (true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        webPanel1 = new com.alee.laf.panel.WebPanel();
        cbTarget = new com.alee.laf.combobox.WebComboBox();
        txtDBDriverLib = new com.alee.laf.text.WebTextField();
        txtDBDriver = new com.alee.laf.text.WebTextField();
        txtDBUser = new com.alee.laf.text.WebTextField();
        txtDBURL = new com.alee.laf.text.WebTextField();
        txtDBPass = new com.alee.laf.text.WebPasswordField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jButtonTest = new javax.swing.JButton();
        jbtnMigrate = new javax.swing.JButton();
        webPanel2 = new com.alee.laf.panel.WebPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jbtnExit = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setPreferredSize(new java.awt.Dimension(650, 400));

        jPanel1.setLayout(null);

        webPanel1.setBackground(new java.awt.Color(255, 255, 255));

        cbTarget.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbTarget.setPreferredSize(new java.awt.Dimension(200, 30));

        txtDBDriverLib.setText("webTextField1");
        txtDBDriverLib.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDBDriverLib.setPreferredSize(new java.awt.Dimension(300, 30));

        txtDBDriver.setText("webTextField2");
        txtDBDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDBDriver.setPreferredSize(new java.awt.Dimension(300, 30));

        txtDBUser.setText("webTextField2");
        txtDBUser.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDBUser.setPreferredSize(new java.awt.Dimension(300, 30));

        txtDBURL.setText("webTextField1");
        txtDBURL.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDBURL.setPreferredSize(new java.awt.Dimension(300, 30));

        txtDBPass.setText("webPasswordField1");
        txtDBPass.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDBPass.setPreferredSize(new java.awt.Dimension(300, 30));

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(64, 32));

        jButtonTest.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/database.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jButtonTest.setText(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.setPreferredSize(new java.awt.Dimension(80, 45));
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestjButtonTestConnectionActionPerformed(evt);
            }
        });

        jbtnMigrate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnMigrate.setText(AppLocal.getIntString("button.migrate")); // NOI18N
        jbtnMigrate.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnMigrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMigrateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout webPanel1Layout = new javax.swing.GroupLayout(webPanel1);
        webPanel1.setLayout(webPanel1Layout);
        webPanel1Layout.setHorizontalGroup(
            webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(webPanel1Layout.createSequentialGroup()
                        .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDBUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbTarget, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(webPanel1Layout.createSequentialGroup()
                                .addComponent(txtDBDriverLib, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDBDriver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDBURL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(webPanel1Layout.createSequentialGroup()
                        .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDBPass, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(webPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        webPanel1Layout.setVerticalGroup(
            webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(cbTarget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDBDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(txtDBDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtDBURL, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtDBUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtDBPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(webPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setText("New  Database ");
        jLabel5.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel5.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel5.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N
        jLabel18.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel18.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel18.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("Label.DbDriver")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel1.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel1.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.DbURL")); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel2.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel2.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("Label.DbUser")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel3.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel3.setPreferredSize(new java.awt.Dimension(200, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("Label.DbPassword")); // NOI18N
        jLabel4.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel4.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel4.setPreferredSize(new java.awt.Dimension(200, 30));

        jbtnExit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnExit.setText(AppLocal.getIntString("Button.Exit")); // NOI18N
        jbtnExit.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout webPanel2Layout = new javax.swing.GroupLayout(webPanel2);
        webPanel2.setLayout(webPanel2Layout);
        webPanel2Layout.setHorizontalGroup(
            webPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webPanel2Layout.createSequentialGroup()
                .addGroup(webPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(webPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        webPanel2Layout.setVerticalGroup(
            webPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(webPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jLabel1.getAccessibleContext().setAccessibleName("DBDriver");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(webPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(webPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(webPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnMigrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMigrateActionPerformed
        if (getTargetDetail()) {

            if (createTargetDB()) {

                try {
                    stmt_source = (Statement) con_source.createStatement();
                    stmt_target = (Statement) con_target.createStatement();

// copy attribute table       
                    SQL = "SELECT * FROM attribute";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO attribute (ID, NAME) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.executeUpdate();
                    }

//  copy attributeinstance table        
                    SQL = "SELECT * FROM attributeinstance";
                    while (rs.next()) {
                        SQL = "INSERT INTO attributeinstance (ID, "
                                + "ATTRIBUTEINSTANCE_ID, ATTRIBUTE_ID, VALUE) "
                                + "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("ATTRIBUTEINSTANCE_ID"));
                        pstmt.setString(3, rs.getString("ATTRIBUTE_ID"));
                        pstmt.setString(4, rs.getString("VALUE"));
                        pstmt.executeUpdate();
                    }

// copy attributeset table       
                    SQL = "SELECT * FROM attributeset";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO attributeset (ID, NAME) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.executeUpdate();
                    }

// copy attributesetinstance table       
                    SQL = "SELECT * FROM attributesetinstance";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO attributesetinstance (ID, ATTRIBUTESET_ID, "
                                + "DESCRIPTION) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        //System.out.println(rs.getString("DESCRIPTION"));
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("ATTRIBUTESET_ID"));
                        pstmt.setString(3, rs.getString("DESCRIPTION"));
                        pstmt.executeUpdate();
                    }

// copy attributeuse table       
                    SQL = "SELECT * FROM attributeuse";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO attributeuse(ID, ATTRIBUTESET_ID, "
                                + "ATTRIBUTE_ID, LINENO) "
                                + "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("ATTRIBUTESET_ID"));
                        pstmt.setString(3, rs.getString("ATTRIBUTE_ID"));
                        pstmt.setInt(4, rs.getInt("LINENO"));
                        pstmt.executeUpdate();
                    }

// copy attributevalue table       
                    SQL = "SELECT * FROM attributevalue";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO attributevalue (ID, ATTRIBUTE_ID, VALUE) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("ATTRIBUTE_ID"));
                        pstmt.setString(3, rs.getString("VALUE"));
                        pstmt.executeUpdate();
                    }

// copy breaks table       
                    SQL = "SELECT * FROM breaks";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO breaks(ID, NAME, NOTES, VISIBLE) "
                                + "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("NOTES"));
                        pstmt.setBoolean(4, rs.getBoolean("VISIBLE"));
                        pstmt.executeUpdate();
                    }

// copy categories table       
                    SQL = "SELECT * FROM categories";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO categories(ID, NAME, PARENTID, IMAGE, "
                                + "TEXTTIP, CATSHOWNAME ) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("PARENTID"));
                        pstmt.setBytes(4, rs.getBytes("IMAGE"));
                        pstmt.setString(5, rs.getString("TEXTTIP"));
                        pstmt.setBoolean(6, rs.getBoolean("CATSHOWNAME"));

                        pstmt.executeUpdate();                        
                    }

// copy closedcash  table       
                    SQL = "SELECT * FROM closedcash";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO closedcash(MONEY, HOST, HOSTSEQUENCE, "
                                + "DATESTART, DATEEND, NOSALES ) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("MONEY"));
                        pstmt.setString(2, rs.getString("HOST"));
                        pstmt.setInt(3, rs.getInt("HOSTSEQUENCE"));
System.out.println(rs.getTimestamp("DATESTART"));                        
                        pstmt.setTimestamp(4, rs.getTimestamp("DATESTART"));
System.out.println(rs.getTimestamp("DATEEND"));                                                
                        pstmt.setTimestamp(5, rs.getTimestamp("DATEEND"));
                        pstmt.setInt(6, rs.getInt("NOSALES"));                        
                        pstmt.executeUpdate();
                    }



// copy csvimport  table       
                    SQL = "SELECT * FROM csvimport";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO csvimport (ID, ROWNUMBER, CSVERROR, " +
                                "REFERENCE, CODE, NAME, PRICEBUY, PRICESELL, " +
                                "PREVIOUSBUY, PREVIOUSSELL, CATEGORY  ) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("ROWNUMBER"));
                        pstmt.setString(3, rs.getString("CSVERROR"));
                        pstmt.setString(4, rs.getString("REFERENCE"));
                        pstmt.setString(5, rs.getString("CODE"));
                        pstmt.setString(6, rs.getString("NAME"));
                        pstmt.setDouble(7, rs.getDouble("PRICEBUY"));
                        pstmt.setDouble(8, rs.getDouble("PRICESELL"));
                        pstmt.setDouble(9, rs.getDouble("PREVIOUSBUY"));
                        pstmt.setDouble(10, rs.getDouble("PREVIOUSSELL"));
                        pstmt.setString(11, rs.getString("CATEGORY"));
                        pstmt.executeUpdate();
                    }



// copy CUSTOMERS  table       
                    SQL = "SELECT * FROM customers";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO customers( " +
                                "ID, SEARCHKEY, TAXID, NAME, TAXCATEGORY, CARD, " +
                                "MAXDEBT, ADDRESS, ADDRESS2, POSTAL, CITY, " +
                                "REGION, COUNTRY, FIRSTNAME, LASTNAME, EMAIL, " +
                                "PHONE, PHONE2, FAX, NOTES, VISIBLE, CURDATE, CURDEBT )"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("SEARCHKEY"));
                        pstmt.setString(3, rs.getString("TAXID"));
                        pstmt.setString(4, rs.getString("NAME"));
                        pstmt.setString(5, rs.getString("TAXCATEGORY"));
                        pstmt.setString(6, rs.getString("CARD"));
                        pstmt.setDouble(7, rs.getDouble("MAXDEBT"));
                        pstmt.setString(8, rs.getString("ADDRESS"));
                        pstmt.setString(9, rs.getString("ADDRESS2"));
                        pstmt.setString(10, rs.getString("POSTAL"));
                        pstmt.setString(11, rs.getString("CITY"));
                        pstmt.setString(12, rs.getString("REGION"));
                        pstmt.setString(13, rs.getString("COUNTRY"));
                        pstmt.setString(14, rs.getString("FIRSTNAME"));
                        pstmt.setString(15, rs.getString("LASTNAME"));
                        pstmt.setString(16, rs.getString("EMAIL"));
                        pstmt.setString(17, rs.getString("PHONE"));
                        pstmt.setString(18, rs.getString("PHONE2"));
                        pstmt.setString(19, rs.getString("FAX"));
                        pstmt.setString(20, rs.getString("NOTES"));
                        pstmt.setBoolean(21, rs.getBoolean("VISIBLE"));
                        pstmt.setTimestamp(22, rs.getTimestamp("CURDATE"));
                        pstmt.setDouble(23, rs.getDouble("CURDEBT"));
                        pstmt.setBytes(24, rs.getBytes("IMAGE"));  
                        pstmt.setBytes(25, rs.getBytes("isvip"));                          
                        pstmt.setBytes(26, rs.getBytes("discount"));  
                        pstmt.executeUpdate();
                    }

// copy DRAWEROPEN table       
                    SQL = "SELECT * FROM draweropened";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO draweropened(OPENDATE, NAME, TICKETID) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("OPENDATE"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("TICKETID"));
                        pstmt.executeUpdate();
                    }                    
                    
// copy FLOORS table       
                    SQL = "SELECT * FROM floors";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO floors (ID, NAME, IMAGE) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setBytes(3, rs.getBytes("IMAGE"));
                        pstmt.executeUpdate();
                    }
                    
// copy LEAVES table       
                    SQL = "SELECT * FROM leaves";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO leaves (ID, PPLID, NAME, STARTDATE, " +
                                "ENDDATE, NOTES) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("PPLID"));
                        pstmt.setString(3, rs.getString("NAME"));
                        pstmt.setTimestamp(4, rs.getTimestamp("STARTDATE"));
                        pstmt.setTimestamp(5, rs.getTimestamp("ENDDATE"));
                        pstmt.setString(6, rs.getString("NOTES"));
                        pstmt.executeUpdate();
                    }



// copy LOCATIONS table       
                    SQL = "SELECT * FROM locations";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO locations(ID, NAME, ADDRESS) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("ADDRESS"));
                        pstmt.executeUpdate();
                    }
// copy LINEREMOVED table       
                    SQL = "SELECT * FROM lineremoved";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO lineremoved (REMOVEDATE, NAME, " +
                                "TICKETID, PRODUCTID, PRODUCTNAME, UNITS) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setTimestamp(1, rs.getTimestamp("REMOVEDATE"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("TICKETID"));
                        pstmt.setString(4, rs.getString("PRODUCTID"));                        
                        pstmt.setString(5, rs.getString("PRODUCTNAME"));                        
                        pstmt.setDouble(6, rs.getDouble("UNITS"));                        
                        pstmt.executeUpdate();
                    }

// copy MOORERS TABLE     
                    SQL = "SELECT * FROM moorers";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO moorers(VESSELNAME, SIZE, DAYS, POWER) "
                                + "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("VESSELNAME"));
                        pstmt.setInt(2, rs.getInt("SIZE"));
                        pstmt.setInt(3, rs.getInt("DAYS"));
                        pstmt.setBoolean(4, rs.getBoolean("POWER"));
                        pstmt.executeUpdate();
                    }



// copy payments table       
                    SQL = "SELECT * FROM payments";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO payments(ID, RECEIPT, PAYMENT, TOTAL, " +
                                "TRANSID, RETURNMSG, NOTES, CARDNAME) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("RECEIPT"));
                        pstmt.setString(3, rs.getString("PAYMENT"));
                        pstmt.setDouble(4, rs.getDouble("TOTAL"));
                        pstmt.setString(5, rs.getString("TRANSID"));
                        pstmt.setBytes(6, rs.getBytes("RETURNMSG"));
                        pstmt.setString(7, rs.getString("NOTES"));
                        pstmt.setDouble(8, rs.getDouble("TENDERED"));
                        pstmt.setString(9, rs.getString("CARDNAME"));                        

                        pstmt.executeUpdate();
                    }


// copy PEOPLE table       
                    SQL = "SELECT * FROM people";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO people(ID, NAME, APPPASSWORD, CARD, " +
                                "ROLE, VISIBLE, IMAGE) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("APPPASSWORD"));
                        pstmt.setString(4, rs.getString("CARD"));
                        pstmt.setString(5, rs.getString("ROLE"));
                        pstmt.setBoolean(6, rs.getBoolean("VISIBLE"));
                        pstmt.setBytes(7, rs.getBytes("IMAGE"));
                        pstmt.executeUpdate();
                    }
                    
                    SQL = "SELECT * FROM pickup_number";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO pickup_number(ID) "
                                + "VALUES (?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.executeUpdate();
                    }

// copy Places table         
                    SQL = "SELECT * FROM places";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO places (ID, NAME, X, Y, FLOOR, " +
                                "CUSTOMER, WAITER, TICKETID, TABLEMOVED) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setInt(3, rs.getInt("X"));
                        pstmt.setInt(4, rs.getInt("Y"));
                        pstmt.setString(5, rs.getString("FLOOR"));
                        pstmt.setString(6, rs.getString("CUSTOMER"));
                        pstmt.setString(7, rs.getString("WAITER"));
                        pstmt.setString(8, rs.getString("TICKETID"));
                        pstmt.setBoolean(9, rs.getBoolean("TABLEMOVED"));
                        pstmt.executeUpdate();
                    }


// copy Products  table 
                    SQL = "SELECT * FROM products";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO products(ID, REFERENCE, CODE, CODETYPE, " 
                            + "NAME, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, "
                            + "ATTRIBUTESET_ID, STOCKCOST, STOCKVOLUME, IMAGE, "
                            + "ISCOM, ISSCALE, ISKITCHEN, PRINTKB, SENDSTATUS, "
                            + "ISSERVICE, ATTRIBUTES, DISPLAY, ISVPRICE, "
                            + "ISVERPATRIB, TEXTTIP, WARRANTY, STOCKUNITS)"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("REFERENCE"));
                        pstmt.setString(3, rs.getString("CODE"));
                        pstmt.setString(4, rs.getString("CODETYPE"));
                        pstmt.setString(5, rs.getString("NAME"));
                        pstmt.setDouble(6, rs.getDouble("PRICEBUY"));
                        pstmt.setDouble(7, rs.getDouble("PRICESELL"));
                        pstmt.setString(8, rs.getString("CATEGORY"));
                        pstmt.setString(9, rs.getString("TAXCAT"));
                        pstmt.setString(10, rs.getString("ATTRIBUTESET_ID"));
                        pstmt.setDouble(11, rs.getDouble("STOCKCOST"));
                        pstmt.setDouble(12, rs.getDouble("STOCKVOLUME"));
                        pstmt.setBytes(13, rs.getBytes("IMAGE"));
                        pstmt.setBoolean(14, rs.getBoolean("ISCOM"));
                        pstmt.setBoolean(15, rs.getBoolean("ISSCALE"));
                        pstmt.setBoolean(16, rs.getBoolean("ISKITCHEN"));
                        pstmt.setBoolean(17, rs.getBoolean("PRINTKB"));
                        pstmt.setBoolean(18, rs.getBoolean("SENDSTATUS"));
                        pstmt.setBoolean(19, rs.getBoolean("ISSERVICE"));
                        pstmt.setBytes(21, rs.getBytes("ATTRIBUTES"));
                        pstmt.setString(20, rs.getString("DISPLAY"));
                        pstmt.setBoolean(22, rs.getBoolean("ISVPRICE"));
                        pstmt.setBoolean(23, rs.getBoolean("ISVERPATRIB"));
                        pstmt.setString(24, rs.getString("TEXTTIP"));
                        pstmt.setBoolean(25, rs.getBoolean("WARRANTY"));
// JG Aug 2014 for 3.80 from 3.70
                        pstmt.setDouble(26, rs.getDouble("STOCKUNITS"));                        
// Dis-Allow Product Control account
                    if (!"xxx999_999xxx_x9x9x9".equals(rs.getString(1))) {
                        pstmt.executeUpdate();
                    }
                }

// copy PRODUCTS_CAT table       
                    SQL = "SELECT * FROM products_cat";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO products_cat(PRODUCT, CATORDER) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("PRODUCT"));
                        pstmt.setInt(2, rs.getInt("CATORDER"));
// JG Aug 2014 for 3.80
// Dis-Allow Product Control account
                        if (!"xxx999_999xxx_x9x9x9".equals(rs.getString(1))) {
                            pstmt.executeUpdate();
                        }
                    }

// copy PRODUCTS_COM table       
                    SQL = "SELECT * FROM products_com";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO products_com(ID, PRODUCT, PRODUCT2 ) "
                                + "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("PRODUCT"));
                        pstmt.setString(3, rs.getString("PRODUCT2"));
                        pstmt.executeUpdate();
                    }

// copy RECEIPTS table       
                    SQL = "SELECT * FROM receipts";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO receipts(ID, MONEY, DATENEW, " +
                                "ATTRIBUTES, PERSON ) " +
                                "VALUES (?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("MONEY"));
                        pstmt.setTimestamp(3, rs.getTimestamp("DATENEW"));
                        pstmt.setBytes(4, rs.getBytes("ATTRIBUTES"));
                        pstmt.setString(5, rs.getString("PERSON"));
                        pstmt.executeUpdate();
                    }

// copy reservation_customers table       
                    SQL = "SELECT * FROM reservation_customers";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO reservation_customers(ID, CUSTOMER) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("CUSTOMER"));
                        pstmt.executeUpdate();
                    }

                    // copy reservationS table       
                    SQL = "SELECT * FROM reservations";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO reservations(ID, CREATED, DATENEW, " +
                                "TITLE, CHAIRS, ISDONE, DESCRIPTION ) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setTimestamp(2, rs.getTimestamp("CREATED"));
                        pstmt.setTimestamp(3, rs.getTimestamp("DATENEW"));
                        pstmt.setString(4, rs.getString("TITLE"));
                        pstmt.setInt(5, rs.getInt("CHAIRS"));
                        pstmt.setBoolean(6, rs.getBoolean("ISDONE"));
                        pstmt.setString(7, rs.getString("DESCRIPTION"));
                        pstmt.executeUpdate();
                    }


// copy resources table       
                    SQL = "SELECT * FROM resources";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO resources(ID, NAME, RESTYPE, CONTENT) " +
                                "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setInt(3, rs.getInt("RESTYPE"));
                        pstmt.setBytes(4, rs.getBytes("CONTENT"));
                        pstmt.executeUpdate();
                    }

// copy ROLES table       
                    SQL = "SELECT * FROM roles";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO roles(ID, NAME, PERMISSIONS ) " +
                                "VALUES (?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setBytes(3, rs.getBytes("PERMISSIONS"));
                        pstmt.executeUpdate();
                    }


// copy SHAREDTICKETS table       
                    SQL = "SELECT * FROM sharedtickets";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO sharedtickets(ID, NAME, CONTENT, " +
                                "APPUSER, PICKUPID ) " +
                                "VALUES (?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setBytes(3, rs.getBytes("CONTENT"));
                        pstmt.setBytes(4, rs.getBytes("APPUSER"));
                        pstmt.setInt(5, rs.getInt("PICKUPID"));
                        pstmt.executeUpdate();
                    }


// copy SHIFT_BREAKS table       
                    SQL = "SELECT * FROM shift_breaks";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO shift_breaks(ID, SHIFTID, BREAKID, " +
                                "STARTTIME, ENDTIME ) " +
                                "VALUES (?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("SHIFTID"));
                        pstmt.setString(3, rs.getString("BREAKID"));
                        pstmt.setTimestamp(4, rs.getTimestamp("STARTTIME"));
                        pstmt.setTimestamp(5, rs.getTimestamp("ENDTIME"));
                        pstmt.executeUpdate();
                    }

// copy SHIFTS table       
                    SQL = "SELECT * FROM shifts";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO shifts(ID, STARTSHIFT, ENDSHIFT, PPLID ) " +
                                "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setTimestamp(2, rs.getTimestamp("STARTSHIFT"));
                        pstmt.setTimestamp(3, rs.getTimestamp("ENDSHIFT"));
                        pstmt.setString(4, rs.getString("PPLID"));
                        pstmt.executeUpdate();
                    }


// copy STOCKCURRENT table       
                    SQL = "SELECT * FROM stockcurrent";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO stockcurrent(LOCATION, PRODUCT, "
                                + "ATTRIBUTESETINSTANCE_ID, UNITS ) "
                                + "VALUES (?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("LOCATION"));
                        pstmt.setString(2, rs.getString("PRODUCT"));
                        pstmt.setString(3, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        pstmt.setDouble(4, rs.getDouble("UNITS"));
                        pstmt.executeUpdate();
                    }


// copy STOCKDIARY table       
                    SQL = "SELECT * FROM stockdiary";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO stockdiary(ID, DATENEW, REASON, LOCATION, "
                                + " PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, APPUSER ) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setTimestamp(2, rs.getTimestamp("DATENEW"));
                        pstmt.setInt(3, rs.getInt("REASON"));
                        pstmt.setString(4, rs.getString("LOCATION"));
                        pstmt.setString(5, rs.getString("PRODUCT"));
                        pstmt.setString(6, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        pstmt.setDouble(7, rs.getDouble("UNITS"));
                        pstmt.setDouble(8, rs.getDouble("PRICE"));
                        pstmt.setString(9, rs.getString("APPUSER"));
                        pstmt.executeUpdate();
                    }


// copy STOCKLEVEL table       
                    SQL = "SELECT * FROM stocklevel";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO stocklevel(ID, LOCATION, PRODUCT, "
                                + "STOCKSECURITY, STOCKMAXIMUM ) "
                                + "VALUES (?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("LOCATION"));
                        pstmt.setString(3, rs.getString("PRODUCT"));
                        pstmt.setDouble(4, rs.getDouble("STOCKSECURITY"));
                        pstmt.setDouble(5, rs.getDouble("STOCKMAXIMUM"));
                        pstmt.executeUpdate();
                    }

// copy TAXCATEGORIES table       
                    SQL = "SELECT * FROM taxcategories";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO taxcategories(ID, NAME) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.executeUpdate();
                    }


// copy TAXCUSTCATEGORIES table       
                    SQL = "SELECT * FROM taxcustcategories";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO taxcustcategories(ID, NAME) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.executeUpdate();
                    }



// copy TAXES table       
                    SQL = "SELECT * FROM taxes";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO taxes(ID, NAME, CATEGORY, CUSTCATEGORY, "
                                + "PARENTID, RATE, RATECASCADE, RATEORDER ) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setString(3, rs.getString("CATEGORY"));
                        pstmt.setString(4, rs.getString("CUSTCATEGORY"));
                        pstmt.setString(5, rs.getString("PARENTID"));
                        pstmt.setDouble(6, rs.getDouble("RATE"));
                        pstmt.setBoolean(7, rs.getBoolean("RATECASCADE"));
                        pstmt.setInt(8, rs.getInt("RATEORDER"));
                        pstmt.executeUpdate();
                    }


// copy TAXLINES table       
                    SQL = "SELECT * FROM taxlines";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO taxlines(ID, RECEIPT, TAXID, BASE, AMOUNT ) "
                                + "VALUES (?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("RECEIPT"));
                        pstmt.setString(3, rs.getString("TAXID"));
                        pstmt.setDouble(4, rs.getDouble("BASE"));
                        pstmt.setDouble(5, rs.getDouble("AMOUNT"));
                        pstmt.executeUpdate();
                    }


// copy THIRDPARTIES table       
                    SQL = "SELECT * FROM thirdparties";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO thirdparties(ID, CIF, NAME, ADDRESS, "
                                + "CONTACTCOMM, CONTACTFACT, PAYRULE, FAXNUMBER, "
                                + "PHONENUMBER, MOBILENUMBER, EMAIL, "
                                + "WEBPAGE, NOTES  ) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("CIF"));
                        pstmt.setString(3, rs.getString("NAME"));
                        pstmt.setString(4, rs.getString("ADDRESS"));
                        pstmt.setString(5, rs.getString("CONTACTCOMM"));
                        pstmt.setString(6, rs.getString("CONTACTFACT"));
                        pstmt.setString(7, rs.getString("PAYRULE"));
                        pstmt.setString(8, rs.getString("FAXNUMBER"));
                        pstmt.setString(9, rs.getString("PHONENUMBER"));
                        pstmt.setString(10, rs.getString("MOBILENUMBER"));
                        pstmt.setString(11, rs.getString("EMAIL"));
                        pstmt.setString(12, rs.getString("WEBPAGE"));
                        pstmt.setString(13, rs.getString("NOTES"));
                        pstmt.executeUpdate();
                    }

// copy TICKETLINES table       
                    SQL = "SELECT * FROM ticketlines";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO ticketlines(TICKET, LINE, PRODUCT, "
                                + "ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, "
                                + "ATTRIBUTES ) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("TICKET"));
                        pstmt.setInt(2, rs.getInt("LINE"));
                        pstmt.setString(3, rs.getString("PRODUCT"));
                        pstmt.setString(4, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                        pstmt.setDouble(5, rs.getDouble("UNITS"));
                        pstmt.setDouble(6, rs.getDouble("PRICE"));
                        pstmt.setString(7, rs.getString("TAXID"));
                        pstmt.setBytes(8, rs.getBytes("ATTRIBUTES"));
                        pstmt.executeUpdate();
                    }


// copy TICKETS table       
                    SQL = "SELECT * FROM tickets";
                    rs = stmt_source.executeQuery(SQL);
                    while (rs.next()) {
                        SQL = "INSERT INTO tickets(ID, TICKETTYPE, TICKETID, "
                                + "PERSON, CUSTOMER, STATUS ) "
                                + "VALUES (?, ?, ?, ?, ?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setInt(2, rs.getInt("TICKETTYPE"));
                        pstmt.setInt(3, rs.getInt("TICKETID"));
                        pstmt.setString(4, rs.getString("PERSON"));
                        pstmt.setString(5, rs.getString("CUSTOMER"));
                        pstmt.setInt(6, rs.getInt("STATUS"));
                        pstmt.executeUpdate();
                    }


// GET THE SEQUENCE NUMBERS
                        SQL = "SELECT * FROM ticketsnum";
                        rs = stmt_source.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnum = rs.getString("ID");
                        }
                        SQL = "SELECT * FROM ticketsnum_payment";
                        rs = stmt_source.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumPayment = rs.getString("ID");
                        }
                        SQL = "SELECT * FROM ticketsnum_refund";
                        rs = stmt_source.executeQuery(SQL);
                        while (rs.next()) {
                            ticketsnumRefund = rs.getString("ID");
                        }

// WRITE SEQUENCE NUMBER
                        SQL = "UPDATE ticketsnum SET ID=" + ticketsnum;
                        stmt_target.executeUpdate(SQL);
                        SQL = "UPDATE ticketsnum__payment SET ID=" + ticketsnumPayment;
                        stmt_target.executeUpdate(SQL);
                        SQL = "UPDATE ticketsnum_refund SET ID=" + ticketsnumRefund;
                        stmt_target.executeUpdate(SQL);

// Add foreign keys back into the datbase
                    addFKeys();

// Write new database settings to properties file

                    if ("MySQL".equals(sDB_target)) {
                        config.setProperty("db.engine", "MySQL");
                    }else{
                        config.setProperty("db.engine", "PostgreSQL");
                    }
                  
                    config.setProperty("db.driverlib", txtDBDriverLib.getText());
                    config.setProperty("db.driver", txtDBDriver.getText());
                    config.setProperty("db.URL", txtDBURL.getText());
                    config.setProperty("db.user", txtDBUser.getText());
                    AltEncrypter cypher = new AltEncrypter("cypherkey" + txtDBUser.getText());
                    config.setProperty("db.password", "crypt:" 
                            + cypher.encrypt(new String(txtDBPass.getPassword())));
                    dirty.setDirty(false);


                    for (PanelConfig c : m_panelconfig) {
                        c.saveProperties(config);
                    }

                    try {
                        config.save();
                        JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.restartchanges"), 
                                AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("message.cannotsaveconfig"), e));
                    }


                    JOptionPane.showMessageDialog(this, "Migration complete.");
                    jbtnMigrate.setEnabled(false);


                } catch (SQLException | HeadlessException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, SQL, e));
                }
            } else {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, AppLocal.getIntString("message.migratenotsupported"), AppLocal.getIntString("message.migratemessage"), JOptionPane.WARNING_MESSAGE);

            }
        }
    }//GEN-LAST:event_jbtnMigrateActionPerformed

    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
        deactivate();
        System.exit(0);
    }//GEN-LAST:event_jbtnExitActionPerformed

    private void jButtonTestjButtonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestjButtonTestConnectionActionPerformed
        try {
            String driverlib = txtDBDriverLib.getText();
            String driver = txtDBDriver.getText();
            String url = txtDBURL.getText();
            String user = txtDBUser.getText();
            String password = new String(txtDBPass.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session_source = new Session(url, user, password);
            Connection connection = session_source.getConnection();
            boolean isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databaseconnectsuccess"), 
                        "Connection Test", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException 
                | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, 
                    AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, 
                    AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (Exception e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jButtonTestjButtonTestConnectionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.combobox.WebComboBox cbTarget;
    private javax.swing.JButton jButtonTest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnMigrate;
    private com.alee.laf.text.WebTextField txtDBDriver;
    private com.alee.laf.text.WebTextField txtDBDriverLib;
    private com.alee.laf.text.WebPasswordField txtDBPass;
    private com.alee.laf.text.WebTextField txtDBURL;
    private com.alee.laf.text.WebTextField txtDBUser;
    private com.alee.laf.panel.WebPanel webPanel1;
    private com.alee.laf.panel.WebPanel webPanel2;
    // End of variables declaration//GEN-END:variables
}
