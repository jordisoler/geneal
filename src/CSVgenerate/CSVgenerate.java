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
package CSVgenerate;

import Exceptions.GException;
import static db.CSVdb.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class CSVgenerate {
    private static final String defaultPath = System.getProperty("user.home")+"/Escriptori/AD/";
    private static final String extension = ".csv";
    private static String path;
    
    public static final int matrimonis = 1;
    public static final int naixements = 2;
    public static final int defuncions = 3;
    
    public CSVgenerate(){
        path = defaultPath;
    }
    
    public CSVgenerate(String path_){
        path = path_;
    }
    
    //public
    
    public void write(int type, String municipi){
        FileWriter fout = null;
        String[][] dades;
        String stype;
        String header;
        
        try {
            switch (type){
                case matrimonis:
                    dades = generateCSVmatrimonis(municipi);
                    stype = "matrimonis";
                    header = "Fitxa;Nom home;Llinatge home;Nom dona;Llinatge dona;Data aproximada;Calculat amb";
                    break;
                case naixements:
                    dades = generateCSVnaixements(municipi);
                    stype = "naixements";
                    header = "Fitxa;Nom;Llinatge 1;Llinatge 2;Nom del pare;Nom de la mare;Data aproximada;Calculat amb";
                    break;
                case defuncions:
                    dades = generateCSVdefuncions(municipi);
                    stype = "defuncions";
                    header = "Fitxa;Nom;Llinatge 1;Llinatge 2;Nom de la parella;Llinatge de la parella;Data aproximada;Calculat amb";
                    break;
                default:
                    throw new GException("S'ha triat una opció incorrete a la funció write",
                            "He fet un error de programació :(");
            }
            
            try {
                fout = new FileWriter(path+stype+"_"+fillGaps(municipi)+extension);
            } catch (IOException ex) {
                final File parent = new File(path);
                if (!parent.mkdirs()){
                   new GException("No s'han pogut crear directoris:\n\t"+path,
                           "Error de  creacio de directoris.").show();
                }
                final File generat = new File(parent, stype+"_"+fillGaps(municipi)+extension);
                generat.createNewFile();
                fout = new FileWriter(generat);
            }
            try (PrintWriter pr = new PrintWriter(fout)) {
                pr.println(header);
                for (String[] row : dades){
                    pr.println(formatRow(row));
                }
            }
            fout.close();
        } catch (IOException | GException ex) {
            final File parent = new File(path);
        } 
    }
    
    private String formatRow(String[] in){
        int len = in.length;
        String out = in[0];
        for (int i=1; i <len; i++){
            out = out+";"+in[i];
        }
        return out;
    }
    
    private String fillGaps(String in){
        return in.replace(' ', '_');
    }
}
