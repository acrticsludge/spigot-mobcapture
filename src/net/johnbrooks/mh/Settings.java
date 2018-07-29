package net.johnbrooks.mh;

import com.palmergames.bukkit.towny.Towny;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    public enum CostMode { VAULT, ITEM }

    public static CostMode costMode = CostMode.ITEM;

    public static Material projectileCatcherMaterial = Material.SNOW_BALL;

    public static Material costMaterial = Material.REDSTONE;
    public static int costAmount = 1;
    public static double costVault = 100;

    public static boolean coloredEggs = true;

    private static List<String> disabledWorlds = new ArrayList<>();

    public static boolean townyHook = false;

    public static void load() {
        // 1) Grab configuration for plugin.
        FileConfiguration config = Main.plugin.getConfig();

        // 2) Retrieve settings data from config.yml.
        try {
            projectileCatcherMaterial = Material.getMaterial(config.getString("Projectile Catcher"));
            costMaterial = Material.getMaterial(config.getString("Cost Item"));
        } catch(Exception ex) {
            Main.logger.severe("Invalid material name! Check " + Main.plugin.getDescription().getName() + "'s " +
                    "SpigotMC page to get Material names.");
        }

        try
        {
            costMode = CostMode.valueOf(config.getString("Cost Mode"));
        } catch(Exception ex) {
            Main.logger.severe("Invalid Cost Mode! Use either 'VAULT' or 'ITEM'.");
        }

        // 3) If vault is set, hook into vault.
        if (costMode == Settings.CostMode.VAULT) {
            Main.logger.info("Vault hook " + (setupEconomy() ? "was successful!" : "has failed!"));
            if (Main.economy == null)
            {
                Main.logger.warning("Reverting cost mode to ITEM.");
                costMode = CostMode.ITEM;
            }
        } else
            Main.logger.info("Cost Mode is ITEM, skipping Vault hook.");

        costAmount = config.getInt("Cost Amount");
        costVault = config.getDouble("Cost Vault");
        disabledWorlds = config.getStringList("Disabled Worlds");
        coloredEggs = config.getBoolean("Colored Eggs");
        townyHook = config.getBoolean("Towny Hook");

        if (townyHook) {
            Main.logger.info("Towny hook: true");
        }
    }

    public static boolean isDisabledWorld(String worldName) {
        for (String s : disabledWorlds)
            if (s.equalsIgnoreCase(worldName))
                return true;
        return false;
    }

    private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Main.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            Main.economy = economyProvider.getProvider();
        }
        return (Main.economy != null);
    }
}
