
package com.example.weather.rest.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity(name="main")
@Table(name="main")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "temp",
    "temp_min",
    "temp_max",
    "pressure",
    "sea_level",
    "grnd_level",
    "humidity",
    "temp_kf"
})
public class Main {

	@Id
	@GeneratedValue
    @JsonProperty("id")
	private int id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_id", nullable = false)
    private City cityMain;
    
    @JsonProperty("temp")
    private Double temp;
    @JsonProperty("temp_min")
    private Double tempMin;
    @JsonProperty("temp_max")
    private Double tempMax;
    @JsonProperty("pressure")
    private Double pressure;
    @JsonProperty("sea_level")
    private Double seaLevel;
    @JsonProperty("grnd_level")
    private Double grndLevel;
    @JsonProperty("humidity")
    private Long humidity;
    @JsonProperty("temp_kf")
    private Double tempKf;

    @JsonProperty("temp")
    public Double getTemp() {
        return temp;
    }

    @JsonProperty("temp")
    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Main withTemp(Double temp) {
        this.temp = temp;
        return this;
    }

    @JsonProperty("temp_min")
    public Double getTempMin() {
        return tempMin;
    }

    @JsonProperty("temp_min")
    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public Main withTempMin(Double tempMin) {
        this.tempMin = tempMin;
        return this;
    }

    @JsonProperty("temp_max")
    public Double getTempMax() {
        return tempMax;
    }

    @JsonProperty("temp_max")
    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    public Main withTempMax(Double tempMax) {
        this.tempMax = tempMax;
        return this;
    }

    @JsonProperty("pressure")
    public Double getPressure() {
        return pressure;
    }

    @JsonProperty("pressure")
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Main withPressure(Double pressure) {
        this.pressure = pressure;
        return this;
    }

    @JsonProperty("sea_level")
    public Double getSeaLevel() {
        return seaLevel;
    }

    @JsonProperty("sea_level")
    public void setSeaLevel(Double seaLevel) {
        this.seaLevel = seaLevel;
    }

    public Main withSeaLevel(Double seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    @JsonProperty("grnd_level")
    public Double getGrndLevel() {
        return grndLevel;
    }

    @JsonProperty("grnd_level")
    public void setGrndLevel(Double grndLevel) {
        this.grndLevel = grndLevel;
    }

    public Main withGrndLevel(Double grndLevel) {
        this.grndLevel = grndLevel;
        return this;
    }

    @JsonProperty("humidity")
    public Long getHumidity() {
        return humidity;
    }

    @JsonProperty("humidity")
    public void setHumidity(Long humidity) {
        this.humidity = humidity;
    }

    public Main withHumidity(Long humidity) {
        this.humidity = humidity;
        return this;
    }

    @JsonProperty("temp_kf")
    public Double getTempKf() {
        return tempKf;
    }

    @JsonProperty("temp_kf")
    public void setTempKf(Double tempKf) {
        this.tempKf = tempKf;
    }

    public Main withTempKf(Double tempKf) {
        this.tempKf = tempKf;
        return this;
    }

	@Override
	public String toString() {
		return "{id=" + id + ", cityMain=" + cityMain + ", temp=" + temp + ", tempMin=" + tempMin + ", tempMax="
				+ tempMax + ", pressure=" + pressure + ", seaLevel=" + seaLevel + ", grndLevel=" + grndLevel
				+ ", humidity=" + humidity + ", tempKf=" + tempKf + "}";
	}

}
