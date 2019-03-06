
package com.example.weather.rest.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "3h"
})
public class Rain {

    @JsonProperty("3h")
    private Long _3h;

    @JsonProperty("3h")
    public Long get3h() {
        return _3h;
    }

    @JsonProperty("3h")
    public void set3h(Long _3h) {
        this._3h = _3h;
    }

    public Rain with3h(Long _3h) {
        this._3h = _3h;
        return this;
    }

	@Override
	public String toString() {
		return "{_3h=" + _3h + "}";
	}

}
