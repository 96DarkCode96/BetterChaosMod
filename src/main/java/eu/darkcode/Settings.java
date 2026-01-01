package eu.darkcode;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Settings {
    public static final StreamCodec<FriendlyByteBuf, Settings> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, Settings settings) {
            buf.writeInt(settings.timerDuration);
            buf.writeInt(settings.eventDuration);
            buf.writeInt(settings.eventRepetitionCooldown);
            buf.writeCollection(settings.disabledEvents, ByteBufCodecs.STRING_UTF8);
        }

        @Override
        public @NotNull Settings decode(FriendlyByteBuf buf) {
            Settings settings = new Settings();
            settings.timerDuration = buf.readInt();
            settings.eventDuration = buf.readInt();
            settings.eventRepetitionCooldown = buf.readInt();
            settings.disabledEvents = buf.readCollection(ArrayList::new, ByteBufCodecs.STRING_UTF8);
            return settings;
        }
    };
    public int timerDuration = 900; // 45s
    public int eventDuration = 600; // 30s
    public int eventRepetitionCooldown = 6; // Event is held as active for 6 timer cycles, then can be picked again
    public @NotNull List<String> disabledEvents = new ArrayList<>(); // Event keys of disabled events
    public boolean disableAccessibility = false;
}
