package edu.team.carshopbackend.dto;

import edu.team.carshopbackend.entity.enums.CarState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCarRequestDTO {
    @NotBlank
    private String name;

    @NotNull
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

