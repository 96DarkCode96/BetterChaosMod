package eu.darkcode.event.impl;

import eu.darkcode.event.Event;
import eu.darkcode.event.EventRegistry;
import eu.darkcode.event.TimedEvent;
import eu.darkcode.server.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

/**
 * @author darkcode
 * @date 08.12.25
 **/
public class SwapHands extends TimedEvent {

    public static final EventRegistry.Entry<?> ENTRY = EventRegistry.entry("swap_hands", SwapHands::new).build();

    private HumanoidArm humanoidArm;

    @Override
    public void start(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void end(@NotNull EventManager manager) {
        // ignored
    }

    @Override
    public void startClient() {
        super.startClient();
        OptionInstance<HumanoidArm> opt = Minecraft.getInstance().options.mainHand();
        humanoidArm = opt.get();
        opt.set(humanoidArm.getOpposite());
    }

    @Override
    public void endClient() {
        super.endClient();
        if (humanoidArm != null)
            Minecraft.getInstance().options.mainHand().set(humanoidArm);
    }

    @Override
    public void tickClient() {
        super.tickClient();
        if (humanoidArm != null)
            Minecraft.getInstance().options.mainHand().set(humanoidArm.getOpposite());
    }

    @Override
    public @NotNull EventRegistry.Entry<? extends Event> entry() {
        return ENTRY;
    }
}