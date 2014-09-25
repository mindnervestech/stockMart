package email;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import models.User;
import play.Configuration;
import play.Logger;
import play.Play;
import akka.actor.Cancellable;

import com.feth.play.module.mail.Mailer;
import com.feth.play.module.mail.Mailer.Mail;
import com.feth.play.module.mail.Mailer.Mail.Body;

public class EDMUtility {
	
	protected static final String PROVIDER_KEY = "password";

	protected static final String SETTING_KEY_MAIL = "mail";
	
	public static void sendMailToUser(final String emailId, final String uId) {
		String url = "http://localhost:9000/changePass/" + emailId + "?token=" + uId;
		Body body =  new Body(url);
		System.out.println("........................"+url);
		sendMail("Password Recovery", body, emailId);
	}

	protected static Cancellable sendMail(final String subject, final Body body, final String recipient) {
		return sendMail(new Mail(subject, body, new String[] { recipient }));
	}
	
	protected static Cancellable sendMail(final Mail mail) {
		Configuration config = Play.application().configuration()
				.getConfig("mail");
		Mailer mailer = Mailer.getCustomMailer(config);
		if(mailer == null) return null;
		return mailer.sendMail(mail);
	}
	
}
