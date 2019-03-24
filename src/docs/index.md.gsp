---
title: heidelpay Payment Api

language_tabs:
   - http
   - shell

search: true
---

# Introduction


# Charge

No matter which payment-method you choose, the implementation of charge is kept consistent. You will always pass the amount as a request parameter together with the payment-methods details as body.


## CreditCard

<% print new File('build/generated-snippets/auto-md/charge/auto-description.md').text %>


<% print new File('build/generated-snippets/auto-md/charge/http-request.md').text %>

<% print new File('build/generated-snippets/auto-md/charge/curl-request.md').text %>

> Response

<% print new File('build/generated-snippets/auto-md/charge/response-body.md').text %>

### HTTP Request
<% print new File('build/generated-snippets/auto-md/charge/auto-method-path.md').text %>

### Parameter
<% print new File('build/generated-snippets/auto-md/charge/auto-request-parameters.md').text.replaceAll("\n\n", " ") %>
### Body
<% print new File('build/generated-snippets/auto-md/charge/auto-request-fields.md').text.replaceAll("\n\n", " ") %>
### Response
<% print new File('build/generated-snippets/auto-md/charge/auto-response-fields.md').text.replaceAll("\n\n", " ") %>
### Exception

## Sepa

### HTTP Request

### Parameter

### Body

### Response

### Exception


## Giropay

### HTTP Request

### Parameter

### Body

### Response

### Exception


## iDEAL

### HTTP Request

### Parameter

### Body

### Response

### Exception


## PayPal

### HTTP Request

### Parameter

### Body

### Response

### Exception

