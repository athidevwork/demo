
package com.example.weather.rest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity(name="coord")
@Table(name="coord")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "lon",
    "lat"
})
public class Coord {

	@Id
	@GeneratedValue
	@Column(name = "coord_id")
	private int id;
	
    @JsonProperty("lon")
    private Double lon;
    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    public Double getLon() {
        return lon;
    }

    @JsonProperty("lon")
    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Coord withLon(Double lon) {
        this.lon = lon;
        return this;
    }

    @JsonProperty("lat")
    public Double getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Coord withLat(Double lat) {
        this.lat = lat;
        return this;
    }

	@Override
	public String toString() {
		return "{id=" + id + ", lon=" + lon + ", lat=" + lat + "}";
	}

}
