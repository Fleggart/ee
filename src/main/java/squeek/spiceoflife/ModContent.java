package squeek.spiceoflife;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig
{
	private static Configuration config;

	/*
	 * SERVER
	 */
	public static int FOOD_CONTAINERS_MAX_STACKSIZE = 2;
	private static final String FOOD_CONTAINERS_MAX_STACKSIZE_NAME = "food.containers.max.stacksize";
	private static final int FOOD_CONTAINERS_MAX_STACKSIZE_DEFAULT = 2;
	private static final String FOOD_CONTAINERS_MAX_STACKSIZE_COMMENT = "The maximum stacksize per slot in a food container";

	/*
	 * CLIENT
	 */
	public static boolean LEFT_CLICK_OPENS_FOOD_CONTAINERS = false;
	private static final String LEFT_CLICK_OPENS_FOOD_CONTAINERS_NAME = "left.click.opens.food.containers";
	private static final boolean LEFT_CLICK_OPENS_FOOD_CONTAINERS_DEFAULT = false;
	private static final String LEFT_CLICK_OPENS_FOOD_CONTAINERS_COMMENT = "If true, left clicking the air while holding a food container will open it";

	/*
	 * ITEMS
	 */
	public static final String ITEM_LUNCH_BOX_NAME = "lunchbox";
	public static final String ITEM_LUNCH_BAG_NAME = "lunchbag";

	public static void init(File file)
	{
		config = new Configuration(file);
		load();

		FOOD_CONTAINERS_MAX_STACKSIZE = config.get("server", FOOD_CONTAINERS_MAX_STACKSIZE_NAME, 
			FOOD_CONTAINERS_MAX_STACKSIZE_DEFAULT, FOOD_CONTAINERS_MAX_STACKSIZE_COMMENT).getInt();

		LEFT_CLICK_OPENS_FOOD_CONTAINERS = config.get("client", LEFT_CLICK_OPENS_FOOD_CONTAINERS_NAME, 
			LEFT_CLICK_OPENS_FOOD_CONTAINERS_DEFAULT, LEFT_CLICK_OPENS_FOOD_CONTAINERS_COMMENT).getBoolean();

		save();
	}

	public static void save()
	{
		config.save();
	}

	public static void load()
	{
		config.load();
	}
}
