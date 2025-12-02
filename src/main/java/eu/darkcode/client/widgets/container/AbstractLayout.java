package eu.darkcode.client.widgets.container;

import eu.darkcode.client.widgets.OreLayout;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class AbstractLayout<T> implements OreLayout {
    protected final List<T> children = new ArrayList<>();
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected AbstractLayout() {
    }

    protected AbstractLayout(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @NotNull
    public <A extends T> A addChild(@NotNull A element) {
        this.children.add(element);
        return element;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int i) {
        this.visitChildren(layoutElement -> {
            int j = layoutElement.getX() + (i - this.getX());
            layoutElement.setX(j);
        });
        this.x = i;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int i) {
        this.visitChildren(layoutElement -> {
            int j = layoutElement.getY() + (i - this.getY());
            layoutElement.setY(j);
        });
        this.y = i;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }
}
