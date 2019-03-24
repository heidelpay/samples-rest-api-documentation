package com.heidelpay.jm.apidoc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Demonstrates the usage of plain Spring Rest Docs
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmApiDocSampleApplication.class)
public class PaymentControllerRestDocs {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	@Autowired
	WebApplicationContext webAppContext;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;
	
	protected MockMvc createMockMvc() {
		return MockMvcBuilders.webAppContextSetup(this.webAppContext)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme("https").withHost("sample.api.com").withPort(443)
						.and().operationPreprocessors().withRequestDefaults(prettyPrint())
						.withResponseDefaults(prettyPrint()))
				.build();
	}
	
	/**
	 * 
	 * Documents the charge method with plain Spring Rest Docs.
	 * 
	 */
	@Test
	public void given_valid_credit_card_then_payment_is_processed() throws Exception {

		String payload = asJson("Marsl Schmitt", "4444333322221111", "111", LocalDate.of(2021, 11, 30));
		// used for access the Bean Validation 2.0 or Hibernate Validator constraints
		ConstraintDescriptions constraints = new ConstraintDescriptions(CreditCard.class);
		MockMvc mockMvc = createMockMvc();
		mockMvc.perform(post("/heidelpay/charge").contentType(MediaType.APPLICATION_JSON)
				// for creditcard as body, amount as param
				.content(payload).param("amount", "12.21"))
				// do some assertion as test
				.andExpect(status().isOk())
				// document
				.andDo(document("restdoc/charge",
						// document request-parameter
						requestParameters(parameterWithName("amount").description("The amount to be charged.")),
						// document the request body
						requestFields(
								fieldWithPath("holder").type(JsonFieldType.STRING)
										.description("The name of the card holder. The holder must not be null"),
								fieldWithPath("number").type(JsonFieldType.STRING)
										.description("The CreditCard number. Length depends on the brand. "
												// append the constraints to the description
												+ String.join(".", constraints.descriptionsForProperty("number"))),
								fieldWithPath("cvc").type(JsonFieldType.STRING)
										.description("The CVC/CVV. Length depends on the brand. "
												// append the constraints
												+ String.join(".", constraints.descriptionsForProperty("cvc"))),
								fieldWithPath("expiryDate").type(JsonFieldType.STRING)
										.description("The expire date of the card."),
								fieldWithPath("brand").type(JsonFieldType.STRING)
										.description("The brand of the CreditCard.")),
						// document the response
						responseFields(
								fieldWithPath("customer").type(JsonFieldType.STRING)
										.description("The customer name this payment was made for."),
								fieldWithPath("amount").type(JsonFieldType.NUMBER).description("The charged amount"),
								fieldWithPath("date").type(JsonFieldType.STRING)
										.description("The date the payment was instantiated.")

						)));
	}

	private String asJson(String cardHolder, String number, String cvc, LocalDate date)
			throws HttpMessageNotWritableException, IOException {
		CreditCard card = CreditCard.VISA(cardHolder, number, cvc, date);
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(card, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
	
}
