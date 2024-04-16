package me.erikbolumburu.economyplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;
import static org.bukkit.Bukkit.getServer;

public class AuctionHouseMenuGUI implements Listener {
    private Inventory inv;
    private Player player;

    public AuctionHouseMenuGUI(Player pPlayer){
        player = pPlayer;
        inv = Bukkit.createInventory(null, 9, "Auction House");
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.PAPER, "View Listings"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.BOOK, "Your Listings"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.EMERALD, "List Item"));
        player.openInventory(inv);
    }


    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        ItemStack item = inv.getItem(e.getRawSlot());
        if(item == null) return;

        // View Listings
        if(item.getType().name() == "PAPER"){
            player.closeInventory();
            getServer().getPluginManager().registerEvents(
                    new AuctionHouseListingsGUI(player),
                    instance
            );
        }
        if(item.getType().name() == "BOOK"){
            player.closeInventory();
            getServer().getPluginManager().registerEvents(
                    new AuctionHouseYourListingsGUI(player),
                    instance
            );
        }
        if(item.getType().name() == "EMERALD"){
            player.closeInventory();
            getServer().getPluginManager().registerEvents(
                    new AuctionHouseCreateListingGUI(player),
                    instance
            );
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
