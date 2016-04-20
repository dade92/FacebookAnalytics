package test;

import java.io.Serializable;

import facebook4j.IdNameEntity;
import facebook4j.Picture;

public class FacebookUser implements Serializable {

	/**
	 * javaBean dell'utente
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String id;
	private IdNameEntity location;
	private String gender;
	private String link;
	private Picture picture;

	/*
	 * getter e setter per ogni attributo
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IdNameEntity getLocation() {
		return location;
	}

	public void setLocation(IdNameEntity idNameEntity) {
		this.location = idNameEntity;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

}
