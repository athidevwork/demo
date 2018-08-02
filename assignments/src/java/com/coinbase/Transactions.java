package com.coinbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {
	static List<Transaction> txns = new ArrayList<Transaction>();
	
	public List<Transaction> getTxns() {
		return txns;
	}

	public void setTxns(List<Transaction> txns) {
		this.txns = txns;
	}

	public static void main (String[] args) {
		Map<String, Double> payments1 = new HashMap<>();
		payments1.put("Alice", 0.0);
		payments1.put("John", 0.0);
		payments1.put("Bob", 0.0);
		payments1.put("Sarah", 0.0);
		
		Transaction txn1 = new Transaction("Sarah", 400.00, 4, payments1);
		txns.add(txn1);
		
		Map<String, Double> payments2 = new HashMap<>();
		payments2.put("John", 0.0);
		payments2.put("Bob", 0.0);
		Transaction txn2 = new Transaction("Alice", 100.00, 2, payments2);
		txns.add(txn2);
		
		divideTransaction();		
		printTransactions();
		
	}

	private static void printTransactions() {
		for (int i = 0; i < txns.size(); i++) {
			Transaction txn = txns.get(i);
			
			txn.getPayments().forEach((k,v) -> {
				if (!txn.getUserPaid().equalsIgnoreCase(k))
					System.out.println(k + " owes " + txn.getUserPaid() + " "+ v);
			}
			);
			System.out.println();
		}
	}

	private static void divideTransaction() {
		for (int i = 0; i < txns.size(); i++) {
			Transaction txn1 = txns.get(i);
			
			//System.out.println("amount = " + txn1.getAmountPaid() + ", participants = " + txn1.getPayments());
			Double remainder = txn1.getAmountPaid() / txn1.getTotal();
			
			Map<String, Double> payments = txn1.getPayments();
			for (String user : payments.keySet()) {
				payments.put(user, remainder);
			}
			//System.out.println("amount = " + txn1.getAmountPaid() + ", participants = " + txn1.getPayments());
		}
	}
}
