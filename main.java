package dungeon.blockynights;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class main extends JavaPlugin implements Listener {

	private Map<String, Boolean> Dungeon = new HashMap<String, Boolean>();
	private Map<String, Boolean> DungeonPlayers = new HashMap<String, Boolean>();
	
	public static Permission perms = null;
	
	private boolean setupPermissions()
	 {
	     RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
	     perms = (Permission)rsp.getProvider();
	     return perms != null;
	  }
	
	public void onEnable() {
		setupPermissions();
		Bukkit.getPluginManager().registerEvents(this, this);
		this.saveDefaultConfig();
		String x = this.getConfig().getString("Dungeon.x");
		String y = this.getConfig().getString("Dungeon.y");
		String z = this.getConfig().getString("Dungeon.z");
		String world = this.getConfig().getString("Dungeon.world");
		String dungeonbutton = x + y + z + world;
		Dungeon.put(dungeonbutton, true);
	}
	public void onDisable() {
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (player.isOp()) {
			if ((cmd.getName().equalsIgnoreCase("Dungeon") && sender instanceof Player) && (args.length == 0)) {
			player.sendMessage("You need arguments");
			}
			if ((cmd.getName().equalsIgnoreCase("Dungeon") && sender instanceof Player) && (args.length == 1) && (args[0].equalsIgnoreCase("setbutton"))) {
				Block block = player.getTargetBlock((Set<Material>) null, 5);
				if (block.getType() == Material.STONE_BUTTON) {
					this.getConfig().set("Dungeon.x",block.getLocation().getX());
					this.getConfig().set("Dungeon.y",block.getLocation().getY());
					this.getConfig().set("Dungeon.z",block.getLocation().getZ());
					this.getConfig().set("Dungeon.world",block.getLocation().getWorld().getName());
					this.saveConfig();
					String dungeonbutton = Double.toString(block.getLocation().getX()) + Double.toString(block.getLocation().getY()) + Double.toString(block.getLocation().getZ()) + block.getLocation().getWorld().getName();
					Dungeon.clear();
					Dungeon.put(dungeonbutton, true);
					player.sendMessage("Dungeon Start set at World:"+block.getLocation().getWorld().getName()+" X:" +block.getLocation().getX()+" Y:" +block.getLocation().getY()+ " Z:" +block.getLocation().getZ());
					
				}
				else { player.sendMessage("You are not looking at a stone button"); }
			}
		}
		if ((cmd.getName().equalsIgnoreCase("Dungeon") && sender instanceof Player) && (args.length == 1) && (args[0].equalsIgnoreCase("end") && (isPlayerDungeonMode(player.getName())))) {
			removeDungeonMode(player);
			player.sendMessage("§bLeaving Dungeon mode. Command restrictions are now gone.");
			Bukkit.getServer().dispatchCommand(player, "spawn");
		}
		return true;
	}

	@EventHandler
	public void onButton(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			Block clicked = event.getClickedBlock();
			if (clicked.getType() == Material.STONE_BUTTON && player.hasPermission("Dungeon.spawn")) {
					double x = event.getClickedBlock().getLocation().getBlockX();
						double y = event.getClickedBlock().getLocation().getBlockY();
						double z = event.getClickedBlock().getLocation().getBlockZ();
						String world = event.getClickedBlock().getLocation().getWorld().getName();
						String dungeonbutton = Double.toString(x) + Double.toString(y) + Double.toString(z) + world;
						if (isDungeonButton(dungeonbutton)) {
							if (!isPlayerDungeonMode(player.getName())) {
							setDungeonMode(player);
						} else { player.sendMessage("§bYou are already in Dungeon mode - to exit type /Dungeon end"); }
				}
			}
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity().getPlayer();
		if (isPlayerDungeonMode(player.getName())) {
			event.setKeepInventory(true);
			event.setKeepLevel(true);
			event.getDrops().clear();
			event.setDroppedExp(0);
			removeDungeonMode(player);
			player.sendMessage("§bYou died while in Dungeon mode, return to dungeon to enter again.");
		}
	}
	
	private void setDungeonMode(Player player) {
		perms.playerAddGroup(player, "event");
		DungeonPlayers.put(player.getName(), true);
		Title.sendTitle(player, "[{text:'Entering Dungeon Mode',color:red}]", "[{text:'You can now enter the dungeon!',color:blue},{text:'',color:green}]", 15, 60, 15);
		player.sendMessage("§bCommands are restricted in Dungeon mode, to exit Dungeon mode either Die or type /Dungeon end - Outside the dungeon area.");
	}
	private void removeDungeonMode(Player player) {
		final Player p = player;
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
            	perms.playerRemoveGroup(p, "event");
            	DungeonPlayers.remove(p.getName());
            }
        }, 20L);
	}
	
	
	 private boolean isDungeonButton(String button) {
		 if (Dungeon.get(button) != null) {
		 return true;
		 }
		 return false;
	}
	 private boolean isPlayerDungeonMode(String player) {
		 if (DungeonPlayers.get(player) != null) {
		 return true;
		 }
		 return false;
	}
}
