package dungeon.blockynights;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title {

	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
	CraftPlayer craftplayer = (CraftPlayer) player;
	PlayerConnection connection = craftplayer.getHandle().playerConnection;
	IChatBaseComponent titleJSON = ChatSerializer.a(title);
	IChatBaseComponent subtitleJSON = ChatSerializer.a(subtitle);
	PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
	PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
	connection.sendPacket(titlePacket);
	connection.sendPacket(subtitlePacket);
	}
}
