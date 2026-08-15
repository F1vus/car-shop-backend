package edu.team.carshopbackend.mapper.impl;

import edu.team.carshopbackend.dto.CarDTO;
import edu.team.carshopbackend.dto.request.CreateCarRequest;
import edu.team.carshopbackend.dto.request.UpdateCarRequest;
import edu.team.carshopbackend.entity.Car;
import edu.team.carshopbackend.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarMapper implements Mapper<Car, CarDTO> {

    private final ModelMapper modelMapper;

    @Override
    public CarDTO mapTo(Car car) {
        CarDTO carDTO = modelMapper.map(car, CarDTO.class);
        if (car.getOwner() != null && car.getOwner().getUser() != null && carDTO.getOwner() != null) {
            carDTO.getOwner().setEmail(car.getOwner().getUser().getEmail());
        }
        carDTO.setHadAccidents(car.getHadAccidents());
        return carDTO;
    }

    @Override
    public Car mapFrom(CarDTO carDTO) {
        Car car = modelMapper.map(carDTO, Car.class);
        car.setHadAccidents(carDTO.getHadAccidents());
        return car;
    }

    public Car mapFrom(CreateCarRequest req) {
        Car car = new Car();
        modelMapper.map(req, car);
        car.setHadAccidents(req.getHadAccidents() == null ? Boolean.FALSE : req.getHadAccidents());
        return car;
    }

    public Car mapFrom(UpdateCarRequest req){
        Car car = new Car();
        modelMapper.map(req, car);
        return car;
    }
}
