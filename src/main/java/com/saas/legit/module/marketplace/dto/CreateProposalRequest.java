package com.saas.legit.module.marketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProposalRequest {
    @NotBlank(message = "El texto de la propuesta es obligatorio")
    @Size(min = 20, max = 2000, message = "La propuesta debe tener entre 20 y 2000 caracteres")
    private String proposalText;

    @NotNull(message = "La tarifa propuesta es obligatoria")
    @DecimalMin(value = "0.01", message = "La tarifa debe ser mayor a cero")
    private BigDecimal proposedFee;
}
