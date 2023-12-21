package com.example.app.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerWeb {
	Integer id;
	String company;
	@NotBlank
	String loginId;
	String loginPass;
}
