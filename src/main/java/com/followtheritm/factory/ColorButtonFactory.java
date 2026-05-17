package com.followtheritm.factory;

import com.followtheritm.model.SimonColor;
import com.followtheritm.view.ColorButton;

/** Encapsulates creation so SimonPanel never handles per-colour wiring. */
public final class ColorButtonFactory {

    private ColorButtonFactory() {}

    public static ColorButton create(SimonColor color) {
        return new ColorButton(color);
    }
}
