package br.com.fiap.msstock.controller.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidateError extends StandardError{
	private final List<ValidateMessage> messages = new ArrayList<>();

	public void addMessage(String entity, String field, String message){
		messages.add(new ValidateMessage(entity, field,message));
	}


}
