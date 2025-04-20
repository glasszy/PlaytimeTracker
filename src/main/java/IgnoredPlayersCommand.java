import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

public class IgnoredPlayersCommand implements CommandExecutor {

    private final PlaytimeLimiter plugin;

    public IgnoredPlayersCommand(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cOnly server operators can use this command.");
            return true;
        }

        if (plugin.getIgnoredPlayers().isEmpty()) {
            sender.sendMessage("§eNo players are currently ignoring the playtime limit.");
            return true;
        }

        sender.sendMessage("§aIgnored players:");
        for (UUID uuid : plugin.getIgnoredPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                sender.sendMessage("§f- " + p.getName());
            }
        }

        return true;
    }
}