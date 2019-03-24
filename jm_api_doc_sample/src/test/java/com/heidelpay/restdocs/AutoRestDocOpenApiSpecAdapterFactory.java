package com.heidelpay.restdocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Attributes.Attribute;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.SimpleType;

import capital.scalable.restdocs.OperationAttributeHelper;
import capital.scalable.restdocs.payload.JacksonRequestFieldSnippet;

/**
 * Factory providing Adapter for tieing togehter Spring Auto Rest Docs and
 * Spriing Rest Docs Openapi.
 * 
 * @author dirk.dorsch
 *
 */
public class AutoRestDocOpenApiSpecAdapterFactory {

	/**
	 * @return a {@code SnippetWithFieldDescriptor} giving access to the Snippets
	 *         {@code FieldDescriptors} of the documented requestFields.
	 */
	public static SnippetWithFieldDescriptor requestFields() {
		return new JacksonRequestFieldSnippetToRequestFieldAdapter();
	}

	/**
	 * @return a {@code SnippetWithFieldDescriptor} giving access to the Snippets
	 *         {@code FieldDescriptors} of the documented requestParameters.
	 */
	public static SnippetWithFieldDescriptor requestParameters() {
		return new AutoDocRequestParameterToRequestParametersAdapter();
	}

	/**
	 * Adapter used to expose requestFields documented with Spring Auto REst Docs to
	 * the openapi generation.
	 */
	static class JacksonRequestFieldSnippetToRequestFieldAdapter extends JacksonRequestFieldSnippet
			implements SnippetWithFieldDescriptor {

		private List<FieldDescriptor> fieldDescriptors;

		@Override
		public void document(Operation operation) throws IOException {
			super.document(operation);
			this.fieldDescriptors = applyConstraintsToDescription(new ArrayList<>(
					createFieldDescriptors(operation, OperationAttributeHelper.getHandlerMethod(operation)).values()));
		}

		@SuppressWarnings("unchecked")
		private List<FieldDescriptor> applyConstraintsToDescription(List<FieldDescriptor> descriptors) {
			for (FieldDescriptor descriptor : descriptors) {
				if (descriptor.getAttributes().containsKey("constraints")) {
					descriptor.description(descriptor.getDescription() + " "
							+ String.join(" ", (List<String>) descriptor.getAttributes().get("constraints")));
				}

			}
			return descriptors;
		}

		public List<FieldDescriptor> getFieldDescriptors() {
			return this.fieldDescriptors;
		}

	}

	/**
	 * Adapter used to expose requestParameters documented with Spring Auto REst Docs to
	 * the openapi generation.
	 */
	static class AutoDocRequestParameterToRequestParametersAdapter
			extends capital.scalable.restdocs.request.RequestParametersSnippet implements SnippetWithFieldDescriptor {

		private List<ParameterDescriptorWithType> parameterDescriptors;

		@Override
		public void document(Operation operation) throws IOException {
			super.document(operation);
			this.parameterDescriptors = toParameterDescriptor(new ArrayList<>(
					createFieldDescriptors(operation, OperationAttributeHelper.getHandlerMethod(operation)).values()));
		}

		private List<ParameterDescriptorWithType> toParameterDescriptor(List<FieldDescriptor> descriptors) {
			List<ParameterDescriptorWithType> parameters = new ArrayList<>();
			for (FieldDescriptor descriptor : descriptors) {
				parameters.add(toParameterDescriptor(descriptor));
			}
			return parameters;
		}

		private ParameterDescriptorWithType toParameterDescriptor(FieldDescriptor descriptor) {
			ParameterDescriptorWithType parameter = new ParameterDescriptorWithType(descriptor.getPath())
					.type(getType(descriptor)).description(descriptor.getDescription())
					.attributes(asAttributes(descriptor.getAttributes()));

			if (descriptor.isIgnored()) {
				parameter.ignored();
			}
			if (descriptor.isOptional()) {
				parameter.optional();
			}
			return parameter;
		}

		private SimpleType getType(FieldDescriptor descriptor) {
			switch (descriptor.getType().toString()) {
			case "String":
				return SimpleType.STRING;
			case "Boolean":
				return SimpleType.BOOLEAN;
			case "Decimal":
				return SimpleType.NUMBER;
			case "Integer":
				return SimpleType.INTEGER;
			default:
				return SimpleType.STRING;
			}
		}

		private Attribute[] asAttributes(Map<String, Object> attrs) {
			List<Attribute> attributes = new ArrayList<>();
			for (String key : attrs.keySet()) {
				attributes.add(Attributes.key(key).value(attrs.get(key)));
			}
			return attributes.toArray(new Attribute[attributes.size()]);
		}

		@Override
		public List<ParameterDescriptorWithType> getParameterDescriptors() {
			return this.parameterDescriptors;
		}

	}

}
