package com.moomeen.endo2java.model;

import static com.moomeen.endo2java.model.Constants.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {

	@JsonProperty("weight_kg")
	private Integer weight;
	private String phone;
	private Sex sex;
	private DateTime syncTime;
	private DateTime dateOfBirth;
	@JsonProperty("lounge_member")
	private Boolean loungeMember;
	@JsonProperty("favorite_sport")
	private Sport favoriteSport;
	@JsonProperty("favorite_sport2")
	private Sport favoriteSport2;
	private String units;
	private String country;
	private Long id;
	@JsonProperty("first_name")
	private String firstName;
	@JsonProperty("last_name")
	private String lastName;
	@JsonProperty("picture_id")
	private Long pictureId;
	private DateTime weightTime;
	@JsonProperty("height_cm")
	private Integer height;
	private DateTime createdTime;
	private DateTimeZone timeZone;

	@JsonProperty("sync_time")
	public void setSyncTime(String s){
		this.syncTime = DateTime.parse(s, DateTimeFormat.forPattern(DATE_FORMAT));
	}

	@JsonProperty("date_of_birth")
	public void setDateOfBirth(String s){
		this.dateOfBirth = DateTime.parse(s, DateTimeFormat.forPattern(DATE_FORMAT));
	}

	@JsonProperty("weight_time")
	public void setWeightTime(String s){
		this.weightTime = DateTime.parse(s, DateTimeFormat.forPattern(DATE_FORMAT));
	}

	@JsonProperty("created_time")
	public void setCreatedTime(String s){
		this.createdTime = DateTime.parse(s, DateTimeFormat.forPattern(DATE_FORMAT));
	}
	
	@JsonProperty("time_zone")
	public void setTimeZone(String s){
		this.timeZone = DateTimeZone.forID(s);
	}

	public Integer getWeight() {
		return weight;
	}

	public String getPhone() {
		return phone;
	}

	public Sex getSex() {
		return sex;
	}

	public DateTime getSyncTime() {
		return syncTime;
	}

	public DateTime getDateOfBirth() {
		return dateOfBirth;
	}

	public Boolean getLoungeMember() {
		return loungeMember;
	}

	public Sport getFavoriteSport() {
		return favoriteSport;
	}

	public Sport getFavoriteSport2() {
		return favoriteSport2;
	}

	public String getUnits() {
		return units;
	}

	public String getCountry() {
		return country;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getPictureId() {
		return pictureId;
	}

	public DateTime getWeightTime() {
		return weightTime;
	}

	public Integer getHeight() {
		return height;
	}

	public DateTime getCreatedTime() {
		return createdTime;
	}

	public DateTimeZone getTimeZone() {
		return timeZone;
	}
}
