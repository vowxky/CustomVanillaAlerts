package vowxky.customvanillaalerts.mixin;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.EventsType;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;
    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    private void onDisconnectedBroadcast(PlayerManager playerManager, Text text, boolean actionBar) {
        List<Map<String, Object>> messages = Config.getInstance().getMessagesByType(EventsType.DISCONNECT.name().toLowerCase());
        boolean isEnabled = Config.getInstance().isEnabled(EventsType.DISCONNECT.name().toLowerCase());

        if (playerManager != null) {
            MutableText message;
            if (isEnabled && messages != null && !messages.isEmpty()) {
                Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(messages);
                message = MessageBuilder.buildMessage(selectedMessage, this.player.getEntityName());
                playerManager.broadcast(message , false);
            } else {
                message = Text.translatable("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW);
                playerManager.broadcast(message , false);
            }
        }
    }
}
