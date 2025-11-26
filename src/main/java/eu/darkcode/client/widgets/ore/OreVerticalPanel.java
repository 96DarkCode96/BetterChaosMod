package eu.darkcode.client.widgets.ore;

import net.minecraft.client.gui.layouts.Layout;

public class OreVerticalPanel extends OrePanel {
    @Override
    public void arrangeElements() {
        int yPos = paddingTop();
        for (OreElement element : children) {
            if (element instanceof Layout layout)
                layout.arrangeElements();

            element.setX(getX() + paddingLeft() + element.marginLeft());
            element.setY(getY() + yPos + element.marginTop());
            element.setWidth(getWidth() - paddingHorizontal() - element.marginHorizontal());

            yPos += element.getHeight() + element.marginVertical();
        }
        setHeight(yPos + paddingBottom());
    }
}
