package vowxky.customvanillaalerts.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vowxky.customvanillaalerts.CustomVanillaAlerts;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(PlayerManager.class)
public class ConnectionMixin {
    @ModifyVariable(method = "onPlayerConnect", at = @At(value = "STORE", ordinal = 0))
    private MutableText modifyJoinMessage(MutableText originalText, ClientConnection connection, ServerPlayerEntity player) {
        Config config = CustomVanillaAlerts.getConfig();
        List<Map<String, Object>> joinMessages = config.getJoinMessages();
        boolean isEnabled = config.isEnabledJoinMessages();
        MutableText message;
        if (isEnabled && joinMessages != null && !joinMessages.isEmpty()) {
            Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(joinMessages);
            message = MessageBuilder.buildMessage(selectedMessage, player.getEntityName());
            return message;
        }
        return originalText;
    }
}