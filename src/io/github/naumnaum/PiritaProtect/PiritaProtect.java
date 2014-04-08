package io.github.naumnaum.PiritaProtect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.Openable;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author naumnaum
 * @modded by Naumnaum and re4397
 */
@SuppressWarnings("deprecation")
public class PiritaProtect extends JavaPlugin implements Listener {
	// The class handling PVP
	public static TimerHandlers PVP;
	public static PiritaProtect Plugin;
	// Files
	public String ProtectionPath;
	public String LandPath;
	// Protections
	public Map<String, ArrayList<ProtectionZone>> Protections = new HashMap<String, ArrayList<ProtectionZone>>();
	public Map<String, Integer> LandOwned = new HashMap<String, Integer>();
	public ArrayList<Tag> Tags = new ArrayList<Tag>();
	public ArrayList<UserType> Types = new ArrayList<UserType>();

	// modificado por Naumnaum
	public UserType UTAccess = new UserType("Portas",
			" - Permite o jogador abrir portas, botoes, placas de pressao.",
			Material.STAINED_CLAY, 8, Material.STAINED_CLAY, 0, 1, false);
	// modificado por Naumnaum
	public UserType UTEntities = new UserType("Entidades",
			" - Permite o jogador usar ENTIDADES como ANIMAIS, carrinhos.",
			Material.STAINED_CLAY, 7, Material.STAINED_CLAY, 12, 2, false);
	// modificado por Naumnaum
	public UserType UTBuildBlocks = new UserType(
			"Construir",
			" - Permite o jogador colocar e quebrar blocos do terreno, mas nao permite quebrar nem acessar blocos que abram inventario, como ba�s, fornos, etc.",
			Material.STAINED_CLAY, 13, Material.STAINED_CLAY, 5, 3, false);
	// modificado por Naumnaum
	public UserType UTUseBlocks = new UserType(
			"Total",
			" - Da permissao para colocar ou tirar todos blocos, acessar todos objetos.",
			Material.STAINED_CLAY, 11, Material.STAINED_CLAY, 3, 3, false);
	// modificado por Naumnaum
	public UserType UTModerator = new UserType(
			"Moderador",
			" - Permite o jogador a usar comandos de DONO do terreno em seu terreno!",
			Material.STAINED_CLAY, 1, Material.STAINED_CLAY, 4, 5, true);
	// Adicionado por Naumnaum
	public UserType UTInv = new UserType("Baus",
			" - Permite usar inventarios como BAUS, fornos, etc...",
			Material.STAINED_GLASS, 11, Material.STAINED_GLASS, 3, 3, false);

	public ArrayList<Material> RodTypes = new ArrayList<Material>();

	public ItemStack RodA;

	public ItemStack addRod(ItemStack baseRod, String Name, int MaxUses,
			int Level, Material baseMaterial) {
		ItemStack Rod = baseRod.clone();
		ItemMeta RodMeta = Rod.getItemMeta();
		RodMeta.setDisplayName(Name);
		ArrayList<String> Lore = new ArrayList<String>();
		Lore.add("Protege terrenos");
		Lore.add(MaxUses + " / " + MaxUses);
		RodMeta.setLore(Lore);
		Rod.setItemMeta(RodMeta);
		Rod.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Level);
		if (baseMaterial != null) {
			ShapedRecipe RodRecipe = new ShapedRecipe(Rod);
			RodRecipe.shape(" GG", " SG", "C  ");
			RodRecipe.setIngredient('G', Material.GLASS);
			RodRecipe.setIngredient('S', baseRod.getType());
			RodRecipe.setIngredient('C', baseMaterial);
			getServer().addRecipe(RodRecipe);
		}
		if (!RodTypes.contains(baseRod.getType()))
			RodTypes.add(baseRod.getType());
		return Rod;
	}

	public void SetupRods() {
		ItemStack baseStick = new ItemStack(Material.STICK);
		ItemStack baseRod = new ItemStack(Material.BLAZE_ROD);
		addRod(baseStick, "Rod of Stone", 4, 1, Material.COBBLESTONE);
		addRod(baseStick, "Rod of Iron", 10, 2, Material.IRON_INGOT);
		addRod(baseStick, "Rod of Gold", 25, 3, Material.GOLD_INGOT);
		addRod(baseStick, "Rod of Diamond", 40, 4, Material.DIAMOND);
		addRod(baseRod, "Rod of Obsidian", 50, 5, Material.OBSIDIAN);
		RodA = addRod(baseRod, "Rod of the Admin", -1, 6, null);
	}

	public void addTag(String name, String desc, String... Values) {
		Tag tag = new Tag(name.toLowerCase(), desc.toLowerCase());
		for (String value : Values) {
			tag.addValues(value.toLowerCase());
		}
		Tags.add(tag);
	}

	public void addUserType(UserType Type) {
		Types.add(Type);
	}

	@Override
	public void onEnable() {
		Plugin = this;
		this.saveDefaultConfig();
		addTag("PVPOff", "Previne PVP", "true", "false");
		addTag("Fire", "Previne fogo espalhar", "true", "false");
		addTag("Ice", "Previne gelo derreter ou formar", "true", "false");
		addTag("Snow", "Previne neve derreter ou formar", "true", "false");
		addTag("EntitySpawn", "Previne mobs nascerem", "true", "false");
		addTag("ServerOwned",
				"Prevents this protection from being counted in the owners land blocks",
				"true");
		addUserType(UTAccess);
		addUserType(UTEntities);
		addUserType(UTBuildBlocks);
		addUserType(UTUseBlocks);
		addUserType(UTModerator);
		// adcionado por Naumnaum
		addUserType(UTInv);
		ProtectionPath = getDataFolder() + File.separator + "Protections.yml";
		File FileP = new File(ProtectionPath);
		LandPath = getDataFolder() + File.separator + "Land.yml";
		File FileL = new File(ProtectionPath);
		if (FileL.exists())
			try {
				LandOwned = load(LandPath);
			} catch (Exception e) {
			}
		Map<String, String> StringProtections = new HashMap<String, String>();
		if (FileP.exists())
			try {
				StringProtections = load(ProtectionPath);
			} catch (Exception e) {
			}
		if (!StringProtections.isEmpty())
			for (String Player : StringProtections.keySet()) {
				ArrayList<ProtectionZone> Zones = new ArrayList<ProtectionZone>();
				for (String Zone : StringProtections.get(Player).split(" / ")) {
					if (Zone.length() > 0) {
						ArrayList<ProtectionZone> newZones = new ArrayList<ProtectionZone>();
						new ProtectionZone(null, null).fromString(Zone,
								newZones);
						for (ProtectionZone newZone : newZones) {
							Zones.add(newZone);
						}
					}
				}
				Protections.put(Player, Zones);
			}
		SetupRods();
		getServer().getPluginManager().registerEvents(this, this);
		PVP = new TimerHandlers(this);
		getServer().getPluginManager().registerEvents(PVP, this);
		new UpdateHandler(this, 68440, this.getFile(),
				UpdateHandler.UpdateType.DEFAULT, true);
	}

	@Override
	public void onDisable() {
		try {
			save(LandOwned, LandPath);
		} catch (Exception e) {
			try {
				save(LandOwned, LandPath);
			} catch (Exception e1) {
			}
		}
		Map<String, String> StringProtections = new HashMap<String, String>();
		if (!Protections.isEmpty())
			for (String Player : Protections.keySet()) {
				String Total = "";
				for (ProtectionZone Zone : Protections.get(Player)) {
					Total = Total + " / " + Zone.toString();
				}
				StringProtections.put(Player, Total);
			}
		try {
			save(StringProtections, ProtectionPath);
		} catch (Exception e) {
			try {
				save(StringProtections, ProtectionPath);
			} catch (Exception e1) {
			}
		}
	}

	public static <T extends Object> void save(T obj, String path)
			throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T load(String path) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		T result = (T) ois.readObject();
		ois.close();
		return result;
	}

	@Override
	public boolean onCommand(CommandSender Sender, Command Cmd, String Label,
			String[] Args) {
		if (Cmd.getName().equalsIgnoreCase("giverod")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.GiveRod")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				Target.getInventory().addItem(RodA);
				Target.sendMessage("Dado a " + Target.getDisplayName() + " a "
						+ RodA.getItemMeta().getDisplayName());
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				((Player) Sender).getInventory().addItem(RodA);
				Sender.sendMessage("Dado a "
						+ ((Player) Sender).getDisplayName() + " a "
						+ RodA.getItemMeta().getDisplayName());
			}
		} else if (Cmd.getName().equalsIgnoreCase("setowner")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.EditOthers")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0]).getOwner()
							.equalsIgnoreCase(Sender.getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (getConfig().getBoolean("BuyableLand")) {
							if (LandOwned.containsKey(Target.getName())) {
								if ((LandOwned.get(Target.getName()) - getTotalLandUsed(Target)) < ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getCube().getSize()
										&& !((ProtectionZone) PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getTag("ServerOwned")
												.equalsIgnoreCase("true")) {
									Sender.sendMessage(Target.getName()
											+ " precisa "
											+ (((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getCube()
													.getSize() - (LandOwned
													.get(Target.getName()) - getTotalLandUsed(Target)))
											+ " mais blocos de protecao para proteger isto");
									return true;
								}
							}
						}
						if (Protections
								.containsKey(((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getOwner())) {
							ProtectionZone Zone = ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0]).Clone();
							ArrayList<ProtectionZone> ZonesA = Protections
									.get(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner());
							ArrayList<ProtectionZone> ZonesB = Protections
									.get(Target.getName());
							if (ZonesB == null) {
								ZonesB = new ArrayList<ProtectionZone>();
							}
							ZonesA.remove((PVP.PlayerSelectedZone.get(Sender)
									.keySet().toArray()[0]));
							Zone.setOwner(Target.getName());
							ZonesB.add(Zone);
							Protections.put(Sender.getName(), ZonesA);
							Protections.put(Target.getName(), ZonesB);
							Sender.sendMessage("Definido o proprietario da protecao a "
									+ Target.getDisplayName());

							Target.sendMessage("Voce recebeu a protecao de terreno de "
									+ ((Player) Sender).getDisplayName());
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao ou nao tem permissao nesta, para selecionar clique com o graveto comum nela.");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("accept")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Accept")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				for (Player Plr : PVP.CommandTrades.keySet()) {
					CommandRequest Request = PVP.CommandTrades.get(Plr);
					if (Request.getTarget() == (Player) Sender) {
						if (!Request.getAccepted()) {
							Request.setAccepted(true);
							onCommand(Request.getSender(),
									Request.getCommand(), Request.getCommand()
											.getName(), Request.getArgs());
							Request.getSender().sendMessage(
									"O comando foi aceito");
							Request.getTarget().sendMessage(
									"Voce aceitou o coamndo");
							PVP.CommandTrades.put(Plr, Request);
							return true;
						}
					}
				}
				Sender.sendMessage("You have no pending requests");
				return true;
			} else if (Args.length > 0) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				((Player) Sender).getInventory().addItem(RodA);
			}
		} else if (Cmd.getName().equalsIgnoreCase("transfer")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Transfer")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 1) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				if (Target == (Player) Sender) {
					Sender.sendMessage("Nao pode transferir para voce mesmo");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (PVP.CommandTrades.containsKey(Sender)
							&& PVP.CommandTrades.get(Sender).getTarget() == Target
							&& PVP.CommandTrades.get(Sender).getAccepted()) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getOwner()
								.equalsIgnoreCase(Sender.getName())
								|| Sender
										.hasPermission("PiritaProtect.Protection.EditOthers")) {
							int Length = Math
									.abs(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getCube().getCorner1().getBlockX()
											- ((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getCube()
													.getCorner2().getBlockX());
							int Width = Math
									.abs(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getCube().getCorner1().getBlockZ()
											- ((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getCube()
													.getCorner2().getBlockZ());
							if (getConfig().getBoolean("BuyableLand")) {
								if (LandOwned.containsKey(Target.getName())) {
									if ((LandOwned.get(Target.getName()) - getTotalLandUsed(Target)) < (Length * Width)
											&& !((ProtectionZone) PVP.PlayerSelectedZone
													.get(Sender).keySet()
													.toArray()[0]).getTag(
													"ServerOwned")
													.equalsIgnoreCase("true")) {
										Sender.sendMessage(Target.getName()
												+ " precisa "
												+ ((Length * Width) - (LandOwned
														.get(Target.getName()) - getTotalLandUsed(Target)))
												+ " mais blocos de protecao para proteger isto");
										return true;
									}
								}
							}
							if (Protections
									.containsKey(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner())) {
								ProtectionZone Zone = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.Clone();
								ArrayList<ProtectionZone> ZonesA = Protections
										.get(((ProtectionZone) PVP.PlayerSelectedZone
												.get(Sender).keySet().toArray()[0])
												.getOwner());
								ArrayList<ProtectionZone> ZonesB = Protections
										.get(Target.getName());
								if (ZonesB == null) {
									ZonesB = new ArrayList<ProtectionZone>();
								}
								ZonesA.remove((PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0]));
								Zone.setOwner(Target.getName());
								ZonesB.add(Zone);
								Protections.put(Sender.getName(), ZonesA);
								Protections.put(Target.getName(), ZonesB);
								Sender.sendMessage("Transferirdo a protecao para "
										+ Target.getDisplayName());
								Target.sendMessage("Voce recebeu a protecao de terreno de "
										+ ((Player) Sender).getDisplayName());
							}
						}
					} else {
						CommandRequest Trades = new CommandRequest(
								(Player) Sender, Target, false, Cmd, Args);
						PVP.CommandTrades.put((Player) Sender, Trades);
						PVP.CommandTimers.put((Player) Sender, 10);
						Sender.sendMessage("Esperando o jogador aceitar");
						Target.sendMessage(Sender.getName()
								+ " deseja dar-lhe a protecao, use /accept para aceitar ou esperae 10 segundos para nao aceitar");
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("addAmigo")
				|| Cmd.getName().equalsIgnoreCase("addUsers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Users")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 2) {
				String Target;
				if (Args[0].equalsIgnoreCase("*")) {
					Target = "*";
				} else if (Bukkit.getPlayer(Args[0]) != null) {
					Target = Bukkit.getPlayer(Args[0]).getName();
				} else {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				UserType UT = Util.parseUserType(Args[1]);

				if (UT == null && !Args[1].equalsIgnoreCase("*")) {
					for (UserType UType : Types) {
						Sender.sendMessage(UType.getName() + " : "
								+ UType.getDesc());
					}
					Sender.sendMessage("Isso nao eh um tipo valido");
					return true;
				}

				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).addUsers(
								Target, UT)) {
							Sender.sendMessage("Adicionado " + Target
									+ " a permissao " + Args[1]);
						} else {
							Sender.sendMessage("Nao pode adicionar " + Target
									+ " a permissao " + Args[1]
									+ " ou, ele ja tem esta permissao!");
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("addtag")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Tag")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 2) {
				if (!Util.isTagAndValue(Args[0], Args[1])) {
					for (Tag tag : Tags) {
						Sender.sendMessage(tag.getName() + " : "
								+ tag.getDesc() + " : "
								+ tag.getValues().toString());
					}
					Sender.sendMessage("Isto nao eh um tag valida");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).setTags(
								Args[0].toLowerCase(), Args[1].toLowerCase())) {
							Sender.sendMessage("Adicionado a tag " + Args[0]
									+ " a protecao");

						} else {
							Sender.sendMessage("Nao foi possivel adicionar a tag "
									+ Args[0] + " a protecao");
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				for (Tag tag : Tags) {
					Sender.sendMessage(tag.getName() + " : " + tag.getDesc()
							+ " : " + tag.getValues().toString());
				}
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("gettags")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Tag")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getTags()
								.isEmpty()) {
							Sender.sendMessage("Esta protecao nao tem tags");
						} else {
							for (String Name : ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getTags().keySet()) {
								String Value = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getTags().get(Name);
								Sender.sendMessage(Name + " : " + Value);
							}
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("getusers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Users")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0]).getUsers()
								.keySet().isEmpty()) {
							Sender.sendMessage("Esta protecao nao tem usuarios");
						} else {
							Sender.sendMessage(((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getOwner() + " : Owner");
							for (String User : ((ProtectionZone) PVP.PlayerSelectedZone
									.get(Sender).keySet().toArray()[0])
									.getUsers().keySet()) {
								String UserHas = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Sender).keySet().toArray()[0])
										.getUsers().get(User).toString()
										.split("\\[")[1].split("\\]")[0];
								Sender.sendMessage(User + " : " + UserHas);
							}
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("delAmigo")
				|| Cmd.getName().equalsIgnoreCase("removeUsers")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Users")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 2) {
				String Target;
				if (Args[0].equalsIgnoreCase("*")) {
					Target = "*";
				} else if (Bukkit.getPlayer(Args[0]) != null) {
					Target = Bukkit.getPlayer(Args[0]).getName();
				} else {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				UserType UT = Util.parseUserType(Args[1]);
				if (UT == null && !Args[1].equalsIgnoreCase("*")) {
					for (UserType UType : Types) {
						Sender.sendMessage(UType.getName() + " : "
								+ UType.getDesc());
					}
					Sender.sendMessage("Isso nao eh um tipo valido");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0])
								.removeUsers(Target, UT)) {
							Sender.sendMessage("Removido " + Target
									+ " da protecao como " + Args[1]);
						} else {
							Sender.sendMessage("Nao pode remover " + Target
									+ " da protecao como " + Args[1]);
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removetag")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Tag")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 1) {
				if (!Util.isTag(Args[0])) {
					for (UserType UType : Types) {
						Sender.sendMessage(UType.getName() + " : "
								+ UType.getDesc());
					}
					Sender.sendMessage("Isto nao eh um tag valida");
					return true;
				}
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0])
							.userHasAdminType(((Player) Sender).getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.EditOthers")) {
						if (((ProtectionZone) PVP.PlayerSelectedZone
								.get(Sender).keySet().toArray()[0])
								.removeTags(Args[0].toLowerCase())) {
							Sender.sendMessage("Removido " + Args[0]
									+ " da protecao");
						} else {
							Sender.sendMessage("Nao pode remover " + Args[0]
									+ " da protecao");
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeprotection")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender
					.hasPermission("PiritaProtect.Commands.RemoveProtections")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.PlayerSelectedZone.containsKey(Sender)) {
					if (((ProtectionZone) PVP.PlayerSelectedZone.get(Sender)
							.keySet().toArray()[0]).getOwner()
							.equalsIgnoreCase(Sender.getName())
							|| Sender
									.hasPermission("PiritaProtect.Protection.RemoveOthers")) {
						if (PVP.CommandTimers.containsKey(Sender)) {
							ArrayList<ProtectionZone> Zones = Protections
									.get(((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner());
							Zones.remove((PVP.PlayerSelectedZone.get(Sender)
									.keySet().toArray()[0]));
							Protections.put(
									((ProtectionZone) PVP.PlayerSelectedZone
											.get(Sender).keySet().toArray()[0])
											.getOwner(), Zones);
							PVP.CommandTimers.put((Player) Sender, -1);
							PVP.updateFakeBlocks((Player) Sender);
							Sender.sendMessage("Removido a protecao");
							return true;
						} else {
							PVP.CommandTimers.put((Player) Sender, 10);
							Sender.sendMessage("Escreva o comando novamente dentro de 10 segundos para confirmar!");
							return true;
						}
					}
				} else {
					Sender.sendMessage("Voce nao selecionou uma protecao, ou voce nao tem permissao nesta, para selecionar, clique direito com um graveto normal dentro de um terreno protegido");
					return true;
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("removeallprotections")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender
					.hasPermission("PiritaProtect.Commands.RemoveProtections")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				if (PVP.CommandTimers.containsKey(Sender)) {
					Protections.remove(Sender.getName());
					PVP.updateFakeBlocks((Player) Sender);
					PVP.CommandTimers.put((Player) Sender, -1);
					Sender.sendMessage("Removed all your protections");
					return true;
				} else {
					PVP.CommandTimers.put((Player) Sender, 10);
					Sender.sendMessage("Escreva o comando novamente dentro de 10 segundos para confirmar!");
					return true;
				}
			} else if (Args.length == 1) {
				Player Target = Bukkit.getPlayer(Args[0]);
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				if (PVP.CommandTimers.containsKey(Sender)) {
					Protections.remove(Target.getName());
					PVP.updateFakeBlocks((Player) Sender);
					PVP.CommandTimers.put((Player) Sender, -1);
					Sender.sendMessage("Removido todas protecoes de "
							+ Target.getDisplayName() + ".");
					return true;
				} else {
					PVP.CommandTimers.put((Player) Sender, 10);
					Sender.sendMessage("Escreva o comando novamente dentro de 10 segundos para confirmar!");
					return true;
				}
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("giveland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.AdminLand")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 2) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				int Num = 0;
				try {
					Num = Integer.parseInt(Args[1]);
				} catch (Exception e) {
					Sender.sendMessage("O segundo argumento precisa ser um n�mero inteiro");
					return true;
				}
				if (Num != 0) {
					if (LandOwned.containsKey(Target.getName())) {
						if (LandOwned.get(Target.getName()).intValue() + Num < 0) {
							LandOwned.put(Target.getName(), 0);
							Sender.sendMessage("Set "
									+ Target.getDisplayName()
									+ "'s land to 0 because the integer you specified was more then the land they owned");
						} else {
							LandOwned.put(Target.getName(),
									LandOwned.get(Target.getName()).intValue()
											+ Num);
							Sender.sendMessage("Gave "
									+ Target.getDisplayName() + " " + Num
									+ " land");
						}
					}
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("stuck")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.Stuck")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 0) {
				ProtectionZone Zone = isInsideProtection(((Player) Sender)
						.getLocation());
				if (Zone != null) {
					if (Zone.userHasType(Sender.getName(), UTBuildBlocks)) {
						Sender.sendMessage("Voce pode construir e quebrar blocos para sair desta protecao");
					} else {
						((Player) Sender).teleport(((Player) Sender).getWorld()
								.getHighestBlockAt(Zone.getCube().getCorner1())
								.getLocation());
						Sender.sendMessage("Teleportado voce fora da protecao");
					}
				}
			} else if (Args.length > 0) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("setland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (!Sender.hasPermission("PiritaProtect.Commands.AdminLand")) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			}
			if (Args.length == 2) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				int Num = 0;
				try {
					Num = Integer.parseInt(Args[1]);
				} catch (Exception e) {
					Sender.sendMessage("O segundo argumento precisa ser um n�mero inteiro");
					return true;
				}
				if (Num > 0) {
					if (LandOwned.containsKey(Target.getName())) {
						LandOwned.put(Target.getName(), Num);
						Sender.sendMessage("Set " + Target.getDisplayName()
								+ "'s land to " + Num);
					}
				} else {
					Sender.sendMessage("O segundo argumento precisa ser um n�merio inteiro maior que 0");
					return true;
				}
			} else if (Args.length > 2) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		} else if (Cmd.getName().equalsIgnoreCase("getland")) {
			if (!(Sender instanceof Player)) {
				Sender.sendMessage("Voce precisa ser um jogador para isto");
				return true;
			}
			if (Args.length == 1
					&& Sender.hasPermission("PiritaProtect.Commands.AdminLand")) {
				Player Target = (Bukkit.getPlayer(Args[0]));
				if (Target == null) {
					Sender.sendMessage("Jogador nao encontrado ou nao esta online.");
					return true;
				}
				if (LandOwned.containsKey(Target.getName())) {
					if (Protections.containsKey(Target.getName())) {
						Sender.sendMessage(Target.getName() + " tem "
								+ LandOwned.get(Target.getName())
								+ " terreno, dos quais "
								+ getTotalLandUsed(Target)
								+ " eh usado por o total de "
								+ Protections.get(Target.getName()).size()
								+ " protecoes.");
					} else {
						Sender.sendMessage(Target.getName()
								+ " tem "
								+ LandOwned.get(Target.getName())
								+ " terreno, dos quais 0 eh usado por o total de 0 procoes.");
					}
				}
			} else if (Args.length == 0
					&& Sender.hasPermission("PiritaProtect.Commands.GetLand")) {
				if (LandOwned.containsKey(Sender.getName())) {
					if (Protections.containsKey(Sender.getName())) {
						Sender.sendMessage("Voce tem "
								+ LandOwned.get(Sender.getName())
								+ " terreno, dos quais "
								+ getTotalLandUsed((Player) Sender)
								+ " eh usado por o total de "
								+ Protections.get(Sender.getName()).size()
								+ " protecoes.");
					} else {
						Sender.sendMessage("Voce tem "
								+ LandOwned.get(Sender.getName())
								+ " terreno, dos quais 0 eh usado por o total de 0 procoes.");
					}
				}
			} else if (Args.length == 0 || Args.length == 1) {
				Sender.sendMessage("Voce nao tem permissao para isto");
				return true;
			} else if (Args.length > 1) {
				Sender.sendMessage("Comando errado - www.piritacraft.com");
				return false;
			} else {
				return false;
			}
		}
		return true;
	}

	public int getTotalLandUsed(Player Plr) {
		if (Protections.containsKey(Plr.getName())) {
			ArrayList<ProtectionZone> Zones = Protections.get(Plr.getName());
			int Total = 0;
			for (ProtectionZone Zone : Zones) {
				if (!Zone.getTag("ServerOwned").equalsIgnoreCase("true")) {
					Total = Total + Zone.getCube().getSize();
				}
			}
			return Total;
		} else {
			return 0;
		}
	}

	public ProtectionZone isInsideBiggestProtection(Location loc) {
		if (Protections.isEmpty())
			return null;
		Location Loc = loc.clone();
		ProtectionZone Zone = null;
		for (ArrayList<ProtectionZone> Zones : Protections.values()) {
			for (ProtectionZone ProtZone : Zones) {
				if (Zone == null) {
					if (ProtZone.getCube().isInside(Loc, false))
						Zone = ProtZone;
				} else {
					if (Zone.getCube().getSize() < ProtZone.getCube().getSize()) {
						if (ProtZone.getCube().isInside(Loc, false))
							Zone = ProtZone;
					}
				}
			}
		}
		return Zone;
	}

	public ProtectionZone isInsideProtection(Location loc) {
		if (Protections.isEmpty())
			return null;
		Location Loc = loc.clone();
		ProtectionZone Zone = null;
		for (ArrayList<ProtectionZone> Zones : Protections.values()) {
			for (ProtectionZone ProtZone : Zones) {
				if (Zone == null) {
					if (ProtZone.getCube().isInside(Loc, false))
						Zone = ProtZone;
				} else {
					if (Zone.getCube().getSize() > ProtZone.getCube().getSize()) {
						if (ProtZone.getCube().isInside(Loc, true))
							Zone = ProtZone;
					} else {
						if (ProtZone.getCube().isInside(Loc, false)
								&& !Zone.getCube().isInside(Loc, true)) {
							Zone = ProtZone;
						}
					}
				}
			}
		}
		return Zone;
	}

	public void CornerRod(Player Plr, Location blockLoc, ItemStack Rod) {
		Block Left = Util.GetLowestBlock(blockLoc.clone().add(1, 0, 0));
		Block Right = Util.GetLowestBlock(blockLoc.clone().add(-1, 0, 0));
		Block Center = Util.GetLowestBlock(blockLoc.clone());
		Block Forward = Util.GetLowestBlock(blockLoc.clone().add(0, 0, 1));
		Block Backward = Util.GetLowestBlock(blockLoc.clone().add(0, 0, -1));
		Plr.sendBlockChange(Left.getLocation(), Material.STAINED_CLAY, (byte) 5);
		Plr.sendBlockChange(Right.getLocation(), Material.STAINED_CLAY,
				(byte) 5);
		Plr.sendBlockChange(Center.getLocation(), Material.STAINED_CLAY,
				(byte) 13);
		Plr.sendBlockChange(Forward.getLocation(), Material.STAINED_CLAY,
				(byte) 5);
		Plr.sendBlockChange(Backward.getLocation(), Material.STAINED_CLAY,
				(byte) 5);
		Location EffectsLoc = Center.getLocation().add(0, 1, 0);
		ItemMeta RodMeta = Rod.getItemMeta();
		boolean infinite = false;
		if (RodMeta.getLore().get(1).split("/").length == 2) {
			String[] Nums = RodMeta.getLore().get(1).replaceAll(" ", "")
					.split("/");
			int Num1 = Integer.parseInt(Nums[0]);
			if (Num1 > 1) {
				Num1 = Num1 - 1;
				ArrayList<String> Lore = new ArrayList<String>();
				Lore.add(RodMeta.getLore().get(0));
				Lore.add(Num1 + " / " + Nums[1]);
				RodMeta.setLore(Lore);
				Rod.setItemMeta(RodMeta);
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.playSound(EffectsLoc, Sound.ENDERMAN_TELEPORT, 1, 1);
			} else if (Num1 == -1) {
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.playSound(EffectsLoc, Sound.ENDERMAN_TELEPORT, 1, 1);
				infinite = true;
			} else {
				Plr.sendMessage("Seu " + Rod.getItemMeta().getDisplayName()
						+ " quebrou! Faca outro.");
				Plr.getInventory().setItemInHand(new ItemStack(Material.STICK));
				Plr.playEffect(EffectsLoc, Effect.ENDER_SIGNAL, 1);
				Plr.getWorld().playSound(Plr.getLocation(), Sound.ITEM_BREAK,
						1, 1);
			}
		}
		Map<Block, Integer> Blocks = PVP.UpdateBlock.get(Plr);
		if (Blocks == null)
			Blocks = new HashMap<Block, Integer>();
		Blocks.put(Left, 60);
		Blocks.put(Right, 60);
		Blocks.put(Center, 60);
		Blocks.put(Forward, 60);
		Blocks.put(Backward, 60);
		PVP.UpdateBlock.put(Plr, Blocks);
		Map<Location, Integer> Selections = PVP.PlayerSelection.get(Plr);
		if (Selections == null)
			Selections = new HashMap<Location, Integer>();
		Selections.put(Center.getLocation(), 60);
		if (Selections.size() >= 3) {
			Selections.remove(Selections.keySet().toArray()[0]);
		} else if (Selections.size() == 2) {
			Location Sel1 = null;
			Location Sel2 = null;
			if (((Location) Selections.keySet().toArray()[0]).equals(Center
					.getLocation())) {
				Sel1 = (Location) Selections.keySet().toArray()[1];
				Sel2 = (Location) Selections.keySet().toArray()[0];
			} else {
				Sel1 = (Location) Selections.keySet().toArray()[0];
				Sel2 = (Location) Selections.keySet().toArray()[1];
			}
			ProtectionZone newProt = new ProtectionZone(new Region(Sel1, Sel2),
					Plr.getName());
			boolean resizeZone = false;
			if (Protections.containsKey(Plr.getName())) {
				for (ProtectionZone Zone : Protections.get(Plr.getName())) {
					for (Location loc : GetCorners(Zone)) {
						boolean insideZone = false;
						if (isInsideBiggestProtection(loc) == Zone) {
							if (loc.getBlockX() == Sel1.getBlockX()
									&& loc.getBlockZ() == Sel1.getBlockZ())
								insideZone = true;
						} else {
							int MinY = Math.min(Sel1.getBlockY(),
									Sel2.getBlockY());
							int MaxY = Math.max(Sel1.getBlockY(),
									Sel2.getBlockY());
							if (loc.getBlockX() == Sel1.getBlockX()
									&& loc.getBlockZ() == Sel1.getBlockZ()
									&& loc.getBlockY() >= MinY
									&& loc.getBlockY() <= MaxY)
								insideZone = true;
						}
						if (insideZone) {
							newProt = Zone.Clone();
							if (loc.getBlockX() == newProt.getCube()
									.getCorner1().getBlockX()
									&& loc.getBlockZ() == newProt.getCube()
											.getCorner1().getBlockZ()) {
								newProt.getCube().setCorner1(Sel2);
								resizeZone = true;
							} else if (loc.getBlockX() == newProt.getCube()
									.getCorner2().getBlockX()
									&& loc.getBlockZ() == newProt.getCube()
											.getCorner2().getBlockZ()) {
								newProt.getCube().setCorner2(Sel2);
								resizeZone = true;
							} else if (loc.getBlockX() == newProt.getCube()
									.getCorner3().getBlockX()
									&& loc.getBlockZ() == newProt.getCube()
											.getCorner3().getBlockZ()) {
								newProt.getCube().setCorner3(Sel2);
								resizeZone = true;
							} else if (loc.getBlockX() == newProt.getCube()
									.getCorner4().getBlockX()
									&& loc.getBlockZ() == newProt.getCube()
											.getCorner4().getBlockZ()) {
								newProt.getCube().setCorner4(Sel2);
								resizeZone = true;
							}
						}
					}
				}
			}
			int Length = newProt.getCube().getLength();
			int Width = newProt.getCube().getWidth();
			if (Length >= getConfig().getInt("MinimumZoneSize")
					&& Width >= getConfig().getInt("MinimumZoneSize")) {
				ArrayList<ProtectionZone> Intersecting = new ArrayList<ProtectionZone>();
				for (ArrayList<ProtectionZone> Zones : Protections.values()) {
					for (ProtectionZone Zone : Zones) {
						if (newProt.getCube().zonesIntersect(Zone.getCube(),
								false)) {
							if (!Zone.userHasAdminType(Plr.getName())) {
								Intersecting.add(Zone);
							} else {
								ProtectionZone InsideZone = isInsideBiggestProtection(newProt
										.getCube().getCorner1());
								if (InsideZone == Zone) {
									if (!Zone.getCube().isInside(
											newProt.getCube().getCorner1(),
											false)
											|| !Zone.getCube().isInside(
													newProt.getCube()
															.getCorner2(),
													false))
										Intersecting.add(Zone);
								} else if (newProt.getCube().zonesIntersect(
										Zone.getCube(), true)) {
									if (!Zone.getCube().isInside(
											newProt.getCube().getCorner1(),
											true)
											|| !Zone.getCube()
													.isInside(
															newProt.getCube()
																	.getCorner2(),
															true))
										Intersecting.add(Zone);
								}
							}
						}
					}
				}
				if (!Intersecting.isEmpty() && Intersecting.size() == 1
						&& resizeZone) {
					if (Intersecting.get(0) == ((ProtectionZone) PVP.PlayerSelectedZone
							.get(Plr).keySet().toArray()[0])) {
						Intersecting.remove(0);
					}
				}
				if (Intersecting.isEmpty()) {
					if (getConfig().getBoolean("BuyableLand") && !infinite) {
						if (LandOwned.containsKey(Plr.getName())) {
							int oldSize = 0;
							if (resizeZone) {
								oldSize = ((ProtectionZone) PVP.PlayerSelectedZone
										.get(Plr).keySet().toArray()[0])
										.getCube().getSize();
							}
							if ((LandOwned.get(Plr.getName())
									- getTotalLandUsed(Plr) + oldSize) < newProt
									.getCube().getSize()) {
								Plr.sendMessage("Voce precisa "
										+ (newProt.getCube().getSize() - (LandOwned
												.get(Plr.getName())
												- getTotalLandUsed(Plr) + oldSize))
										+ " mais blocos de protecao");
								PVP.PlayerSelection.remove(Plr);
								PVP.updateFakeBlocks(Plr);
								return;
							}
						}
					}
					if (resizeZone) {
						Protections.get(Plr.getName()).remove(
								(PVP.PlayerSelectedZone.get(Plr).keySet()
										.toArray()[0]));
					}
					ArrayList<ProtectionZone> Prots = Protections.get(Plr
							.getName());
					if (Prots == null)
						Prots = new ArrayList<ProtectionZone>();
					if (infinite)
						newProt.setTags("ServerOwned", "true");
					Prots.add(newProt);
					Protections.put(Plr.getName(), Prots);
					PVP.PlayerSelection.remove(Plr);
					PVP.updateFakeBlocks(Plr);
					return;
				} else {
					for (ProtectionZone Zone : Intersecting) {
						DisplayProtection(Plr, Zone.getCube().getCorner1());
					}
					Plr.sendMessage("Este terreno pega outro terreno");
				}
			} else {
				Plr.sendMessage("A protecao deve ser pelo menos "
						+ getConfig().getInt("MinimumZoneSize") + " x "
						+ +getConfig().getInt("MinimumZoneSize"));
				PVP.PlayerSelection.remove(Plr);
				PVP.updateFakeBlocks(Plr);
				return;
			}
		}
		PVP.PlayerSelection.put(Plr, Selections);
	}

	public ArrayList<Location> GetCorners(ProtectionZone Zone) {
		Location Corner1 = Zone.getCube().getCorner1();
		Location Corner2 = Zone.getCube().getCorner2();
		Location Corner3 = Zone.getCube().getCorner3();
		Location Corner4 = Zone.getCube().getCorner4();

		ArrayList<Location> Corners = new ArrayList<Location>();

		if (Corner1.getBlockX() < Corner2.getBlockX()
				&& Corner1.getBlockZ() < Corner2.getBlockZ()) {
			Corners.add(Corner1);
			if (Corner3.getBlockX() < Corner4.getBlockX()) {
				Corners.add(Corner3);
				Corners.add(Corner2);
				Corners.add(Corner4);
			} else {
				Corners.add(Corner4);
				Corners.add(Corner2);
				Corners.add(Corner3);
			}
		} else if (Corner2.getBlockX() < Corner1.getBlockX()
				&& Corner2.getBlockZ() < Corner1.getBlockZ()) {
			Corners.add(Corner2);
			if (Corner3.getBlockX() < Corner4.getBlockX()) {
				Corners.add(Corner3);
				Corners.add(Corner1);
				Corners.add(Corner4);
			} else {
				Corners.add(Corner4);
				Corners.add(Corner1);
				Corners.add(Corner3);
			}
		} else if (Corner3.getBlockX() < Corner4.getBlockX()
				&& Corner3.getBlockZ() < Corner4.getBlockZ()) {
			Corners.add(Corner3);
			if (Corner1.getBlockX() < Corner2.getBlockX()) {
				Corners.add(Corner1);
				Corners.add(Corner4);
				Corners.add(Corner2);
			} else {
				Corners.add(Corner2);
				Corners.add(Corner4);
				Corners.add(Corner1);
			}
		} else {
			Corners.add(Corner4);
			if (Corner1.getBlockX() < Corner2.getBlockX()) {
				Corners.add(Corner1);
				Corners.add(Corner3);
				Corners.add(Corner2);
			} else {
				Corners.add(Corner2);
				Corners.add(Corner3);
				Corners.add(Corner1);
			}
		}

		return Corners;
	}

	public void DisplayProtection(Player Plr, Location blockLoc) {
		ProtectionZone Zone = isInsideProtection(blockLoc);
		if (Zone == null)
			return;

		Material CType = Material.STAINED_CLAY;
		int CMeta = 14;
		Material SType = Material.STAINED_CLAY;
		int SMeta = 6;
		int priority = 0;
		boolean Admin = false;

		for (UserType UType : Types) {
			if (Zone.userHasType(Plr.getName(), UType)) {
				if (UType.getPriority() > priority) {
					if (UType.getCornerDisplay() != null
							&& UType.getSideDisplay() != null) {
						CType = UType.getCornerDisplay();
						CMeta = UType.getCornerMeta();
						SType = UType.getSideDisplay();
						SMeta = UType.getSideMeta();
						priority = UType.getPriority();
					}
				}
				if (UType.isAdmin()) {
					Admin = true;
				}
			}
		}
		if (Admin) {
			Map<ProtectionZone, Integer> SelZone = new HashMap<ProtectionZone, Integer>();
			SelZone.put(Zone, 60);
			PVP.PlayerSelectedZone.put(Plr, SelZone);
			// Plr.sendMessage("Dono: "+Zone.toString());
		}
		if (Plr.hasPermission("PiritaProtect.Protection.SelectOthers")) {
			Map<ProtectionZone, Integer> SelZone = new HashMap<ProtectionZone, Integer>();
			SelZone.put(Zone, 60);
			PVP.PlayerSelectedZone.put(Plr, SelZone);
			// Plr.sendMessage("Dono: "+Zone.getOwner());
		}
		Plr.sendMessage("Dono: " + Zone.getOwner());

		Location LocUseL;
		Location LocUseR;
		Location LocUseC;

		ArrayList<Location> Corners = GetCorners(Zone);

		Map<Block, Integer> Blocks = PVP.UpdateBlock.get(Plr);
		if (Blocks == null) {
			Blocks = new HashMap<Block, Integer>();
		}

		PVP.updateFakeBlocks(Plr);

		for (int i = 0; i < Corners.size(); i++) {

			LocUseC = Corners.get(i);
			if (i == 0) {
				LocUseR = Corners.get(i).clone().add(1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, 1);
			} else if (i == 1) {
				LocUseR = Corners.get(i).clone().add(1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, -1);
			} else if (i == 2) {
				LocUseR = Corners.get(i).clone().add(-1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, -1);
			} else {
				LocUseR = Corners.get(i).clone().add(-1, 0, 0);
				LocUseL = Corners.get(i).clone().add(0, 0, 1);
			}

			Block Left = Util
					.GetLowestBlockRelative(LocUseL, Plr.getLocation());
			Block Right = Util.GetLowestBlockRelative(LocUseR,
					Plr.getLocation());
			Block Center = Util.GetLowestBlockRelative(LocUseC,
					Plr.getLocation());

			Plr.sendBlockChange(Left.getLocation(), SType, (byte) SMeta);
			Plr.sendBlockChange(Right.getLocation(), SType, (byte) SMeta);
			Plr.sendBlockChange(Center.getLocation(), CType, (byte) CMeta);

			Blocks.put(Left, 60);
			Blocks.put(Right, 60);
			Blocks.put(Center, 60);
		}

		PVP.UpdateBlock.put(Plr, Blocks);
	}

	public String getOverridingTag(String Tag, Location Loc) {
		ProtectionZone Zone = isInsideProtection(Loc);
		if (Zone != null) {
			ProtectionZone MainZone = isInsideBiggestProtection(Loc);
			if (Zone != MainZone) {
				if (!MainZone.getTag(Tag.toLowerCase()).equalsIgnoreCase("")) {
					return MainZone.getTag(Tag.toLowerCase());
				} else {
					return Zone.getTag(Tag.toLowerCase());
				}
			} else {
				return Zone.getTag(Tag.toLowerCase());
			}
		}
		// if (World.hasTag(Tag) != "") {
		// return World.hasTag(Tag);
		// }
		return "";
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void WorldSave(WorldSaveEvent Event) {
		try {
			save(LandOwned, LandPath);
		} catch (Exception e) {
			try {
				save(LandOwned, LandPath);
			} catch (Exception e1) {
			}
		}
		Map<String, String> StringProtections = new HashMap<String, String>();
		if (!Protections.isEmpty())
			for (String Player : Protections.keySet()) {
				String Total = "";
				for (ProtectionZone Zone : Protections.get(Player)) {
					Total = Total + " / " + Zone.toString();
				}
				StringProtections.put(Player, Total);
			}
		try {
			save(StringProtections, ProtectionPath);
		} catch (Exception e) {
			try {
				save(StringProtections, ProtectionPath);
			} catch (Exception e1) {
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void CreatePortal(EntityCreatePortalEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!(Event.getEntity() instanceof Player))
			return;
		if (!getConfig().getBoolean("PlayersCanCreatePortals")) {
			Event.setCancelled(true);
			return;
		}
		ArrayList<ProtectionZone> ProtectionZones = new ArrayList<ProtectionZone>();
		for (BlockState block : Event.getBlocks()) {
			if (isInsideProtection(block.getLocation()) != null)
				ProtectionZones.add(isInsideProtection(block.getLocation()));
		}
		if (ProtectionZones.isEmpty())
			return;
		boolean canUseBlock = true;
		for (ProtectionZone Zone : ProtectionZones) {
			if (!Zone.userHasType(((Player) Event.getEntity()).getName(),
					UTBuildBlocks))
				canUseBlock = false;
		}
		if (!canUseBlock)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerJoin(PlayerLoginEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (getConfig().getBoolean("BuyableLand")) {
			if (!LandOwned.containsKey(Event.getPlayer().getName())) {
				LandOwned.put(Event.getPlayer().getName(),
						getConfig().getInt("StartLand"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PistonRetract(BlockPistonRetractEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (Event.getBlock().getType() == Material.PISTON_BASE
				|| Event.getBlock().getWorld()
						.getBlockAt(Event.getRetractLocation()).getType() == Material.AIR)
			return;
		ProtectionZone Zone = isInsideProtection(Event.getBlock().getLocation());
		ProtectionZone Zone2 = isInsideProtection(Event.getRetractLocation());
		if (Zone == null && Zone2 == null)
			return;
		if (Zone == Zone2)
			return;
		if (Zone != null && Zone2 == null)
			return;
		if (Zone != null && Zone2 != null) {
			if (Zone2.userHasType(Zone.getOwner(), UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void StructureGrow(StructureGrowEvent Event) {
		if (Event.getBlocks().isEmpty())
			return;
		if (Event.getPlayer() == null)
			return;
		ArrayList<ProtectionZone> Zones = new ArrayList<ProtectionZone>();
		for (BlockState block : Event.getBlocks()) {
			ProtectionZone Zone = isInsideProtection(block.getLocation());
			if (Zone != null)
				Zones.add(Zone);
		}
		boolean insideZone = false;
		for (ProtectionZone Zone : Zones) {
			if (!Zone.getOwner().equalsIgnoreCase(Event.getPlayer().getName())) {
				insideZone = true;
			} else if (!Zone.userHasType(Event.getPlayer().getName(),
					UTBuildBlocks)) {
				insideZone = true;
			}
		}
		if (insideZone)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PistonExtend(BlockPistonExtendEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (Event.getBlocks().isEmpty())
			return;
		List<Block> Blocks = new ArrayList<Block>();
		Blocks.addAll(Event.getBlocks());
		Blocks.add(Event.getBlock());
		for (Block block : Blocks) {
			boolean insideZone = false;
			ProtectionZone Zone = isInsideProtection(block.getLocation());
			ProtectionZone Zone2 = isInsideProtection(block.getRelative(
					Event.getDirection()).getLocation());
			if (Zone == null && Zone2 == null)
				insideZone = true;
			if (Zone == Zone2)
				insideZone = true;
			if (Zone != null && Zone2 == null)
				insideZone = true;
			if (Zone != null && Zone2 != null) {
				if (Zone2.userHasType(Zone.getOwner(), UTBuildBlocks))
					insideZone = true;
			}
			if (!insideZone)
				Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockFromTo(BlockFromToEvent Event) {
		if (Event.getBlock() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		ProtectionZone Protection2 = isInsideProtection(Event.getToBlock()
				.getLocation());
		if (Protection == null && Protection2 == null)
			return;
		if (Protection == Protection2)
			return;
		if (Protection != null && Protection2 == null)
			return;
		if (Protection != null && Protection2 != null) {
			if (Protection2.userHasType(Protection.getOwner(), UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockForm(BlockFormEvent Event) {
		if (Event.getBlock() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null)
			return;
		if (getOverridingTag("Ice", Event.getBlock().getLocation())
				.equalsIgnoreCase("true")
				&& Event.getNewState().getType() == Material.ICE) {
			Event.setCancelled(true);
		}
		if (getOverridingTag("Snow", Event.getBlock().getLocation())
				.equalsIgnoreCase("true")
				&& Event.getNewState().getType() == Material.SNOW) {
			Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockFade(BlockFadeEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (getOverridingTag("Ice", Event.getBlock().getLocation())
				.equalsIgnoreCase("true")
				&& Event.getBlock().getType() == Material.ICE) {
			Event.setCancelled(true);
		}
		if (getOverridingTag("Snow", Event.getBlock().getLocation())
				.equalsIgnoreCase("true")
				&& Event.getBlock().getType() == Material.SNOW) {
			Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void HoldItem(PlayerItemHeldEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getPlayer().getInventory().getItem(Event.getNewSlot()) != null)
			if (RodTypes.contains(Event.getPlayer().getInventory()
					.getItem(Event.getNewSlot()).getType())) {
				if (Event.getPlayer().getInventory()
						.getItem(Event.getNewSlot()).getItemMeta().getLore() != null
						&& Event.getPlayer().getInventory()
								.getItem(Event.getNewSlot()).getItemMeta()
								.getLore().get(0).equals("Protege terrenos")) {
					Event.getPlayer()
							.sendMessage(
									"Voce tem "
											+ (LandOwned.get(Event.getPlayer()
													.getName()) - getTotalLandUsed(Event
													.getPlayer()))
											+ " blocks of land");
				}
			}
		if (PVP.PlayerSelection.containsKey(Event.getPlayer())) {
			PVP.PlayerSelection.remove(Event.getPlayer());
			PVP.updateFakeBlocks(Event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractPlayer(final PlayerInteractEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getAction() == Action.LEFT_CLICK_AIR
				|| Event.getAction() == Action.RIGHT_CLICK_AIR)
			return;
		if (Event.getClickedBlock().getType() == Material.SOIL
				&& Event.getAction() == Action.PHYSICAL) {
			if (!getConfig().getBoolean("PlayersTrampleCrops")) {
				Event.setCancelled(true);
			}
			// Plr.sendMessage("Plantacao"); //teste
		}
		if (Event.getClickedBlock().getType() == Material.SIGN) {
			return;
		}
		ProtectionZone Protection = isInsideProtection(Event.getClickedBlock()
				.getLocation());
		UserType requiredPerm = UTUseBlocks;
		requiredPerm = UTBuildBlocks;
		if (Event.getClickedBlock() != null) {
			// Plr.sendMessage("Bloco nao nulo"); //teste
			if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (Event.getClickedBlock().getState().getData() instanceof Openable) {
					requiredPerm = UTAccess;
					// Plr.sendMessage("Porta"); //teste
				} else if (Event.getClickedBlock().getState().getData() instanceof Lever) {
					// Plr.sendMessage("Alavanca"); //teste
					Block Attached = Event.getClickedBlock().getRelative(
							((Lever) Event.getClickedBlock().getState()
									.getData()).getAttachedFace());
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Attached))
						requiredPerm = UTAccess;
				} else if (Event.getClickedBlock().getState().getData() instanceof Button) {
					// Plr.sendMessage("Botao"); //teste
					Block Attached = Event.getClickedBlock().getRelative(
							((Button) Event.getClickedBlock().getState()
									.getData()).getAttachedFace());
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Attached))
						requiredPerm = UTAccess;
				}
			} else if (Event.getAction() == Action.PHYSICAL) {
				if (Event.getClickedBlock().getState().getData() instanceof PressurePlate) {
					// Plr.sendMessage("Placa de pressao"); //teste
					if (Util.poweringDoor(Event.getClickedBlock())
							|| Util.poweringDoor(Event.getClickedBlock()
									.getRelative(BlockFace.DOWN)))
						requiredPerm = UTAccess;
				}
			}
			// codigo adicionado
			if (Event.getClickedBlock().getState() instanceof InventoryHolder) {
				// Plr.sendMessage("inventario"); //teste
				requiredPerm = UTUseBlocks;
				if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					requiredPerm = UTInv;
				}
			}
			if (Event.getClickedBlock().getState().getType() == Material.ENDER_CHEST
					|| Event.getClickedBlock().getState().getType() == Material.WORKBENCH
					|| Event.getClickedBlock().getState().getType() == Material.ITEM_FRAME
					|| Event.getClickedBlock().getState().getType() == Material.ANVIL) {
				requiredPerm = UTUseBlocks;
				if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					requiredPerm = UTInv;
				}
			}
		}
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTUseBlocks)
				|| Protection.userHasType(Event.getPlayer().getName(),
						requiredPerm)) {
			// Plr.sendMessage("Sem protecao?"); //teste
			if (Event.getAction() == Action.RIGHT_CLICK_BLOCK)
				if (Event.getPlayer().getItemInHand().getType() != Material.AIR
						&& Event.getPlayer().isSneaking()) {
					// Plr.sendMessage("Nao eh ar?"); //teste
				} else if (Event.getClickedBlock() != null) {
					if (getConfig().getBoolean("RightClickIronDoor")) {
						// Plr.sendMessage("Porta de ferro"); //teste
						if (Event.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK) {
							if (Event.getClickedBlock()
									.getRelative(BlockFace.DOWN).getType() == Material.IRON_DOOR_BLOCK) {
								if (Event.getClickedBlock()
										.getRelative(BlockFace.DOWN).getData() <= 3) {
									Event.getClickedBlock()
											.getRelative(BlockFace.DOWN)
											.setData(
													(byte) (Event
															.getClickedBlock()
															.getRelative(
																	BlockFace.DOWN)
															.getData() + 4));
								} else {
									Event.getClickedBlock()
											.getRelative(BlockFace.DOWN)
											.setData(
													(byte) (Event
															.getClickedBlock()
															.getRelative(
																	BlockFace.DOWN)
															.getData() - 4));
								}
								Event.getClickedBlock()
										.getWorld()
										.playEffect(
												Event.getClickedBlock()
														.getLocation(),
												Effect.DOOR_TOGGLE, 0);
							} else {
								if (Event.getClickedBlock().getData() <= 3) {
									Event.getClickedBlock().setData(
											(byte) (Event.getClickedBlock()
													.getData() + 4));
								} else {
									Event.getClickedBlock().setData(
											(byte) (Event.getClickedBlock()
													.getData() - 4));
								}
								Event.getClickedBlock()
										.getWorld()
										.playEffect(
												Event.getClickedBlock()
														.getLocation(),
												Effect.DOOR_TOGGLE, 0);
							}
						}
					}
				}
			if (Event.hasBlock()) {
				if (Event.getItem() != null) {
					// Plr.sendMessage("bloco + item"); //teste
					if (Event.getAction() == Action.RIGHT_CLICK_BLOCK
							&& Event.getPlayer().hasPermission(
									"PiritaProtect.Protection.MakeProtections")) {
						Bukkit.getServer().getScheduler()
								.scheduleSyncDelayedTask(this, new Runnable() {
									public void run() {
										ItemStack Rod = Event.getPlayer()
												.getInventory().getItemInHand();
										if (RodTypes.contains(Rod.getType())) {
											if (Rod.getItemMeta().getLore() != null
													&& Rod.getItemMeta()
															.getLore()
															.get(0)
															.equals("Protege terrenos")) {
												if (Event.getPlayer()
														.isSneaking()) {
													if (Rod.getAmount() > 1) {
														Event.getPlayer()
																.sendMessage(
																		"Voce nao precisa ter mais de um "
																				+ Rod.getItemMeta()
																						.getDisplayName()
																				+ " empilhado");
														return;
													}
													CornerRod(
															Event.getPlayer(),
															Event.getClickedBlock()
																	.getLocation(),
															Rod);
												}
											} else if (Rod.getItemMeta()
													.getLore() == null) {
												DisplayProtection(Event
														.getPlayer(), Event
														.getClickedBlock()
														.getLocation());
											}
										}
									}
								}, 2);
					}
				}
			}
		} else if (!Protection.userHasType(Event.getPlayer().getName(),
				requiredPerm)) {
			// Plr.sendMessage("Nao tem perm"); //teste
			Event.setUseItemInHand(Result.ALLOW);
			if (Event.hasBlock()) {
				if (Event.getItem() != null) {
					Bukkit.getServer().getScheduler()
							.scheduleSyncDelayedTask(this, new Runnable() {
								public void run() {
									ItemStack Rod = Event.getPlayer()
											.getInventory().getItemInHand();
									if (RodTypes.contains(Rod.getType())) {
										if (Rod.getItemMeta().getLore() == null) {
											DisplayProtection(
													Event.getPlayer(), Event
															.getClickedBlock()
															.getLocation());
										}
									}
								}
							}, 2);
					if (Event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						List<Integer> ItemsIDs = getConfig().getIntegerList(
								"BlockedItems");
						for (int ID : ItemsIDs) {
							if (Event.getItem().getType().getId() == ID)
								Event.setUseItemInHand(Result.DENY);
							// Plr.sendMessage("Item bloqueado"); //teste
						}
					}
				}
			}
			Event.setUseInteractedBlock(Result.DENY);
			// Plr.sendMessage("Evento negado"); //teste
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void CreatureSpawn(CreatureSpawnEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getEntity() instanceof Player)
			return;
		if (Event.getSpawnReason() != SpawnReason.NATURAL)
			return;
		if (getOverridingTag("EntitySpawn", Event.getLocation())
				.equalsIgnoreCase("true")) {
			Event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBurn(BlockBurnEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (!getConfig().getBoolean("FireBurn"))
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockIgnite(BlockIgniteEvent Event) {
		if (Event.getBlock() == null)
			return;
		if (!getConfig().getBoolean("FireSpread"))
			if (Event.getCause() == IgniteCause.SPREAD) {
				Event.setCancelled(true);
			}
		if (getOverridingTag("Fire", Event.getBlock().getLocation())
				.equalsIgnoreCase("true")) {
			if (Event.getCause() == IgniteCause.SPREAD) {
				Event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void HangingPlace(HangingPlaceEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks)
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTUseBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockBreak(BlockBreakEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks)
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTUseBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BlockPlace(BlockPlaceEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlock()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks)
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTUseBlocks)) {
			if (getConfig().getBoolean("ProtectChests"))
				if (Event.getBlock().getType() == Material.CHEST) {
					if (!Protections.containsKey(Event.getPlayer().getName())
							|| Protections.get(Event.getPlayer().getName())
									.isEmpty()) {
						int DefaultSize = 0;
						try {
							DefaultSize = (int) Math.sqrt(getConfig()
									.getDouble("StartLand"));
						} catch (Exception e) {
							getLogger()
									.warning(
											"Your 'StartLand' value in the config is not a square number!");
						}
						if (DefaultSize == 0)
							return;
						Region cube = new Region(Event.getBlock().getLocation()
								.clone().add(DefaultSize, 0, DefaultSize),
								Event.getBlock().getLocation().clone()
										.add(-DefaultSize, 0, -DefaultSize));
						ProtectionZone newProt = new ProtectionZone(cube, Event
								.getPlayer().getName());
						boolean Intersecting = false;
						for (ArrayList<ProtectionZone> Zones : Protections
								.values()) {
							for (ProtectionZone Zone : Zones) {
								if (newProt.getCube().zonesIntersect(
										Zone.getCube(), false)) {
									Intersecting = true;
								}
							}
						}
						if (!Intersecting) {
							ArrayList<ProtectionZone> Prots = new ArrayList<ProtectionZone>();
							Prots.add(newProt);
							Protections.put(Event.getPlayer().getName(), Prots);
							PVP.PlayerSelection.remove(Event.getPlayer());
							PVP.updateFakeBlocks(Event.getPlayer());
						}
					}
				}
			return;
		}
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void InteractEntity(PlayerInteractEntityEvent Event) {
		if (Event.getPlayer() == null)
			return;
		if (Event.getRightClicked() instanceof Tameable) {
			if (((Tameable) Event.getRightClicked()).getOwner() == null
					|| ((Tameable) Event.getRightClicked()).getOwner() == Event
							.getPlayer())
				return;
		} else {
			ProtectionZone Protection = isInsideProtection(Event
					.getRightClicked().getLocation());
			if (Protection == null
					|| Protection.userHasType(Event.getPlayer().getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageVehicle(VehicleDamageEvent Event) {
		if (Event.getVehicle() == null)
			return;
		if (Event.getAttacker() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getVehicle()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getAttacker()).getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BreakVehicle(VehicleDestroyEvent Event) {
		if (Event.getVehicle() == null)
			return;
		if (Event.getAttacker() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getVehicle()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getAttacker()).getName(),
							UTEntities))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerBucketFill(PlayerBucketFillEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlockClicked()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void PlayerBucketEmpty(PlayerBucketEmptyEvent Event) {
		if (Event.getPlayer() == null)
			return;
		ProtectionZone Protection = isInsideProtection(Event.getBlockClicked()
				.getLocation());
		if (Protection == null
				|| Protection.userHasType(Event.getPlayer().getName(),
						UTBuildBlocks))
			return;
		Event.setCancelled(true);
		Event.getPlayer().updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void BreakHanging(HangingBreakByEntityEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getRemover() instanceof Player) {
			ProtectionZone Protection = isInsideProtection(Event.getEntity()
					.getLocation());
			if (Protection == null
					|| Protection.userHasType(
							((Player) Event.getRemover()).getName(),
							UTBuildBlocks))
				return;
		}
		Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void DamageEntity(EntityDamageEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (Event.getEntity() instanceof Monster)
			return;
		if (Event.getCause() == DamageCause.ENTITY_EXPLOSION
				|| Event.getCause() == DamageCause.BLOCK_EXPLOSION) {
			if (!getConfig().getBoolean("TNTDamageEntity")
					&& Event.getEntityType() == EntityType.PRIMED_TNT)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("TNTCartDamageEntity")
					&& Event.getEntityType() == EntityType.MINECART_TNT)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("CreeperDamageEntity")
					&& Event.getEntityType() == EntityType.CREEPER)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("GhastDamageEntity")
					&& Event.getEntityType() == EntityType.FIREBALL)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("WitherDamageEntity")
					&& Event.getEntityType() == EntityType.WITHER)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("WitherHeadDamageEntity")
					&& Event.getEntityType() == EntityType.WITHER_SKULL)
				Event.setCancelled(true);
			if (!getConfig().getBoolean("EnderCrystalDamageEntity")
					&& Event.getEntityType() == EntityType.ENDER_CRYSTAL)
				Event.setCancelled(true);
		}
		if (!(Event instanceof EntityDamageByEntityEvent))
			return;
		EntityDamageByEntityEvent SubEvent = (EntityDamageByEntityEvent) Event;
		if (SubEvent.getDamager() == null)
			return;
		Player Attacker = null;
		if (SubEvent.getDamager() instanceof Player)
			Attacker = (Player) SubEvent.getDamager();
		if (SubEvent.getDamager() instanceof Projectile)
			if (((Projectile) SubEvent.getDamager()).getShooter() != null
					&& ((Projectile) SubEvent.getDamager()).getShooter() instanceof Player)
				Attacker = (Player) ((Projectile) SubEvent.getDamager())
						.getShooter();
		if (Attacker == null)
			return;
		if (Event.getEntity() instanceof Tameable) {
			if (((Tameable) Event.getEntity()).getOwner() == Attacker) {
				return;
			} else if (((Tameable) Event.getEntity()).getOwner() == null) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(Attacker.getName(),
								UTEntities))
					return;
			} else if (Event.getEntity() instanceof Wolf) {
				Object Owner = ((Wolf) Event.getEntity()).getOwner();
				if (Owner instanceof Player)
					if (PVP.isPlayerInPVPWith(Attacker, (Player) Owner))
						return;
				if (Owner instanceof OfflinePlayer)
					if (PVP.isPlayerInPVPWith(Attacker,
							((OfflinePlayer) Owner).getPlayer()))
						return;
			}
		} else {
			if (!(Event.getEntity() instanceof Player)) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(Attacker.getName(),
								UTEntities))
					return;
			}
		}
		if (Event.getEntity() instanceof Player) {
			if (PVP.isPlayerInPVPWith((Player) Event.getEntity(), Attacker))
				return;
			if (!getOverridingTag("PVPOff", Event.getEntity().getLocation())
					.equalsIgnoreCase("true")
					&& !getOverridingTag("PVPOff", Attacker.getLocation())
							.equalsIgnoreCase("true"))
				return;
		}

		Event.setCancelled(true);
		Attacker.updateInventory();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityInteract(EntityInteractEvent Event) {
		if (Event.getEntity() == null || Event.getBlock() == null)
			return;
		if (Event.getEntity() instanceof Projectile) {
			if (((Projectile) Event.getEntity()).getShooter() instanceof Player) {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(((Player) ((Projectile) Event
								.getEntity()).getShooter()).getName(),
								UTEntities))
					return;
				if (Event.getBlock().getType() == Material.TRIPWIRE
						|| Event.getBlock().getType() == Material.WOOD_BUTTON
						|| Event.getBlock().getType() == Material.WOOD_PLATE)
					Event.setCancelled(true);
			}
		}
		if (Event.getEntity() instanceof Tameable)
			if (!((Tameable) Event.getEntity()).isTamed()) {
				return;
			} else {
				ProtectionZone Protection = isInsideProtection(Event
						.getEntity().getLocation());
				if (Protection == null
						|| Protection.userHasType(
								((Tameable) Event.getEntity()).getOwner()
										.getName(), UTEntities)) {
					return;
				} else {
					Event.setCancelled(true);
				}
			}
		if (Event.getBlock().getType() == Material.SOIL) {
			if (!(Event.getEntity() instanceof Player)) {
				if (!getConfig().getBoolean("EntitiesTrampleCrops"))
					Event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityChangeBlock(EntityChangeBlockEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!getConfig().getBoolean("EndermenBlockChange")
				&& Event.getEntityType() == EntityType.ENDERMAN)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("ZombiesBreakDoors")
				&& Event.getEntityType() == EntityType.ZOMBIE)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("WitherBlockDamage")
				&& Event.getEntityType() == EntityType.WITHER)
			Event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void EntityExplode(EntityExplodeEvent Event) {
		if (Event.getEntity() == null)
			return;
		if (!getConfig().getBoolean("TNTBlockDamage")
				&& Event.getEntityType() == EntityType.PRIMED_TNT)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("TNTCartBlockDamage")
				&& Event.getEntityType() == EntityType.MINECART_TNT)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("CreeperBlockDamage")
				&& Event.getEntityType() == EntityType.CREEPER)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("GhastBlockDamage")
				&& Event.getEntityType() == EntityType.FIREBALL)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("WitherBlockDamage")
				&& Event.getEntityType() == EntityType.WITHER)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("WitherHeadBlockDamage")
				&& Event.getEntityType() == EntityType.WITHER_SKULL)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("EnderCrystalBlockDamage")
				&& Event.getEntityType() == EntityType.ENDER_CRYSTAL)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("EnderDragonBlockDamage")
				&& Event.getEntityType() == EntityType.COMPLEX_PART)
			Event.setCancelled(true);
		if (!getConfig().getBoolean("EnderDragonBlockDamage")
				&& Event.getEntityType() == EntityType.ENDER_DRAGON)
			Event.setCancelled(true);
	}
}
