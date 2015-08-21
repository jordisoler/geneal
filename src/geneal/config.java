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

import Exceptions.DBException;
import Exceptions.GException;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public final class config {
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
    public static final Color greenColour = new Color(13172710);
    
    private static final String home = System.getProperty("user.home");
    private static final String configDir = home+File.separator+".geneal"+File.separator;
    private static final String propFile = configDir+"properties.txt";
    
    private static Integer rootFitxa = null;
    
    public static Font normalFont = defaultNormalFont;
    public static Font smallFont = defaultSmallFont;
    public static Font boldFont = defaultBoldFont;
    public static Font tinyFont = defaultTinyFont;
    public static Font bigFont = defaultBigFont;
    public static Font bigBoldFont = defaultBigBoldFont;
    
    public static void setup(){
        java.io.File f = new java.io.File(propFile);
        if(!f.exists() || f.isDirectory()) {
            Integer fitxa = null;
            while (fitxa==null){
                fitxa = newRoot();
            }
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(propFile)) {
                    Properties prop = new Properties();
                    prop.put("fitxa.root", String.valueOf(fitxa));
                    prop.store(fos, "Main fitxa");
                    fos.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //ClassLoader loader = Thread.currentThread().getContextClassLoader();
        //java.io.InputStream is = loader.getResourceAsStream(propFile);
        java.io.InputStream is = null;
        try {
            is = new java.io.FileInputStream(f);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(config.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        try {
            props.load(is);
            rootFitxa = Integer.parseInt(props.get("fitxa.root").toString());
            System.out.println("La fitxa ruta és: "+rootFitxa);
        } catch (IOException ex) {
            Logger.getLogger(config.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }
    
    private static Integer newRoot(){
        String sfitxa = JOptionPane.showInputDialog("No has configurat mai les propietats "
                + "d'aquest programa. \n Quina vols que sigui la fitxa a partir"
                + " de la qual es comptin les generacions?");
        Integer fitxa;
        if (!(sfitxa==null || sfitxa.isEmpty())){
            try{
                fitxa = Integer.parseInt(sfitxa);
                if(!db.unio.existFitxa(fitxa)){
                    throw new DBException();
                }else{
                    return fitxa;
                }
            }catch (NumberFormatException | DBException e){
                new GException("No has introduit una fitxa correcta, torna-ho a "
                        + "intentar.", "Fitxa incorrecte").show();
                return null;
            }
        }else{
            new GException("No has introduit una fitxa correcta, torna-ho a "
                        + "intentar.", "Fitxa incorrecte").show();
            return null;
        }
    }
}
