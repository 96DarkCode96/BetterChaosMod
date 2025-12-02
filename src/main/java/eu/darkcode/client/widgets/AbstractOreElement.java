package eu.darkcode.client.widgets;

public abstract class AbstractOreElement implements OreElement {
    private int x;
    private int y;
    private int width;
    private int height;

    protected AbstractOreElement() {
    }

    protected AbstractOreElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int i) {
        this.x = i;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int i) {
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
