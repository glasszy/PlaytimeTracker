import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PlaytimeCommand implements CommandExecutor {
    private final PlaytimeLimiter plugin;

    public PlaytimeCommand(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Console must specify a player.");
            return true;
        }

        long millis = plugin.getPlaytime(target.getUniqueId());
        long minutes = millis / 60_000;
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has played " + minutes + " minutes today.");
        return true;
    }
}
