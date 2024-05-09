package br.com.fiap.mspurchases.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Customer {
	private String cpf;
	private String fullName;
	private String email;
	private String zipCode; // CEP
	private String address;
	private String city;
	private String state;
	private String country;
}
