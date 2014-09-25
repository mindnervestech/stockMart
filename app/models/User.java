package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import play.data.DynamicForm;
import play.db.ebean.Model;

import com.avaje.ebean.ExpressionList;

@Entity
public class User extends Model{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	public String name;
    
    public String email;

    public String password;
    
    public String status = "A";
    
    public String uId;
    
    @Lob
    public String userPic;
    
    @OneToMany(cascade = CascadeType.ALL)
    public List<Wishlist> wishlists;
    
    public List<Wishlist> getWishlist() {
		return wishlists;
	}


	public void setWishlist(List<Wishlist> wishlist) {
		this.wishlists = wishlist;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getuId() {
		return uId;
	}


	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUserPic() {
		return userPic;
	}


	public User(){};
  
    
    public User(DynamicForm form) {
		this.name = form.get("name");
		this.email = form.get("email");
		this.password = form.get("pwd");
		this.save();
	}
    
    public static Model.Finder<Long,User> find = new Finder<Long, User>(Long.class, User.class);

    public static User findByEmail(final String email) {
            return getEmailUserFind(email).findUnique();
    }
    
    private static ExpressionList<User> getEmailUserFind(final String email) {
        return find.where().eq("email", email);
    }


	public static Long findByUsername(String user) {
		return find.where().eq("name", user).findUnique().id;
	}


	public void setUserPic(String imageDataString) {
		this.userPic = imageDataString;
	}


	public void addToWishlist(Wishlist wishlist) {
		if(this.wishlists == null)
			this.wishlists = new ArrayList<>();
		this.wishlists.add(wishlist);
		this.update();
	}


	public void removeFromWishlist(Wishlist wl) {
		this.wishlists.remove(wl);
		this.update();
		wl.delete();
	}
    

}