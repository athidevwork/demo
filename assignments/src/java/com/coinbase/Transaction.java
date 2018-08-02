package com.coinbase;

import java.util.Map;

public class Transaction {
	String userPaid;
	double amountPaid;
	int    total;
	//List<String> participantNames;
	Map<String, Double> payments;
	
	public Transaction(String userPaid, double amountPaid, int total, Map<String, Double> payments) {
		super();
		this.userPaid = userPaid;
		this.amountPaid = amountPaid;
		this.total = total;
		this.payments = payments;
	}

	public String getUserPaid() {
		return userPaid;
	}

	public void setUserPaid(String userPaid) {
		this.userPaid = userPaid;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Map<String, Double> getPayments() {
		return payments;
	}

	public void setPayments(Map<String, Double> payments) {
		this.payments = payments;
	}
}

