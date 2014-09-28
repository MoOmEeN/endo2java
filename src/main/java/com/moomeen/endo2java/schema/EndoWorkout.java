package com.moomeen.endo2java.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndoWorkout {

	public Long id;
	public Long duration;
	public Double distance;
	@JsonProperty("burgers_burned")
	public Double burgersBurned;
	public Integer sport;
	@JsonProperty("start_time")
	public String startTime;
	public Double calories;
	public Boolean live;

}