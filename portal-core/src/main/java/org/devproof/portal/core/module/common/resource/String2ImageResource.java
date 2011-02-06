/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.resource;

import org.apache.wicket.markup.html.image.resource.RenderedDynamicImageResource;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Converts a String into an image
 *
 * @author Carsten Hufe
 */
public class String2ImageResource extends RenderedDynamicImageResource {

    private static final long serialVersionUID = 1L;
    private List<String> lines;
    private Font font;
    private int height;
    private int width;
    private int size;

    public String2ImageResource(List<String> lines, Font font) {
        super(100, 100);
        this.lines = lines;
        this.font = font;
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        width = 0;
        size = font.getSize();
        Graphics2D g = (Graphics2D) image.getGraphics();
        for (String str : lines) {
            Rectangle2D l = font.getStringBounds(str, g.getFontRenderContext());
            int tmpWidth = (int) l.getWidth();
            if (tmpWidth > width) {
                width = tmpWidth;
            }
            height += size + (size / 2);

        }
        setHeight(height);
        setWidth(width);
    }

    @Override
    protected boolean render(Graphics2D g) {
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        int actHeight = -5;
        for (String str : lines) {
            actHeight += ((size / 2) + size);
            g.drawString(str, 0, actHeight);
        }
        return true;
    }

}
