package me.solarcollections.jvpc.p4.command.p4;

import me.solarcollections.jvpc.booster.Booster.BoosterType;
import me.solarcollections.jvpc.p4.command.SubCommand;
import me.solarcollections.jvpc.player.Profile;
import org.bukkit.command.CommandSender;

public class GiveCommand extends SubCommand {

  public GiveCommand() {
    super("dar", "dar [jogador] [booster/coins]", "Dar multiplicadores e coins.", false);
  }

  @Override
  public void perform(CommandSender sender, String[] args) {
    if (args.length <= 1) {
      sender.sendMessage(" \n§eAjuda - Dar\n \n§3/p4 dar [jogador] coins [quantia]\n§3/sw dar [jogador] booster [private/network] [multiplicador] [horas]\n ");
      return;
    }

    Profile target = Profile.getProfile(args[0]);
    if (target == null) {
      sender.sendMessage("§cUsuário não encontrado.");
      return;
    }

    String action = args[1];
    if (action.equalsIgnoreCase("booster")) {
      if (args.length < 5) {
        sender.sendMessage("§cUtilize /p4 dar [jogador] booster [private/network] [multiplicador] [horas]");
        return;
      }

      try {
        BoosterType type = BoosterType.valueOf(args[2].toUpperCase());
        try {
          double multiplier = Double.parseDouble(args[3]);
          long hours = Long.parseLong(args[4]);
          if (multiplier < 1.0D || hours < 1) {
            throw new Exception();
          }

          target.getBoostersContainer().addBooster(type, multiplier, hours);
          sender.sendMessage("§aMultiplicador adicionado.");
        } catch (Exception ex) {
          sender.sendMessage("§cUtilize números válidos.");
        }
      } catch (Exception ex) {
        sender.sendMessage("§cUtilize /p4 dar [jogador] booster [private/network] [multiplicador] [horas]");
      }
    } else if (action.equalsIgnoreCase("coins")) {
      if (args.length < 3) {
        sender.sendMessage("§cUtilize /p4 dar [jogador] coins [quantia]");
        return;
      }

      try {
        double coins = Double.parseDouble(args[2]);
        if (coins < 1.0D) {
          throw new Exception();
        }

        target.addCoins("sCoreP4", coins);
        sender.sendMessage("§aCoins adicionados.");
      } catch (Exception ex) {
        sender.sendMessage("§cUtilize números válidos.");
      }
    } else {
      sender.sendMessage(" \n§eAjuda - Dar\n \n§3/p4 dar [jogador] coins [quantia]\n§3/p4 dar [jogador] booster [private/network] [multiplicador] [horas]\n ");
    }
  }
}
