/**
 * 
 */
package com.example.weather.rest.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author athi
 *
 */
@Entity(name="simple_city")
@Table(name="simple_city")
public class SimpleCity {
	@Id
	@GeneratedValue
    @Column(name = "city_id")
	private int id;
	
	private String name;
	private int code;
	
    @JoinColumn(name = "weather_id")
    @OneToOne(cascade = CascadeType.ALL)
	private SimpleWeather weather;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public SimpleWeather getWeather() {
		return weather;
	}

	public void setWeather(SimpleWeather weather) {
		this.weather = weather;
	}

	@Override
	public String toString() {
		return "{id=" + id + ", name=" + name + ", code=" + code + ", weather=" + weather + "}";
	}
	
    
}
