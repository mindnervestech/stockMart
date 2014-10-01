package controllers;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.imageio.ImageIO;

import models.Chat;
import models.ChatRoom;
import models.User;
import models.Wishlist;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;

import play.Play;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.WebSocket;
import viewmodel.MemberStatus;
import viewmodel.UserChat;

import com.avaje.ebean.ExpressionList;

import email.EDMUtility;

public class Application extends Controller {
  
    public static Result index() {
    	User user = User.findByEmail(session().get("email"));
    	if(user != null){
    		return redirect("/home");
    	}
        return ok(views.html.index.render(true));
    }
    
    public static Result signup() {
        return ok(views.html.signup.render());
    }
    
    public static Result forgotPass() {
        return ok(views.html.forgotPass.render());
    }
    
    public static Result sendPassURL(){
    	DynamicForm form = new DynamicForm().bindFromRequest();
        String email = form.get("email");
        if ((email == null || email.isEmpty())) {
            flash("error", "Please Enter E-mail.");
            return redirect("/forgotPass");
        }
        User user = User.findByEmail(email);
        if (user == null) {
            flash("error", "Email does not exist.");
            return redirect("/forgotPass");
        }
        String uId = UUID.randomUUID().toString();
        user.setuId(uId);
        user.setStatus("D");
        user.update();
        EDMUtility.sendMailToUser(email, uId);
    	flash("success", "Password has been sent to your email. Check your email.");
        return redirect("/forgotPass");
    }
    
    public static Result changePass(String emailId){
    	System.out.println("email:"+emailId);
    	String uId = request().getQueryString("token");
    	System.out.println("token:"+uId);
    	User user = User.findByEmail(emailId);
    	if(user.email.equalsIgnoreCase(emailId)){
    		if(user.uId.equalsIgnoreCase(uId)){
    			return ok(views.html.changePass.render(user));
    		}
    	}
    	return ok();
    }
        
    public static Result changePass1(String email){
    	DynamicForm form = new DynamicForm().bindFromRequest();
        String pwd = form.get("pwd");
        User user = User.findByEmail(email);
        System.out.println(email);
        user.setStatus("A");
		user.setPassword(pwd);
        user.update();
        session().clear();
        response().setCookie("email", email);
        session("email", email);
        return redirect("/home");
    }
    
    public static Result changePassword(){
    	User user = Application.getLocalUser(session());
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	String pwd = form.get("pwd");
    	user.setPassword(pwd);
    	user.update();
    	return ok();
    }
    
    public static Result signOut() {
    	session().clear();
    	response().discardCookie("email");
        return ok(views.html.index.render(true));
    }
    

    @Security.Authenticated(Secured.class)
    public static Result home() {
    	User user = Application.getLocalUser(session());
        return ok(views.html.home.render(user));
    }
    
    public static Result doLogin() {
        DynamicForm form = new DynamicForm().bindFromRequest();
        String email = form.get("email");
        String password = form.get("password");
        System.out.println("Email :: "+email);
        System.out.println("Password :: "+password);
        if ((email == null || email.isEmpty()) && (password == null || password.isEmpty())) {
            flash("error", "Please Enter E-mail and Password");
            return badRequest(views.html.index.render(false));
        }
        if (email == null || email.isEmpty()) {
            return badRequest(views.html.index.render(false));
        }
        try {
            User user = User.findByEmail(email);
            if (user == null) {
                flash("error", "You have Entered Username that does not exists");
                return badRequest(views.html.index.render(false));
            }
            String originalPassword = user.password;
            if (!originalPassword.equals(password)) {
                flash("error", "Please Enter Valid Password");
                return badRequest(views.html.index.render(false));
            } else if(user.status.equalsIgnoreCase("D")){
            	flash("error", "Please check your mail to validate your account.");
                return badRequest(views.html.index.render(false));
            } else{
            	//session().clear();
                response().setCookie("email", form.get("email"));
                session("email", form.get("email"));
                return redirect("/home");
            }
        } catch (NoSuchElementException e) {
            flash("error", "You have Entered Username that does not exists");
            return badRequest(views.html.index.render(false));
        }
    }


    public static Result registerUser(){
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	User user =  new User(form);
    	session().clear();
        response().setCookie("email", form.get("email"));
    	return redirect("/home");
    	
    }
    
    public static User getLocalUser(final Session session) {
        final User localUser = User.findByEmail(session.get("email"));
        return localUser;
    }
    
    public static WebSocket<JsonNode> chat(final String username) {
        return new WebSocket<JsonNode>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
                
                // Join the chat room.
                try { 
                    ChatRoom.join(username, in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }
    
    
    public static Result dashboardPage(){
    	User user = Application.getLocalUser(session());
    	return ok(views.html.dashboard.render(user));
    }
    
    public static Result loadAllChats(){
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	String userId = form.get("userId");
    	Date d = new Date();
    	Calendar c = Calendar.getInstance();
    	c.setTime(d);
    	c.add(Calendar.DATE, -2);
    	//System.out.println(c.getTime());
    	ExpressionList<Chat> allChats = Chat.find.where().gt("message_time", c.getTime());
    	//allChats.findList();
    	List<Chat> allChatsList = allChats.findList();
    	List<UserChat> ucAll = new ArrayList<>();
    	for(Chat chat: allChatsList){
    		UserChat uc = new UserChat();
    		uc.id = chat.id;
    		uc.message = chat.getMessage();
    		//uc.attachment = chat.getAttachement();
    		uc.messageTime = chat.getMessageTime();
    		uc.user = User.find.byId(chat.getUserId()).getName();
    		//uc.userPic = User.find.byId(chat.getUserId()).getUserPic();
    		uc.userId = chat.getUserId();
    		uc.type = chat.getMessageType();
    		ucAll.add(uc);
    	}
    	Map<String, Object> map = new HashMap<String, Object>();
        map.put("chats", ucAll);
        return ok(Json.toJson(map));
    }
    
    public static Result uploadUserPic() throws IOException{
    	User user = Application.getLocalUser(session());
    	MultipartFormData body = request().body().asMultipartFormData();
        FilePart filePart = body.getFile("file");
        File f = filePart.getFile();
       	FileInputStream f1 = new FileInputStream(f);
		byte[] arr = new byte[(int)f.length()];
		f1.read(arr);
		f1.close();
		String imageDataString = encodeImage(arr);
		user.setUserPic(imageDataString);
		user.update();
        return ok();
    }
    
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }
     
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
    
    public static Result getUserPic(String uId) throws IOException{
    	User user = User.find.byId(Long.parseLong(uId));
    	String userPic = user.getUserPic();
    	if(userPic == null){
    		return ok(Play.application().resourceAsStream("user.png")).as(("picture/stream"));
    	}
    	byte[] arr = decodeImage(user.getUserPic());
    	return ok(arr).as(("picture/stream"));
    }
    
    
	public static Result addToWishlist(){
    	User user = Application.getLocalUser(session());
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	Wishlist wishlist = new Wishlist(form);
    	user.addToWishlist(wishlist);
    	return ok(Json.toJson(wishlist));
    }
    
    public static Result loadAllWishlist(){
    	User user = Application.getLocalUser(session());
    	return ok(Json.toJson(user.wishlists));
    }
    
    public static Result removeFromWishlist(){
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	String name = form.get("name");
    	System.out.println("name:"+name);
    	Wishlist wl = Wishlist.findByName(name);
    	User user = Application.getLocalUser(session());
    	user.removeFromWishlist(wl);
    	return ok();
    }
    
    public static Result loadAllMembers(){
    	List<User> all = User.find.all();
    	List<MemberStatus> allMS = new ArrayList<>();
    	for(User user: all){
    		MemberStatus ms = new MemberStatus(); 
        	ms.id = user.id;
        	ms.name = user.getName();
        	ms.status = "offline";
        	allMS.add(ms);
    	}
    	return ok(Json.toJson(allMS));
    }
    
    static final String FILE_PATH = Play.application().configuration().getString("filePath");
    
    private static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null), false, null);
    }
    
    
    public static Result downloadFile() throws IOException{
    	DynamicForm form = DynamicForm.form().bindFromRequest();
    	long cId = Long.parseLong(form.get("id"));
    	String fileString = Chat.find.byId(cId).getAttachement();
    	Date d = new Date();
    	String[] s = fileString.split(",");
    	//fileString = fileString.replace("data:image/jpeg;base64,","");
    	byte[] arr = decodeImage(s[1]);
    	FileOutputStream fos = new FileOutputStream(FILE_PATH + File.separator + "chat_" + d.getTime() + ".jpg");
    	fos.write(arr);
    	fos.flush();
    	fos.close();
    	System.out.println("DONE");
    	return ok();
    }    

}
