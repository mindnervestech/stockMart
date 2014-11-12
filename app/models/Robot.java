package models;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Date;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;

public class Robot {
    
    public Robot(ActorRef chatRoom) {
        
        // Create a Fake socket out for the robot that log events to the console.
        WebSocket.Out<JsonNode> robotChannel = new WebSocket.Out<JsonNode>() {
            
            public void write(JsonNode frame) {
                Logger.of("robot").info(Json.stringify(frame));
            }
            
            public void close() {}
            
        };
        
        // Join the room
        chatRoom.tell(new ChatRoom.Join("Robot", robotChannel, "message", new Date()));
        
        // Make the robot talk every 30 seconds
        Akka.system().scheduler().schedule(
            Duration.create(30, SECONDS),
            Duration.create(30, SECONDS),
            chatRoom,
            new ChatRoom.Talk("Robot", "I'm still alive","message", new Date().getTime(),null),
            Akka.system().dispatcher()
        );
        
    }
    
}
