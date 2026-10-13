package eu.darkcode.event.impl.inventory;

/**
 * @author _dark_code_
 * @date 12.10.2026
 **/
public interface Lockable {

    int betterchaosmod$getLockedSlots();

    void betterchaosmod$setLockedSlots(int slots);

    default boolean betterchaosmod$isSlotLocked(int slot) {
        return slot >= 9 && slot < 9 + betterchaosmod$getLockedSlots();
    }

    default void betterchaosmod$lockSlots(int amount) {
        betterchaosmod$setLockedSlots(betterchaosmod$getLockedSlots() + amount);
    }

    default void betterchaosmod$unlockSlots(int amount) {
        betterchaosmod$setLockedSlots(betterchaosmod$getLockedSlots() - amount);
    }

}
