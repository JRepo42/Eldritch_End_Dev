package elocindev.eldritch_end.mixin.entity.attribute;

import elocindev.eldritch_end.effects.Corruption;
import elocindev.eldritch_end.registry.AttributeRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class CorruptionDamageMixin {
    @Shadow
    public abstract double getAttributeValue(EntityAttribute attribute);

    @Inject(method = "modifyAppliedDamage", at = @At(value = "TAIL"), cancellable = true)
    protected void modifyAppliedDamage(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (getAttributeValue(AttributeRegistry.CORRUPTION) < 10 || (source.isOf(Corruption.DAMAGE))) return;
        cir.setReturnValue(cir.getReturnValue() * 1.1f);
    }
}
