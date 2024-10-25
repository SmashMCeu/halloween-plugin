package eu.smashmc.halloween;

import eu.smashmc.api.core.packet.PacketUtil;
import eu.smashmc.lib.bukkit.world.location.Locations;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class HalloweenPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		setNether(Locations.spawn());
		Bukkit.getPluginManager().registerEvents(this, this);
		PacketUtil.listenPacket(PacketPlayOutRespawn.class, event -> {
			PacketPlayOutRespawn respawn = event.getPacket();
			if (!event.getPlayer().getWorld().equals(Locations.spawn().getWorld())) {
				return;
			}
			try {
				FieldUtils.writeField(respawn, "a", -1, true);
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
		Chunk chunk = player.getLocation()
				.getChunk();
		for (int x = -10; x < 10; x++) {
			for (int z = -10; z < 10; z++) {
				player.getWorld()
						.refreshChunk(chunk.getX() + x, chunk.getZ() + z);
			}
		}
	}
}
