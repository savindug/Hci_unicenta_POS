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

package com.openbravo.pos.forms;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.*;

/**
 *
 * @author uniCenta
 */
public class DataLogicOrders extends BeanFactoryDataSingle {
    private SentenceExec m_addOrder;
    private SentenceExec m_resetPickup;
    
    /** Creates a new instance of DataLogicOrders */
    public DataLogicOrders() {            
    }
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){
        
        m_addOrder =  new StaticSentence(s
                , "INSERT INTO orders (orderid, qty, details, attributes, "
                        + "notes, ticketid, ordertime, displayid, auxiliary, "
                        + "completetime) " +
                  "VALUES (?, ?, ?, ?, ?, "
                        + "?, ?, ?, ?, ? ) "
                , new SerializerWriteBasic(new Datas[] {
                    Datas.STRING,   // OrderId
                    Datas.INT,      // Qty
                    Datas.STRING,   // Details
                    Datas.STRING,   // Attributes
                    Datas.STRING,   // Notes
                    Datas.STRING,   // TicketId
                    Datas.STRING,   // OrderTime
                    Datas.INT,      // DisplayId
                    Datas.INT,     // Auxiliary
                    Datas.STRING    // CompleteTime
                }));

        m_resetPickup =  new PreparedSentence(s
                , "UPDATE pickup_number SET id=1"
                , SerializerWriteParams.INSTANCE); 
    }


    public final void addOrder(String orderId, Integer qty, 
            String details, String attributes, String notes, String ticketId, 
            String ordertime, Integer displayId, String auxiliary, String completetime
        ) throws BasicException {

        m_addOrder.exec(orderId, qty, details, attributes, notes, ticketId, 
                ordertime, displayId, auxiliary, completetime);    
    } 
    
    public final void resetPickup() throws BasicException {
        m_resetPickup.exec();    
    }      
}
