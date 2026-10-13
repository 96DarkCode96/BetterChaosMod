package eu.darkcode.event.impl.inventory;

import eu.darkcode.utils.Utils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * @author _dark_code_
 * @date 12.10.2026
 **/
public class LockableSlot extends Slot {

    private static final @NotNull Identifier LOCKED_SLOT = Utils.resourceLocation("container/slot/locked");

    public LockableSlot(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public @Nullable Identifier getNoItemIcon() {
        return isLocked() ? LOCKED_SLOT : null;
    }

    @Override
    public boolean isFake() {
        return isLocked();
    }

    @Override
    public boolean isHighlightable() {
        return !isLocked();
    }

    @Override
    public boolean mayPickup(@NonNull Player player) {
        return !isLocked();
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack itemStack) {
        return !isLocked();
    }

    public boolean isLocked() {
        return container instanceof Inventory i && ((Lockable) i.player).betterchaosmod$isSlotLocked(getContainerSlot());
    }

}
