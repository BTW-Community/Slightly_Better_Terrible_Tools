package btw.community.abbyread.sbtt.mixin;

import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {

    @Invoker("getMaxDamage")
    int abbyread$getMaxDamage();

    @SuppressWarnings("UnusedReturnValue")
    @Invoker("setMaxDamage")
    Item abbyread$setMaxDamage(int maxDamage);
}
