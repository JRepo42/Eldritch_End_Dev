package elocindev.eldritch_end.mixin.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import elocindev.eldritch_end.api.CorruptionAPI;
import elocindev.eldritch_end.corruption.CorruptionDisplayTooltip;
import elocindev.eldritch_end.item.relics.base.CorruptionRelic;
import elocindev.eldritch_end.item.relics.base.Relic;
import elocindev.necronomicon.api.text.TextAPI;


@Mixin(ItemStack.class)
public abstract class ItemNameMixin {
    @Inject(method="getName", at = @At(value = "HEAD"), cancellable = true)
    private void getName(CallbackInfoReturnable<Text> cir) {
        ItemStack stack = (ItemStack)(Object)this;

        var name = Text.translatable(stack.getTranslationKey());
        var bold = name.getStyle().withBold(true);
        MutableText gradient;
        Object item = (Object)stack.getItem();

        if(item instanceof Relic || (Object) stack.getItem() instanceof CorruptionDisplayTooltip) {

            NbtCompound nbtCompound = stack.getSubNbt("display");

            if (item instanceof CorruptionRelic) gradient = TextAPI.Styles.getGradient(name.setStyle(bold), 1, 0x5c3885, 0xb8731a, 1.0F);
            else if (item instanceof Relic) gradient = TextAPI.Styles.getGradient(name.setStyle(bold), 1, 0x5d378c, 0xa89532, 1.0F);
            else gradient = CorruptionAPI.getCMenuTitle();

            if (nbtCompound != null && nbtCompound.contains("Name", 8)) {
                try {
                    Text text = Text.Serializer.fromJson(nbtCompound.getString("Name"));

                    if (text != null) {
                        
                        
                        cir.setReturnValue(gradient);
                        return;
                    }

                    nbtCompound.remove("Name");
                } catch (Exception e) {
                    nbtCompound.remove("Name");
                }
            }

            cir.setReturnValue(gradient);
        }
    }
}