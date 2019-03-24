package com.heidelpay.jm.apidoc;

import java.time.LocalDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.heidelpay.jm.apidoc.util.JsonExpiryDateDeSerializer;
import com.heidelpay.jm.apidoc.util.JsonExpiryDateSerializer;

/**
 * Represents a Credit-Card
 */
public class CreditCard {

	
	/**
	 * The holder of the CreditCard.
	 */
	@NotNull
	@NotEmpty
	private String holder;

	/**
	 * The CVC/CVV. Length depends on the brand.
	 */
	@Size(min=3, max=4)
	private String cvc;
	
	/**
	 * The CreditCard number.
	 */
	@CreditCardNumber
	private String number;
	
	/**
	 * The expire date of the card.
	 */
	@DateTimeFormat(pattern="MM/YY")
	@Future
	@JsonSerialize(using = JsonExpiryDateSerializer.class)
	@JsonDeserialize(using = JsonExpiryDateDeSerializer.class)
	private LocalDate expiryDate;
	
	/**
	 * The brand of the CreditCard.
	 */
	@NotNull
	private CreditCardBrand brand;

	public static CreditCard VISA(String cardHolder, String number, String cvc, LocalDate date) {
		CreditCard card = new CreditCard();
		card.setHolder(cardHolder);
		card.setNumber(number);
		card.setCvc(cvc);
		card.setExpiryDate(date);
		card.setBrand(CreditCardBrand.VISA);
		return card;
	}
	
	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getCvc() {
		return cvc;
	}

	public void setCvc(String cvc) {
		this.cvc = cvc;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public CreditCardBrand getBrand() {
		return brand;
	}

	public void setBrand(CreditCardBrand brand) {
		this.brand = brand;
	} 
	
}
