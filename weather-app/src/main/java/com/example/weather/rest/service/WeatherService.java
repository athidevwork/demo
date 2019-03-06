/**
 * 
 */
package com.example.weather.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.weather.rest.dao.CityRepository;
import com.example.weather.rest.dao.SimpleCityRepository;
import com.example.weather.rest.entity.City;
import com.example.weather.rest.entity.Example;
import com.example.weather.rest.entity.SimpleCity;

/**
 * @author athi
 *
 */
@Component
public class WeatherService {
	@Autowired
	SimpleCityRepository simpleCityRepo;
	
	@Autowired
	CityRepository cityRepo;
	
	public SimpleCity getWeatherBySimpleCity(String cityName) {
		return simpleCityRepo.findByName(cityName);
	}
	
	public SimpleCity addSimpleCity(SimpleCity entity) {
		return simpleCityRepo.save(entity);
	}
	
	public City addCity(City entity) {
		return cityRepo.save(entity);
	}
	
	public City addExample(Example example) {
		City city = example.getCity();
		return cityRepo.save(city);
	}
	
	public City getWeatherByCity(String cityName) {
		return cityRepo.findByName(cityName);
	}
}
