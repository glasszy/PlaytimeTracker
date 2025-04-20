import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final PlaytimeLimiter plugin;

    public PlayerListener(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (plugin.isBypassed(player)) return;

        plugin.setLogin(player.getUniqueId(), System.currentTimeMillis());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            long playtime = plugin.getPlaytime(player.getUniqueId());
            if (playtime >= plugin.getMaxPlaytimeMinutes() * 60_000L) {
                player.kickPlayer("You have reached your daily playtime limit.");
            }
        }, 40L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        if (plugin.isBypassed(player)) return;

        long login = plugin.getLogin(player.getUniqueId());
        long now = System.currentTimeMillis();
        plugin.addSession(player.getUniqueId(), now - login);
    }
}
