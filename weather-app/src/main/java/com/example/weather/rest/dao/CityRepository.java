/**
 * 
 */
package com.example.weather.rest.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.weather.rest.entity.City;

/**
 * @author athi
 *
 */
public interface CityRepository extends JpaRepository<City, Long> {
	public City findByName(String city);
}
