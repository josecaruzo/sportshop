package br.com.fiap.mslogistic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "purchase_history")
public class PurchaseHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long  id;

	@Positive
	@Column(nullable = false)
	private Long purchaseId;

	@Column(length = 20, nullable = false)
	private String status;

	@Column
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	private LocalDateTime statusDate;
}
