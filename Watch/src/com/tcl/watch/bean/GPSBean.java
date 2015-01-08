package com.tcl.watch.bean;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Property;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "gps_table")
public class GPSBean implements Serializable {
	@Id(column = "_id")
	private int id;

	@Property(column = "dates")
	private String date;

	@Property(column = "speed")
	private float speed;

	@Property(column = "latitude")
	private double latituede;

	@Property(column = "longitude")
	private double longitude;

	@Property(column = "altitude")
	private double altitude;

	@Property(column = "bearing")
	private float bearing;// 方向

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

	// public Time getTime() {
	// return time;
	// }
	//
	// public void setTime(Time time) {
	// this.time = time;
	// }

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public double getLatituede() {
		return latituede;
	}

	public void setLatituede(double latituede) {
		this.latituede = latituede;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}


	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

}
