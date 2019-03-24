package com.heidelpay.jm.apidoc.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonExpiryDateSerializer extends StdSerializer<LocalDate> {
	
	private static final long serialVersionUID = 6492213861065507447L;
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YY/MM");
	
	public JsonExpiryDateSerializer() {
		super(LocalDate.class);
	}
	
	
	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if(value != null) {
			gen.writeString(value.format(formatter));
		}
	}
}
