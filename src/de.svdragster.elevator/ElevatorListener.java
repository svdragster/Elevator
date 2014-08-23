package de.svdragster.elevator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.PacketFactory;
import net.canarymod.api.packet.Packet;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.world.blocks.Sign;
import net.canarymod.api.world.blocks.TileEntity;
import net.canarymod.chat.Colors;
import net.canarymod.chat.TextFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.BlockLeftClickHook;
import net.canarymod.hook.player.BlockRightClickHook;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.PlayerMoveHook;
import net.canarymod.hook.player.SignChangeHook;
import net.canarymod.plugin.PluginListener;

public class ElevatorListener implements PluginListener {
	
	public static final String PERMISSION_PLACE = "elevator.place";
	public static final String PERMISSION_USE = "elevator.use";
	public static final String PERMISSION_DESTROY = "elevator.destroy";
	public static final String PERMISSION_UPDATE = "elevator.checkupdates";
	public static final String SIGN_ELEVATOR = "[Elevator]";
	public static final int TP_EYE = 1; //Teleport the player, so the Sign is at the height of the eyes of the player.
	public static final int TP_BELOW = 2; //Teleport the player, so the Sign is at the height of the players legs or feet.
	public static final int TP_CANCEL = 0; //Cancel the teleport.
	private static final String USER_AGENT = "Minecraft Server " + Canary.getServer().getName();
	public static final String VERSION = new Elevator().getVersion();
	private static final String DIR = "config/elevator/";
	private static final String FILE = DIR + "elevator.properties";
	public static boolean CheckForUpdates = false;
	public static boolean Message_WelcomeToLevel = false;
	public static boolean Message_CheckForUpdates = false;
	public static boolean Message_NoUpdateAvailable = false;
	public static boolean Message_UpdateAvailable = false;
	public static int WorldHeight = Canary.getServer().getDefaultWorld().getHeight();
	
	public static boolean isNumeric(String str) {  
		try {  
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);  
		} catch (NumberFormatException nfe) {  
			return false;  
		}  
		return true;  
	}
	
	public void cast(String string) {
		Canary.getServer().broadcastMessage(string);
	}
	
	public void createFiles() {
		File file = new File(FILE);
		File dir = new File(DIR);
		
		Properties props = new Properties();
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			props.load(new FileInputStream(FILE));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
	        FileOutputStream out = new FileOutputStream(FILE);
	        
	        if (!props.containsKey("CheckForUpdates")) {
	        	props.setProperty("CheckForUpdates", "true");
	        }
	        if (!props.containsKey("Message_WelcomeToLevel")) {
	        	props.setProperty("Message_WelcomeToLevel", "true");
	        }
	        if (!props.containsKey("Message_CheckForUpdates")) {
	        	props.setProperty("Message_CheckForUpdates", "true");
	        }
	        if (!props.containsKey("Message_NoUpdateAvailable")) {
	        	props.setProperty("Message_NoUpdateAvailable", "true");
	        }
	        if (!props.containsKey("Message_UpdateAvailable")) {
	        	props.setProperty("Message_UpdateAvailable", "true");
	        }
	        props.store(out, null);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getProps() {
		Properties prop = new Properties();
    	try {
               //load a properties file
    		prop.load(new FileInputStream(FILE));
               //get the property value
    		String checkUpdates = prop.getProperty("CheckForUpdates");
    		String messageLevel = prop.getProperty("Message_WelcomeToLevel");
    		String messageCheckUpdates = prop.getProperty("Message_CheckForUpdates");
    		String messageNoUpdateAvailable = prop.getProperty("Message_NoUpdateAvailable");
    		String messageUpdateAvailable = prop.getProperty("Message_UpdateAvailable");
    		if (checkUpdates.equalsIgnoreCase("true")) {
    			CheckForUpdates = true;
    		}
    		if (messageLevel.equalsIgnoreCase("true")) {
    			Message_WelcomeToLevel = true;
    		}
    		if (messageCheckUpdates.equalsIgnoreCase("true")) {
    			Message_CheckForUpdates = true;
    		}
    		if (messageUpdateAvailable.equalsIgnoreCase("true")) {
    			Message_UpdateAvailable = true;
    		}
    		if (messageNoUpdateAvailable.equalsIgnoreCase("true")) {
    			Message_NoUpdateAvailable = true;
    		}
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	public void CheckForElevator(Sign sign, String direction, Player player) {
		if (direction == "up") {
			int SignY = sign.getY();
			for (int i=SignY+1; i<=WorldHeight; i++) {
				int PositionX = sign.getX(), PositionY = i, PositionZ = sign.getZ();
				Block NeuerBlock = player.getWorld().getBlockAt(PositionX, PositionY, PositionZ);
				if (NeuerBlock.getType().equals(BlockType.WallSign)) {
					TileEntity ComplexBlock = player.getWorld().getTileEntityAt(PositionX, PositionY, PositionZ);
					Sign OtherSign = (Sign) ComplexBlock;
					if (OtherSign.getTextOnLine(1).equals(Colors.WHITE + SIGN_ELEVATOR)) {
						Block BelowSign = player.getWorld().getBlockAt(PositionX, PositionY-1, PositionZ);
						Block AboveSign = player.getWorld().getBlockAt(PositionX, PositionY+1, PositionZ);
						if (BelowSign.isAir()) {
							//TP_EYE
							player.teleportTo(PositionX+0.5, PositionY-1, PositionZ+0.5, player.getPitch(), player.getRotation());
							if (!OtherSign.getTextOnLine(0).isEmpty()) {
								if (Message_WelcomeToLevel) {
									player.message(Colors.LIGHT_GREEN + "Welcome to level " + OtherSign.getTextOnLine(0));
								}
							}
							return;
						} else {
							if (AboveSign.isAir()) {
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
					Block NeuerBlock = player.getWorld().getBlockAt(PositionX, PositionY, PositionZ);
					if (NeuerBlock.getType().equals(BlockType.WallSign)) {
						TileEntity ComplexBlock = player.getWorld().getTileEntityAt(PositionX, PositionY, PositionZ);
						Sign OtherSign = (Sign) ComplexBlock;
						if (OtherSign.getTextOnLine(1).equals(Colors.WHITE + SIGN_ELEVATOR)) {
							Block BelowSign = player.getWorld().getBlockAt(PositionX, PositionY-1, PositionZ);
							Block AboveSign = player.getWorld().getBlockAt(PositionX, PositionY+1, PositionZ);
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
			if (hook.getPlayer().hasPermission(PERMISSION_USE)) {
				TileEntity MyComplexBlock = hook.getBlockClicked().getWorld().getTileEntityAt(hook.getBlockClicked().getX(), hook.getBlockClicked().getY(), hook.getBlockClicked().getZ());
				Sign MySign = (Sign) MyComplexBlock;
				String SignSecondLine = MySign.getTextOnLine(1);
				String SignThirdLine = MySign.getTextOnLine(2);
				String SignFourthLine = TextFormat.removeFormatting(MySign.getTextOnLine(3));
				if (!SignFourthLine.isEmpty()) {
					for (int i=(int) MySign.getY()+1; i<=WorldHeight; i++) {
						if (hook.getPlayer().getWorld().getBlockAt((int) hook.getPlayer().getX(), i, (int) hook.getPlayer().getZ()).getType().equals(BlockType.WallSign)) {
							int v = 1;//Integer.parseInt(SignFourthLine);
							ElevatorTimer task = new ElevatorTimer();
							task.setPlayer(hook.getPlayer());
							task.setDirection(1);
					    	Timer timer = new Timer();
					    	timer.schedule(task, 100, v*100);
					    	return;
						}
					}			
				} else {
					if (SignSecondLine.equals(Colors.WHITE + SIGN_ELEVATOR)) {
						if (SignThirdLine.equalsIgnoreCase("down")) {
							CheckForElevator(MySign, "down", hook.getPlayer());
						} else {
							CheckForElevator(MySign, "up", hook.getPlayer());
						}
					}
				}
			}
		}
	}
	
	@HookHandler
	public void onBlockLeftClick(BlockLeftClickHook hook) {
		if (hook.getBlock().getType() != null) {
			if (hook.getBlock().getType().equals(BlockType.WallSign)) {
				if (hook.getPlayer().isSneaking()) {
					Sign MySign = (Sign) hook.getBlock().getTileEntity();
					String SignSecondLine = MySign.getTextOnLine(1);
					if (SignSecondLine.equals(Colors.WHITE + SIGN_ELEVATOR)) {
						if (hook.getPlayer().hasPermission(PERMISSION_DESTROY)) {
							hook.getPlayer().message(Colors.GREEN + "Sign destroyed.");
							return;
						} else {
							hook.getPlayer().message(Colors.LIGHT_RED + "You don't have the permission to destroy an elevator sign.");
							hook.setCanceled();
						}
					}
				}
				Sign MySign = (Sign) hook.getBlock().getTileEntity();
				String SignSecondLine = MySign.getTextOnLine(1);
				if (SignSecondLine.equals(Colors.WHITE + SIGN_ELEVATOR)) {
					if (hook.getPlayer().hasPermission(PERMISSION_USE)) {
						hook.setCanceled();
						String SignThirdLine = MySign.getTextOnLine(2);
						String SignFourthLine = TextFormat.removeFormatting(MySign.getTextOnLine(3));
						if (isNumeric(SignFourthLine)) {
							for (int i=(int) MySign.getY()-1; i>0; i--) {
								if (hook.getPlayer().getWorld().getBlockAt((int) hook.getPlayer().getX(), i, (int) hook.getPlayer().getZ()).getType().equals(BlockType.WallSign)) {
									PacketFactory factory = Canary.factory().getPacketFactory();
									Player player = hook.getPlayer();
									Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()-1, (int) player.getZ(), 0, 0);
									player.sendPacket(packet);
									int v = Integer.parseInt(SignFourthLine);
									ElevatorTimer task = new ElevatorTimer();
									task.setPlayer(player);
									task.setDirection(-1);
							    	Timer timer = new Timer();
							    	timer.schedule(task, 100, v*100);
							    	return;
								}
							}
						} else {
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
	
	@HookHandler
	public void onPlayerMove(PlayerMoveHook hook) {
		Player player = hook.getPlayer();
		if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY()+1, (int) player.getZ()).getType().equals(BlockType.WallSign)) {
			Sign sign = (Sign) player.getWorld().getTileEntityAt((int) player.getX(), (int) player.getY()+1, (int) player.getZ());
			String str = TextFormat.removeFormatting(sign.getTextOnLine(3));
			if (isNumeric(str)) {
				PacketFactory factory = Canary.factory().getPacketFactory();
				Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()-1, (int) player.getZ(), 20, 0);
				player.sendPacket(packet);
			}
		}
		if (player.getWorld().getBlockAt((int) hook.getFrom().getX(), (int) hook.getFrom().getY()+1, (int) hook.getFrom().getZ()).getType().equals(BlockType.WallSign)) {
			Sign sign = (Sign) player.getWorld().getTileEntityAt((int) hook.getFrom().getX(), (int) hook.getFrom().getY()+1, (int) hook.getFrom().getZ());
			String str = TextFormat.removeFormatting(sign.getTextOnLine(3));
			if (isNumeric(str)) {
				PacketFactory factory = Canary.factory().getPacketFactory();
				Packet packet = factory.blockChange((int) hook.getFrom().getX(), (int) hook.getFrom().getY()-1, (int) hook.getFrom().getZ(), 0, 0);
				player.sendPacket(packet);
			}
		}
		if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY(), (int) player.getZ()).getType().equals(BlockType.WallSign)) {
			PacketFactory factory = Canary.factory().getPacketFactory();
			Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()-2, (int) player.getZ(), 0, 0);
			player.sendPacket(packet);
		}
		if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY()+2, (int) player.getZ()).getType().equals(BlockType.WallSign)) {
			PacketFactory factory = Canary.factory().getPacketFactory();
			Packet packet = factory.blockChange((int) player.getX(), (int) player.getY(), (int) player.getZ(), 0, 0);
			player.sendPacket(packet);
		}
		if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY()+3, (int) player.getZ()).getType().equals(BlockType.WallSign)) {
			PacketFactory factory = Canary.factory().getPacketFactory();
			Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()+1, (int) player.getZ(), 0, 0);
			player.sendPacket(packet);
		}
		if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY()+1, (int) player.getZ()).getType().equals(BlockType.WallSign)) {
			Sign sign = (Sign) player.getWorld().getTileEntityAt((int) player.getX(), (int) player.getY()+1, (int) player.getZ());
			String str = TextFormat.removeFormatting(sign.getTextOnLine(3));
			if (isNumeric(str)) {
				PacketFactory factory = Canary.factory().getPacketFactory();
				Packet packet = factory.blockChange((int) player.getX(), (int) player.getY()-1, (int) player.getZ(), 20, 0);
				player.sendPacket(packet);
			}
		}
	}
	
	/*@HookHandler
	public void onDamage(DamageHook hook) {
		Canary.getServer().broadcastMessage("damage");
		if (hook.getDefender().isPlayer()) {
			Canary.getServer().broadcastMessage("player");
			Player player = (Player) hook.getDefender();
			if (hook.getDamageSource() != null) {
				Canary.getServer().broadcastMessage("source");
				if (hook.getDamageSource().getDamagetype() != null) {
					Canary.getServer().broadcastMessage("damagetype");
					if (hook.getDamageSource().getDamagetype().equals(DamageType.FALL)) {
						Canary.getServer().broadcastMessage("fall");
						if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY(), (int) player.getZ()).getType().equals(BlockType.WallSign)) {
							Canary.getServer().broadcastMessage("1");
							hook.setCanceled();
						}
						if (player.getWorld().getBlockAt((int) player.getX(), (int) player.getY()-1, (int) player.getZ()).getType().equals(BlockType.WallSign)) {
							Canary.getServer().broadcastMessage("2");
							hook.setCanceled();
						}
					}
				}
			}
		}
	}*/
	
	@HookHandler
	public void onLogin(ConnectionHook hook) {
		if (CheckForUpdates) {
			if (hook.getPlayer().hasPermission(PERMISSION_UPDATE)) {
				try {
					if (Message_CheckForUpdates) {
						hook.getPlayer().message(Colors.ORANGE + "<Elevator> " + Colors.LIGHT_GRAY + "Checking for updates...");
					}
					String result = sendGet();
					if (result.equalsIgnoreCase(Colors.ORANGE + "<Elevator> " + Colors.LIGHT_RED + "No update available.")) {
						if (Message_NoUpdateAvailable) {
							hook.getPlayer().message(result);
						}
					}
					if (result.contains("Update available at")) {
						if (Message_UpdateAvailable) {
							hook.getPlayer().message(result);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String sendGet() throws Exception {
		String MYIDSTART = "svdragster>";
		String MYIDEND = "<svdragster";
		String url = "http://sv.slyip.net/checkupdate.php?version=" + VERSION + "&plugin=elevator";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		String result = response.toString();
		if (result.contains(MYIDSTART) && result.contains(MYIDEND)) {
			int endPos = result.indexOf(MYIDEND);
			result = Colors.ORANGE + "<Elevator> " + Colors.GREEN + "Update available at: " + Colors.WHITE + result.substring(MYIDSTART.length(), endPos);
		} else {
			result = Colors.ORANGE + "<Elevator> " + Colors.LIGHT_RED + "No update available";
		}
		return result;
 
	}
}
