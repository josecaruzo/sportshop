package br.com.fiap.mslogistic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "purchases")
public class Purchase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CPF
	@Column(length = 14, nullable = false) // 000.000.000-00
	private String customerCpf;

	@Column(length = 50)
	private String customerName;

	@Column(length = 9) // 00000-000
	private String deliveryZipCode;

	@Column(length = 200)
	private String deliveryAddress;

	@Column(length = 17) // YYYYMMDDHHMMSSsss
	private String deliveryGroup;

	@Column
	private Float totalAmount;

	@Column(length = 20)
	private String status;

	@OneToMany(cascade = CascadeType.ALL)
	@JsonIgnore
	List<PurchaseItem> items;
}
