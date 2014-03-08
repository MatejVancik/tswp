package com.mv2studio.tswp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class TClass implements Serializable {

	private static final long serialVersionUID = -8639004259345142397L;
	private int id;
	private String name, room;
	private Date start, end;
	private boolean notify, repeatWeekly;

	
	public TClass(int id, String name, String room, Date start, Date end, boolean notify) {
		this(name, room, start,end, notify);
		this.id = id;
	}
	
	public TClass(String name, String room, Date start, Date end, boolean notify) {
		this.name = name;
		this.room = room;
		this.start = start;
		this.end = end;
		this.notify = notify;
		
		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();
		
		startDay.setTime(start);
		endDay.setTime(end);
		
		long diff = endDay.getTimeInMillis() - startDay.getTimeInMillis();
		long days = diff / (24 * 60 * 60 * 1000);
		
		repeatWeekly = days > 7;
	}

	public boolean isWeekly() {
		return repeatWeekly;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isNotify() {
		return notify;
	}

	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	
}
