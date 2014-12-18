package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;

import play.db.ebean.Model;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
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

    

    public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	public String getAttachement() {
		return attachement;
	}



	public void setAttachement(String attachement) {
		this.attachement = attachement;
	}



	public Date getMessageTime() {
		return messageTime;
	}



	public void setMessageTime(Date messageTime) {
		this.messageTime = messageTime;
	}



	public String getMessageType() {
		return messageType;
	}



	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}



	public Long getUserId() {
		return userId;
	}



	public void setUserId(Long userId) {
		this.userId = userId;
	}



	public static ExpressionList<Chat> findAllByDate(Date d) {
		return Chat.find.where().gt("messageTime", d);
	}

}
