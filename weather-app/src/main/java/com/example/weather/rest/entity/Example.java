
package com.example.weather.rest.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "city",
    "time",
    "data"
})
public class Example {

    @JsonProperty("city")
    private City city;
    @JsonProperty("time")
    private Long time;
    @JsonProperty("data")
    private List<Datum> data = new ArrayList<Datum>();

    @JsonProperty("city")
    public City getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(City city) {
        this.city = city;
    }

    public Example withCity(City city) {
        this.city = city;
        return this;
    }

    @JsonProperty("time")
    public Long getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Long time) {
        this.time = time;
    }

    public Example withTime(Long time) {
        this.time = time;
        return this;
    }

    @JsonProperty("data")
    public List<Datum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Example withData(List<Datum> data) {
        this.data = data;
        return this;
    }

	@Override
	public String toString() {
		return "{city=" + city + ", time=" + time + ", data=" + data + "}";
	}
}
