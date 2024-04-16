package me.erikbolumburu.economyplugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.erikbolumburu.economyplugin.AuctionHouseListingsGUI.addEnchantmentsToItem;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;
import static me.erikbolumburu.economyplugin.MyListener.getDocument;
import static org.apache.commons.text.WordUtils.capitalizeFully;
import static org.bukkit.Bukkit.getServer;

public class AuctionHouseCreateListingGUI implements Listener {
    private Inventory inv;
    private Player player;
    private ItemStack selectedItem;
    private ItemStack playerSelectedItem;
    private ItemMeta selectedItemMeta;
    private int itemPrice;

    public AuctionHouseCreateListingGUI(Player pPlayer){
        player = pPlayer;
        inv = Bukkit.createInventory(null, 9, "Create Listing");

        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.REDSTONE, "Decrease $100"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.REDSTONE_BLOCK, "Decrease $1000"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.ITEM_FRAME, "Select Item"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.EMERALD_BLOCK, "Increase $1000"));
        inv.addItem(AuctionHouseListingsGUI.createGuiItem(Material.EMERALD, "Increase $100"));
        inv.setItem(7, AuctionHouseListingsGUI.createGuiItem(Material.RED_STAINED_GLASS, "Back"));
        inv.setItem(8, AuctionHouseListingsGUI.createGuiItem(Material.GREEN_STAINED_GLASS, "Create Listing"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        // Using slots click is a best option for your inventory click's
        ItemStack item;

        // NEW INVENTORY CLICK
        if (e.getRawSlot() <= 8) {
            item = inv.getItem(e.getRawSlot());
            if (item == null) return;
            if (item.getType().name() == "REDSTONE") {
                itemPrice -= 100;
                if (itemPrice < 0) itemPrice = 0;
            }
            if (item.getType().name() == "REDSTONE_BLOCK") {
                itemPrice -= 1000;
                if (itemPrice < 0) itemPrice = 0;
            }
            if (item.getType().name() == "EMERALD") {
                itemPrice += 100;
            }
            if (item.getType().name() == "EMERALD_BLOCK") {
                itemPrice += 1000;
            }
            if (item.getType().name() == "RED_STAINED_GLASS") {
                getServer().getPluginManager().registerEvents(
                        new AuctionHouseMenuGUI(player), instance
                );
            }
            // CREATE LISTING
            // -----------------
            if (item.getType().name() == "GREEN_STAINED_GLASS") {
                // SET AND FORMAT NAME OF ITEM
                // ------------------------------
                player.closeInventory();
                String itemName;
                if(selectedItem.getItemMeta().hasDisplayName()){
                    itemName = selectedItem.getItemMeta().getDisplayName();
                }
                else{
                    itemName = selectedItem.getType().name().replace("_", " ");
                    itemName = capitalizeFully(itemName);
                }

                // --------------------------------------------------------
                Map<Enchantment, Integer> enchantmentIntegerMap = selectedItem.getEnchantments();
                Map<String, Integer> enchantments = new HashMap<String, Integer>();
                for (var entry : enchantmentIntegerMap.entrySet()) {
                    enchantments.put(entry.getKey().getKey().toString(), entry.getValue());
                }

                player.getInventory().removeItem(playerSelectedItem);
                DBObject listing = new BasicDBObject("name", itemName)
                        .append("price", itemPrice)
                        .append("materialName", selectedItem.getType().name())
                        .append("quantity", selectedItem.getAmount())
                        .append("sellerUUID", player.getUniqueId())
                        .append("enchantments", enchantments);
                instance.auctionhouse.insertOne(getDocument(listing));
            }
            selectedItem.setItemMeta(null);
            selectedItem.setItemMeta(UpdateSelectedItem());
        }
        // PLAYER INVENTORY CLICK
        else {
            item = player.getInventory().getItem(e.getSlot());
            playerSelectedItem = item;
            if (item == null) return;
            item.addEnchantments(item.getEnchantments());
            selectedItemMeta = item.getItemMeta();
            inv.setItem(2, item);
            selectedItem = inv.getItem(2);
            selectedItem.setItemMeta(selectedItemMeta);
            selectedItem.setItemMeta(null);
            selectedItem.setItemMeta(UpdateSelectedItem());
        }
    }

    public ItemMeta UpdateSelectedItem(){
        List<String> lore = new ArrayList<>();
        lore.add("$" + itemPrice);
        ItemMeta meta = selectedItemMeta;
        meta.setLore(lore);
        return meta;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

}
