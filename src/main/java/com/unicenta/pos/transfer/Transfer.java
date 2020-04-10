//    uniCenta oPOS  - Touch Friendly Point Of Sale
//    Copyright (c) 2009-2016 uniCenta
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



import com.openbravo.data.loader.BatchSentence;
import com.openbravo.data.loader.BatchSentenceResource;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.config.PanelConfig;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.util.AltEncrypter;
import com.openbravo.pos.util.DirectoryEvent;

import java.awt.HeadlessException;
import java.io.File;
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
import java.util.Properties;
import java.util.regex.Matcher;
import javax.swing.*;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessageDialog;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.user.DirtyManager;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;


/**
 *
 * @author JG uniCenta
 */
public final class Transfer extends JPanel implements JPanelView {

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
    private PreparedStatement pstmt;
    private String SQL;
    
    private String source_version;

    private String ticketsnum;
    private String ticketsnumRefund;
    private String ticketsnumPayment;

    private String targetCreate = "";
    private String targetFKadd = "";
    private String targetFKdrop = "";    
    
    ArrayList<String> stringList = new ArrayList<>();
    
    public String strOut = "";
        

    /**
     * Creates new form JPaneldbTransfer
     * @param oApp
     */
    public Transfer(AppView oApp) {
        this(oApp.getProperties());
    }

    /**
     *
     * @param props
     */
    public Transfer(AppProperties props) {

        initComponents();

        config = new AppConfig(props.getConfigFile());
        m_props = props;
        m_panelconfig = new ArrayList<>();
        config.load();
        
        m_panelconfig.stream().forEach((c) -> {
            c.loadProperties(config);
        });

        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        txtDbPass.getDocument().addDocumentListener(dirty);
        txtDbUser.getDocument().addDocumentListener(dirty);
        txtOut.getDocument().addDocumentListener(dirty);
        
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));

        cbSource.addActionListener(dirty);
        
        cbSource.addItem("Derby");
        cbSource.addItem("MySQL");
        cbSource.addItem("PostgreSQL");
        
        stringList.add("Transfer Ready..." + "\n");
        txtOut.setText(stringList.get(0));

        webPBar.setIndeterminate ( true );
        webPBar.setStringPainted ( true );
        webPBar.setString ( "Waiting..." );
        webPBar.setVisible(false);
        
        jbtnTransfer.setEnabled(false);
        jbtnExit.setVisible(false);
       
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
        return AppLocal.getIntString("Menu.Transfer");
    }

    /**
     *
     * @return
     */
    public Boolean getSource() {

        String db_user2 = txtDbUser.getText();
        String db_url2 = jtxtDbURL.getText();
        char[] pass = txtDbPass.getPassword();
        String db_password2 = new String(pass);

        Properties connectionProps = new Properties();
        connectionProps.put("user", db_user2);
        connectionProps.put("password", db_password2);
        
        try {
            Class.forName(jtxtDbDriver.getText());

            ClassLoader cloader = new URLClassLoader(
                    new URL[]{
                        new File(jtxtDbDriverLib.getText()).toURI().toURL()
                    });
            DriverManager.registerDriver(
                    new DriverWrapper((Driver) Class.forName(jtxtDbDriver.getText(), 
                            true, cloader).newInstance()));
            con_source = (Connection) DriverManager.getConnection(
                    db_url2, db_user2, db_password2);

            session_source = new Session(db_url2, db_user2, db_password2);
            sDB_source = con_source.getMetaData().getDatabaseProductName();          

            txtOut.append("Connected to Source OK" + "\n");
            jbtnTransfer.setEnabled(true);            
            
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
     * @return
     */
    @SuppressWarnings("empty-statement")
    public Boolean createTargetDB() {
// Transfer is into current MySQL database in unicentaopos.properties 
        
        targetCreate = "/com/openbravo/pos/scripts/" + sDB_target + "-create-transfer.sql";
        targetFKadd = "/com/openbravo/pos/scripts/MySQL-FKeys.sql";
        targetFKdrop = "/com/openbravo/pos/scripts/MySQL-dropFKeys.sql";
        
        if ("".equals(targetCreate)) {
            return (false);
        }

        try {
            BatchSentence bsentence = new BatchSentenceResource(session_target, targetCreate);
            bsentence = new BatchSentenceResource(session_target, targetFKdrop);
            
            bsentence.putParameter("APP_ID", Matcher.quoteReplacement(AppLocal.APP_ID));
            bsentence.putParameter("APP_NAME", Matcher.quoteReplacement(AppLocal.APP_NAME));
            bsentence.putParameter("APP_VERSION", Matcher.quoteReplacement(AppLocal.APP_VERSION));

            java.util.List l = bsentence.list();
            
            if (l.size() > 0) {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("transfer.warning"), 
                                l.toArray(new Throwable[l.size()])));
            } else {
 
                txtOut.append("Connected to Target OK"  + "\n");
                txtOut.revalidate();
                txtOut.repaint();                 
            }

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("transfer.warningnodefault"), 
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
    public Boolean FKeys() {
        if ("".equals(targetFKadd)) {
            return (false);
        }
        try {
            txtOut.append("Adding Foreign Keys" + "\n");
            webPBar.setString("Adding Keys...");
                    
            BatchSentence bsentence = new BatchSentenceResource(session_target, targetFKadd);
            
            java.util.List l = bsentence.list();
            if (l.size() > 0) {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, 
                                AppLocal.getIntString("transfer.warning"), 
                                l.toArray(new Throwable[l.size()])));                   
                txtOut.append("Foreign Key error" + "\n");
            } else {
                txtOut.append("Foreign Keys completed" + "\n");               
            }

        } catch (BasicException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("database.ScriptNotFound"), 
                            e));
            session_source.close();
        } finally {
        }
        txtOut.revalidate();
        txtOut.repaint();
        return (true);

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
            session_target = AppViewConnection.createSession(m_props);            
            con_target  = DriverManager.getConnection(db_url, db_user, db_password);
            sDB_target = con_target.getMetaData().getDatabaseProductName();
            jlblSource.setText(con_target.getCatalog());            
            
        } catch (BasicException | SQLException e) {
            JMessageDialog.showMessage(this, 
                    new MessageInf(MessageInf.SGN_DANGER, 
                            AppLocal.getIntString("database.UnableToConnect"), 
                            e));
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
     *
     * @throws java.sql.SQLException
     */
    public void clearData() throws SQLException {
        Statement stmt = null;
        try {
            this.con_target.setAutoCommit(false);
            stmt = this.con_target.createStatement();

            stmt.addBatch("DELETE FROM attributesetinstance;");
            stmt.addBatch("DELETE FROM attributeinstance;");
            stmt.addBatch("DELETE FROM attributevalue");
            stmt.addBatch("DELETE FROM attributeuse");
            stmt.addBatch("DELETE FROM attributeset");
            stmt.addBatch("DELETE FROM attribute"); 
            stmt.addBatch("DELETE FROM people;");
            stmt.addBatch("DELETE FROM categories;");
            stmt.addBatch("DELETE FROM taxcategories;");
            stmt.addBatch("DELETE FROM taxcustcategories;");
            stmt.addBatch("DELETE FROM taxes;");
            stmt.addBatch("DELETE FROM locations;");
            stmt.addBatch("DELETE FROM floors;");
            stmt.addBatch("DELETE FROM places;");
            stmt.addBatch("DELETE FROM shifts;");
            stmt.addBatch("DELETE FROM breaks;");
            stmt.addBatch("DELETE FROM shift_breaks;");
            stmt.addBatch("DELETE FROM closedcash;");
            stmt.addBatch("DELETE FROM csvimport;");
            stmt.addBatch("DELETE FROM customers;");
            stmt.addBatch("DELETE FROM draweropened;");
            stmt.addBatch("DELETE FROM leaves;");
            stmt.addBatch("DELETE FROM lineremoved;");
            stmt.addBatch("DELETE FROM payments;");
            stmt.addBatch("DELETE FROM products_cat;");
            stmt.addBatch("DELETE FROM products_com;");
            stmt.addBatch("DELETE FROM products;");
            stmt.addBatch("DELETE FROM receipts;");
            stmt.addBatch("DELETE FROM roles WHERE NOT ID = '0';");
            stmt.addBatch("DELETE FROM stockcurrent;");
            stmt.addBatch("DELETE FROM stockdiary;");
            stmt.addBatch("DELETE FROM stocklevel;");
            stmt.addBatch("DELETE FROM suppliers;");
            stmt.addBatch("DELETE FROM uom;");
            stmt.addBatch("DELETE FROM vouchers;");        
        
            int [] updateCounts = stmt.executeBatch();
            this.con_target.commit();

        } catch(BatchUpdateException b) {

        } catch(SQLException ex) {

        } finally {
            if (stmt != null) { 
                stmt.close(); 
            }
            this.con_target.setAutoCommit(true);
        }
    }
    
    public void doTransfer() {

        webPBar.setString ( "Starting..." );
        webPBar.setVisible(true);
         
        String Dbtname = "";
        
        if (getSource()) {          
                    txtOut.setVisible(true);
                    txtOut.append("Transfer Started..." + "\n");
                    
            if (createTargetDB()) {
                    jbtnTransfer.setEnabled(false);
                    
                try {
                    stmt_source = (Statement) con_source.createStatement();
                    stmt_target = (Statement) con_target.createStatement();

                    clearData();        

                    this.con_target.setAutoCommit(false);      

                    webPBar.setString ( "Running..." );
                    
                    Dbtname="attribute";
                    ResultSet rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attribute" + "\n");                  
                        SQL = "SELECT * FROM attribute";
                        rs = stmt_source.executeQuery(SQL); 
                        
                        while (rs.next()) {
                            SQL = "INSERT INTO attribute ("
                                + "ID, NAME) "
                                + "VALUES (?, ?)";
                
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                
                            pstmt.executeUpdate();                 
                        }


                    Dbtname="attributeinstance";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attributeinstance" + "\n");                  
                        SQL = "SELECT * FROM attributeinstance";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO attributeinstance ("
                                + "ID, ATTRIBUTESETINSTANCE_ID, ATTRIBUTE_ID, VALUE) "
                                + "VALUES (?, ?, ?, ?)";

                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                            pstmt.setString(3, rs.getString("ATTRIBUTE_ID"));
                            pstmt.setString(4, rs.getString("VALUE"));

                            pstmt.executeUpdate();
                        }
      
                    Dbtname="attributeset";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attributeset" + "\n");                  
                        SQL = "SELECT * FROM attributeset";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO attributeset ("
                                + "ID, NAME) "
                                + "VALUES (?, ?)";
                        
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                        
                            pstmt.executeUpdate();
                        }    

                    Dbtname="attributesetinstance";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attributesetinstance" + "\n");                  
                        SQL = "SELECT * FROM attributesetinstance";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO attributesetinstance ("
                                + "ID, ATTRIBUTESET_ID, DESCRIPTION) "
                                + "VALUES (?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("ATTRIBUTESET_ID"));
                            pstmt.setString(3, rs.getString("DESCRIPTION"));
                        
                            pstmt.executeUpdate();
                        }

                    Dbtname="attributeuse";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attributeuse" + "\n");
                        SQL = "SELECT * FROM attributeuse";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO attributeuse("
                                + "ID, ATTRIBUTESET_ID, ATTRIBUTE_ID, LINENO) "
                                + "VALUES (?, ?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("ATTRIBUTESET_ID"));
                            pstmt.setString(3, rs.getString("ATTRIBUTE_ID"));
                            pstmt.setInt(4, rs.getInt("LINENO"));
                        
                            pstmt.executeUpdate();
                        }
                    
                    Dbtname="attributevalue";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Attributevalue" + "\n");
                        SQL = "SELECT * FROM attributevalue";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO attributevalue ("
                                + "ID, ATTRIBUTE_ID, VALUE) "
                                + "VALUES (?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("ATTRIBUTE_ID"));
                            pstmt.setString(3, rs.getString("VALUE"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="breaks";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Breaks" + "\n");
                        SQL = "SELECT * FROM breaks";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO breaks("
                                + "ID, NAME, VISIBLE, NOTES) "
                                + "VALUES (?, ?, ?, ?)";
                
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setString(3, rs.getString("VISIBLE"));
                            pstmt.setBoolean(4, rs.getBoolean("NOTES"));

                            pstmt.executeUpdate();                       
                        }   
                    
                    Dbtname="categories";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Categories" + "\n");
                        SQL = "SELECT * FROM categories";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {

                            SQL = "INSERT INTO categories("
                                + "ID, NAME, PARENTID, IMAGE, "
                                + "TEXTTIP, CATSHOWNAME) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?)";

                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setString(3, rs.getString("PARENTID"));
                            pstmt.setBytes(4, rs.getBytes("IMAGE"));
                            pstmt.setString(5, rs.getString("TEXTTIP"));
                            pstmt.setString(6, rs.getString("CATSHOWNAME"));                         
                        
                            pstmt.executeUpdate();
                        }
                    
                    Dbtname="closedcash";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("ClosedCash" + "\n");
                        SQL = "DELETE FROM closedcash";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.executeUpdate();
                        
                        SQL = "SELECT * FROM closedcash";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO closedcash("
                                + "MONEY, HOST, HOSTSEQUENCE, "
                                + "DATESTART, DATEEND, NOSALES) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?, ?)";
    
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("MONEY"));
                            pstmt.setString(2, rs.getString("HOST"));
                            pstmt.setInt(3, rs.getInt("HOSTSEQUENCE"));                        
                            pstmt.setTimestamp(4, rs.getTimestamp("DATESTART"));                                               
                            pstmt.setTimestamp(5, rs.getTimestamp("DATEEND"));
                            pstmt.setInt(6, rs.getInt("NOSALES"));                        
                        
                            pstmt.executeUpdate();
                        }    

                    Dbtname="customers";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Customers" + "\n");
                        SQL = "SELECT * FROM customers";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO customers("
                                + "ID, SEARCHKEY, TAXID, NAME, "
                                + "TAXCATEGORY, CARD, MAXDEBT,"
                                + "ADDRESS, ADDRESS2, POSTAL, CITY, "
                                + "REGION, COUNTRY, FIRSTNAME, LASTNAME, "
                                + "EMAIL, PHONE, PHONE2, FAX, "
                                + "NOTES, VISIBLE, CURDATE, CURDEBT, IMAGE )"
                                + " VALUES ("
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, ?)";

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
                         
                            pstmt.executeUpdate();
                        }
                    
                    Dbtname="draweropened";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("DrawerOpened" + "\n");
                        SQL = "SELECT * FROM draweropened";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO draweropened("
                                + "OPENDATE, NAME, TICKETID) "
                                + "VALUES (?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("OPENDATE"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setString(3, rs.getString("TICKETID"));
                        
                            pstmt.executeUpdate();
                        }
                    
                    Dbtname="floors";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Floors" + "\n");
                        SQL = "SELECT * FROM floors";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO floors ("
                                + "ID, NAME, IMAGE) "
                                + "VALUES (?, ?, ?)";
                        
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setBytes(3, rs.getBytes("IMAGE"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="leaves";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Leaves" + "\n");
                        SQL = "SELECT * FROM leaves";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO leaves ("
                                + "ID, PPLID, NAME, STARTDATE, "
                                + "ENDDATE, NOTES) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?)";
    
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("PPLID"));
                            pstmt.setString(3, rs.getString("NAME"));
                            pstmt.setTimestamp(4, rs.getTimestamp("STARTDATE"));
                            pstmt.setTimestamp(5, rs.getTimestamp("ENDDATE"));
                            pstmt.setString(6, rs.getString("NOTES"));
                        
                            pstmt.executeUpdate();
                        }
                    
                    Dbtname="lineremoved";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("LineRemoved" + "\n");
                        SQL = "SELECT * FROM lineremoved";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO lineremoved ("
                            + "REMOVEDDATE, NAME, TICKETID, "
                            + "PRODUCTID, PRODUCTNAME, UNITS) "
                            + "VALUES (?, ?, ?, "
                            + "?, ?, ?)";
    
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setTimestamp(1, rs.getTimestamp("REMOVEDDATE"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setString(3, rs.getString("TICKETID"));
                            pstmt.setString(4, rs.getString("PRODUCTID"));                        
                            pstmt.setString(5, rs.getString("PRODUCTNAME"));                        
                            pstmt.setDouble(6, rs.getDouble("UNITS"));                        
                        
                            pstmt.executeUpdate();
                        }                    

                    Dbtname="locations";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Locations" + "\n");
                        SQL = "SELECT * FROM locations";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO locations("
                                + "ID, NAME, ADDRESS) "
                                + "VALUES (?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                            pstmt.setString(3, rs.getString("ADDRESS"));
                        
                            pstmt.executeUpdate();
                        }     
                    
                    Dbtname="moorers";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Moorers" + "\n");
                        SQL = "SELECT * FROM moorers";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO moorers("
                                + "VESSELNAME, SIZE, DAYS, POWER) "
                                + "VALUES (?, ?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("VESSELNAME"));
                            pstmt.setInt(2, rs.getInt("SIZE"));
                            pstmt.setInt(3, rs.getInt("DAYS"));
                            pstmt.setBoolean(4, rs.getBoolean("POWER"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="payments";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Payments" + "\n");
                        SQL = "SELECT * FROM payments";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO payments("
                                + "ID, RECEIPT, PAYMENT, TOTAL, "
                                + "TRANSID, RETURNMSG, NOTES, TENDERED) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?, ?)";
                
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("RECEIPT"));
                            pstmt.setString(3, rs.getString("PAYMENT"));
                            pstmt.setDouble(4, rs.getDouble("TOTAL"));                       
                            pstmt.setString(5, rs.getString("TRANSID"));
                            pstmt.setBytes(6, rs.getBytes("RETURNMSG"));                        
                            pstmt.setString(7, rs.getString("NOTES"));
                            pstmt.setDouble(8, rs.getDouble("TENDERED"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="people";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("People" + "\n");
                        SQL = "SELECT * FROM people";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO people("
                                + "ID, NAME, APPPASSWORD, CARD, "
                                + "ROLE, VISIBLE, IMAGE) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?)";
    
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
                    
                    Dbtname="pickup_number";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Pickup Number" + "\n");
                        SQL = "SELECT * FROM pickup_number";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO pickup_number(ID) "
                                + "VALUES (?)";
                
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="places";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Places" + "\n");
                        SQL = "SELECT * FROM places";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO places ("
                                + "ID, NAME, X, Y, FLOOR, "
                                + "CUSTOMER, WAITER, TICKETID, TABLEMOVED) "
                                + "VALUES (?, ?, ?, ?, ?, "
                                + "?, ?, ?, ?)";
    
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

// Products is verbose SELECT
                    Dbtname="products";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Products" + "\n");
                        SQL = "SELECT "
                            + "ID, REFERENCE, CODE, CODETYPE, NAME, " 
                            + "PRICEBUY, PRICESELL, CATEGORY, TAXCAT, "
                            + "ATTRIBUTESET_ID, STOCKCOST, STOCKVOLUME, IMAGE, "
                            + "ISCOM, ISSCALE, PRINTKB, SENDSTATUS, "
                            + "ISSERVICE, ATTRIBUTES, DISPLAY, ISVPRICE, "
                            + "ISVERPATRIB, TEXTTIP, WARRANTY, STOCKUNITS "
                            + "FROM products ";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO products("
                                + "ID, REFERENCE, CODE, CODETYPE, NAME, " 
                                + "PRICEBUY, PRICESELL, CATEGORY, TAXCAT, "
                                + "ATTRIBUTESET_ID, STOCKCOST, STOCKVOLUME, IMAGE, "
                                + "ISCOM, ISSCALE, PRINTKB, SENDSTATUS, "
                                + "ISSERVICE, ATTRIBUTES, DISPLAY, ISVPRICE, "
                                + "ISVERPATRIB, TEXTTIP, WARRANTY, STOCKUNITS) "
                                + "VALUES (?, ?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?)";
        
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
                            pstmt.setBoolean(16, rs.getBoolean("PRINTKB"));
                            pstmt.setBoolean(17, rs.getBoolean("SENDSTATUS"));
                            pstmt.setBoolean(18, rs.getBoolean("ISSERVICE"));
                            pstmt.setBytes(19, rs.getBytes("ATTRIBUTES"));
                            pstmt.setString(20, rs.getString("DISPLAY"));
                            pstmt.setBoolean(21, rs.getBoolean("ISVPRICE"));
                            pstmt.setBoolean(22, rs.getBoolean("ISVERPATRIB"));
                            pstmt.setString(23, rs.getString("TEXTTIP"));
                            pstmt.setBoolean(24, rs.getBoolean("WARRANTY"));
                            pstmt.setDouble(25, rs.getDouble("STOCKUNITS"));                       

                            pstmt.executeUpdate();                                
                        }    
                    
                    Dbtname="products_cat";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Products_Cat" + "\n");
                        SQL = "SELECT * FROM products_cat";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO products_cat("
                                + "PRODUCT, CATORDER) "
                                + "VALUES (?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("PRODUCT"));
                            pstmt.setInt(2, rs.getInt("CATORDER"));
                        
                            pstmt.executeUpdate();                                
                        }    
                    
                    Dbtname="products_com";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Products_Com" + "\n");
                        SQL = "SELECT * FROM products_com";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO products_com("
                                + "ID, PRODUCT, PRODUCT2) "
                                + "VALUES (?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("PRODUCT"));
                            pstmt.setString(3, rs.getString("PRODUCT2"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="receipts";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Receipts" + "\n");
                        SQL = "SELECT * FROM receipts";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO receipts("
                                + "ID, MONEY, DATENEW, "
                                + "ATTRIBUTES, PERSON) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("MONEY"));
                            pstmt.setTimestamp(3, rs.getTimestamp("DATENEW"));
                            pstmt.setBytes(4, rs.getBytes("ATTRIBUTES"));
                            pstmt.setString(5, rs.getString("PERSON"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="reservation_customers";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Reservation_Customers" + "\n");
                        SQL = "SELECT * FROM reservation_customers";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO reservation_customers("
                                + "ID, CUSTOMER) "
                                + "VALUES (?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("CUSTOMER"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="reservations";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Reservations" + "\n");
                        SQL = "SELECT * FROM reservations";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO reservations("
                                + "ID, CREATED, DATENEW, TITLE, "
                                + "CHAIRS, ISDONE, DESCRIPTION) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?)";
    
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

// Need to have v4.2 else new features not accessible so ID=0 Administrator stays
                    Dbtname="roles";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Roles" + "\n");
                        SQL = "SELECT * FROM roles WHERE NOT ID='0' ";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO roles("
                            + "ID, NAME, PERMISSIONS) "
                            + "VALUES (?, ?, ?)";
            
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("ID"));
                        pstmt.setString(2, rs.getString("NAME"));
                        pstmt.setBytes(3, rs.getBytes("PERMISSIONS"));
                        
                        pstmt.executeUpdate();
                    }
                    
                    Dbtname="shift_breaks";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Shift_breaks" + "\n");
                        SQL = "SELECT * FROM shift_breaks";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO shift_breaks("
                                + "ID, SHIFTID, BREAKID, "
                                + "STARTTIME, ENDTIME) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?)";
        
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("SHIFTID"));
                            pstmt.setString(3, rs.getString("BREAKID"));
                            pstmt.setTimestamp(4, rs.getTimestamp("STARTTIME"));
                            pstmt.setTimestamp(5, rs.getTimestamp("ENDTIME"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="shifts";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Shifts" + "\n");
                        SQL = "SELECT * FROM shifts";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO shifts("
                                + "ID, STARTSHIFT, ENDSHIFT, PPLID) "
                                + "VALUES (?, ?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setTimestamp(2, rs.getTimestamp("STARTSHIFT"));
                            pstmt.setTimestamp(3, rs.getTimestamp("ENDSHIFT"));
                            pstmt.setString(4, rs.getString("PPLID"));
                        
                            pstmt.executeUpdate();
                        }    

                    Dbtname="stockcurrent";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Stockcurrent" + "\n");
                        SQL = "SELECT * FROM stockcurrent";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO stockcurrent("
                                + "LOCATION, PRODUCT, "
                                + "ATTRIBUTESETINSTANCE_ID, UNITS) "
                                + "VALUES (?, ?, "
                                + "?, ?)";
    
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("LOCATION"));
                            pstmt.setString(2, rs.getString("PRODUCT"));
                            pstmt.setString(3, rs.getString("ATTRIBUTESETINSTANCE_ID"));
                            pstmt.setDouble(4, rs.getDouble("UNITS"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="stockdiary";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Stockdiary" + "\n");
                        SQL = "SELECT * FROM stockdiary";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO stockdiary("
                                + "ID, DATENEW, REASON, LOCATION, "
                                + "PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, "
                                + "PRICE, APPUSER) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?, "
                                + "?, ?)";
        
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
                    
                    Dbtname="stocklevel";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Stocklevel" + "\n");
                        SQL = "SELECT * FROM stocklevel";
                        rs = stmt_source.executeQuery(SQL);
                        
                        while (rs.next()) {
                            SQL = "INSERT INTO stocklevel("
                                + "ID, LOCATION, PRODUCT, "
                                + "STOCKSECURITY, STOCKMAXIMUM) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?)";
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("LOCATION"));
                            pstmt.setString(3, rs.getString("PRODUCT"));
                            pstmt.setDouble(4, rs.getDouble("STOCKSECURITY"));
                            pstmt.setDouble(5, rs.getDouble("STOCKMAXIMUM"));
                            pstmt.executeUpdate();
                        }    

// v4 table
                if (jlblVersion.getText().startsWith("4")) {
                    Dbtname="suppliers";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Suppliers" + "\n");
                        SQL = "SELECT * FROM suppliers";
                        rs = stmt_source.executeQuery(SQL);
                    
                        while (rs.next()) {
                            SQL = "INSERT INTO suppliers("
                                + "ID, SEARCHKEY, TAXID, NAME, MAXDEBT,"
                                + "ADDRESS, ADDRESS2, POSTAL, CITY, "
                                + "REGION, COUNTRY, FIRSTNAME, LASTNAME, "
                                + "EMAIL, PHONE, PHONE2, FAX, "
                                + "NOTES, VISIBLE, CURDATE, CURDEBT, VATID)"
                                + " VALUES ("
                                + "?, ?, ?, ?, ?,"
                                + "?, ?, ?, ?,"
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, ?, ?)";

                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("SEARCHKEY"));
                            pstmt.setString(3, rs.getString("TAXID"));
                            pstmt.setString(4, rs.getString("NAME"));
                            pstmt.setDouble(5, rs.getDouble("MAXDEBT"));
                            pstmt.setString(6, rs.getString("ADDRESS"));
                            pstmt.setString(7, rs.getString("ADDRESS2"));
                            pstmt.setString(8, rs.getString("POSTAL"));
                            pstmt.setString(9, rs.getString("CITY"));
                            pstmt.setString(10, rs.getString("REGION"));
                            pstmt.setString(11, rs.getString("COUNTRY"));
                            pstmt.setString(12, rs.getString("FIRSTNAME"));
                            pstmt.setString(13, rs.getString("LASTNAME"));
                            pstmt.setString(14, rs.getString("EMAIL"));
                            pstmt.setString(15, rs.getString("PHONE"));
                            pstmt.setString(16, rs.getString("PHONE2"));
                            pstmt.setString(17, rs.getString("FAX"));
                            pstmt.setString(18, rs.getString("NOTES"));
                            pstmt.setBoolean(19, rs.getBoolean("VISIBLE"));
                            pstmt.setTimestamp(20, rs.getTimestamp("CURDATE"));
                            pstmt.setDouble(21, rs.getDouble("CURDEBT"));
                            pstmt.setString(22, rs.getString("VATID"));    

                            pstmt.executeUpdate();
                        }
                    } else {
                        txtOut.append("Suppliers... skipped" + "\n");                            
                    }

                    Dbtname="taxcategories";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                         txtOut.append("TaxCategories" + "\n");
                        SQL = "SELECT * FROM taxcategories";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO taxcategories("
                                + "ID, NAME) "
                                + "VALUES (?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="taxcustcategories";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Tax Customer Categories" + "\n");
                        SQL = "SELECT * FROM taxcustcategories";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO taxcustcategories("
                                + "ID, NAME) "
                                + "VALUES (?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("NAME"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="taxes";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Taxes" + "\n");
                        SQL = "SELECT * FROM taxes";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO taxes("
                                + "ID, NAME, CATEGORY, CUSTCATEGORY, "
                                + "PARENTID, RATE, RATECASCADE, RATEORDER) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?, ?)";
    
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
                    
                    Dbtname="taxlines";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("TaxLines" + "\n");
                        SQL = "SELECT * FROM taxlines";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO taxlines("
                                + "ID, RECEIPT, TAXID, BASE, AMOUNT) "
                                + "VALUES (?, ?, ?, ?, ?)";
            
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setString(2, rs.getString("RECEIPT"));
                            pstmt.setString(3, rs.getString("TAXID"));
                            pstmt.setDouble(4, rs.getDouble("BASE"));
                            pstmt.setDouble(5, rs.getDouble("AMOUNT"));
                        
                            pstmt.executeUpdate();
                        }   
                    
                    Dbtname="thirdparties";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("ThirdParties" + "\n");
                        SQL = "SELECT * FROM thirdparties";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO thirdparties("
                                + "ID, CIF, NAME, ADDRESS, "
                                + "CONTACTCOMM, CONTACTFACT, PAYRULE, FAXNUMBER, "
                                + "PHONENUMBER, MOBILENUMBER, EMAIL, "
                                + "WEBPAGE, NOTES) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?, ?, "
                                + "?, ?, ?, "
                                + "?, ?)";
                        
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
                    
                    Dbtname="ticketlines";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("TicketLines" + "\n");
                        SQL = "SELECT * FROM ticketlines";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO ticketlines("
                                + "TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, "
                                + "UNITS, PRICE, TAXID, ATTRIBUTES) "
                                + "VALUES (?, ?, ?, ?, "
                                + "?, ?, ?, ?)";
          
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
                    
                    Dbtname="tickets";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Tickets" + "\n");
                        SQL = "SELECT * FROM tickets";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            SQL = "INSERT INTO tickets("
                                + "ID, TICKETTYPE, TICKETID, "
                                + "PERSON, CUSTOMER, STATUS) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?, ?)";
    
                            pstmt = con_target.prepareStatement(SQL);
                            pstmt.setString(1, rs.getString("ID"));
                            pstmt.setInt(2, rs.getInt("TICKETTYPE"));
                            pstmt.setInt(3, rs.getInt("TICKETID"));
                            pstmt.setString(4, rs.getString("PERSON"));
                            pstmt.setString(5, rs.getString("CUSTOMER"));
                            pstmt.setInt(6, rs.getInt("STATUS"));
                        
                            pstmt.executeUpdate();
                        }    
                    
                    Dbtname="ticketsnum";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Tickets Number" + "\n");
                        SQL = "SELECT * FROM ticketsnum";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            ticketsnum = rs.getString("ID");
                        }                          
                        SQL = "UPDATE ticketsnum SET ID=" + ticketsnum;
                        stmt_target.executeUpdate(SQL);        

                    Dbtname="ticketsnum_payment";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Tickets Number Payments" + "\n");
                        SQL = "SELECT * FROM ticketsnum_payment";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            ticketsnumPayment = rs.getString("ID");
                        }                          
                        SQL = "UPDATE ticketsnum_payment SET ID=" + ticketsnumPayment;

                        stmt_target.executeUpdate(SQL);

                    Dbtname="ticketsnum_refund";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("Tickets Number Refunds" + "\n");
                        SQL = "SELECT * FROM ticketsnum_refund";
                        rs = stmt_source.executeQuery(SQL);

                        while (rs.next()) {
                            ticketsnumRefund = rs.getString("ID");
                        }                          
                        SQL = "UPDATE ticketsnum_refund SET ID=" + ticketsnumRefund;

                        stmt_target.executeUpdate(SQL);   
                    
//  v4 table
                if (jlblVersion.getText().startsWith("4")) {
                    Dbtname="uom";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);
                        txtOut.append("UOM" + "\n");
                        SQL = "SELECT * FROM uom";
                        rs = stmt_source.executeQuery(SQL);
                    
                    while (rs.next()) {
                        SQL = "INSERT INTO uom("
                                + "id, name) "
                                + "VALUES (?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("id"));
                        pstmt.setString(2, rs.getString("name"));

                        pstmt.executeUpdate();
                    }
                } else {
                    txtOut.append("UOM... skipped" + "\n");                            
                }                    

                if (jlblVersion.getText().startsWith("4")) {
                    Dbtname="vouchers";
                    rs = con_source.getMetaData().getTables(null,null,Dbtname,null);                    
                    txtOut.append("Vouchers" + "\n");                    
                    SQL = "SELECT * FROM vouchers";
                    rs = stmt_source.executeQuery(SQL);

                    while (rs.next()) {
                        SQL = "INSERT INTO vouchers("
                                + "id, voucher_number, customer, "
                                + "amount, status) "
                                + "VALUES (?, ?, ?, "
                                + "?, ?)";
                        pstmt = con_target.prepareStatement(SQL);
                        pstmt.setString(1, rs.getString("id"));
                        pstmt.setString(2, rs.getString("voucher_number"));
                        pstmt.setString(3, rs.getString("customer"));
                        pstmt.setDouble(4, rs.getDouble("amount"));
                        pstmt.setString(5, rs.getString("status"));

                        pstmt.executeUpdate();
                    } 
                } else {
                    txtOut.append("Vouchers... skipped" + "\n");                        
                }

                try {                  
                    this.con_target.setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Transfer.class.getName()).log(Level.SEVERE, null, ex);
                }
            
// Add ForeignKeys                   
                JOptionPane.showMessageDialog(this
                        , AppLocal.getIntString("message.transfercomplete")
                        , AppLocal.getIntString("message.transfermessage")
                        , JOptionPane.WARNING_MESSAGE);                    
                    
                    FKeys();
                    txtOut.append("Data Transfer Complete" + "\n");
                    
                    webPBar.setString("Finished!");
                    jbtnTransfer.setEnabled(true);

                    JOptionPane.showMessageDialog(this
                        , AppLocal.getIntString("message.indexcomplete")
                        , AppLocal.getIntString("message.transfermessage")
                        , JOptionPane.WARNING_MESSAGE);                    
                                        
                } catch (SQLException | HeadlessException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, SQL, e));
                }
            } else {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame
                        , AppLocal.getIntString("message.transfernotsupported")
                        , AppLocal.getIntString("message.transfermessage")
                        , JOptionPane.WARNING_MESSAGE);
            }

            session_source.close();
        }
    }
       
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        webPanel2 = new com.alee.laf.panel.WebPanel();
        cbSource = new com.alee.laf.combobox.WebComboBox();
        jtxtDbDriverLib = new com.alee.laf.text.WebTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jtxtDbDriver = new com.alee.laf.text.WebTextField();
        jtxtDbURL = new com.alee.laf.text.WebTextField();
        jlblVersion = new javax.swing.JLabel();
        txtDbUser = new com.alee.laf.text.WebTextField();
        txtDbPass = new com.alee.laf.text.WebPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOut = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jlblSource = new javax.swing.JLabel();
        jbtnExit = new javax.swing.JButton();
        jtxtDbName = new com.alee.laf.text.WebTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jbtnTest = new javax.swing.JButton();
        jbtnTransfer = new javax.swing.JButton();
        webPBar = new com.alee.laf.progressbar.WebProgressBar();

        setBackground(new java.awt.Color(255, 255, 255));
        setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        setPreferredSize(new java.awt.Dimension(900, 425));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel5.setText(bundle.getString("label.DbType")); // NOI18N
        jLabel5.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel5.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel5.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N
        jLabel18.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel18.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel18.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText(AppLocal.getIntString("label.DbDriver")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel1.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel1.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("URL");
        jLabel2.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel2.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel2.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("DB Version");
        jLabel7.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel7.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel7.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 102, 102));
        jLabel3.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel3.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel3.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(102, 102, 102));
        jLabel4.setText(AppLocal.getIntString("label.DbPassword")); // NOI18N
        jLabel4.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel4.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel4.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("TRANSFER TO :");
        jLabel6.setPreferredSize(new java.awt.Dimension(150, 30));
        jLabel6.setSize(new java.awt.Dimension(150, 30));

        webPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout webPanel2Layout = new javax.swing.GroupLayout(webPanel2);
        webPanel2.setLayout(webPanel2Layout);
        webPanel2Layout.setHorizontalGroup(
            webPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 270, Short.MAX_VALUE)
        );
        webPanel2Layout.setVerticalGroup(
            webPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        cbSource.setForeground(new java.awt.Color(51, 51, 51));
        cbSource.setToolTipText(bundle.getString("tooltip.transferfromdb")); // NOI18N
        cbSource.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cbSource.setPreferredSize(new java.awt.Dimension(150, 30));
        cbSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSourceActionPerformed(evt);
            }
        });

        jtxtDbDriverLib.setForeground(new java.awt.Color(51, 51, 51));
        jtxtDbDriverLib.setToolTipText(bundle.getString("tootltip.transferlib")); // NOI18N
        jtxtDbDriverLib.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriverLib.setPreferredSize(new java.awt.Dimension(300, 30));

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setToolTipText(bundle.getString("tooltip.openfile")); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(60, 30));

        jtxtDbDriver.setForeground(new java.awt.Color(51, 51, 51));
        jtxtDbDriver.setToolTipText(bundle.getString("tootltip.transferclass")); // NOI18N
        jtxtDbDriver.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbDriver.setPreferredSize(new java.awt.Dimension(300, 30));

        jtxtDbURL.setForeground(new java.awt.Color(51, 51, 51));
        jtxtDbURL.setToolTipText(bundle.getString("tootltip.transferdbname")); // NOI18N
        jtxtDbURL.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbURL.setPreferredSize(new java.awt.Dimension(300, 30));

        jlblVersion.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        jlblVersion.setForeground(new java.awt.Color(0, 204, 255));
        jlblVersion.setToolTipText(bundle.getString("tooltip.transferdbversion")); // NOI18N
        jlblVersion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 204, 255)));
        jlblVersion.setMaximumSize(new java.awt.Dimension(150, 30));
        jlblVersion.setMinimumSize(new java.awt.Dimension(150, 30));
        jlblVersion.setPreferredSize(new java.awt.Dimension(100, 30));

        txtDbUser.setForeground(new java.awt.Color(51, 51, 51));
        txtDbUser.setToolTipText(bundle.getString("tooltip.dbuser")); // NOI18N
        txtDbUser.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDbUser.setPreferredSize(new java.awt.Dimension(300, 30));

        txtDbPass.setForeground(new java.awt.Color(51, 51, 51));
        txtDbPass.setToolTipText(bundle.getString("tooltip.dbpassword")); // NOI18N
        txtDbPass.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtDbPass.setPreferredSize(new java.awt.Dimension(300, 30));

        jScrollPane1.setBorder(null);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(200, 350));

        txtOut.setColumns(20);
        txtOut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtOut.setForeground(new java.awt.Color(0, 153, 255));
        txtOut.setRows(5);
        txtOut.setToolTipText(bundle.getString("tooltip.transfertxtout")); // NOI18N
        txtOut.setBorder(null);
        jScrollPane1.setViewportView(txtOut);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 255));
        jLabel8.setText("PROGRESS");
        jLabel8.setPreferredSize(new java.awt.Dimension(150, 30));
        jLabel8.setSize(new java.awt.Dimension(150, 30));

        jlblSource.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jlblSource.setForeground(new java.awt.Color(0, 153, 255));
        jlblSource.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jlblSource.setPreferredSize(new java.awt.Dimension(150, 30));
        jlblSource.setSize(new java.awt.Dimension(150, 30));

        jbtnExit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnExit.setText(AppLocal.getIntString("Button.Exit")); // NOI18N
        jbtnExit.setToolTipText(bundle.getString("tooltip.exit")); // NOI18N
        jbtnExit.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setPreferredSize(new java.awt.Dimension(80, 45));
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        jtxtDbName.setForeground(new java.awt.Color(51, 51, 51));
        jtxtDbName.setToolTipText(bundle.getString("tooltip.dbname")); // NOI18N
        jtxtDbName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jtxtDbName.setPreferredSize(new java.awt.Dimension(300, 30));
        jtxtDbName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtxtDbNameFocusLost(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 102, 102));
        jLabel9.setText(AppLocal.getIntString("label.DbUser")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel9.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel9.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));
        jLabel10.setText(AppLocal.getIntString("label.DbName")); // NOI18N
        jLabel10.setMaximumSize(new java.awt.Dimension(150, 30));
        jLabel10.setMinimumSize(new java.awt.Dimension(150, 30));
        jLabel10.setPreferredSize(new java.awt.Dimension(160, 30));

        jbtnTest.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jbtnTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/database.png"))); // NOI18N
        jbtnTest.setText(bundle.getString("Button.Test")); // NOI18N
        jbtnTest.setToolTipText(bundle.getString("tooltip.dbtest")); // NOI18N
        jbtnTest.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jbtnTest.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTestjButtonTestConnectionActionPerformed(evt);
            }
        });

        jbtnTransfer.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jbtnTransfer.setText(AppLocal.getIntString("button.transfer")); // NOI18N
        jbtnTransfer.setToolTipText(bundle.getString("tooltip.transferdb")); // NOI18N
        jbtnTransfer.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnTransfer.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnTransfer.setPreferredSize(new java.awt.Dimension(160, 45));
        jbtnTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnTransferActionPerformed(evt);
            }
        });

        webPBar.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        webPBar.setPreferredSize(new java.awt.Dimension(240, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jlblSource, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, 0, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtDbName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jbtnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDbPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDbUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jlblVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(webPBar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jtxtDbDriver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbURL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(webPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlblSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(webPBar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDbPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jlblVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(webPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.getAccessibleContext().setAccessibleName("DBDriver");
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTransferActionPerformed
// csvimport & sharedtickets are transient tables and so not transferred
// v4 tables and later are not transferred
        if (JOptionPane.showConfirmDialog(this, 
                AppLocal.getIntString("message.transfer"), 
                AppLocal.getIntString("message.transfertitle"), 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {          

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            
                @Override
                protected Void doInBackground() throws Exception {
                    webPBar.setString ( "Starting..." );                
                    doTransfer();       
                    return null;
                }
                
            };
            
            worker.execute();
            
        }

    }//GEN-LAST:event_jbtnTransferActionPerformed

    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed

        session_source.close();

        deactivate();
        
    }//GEN-LAST:event_jbtnExitActionPerformed

    private void jbtnTestjButtonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnTestjButtonTestConnectionActionPerformed

        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = txtDbUser.getText();
            String password = new String(txtDbPass.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[] {
                new File(driverlib).toURI().toURL()
            });
            
            DriverManager.registerDriver(
                    new DriverWrapper((Driver) 
                            Class.forName(driver, true, cloader).newInstance()));

            Session session_source = new Session(url, user, password);
            Connection connection = session_source.getConnection();
            boolean isValid = (connection == null) 
                    ? false : connection.isValid(1000);

            if (isValid) {
                SQL = "SELECT * FROM applications";
                Statement stmt = (Statement) connection.createStatement();
                rs = stmt.executeQuery(SQL);            
                rs.next();
                jlblVersion.setText(rs.getString(3));
                
                stringList.add("Version check... " + rs.getString(3) + "\n");
                txtOut.setText(stringList.get(1));                
                
                JOptionPane.showMessageDialog(this, 
                        AppLocal.getIntString("message.databaseconnectsuccess"), 
                        "Connection Test"
                        , JOptionPane.INFORMATION_MESSAGE);
            
                jbtnTransfer.setEnabled(true);
        
            } else {
                JMessageDialog.showMessage(this, 
                        new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException 
                | IllegalAccessException 
                | MalformedURLException 
                | ClassNotFoundException e) {
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
    }//GEN-LAST:event_jbtnTestjButtonTestConnectionActionPerformed

    private void cbSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSourceActionPerformed
        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
                  
        if ("Derby".equals(cbSource.getSelectedItem())) {
            jtxtDbName.setText("unicentaopos-database");
            jtxtDbDriverLib.setText(new File(new File(dirname)
                    , "/lib/derby-10.10.2.0.jar").getAbsolutePath());
            jtxtDbDriver.setText("org.apache.derby.jdbc.EmbeddedDriver");
            jtxtDbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home"))
                    , "unicentaopos-database"));
            txtDbUser.setText("");
            txtDbPass.setText("");           
        } else if ("PostgreSQL".equals(cbSource.getSelectedItem())) {
            jtxtDbName.setText("unicentaopos");
            jtxtDbDriverLib.setText(new File(new File(dirname)
                    , "/lib/postgresql-9.4-1208.jdbc4.jar").getAbsolutePath());
            jtxtDbDriver.setText("org.postgresql.Driver");
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/unicentaopos");            
        } else {
            jtxtDbName.setText("unicentaopos");
            jtxtDbDriverLib.setText(new File(new File(dirname)
                , "/lib/mysql-connector-java-5.1.39.jar").getAbsolutePath());
    
            jtxtDbDriver.setText("com.mysql.jdbc.Driver");
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/unicentaopos");
        }
    }//GEN-LAST:event_cbSourceActionPerformed

    private void jtxtDbNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtxtDbNameFocusLost
        jtxtDbURL.setText(jtxtDbName.getText());
        String dirname = System.getProperty("dirname.path");
                  
        if ("Derby".equals(cbSource.getSelectedItem())) {
            jtxtDbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home"))
                ,jtxtDbName.getText()));

        } else if ("PostgreSQL".equals(cbSource.getSelectedItem())) {
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/"
                + jtxtDbName.getText());            
        } else {
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/"
                + jtxtDbName.getText()); 
        }        
        
    }//GEN-LAST:event_jtxtDbNameFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.alee.laf.combobox.WebComboBox cbSource;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnTest;
    private javax.swing.JButton jbtnTransfer;
    private javax.swing.JLabel jlblSource;
    private javax.swing.JLabel jlblVersion;
    private com.alee.laf.text.WebTextField jtxtDbDriver;
    private com.alee.laf.text.WebTextField jtxtDbDriverLib;
    private com.alee.laf.text.WebTextField jtxtDbName;
    private com.alee.laf.text.WebTextField jtxtDbURL;
    private com.alee.laf.text.WebPasswordField txtDbPass;
    private com.alee.laf.text.WebTextField txtDbUser;
    private javax.swing.JTextArea txtOut;
    private com.alee.laf.progressbar.WebProgressBar webPBar;
    private com.alee.laf.panel.WebPanel webPanel2;
    // End of variables declaration//GEN-END:variables

/*    private int checkTableExist(String DBschema, String DBtname) throws SQLException {

        String SQLs = "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE " + DBschema + "AND table_name " + DBtname + ";"; 
        ResultSet rs = stmt_source.executeQuery(SQLs);
        return rs.getFetchSize();
    }
*/
}