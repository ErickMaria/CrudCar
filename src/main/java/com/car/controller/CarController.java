package com.car.controller;

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
	private static final String UPLOADED_DIR = "C:\\Users\\User\\eclipse-workspace\\CarSaleSpringMVC\\src\\main\\resources\\static" + DEFAULT_DIR;
	private static final String UPLOADED_DEFAULT_DIR = "\\images\\defalut\\default_img.png";
	
	
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
			car.setImage(UPLOADED_DEFAULT_DIR);
		}
		
		try {
			
			if(car.getImage() == null) {
			
				byte[] bytes = image.getBytes();
	            Path path = Paths.get( UPLOADED_DIR + car.getModel() + image.getOriginalFilename());
	            Files.write(path, bytes);
	            car.setImage(DEFAULT_DIR + car.getModel() + image.getOriginalFilename());
			}
			
            this.cars.save(car);

    		attribute.addFlashAttribute("message", "Car save sucessefull");

    		return new ModelAndView("redirect:/dashboard/car/register");
			
		}catch(IOException ex) {
			attribute.addFlashAttribute("message", "Error at upoload image");
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

		cars.delete(id);

		return new ModelAndView("redirect:/dashboard");
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public ModelAndView update(Car car) {

		cars.save(car);

		return new ModelAndView("redirect:/dashboard");
	}
}