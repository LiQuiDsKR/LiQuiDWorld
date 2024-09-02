package com.liquidskr.liQuiDWorld.commands;

import com.liquidskr.liQuiDWorld.menu.TradeHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCommand implements CommandExecutor {
    private final TradeHandler tradeHandler;

    public TradeCommand(TradeHandler tradeHandler) {
        this.tradeHandler = tradeHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 1 && args[0].equalsIgnoreCase("trade")) {
                if (args[1].equalsIgnoreCase("confirm")) {
                    tradeHandler.confirmTrade(player);
                    return true;
                } else if (args[1].equalsIgnoreCase("deny")) {
                    tradeHandler.denyTrade(player);
                    return true;
                }
            }
        }
        return false;
    }
}
