package vowxky.customvanillaalerts.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.EventsType;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(DamageTracker.class)
public class DamageMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
    private void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
        List<Map<String, Object>> messages = Config.getInstance().getMessagesByType(EventsType.DEATH.name().toLowerCase());
        boolean isEnabled = Config.getInstance().isEnabled(EventsType.DEATH.name().toLowerCase());
        MutableText message;
        if (isEnabled && messages != null && !messages.isEmpty()) {
            Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(messages);

            Text deathMessage = Objects.requireNonNull(entity.getDamageTracker().getMostRecentDamage()).getDamageSource().getDeathMessage(entity);

            String deathReason = deathMessage.getString().replace(entity.getDisplayName().getString() + " ", "");

            message = MessageBuilder.buildMessage(selectedMessage, entity.getEntityName(), deathReason , null);
            cir.setReturnValue(message);
        }
    }
}