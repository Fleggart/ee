package squeek.spiceoflife.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class ContainerGeneric extends Container
{
    protected IInventory inventory;
    protected int nextSlotIndex = 0;

    public ContainerGeneric(IInventory inventory)
    {
        this.inventory = inventory;
    }

    protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart)
    {
        addSlotsOfType(slotClass, inventory, xStart, yStart, inventory.getSizeInventory(), 1);
    }

    protected void addSlotsOfType(Class<? extends Slot> slotClass, IInventory inventory, int xStart, int yStart, int numSlots, int rows)
    {
        int numSlotsPerRow = numSlots / rows;
        for (int i = 0, col = 0, row = 0; i < numSlots; ++i, ++col)
        {
            if (col >= numSlotsPerRow)
            {
                row++;
                col = 0;
            }

            try
            {
                this.addSlotToContainer(slotClass.getConstructor(IInventory.class, int.class, int.class, int.class)
                    .newInstance(inventory, getNextSlotIndex(), xStart + col * 18, yStart + row * 18));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected int getNextSlotIndex()
    {
        nextSlotIndex++;
        return nextSlotIndex - 1;
    }

    protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int yStart)
    {
        addPlayerInventorySlots(playerInventory, 8, yStart);
    }

    protected void addPlayerInventorySlots(InventoryPlayer playerInventory, int xStart, int yStart)
    {
        for (int row = 0; row < 3; ++row)
        {
            for (int col = 0; col < 9; ++col)
            {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col)
        {
            this.addHotbarSlot(playerInventory, col, xStart + col * 18, yStart + 58);
        }
    }

    protected void addHotbarSlot(InventoryPlayer playerInventory, int slotNum, int x, int y)
    {
        this.addSlotToContainer(new Slot(playerInventory, slotNum, x, y));
    }

    @Override
    @Nonnull
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
        {
            int hotbarSlotIndex = this.inventorySlots.size() - 9 + dragType;
            Slot hotbarSlot = getSlot(hotbarSlotIndex);
            Slot swapSlot = getSlot(slotId);
            if (hotbarSlot instanceof SlotLocked || swapSlot instanceof SlotLocked)
            {
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
    {
        Slot slot = this.inventorySlots.get(slotNum);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stackToTransfer = slot.getStack();

            if (slotNum < this.inventory.getSizeInventory())
            {
                if (!this.mergeItemStack(stackToTransfer, this.inventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if (!this.mergeItemStack(stackToTransfer, 0, this.inventory.getSizeInventory(), false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if (stackToTransfer.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            return stackToTransfer;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player)
    {
        return inventory.isUsableByPlayer(player);
    }
}
