package eu.darkcode.event;

import eu.darkcode.client.screen.GameLogScreen;
import eu.darkcode.server.EventManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public interface Event extends GameLogScreen.GameLogEntry {

    void start(@NotNull EventManager manager);

    @Environment(EnvType.CLIENT)
    void startClient();

    void tick(@NotNull EventManager manager);

    @Environment(EnvType.CLIENT)
    void tickClient();

    void end(@NotNull EventManager manager);

    void endPlayer(@NotNull EventManager manager, @NotNull ServerPlayer player);

    @Environment(EnvType.CLIENT)
    void endClient();

    int currentTick();

    int duration();

    boolean ended();

    void ended(boolean ended);

    @NotNull
    EventRegistry.Entry<? extends Event> entry();

    @NotNull
    default Component getName() {
        return Component.translatable("betterchaosmod.event." + entry().key());
    }

    @NotNull
    default Component getVoteName() {
        return getName();
    }

    @Override
    default int renderGameLogEntry(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, int x, int y, int width) {
        Font font = Minecraft.getInstance().font;
        graphics.text(font, getName().getString(), x + 10, y + 5, 0xFFFFFFFF);

        if (this instanceof TimedEvent) {
            int sec = duration() / 20;
            String time = String.format("[%02d:%02d]", sec / 60, sec % 60);
            int textWidth = font.width(time);
            graphics.text(font, time, x + width - 10 - textWidth, y + 5, 0xFF9CA1A8);
        }

        return 30;
    }

    default boolean renderDurationBar() {
        return true;
    }
}
