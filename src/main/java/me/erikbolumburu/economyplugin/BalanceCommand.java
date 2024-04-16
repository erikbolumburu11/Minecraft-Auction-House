package me.erikbolumburu.economyplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;

public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            int amount = Integer.parseInt(instance.players.find(eq(
                    "uuid",
                    player.getUniqueId())).first().get("balance").toString()
            );
            player.sendMessage("Bal: " + amount);
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
