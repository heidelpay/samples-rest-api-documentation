package com.heidelpay.jm.apidoc.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonExpiryDateDeSerializer extends StdDeserializer<LocalDate> {

	private static final long serialVersionUID = 1385052016802991558L;
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy/MM/dd");
	
	
	public JsonExpiryDateDeSerializer() {
		super(LocalDate.class);
		 SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
	@Override
	public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String asText = p.getText();
		if(asText == null) {
			return null;
		}
		return LocalDate.parse( appendDay(asText), formatter);	
	}

	private String appendDay(String date) {
		return (date.length() < 8) ? date + "/11" : date;
	}
	
}
