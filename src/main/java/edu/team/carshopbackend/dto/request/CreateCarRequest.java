package edu.team.carshopbackend.dto.request;

import edu.team.carshopbackend.entity.enums.CarState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateCarRequest {
    @NotBlank(message = "Name cannot be empty!")
    private String name;

    @PositiveOrZero(message = "The price must be greater than or equal to zero")
    private Long price;

    private String description;

    @NotNull
    private Long color;

    private Long mileage;

    @NotNull
    private CarState carState;

    @NotNull
    private Long petrolType;

    private Integer engineCapacity;

    private Integer power;

    @NotNull
    private Integer year;

    @NotNull
    private Long producent;

    @NotNull
    private Boolean hadAccidents;
}

