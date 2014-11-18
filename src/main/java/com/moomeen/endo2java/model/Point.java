package com.moomeen.endo2java.model;

import static com.moomeen.endo2java.model.Constants.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Point {

	private DateTime time;
	private Double speed;
	@JsonProperty("dist")
	private Double distance;
	@JsonProperty("alt")
	private Double altitude;
	@JsonProperty("lng")
	private Double longitude;
	@JsonProperty("lat")
	private Double latitude;
	@JsonProperty("inst")
	private PointInstruction instruction;

	@JsonProperty("time")
	public void setStartTime(String startTime){
		this.time = DateTime.parse(startTime, DateTimeFormat.forPattern(DATE_FORMAT));
	}

	public DateTime getTime() {
		return time;
	}
	public Double getSpeed() {
		return speed;
	}
	public Double getDistance() {
		return distance;
	}
	public Double getAltitude() {
		return altitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public PointInstruction getInstruction() {
		return instruction;
	}
}