package com.heidelpay.restdocs;

import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Snippet;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;

/**
 * Interface defining conversion method from Spring Rest Docs
 * {@code FieldDescriptors} to epages apispecs
 * {@code ParamterDescriptorWithType}. Those conversions are used to tie
 * together results fromSpring auto Rest Docs with epages apispec generation.
 *
 *
 */
public interface SnippetWithFieldDescriptor extends Snippet {

	default List<FieldDescriptor> getFieldDescriptors() {
		return null;
	}

	default List<ParameterDescriptorWithType> getParameterDescriptors() {
		return null;
	}

}
