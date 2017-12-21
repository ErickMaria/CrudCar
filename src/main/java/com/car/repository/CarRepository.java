package com.car.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.car.entity.Car;


public interface CarRepository extends JpaRepository<Car, Long> {
	 
	public List<Car> findByModelIgnoreCase(String model);
	
	
	//public List<Car>findByModelIgnoreCase(String model);
	
}
