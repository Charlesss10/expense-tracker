package com.charles;

import java.sql.Date;
import java.sql.Timestamp;

public class Transaction {
	private int accountId;
	private String transactionId;
	private String type;
	private double amount;
	private String source;
	private String category;
	private String description;
	private Date date;
	private Timestamp system_date;

	public String getType() {
		return this.type;
	}

	public double getAmount() {
		return this.amount;
	}

	public Date getDate() {
		return this.date;
	}

	public String getTransactionId() {
		return this.transactionId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTransactionId(String id) {
		this.transactionId = id;
	}

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Timestamp getSystem_date() {
        return system_date;
    }

    public void setSystem_date(Timestamp system_date) {
        this.system_date = system_date;
    }
}