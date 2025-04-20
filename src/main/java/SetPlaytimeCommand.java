import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetPlaytimeCommand implements CommandExecutor {

    private final PlaytimeLimiter plugin;

    public SetPlaytimeCommand(PlaytimeLimiter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || ((Player) sender).isOp()) {
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /setplaytime <player> <minutes>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found or not online.");
                return true;
            }

            try {
                int minutes = Integer.parseInt(args[1]);
                if (minutes < 0) minutes = 0;

                UUID uuid = target.getUniqueId();
                plugin.setPlaytime(uuid, minutes * 60000L);
                sender.sendMessage("§aSet playtime of " + target.getName() + " to " + minutes + " minutes.");
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid number.");
            }

        } else {
            sender.sendMessage("§cYou must be an operator to use this command.");
        }

        return true;
    }
}