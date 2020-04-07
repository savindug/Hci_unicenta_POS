/*
 * Copyright (C) 2016 uniCenta <info at unicenta.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.unicenta.pos.util;

import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.DataResultSet;
import com.openbravo.data.loader.SerializerReadInteger;
import com.openbravo.data.loader.Session;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.panels.PaymentsModel;
import com.openbravo.pos.util.AltEncrypter;
import java.util.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 *
 * @author uniCenta <info at unicenta.com>
 */
public class ScriptletUtil {
 
    private Session session;
 
    public String printCashEarnings(Date datestart, Date dateend) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String sentSql =
            "SELECT avg(ve.to) as media_total_ticket,  " +
                "AVG(ve.pb) as media_total_precio_compra_producto,  " +
                "AVG(ve.ps) as media_total_precio_venta_producto,  " +
                "AVG(case ve.pb when null then null when 0 then null else " +
                "((ve.to / ve.pb) * 100) end) as media_ganancia_del_ticket " +
            "FROM (SELECT ti.id, ti.ticketid,  " +
                    "(SUM(tl.price * tl.units) * 1.16) as to,  " +
                    "(SUM(pr.pricebuy * tl.units) * 1.20) as pb,  " +
                    "(SUM(pricesell * tl.units) * 1.16) as ps " +
                "FROM tickets ti " +
                    "INNER JOIN ticketlines tl on tl.ticket = ti.id " +
                    "LEFT OUTER JOIN products pr on tl.product = pr.id " +
                "GROUP BY ti.id, ti.ticketid) ve " +
                    "INNER JOIN receipts re on re.id = ve.id " +
                "WHERE ve.to > 0 AND " +
                    "re.datenew BETWEEN to_timestamp('" + df.format(datestart) +
                    "', 'YYYYMMDDHH24MISSMS') AND to_timestamp('" + df.format(dateend) +
                    "', 'YYYYMMDDHH24MISSMS')";
        
        BaseSentence sent = new StaticSentence(getSession(), sentSql, null, SerializerReadInteger.INSTANCE);
        DataResultSet drs;
        String res = "";
        try {
            drs = sent.openExec(null);
            if (drs.next())
                res = (new DecimalFormat("#,##0.00")).format(drs.getDouble(4)) + " %, " +
                    (new DecimalFormat("#,##0.00")).format(drs.getDouble(1) - drs.getDouble(2)) + " €";
            drs.close();
        } catch (Exception ex) {
            Logger.getLogger(ScriptletUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
 
    public String printCashEarnings(PaymentsModel p) {
        return p == null ? "" : printCashEarnings(p.getDateStart(), p.getDateEnd());
    }
 
    public Session getSession() {
        if (this.session == null) {
            AppConfig config = new AppConfig(new String[] { });
            config.load();
            String sDBURL = config.getProperty("db.URL");
            String sDBUser = config.getProperty("db.user");
            String sDBPassword = config.getProperty("db.password");
            if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                // the password is encrypted
                AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                sDBPassword = cypher.decrypt(sDBPassword.substring(6));
            }
            try {
                this.session = new Session(sDBURL, sDBUser, sDBPassword);
            } catch (SQLException ex) {
                Logger.getLogger(ScriptletUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.session;
    }
}