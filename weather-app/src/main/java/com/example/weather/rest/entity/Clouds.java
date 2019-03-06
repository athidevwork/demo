
package com.example.weather.rest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "all"
})
public class Clouds {

    @JsonProperty("all")
    private Long all;

    @JsonProperty("all")
    public Long getAll() {
        return all;
    }

    @JsonProperty("all")
    public void setAll(Long all) {
        this.all = all;
    }

    public Clouds withAll(Long all) {
        this.all = all;
        return this;
    }

	@Override
	public String toString() {
		return "{all=" + all + "}";
	}

}
