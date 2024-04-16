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

public class ConfirmRemoveListingGUI implements Listener {
    private Inventory inv;
    private Player player;
    private GUISlot slot;

    public ConfirmRemoveListingGUI(Player pPlayer, GUISlot pSlot){
        player = pPlayer;
        slot = pSlot;
        inv = Bukkit.createInventory(null, 9, "Remove Listing?");
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.RED_STAINED_GLASS, "Do Not Remove Listing"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.GREEN_STAINED_GLASS, "Remove Listing"));
        player.openInventory(inv);
    }


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
        if (item == null) return;

        // View Listings
        if (item.getType().name() == "RED_STAINED_GLASS") {
            player.closeInventory();
        }
        if (item.getType().name() == "GREEN_STAINED_GLASS") {
            player.getInventory().addItem(slot.itemStack);
            player.closeInventory();
            instance.auctionhouse.deleteOne(slot.doc);
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
