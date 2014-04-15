package com.mv2studio.tswp.model;

import java.io.Serializable;

public class EventFile implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String name;
	public int id;
	
	public EventFile(String name, int id) {
		this.id = id;
		this.name = name;
	}
}
