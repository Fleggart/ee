package squeek.spiceoflife;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import squeek.spiceoflife.items.ItemFoodContainer;

@Mod.EventBusSubscriber
public class ModContent
{
    public static ItemFoodContainer lunchBox;
    public static ItemFoodContainer lunchBag;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        // 从配置读取格子数量
        lunchBox = new ItemFoodContainer(ModConfig.ITEM_LUNCH_BOX_NAME, ModConfig.LUNCHBOX_SLOTS);
        event.getRegistry().register(lunchBox);

        lunchBag = new ItemFoodContainer(ModConfig.ITEM_LUNCH_BAG_NAME, ModConfig.LUNCHBAG_SLOTS);
        event.getRegistry().register(lunchBag);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        lunchBox.registerModels();
        lunchBag.registerModels();
    }
}
