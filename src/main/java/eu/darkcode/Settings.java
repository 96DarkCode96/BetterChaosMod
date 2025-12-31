package eu.darkcode;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public final class Settings {
    public static final StreamCodec<FriendlyByteBuf, Settings> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, Settings settings) {
            buf.writeInt(settings.timerDuration);
            buf.writeInt(settings.eventDuration);
            buf.writeInt(settings.eventRepetitionCooldown);
        }

        @Override
        public @NotNull Settings decode(FriendlyByteBuf buf) {
            Settings settings = new Settings();
            settings.timerDuration = buf.readInt();
            settings.eventDuration = buf.readInt();
            settings.eventRepetitionCooldown = buf.readInt();
            return settings;
        }
    };
    public int timerDuration = 900; // 45s
    public int eventDuration = 600; // 30s
    public int eventRepetitionCooldown = 6; // Event is held as active for 6 timer cycles, then can be picked again
}
