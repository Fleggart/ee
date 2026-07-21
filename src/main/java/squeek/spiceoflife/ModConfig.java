package squeek.spiceoflife;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig
{
    private static Configuration config;

    /*
     * SERVER
     */
    public static int LUNCHBOX_SLOTS = 6;
    public static int LUNCHBAG_SLOTS = 3;
    public static int FOOD_CONTAINERS_MAX_STACKSIZE = 2;

    /*
     * CLIENT
     */
    public static boolean LEFT_CLICK_OPENS_FOOD_CONTAINERS = false;

    /*
     * ITEMS
     */
    public static final String ITEM_LUNCH_BOX_NAME = "lunchbox";
    public static final String ITEM_LUNCH_BAG_NAME = "lunchbag";

    public static void init(File file)
    {
        config = new Configuration(file);
        load();

        LUNCHBOX_SLOTS = config.get("server", "lunchbox.slots", 6, "午餐盒的格子数量 (1-27)").getInt();
        LUNCHBAG_SLOTS = config.get("server", "lunchbag.slots", 3, "午餐袋的格子数量 (1-18)").getInt();
        FOOD_CONTAINERS_MAX_STACKSIZE = config.get("server", "food.containers.max.stacksize", 2, "每个格子最大堆叠数").getInt();

        LEFT_CLICK_OPENS_FOOD_CONTAINERS = config.get("client", "left.click.opens.food.containers", false, "左键点击空气开关容器").getBoolean();

        // 限制范围
        if (LUNCHBOX_SLOTS < 1) LUNCHBOX_SLOTS = 1;
        if (LUNCHBOX_SLOTS > 27) LUNCHBOX_SLOTS = 27;
        if (LUNCHBAG_SLOTS < 1) LUNCHBAG_SLOTS = 1;
        if (LUNCHBAG_SLOTS > 18) LUNCHBAG_SLOTS = 18;

        save();
    }

    public static void save()
    {
        if (config != null)
            config.save();
    }

    public static void load()
    {
        if (config != null)
            config.load();
    }
}
