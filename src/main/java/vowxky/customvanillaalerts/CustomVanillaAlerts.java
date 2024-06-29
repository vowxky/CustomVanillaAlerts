package vowxky.customvanillaalerts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vowxky.customvanillaalerts.command.CustomVanillaAlertsCommands;
import vowxky.customvanillaalerts.config.Config;

public class CustomVanillaAlerts implements ModInitializer {
    public static final String MOD_ID = "customvanillaalerts";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Config.getInstance().init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CustomVanillaAlertsCommands.register(dispatcher));
    }
}
