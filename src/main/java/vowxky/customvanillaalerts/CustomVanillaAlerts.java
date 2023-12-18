package vowxky.customvanillaalerts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import vowxky.customvanillaalerts.command.CustomVanillaAlertsCommands;
import vowxky.customvanillaalerts.config.Config;

public class CustomVanillaAlerts implements ModInitializer {
    private static Config config;

    public static final String MOD_ID = "customvanillaalerts";

    @Override
    public void onInitialize() {
        getConfig().load();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CustomVanillaAlertsCommands.register(dispatcher));
    }

    public static Config getConfig() {
        if (config == null) {
            config = new Config("messages.json" , MOD_ID);
        }
        return config;
    }
}
