package me.erikbolumburu.economyplugin;

import org.bson.Document;
import org.bukkit.inventory.ItemStack;

public class GUISlot {
    public Document doc;
    public ItemStack itemStack;
    public GUISlot(Document pDoc, ItemStack pItemStack){
        doc = pDoc;
        itemStack = pItemStack;
    }
}
