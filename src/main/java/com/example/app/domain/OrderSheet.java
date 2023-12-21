package com.example.app.domain;

import java.util.Date;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderSheet{
	Integer	entryId;
	Integer	asId;
	Integer	cuId;
	@NotBlank
	String		invoiceId;
	@NotBlank
	String		model;
	@NotBlank
	String		serial;
	@NotBlank
	String		demand;
	Date		receptionDate;
	Date		inceptionDate;
	Date		completionDate;
	Date 		shippingDate;
	Integer	opId;
	@Min(0)
	int			laborMin;
	@Min(0) 
	int			laborCharge;
	String		opComment;
	String		asComment;
	String		cuCompany;
	String		asName;
	String		opName;
}
