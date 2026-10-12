package eu.darkcode.client.widgets.impl;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import eu.darkcode.client.widgets.OreWidget;
import eu.darkcode.utils.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OreTextInput extends OreWidget {
    private static final Identifier SPRITE = Utils.resourceLocation("widget/text_field");
    private final @NotNull Font font;
    private final List<TextFormatter> formatters = new ArrayList<>();
    private String value = "";
    private int maxLength = 32;
    private boolean canLoseFocus = true;
    private boolean centered = false;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 0xffE0E0E0;
    private int textColorUneditable = 0xFF707070;
    @Nullable
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;
    private long focusedTime = Util.getMillis();
    private int textX;
    private int textY;

    public OreTextInput(@NotNull String text, @NotNull Consumer<String> valueConsumer) {
        super(0, 0, 340, 20);
        this.font = Minecraft.getInstance().font;

        setValue(text);
        setResponder(valueConsumer);

        this.updateTextPosition();
    }

    public void setResponder(@Nullable Consumer<String> consumer) {
        this.responder = consumer;
    }

    public void addFormatter(TextFormatter textFormatter) {
        this.formatters.add(textFormatter);
    }

    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.editBox", Component.empty(), this.value);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphicsExtractor g, int i, int j, float f) {
        // [FOCUSED BORDER]
        if (isFocused()) {
            g.fill(getX() - 2, getY() - 2, getRight() + 2, getY() - 1, 0xFFFFFFFF);
            g.fill(getX() - 2, getBottom() + 1, getRight() + 2, getBottom() + 2, 0xFFFFFFFF);
            g.fill(getX() - 2, getY() - 1, getX() - 1, getBottom() + 1, 0xFFFFFFFF);
            g.fill(getRight() + 1, getY() - 1, getRight() + 2, getBottom() + 1, 0xFFFFFFFF);
        }
        // [SPRITE BACKGROUND]
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        int k = this.isActive() ? this.textColor : this.textColorUneditable;
        int l = this.cursorPos - this.displayPos;
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean bl = l >= 0 && l <= string.length();
        boolean bl2 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && bl;
        int m = this.textX;
        int n = Mth.clamp(this.highlightPos - this.displayPos, 0, string.length());
        if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, l) : string;
            FormattedCharSequence formattedCharSequence = this.applyFormat(string2, this.displayPos);
            g.text(this.font, formattedCharSequence, m, this.textY, k, false);
            m += this.font.width(formattedCharSequence) + 1;
        }

        boolean bl3 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int o = m;
        if (!bl) {
            o = l > 0 ? this.textX + this.getWidth() : this.textX;
        } else if (bl3) {
            o = m - 1;
            m--;
        }

        if (!string.isEmpty() && bl && l < string.length()) {
            g.text(this.font, this.applyFormat(string.substring(l), this.cursorPos), m, this.textY, k, false);
        }

        if (n != l) {
            int p = this.textX + this.font.width(string.substring(0, n));
            g.textHighlight(Math.min(o, getRight()), this.textY - 1, Math.min(p - 1, getRight()), this.textY + 1 + 9, true);
        }

        if (bl2) {
            if (bl3) {
                g.fill(o, this.textY - 1, o + 1, this.textY + 1 + 9, k);
            } else {
                g.text(this.font, "_", o, this.textY, k, false);
            }
        }

        if (this.isHovered())
            g.requestCursor(isActive() ? CursorTypes.IBEAM : CursorTypes.NOT_ALLOWED);
    }

    @Override
    public void updateNarration(@NonNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    @Override
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (bl) {
            this.selectWord(mouseButtonEvent);
        } else {
            this.moveCursorTo(this.findClickedPositionInText(mouseButtonEvent), mouseButtonEvent.hasShiftDown());
        }
    }

    @Override
    protected void onDrag(MouseButtonEvent mouseButtonEvent, double d, double e) {
        this.moveCursorTo(this.findClickedPositionInText(mouseButtonEvent), true);
    }

    @Override
    public void playDownSound(@NotNull SoundManager soundManager) {
        // ignored
    }

    @Override
    public void setFocused(boolean bl) {
        if (!this.canLoseFocus && !bl)
            return;
        super.setFocused(bl);
        if (bl)
            this.focusedTime = Util.getMillis();
    }

    @Override
    public void setX(int i) {
        super.setX(i);
        this.updateTextPosition();
    }

    @Override
    public void setY(int i) {
        super.setY(i);
        this.updateTextPosition();
    }

    public void setValue(String string) {
        if (!this.filter.test(string))
            return;

        if (string.length() > this.maxLength) {
            this.value = string.substring(0, this.maxLength);
        } else {
            this.value = string;
        }

        this.moveCursorToEnd(false);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(string);
    }

    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    public void setFilter(Predicate<String> predicate) {
        this.filter = predicate;
    }

    public void insertText(String string) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        if (k <= 0)
            return;
        String string2 = StringUtil.filterText(string);
        int l = string2.length();
        if (k < l) {
            if (Character.isHighSurrogate(string2.charAt(k - 1))) {
                k--;
            }

            string2 = string2.substring(0, k);
            l = k;
        }

        String string3 = new StringBuilder(this.value).replace(i, j, string2).toString();
        if (this.filter.test(string3)) {
            this.value = string3;
            this.setCursorPosition(i + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
        }
    }

    private void onValueChange(String string) {
        if (this.responder != null)
            this.responder.accept(string);
        this.updateTextPosition();
    }

    private void deleteText(int i, boolean bl) {
        if (bl)
            this.deleteWords(i);
        else
            this.deleteChars(i);
    }

    public void deleteWords(int i) {
        if (this.value.isEmpty())
            return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
        } else {
            this.deleteCharsToPos(this.getWordPosition(i));
        }
    }

    public void deleteChars(int i) {
        this.deleteCharsToPos(this.getCursorPos(i));
    }

    public void deleteCharsToPos(int i) {
        if (this.value.isEmpty())
            return;
        if (this.highlightPos != this.cursorPos) {
            this.insertText("");
            return;
        }
        int j = Math.min(i, this.cursorPos);
        int k = Math.max(i, this.cursorPos);
        if (j == k)
            return;
        String string = new StringBuilder(this.value).delete(j, k).toString();
        if (!this.filter.test(string))
            return;
        this.value = string;
        this.moveCursorTo(j, false);
    }

    public int getWordPosition(int i) {
        return this.getWordPosition(i, this.getCursorPosition());
    }

    private int getWordPosition(int i, int j) {
        int k = j;
        boolean bl2 = i < 0;
        int l = Math.abs(i);

        for (int m = 0; m < l; m++) {
            if (!bl2) {
                int n = this.value.length();
                k = this.value.indexOf(32, k);
                if (k == -1) {
                    k = n;
                } else {
                    while (k < n && this.value.charAt(k) == ' ') {
                        k++;
                    }
                }
            } else {
                while (k > 0 && this.value.charAt(k - 1) == ' ') {
                    k--;
                }

                while (k > 0 && this.value.charAt(k - 1) != ' ') {
                    k--;
                }
            }
        }

        return k;
    }

    public void moveCursor(int i, boolean bl) {
        this.moveCursorTo(this.getCursorPos(i), bl);
    }

    private int getCursorPos(int i) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, i);
    }

    public void moveCursorTo(int i, boolean bl) {
        this.setCursorPosition(i);
        if (!bl)
            this.setHighlightPos(this.cursorPos);

        this.onValueChange(this.value);
    }

    public void moveCursorToStart(boolean bl) {
        this.moveCursorTo(0, bl);
    }

    public void moveCursorToEnd(boolean bl) {
        this.moveCursorTo(this.value.length(), bl);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent keyEvent) {
        if (!this.isActive() || !this.isFocused())
            return false;

        switch (keyEvent.key()) {
            case 259 -> {
                this.deleteText(-1, keyEvent.hasControlDown());
                return true;
            }
            case 261 -> {
                this.deleteText(1, keyEvent.hasControlDown());
                return true;
            }
            case 262 -> {
                if (keyEvent.hasControlDown())
                    this.moveCursorTo(this.getWordPosition(1), keyEvent.hasShiftDown());
                else
                    this.moveCursor(1, keyEvent.hasShiftDown());
                return true;
            }
            case 263 -> {
                if (keyEvent.hasControlDown())
                    this.moveCursorTo(this.getWordPosition(-1), keyEvent.hasShiftDown());
                else
                    this.moveCursor(-1, keyEvent.hasShiftDown());
                return true;
            }
            case 268 -> {
                this.moveCursorToStart(keyEvent.hasShiftDown());
                return true;
            }
            case 269 -> {
                this.moveCursorToEnd(keyEvent.hasShiftDown());
                return true;
            }
            default -> {
                if (keyEvent.isSelectAll()) {
                    this.moveCursorToEnd(false);
                    this.setHighlightPos(0);
                    return true;
                } else if (keyEvent.isCopy()) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    return true;
                } else if (keyEvent.isPaste()) {
                    this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
                    return true;
                } else if (keyEvent.isCut()) {
                    Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
                    this.insertText("");
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent characterEvent) {
        if (!this.isActive() || !this.isFocused() || !characterEvent.isAllowedChatCharacter())
            return false;

        this.insertText(characterEvent.codepointAsString());
        return true;
    }

    private int findClickedPositionInText(MouseButtonEvent mouseButtonEvent) {
        int i = Math.min(Mth.floor(mouseButtonEvent.x()) - this.textX, this.getInnerWidth());
        String string = this.value.substring(this.displayPos);
        return this.displayPos + this.font.plainSubstrByWidth(string, i).length();
    }

    private void selectWord(MouseButtonEvent mouseButtonEvent) {
        int i = this.findClickedPositionInText(mouseButtonEvent);
        int j = this.getWordPosition(-1, i);
        int k = this.getWordPosition(1, i);
        this.moveCursorTo(j, false);
        this.moveCursorTo(k, true);
    }

    private FormattedCharSequence applyFormat(String string, int i) {
        for (TextFormatter textFormatter : this.formatters) {
            FormattedCharSequence formattedCharSequence = textFormatter.format(string, i);
            if (formattedCharSequence != null)
                return formattedCharSequence;
        }

        return FormattedCharSequence.forward(string, Style.EMPTY);
    }

    private void updateTextPosition() {
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.textX = this.getX() + (this.isCentered() ? (this.getWidth() - this.font.width(string)) / 2 : 4);
        this.textY = this.getY() + (this.getHeight() - 8) / 2;
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int i) {
        this.maxLength = i;
        if (this.value.length() > i) {
            this.value = this.value.substring(0, i);
            this.onValueChange(this.value);
        }
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public void setCursorPosition(int i) {
        this.cursorPos = Mth.clamp(i, 0, this.value.length());
        this.scrollTo(this.cursorPos);
    }

    public void setTextColor(int i) {
        this.textColor = i;
    }

    public void setTextColorUneditable(int i) {
        this.textColorUneditable = i;
    }

    private boolean isCentered() {
        return this.centered;
    }

    public void setCentered(boolean bl) {
        this.centered = bl;
        this.updateTextPosition();
    }

    public int getInnerWidth() {
        return this.getWidth() - 8;
    }

    public void setHighlightPos(int i) {
        this.highlightPos = Mth.clamp(i, 0, this.value.length());
        this.scrollTo(this.highlightPos);
    }

    private void scrollTo(int i) {
        this.displayPos = Math.min(this.displayPos, this.value.length());
        int j = this.getInnerWidth();
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
        int k = string.length() + this.displayPos;
        if (i == this.displayPos) {
            this.displayPos = this.displayPos - this.font.plainSubstrByWidth(this.value, j, true).length();
        }

        if (i > k) {
            this.displayPos += i - k;
        } else if (i <= this.displayPos) {
            this.displayPos = this.displayPos - (this.displayPos - i);
        }

        this.displayPos = Mth.clamp(this.displayPos, 0, this.value.length());
    }

    public void setCanLoseFocus(boolean bl) {
        this.canLoseFocus = bl;
    }

    public int getScreenX(int i) {
        return i > this.value.length() ? this.getX() : this.getX() + this.font.width(this.value.substring(0, i));
    }

    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface TextFormatter {
        @Nullable
        FormattedCharSequence format(String string, int i);
    }
}
