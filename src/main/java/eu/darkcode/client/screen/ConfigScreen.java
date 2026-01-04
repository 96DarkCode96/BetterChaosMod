package eu.darkcode.client.screen;

import eu.darkcode.BetterChaosMod;
import eu.darkcode.BetterChaosModClient;
import eu.darkcode.Settings;
import eu.darkcode.UpdateStatus;
import eu.darkcode.client.ClientSettings;
import eu.darkcode.client.hud.HudSettings;
import eu.darkcode.client.overlay.OverlayWebServer;
import eu.darkcode.client.voting.integration.twitch.TwitchIntegrationSettings;
import eu.darkcode.client.voting.integration.twitch.TwitchTokenServer;
import eu.darkcode.client.widgets.OreButton;
import eu.darkcode.client.widgets.config.OreInputOption;
import eu.darkcode.client.widgets.config.OreSidebarOption;
import eu.darkcode.client.widgets.container.OrePanel;
import eu.darkcode.event.EventRegistry;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.darkcode.client.widgets.impl.OreSlider.*;
import static eu.darkcode.client.widgets.utils.Ore.PASSWORD_FORMATTER;

public class ConfigScreen extends BaseScreen {
    private final @NotNull Settings settings;
    private final @NotNull ClientSettings clientSettings;
    private @NotNull Category category = Category.GENERAL;
    private OrePanel panel = null;
    private OreInputOption overlayWebPort;

    public ConfigScreen(OptionsScreen parent) {
        super(parent, Component.translatable("betterchaosmod.config.title"));
        settings = BetterChaosMod.instance().settings();
        clientSettings = BetterChaosModClient.instance().settings();
    }

    @Override
    protected void init() {
        OrePanel sidebar = new OrePanel(1, 7, 1, 7, 0);
        for (Category value : Category.values()) {
            OreSidebarOption option = sidebar.addSidebarOption(value.key(), value.sprite(), category == value, () -> {
                category = value;
                reinit();
            });
            if (category == value)
                setInitialFocus(option);
        }

        UpdateStatus updateStatus = BetterChaosModClient.instance().updateStatus;
        if (updateStatus == UpdateStatus.OUTDATED) {
            sidebar.addChild(new OreButton(0, 0, 100, 20, Component.literal("Update available!")) {
                @Override
                public void onPress(InputWithModifiers inputWithModifiers) {
                    Util.getPlatform().openUri(UpdateStatus.UPDATE_LINK);
                }
            }, a -> a.margin(10, 0, 10, 0));
        }

        int preserveScroll = 0;
        if (panel != null)
            preserveScroll = panel.scrollAmount();
        panel = new OrePanel();

        switch (category) {
            case Category.GENERAL -> generalConfig();
            case Category.GRAPHICS -> clientConfig();
            default -> integrationConfig();
        }

        this.layout.addBackButton();
        this.layout.addToContents(sidebar);
        this.layout.addToContents(panel);
        super.init();
        panel.scrollTo(preserveScroll);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        this.layout.scrollContent(d, e, f, g);
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public void onClose() {
        // [SAVE SERVER SETTINGS]
        BetterChaosMod.instance().saveSettings();
        // [SAVE CLIENT SETTINGS]
        BetterChaosModClient.instance().saveSettings();
        super.onClose();
    }

    public void reinit() {
        this.clearWidgets();
        this.clearFocus();
        this.init();
    }

    private void generalConfig() {
        boolean enabledOptions = Minecraft.getInstance().level == null;
        panel.addSlider(
                "server.duration.event",
                "server.duration.event_desc",
                settings.eventDuration / 20, 10, 120,
                FORMATTER_SECONDS,
                a -> settings.eventDuration = a * 20,
                enabledOptions
        );
        panel.addSlider(
                "server.duration.timer",
                "server.duration.timer_desc",
                settings.timerDuration / 20, 15, 300,
                FORMATTER_SECONDS,
                a -> settings.timerDuration = a * 20,
                enabledOptions
        );
        panel.addSlider(
                "server.repetition_cooldown",
                "server.repetition_cooldown.desc",
                settings.eventRepetitionCooldown, 3, 10,
                i -> Component.literal(i + " cycles"),
                a -> settings.eventRepetitionCooldown = a,
                enabledOptions
        );
        panel.addCategory("server.accessibility", null);
        panel.addSwitch("server.accessibility", "server.accessibility.desc", settings.disableAccessibility, b -> {
            settings.disableAccessibility = b;
            reinit();
        }, enabledOptions);
        panel.addCategory(
                Component.translatable("betterchaosmod.option.category.server.events"),
                Component.literal("Total: " + EventRegistry.EVENTS.size() + " events")
        );
        boolean minEvents = EventRegistry.EVENTS.size() - settings.disabledEvents.size() <= 10;
        for (EventRegistry.Entry<?> event : EventRegistry.EVENTS) {
            boolean enabled = event.enabled(settings);
            panel.addSwitch(Component.translatable("betterchaosmod.event." + event.key()), null, enabled, b -> {
                if (Boolean.FALSE.equals(b))
                    settings.disabledEvents.add(event.key());
                else
                    settings.disabledEvents.removeIf(a -> a.equals(event.key()));
                reinit();
            }, enabledOptions && !event.disabledByAccessibility(settings) && (!enabled || !minEvents));
        }
    }

    private void clientConfig() {
        final HudSettings hud = clientSettings.hud;
        panel.addCategory("client.timer", null);
        panel.addSlider(
                "client.timer.height",
                null,
                hud.timerHeight(), 5, 40,
                FORMATTER_PIXELS,
                hud::timerHeight,
                true
        );
        panel.addColorOption("client.timer.foreground", null, hud.timerForegroundColor(), hud::timerForegroundColor, true);
        panel.addColorOption("client.timer.background", null, hud.timerBackgroundColor(), hud::timerBackgroundColor, true);
        panel.addCategory("client.poll", null);
        panel.addSlider(
                "client.poll.width",
                null,
                hud.pollWidth(), 150, 400,
                FORMATTER_PIXELS,
                hud::pollWidth,
                true
        );
        panel.addColorOption("client.poll.bar.foreground", null, hud.pollBarForegroundColor(), hud::pollBarForegroundColor, true);
        panel.addColorOption("client.poll.bar.background", null, hud.pollBarBackgroundColor(), hud::pollBarBackgroundColor, true);
        panel.addSlider(
                "client.activeEvents.maxEntries",
                null,
                hud.activeEventsMaxEntries(), 4, 12,
                FORMATTER_PLAIN,
                hud::activeEventsMaxEntries,
                true
        );
    }

    private void integrationConfig() {
        panel.addCategory("integration.twitch", "integration.twitch.description");

        boolean enabledOptions = Minecraft.getInstance().level == null;
        TwitchIntegrationSettings twitchIntegration = clientSettings.integration.twitchIntegration;

        OreInputOption channelName = panel.addTextInput("client.integration.twitch.channel", null, twitchIntegration.channel, a -> twitchIntegration.channel = a, enabledOptions);
        OreInputOption channelToken = panel.addTextInput("client.integration.twitch.token", null, twitchIntegration.token, a -> twitchIntegration.token = a, enabledOptions);
        channelToken.textInput().addFormatter(PASSWORD_FORMATTER);

        panel.addSwitch("client.integration.twitch.announceNewPolls", "client.integration.twitch.announceNewPolls.description", twitchIntegration.announceNewPolls, a -> twitchIntegration.announceNewPolls = a, enabledOptions);
        panel.addSwitch("client.integration.twitch.ignoreSharedChat", "client.integration.twitch.ignoreSharedChat.description", twitchIntegration.ignoreSharedChat, a -> twitchIntegration.ignoreSharedChat = a, enabledOptions);

        panel.addChild(new OreButton(0, 0, 150, 20, Component.translatable("betterchaosmod.option.client.integration.twitch.getToken")) {
            @Override
            public void onPress(InputWithModifiers inputWithModifiers) {
                TwitchTokenServer.openUrl();
            }
        }, widget -> {
            widget.marginLeft(20);
            widget.marginRight(20);
        }).setActive(enabledOptions);

        overlayConfig();
    }

    private void overlayConfig() {
        panel.addCategory("integration.overlay", "integration.overlay.description");
        panel.addSwitch("client.overlay.enabled", null, clientSettings.overlay.enabled, a -> {
            clientSettings.overlay.enabled = a;
            overlayWebPort.textInput().setActive(!a);
            if (clientSettings.overlay.enabled)
                OverlayWebServer.startServer(clientSettings.overlay.webServerPort);
            else
                OverlayWebServer.stopServer();
        }, true);
        overlayWebPort = panel.addTextInput("client.overlay.webPort", null, String.valueOf(clientSettings.overlay.webServerPort), a -> {
            try {
                int port = Integer.parseInt(a);
                if (port >= 1024 && port <= 65535)
                    clientSettings.overlay.webServerPort = port;
                else
                    overlayWebPort.textInput().setValue("" + clientSettings.overlay.webServerPort);
            } catch (NumberFormatException ignored) {
            }
        }, !clientSettings.overlay.enabled);
        overlayWebPort.textInput().setFilter(s -> {
            if (s.isEmpty())
                return true;
            if (s.startsWith("0"))
                return false;
            try {
                return Integer.parseInt(s) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    private enum Category {
        GENERAL("general", "server"),
        GRAPHICS("graphics", "client"),
        INTEGRATION("integration", "integration");
        private final @NotNull String key;
        private final @Nullable String sprite;

        Category(@NotNull String key, @Nullable String sprite) {
            this.key = key;
            this.sprite = sprite;
        }

        public @NotNull String key() {
            return key;
        }

        public @Nullable String sprite() {
            return sprite;
        }
    }

}
