package com.heidelpay.jm.apidoc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.templates.TemplateFormats.asciidoctor;
import static org.springframework.restdocs.templates.TemplateFormats.markdown;
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
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.templates.TemplateFormat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JmApiDocSampleApplication.class)
public class PaymentControllerAutoRestDocs {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
	@Autowired
	WebApplicationContext webAppContext;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected HttpMessageConverter<Object> mappingJackson2HttpMessageConverter;

	private MockMvcBuilder createMockMvcBuilder(TemplateFormat templateFormat) {
		return MockMvcBuilders.webAppContextSetup(this.webAppContext)
				.apply(documentationConfiguration(this.restDocumentation)
				.uris().withScheme("https").withHost("sample.api.com")
				.withPort(443).and().operationPreprocessors().withRequestDefaults(prettyPrint())
				.withResponseDefaults(prettyPrint()).and()
				.snippets().withDefaults(
						CliDocumentation.curlRequest(), HttpDocumentation.httpRequest(),
						PayloadDocumentation.responseBody(), HttpDocumentation.httpResponse(),
						AutoDocumentation.requestFields(), AutoDocumentation.responseFields(),
						AutoDocumentation.pathParameters(), AutoDocumentation.requestParameters(),
						AutoDocumentation.description(), AutoDocumentation.methodAndPath(),
						AutoDocumentation.requestHeaders(), AutoDocumentation.section())
				.withTemplateFormat(templateFormat)
				);
	}
	
	/**
	 * 
	 * @return the configured MockMVC using asciidoctor as templates
	 */
	private MockMvc createMockMvc() {
		return createMockMvcBuilder(asciidoctor()).build();
	}
	
	/**
	 * 
	 * @return MockMvc producing markdown
	 */
	private MockMvc createMockMvcWithMarkdownTemplate() {
		return createMockMvcBuilder(markdown()).build();
	}
	
	/**
	 * 
	 * Documents the charge call based on Spring REst Docs with Spring auto Rest Docs
	 */
	@Test
	public void auto_rest_doc_adoc() throws Exception {
		String payload = asJson("Marsl Schmitt", "4444333322221111", "123", LocalDate.of(2021, 11, 30));
		MockMvc mockMvc = createMockMvc(); 
	
		mockMvc.perform(post("/heidelpay/charge").contentType(MediaType.APPLICATION_JSON)
				// creditcard as body, amount as param
				.content(payload).param("amount", "12.21"))
				.andExpect(status().isOk())
				.andDo(JacksonResultHandlers.prepareJackson(objectMapper))
				.andDo(document("auto-adoc/charge"));
	}
	

	/**
	 * Documents the charge method with Spring (auto) Rest Docs and produces
	 * Markdown to be used together with Slate
	 * 
	 */
	@Test
	public void auto_rest_doc_md() throws Exception {
		String payload = asJson("Marsl Schmitt", "4444333322221111", "123", LocalDate.of(2021, 11, 30));
		MockMvc mockMvc = createMockMvcWithMarkdownTemplate();
		
		mockMvc.perform(post("/heidelpay/charge").contentType(MediaType.APPLICATION_JSON)
				// for creditcard as body, amount as param
				.content(payload).param("amount", "12.21")).andExpect(status().isOk())
				.andDo(JacksonResultHandlers.prepareJackson(objectMapper)).andDo(document("auto-md/charge"));
	}

	
	private String asJson(String cardHolder, String number, String cvc, LocalDate date)
			throws HttpMessageNotWritableException, IOException {
		CreditCard card = CreditCard.VISA(cardHolder, number, cvc, date);
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(card, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}
	
}
