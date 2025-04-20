import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IgnorePlaytimeCommand implements CommandExecutor {

    private final PlaytimeLimiter plugin;

    public IgnorePlaytimeCommand(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cOnly server operators can use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /ignoreplaytime <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found or not online.");
            return true;
        }

        UUID uuid = target.getUniqueId();
        boolean isNowIgnoring = plugin.toggleIgnore(uuid);

        if (isNowIgnoring) {
            sender.sendMessage("§a" + target.getName() + " is now ignoring the playtime limit.");
        } else {
            sender.sendMessage("§e" + target.getName() + " is no longer ignoring the playtime limit.");
        }

        return true;
    }
}