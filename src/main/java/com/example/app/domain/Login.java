package com.example.app.domain;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class Login {
	private Integer id;
	@NotBlank
	private String name;
	@NotBlank
	private String loginId;
	@NotBlank
	private String loginPass;
}