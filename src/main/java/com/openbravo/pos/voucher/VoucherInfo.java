
package com.openbravo.pos.voucher;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.SerializerRead;


public class VoucherInfo implements IKeyed {
    private String id;
    private String voucherNumber;
    private String customerId;
    private String customerName;
    private double amount;

    public VoucherInfo() {
    }

    public VoucherInfo(String id, String voucherNumber, String customerId, String customerName, double amount) {
        this.id = id;
        this.voucherNumber = voucherNumber;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
    }
    
    
    

    @Override
    public Object getKey() {
        return getId();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the voucherNumber
     */
    public String getVoucherNumber() {
        return voucherNumber;
    }

    /**
     * @param voucherNumber the voucherNumber to set
     */
    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    /**
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    
     @Override
    public String toString() {
        return voucherNumber;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
            return new VoucherInfo(dr.getString(1), dr.getString(2), dr.getString(3),dr.getString(4),dr.getDouble(5));
        }};
    }

    
}
