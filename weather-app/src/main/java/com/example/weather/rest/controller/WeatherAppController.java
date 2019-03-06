/**
 * 
 */
package com.example.weather.rest.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.rest.entity.City;
import com.example.weather.rest.entity.Coord;
import com.example.weather.rest.entity.Datum;
import com.example.weather.rest.entity.Example;
import com.example.weather.rest.entity.Main;
import com.example.weather.rest.entity.SimpleCity;
import com.example.weather.rest.entity.SimpleWeather;
import com.example.weather.rest.entity.Weather;
import com.example.weather.rest.service.WeatherService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * @author athi
 *
 */
@RestController
@RequestMapping("/api/weatherapp")
public class WeatherAppController {
	@Autowired
	WeatherService service;
	
	Logger l = LoggerFactory.getLogger("WeatherAppController");
	
	@GetMapping("/byName/{city}")
	public SimpleCity getWeatherBySimpleCity(@PathVariable(name = "city") String name) {
		return service.getWeatherBySimpleCity(name);
	}
	
	@GetMapping("/{city}")
	public City getWeatherByCity(@PathVariable(name = "city") String name) {
		return service.getWeatherByCity(name);
	}
	
	@PostMapping("/city")
	public City addCity(City city) {
		System.out.println(city);
		return service.addCity(city);
	}
	
	@PostMapping("/example")
	public City addHourly(Example example) {
		System.out.println(example);
		return service.addExample(example);
	}
	
	@GetMapping("/domdata")
	public String addDomData() {		
		File initialFile = new File("src/main/resources/data/1_formatted.json");
	    
		try {
		    InputStream targetStream = new FileInputStream(initialFile);
		    
		    //String result = getStringFromInputStream(targetStream);
		    //System.out.println(result);
		    
	        JsonReader reader = new JsonReader(new InputStreamReader(targetStream, "UTF-8"));
	        Gson gson = new GsonBuilder().create();

	        long ti, tf;

	        ti = System.currentTimeMillis();
	        System.out.println("Start reading in object mode: " + ti);
	        Example example = gson.fromJson(reader, Example.class);
	        System.out.println(example);
	        tf = System.currentTimeMillis();
	        System.out.println("Completed. Total time: " + (tf - ti));
	    } catch (UnsupportedEncodingException ex) {
	    	System.out.println("UnsupportedEncodingException : " + ex);
	    } catch (IOException ex) {
	        System.out.println("IOException : " + ex);
	    }
		
		return "Added dom json data";
	}
	
	@GetMapping("/streamdata")
	public String addStreamData() {		
		File initialFile = new File("src/main/resources/data/1_formatted.json");
	    
		try {
		    InputStream targetStream = new FileInputStream(initialFile);
		    
		    //String result = getStringFromInputStream(targetStream);
		    //System.out.println(result);
		    
	        JsonReader reader = new JsonReader(new InputStreamReader(targetStream, "UTF-8"));
	        Gson gson = new GsonBuilder().create();

	        long ti, tf;

	        ti = System.currentTimeMillis();
	        System.out.println("Start reading in object mode: " + ti);
	        
	        //Example example = gson.fromJson(reader, Example.class);
	        //System.out.println(example);
	        
	        // Read file in stream mode
	        Example example = new Example();
	        reader.beginObject();
	        while (reader.hasNext()) {
	        	if (reader.nextName().equals("city")) {
		        	City city = gson.fromJson(reader, City.class);
		        	System.out.println(city);
		        	example.setCity(city);		        	
	        	}
	        	if (reader.nextName().equals("time")) {
	        		example.setTime(reader.nextLong());
	        	}
	        	if (reader.nextName().equals("data")) {
	        		reader.beginArray();
	    	        while (reader.hasNext()) {
        	
	    	            // Read data into object model
	    	        	Datum data = gson.fromJson(reader, Datum.class);
	    	            System.out.println("data: " + data);
	    	            example.getData().add(data);
	    	            reader.beginObject();
	    	            break;
	    	        }
	        	}
	        }
	        reader.close();
	        System.out.println(example);
	        
	        tf = System.currentTimeMillis();
	        System.out.println("Completed. Total time: " + (tf - ti));
	    } catch (UnsupportedEncodingException ex) {
	    	System.out.println("UnsupportedEncodingException : " + ex);
	    } catch (IOException ex) {
	        System.out.println("IOException : " + ex);
	    }
		
		return "Added stream json data";
	}
	
	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	@GetMapping("/addSimpleCity")
	public String setSimpleCity() {
		l.debug("Starting adding data");
		SimpleCity city1 = new SimpleCity();
		city1.setCode(100);
		city1.setName("London");
		SimpleWeather weather = new SimpleWeather();
		weather.setTempMin(Double.valueOf("90.5"));
		weather.setTempMax(Double.valueOf("100.12"));
		city1.setWeather(weather);
		
		service.addSimpleCity(city1);
		l.debug("Adding data complete");
		return "Added data";
	}
	
	@GetMapping("/addCity")
	public String setCity() {
		City city1 = new City();
		city1.setName("Dayton");
		city1.setId(Long.valueOf(1234));
		city1.setCountry("US");
		Coord coord = new Coord();
		coord.setLat(Double.valueOf(1234.12));
		coord.setLon(Double.valueOf(-123.12));
		city1.setCoord(coord);
		List<Weather> weatherList = new ArrayList<Weather>();
		Weather weather = new Weather();
		weather.setDescription("Test");
		//weather.setId(Long.valueOf(1234));
		weather.setMain("Test Main");
		weatherList.add(weather);
		city1.setWeather(weatherList);
		
		List<Main> mainList = new ArrayList<Main>();
		Main main = new Main();
		main.setPressure(Double.valueOf(100.0));
		main.setGrndLevel(Double.valueOf(90.13));
		main.setSeaLevel(Double.valueOf(400.23));
		main.setTemp(Double.valueOf(432.23));
		main.setTempMin(Double.valueOf(-234.13));
		main.setTempMax(Double.valueOf(4532.24));
		mainList.add(main);
		city1.setMain(mainList);
		
		service.addCity(city1);
		return "Added City " + city1.getName();
	}
}
