package eu.smashmc.halloween;

import eu.smashmc.api.core.packet.PacketUtil;
import eu.smashmc.lib.bukkit.NmsUtil;
import eu.smashmc.lib.bukkit.world.location.Locations;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class HalloweenPlugin extends JavaPlugin implements Listener {

	private static final int DIMENSION = -1;

	@Override
	public void onEnable() {
		Bukkit.getScheduler().runTaskLater(this, () -> {
			setNether(Locations.spawn());
		}, 50);
		Bukkit.getPluginManager().registerEvents(this, this);
		PacketUtil.listenPacket(PacketPlayOutRespawn.class, event -> {
			PacketPlayOutRespawn respawn = event.getPacket();
			if (!event.getPlayer().getWorld().equals(Locations.spawn().getWorld())) {
				return;
			}
			try {
				FieldUtils.writeField(respawn, "a", DIMENSION, true);
				event.setPacket(respawn);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
		super.onEnable();
	}

	private void setNether(Location spawn) {
		for (int x = -100; x < 100; x++) {
			for (int y = -100; y < 100; y++) {
				spawn.clone().add(x, 0, y).getBlock().setBiome(Biome.HELL);
			}
		}
	}

	@EventHandler
	public void onJOin(PlayerJoinEvent event) {
		var player = event.getPlayer();
		if (!player.getWorld().equals(Locations.spawn().getWorld())) {
			return;
		}
		// respawn player to apply the dimension
		NmsUtil.respawnPlayerClientSide(player);
	}
}
