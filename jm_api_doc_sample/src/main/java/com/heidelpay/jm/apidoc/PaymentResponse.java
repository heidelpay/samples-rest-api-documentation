package com.heidelpay.jm.apidoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The PaymentResponse summarizes the result of the payment.
 */
public class PaymentResponse {

	/**
	 * The charged amount.
	 */
	private BigDecimal amount;
	/**
	 * The customer name this payment was made for.
	 */
	private String customer;
	/**
	 * The date the payment was instantiated.
	 */
	private LocalDateTime date = LocalDateTime.now();
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
}

