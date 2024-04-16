package me.erikbolumburu.economyplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;
import static me.erikbolumburu.economyplugin.MyListener.getDocument;
import static org.apache.commons.text.WordUtils.capitalizeFully;
import static org.bukkit.Bukkit.getServer;

public class AHCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // /ah
            if(args.length == 0){
                getServer().getPluginManager().registerEvents(new AuctionHouseMenuGUI(player), instance);
                return true;
            }
            // /ah sell [price]
            if(args[0].equalsIgnoreCase("sell")){
                // Get item in hand
                // -----------------
                ItemStack item = player.getInventory().getItemInMainHand();

                // Set itemName
                // ----------------
                String itemName;
                if(item.getItemMeta().hasDisplayName()){
                    itemName = item.getItemMeta().getDisplayName();
                }
                else{
                    itemName = item.getType().name().replace("_", " ");
                    itemName = capitalizeFully(itemName);
                }

                // Set price
                // ---------------
                int price = Integer.parseInt(args[1]);

                // Get enchantments
                // ---------------
                Map<Enchantment, Integer> enchantmentIntegerMap = item.getEnchantments();
                Map<String, Integer> enchantments = new HashMap<String, Integer>();
                for (var entry : enchantmentIntegerMap.entrySet()) {
                    enchantments.put(entry.getKey().getKey().toString(), entry.getValue());
                }


                // Create Listing
                // ----------------
                DBObject listing = new BasicDBObject("name", itemName)
                        .append("price", price)
                        .append("materialName", item.getType().name())
                        .append("quantity", item.getAmount())
                        .append("sellerUUID", player.getUniqueId())
                        .append("enchantments", enchantments);
                player.getInventory().removeItem(item);
                instance.auctionhouse.insertOne(getDocument(listing));
            }


        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }

}
