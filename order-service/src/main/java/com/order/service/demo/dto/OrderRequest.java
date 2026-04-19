package com.order.service.demo.dto;

import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "Address is required")
    private String address;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
    
    

}
