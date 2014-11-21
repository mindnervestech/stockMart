package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Sector extends Model {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	public Double portWeight;
    
    public Double spWeight;

    public static Model.Finder<Long, Sector> find = new Finder<Long, Sector>(Long.class, Sector.class);

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPortWeight() {
		return portWeight;
	}

	public void setPortWeight(Double portWeight) {
		this.portWeight = portWeight;
	}

	public Double getSpWeight() {
		return spWeight;
	}

	public void setSpWeight(Double spWeight) {
		this.spWeight = spWeight;
	}
    
    
}
