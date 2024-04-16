package me.erikbolumburu.economyplugin;

import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;

public class SellCommand implements CommandExecutor {
    //item.getType().name();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            Document query = instance.items.find(eq("itemName", item.getType().name())).first();
            if(query == null){
                player.sendMessage("Item cannot be sold.");
                return false;
            }
            int price = 0;
            int itemsToRemove = 0;
            if(args.length == 0){
                price = Integer.parseInt(query.get("price").toString());
                itemsToRemove = 1;
            }
            else if(args.length == 1){
                price = Integer.parseInt(query.get("price").toString()) * Integer.parseInt(args[0]);
                itemsToRemove = Integer.parseInt(args[0]);
            }
            else{
                player.sendMessage("Too many args");
                return false;
            }
            if(item.getAmount() - itemsToRemove < 0){
                player.sendMessage("You do not have enough items.");
                return false;
            }

            player.sendMessage("Sold " + item.getType().name() + " for $" + price);
            item.setAmount(item.getAmount() - itemsToRemove);
            Document playerDoc = instance.players.find(eq(
                    "uuid",
                    player.getUniqueId()
            )).first();

            int playerBal = Integer.parseInt(playerDoc.get("balance").toString());
            Bson updates = Updates.combine(
                    Updates.set("balance", playerBal + price)
            );
            instance.players.updateOne(playerDoc, updates);
        }
        return true;
    }
}
