package com.mv2studio.tswp.model;

import java.io.Serializable;

/**
 * represent file. File is physically on server, so only name and id is needed. Name to show in app, id to get request form server when downloading
 */
public class EventFile implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String name;
	public int id;
	
	public EventFile(String name, int id) {
		this.id = id;
		this.name = name;
	}
}
