package com.tcl.watch.bean;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Property;

public class SensorBean {
	@Id(column = "_id")
	private int id;

	@Property(column = "dates")
	private String date;
	@Property(column = "fit")
	private int fit;
	@Property(column = "calorie")
	private float calorie;

	@Property(column = "temperature")
	private float temperature;

	@Property(column = "uvsen")
	private float uvsen;

	@Property(column = "gsenx")
	private float gsenx;

	@Property(column = "gseny")
	private float gseny;

	@Property(column = "gsenz")
	private float gsenz;

	@Property(column = "msenv")
	private float msenv;

	@Property(column = "upload")
	private int upload;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getFit() {
		return fit;
	}

	public void setFit(int fit) {
		this.fit = fit;
	}

	public float getCalorie() {
		return calorie;
	}

	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getUvsen() {
		return uvsen;
	}

	public void setUvsen(float uvsen) {
		this.uvsen = uvsen;
	}

	public float getGsenx() {
		return gsenx;
	}

	public void setGsenx(float gsenx) {
		this.gsenx = gsenx;
	}

	public float getGseny() {
		return gseny;
	}

	public void setGseny(float gseny) {
		this.gseny = gseny;
	}

	public float getGsenz() {
		return gsenz;
	}

	public void setGsenz(float gsenz) {
		this.gsenz = gsenz;
	}

	public float getMsenv() {
		return msenv;
	}

	public void setMsenv(float msenv) {
		this.msenv = msenv;
	}

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}
	
	

}
