/**
 * 
 */
package com.example.weather.rest.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.weather.rest.entity.SimpleCity;

/**
 * @author athi
 *
 */
public interface SimpleCityRepository extends JpaRepository<SimpleCity, Long> {
	public SimpleCity findByName(String city);
}
