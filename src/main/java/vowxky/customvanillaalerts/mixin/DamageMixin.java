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
import vowxky.customvanillaalerts.CustomVanillaAlerts;
import vowxky.customvanillaalerts.config.Config;
import vowxky.customvanillaalerts.util.MessageBuilder;

import java.util.List;
import java.util.Map;

@Mixin(DamageTracker.class)
public class DamageMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "getDeathMessage", at = @At("HEAD"), cancellable = true)
    private void onGetDeathMessage(CallbackInfoReturnable<Text> cir) {
        Config config = CustomVanillaAlerts.getConfig();
        List<Map<String, Object>> deathMessages = config.getDeathMessages();
        boolean isEnabled = config.isEnabledDeathMessages();
        MutableText message;
        if (isEnabled && deathMessages != null && !deathMessages.isEmpty()) {
                Map<String, Object> selectedMessage = MessageBuilder.getRandomMessage(deathMessages);
                message = MessageBuilder.buildMessage(selectedMessage, entity.getEntityName(), entity.getDamageTracker().getDeathMessage().getString());
                cir.setReturnValue(message);
        }
    }
}
