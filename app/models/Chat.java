package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

import play.db.ebean.Model;

@Entity
public class Chat extends Model {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;
	
	@Lob
	public String message;
	
	@Lob
	public String attachement;
	
    public Date messageTime;
    
    public String messageType;
	
    public Long userId;
    
    public static Model.Finder<Long,Chat> find = new Finder<Long, Chat>(Long.class, Chat.class);


    public static ExpressionList<Chat> findAllByDate(Date d) {
		return Chat.find.where().gt("messageTime", d);
	}

}
