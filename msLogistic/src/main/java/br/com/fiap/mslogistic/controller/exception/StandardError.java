package br.com.fiap.mslogistic.controller.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class StandardError {
	private Instant timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
}
