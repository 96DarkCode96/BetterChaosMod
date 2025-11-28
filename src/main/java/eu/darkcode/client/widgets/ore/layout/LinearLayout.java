package eu.darkcode.client.widgets.ore.layout;

import eu.darkcode.client.widgets.ore.OreContainer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class LinearLayout implements Layout {

    private @NotNull Orientation orientation;
    private int gap;

    public LinearLayout() {
        this(Orientation.VERTICAL);
    }

    public LinearLayout(@NotNull Orientation orientation) {
        this(orientation, 0);
    }

    public LinearLayout(int gap) {
        this(Orientation.VERTICAL, gap);
    }

    public LinearLayout(@NotNull Orientation orientation, int gap) {
        this.orientation = orientation;
        this.gap = gap;
    }

    public int gap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public @NotNull Orientation orientation() {
        return orientation;
    }

    public void setOrientation(@NotNull Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public void arrangeElements(@NotNull OreContainer container) {
        orientation.arrangeElements(container, gap);
    }

    public enum Orientation {
        HORIZONTAL {
            @Override
            protected void arrangeElements(@NotNull OreContainer container, int gap) {
                VERTICAL.arrangeElements(container, gap);
            }
        },
        VERTICAL {
            @Override
            protected void arrangeElements(@NotNull OreContainer con, int gap) {
                if (con.isEmpty()) {
                    con.setHeight(con.paddingVertical());
                    return;
                }
                AtomicInteger yPos = new AtomicInteger(con.paddingTop());
                con.elements(el -> {
                    el.setY(con.getY() + yPos.get() + el.marginTop());
                    el.setX(con.getX() + con.paddingLeft() + el.marginLeft());
                    el.setWidth(con.getWidth() - con.paddingHorizontal() - el.marginHorizontal());

                    if (el instanceof OreContainer oc)
                        oc.arrangeElements();

                    yPos.addAndGet(el.getHeight() + el.marginVertical() + gap);
                });
                con.setHeight(yPos.get() + con.paddingBottom() - gap);
            }
        };

        protected abstract void arrangeElements(@NotNull OreContainer container, int gap);
    }

}