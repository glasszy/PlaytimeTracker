import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeListCommand implements CommandExecutor {

    private final PlaytimeLimiter plugin;

    public PlaytimeListCommand(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || ((Player) sender).isOp()) {
            sender.sendMessage("§e[PlaytimeLimiter]");
            for (Player player : Bukkit.getOnlinePlayers()) {
                long played = plugin.getPlaytime(player.getUniqueId()) / 60000; // in minutes
                long left = plugin.getMaxPlaytimeMinutes() - played;
                if (left < 0) left = 0;
                sender.sendMessage("§7Player: §a" + player.getName() + " §7- Played: §b" + played + " min §7- Left: §c" + left + " min");
            }
        } else {
            sender.sendMessage("§cYou must be an operator to use this command.");
        }
        return true;
    }
}