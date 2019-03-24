package com.heidelpay.jm.apidoc;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {

	public PaymentResponse doPay(CreditCard card, BigDecimal amount) {
		PaymentResponse response = new PaymentResponse();
		amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		response.setAmount(amount);
		response.setCustomer(card.getHolder());
		return response;
	}
	
}
