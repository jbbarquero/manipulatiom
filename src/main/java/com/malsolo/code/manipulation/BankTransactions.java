package com.malsolo.code.manipulation;

public class BankTransactions {
	
	public static void main(String[] args) {
		
		System.out.println("BankTransactions.main");
		
		BankTransactions bank = new BankTransactions();
		
		//login and add i dollars to account
		for (int i = 0; i < 100; i++) {
			String accountId = "account" + i;
			bank.login("password", accountId, "Javier");
			bank.unimportantProcessing(accountId);
			bank.finalizeTransaction(accountId, Double.valueOf(i));
		}
		
		System.out.println("\nBankTransactions.main END");

	}
	
	@ImportantLog(fields = {"1", "2"})
	public void login(String p, String i, String n) {
		System.out.printf("\nLogin p:%s, i:%s, n:%s\n", p, i, n);
	}
	
	public void unimportantProcessing(String i) {
		System.out.printf("unimportantProcessing i:%s\n", i);
	}
	
	@ImportantLog(fields = {"0", "1"})
	public void finalizeTransaction(String i, double d) {
		System.out.printf("finalizeTransaction i:%s, d:%f\n", i, d);
	}

}
