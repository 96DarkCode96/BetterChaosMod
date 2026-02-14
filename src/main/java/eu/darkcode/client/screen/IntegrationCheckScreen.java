package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosModClient;
import eu.darkcode.client.EventManager;
import eu.darkcode.client.voting.integration.Integration;
import eu.darkcode.client.voting.integration.IntegrationSettings;
import eu.darkcode.client.voting.integration.TokenServer;
import eu.darkcode.client.voting.integration.kick.KickIntegration;
import eu.darkcode.client.voting.integration.twitch.irc.IRCTest;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegration;
import eu.darkcode.client.widgets.OreButton;
import eu.darkcode.client.widgets.config.OreSwitchOption;
import eu.darkcode.client.widgets.container.OrePanel;
import eu.darkcode.networking.configuration.ServerboundIntegrationPass;
import eu.darkcode.voting.IntegrationType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class IntegrationCheckScreen extends BaseScreen {

    private final @NotNull PacketSender packetSender;
    private final @NotNull HashSet<IntegrationType> enabled = new HashSet<>();
    private final @NotNull IntegrationSettings settings;
    private @Nullable CompletableFuture<Map<IntegrationType, Integration>> submitFuture = null;
    private OreButton connectButton;

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
        final OrePanel panel = new OrePanel(18, 12, 18, 12, 4);

        panel.addChild(new OreSwitchOption(Component.literal("Twitch"), null,
                enabled.contains(IntegrationType.TWITCH), value -> toggle(value, IntegrationType.TWITCH), true));
        panel.addChild(new OreSwitchOption(Component.literal("Kick"), null,
                enabled.contains(IntegrationType.KICK), value -> toggle(value, IntegrationType.KICK), true));
        connectButton = panel.addChild(new OreButton(0, 0, 150, 20, Component.literal("Connect")) {
            @Override
            public void onPress(InputWithModifiers inputWithModifiers) {
                submit(enabled);
            }
        });
        if (submitFuture != null && !submitFuture.isDone()) {
            connectButton.setActive(false);
            connectButton.setLabel(Component.literal("Connecting..."));
        }

        this.layout.addBackButton();
        this.layout.addToContents(panel);
        super.init();
    }

    @Override
    public void onBack() {
        packetSender.disconnect(Component.literal("Integration check cancelled by user."));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private void toggle(boolean on, @NotNull IntegrationType type) {
        if (on)
            enabled.add(type);
        else
            enabled.remove(type);
    }

    public void submit(@NotNull Set<IntegrationType> enabled) {
        if (submitFuture != null && !submitFuture.isDone())
            return;

        connectButton.setActive(false);
        connectButton.setLabel(Component.literal("Connecting..."));

        CompletableFuture<Map<IntegrationType, Integration>> chain = CompletableFuture.completedFuture(new EnumMap<>(IntegrationType.class));

        // [TWITCH]
        if (enabled.contains(IntegrationType.TWITCH))
            chain = chain.thenCombine(CompletableFuture.supplyAsync(() -> {
                IRCTest.TestResult result = IRCTest.testConnection(settings.twitchIntegration).join();

                if (result == IRCTest.TestResult.AUTHENTICATION_FAILED && TokenServer.refreshTwitchToken().join())
                    result = IRCTest.testConnection(settings.twitchIntegration).join();

                if (result == IRCTest.TestResult.SUCCESS)
                    return new TwitchIntegration(settings.twitchIntegration);

                return null;
            }), (prev, current) -> {
                if (current != null)
                    prev.put(IntegrationType.TWITCH, current);
                return prev;
            });
        // [KICK]
        if (enabled.contains(IntegrationType.KICK))
            chain = chain.thenCombine(CompletableFuture.supplyAsync(() -> {
                try {
                    return new KickIntegration(settings.kickIntegration);
                } catch (Exception e) {
                    BetterChaosModClient.LOGGER.error("Kick Integration failed", e);
                    return null;
                }
            }), (prev, current) -> {
                if (current != null)
                    prev.put(IntegrationType.KICK, current);
                return prev;
            });

        submitFuture = chain;
        submitFuture.whenComplete((success, err) -> minecraft.execute(() -> {
            EventManager eventManager = BetterChaosModClient.instance().eventManager();
            if (eventManager == null || err != null || success == null || success.size() != enabled.size()) {
                BetterChaosModClient.LOGGER.error("Integration check failed!", err);
                packetSender.disconnect(Component.literal("Integration check failed!\n\nSome of enabled integrations failed to start! Check your configuration for valid access tokens!"));

                if (success != null)
                    success.forEach((type, integration) -> integration.shutdown());

                return;
            }
            eventManager.votingSystem().setActiveIntegrations(success.values().stream().toList());
            packetSender.sendPacket(new ServerboundIntegrationPass(enabled.isEmpty() ? EnumSet.noneOf(IntegrationType.class) : EnumSet.copyOf(enabled)));
            onClose();
        }));
    }
}
