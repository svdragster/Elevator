package de.svdragster.elevator;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
/*import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;*/
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.ComplexBlock;
import net.canarymod.api.world.blocks.Sign;
import net.canarymod.chat.Colors;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockLeftClickHook;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.player.SignChangeHook;
import net.canarymod.plugin.PluginListener;

public class ElevatorListener implements PluginListener {
  
	public static final String PERMISSION_PLACE = "elevator.place";
	public static final String PERMISSION_USE = "elevator.use";
	public static final String SIGN_ELEVATOR = "[Elevator]";
	public static final int TP_EYE = 1; //Teleport the player, so the Sign is at the height of the eyes of the player.
	public static final int TP_BELOW = 2; //Teleport the player, so the Sign is at the height of the players legs or feet.
	public static final int TP_CANCEL = 0; //Cancel the teleport.
	public int WorldHeight = Canary.getServer().getDefaultWorld().getHeight();
	boolean AlreadyTeleported = false;
	
	public void cast(String string) {
		Canary.getServer().broadcastMessage(string);
	}
	
	public void CheckForElevator(Sign sign, String direction, Player player) {
		if (direction == "up") {
			int SignY = sign.getY();
			for (int i=SignY+1; i<=WorldHeight; i++) {
				//Block RelativeBlock = sign.getBlock().getPosition().;//getRelative(sign.getBlock().getX(), i, sign.getBlock().getZ());
				int PositionX = sign.getX(), PositionY = i, PositionZ = sign.getZ();
				Block NeuerBlock = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY, PositionZ);
				if (NeuerBlock.getType().equals(BlockType.WallSign)) {
					ComplexBlock ComplexBlock = Canary.getServer().getDefaultWorld().getComplexBlockAt(PositionX, PositionY, PositionZ);
					Sign OtherSign = (Sign) ComplexBlock;
					if (OtherSign.getTextOnLine(1).equals(Colors.WHITE + SIGN_ELEVATOR)) {
						Block BelowSign = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY-1, PositionZ);
						Block AboveSign = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY+1, PositionZ);
						if (BelowSign.isAir()) {
							//TP_EYE
							player.teleportTo(PositionX+0.5, PositionY-1, PositionZ+0.5, player.getPitch(), player.getRotation());
							if (!OtherSign.getTextOnLine(0).isEmpty()) {
								player.message(Colors.LIGHT_GREEN + "Welcome to level " + OtherSign.getTextOnLine(0));
							}
							return;
						} else {
							if (AboveSign.isAir()) {
								//return TP_BELOW;
								player.teleportTo(PositionX+0.5, PositionY, PositionZ+0.5, player.getPitch(), player.getRotation());
								if (!OtherSign.getTextOnLine(0).isEmpty()) {
									player.message(Colors.LIGHT_GREEN + "Welcome to level " + OtherSign.getTextOnLine(0));
								}
								return;
							} else {
								player.message(Colors.LIGHT_RED + "You would be obstructed and die from the pain!");
							}
						}
					}
				}
			}
		} else {
			if (direction == "down") {
				int SignY = sign.getY();
				for (int i=SignY-1; i>1; i--) {
					//Block RelativeBlock = sign.getBlock().getPosition().;//getRelative(sign.getBlock().getX(), i, sign.getBlock().getZ());
					int PositionX = sign.getX(), PositionY = i, PositionZ = sign.getZ();
					Block NeuerBlock = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY, PositionZ);
					if (NeuerBlock.getType().equals(BlockType.WallSign)) {
						ComplexBlock ComplexBlock = Canary.getServer().getDefaultWorld().getComplexBlockAt(PositionX, PositionY, PositionZ);
						Sign OtherSign = (Sign) ComplexBlock;
						if (OtherSign.getTextOnLine(1).equals(Colors.WHITE + SIGN_ELEVATOR)) {
							Block BelowSign = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY-1, PositionZ);
							Block AboveSign = Canary.getServer().getDefaultWorld().getBlockAt(PositionX, PositionY+1, PositionZ);
							if (BelowSign.isAir()) {
								//TP_EYE
								player.teleportTo(PositionX+0.5, PositionY-1, PositionZ+0.5, player.getPitch(), player.getRotation());
								if (!OtherSign.getTextOnLine(0).isEmpty()) {
									player.message(Colors.LIGHT_GREEN + "Welcome to level " + OtherSign.getTextOnLine(0));
								}
								return;
							} else {
								if (AboveSign.isAir()) {
									//return TP_BELOW;
									player.teleportTo(PositionX+0.5, PositionY, PositionZ+0.5, player.getPitch(), player.getRotation());
									if (!OtherSign.getTextOnLine(0).isEmpty()) {
										player.message(Colors.LIGHT_GREEN + "Welcome to level " + OtherSign.getTextOnLine(0));
									}
									return;
								} else {
									player.message(Colors.LIGHT_RED + "You would be obstructed and die from the pain!");
								}
							}
						}
					}
				}
			}
		}
	}
	
	@HookHandler
	public void onBlockRightClick(BlockRightClickHook hook) {
		if (hook.getBlockClicked().getType().equals(BlockType.WallSign)) {
			if(hook.getPlayer().hasPermission(PERMISSION_USE)) {
				ComplexBlock MyComplexBlock = Canary
						.getServer()
						.getDefaultWorld()
						.getComplexBlockAt(hook.getBlockClicked().getX(),
								hook.getBlockClicked().getY(),
								hook.getBlockClicked().getZ());
				Sign MySign = (Sign) MyComplexBlock;
				String SignSecondLine = MySign.getTextOnLine(1);
				String SignThirdLine = MySign.getTextOnLine(2);
				if (SignSecondLine.equals(Colors.WHITE + SIGN_ELEVATOR)) {
					if (SignThirdLine.equalsIgnoreCase("down")) {
						CheckForElevator(MySign, "down", hook.getPlayer());
					} else {
						CheckForElevator(MySign, "up", hook.getPlayer());
					}
					// CheckForElevator(MySign, "up", hook.getPlayer());
				}
			}
		}
	}
	
	@HookHandler
	public void onBlockLeftClick(BlockLeftClickHook hook) {
		//cast("isSneaking: " + hook.getPlayer().isSneaking());
		/*ItemFactory MyItemFactory = Canary.factory().getItemFactory();
		Item HoldingItem = hook.getPlayer().getItemHeld();
		Item WantedItem =  MyItemFactory.newItem(264);*/
		//if (!HoldingItem.equals(WantedItem)) {
		//if (!hook.getPlayer().getItemHeld().equals(ItemType.Diamond)) {
		if (hook.getBlock().getType().equals(BlockType.WallSign)) {
			if (hook.getPlayer().hasPermission(PERMISSION_USE)) {
				ComplexBlock MyComplexBlock = Canary
						.getServer()
						.getDefaultWorld()
						.getComplexBlockAt(hook.getBlock().getX(),
								hook.getBlock().getY(), hook.getBlock().getZ());
				Sign MySign = (Sign) MyComplexBlock;
				String SignSecondLine = MySign.getTextOnLine(1);
				String SignThirdLine = MySign.getTextOnLine(2);
				if (SignSecondLine.equals(Colors.WHITE + SIGN_ELEVATOR)) {
					if (SignThirdLine.equalsIgnoreCase("up")) {
						CheckForElevator(MySign, "up", hook.getPlayer());
					} else {
						CheckForElevator(MySign, "down", hook.getPlayer());
					}
					hook.setCanceled();
				}
				MySign.update();
			}
		}
	}
	
	@HookHandler
	public void onSignChange(SignChangeHook hook) {
		Sign ChangedSign = hook.getSign();
		String SignSecondLine = ChangedSign.getTextOnLine(1);
		if (ChangedSign.isWallSign()) {
			if (hook.getPlayer().hasPermission(PERMISSION_PLACE)) {
				if (SignSecondLine.equals(SIGN_ELEVATOR)) {
					ChangedSign.setTextOnLine(Colors.WHITE + SIGN_ELEVATOR, 1);
					ChangedSign.update();
					hook.getPlayer().message(Colors.GREEN + "Successfully created new Elevator Sign!");
				}
			}
		} else {
			if (SignSecondLine.equals(SIGN_ELEVATOR)) {
				hook.getPlayer().message(Colors.LIGHT_RED + "The sign needs to be a Wall Sign!");
			}
		}
	}
}
