package com.mv2studio.tswp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TClass implements Serializable {

	private static final long serialVersionUID = -8639004259345142397L;
	private int id, department, year;
	private State state = State.NUL;
	private String name, desc, room;
	private Date start, end;
	private boolean notify, repeatWeekly, excercise, newDay;
	private ArrayList<EventFile> files = new ArrayList<EventFile>();
	
	public enum State {
		NUL, NEXT, RUN
	}

	public TClass(int id, String name, String room, Date start, Date end, boolean newDay, boolean isExercise, boolean notify) {
		this(name, room, start, end, isExercise, notify);
		this.newDay = newDay;
		this.id = id;
	}

	public TClass(String name, String room, Date start, Date end, boolean isExercise, boolean notify) {
		this.name = name;
		this.room = room;
		this.start = start;
		this.end = end;
		this.notify = notify;
		this.excercise = isExercise;

		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();

		startDay.setTime(start);
		endDay.setTime(end);

		long diff = endDay.getTimeInMillis() - startDay.getTimeInMillis();
		long days = diff / (24 * 60 * 60 * 1000);

		repeatWeekly = days > 7;
	}
	
	public TClass(String name, String desc, String room, Date start, Date end, boolean isExercise, boolean notify) {
		this(name, room, start, end, notify, isExercise);
		this.desc = desc;
	}
	
	public void addFile(EventFile i) {
		files.add(i);
	}
	
	public ArrayList<EventFile> getFiles(){
		return files;
	}
	
	public void setFiles(ArrayList<EventFile> files) {
		this.files = files;
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

	public boolean isExcercise() {
		return excercise;
	}

	public void setExcercise(boolean excercise) {
		this.excercise = excercise;
	}

	public boolean isNewDay() {
		return newDay;
	}

	public void setNewDay(boolean newDay) {
		this.newDay = newDay;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getDepartment() {
		return department;
	}

	public void setDepartment(int department) {
		this.department = department;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
