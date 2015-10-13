package org.springframework.data.gremlin.schema.property.accessor;

/**
 * Created by gman on 12/10/15.
 */
public final class Rectangle {
    final private int w, h;

    public Rectangle(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public int getW() { return w; }

    public int getH() { return h; }

    public int getSize() { return w * h; }
}
