package com.moomeen.endo2java.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Sex {

	MALE, FEMALE;

	@JsonCreator
	public static Sex fromString(String s){
		for (Sex value : values()) {
			if (value.name().equals(s)){
				return value;
			}
		}
		return MALE;
	}

}
