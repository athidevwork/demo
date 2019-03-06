package com.example.weather.rest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="simple_weather")
@Table(name="simple_weather")
public class SimpleWeather {
	@Id
	@GeneratedValue
	@Column(name = "weather_id")
	private int id;
    
	private double tempMin;
	private double tempMax;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getTempMin() {
		return tempMin;
	}
	public void setTempMin(double tempMin) {
		this.tempMin = tempMin;
	}
	public double getTempMax() {
		return tempMax;
	}
	public void setTempMax(double tempMax) {
		this.tempMax = tempMax;
	}
	@Override
	public String toString() {
		return "{tempMin=" + tempMin + ", tempMax=" + tempMax + "}";
	}
}
