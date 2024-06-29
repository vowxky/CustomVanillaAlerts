package vowxky.customvanillaalerts.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.EventsType;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public abstract class AdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @Redirect(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    private MutableText redirectTranslatable(String key, Object[] args , Advancement advancement) {
        List<Map<String, Object>> messages = Config.getInstance().getMessagesByType(EventsType.ADVANCEMENT.name().toLowerCase());
        boolean isEnabled = Config.getInstance().isEnabled(EventsType.ADVANCEMENT.toString().toLowerCase());
        MutableText message;

        if (isEnabled && messages != null && !messages.isEmpty()) {
            Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(messages);
            message = MessageBuilder.buildMessage(selectedMessage, owner.getEntityName(), null, advancement.toHoverableText());
            return message;
        }

        return Text.translatable(key, args);
    }
}