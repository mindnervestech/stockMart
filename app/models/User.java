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

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
    
    public Boolean isAdmin = false;
    
    
	@Lob
	@JsonIgnore
    public String userPic;
    
	public boolean isAdmin() {
		if(isAdmin == null){
			return false;
		}
		return isAdmin;
	}


	public void setAdmin(Boolean isAdmin) {
		if(isAdmin == null){
			isAdmin = false;
		}
		this.isAdmin = isAdmin;
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


	public static List<User> getAllUsersNotAdmin() {
		List<User> list =  find.all();
		List<User> allUsers = new ArrayList<>();
		if(list != null){
			for(User u:list){
				if(!u.isAdmin()){
					allUsers.add(u);
				}
			}	
		}
		return allUsers;
	}

}
