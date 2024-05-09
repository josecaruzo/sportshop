package br.com.fiap.msstock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(length = 50, nullable = false)
	private String name;

	@NotBlank
	@Column(nullable = false)
	private String description;

	@Positive
	@Column(nullable = false)
	private Float price;

	@PositiveOrZero
	@Column(nullable = false)
	private Integer quantity;
}
