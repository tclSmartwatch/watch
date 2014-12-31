package com.tcl.watch.bean;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Property;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "control_table")
public class ControlBean {
	@Id(column = "_id")
	private int id;

	@Property(column = "startdate")
	private String startDate;
	@Property(column = "stopdate")
	private String stopDate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getStopDate() {
		return stopDate;
	}
	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}
	
	
}
