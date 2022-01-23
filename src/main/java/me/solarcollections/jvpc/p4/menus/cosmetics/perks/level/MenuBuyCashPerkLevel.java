package me.solarcollections.jvpc.p4.menus.cosmetics.perks.level;

import me.solarcollections.jvpc.cash.CashException;
import me.solarcollections.jvpc.cash.CashManager;
import me.solarcollections.jvpc.libraries.menu.PlayerMenu;
import me.solarcollections.jvpc.p4.Main;
import me.solarcollections.jvpc.p4.container.CosmeticsContainer;
import me.solarcollections.jvpc.p4.cosmetics.object.perk.PerkLevel;
import me.solarcollections.jvpc.p4.cosmetics.types.Perk;
import me.solarcollections.jvpc.p4.menus.cosmetics.perks.MenuPerkUpgrade;
import me.solarcollections.jvpc.player.Profile;
import me.solarcollections.jvpc.utils.BukkitUtils;
import me.solarcollections.jvpc.utils.StringUtils;
import me.solarcollections.jvpc.utils.enums.EnumSound;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MenuBuyCashPerkLevel<T extends Perk> extends PlayerMenu {
  
  private String name;
  private T cosmetic;
  private PerkLevel perkLevel;
  private long level;
  private Class<T> cosmeticClass;
  public MenuBuyCashPerkLevel(Profile profile, String name, T cosmetic, PerkLevel perkLevel, long level, Class<T> cosmeticClass) {
    super(profile.getPlayer(), "Confirmar compra", 3);
    this.name = name;
    this.cosmetic = cosmetic;
    this.perkLevel = perkLevel;
    this.level = level;
    this.cosmeticClass = cosmeticClass;
    
    String levelName = " " + (level > 3 ? level == 4 ? "IV" : "V" : StringUtils.repeat("I", (int) level));
    this.setItem(11, BukkitUtils.deserializeItemStack(
        "GOLD_INGOT : 1 : nome>&aConfirmar : desc>&7Comprar \"" + cosmetic.getName() + levelName + "\"\n&7por &6" + StringUtils.formatNumber(perkLevel.getCoins()) + " Coins&7."));
    
    this.setItem(13, BukkitUtils.deserializeItemStack(
        "DIAMOND : 1 : nome>&aConfirmar : desc>&7Comprar \"" + cosmetic.getName() + levelName + "\"\n&7por &b" + StringUtils.formatNumber(perkLevel.getCash()) + " Cash&7."));
    
    this.setItem(15, BukkitUtils.deserializeItemStack("STAINED_GLASS_PANE:14 : 1 : nome>&cCancelar : desc>&7Voltar para a Habilidade " + cosmetic.getName() + "."));
    
    this.register(Main.getInstance());
    this.open();
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent evt) {
    if (evt.getInventory().equals(this.getInventory())) {
      evt.setCancelled(true);
      
      if (evt.getWhoClicked().equals(this.player)) {
        Profile profile = Profile.getProfile(this.player.getName());
        if (profile == null) {
          this.player.closeInventory();
          return;
        }
        
        if (evt.getClickedInventory() != null && evt.getClickedInventory().equals(this.getInventory())) {
          ItemStack item = evt.getCurrentItem();
          
          if (item != null && item.getType() != Material.AIR) {
            if (evt.getSlot() == 11) {
              if (profile.getCoins("sCoreP4") < this.perkLevel.getCoins()) {
                EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                this.player.sendMessage("§cVocê não possui Coins suficientes para completar esta transação.");
                return;
              }
              
              String levelName = " " + (level > 3 ? level == 4 ? "IV" : "V" : StringUtils.repeat("I", (int) level));
              EnumSound.LEVEL_UP.play(this.player, 0.5F, 2.0F);
              profile.removeCoins("sCoreP4", this.perkLevel.getCoins());
              profile.getAbstractContainer("sCoreP4", "cosmetics", CosmeticsContainer.class).setLevel(this.cosmetic, this.level);
              this.player.sendMessage("§aVocê comprou '" + this.cosmetic.getName() + levelName + "'");
              new MenuPerkUpgrade<>(profile, this.name, this.cosmetic, this.cosmeticClass);
            } else if (evt.getSlot() == 13) {
              if (profile.getStats("sCoreProfile", "cash") < this.perkLevel.getCash()) {
                EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
                this.player.sendMessage("§cVocê não possui Cash suficiente para completar esta transação.");
                return;
              }
              
              try {
                String levelName = " " + (level > 3 ? level == 4 ? "IV" : "V" : StringUtils.repeat("I", (int) level));
                CashManager.removeCash(profile, this.perkLevel.getCash());
                profile.getAbstractContainer("sCoreP4", "cosmetics", CosmeticsContainer.class).setLevel(this.cosmetic, this.level);
                this.player.sendMessage("§aVocê comprou '" + this.cosmetic.getName() + levelName + "'");
                EnumSound.LEVEL_UP.play(this.player, 0.5F, 2.0F);
              } catch (CashException ignore) {
              }
              new MenuPerkUpgrade<>(profile, this.name, this.cosmetic, this.cosmeticClass);
            } else if (evt.getSlot() == 15) {
              EnumSound.ENDERMAN_TELEPORT.play(this.player, 0.5F, 1.0F);
              new MenuPerkUpgrade<>(profile, this.name, this.cosmetic, this.cosmeticClass);
            }
          }
        }
      }
    }
  }
  
  public void cancel() {
    HandlerList.unregisterAll(this);
    this.name = null;
    this.cosmetic = null;
    this.perkLevel = null;
    this.level = 0;
    this.cosmeticClass = null;
  }
  
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent evt) {
    if (evt.getPlayer().equals(this.player)) {
      this.cancel();
    }
  }
  
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent evt) {
    if (evt.getPlayer().equals(this.player) && evt.getInventory().equals(this.getInventory())) {
      this.cancel();
    }
  }
}
