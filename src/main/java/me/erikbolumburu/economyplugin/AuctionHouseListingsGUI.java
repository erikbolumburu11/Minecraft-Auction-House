package me.erikbolumburu.economyplugin;

import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.AuctionHouseYourListingsGUI.RemoveListing;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;

public class AuctionHouseListingsGUI implements Listener {
    private Inventory inv;
    private List<GUISlot> GUISlots;
    private Player player;
    public AuctionHouseListingsGUI(Player pPlayer){
        player = pPlayer;
        inv = Bukkit.createInventory(null, 54, "Auction House");
        GUISlots = new ArrayList<GUISlot>();
        try (MongoCursor<Document> cursor = instance.auctionhouse.find().iterator()) {
            while (cursor.hasNext()) {
                Document res = cursor.next();
                ItemStack itemStack = createGuiItem(
                        Material.getMaterial(res.get("materialName").toString()),
                        res.get("name").toString(),
                        res,
                        "Price: $" + res.get("price").toString()
                );

                itemStack = addEnchantmentsToItem(itemStack, res);
                itemStack.setAmount(Integer.parseInt(res.get("quantity").toString()));
                inv.addItem(itemStack);
                GUISlots.add(new GUISlot(res, itemStack));
            }
        }
        player.openInventory(inv);
    }

    public static ItemStack createGuiItem(final Material material, final String name, Document doc, final String... lore) {
        ItemStack item = new ItemStack(material, Integer.parseInt(doc.get("quantity").toString()));
        ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack addEnchantmentsToItem(ItemStack item, Document doc){
        Map<String, Integer> enchantments = (Map<String, Integer>) doc.get("enchantments");
        for (var entry : enchantments.entrySet()) {
            item.addEnchantment(Enchantment.getByKey(NamespacedKey.fromString(entry.getKey())), entry.getValue());
        }
        return item;
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
        if(GUISlots.get(e.getRawSlot()) == null) return;
        PurchaseItem(GUISlots.get(e.getRawSlot()));
    }

    public boolean PurchaseItem(GUISlot slot){
        Document buyerDoc = instance.players.find(eq("uuid", player.getUniqueId())).first();
        Document sellerDoc = instance.players.find(eq("uuid", slot.doc.get("sellerUUID"))).first();
        if(buyerDoc.get("name").equals(sellerDoc.get("name"))){
            RemoveListing(slot, player);
            return false;
        }
        // Check price
        // ---------------
        int price = Integer.parseInt(slot.doc.get("price").toString());
        int playerBalance = Integer.parseInt(buyerDoc.get("balance").toString());
        if(price > playerBalance) return false;
        player.getInventory().addItem(slot.itemStack); // Add item to player
        player.closeInventory(); // Close auction house

        // Subtract Funds from player
        // --------------------------
        Bson buyerUpdates = Updates.combine(
                Updates.set("balance", playerBalance - price)
        );
        Bson sellerUpdates = Updates.combine(
                Updates.set("balance", Integer.parseInt(sellerDoc.get("balance").toString()) + price)
        );
        instance.players.updateOne(buyerDoc, buyerUpdates);
        instance.players.updateOne(sellerDoc, sellerUpdates);
        instance.auctionhouse.deleteOne(slot.doc); // Delete item from auction house db
        return true;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
