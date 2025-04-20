import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlaytimePlugin_OLD extends JavaPlugin implements Listener {

    private HashMap<UUID, Long> playerPlaytime;
    private long playtimeLimit; // in minutes
    private long resetTime = 0;  // Store last reset time in milliseconds

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        playtimeLimit = getConfig().getLong("playtime-limit") * 60 * 1000; // convert to milliseconds
        playerPlaytime = new HashMap<>();

        getServer().getPluginManager().registerEvents(this, this);

        // Load player playtime from a file (optional)
        loadPlayerPlaytime();

        // Schedule the timer reset task
        startTimerResetTask();
    }

    @Override
    public void onDisable() {
        // Save player playtime to file before disabling
        savePlayerPlaytime();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // If player already exists in the map, add to their playtime
        if (!playerPlaytime.containsKey(playerUUID)) {
            playerPlaytime.put(playerUUID, System.currentTimeMillis());
        }

        // Check if player has exceeded playtime limit
        checkPlaytime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Calculate playtime spent this session
        long timeSpent = System.currentTimeMillis() - playerPlaytime.get(playerUUID);

        // Update total playtime (could save this to a file for persistence)
        playerPlaytime.put(playerUUID, timeSpent);
    }

    private void checkPlaytime(Player player) {
        UUID playerUUID = player.getUniqueId();

        long totalPlaytime = playerPlaytime.get(playerUUID);

        if (totalPlaytime >= playtimeLimit) {
            player.kickPlayer("You have exceeded your playtime limit!");
        }
    }

    // Save player playtime to a file
    private void savePlayerPlaytime() {
        FileConfiguration config = this.getConfig();
        for (UUID playerUUID : playerPlaytime.keySet()) {
            config.set("players." + playerUUID.toString(), playerPlaytime.get(playerUUID));
        }
        saveConfig();
    }

    // Load player playtime from a file
    private void loadPlayerPlaytime() {
        FileConfiguration config = this.getConfig();
        for (String key : config.getConfigurationSection("players").getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            long playtime = config.getLong("players." + key);
            playerPlaytime.put(playerUUID, playtime);
        }
    }

    // Start the scheduled task to check time and reset playtime
    private void startTimerResetTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long currentHour = (currentTime / 1000 / 3600) % 24;  // Get current hour (in 24-hour format)

                // Check if the time is 00:00 or 12:00 UTC
                if (currentHour == 0 || currentHour == 12) {
                    if (currentTime - resetTime >= 60 * 60 * 1000) { // Once every hour
                        resetPlaytime();
                        resetTime = currentTime;
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20 * 60); // Check every minute (20 ticks per second * 60 seconds)
    }

    // Reset the playtime for all players
    private void resetPlaytime() {
        for (UUID playerUUID : playerPlaytime.keySet()) {
            playerPlaytime.put(playerUUID, 0L); // Reset the playtime
        }
        getServer().broadcastMessage("Playtime has been reset for all players!");
    }
}
