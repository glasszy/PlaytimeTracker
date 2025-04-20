import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.*;
import java.util.*;

public class PlaytimeLimiter extends JavaPlugin {

    private final HashMap<UUID, Long> playtimeToday = new HashMap<>();
    private final HashMap<UUID, Long> loginTimestamps = new HashMap<>();
    private int maxPlaytimeMinutes;
    private final List<LocalTime> resetTimes = new ArrayList<>();
    private final Set<UUID> warnedPlayers = new HashSet<>();
    private final Set<UUID> ignoredPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxPlaytimeMinutes = getConfig().getInt("max-playtime", 120);
        List<String> resetTimeStrings = getConfig().getStringList("reset-times");

        getCommand("playtimelist").setExecutor(new PlaytimeListCommand(this));
        getCommand("setplaytime").setExecutor(new SetPlaytimeCommand(this));
        getCommand("ignoreplaytime").setExecutor(new IgnorePlaytimeCommand(this));
        getCommand("ignoredplayers").setExecutor(new IgnoredPlayersCommand(this));
        getCommand("playtimecheck").setExecutor(new PlaytimeCommand(this));

        for (String timeString : resetTimeStrings) {
            try {
                resetTimes.add(LocalTime.parse(timeString));
            } catch (Exception e) {
                getLogger().warning("Invalid time format in config: " + timeString);
            }
        }

        scheduleResetTasks();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isBypassed(player)) continue;

                    UUID uuid = player.getUniqueId();
                    long playtime = getPlaytime(uuid) + 60_000;
                    playtimeToday.put(uuid, playtime);

                    long remaining = maxPlaytimeMinutes * 60_000L - playtime;

                    // Send 1-minute warning once
                    if (remaining <= 60_000L && remaining > 0 && !warnedPlayers.contains(uuid)) {
                        player.sendMessage("§eYou have 1 minute left before you're kicked!");
                        warnedPlayers.add(uuid);
                    }

                    if (playtime >= maxPlaytimeMinutes * 60_000L && !isIgnoring(uuid)) {
                        player.kickPlayer("You have reached your daily playtime limit. Please pay if you want to play more!");
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L * 60); // every minute
    }

    @Override
    public void onDisable() {
        // Nothing to save
    }

    public long getPlaytime(UUID uuid) {
        return playtimeToday.getOrDefault(uuid, 0L);
    }

    public void addSession(UUID uuid, long millis) {
        playtimeToday.put(uuid, getPlaytime(uuid) + millis);
    }

    public void setLogin(UUID uuid, long time) {
        loginTimestamps.put(uuid, time);
    }

    public void setPlaytime(UUID uuid, long millis) {
        playtimeToday.put(uuid, millis);
    }

    public long getLogin(UUID uuid) {
        return loginTimestamps.getOrDefault(uuid, System.currentTimeMillis());
    }

    public int getMaxPlaytimeMinutes() {
        return maxPlaytimeMinutes;
    }

    public boolean isBypassed(Player player) {
        return player.hasPermission("playtimelimit.bypass");
    }

    public Set<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public boolean isIgnoring(UUID uuid) {
        return ignoredPlayers.contains(uuid);
    }

    public boolean toggleIgnore(UUID uuid) {
        if (ignoredPlayers.contains(uuid)) {
            ignoredPlayers.remove(uuid);
            return false;
        } else {
            ignoredPlayers.add(uuid);
            return true;
        }
    }

    private void scheduleResetTasks() {
        for (LocalTime reset : resetTimes) {
            long delay = getTicksUntil(reset);
            new BukkitRunnable() {
                @Override
                public void run() {
                    playtimeToday.clear();
                    warnedPlayers.clear(); // Clear warnings on reset
                    Bukkit.getLogger().info("[PlaytimeLimiter] Daily playtime reset at " + reset + " UTC.");
                }
            }.runTaskTimer(this, delay, 24 * 60 * 60 * 20); // every 24h
        }
    }

    private long getTicksUntil(LocalTime resetTime) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextReset = now.withHour(resetTime.getHour()).withMinute(resetTime.getMinute()).withSecond(0);
        if (now.compareTo(nextReset) >= 0) {
            nextReset = nextReset.plusDays(1);
        }
        Duration duration = Duration.between(now, nextReset);
        return duration.getSeconds() * 20;
    }
}
