package vowxky.customvanillaalerts.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vowxky.customvanillaalerts.CustomVanillaAlerts;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private MinecraftServer server;

    @Shadow protected abstract boolean isHost();

    @Inject(method = "onDisconnected", at = @At("HEAD"), cancellable = true)
    private void onDisconnectedMixin(Text reason, CallbackInfo ci) {
        ci.cancel();
        Config config = CustomVanillaAlerts.getConfig();
        List<Map<String, Object>> disconnectMessages = config.getDisconnectMessages();
        boolean isEnabled = config.isEnabledDisconnectMessages();
        LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
        this.server.forcePlayerSampleUpdate();
        MutableText message;
        if (isEnabled && disconnectMessages != null && !disconnectMessages.isEmpty()) {
            Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(disconnectMessages);
            message = MessageBuilder.buildMessage(selectedMessage, this.player.getEntityName());
        }else{
            message = Text.translatable("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW);
        }
        this.server.getPlayerManager().broadcast(message, false);
        this.player.onDisconnect();
        this.server.getPlayerManager().remove(this.player);
        this.player.getTextStream().onDisconnect();
        if (this.isHost()) {
            LOGGER.info("Stopping singleplayer server as player logged out");
            this.server.stop(false);
        }
    }
}
