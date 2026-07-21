package squeek.spiceoflife;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import squeek.spiceoflife.helpers.GuiHelper;
import squeek.spiceoflife.helpers.MovementHelper;

@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION)
public class ModSpiceOfLife
{
	public static final Logger Log = LogManager.getLogger(ModInfo.MODID);

	@Instance(ModInfo.MODID)
	public static ModSpiceOfLife instance;
	public File sourceFile;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		sourceFile = event.getSourceFile();
		ModConfig.init(event.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GuiHelper.init();
		MovementHelper.init();
		MinecraftForge.EVENT_BUS.register(new FoodContainerHandler());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		// 不需要版本检查
	}
}
