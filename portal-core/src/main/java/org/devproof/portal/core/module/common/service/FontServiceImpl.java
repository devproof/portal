/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("fontService")
public class FontServiceImpl implements FontService {

    @Override
    public List<Font> findAllSystemFonts() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = env.getAvailableFontFamilyNames();
        List<Font> fonts = new ArrayList<Font>(fontNames.length);
        for (String fontName : fontNames) {
            fonts.add(new Font(fontName, Font.PLAIN, 12));
        }
        return fonts;
    }

    @Override
    public List<String> findSyntaxHighlighterThemes() {
        return Arrays.asList("Default", "Django", "Eclipse", "Emacs", "FadeToGrey", "MDUltra", "Midnight", "RDark");
    }
}
