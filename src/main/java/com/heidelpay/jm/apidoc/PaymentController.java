package com.heidelpay.jm.apidoc;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heidelpay")
public class PaymentController {

	@Autowired 
	PaymentService paymentService;

	/**
	 * Performs a charge of the given CreditCard. This description comes from the JavaDocs of the Controller-Action.
	 * @param card The credit card data.
	 * @param amount The amount the CreditCard should be charged with.
	 * @return a summary about the payment
	 */
	@PostMapping("charge")
	public PaymentResponse charge(@RequestBody CreditCard card, @RequestParam BigDecimal amount) {
		return paymentService.doPay(card, amount);
	}
	
	@GetMapping("/payment/{id}")
	public String get(@PathVariable String id) {
		return "{\"id\":\"" + id + "\"}";
	}
	
}
