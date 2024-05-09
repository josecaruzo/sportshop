package br.com.fiap.msstock.controller.exception;

import lombok.Getter;

@Getter
public class ValidateMessage {
	private String entity;
	private String field;
	private String message;

	public ValidateMessage(String entity, String field, String message) {
		this.entity = entity;
		this.field = field;
		this.message = message;
	}
}
