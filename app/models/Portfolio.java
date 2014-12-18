package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import play.db.ebean.Model;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Portfolio extends Model {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
	
	public String symbol;
    
    public long noOfShares;

    public static Model.Finder<Long, Portfolio> find = new Finder<Long, Portfolio>(Long.class, Portfolio.class);

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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getNoOfShares() {
		return noOfShares;
	}

	public void setNoOfShares(long noOfShares) {
		this.noOfShares = noOfShares;
	}


    
}
