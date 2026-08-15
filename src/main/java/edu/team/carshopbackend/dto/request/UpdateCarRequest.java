package edu.team.carshopbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;


@Data
public class UpdateCarRequest {
    @NotBlank(message = "Name can't be empty!")
    private String name;

    @PositiveOrZero(message = "The price must be greater than or equal to zero")
    private Long price;

    private String description;

    @PositiveOrZero(message = "The mileage must be greater than or equal to zero")
    private Long mileage;

    private Integer year;
}


