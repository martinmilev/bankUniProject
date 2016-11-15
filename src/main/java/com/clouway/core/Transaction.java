package com.clouway.core;

import java.sql.Timestamp;

/**
 * @author Borislav Gadjev <gadjevb@gmail.com>
 */
public class Transaction {
    public final Timestamp operationDate;
    public final String customerName;
    public final String operationType;
    public final Double amount;

    public Transaction(Timestamp operationDate, String customerName, String operationType, Double amount) {
        this.operationDate = operationDate;
        this.customerName = customerName;
        this.operationType = operationType;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        if (operationDate != null ? !operationDate.equals(that.operationDate) : that.operationDate != null)
            return false;
        if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;
        if (operationType != null ? !operationType.equals(that.operationType) : that.operationType != null)
            return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;

    }

    @Override
    public int hashCode() {
        int result = operationDate != null ? operationDate.hashCode() : 0;
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}

