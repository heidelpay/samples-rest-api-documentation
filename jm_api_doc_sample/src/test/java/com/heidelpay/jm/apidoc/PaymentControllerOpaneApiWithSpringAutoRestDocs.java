package com.heidelpay.jm.apidoc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.templates.TemplateFormats.markdown;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heidelpay.restdocs.AutoRestDocOpenApiSpecAdapterFactory;
import com.heidelpay.restdocs.SnippetWithFieldDescriptor;

import capital.scalable.restdocs.jackson.JacksonResultHandlers;

/**
 * Demonstrates how to build OpenApi spec with the epages restdocs apispec
 * extension integrated with Spring auto rest Docs
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmApiDocSampleApplication.class)
public class PaymentControllerOpaneApiWithSpringAutoRestDocs {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	@Autowired
	WebApplicationContext webAppContext;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;


	@Test
	public void auto_and_open_api() throws Exception {

		String payload = asJson("Marsl Schmitt", "4444333322221111", "111", LocalDate.of(2021, 11, 30));

		// records Descriptors within auto-rest-doc run
		SnippetWithFieldDescriptor requestFieldsWithFieldDescriptor = AutoRestDocOpenApiSpecAdapterFactory
				.requestFields();
		SnippetWithFieldDescriptor requestParamterWithDescriptor = AutoRestDocOpenApiSpecAdapterFactory
				.requestParameters();
		MockMvc mockMvc = createMockMvcRestDocBuilder(createConfigurer(), markdown()).build();
		mockMvc.perform(
				RestDocumentationRequestBuilders.post("/heidelpay/charge").contentType(MediaType.APPLICATION_JSON)
						// for creditcard as body, amount as param
						.content(payload).param("amount", "12.21"))
				.andExpect(status().isOk()).andDo(JacksonResultHandlers.prepareJackson(objectMapper))
				.andDo(document("openapi/md/charge", requestFieldsWithFieldDescriptor, requestParamterWithDescriptor))
				.andDo(document("openapi/spec/charge",
						ResourceDocumentation.resource(ResourceSnippetParameters.builder()
								.description("Charges a CreditCard.")
								.requestParameters(requestParamterWithDescriptor.getParameterDescriptors())
								.requestFields(requestFieldsWithFieldDescriptor.getFieldDescriptors()).build())));

	}


	private MockMvcRestDocumentationConfigurer createConfigurer() {
		return documentationConfiguration(this.restDocumentation).uris().withScheme("https").withHost("sample.api.com")
				.withPort(443).and().operationPreprocessors().withRequestDefaults(prettyPrint())
				.withResponseDefaults(prettyPrint()).and();
	}
	
	protected DefaultMockMvcBuilder createMockMvcRestDocBuilder(MockMvcRestDocumentationConfigurer restDocConfigurer,
			TemplateFormat templateFormat) {
		return MockMvcBuilders.webAppContextSetup(this.webAppContext)
				.apply(restDocConfigurer.snippets().withTemplateFormat(templateFormat));
	}

	
	private String asJson(String cardHolder, String number, String cvc, LocalDate date)
			throws HttpMessageNotWritableException, IOException {
		CreditCard card = CreditCard.VISA(cardHolder, number, cvc, date);
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(card, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

	
}
