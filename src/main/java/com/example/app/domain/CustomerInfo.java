package com.example.app.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerInfo {
	Integer id;
	@NotBlank
	String company;
	String section;
	@NotBlank
	String name;
	@Min(0)@NotNull
	Integer phoneEx;
	@Min(1)@NotNull
	Integer phoneDm;
	@Min(20000)@NotNull
	Integer phone;
	String address;
	String loginId;
	Integer zip;
	@Min(1)
	Integer zip1;
	@Min(1)
	Integer zip2;
	String phoneStr;
	String zipStr;
}
