/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.util;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Collections;
import java.util.List;

/**
 * Render Markdown to HTML.
 */
public enum MarkdownParser {

    /**
     * Instance of MarkdownParser.
     */
    INSTANCE;

    /**
     * Markdown parser.
     */
    private final Parser mParser;

    /**
     * HTML renderer for the text parsed by Markdown parser.
     */
    private final HtmlRenderer mRenderer;

    /**
     * Initialize Markdown engine.
     */
    MarkdownParser() {
        List<Extension> extensions = Collections.singletonList(TablesExtension.create());
        mParser = Parser.builder()
                .extensions(extensions)
                .build();

        mRenderer = HtmlRenderer.builder()
                .extensions(extensions)
                .escapeHtml(true)
                .build();
    }

    /**
     * Converts Mardown to HTML.
     *
     * @param text text for rendering
     * @return html
     */
    public String toHtml(final String text) {
        return mRenderer.render(mParser.parse(text));
    }
}
