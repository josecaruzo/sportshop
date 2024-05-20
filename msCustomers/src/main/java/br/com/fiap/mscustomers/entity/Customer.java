package br.com.fiap.mscustomers.entity;

import br.com.fiap.mscustomers.entity.validation.ValidZipCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Data
@NoArgsConstructor
@Entity
@ValidZipCode //Validating the zip code (CEP) to this entity
@Table(name = "customers")
public class Customer {

	@Id
	@CPF
	@Column(length = 14, nullable = false) //Format: 000.000.000-00
	private String cpf;

	@NotBlank
	@Column(length = 50, nullable = false)
	private String fullName;

	@Email
	@NotBlank
	@Column(length = 50, nullable = false)
	private String email;

	@NotBlank
	@Column(length = 9, nullable = false) //Format: 00000-000
	private String zipCode; // CEP

	@NotBlank
	@Column(nullable = false)
	private String address;

	@NotBlank
	@Column(length = 50, nullable = false)
	private String city;

	@NotBlank
	@Column(length = 30, nullable = false)
	private String state;

	@NotBlank
	@Column(length = 30, nullable = false)
	private String country;
}
