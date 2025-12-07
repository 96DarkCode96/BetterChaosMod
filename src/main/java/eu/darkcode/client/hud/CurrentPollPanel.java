package eu.darkcode.client.hud;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.VotingSystem;
import eu.darkcode.voting.PollDefinition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static eu.darkcode.utils.Utils.getVoteOptionIndex;
import static net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil.renderTooltipBackground;

public class CurrentPollPanel implements Hud {
    private static final int LINE_SPACE = 2;
    private static final int ENTRY_VERTICAL_PADDING = 4;
    private static final int MARGIN = 10;

    private final @NotNull List<ComputedEntry> entries = new ArrayList<>();
    private @Nullable PollDefinition poll;
    private int panelWidth = 100;
    private int panelHeight = 100;
    private int totalVotes = 0;

    public void newPoll(@NotNull PollDefinition poll) {
        this.poll = poll;
        recalculateEntries();
    }

    /**
     * Re-calculates all entries and caches them so it's not calculated on render
     */
    public void recalculateEntries() {
        if (this.poll == null)
            return;

        double[] previousPercentage = this.entries.stream().mapToDouble(e -> e.percentage).toArray();
        if (previousPercentage.length != poll.entries().size())
            previousPercentage = new double[poll.entries().size()];

        this.entries.clear();

        final VotingSystem votingSystem = BetterChaosModClient.instance().eventManager().votingSystem();
        final Font font = Minecraft.getInstance().font;

        this.totalVotes = votingSystem.totalVotes();
        this.panelWidth = BetterChaosModClient.instance().settings().hud.pollWidth();
        this.panelHeight = 2 * ENTRY_VERTICAL_PADDING + font.lineHeight;

        @NotNull List<Component> components = poll.entries();
        for (int i = 0; i < components.size(); i++) {
            List<FormattedCharSequence> list = font.split(components.get(i), panelWidth - 40); // TODO:

            int height = list.size() * (font.lineHeight + LINE_SPACE) - LINE_SPACE + 2 * ENTRY_VERTICAL_PADDING;
            this.panelHeight += height;

            this.entries.add(new ComputedEntry(list, getVoteOptionIndex(this.poll.pollId(), i), height, votingSystem.percentage(i), previousPercentage[i]));
        }
    }

    @Override
    public void render(HudSettings settings, @NotNull GuiGraphics graphics, @NotNull DeltaTracker deltaTracker) {
        if (poll == null)
            return;

        graphics.pose().pushMatrix();

        // [BACKGROUND]
        int y = settings.timerHeight() + MARGIN;
        renderTooltipBackground(graphics, MARGIN, y, panelWidth, panelHeight, null);

        // [TITLE]
        final Font font = Minecraft.getInstance().font;
        y += ENTRY_VERTICAL_PADDING;

        MutableComponent pollTitle = Component.translatable("betterchaosmod.poll.title", Component.literal("" + totalVotes));
        graphics.drawString(font, pollTitle, MARGIN + 4, y, 0xFFFFFFFF);

        y += ENTRY_VERTICAL_PADDING + font.lineHeight;

        graphics.fill(MARGIN + 4, y, MARGIN + this.panelWidth - 4, y + 1, 0xFFFFFFFF);

        y += 2;

        // [ENTRIES]
        for (ComputedEntry entry : this.entries) {
            renderEntry(graphics, deltaTracker, y, entry);
            y += entry.height;
        }
        graphics.pose().popMatrix();
    }

    @Override
    public boolean enabled(boolean hideGui) {
        return !hideGui;
    }

    private void renderEntry(@NotNull GuiGraphics graphics, @NotNull DeltaTracker delta, int y, @NotNull CurrentPollPanel.ComputedEntry entry) {
        final Font font = Minecraft.getInstance().font;

        double percDelta;
        if (entry.time < 1) {
            entry.time += .05;
            percDelta = entry.previousPercentage + entry.time * (entry.percentage - entry.previousPercentage);
        } else {
            percDelta = entry.percentage;
        }

        graphics.fill(MARGIN + 2, y + 2, MARGIN + 2 + (int) ((panelWidth - 4) * percDelta), y + entry.height - 4, BetterChaosModClient.instance().settings().hud.pollBarBackgroundColor());

        y += ENTRY_VERTICAL_PADDING;
        final int color = BetterChaosModClient.instance().settings().hud.pollBarForegroundColor();

        graphics.drawString(font, entry.optionNumber + ".", MARGIN + 4, y, color, false);

        String percentage = String.format("%.0f%%", entry.percentage * 100);
        int w = font.width(percentage);
        graphics.drawString(font, percentage, MARGIN - 4 + panelWidth - w, y, color, false);

        for (FormattedCharSequence line : entry.lines) {
            graphics.drawString(font, line, MARGIN + 16, y, color, false);
            y += font.lineHeight + LINE_SPACE;
        }
    }

    private static final class ComputedEntry {
        private final @NotNull List<FormattedCharSequence> lines;
        private final int optionNumber;
        private final int height;
        private final double percentage;
        private final double previousPercentage;
        private double time = 0.0;

        private ComputedEntry(@NotNull List<FormattedCharSequence> lines, int optionNumber, int height, double percentage, double previousPercentage) {
            this.lines = lines;
            this.optionNumber = optionNumber;
            this.height = height;
            this.percentage = percentage;
            this.previousPercentage = previousPercentage;
        }

    }
}
