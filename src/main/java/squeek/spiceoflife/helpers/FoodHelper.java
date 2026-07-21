package squeek.spiceoflife.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import squeek.applecore.api.AppleCoreAPI;
import squeek.spiceoflife.items.ItemFoodContainer;

import javax.annotation.Nonnull;

public class FoodHelper
{
    public static boolean isValidFood(@Nonnull ItemStack itemStack)
    {
        return isFood(itemStack) && !isFoodContainer(itemStack);
    }

    public static boolean isFood(@Nonnull ItemStack itemStack)
    {
        return AppleCoreAPI.accessor.isFood(itemStack);
    }

    public static boolean isFoodContainer(@Nonnull ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemFoodContainer;
    }

    public static boolean isDirectlyEdible(@Nonnull ItemStack itemStack)
    {
        return !(itemStack.getItem() == Items.CAKE || isFoodContainer(itemStack));
    }

    public static float getExhaustionLevel(EntityPlayer player)
    {
        return AppleCoreAPI.accessor.getExhaustion(player);
    }

    public static float getMaxExhaustionLevel(EntityPlayer player)
    {
        return AppleCoreAPI.accessor.getMaxExhaustion(player);
    }
}
