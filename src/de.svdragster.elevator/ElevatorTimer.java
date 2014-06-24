package de.svdragster.elevator;

import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.PacketFactory;
import net.canarymod.api.packet.Packet;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;


public class ElevatorTimer extends TimerTask {
	
	private Player player;
	private int direction;
	List<BlockType> exceptions;
	
	public ElevatorTimer() {
		exceptions = Arrays.asList(new BlockType[] {
			BlockType.WoodenDoor, BlockType.IronDoor, BlockType.WoodenButton, BlockType.StoneButton, BlockType.FenceGate, BlockType.Water, BlockType.WaterFlowing, BlockType.Lava, BlockType.LavaFlowing,
			BlockType.Ladder, BlockType.Torch, BlockType.Vines, BlockType.SpiderWeb, BlockType.Trapdoor, BlockType.Lever, BlockType.RedstoneTorchOn, BlockType.RedstoneTorchOff, BlockType.Tripwire,
			BlockType.TripwireHook, BlockType.Air
		});
	}
	
	@Override
	public void run() {
		int d = 1;
		if (direction <= 0) {
			d = -1;
		}
		//player.moveEntity(0, velocity, 0);
		player.teleportTo(player.getX(), player.getY()+d, player.getZ(), player.getPitch(), player.getRotation());
		Block block = player.getWorld().getBlockAt((int) player.getX(), (int)  player.getY()+1, (int)  player.getZ());
		if (!exceptions.contains(block.getType()) || player.getY() >= ElevatorListener.WorldHeight || player.getY() < 5) {
			PacketFactory factory = Canary.factory().getPacketFactory();
			Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()-1, (int) player.getZ(), 20, 0);
			player.sendPacket(packet);
			this.cancel();
			player.teleportTo(player.getX(), player.getY()+1, player.getZ(), player.getPitch(), player.getRotation());
		}
	}

	public void setPlayer(Player _player) {
		player = _player;
	}
	
	public void setDirection(int _direction) {
		direction = _direction;
	}
}
