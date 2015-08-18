/*
 * Copyright (C) 2015 jordi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package geneal;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author jordi
 */
public class config {
    // Fonts
    private static final Font defaultNormalFont = new Font("Ubuntu", Font.PLAIN, 14);
    private static final Font defaultBoldFont = new Font("Ubuntu", Font.BOLD, 14);
    private static final Font defaultSmallFont = new Font("Ubuntu", Font.PLAIN, 12);
    private static final Font defaultTinyFont = new Font("Ubuntu", Font.PLAIN, 10);
    private static final Font defaultBigFont = new Font("Ubuntu", Font.PLAIN, 16);
    private static final Font defaultBigBoldFont = new Font("Ubuntu", Font.BOLD, 16);
    
    // Colours
    public static final Color maleColour = new java.awt.Color(11190015); // Blue
    public static final Color femaleColour = new Color(16757960); // Red
    public static final Color defaultColour = new Color(15790320); // Grey
    public static final Color darkGray = new Color(9868950); // Grey
    
    public static Font normalFont = defaultNormalFont;
    public static Font smallFont = defaultSmallFont;
    public static Font boldFont = defaultBoldFont;
    public static Font tinyFont = defaultTinyFont;
    public static Font bigFont = defaultBigFont;
    public static Font bigBoldFont = defaultBigBoldFont;
}
