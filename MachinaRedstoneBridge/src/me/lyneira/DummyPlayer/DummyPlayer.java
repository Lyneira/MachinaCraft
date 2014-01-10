package me.lyneira.DummyPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class DummyPlayer extends PermissibleBase implements Player {
    private static final DummyServerOperator operator = new DummyServerOperator();

    private final PlayerInventory inventory = new DummyPlayerInventory(this);
    private final CraftingInventory craftingInventory = new DummyCraftingInventory(this);
    private final InventoryView inventoryView = new DummyInventoryView(this, craftingInventory, inventory, InventoryType.CRAFTING);
    private final Server server;
    private final World world;
    private final UUID uuid = UUID.randomUUID();
    private final Logger log;

    public DummyPlayer(Server server, World world, Logger log) {
        super(operator);
        this.server = server;
        this.world = world;
        this.log = log;
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public PlayerInventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack getItemInHand() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public String getName() {
        return "Redstone-Bridge";
    }

    @Override
    public InventoryView getOpenInventory() {
        return inventoryView;
    }

    @Override
    public int getSleepTicks() {
        return 0;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public boolean isSleeping() {
        return false;
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        return null;
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        return new DummyInventoryView(this, inventory, this.inventory, inventory.getType());
    }

    @Override
    public void openInventory(InventoryView inventory) {
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        return null;
    }

    @Override
    public void setGameMode(GameMode mode) {
    }

    @Override
    public void setItemInHand(ItemStack item) {
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
    }

    @Override
    public boolean setWindowProperty(Property prop, int value) {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        return false;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        return false;
    }

    public void damage(int amount) {
    }

    public void damage(int amount, Entity source) {
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        return new ArrayList<PotionEffect>(0);
    }

    @Override
    public double getEyeHeight() {
        return 1.0D;
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        return 1.0D;
    }

    @Override
    public Location getEyeLocation() {
        Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    @Override
    public double getHealth() {
        return getMaxHealth();
    }

    @Override
    public Player getKiller() {
        return null;
    }

    @Override
    public double getLastDamage() {
        return 0;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return new ArrayList<Block>(0);
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return new ArrayList<Block>(0);
    }

    @Override
    public double getMaxHealth() {
        return 20;
    }

    @Override
    public int getMaximumAir() {
        return 300;
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return 0;
    }

    @Override
    public int getNoDamageTicks() {
        return 0;
    }

    @Override
    public int getRemainingAir() {
        return 300;
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return null;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType type) {
        return false;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return null;
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
    }

    public void setHealth(int health) {
    }

    public void setLastDamage(int damage) {
    }

    @Override
    public void setMaximumAir(int ticks) {
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
    }

    @Override
    public void setNoDamageTicks(int ticks) {
    }

    @Override
    public void setRemainingAir(int ticks) {
    }

    @Override
    public Arrow shootArrow() {
        return null;
    }

    @Override
    public Egg throwEgg() {
        return null;
    }

    @Override
    public Snowball throwSnowball() {
        return null;
    }

    @Override
    public boolean eject() {
        return false;
    }

    @Override
    public int getEntityId() {
        return 0;
    }

    @Override
    public float getFallDistance() {
        return 0;
    }

    @Override
    public int getFireTicks() {
        return 0;
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return null;
    }

    @Override
    public Location getLocation() {
        return new Location(world, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public int getMaxFireTicks() {
        return 1;
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return new ArrayList<Entity>(0);
    }

    @Override
    public Entity getPassenger() {
        return null;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public int getTicksLived() {
        return 1;
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Entity getVehicle() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return new Vector(0.0D, 0.0D, 0.0D);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isInsideVehicle() {
        return false;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public void playEffect(EntityEffect type) {
    }

    @Override
    public void remove() {
    }

    @Override
    public void setFallDistance(float distance) {
    }

    @Override
    public void setFireTicks(int ticks) {
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        return false;
    }

    @Override
    public void setTicksLived(int value) {
    }

    @Override
    public void setVelocity(Vector velocity) {
    }

    @Override
    public boolean teleport(Location location) {
        return false;
    }

    @Override
    public boolean teleport(Entity destination) {
        return false;
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        return false;
    }

    @Override
    public boolean teleport(Entity destination, TeleportCause cause) {
        return false;
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return new ArrayList<MetadataValue>(0);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return false;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
    }

    @Override
    public void abandonConversation(Conversation conversation) {
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
    }

    @Override
    public void acceptConversationInput(String input) {
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        log.info(String.format("[%s] %s: %s", world.getName(), getName(), message));
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public long getFirstPlayed() {
        return 1;
    }

    @Override
    public long getLastPlayed() {
        return 1;
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public boolean hasPlayedBefore() {
        return true;
    }

    @Override
    public boolean isBanned() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public boolean isWhitelisted() {
        return true;
    }

    @Override
    public void setBanned(boolean banned) {
    }

    @Override
    public void setWhitelisted(boolean value) {
    }

    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(0);
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return new HashSet<String>(0);
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
    }

    @Override
    public void awardAchievement(Achievement arg0) {
    }

    @Override
    public boolean canSee(Player arg0) {
        return false;
    }

    @Override
    public void chat(String arg0) {
    }

    @Override
    public InetSocketAddress getAddress() {
        return InetSocketAddress.createUnresolved("DummyPlayer.lyneira.me", 0);
    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public Location getBedSpawnLocation() {
        return getLocation();
    }

    @Override
    public Location getCompassTarget() {
        return getLocation();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public float getExhaustion() {
        return 0;
    }

    @Override
    public float getExp() {
        return 0;
    }

    @Override
    public int getFoodLevel() {
        return 0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public String getPlayerListName() {
        return getName();
    }

    @Override
    public long getPlayerTime() {
        return 0;
    }

    @Override
    public long getPlayerTimeOffset() {
        return 0;
    }

    @Override
    public float getSaturation() {
        return 0;
    }

    @Override
    public int getTotalExperience() {
        return 0;
    }

    @Override
    public void giveExp(int arg0) {
    }

    @Override
    public void hidePlayer(Player arg0) {
    }

    @Override
    public void incrementStatistic(Statistic arg0) {
    }

    @Override
    public void incrementStatistic(Statistic arg0, int arg1) {
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1) {
    }

    @Override
    public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
    }

    @Override
    public boolean isFlying() {
        return false;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return false;
    }

    @Override
    public boolean isSleepingIgnored() {
        return false;
    }

    @Override
    public boolean isSneaking() {
        return false;
    }

    @Override
    public boolean isSprinting() {
        return false;
    }

    @Override
    public void kickPlayer(String message) {
    }

    @Override
    public void loadData() {
    }

    @Override
    public boolean performCommand(String command) {
        return false;
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
    }

    @Override
    public void resetPlayerTime() {
    }

    @Override
    public void saveData() {
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
    }

    @Override
    public void sendBlockChange(Location loc, int material, byte data) {
    }

    @Override
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        return false;
    }

    @Override
    public void sendMap(MapView map) {
    }

    @Override
    public void sendRawMessage(String message) {
    }

    @Override
    public void setAllowFlight(boolean flight) {
    }

    @Override
    public void setBedSpawnLocation(Location location) {
    }

    @Override
    public void setCompassTarget(Location loc) {
    }

    @Override
    public void setDisplayName(String name) {
    }

    @Override
    public void setExhaustion(float value) {
    }

    @Override
    public void setExp(float exp) {
    }

    @Override
    public void setFlying(boolean value) {
    }

    @Override
    public void setFoodLevel(int value) {
    }

    @Override
    public void setLevel(int level) {
    }

    @Override
    public void setPlayerListName(String name) {
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
    }

    @Override
    public void setSaturation(float value) {
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
    }

    @Override
    public void setSneaking(boolean sneak) {
    }

    @Override
    public void setSprinting(boolean sprinting) {
    }

    @Override
    public void setTotalExperience(int exp) {
    }

    @Override
    public void showPlayer(Player player) {
    }

    @Override
    public void updateInventory() {
    }

    @Override
    public int getExpToLevel() {
        return 0;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public float getFlySpeed() {
        return 0;
    }

    @Override
    public float getWalkSpeed() {
        return 0;
    }

    @Override
    public void setFlySpeed(float arg0) throws IllegalArgumentException {
    }

    @Override
    public void setWalkSpeed(float arg0) throws IllegalArgumentException {
    }

    @Override
    public Inventory getEnderChest() {
        return new DummyEnderChestInventory(this);
    }

    @Override
    public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
    }

    @Override
    public void giveExpLevels(int arg0) {
    }

    @Override
    public void setBedSpawnLocation(Location arg0, boolean arg1) {
    }

    @Override
    public boolean getCanPickupItems() {
        return false;
    }

    @Override
    public EntityEquipment getEquipment() {
        return new DummyEntityEquipment(this);
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return false;
    }

    @Override
    public void setCanPickupItems(boolean arg0) {
    }

    @Override
    public void setRemoveWhenFarAway(boolean arg0) {
    }

    @Override
    public Location getLocation(Location arg0) {
        return arg0;
    }

    @Override
    public void setTexturePack(String arg0) {
    }

    @Override
    public void resetMaxHealth() {
    }

    public void setMaxHealth(int arg0) {
    }

    @Override
    public String getCustomName() {
        return "Redstone-Bridge";
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    @Override
    public void setCustomName(String arg0) {
    }

    @Override
    public void setCustomNameVisible(boolean arg0) {
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        return false;
    }

    @Override
    public WeatherType getPlayerWeather() {
        return WeatherType.CLEAR;
    }

    @Override
    public void resetPlayerWeather() {
    }

    @Override
    public void setPlayerWeather(WeatherType arg0) {
    }

	@Override
	public int _INVALID_getLastDamage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void _INVALID_setLastDamage(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeashed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLastDamage(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean setLeashHolder(Entity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void _INVALID_damage(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void _INVALID_damage(int arg0, Entity arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int _INVALID_getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int _INVALID_getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void _INVALID_setHealth(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void _INVALID_setMaxHealth(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void damage(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void damage(double arg0, Entity arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHealth(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaxHealth(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getHealthScale() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Scoreboard getScoreboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHealthScaled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void playSound(Location arg0, String arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHealthScale(double arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHealthScaled(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourcePack(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setScoreboard(Scoreboard arg0) throws IllegalArgumentException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}

}
