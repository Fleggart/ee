package squeek.spiceoflife.helpers;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MiscHelper
{
    @SideOnly(Side.CLIENT)
    public static boolean isMouseOverNothing()
    {
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult mouseOver = mc.objectMouseOver;

        if (mouseOver == null || mouseOver.typeOfHit == RayTraceResult.Type.MISS)
            return true;
        else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            return mc.world.getBlockState(pos).getMaterial() == Material.AIR;
        }
        return false;
    }
}
