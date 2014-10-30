package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.data.DynamicForm;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Wishlist extends Model {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
    
    public String symbol;

    public String exchange;

    public static Model.Finder<Long,Wishlist> find = new Finder<Long, Wishlist>(Long.class, Wishlist.class);
    
    public Wishlist() {
	}
    
	public Wishlist(DynamicForm form) {
		this.name = form.get("name");
		this.symbol = form.get("symbol");
		this.exchange = form.get("exchange");
	}

	public static Wishlist findByName(String name) {
		return Wishlist.find.where().eq("name", name).findUnique();
	}
    

}
