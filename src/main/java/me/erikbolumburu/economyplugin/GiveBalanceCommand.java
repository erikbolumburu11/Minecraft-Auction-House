package me.erikbolumburu.economyplugin;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;

public class GiveBalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length <= 2){
                player.sendMessage("Not enough arguements. Provide a player and an amount");
            }
            String recieverString = args[0];
            String amount = args[1];
            Document query = instance.players.find(eq("name", recieverString)).first();
            Bson updates = Updates.combine(
                    Updates.set("balance", Integer.parseInt(query.get("balance").toString()) + Integer.parseInt(amount))
            );
            instance.players.updateOne(query, updates);
            player.sendMessage("Paid " + recieverString + " $" + amount);
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }

}
