package me.erikbolumburu.economyplugin;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static me.erikbolumburu.economyplugin.AuctionHouseListingsGUI.addEnchantmentsToItem;
import static me.erikbolumburu.economyplugin.EconomyPlugin.instance;
import static org.bukkit.Bukkit.getServer;

public class AuctionHouseYourListingsGUI implements Listener {
    private Inventory inv;
    private List<GUISlot> GUISlots;
    private Player player;

    public AuctionHouseYourListingsGUI(Player pPlayer){
        player = pPlayer;
        inv = Bukkit.createInventory(null, 54, "Your Listings");
        GUISlots = new ArrayList<>();
        try (MongoCursor<Document> cursor = instance.auctionhouse.find(eq(
                "sellerUUID",
                player.getUniqueId())).iterator()
        ){
            while (cursor.hasNext()) {
                Document res = cursor.next();
                ItemStack itemStack = AuctionHouseListingsGUI.createGuiItem(
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
        RemoveListing(GUISlots.get(e.getRawSlot()), player);
    }

    public static void RemoveListing(GUISlot slot, Player player){
        getServer().getPluginManager().registerEvents(
                new ConfirmRemoveListingGUI(player, slot),
                instance
        );
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
