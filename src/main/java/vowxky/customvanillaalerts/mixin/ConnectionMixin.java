package vowxky.customvanillaalerts.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.EventsType;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(PlayerManager.class)
public class ConnectionMixin {
    @ModifyVariable(method = "onPlayerConnect", at = @At(value = "STORE", ordinal = 0))
    private MutableText modifyJoinMessage(MutableText originalText, ClientConnection connection, ServerPlayerEntity player) {
        List<Map<String, Object>> messages = Config.getInstance().getMessagesByType(EventsType.JOIN.name().toLowerCase());
        boolean isEnabled = Config.getInstance().isEnabled(EventsType.JOIN.name().toLowerCase());
        MutableText message;
        if (isEnabled && messages != null && !messages.isEmpty()) {
            Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(messages);
            message = MessageBuilder.buildMessage(selectedMessage, player.getEntityName());
            return message;
        }
        return originalText;
    }
}