package me.erikbolumburu.economyplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;

public class MyListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        System.out.println("player joined");
        if(instance.players.find(eq("uuid", event.getPlayer().getUniqueId())).first() == null){
            storePlayer(event.getPlayer().getUniqueId(), event.getPlayer().getDisplayName(), 100);
        }
        else{
            System.out.println("User Exists In DB");
        }
    }

    public static void storePlayer(UUID uuid, String name, long balance){
        DBObject player = new BasicDBObject("uuid", uuid)
                .append("name", name)
                .append("balance", balance);
        instance.players.insertOne(getDocument(player));
    }

    public static Document getDocument(DBObject obj){
        if(obj == null) return null;
        return new Document(obj.toMap());
    }
}
