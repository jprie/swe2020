package model;

import java.time.LocalDate;



public class Photo {

	// attributes are stored in properties to allow for easier access in the
	// controller
	private long id;
	private String name;
	private String url;
	private Photographer photographer;
	private LocalDate date;
	private String location;

	public Photo(String name, String url, Photographer photographer, LocalDate date, String location) {
		super();
		this.name = name;
		this.url = url;
		this.photographer = photographer;
		this.date = date;
		this.location = location;
	}
	
	

	public Photo(long id, String name, String url, Photographer photographer, LocalDate date, String location) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.photographer = photographer;
		this.date = date;
		this.location = location;
	}



	public Photo(Photo oldPhoto) {
		super();
		this.name = oldPhoto.name;
		this.url = oldPhoto.url;
		this.photographer = oldPhoto.photographer;
		this.date = oldPhoto.date;
		this.location = oldPhoto.location;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Photographer getPhotographer() {
		return photographer;
	}

	public void setPhotographer(Photographer photographer) {
		this.photographer = photographer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((photographer == null) ? 0 : photographer.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Photo other = (Photo) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (photographer == null) {
			if (other.photographer != null)
				return false;
		} else if (!photographer.equals(other.photographer))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	

}