package squeek.spiceoflife.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import squeek.spiceoflife.items.ItemFoodContainer;

import javax.annotation.Nonnull;

public class FoodHelper
{
    public static boolean isValidFood(@Nonnull ItemStack itemStack)
    {
        return isFood(itemStack) && !isFoodContainer(itemStack);
    }

    // 使用 Minecraft 原生方法检测食物
    public static boolean isFood(@Nonnull ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemFood;
    }

    public static boolean isFoodContainer(@Nonnull ItemStack itemStack)
    {
        return itemStack.getItem() instanceof ItemFoodContainer;
    }

    public static boolean isDirectlyEdible(@Nonnull ItemStack itemStack)
    {
        return !(itemStack.getItem() == Items.CAKE || isFoodContainer(itemStack));
    }
}
