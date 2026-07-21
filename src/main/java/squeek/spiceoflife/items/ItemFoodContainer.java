package squeek.spiceoflife.items;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import squeek.spiceoflife.ModConfig;
import squeek.spiceoflife.ModInfo;
import squeek.spiceoflife.helpers.FoodHelper;
import squeek.spiceoflife.helpers.GuiHelper;
import squeek.spiceoflife.helpers.InventoryHelper;
import squeek.spiceoflife.helpers.MiscHelper;
import squeek.spiceoflife.inventory.ContainerFoodContainer;
import squeek.spiceoflife.inventory.FoodContainerInventory;
import squeek.spiceoflife.inventory.INBTInventoryHaver;
import squeek.spiceoflife.inventory.NBTInventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class ItemFoodContainer extends Item implements INBTInventoryHaver
{
    public static final String TAG_INVENTORY = "Inventory";
    public static final String TAG_OPEN = "Open";
    public static final String TAG_UUID = "UUID";
    
    public final int numSlots;
    public final String itemName;
    private static final Random RANDOM = new Random();

    public ItemFoodContainer(String itemName, int numSlots)
    {
        super();
        this.itemName = itemName;
        this.numSlots = numSlots;
        setMaxStackSize(1);
        setRegistryName(itemName);
        setTranslationKey(ModInfo.MODID + '.' + itemName);
        setCreativeTab(CreativeTabs.MISC);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels()
    {
        ModelLoader.setCustomMeshDefinition(this, stack -> {
            if (isOpen(stack)) {
                return isEmpty(stack) ? 
                    new ModelResourceLocation(getRegistryName() + "_open_empty", "inventory") :
                    new ModelResourceLocation(getRegistryName() + "_open_full", "inventory");
            }
            return new ModelResourceLocation(getRegistryName(), "inventory");
        });
    }

    public boolean isEmpty(@Nonnull ItemStack stack)
    {
        return NBTInventory.isInventoryEmpty(getInventoryTag(stack));
    }

    public boolean isFull(@Nonnull ItemStack stack)
    {
        return getInventory(stack).isInventoryFull();
    }

    public boolean isOpen(@Nonnull ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().getBoolean(TAG_OPEN);
    }

    public void setIsOpen(@Nonnull ItemStack stack, boolean open)
    {
        getOrInitBaseTag(stack).setBoolean(TAG_OPEN, open);
    }

    public UUID getUUID(@Nonnull ItemStack stack)
    {
        return getOrInitBaseTag(stack).getUniqueId(TAG_UUID);
    }

    private NBTTagCompound getOrInitBaseTag(@Nonnull ItemStack stack)
    {
        NBTTagCompound tag = stack.getOrCreateTag();
        if (!tag.hasKey(TAG_UUID))
            tag.setUniqueId(TAG_UUID, UUID.randomUUID());
        return tag;
    }

    private NBTTagCompound getInventoryTag(@Nonnull ItemStack stack)
    {
        NBTTagCompound tag = getOrInitBaseTag(stack);
        if (!tag.hasKey(TAG_INVENTORY))
            tag.setTag(TAG_INVENTORY, new NBTTagCompound());
        return tag.getCompoundTag(TAG_INVENTORY);
    }

    public FoodContainerInventory getInventory(@Nonnull ItemStack stack)
    {
        return new FoodContainerInventory(this, stack);
    }

    public void tryDumpFoodInto(@Nonnull ItemStack stack, IItemHandler target, EntityPlayer player)
    {
        FoodContainerInventory inv = getInventory(stack);
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);
            if (!slot.isEmpty()) {
                inv.setInventorySlotContents(i, InventoryHelper.insertStackIntoInventory(slot, target));
            }
        }
    }

    public void tryPullFoodFrom(@Nonnull ItemStack stack, IItemHandlerModifiable source, EntityPlayer player)
    {
        FoodContainerInventory inv = getInventory(stack);
        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack slot = source.getStackInSlot(i);
            if (!slot.isEmpty() && FoodHelper.isFood(slot)) {
                source.setStackInSlot(i, InventoryHelper.insertStackIntoInventoryOnce(slot, inv.getItemHandler()));
                if (inv.isInventoryFull()) break;
            }
        }
    }

    public boolean canBeEatenFrom(@Nonnull ItemStack stack)
    {
        return isOpen(stack) && !isEmpty(stack);
    }

    public boolean canPlayerEatFrom(EntityPlayer player, @Nonnull ItemStack stack)
    {
        return canBeEatenFrom(stack) && player.canEat(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
    {
        super.addInformation(stack, world, tooltip, flag);
        String key = isOpen(stack) ? "spiceoflife.tooltip.to.close.food.container" : "spiceoflife.tooltip.to.open.food.container";
        tooltip.add(TextFormatting.GRAY + I18n.format(key));
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && isOpen(stack)) {
            IItemHandler target = InventoryHelper.getInventoryAtLocation(world, pos);
            if (target instanceof IItemHandlerModifiable) {
                tryDumpFoodInto(stack, target, player);
                tryPullFoodFrom(stack, (IItemHandlerModifiable) target, player);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return canBeEatenFrom(stack) ? EnumAction.EAT : EnumAction.NONE;
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            setIsOpen(stack, !isOpen(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (canPlayerEatFrom(player, stack)) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if (!isOpen(stack) && hand == EnumHand.MAIN_HAND) {
            GuiHelper.openGuiOfItemStack(player, stack);
            setIsOpen(stack, true);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
    {
        if (entity.world.isRemote && ModConfig.LEFT_CLICK_OPENS_FOOD_CONTAINERS && MiscHelper.isMouseOverNothing()) {
            setIsOpen(stack, !isOpen(stack));
            return true;
        }
        return super.onEntitySwing(entity, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entity)
    {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            IItemHandlerModifiable handler = getInventory(stack).getItemHandler();
            
            int bestSlot = -1;
            float bestValue = -1;
            
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack food = handler.getStackInSlot(i);
                if (!food.isEmpty() && FoodHelper.isFood(food)) {
                    float value = getFoodValue(food);
                    if (value > bestValue) {
                        bestValue = value;
                        bestSlot = i;
                    }
                }
            }
            
            if (bestSlot != -1) {
                ItemStack food = handler.getStackInSlot(bestSlot);
                ItemStack result = food.onItemUseFinish(world, player);
                result = ForgeEventFactory.onItemUseFinish(player, food, 32, result);
                handler.setStackInSlot(bestSlot, result.isEmpty() ? ItemStack.EMPTY : result);
            }
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    private float getFoodValue(ItemStack stack)
    {
        if (!(stack.getItem() instanceof ItemFood)) return 0;
        ItemFood food = (ItemFood) stack.getItem();
        int hunger = food.getHealAmount(stack);
        float saturation = hunger * food.getSaturationModifier(stack) * 2;
        return hunger * saturation;
    }

    // ============ INBTInventoryHaver 接口 ============
    
    @Override public int getSizeInventory() { return numSlots; }
    @Override public String getInvName(NBTInventory inv) { return getTranslationKey() + ".name"; }
    @Override public boolean hasCustomName(NBTInventory inv) { return false; }
    @Override public int getInventoryStackLimit(NBTInventory inv) { return ModConfig.FOOD_CONTAINERS_MAX_STACKSIZE; }
    @Override public void onInventoryChanged(NBTInventory inv) {}
    @Override public boolean isItemValidForSlot(NBTInventory inv, int slot, @Nonnull ItemStack stack) {
        return FoodHelper.isFood(stack) && FoodHelper.isDirectlyEdible(stack);
    }
}
