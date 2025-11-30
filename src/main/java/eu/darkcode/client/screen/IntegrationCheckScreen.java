package eu.darkcode.client.screen;

import eu.darkcode.client.widgets.OreButton;
import eu.darkcode.client.widgets.OreLabel;
import eu.darkcode.client.widgets.OrePanel;
import eu.darkcode.networking.configuration.ServerboundIntegrationPass;
import eu.darkcode.voting.IntegrationType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class IntegrationCheckScreen extends BaseScreen {

    private final @NotNull PacketSender packetSender;
    private final @NotNull HashSet<IntegrationType> enabled = new HashSet<>();

    public IntegrationCheckScreen(@Nullable Screen parent, @NotNull PacketSender packetSender) {
        super(parent, Component.translatable("betterchaosmod.integration_check.title"));
        this.packetSender = packetSender;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        final Font font = Minecraft.getInstance().font;

        final OrePanel panel = new OrePanel();
        panel.defaultCellSetting().paddingHorizontal(18).paddingVertical(12);
        panel.spacing(4);

        panel.addChild(new OreLabel(Component.literal("Checking..."), font));
        panel.addChild(new OreButton(0, 0, 150, 20, Component.literal("Connect")) {
            @Override
            public void onPress(InputWithModifiers inputWithModifiers) {
                submit(enabled);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

            }
        });

        this.layout.addBackButton();
        this.layout.addToContents(panel);
        super.init();
    }

    @Override
    public void onBack() {
        packetSender.disconnect(Component.literal("Integration check cancelled by user."));
    }

    public void submit(@NotNull Set<IntegrationType> enabled) {
        packetSender.sendPacket(new ServerboundIntegrationPass(enabled.isEmpty() ? EnumSet.noneOf(IntegrationType.class) : EnumSet.copyOf(enabled)));
        onClose();
    }

    /*
    Integration integration = null;

        IntegrationSettings integrationSettings = BetterChaosModClient.instance().settings().integration;
        if (integrationSettings.twitchEnabled)
            integration = new TwitchIntegration(integrationSettings.twitchIntegration);

        final IntegrationSettings integration = settings.integration;
        final HashSet<IntegrationType> resultSet = new HashSet<>();

        CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);

        // [TWITCH]
        if (integration.twitchEnabled) {
            resultSet.add(IntegrationType.TWITCH);
            chain = chain.thenCombine(IRCClient.testConnection(integration.twitchIntegration), (prev, current) -> prev && current);
        }

        chain.whenComplete((success, err) -> minecraft.execute(() -> {
            System.out.println("Response: " + success + " | Error: " + err);
            if (err != null || !success) {
                context.responseSender().disconnect(Component.literal("Integration check failed!"));
                return;
            }
            context.responseSender().sendPacket(new ServerboundIntegrationPass(resultSet.isEmpty() ? EnumSet.noneOf(IntegrationType.class) : EnumSet.copyOf(resultSet)));
        }));
     */
}
