package me.erikbolumburu.economyplugin;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

public final class EconomyPlugin extends JavaPlugin {
    public static EconomyPlugin instance;
    public MongoCollection<Document> players;
    public MongoCollection<Document> items;
    public MongoCollection<Document> auctionhouse;
    private MongoDatabase mcserverdb;
    private MongoClient client;
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        System.out.println("Economy Plugin Has Started.");

        //Register Commands
        this.getCommand("bal").setExecutor(new BalanceCommand());
        this.getCommand("givebalance").setExecutor(new GiveBalanceCommand());
        this.getCommand("sell").setExecutor(new SellCommand());
        this.getCommand("ah").setExecutor(new AHCommand());

        //Register Listener
        getServer().getPluginManager().registerEvents(new MyListener(), this);

        connectToMongoDB("localhost", 27017);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("Economy Plugin Has Stopped.");
    }


    public boolean connectToMongoDB(String ip, int port){
        client = new MongoClient(ip, port);
        mcserverdb = client.getDatabase("mcserver");
        players = mcserverdb.getCollection("players");
        items = mcserverdb.getCollection("items");
        auctionhouse = mcserverdb.getCollection("auctionhouse");
        return true;
    }

}
