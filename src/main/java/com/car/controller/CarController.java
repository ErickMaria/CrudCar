package com.car.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.car.entity.Car;
import com.car.entity.Carmaker;
import com.car.repository.CarRepository;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard/car")
public class CarController {
	
	private static final String DEFAULT_DIR = "\\images\\cars\\";
	private static final String UPLOAD_DIR = "C:\\Users\\User\\eclipse-workspace\\CrudCarSpringMVC\\src\\main\\resources\\static";
	private static final String UPLOAD_DEFAULT_IMG = "\\images\\defalut\\no_image.png";
	
	@Autowired
	private CarRepository cars;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ModelAndView home() {
		ModelAndView mav = new ModelAndView("dashboard/car/home");

		mav.addObject("listCar", cars.findAll());
		mav.addObject("searchCar", new Car());
		mav.addObject("carmakers", Carmaker.values());

		return mav;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register(Car car) {
		ModelAndView mav = new ModelAndView("dashboard/car/register");
		mav.addObject("car");
		mav.addObject("carmakers", Carmaker.values());

		return mav;
	}
	
	

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView save(@RequestParam("imageFile") MultipartFile image, @Valid Car car, 
							BindingResult result, RedirectAttributes attribute) {
		
		if (result.hasErrors()) {
			return register(car);
		}
		
		if(image.isEmpty()) {
			car.setImage(UPLOAD_DEFAULT_IMG);
		}
		
		try {
			
			if(car.getImage() == null) {
			
				byte[] bytes = image.getBytes();
	            Path path = Paths.get( UPLOAD_DIR + DEFAULT_DIR + car.getModel() + image.getOriginalFilename());
	            Files.write(path, bytes);
	            car.setImage(DEFAULT_DIR + car.getModel() + image.getOriginalFilename());
			}
			
            this.cars.save(car);

    		attribute.addFlashAttribute("success_message", "Car save sucessefull");

    		return new ModelAndView("redirect:/dashboard/car/register");
			
		}catch(IOException ex) {
			attribute.addFlashAttribute("fail_message", "Error at upoload image");
			return new ModelAndView("redirect:/dashboard/car/register");
		}
		
	}

	@RequestMapping(value = "/find", method = RequestMethod.POST)
	public ModelAndView find(Car car) {
		ModelAndView mav = new ModelAndView("dashboard/car/find");

		if (car.getModel().equals("")) {
			return new ModelAndView("redirect:/dashboard");
		}

		mav.addObject("foundCar", this.cars.findByModelIgnoreCase(car.getModel()));

		return mav;
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public ModelAndView delete(@PathVariable("id") Long id) {
		
		Car car = cars.findOne(id);
		
		File image = new File( UPLOAD_DIR + car.getImage());
		
		if(image.exists() && !car.getImage().equals(UPLOAD_DEFAULT_IMG)) {
			image.delete();
		}
		
		cars.delete(id);
		
		return new ModelAndView("redirect:/dashboard/car");
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public String update(@RequestParam("imageFile") MultipartFile image, Car car) {
		
		
		//Not Working
		
		boolean flag = false;
		Car fCar = cars.findOne(car.getId());
		
		File fImage = new File( UPLOAD_DIR + fCar.getImage());
		
		if(fImage.exists() && !image.getOriginalFilename().equals(UPLOAD_DEFAULT_IMG)) {
			fImage.delete();
			flag = true;
		}
		
		try {
			
			if(flag && !fCar.getImage().equals(image.getOriginalFilename())) {
			
			byte[] bytes = image.getBytes();
		    Path path = Paths.get( UPLOAD_DIR + DEFAULT_DIR + car.getModel() + image.getOriginalFilename());
		    Files.write(path, bytes);
		    car.setImage(DEFAULT_DIR + car.getModel() + image.getOriginalFilename());
		    
			}
			
			this.cars.save(car);
		    return new ModelAndView("redirect:/dashboard/car");
			
		}catch(IOException ex) {
			return new ModelAndView("redirect:/dashboard/car");
		}
		
	}

}