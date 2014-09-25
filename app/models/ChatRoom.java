package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * A chat room is an Actor.
 */
public class ChatRoom extends UntypedActor {
    
    // Default room.
    static ActorRef defaultRoom = Akka.system().actorOf(new Props(ChatRoom.class));
    
    // Create a Robot, just for fun.
    static {
        new Robot(defaultRoom);
    }
    
    /**
     * Join the default room.
     */
    public static void join(final String username, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) throws Exception{
        
        // Send the Join message to the room
        String result = (String)Await.result(ask(defaultRoom,new Join(username, out, "message", new Date()), 1000), Duration.create(1, SECONDS));
        
        if("OK".equals(result)) {
            
            // For each event received on the socket,
            in.onMessage(new Callback<JsonNode>() {
               public void invoke(JsonNode event) {
                   
                   // Send a Talk message to the room.
                   defaultRoom.tell(new Talk(username, event.get("text").asText(), event.get("type").asText(), new Date()));
                   
               } 
            });
            
            // When the socket is closed.
            in.onClose(new Callback0() {
               public void invoke() {
                   
                   // Send a Quit message to the room.
                   defaultRoom.tell(new Quit(username,"message", new Date()));
                   
               }
            });
            
        } else {
            
            // Cannot connect, create a Json error.
            ObjectNode error = Json.newObject();
            error.put("error", result);
            
            // Send the error to the socket.
            out.write(error);
            
        }
        
    }
    
    // Members of this room.
    Map<String, WebSocket.Out<JsonNode>> members = new HashMap<String, WebSocket.Out<JsonNode>>();
    
    public void onReceive(Object message) throws Exception {
        
        if(message instanceof Join) {
            
            // Received a Join message
            Join join = (Join)message;
            
            // Check if this username is free.
            if(members.containsKey(join.username)) {
                getSender().tell("This username is already used");
            } else {
                members.put(User.find.byId(User.findByUsername(join.username)).id.toString(), join.channel);
                notifyAll("join", join.username, "has entered the room", join.type, join.time);
                getSender().tell("OK");
            }
            
        } else if(message instanceof Talk)  {
            
            // Received a Talk message
            Talk talk = (Talk)message;
            
            if(!talk.username.equalsIgnoreCase("Robot")){
            	notifyAllTalks("talk", talk.username, talk.text, talk.type, talk.time);
            }       
            
        } else if(message instanceof Quit)  {
            
            // Received a Quit message
            Quit quit = (Quit)message;
            members.remove(User.find.byId(User.findByUsername(quit.username)).id.toString());
            notifyAll("quit", quit.username, "has leaved the room", quit.type, quit.time);
        
        } else {
            unhandled(message);
        }
        
    }
    
    // Send a Json event to all members
    public void notifyAll(String kind, String user, String text, String type, Date time) {
        for(WebSocket.Out<JsonNode> channel: members.values()) {
            
            ObjectNode event = Json.newObject();
            /*event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);
            event.put("type", type);
            event.put("time", time.toString());
            event.put("userId", User.findByUsername(user));*/
            
            ArrayNode m = event.putArray("members");
            for(String u: members.keySet()) {
                m.add(u);
            }
            
            channel.write(event);
        }
    }

    public void notifyAllTalks(String kind, String user, String text, String type, Date d) {
        
    	if(!kind.equalsIgnoreCase("Robot")){
            Chat chat = new Chat();
            chat.message = text;
            chat.messageTime = d;
            chat.userId = User.findByUsername(user);
            chat.save();
        }
    	
    	for(WebSocket.Out<JsonNode> channel: members.values()) {
            
            ObjectNode event = Json.newObject();
            event.put("kind", kind);
            event.put("user", user);
            event.put("message", text);
            event.put("type", type);
            event.put("time", d.toString());
            event.put("userId", User.findByUsername(user));
                        
            ArrayNode m = event.putArray("members");
            for(String u: members.keySet()) {
                m.add(u);
            }
            
            channel.write(event);
        }
    }
    
    
    // -- Messages
    
    public static class Join {
        
        final String username;
        final WebSocket.Out<JsonNode> channel;
        final String type;
        final Date time;
        
        public Join(String username, WebSocket.Out<JsonNode> channel, String type, Date time) {
            this.username = username;
            this.channel = channel;
            this.type = type;
            this.time = time;
        }
        
    }
    
    public static class Talk {
        
        final String username;
        final String text;
        final String type;
        final Date time;
        
        public Talk(String username, String text, String type, Date time) {
            this.username = username;
            this.text = text;
            this.type = type;
            this.time = time;
        }
        
    }
    
    public static class Quit {
        
        final String username;
        final String type;
        final Date time;
        
        public Quit(String username, String type, Date time) {
            this.username = username;
            this.type = type;
            this.time = time;
        }
        
    }
    
}
