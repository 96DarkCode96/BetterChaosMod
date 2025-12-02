package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.voting.integration.IntegrationSettings;
import eu.darkcode.client.voting.integration.twitch.IRCClient;
import eu.darkcode.client.widgets.OreButton;
import eu.darkcode.client.widgets.container.OrePanel;
import eu.darkcode.client.widgets.impl.OreLabel;
import eu.darkcode.networking.configuration.ServerboundIntegrationPass;
import eu.darkcode.voting.IntegrationType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class IntegrationCheckScreen extends BaseScreen {

    private final @NotNull PacketSender packetSender;
    private final @NotNull HashSet<IntegrationType> enabled = new HashSet<>();
    private final @NotNull IntegrationSettings settings;

    public IntegrationCheckScreen(@Nullable Screen parent, @NotNull PacketSender packetSender) {
        super(parent, Component.translatable("betterchaosmod.integration_check.title"));
        this.packetSender = packetSender;
        this.settings = BetterChaosModClient.instance().settings().integration;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void init() {
        final Font font = Minecraft.getInstance().font;

        final OrePanel panel = new OrePanel(18, 12, 18, 12, 4);

        panel.addChild(new OreLabel(Component.literal("Checking..."), font));
        panel.addChild(new OreButton(0, 0, 150, 20, Component.literal("Connect")) {
            @Override
            public void onPress(InputWithModifiers inputWithModifiers) {
                submit(enabled);
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
        CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);

        // [TWITCH]
        if (enabled.contains(IntegrationType.TWITCH))
            chain = chain.thenCombine(IRCClient.testConnection(settings.twitchIntegration), (prev, current) -> prev && current);

        chain.whenComplete((success, err) -> minecraft.execute(() -> {
            System.out.println("Response: " + success + " | Error: " + err);
            if (err != null || !success) {
                packetSender.disconnect(Component.literal("Integration check failed!"));
                return;
            }
            packetSender.sendPacket(new ServerboundIntegrationPass(enabled.isEmpty() ? EnumSet.noneOf(IntegrationType.class) : EnumSet.copyOf(enabled)));
            onClose();
        }));
    }
}
