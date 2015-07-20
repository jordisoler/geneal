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
package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class CSVdb extends conexio{
    
    private static final int edatBodaMinima = 18;
    private static final int edatPrimerFillMinima = 20;
    private static final int edatDefuncioMinima = 60;
    
    private static final int edatBodaMaxima = 50;
    private static final int edatDefuncioMaxima = 95;
    private static final int anysDefuncioDespresDarrerFillMaxim = 70;
    
    private static final int anysEntreBodesGeneracionsConsecutives = 20;
    
    public static String[][] generateCSVmatrimonis(String municipi){
        String query = "select u.fitxa as fitxa, c1.nom as nomc1,  c1.llinatge1 as llinatgec1, "
                + "c2.nom as nomc2, c2.llinatge1 as llinatgec2, " +
                "if(min(nfill.data_naixement) is not null, year(min(nfill.data_naixement)), " +
                "if (n1.data_naixement is not null, year(n1.data_naixement)+?, " +
                "if (n2.data_naixement is not null,year(min(n2.data_naixement))+?, " +
                "year(b_fill.data_boda)-?))) as data_approx, " +
                "if(min(nfill.data_naixement) is not null, 'Naixement fill', " +
                "if (n1.data_naixement is not null, 'Naixement home', " +
                "if (n2.data_naixement is not null,'Naixement dona', " +
                "if (b_fill.data_boda is not null, 'Matrimoni fill',null)))) as calculat_amb " +
                "from unio as u " +
                "left join persona as c1 on " +
                "u.id_conjuge1 = c1.id_persona " +
                "left join persona as c2 on " +
                "u.id_conjuge2 = c2.id_persona " +
                "left join naixement as n1 on " +
                "u.id_conjuge1 = n1.id_fill " +
                "left join naixement as n2 on " +
                "u.id_conjuge2 = n2.id_fill " +
                "left join boda as b on " +
                "u.id_unio = b.id_unio " +
                "left join lloc as ll on " +
                "b.id_lloc = ll.id_lloc " +
                "left join naixement as nfill on " +
                "u.id_unio = nfill.id_unio " +
                "left join persona as fill on " +
                "nfill.id_fill = fill.id_persona " +
                "left join unio as u_fill on " +
                "fill.id_persona = if(fill.sexe='m',u_fill.id_conjuge1,u_fill.id_conjuge2) " +
                "left join boda as b_fill on " +
                "u_fill.id_unio = b_fill.id_unio " +
                "where ll.id_municipi = ? and b.data_boda is null " +
                "group by u.id_unio " +
                "having data_approx is not null;";
        ArrayList<String[]> result = new ArrayList<>();
        int elements=7;
        try {
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, edatBodaMinima);
            pst.setInt(2, edatBodaMinima);
            pst.setInt(3, anysEntreBodesGeneracionsConsecutives);
            pst.setString(4, municipi);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()){
                String[] row = new String[elements];
                row[0] = String.valueOf(rs.getInt("fitxa"));
                row[1] = rs.getString("nomc1");
                row[2] = rs.getString("llinatgec1");
                row[3] = rs.getString("nomc2");
                row[4] = rs.getString("llinatgec2");
                row[5] = rs.getString("data_approx");
                row[6] = rs.getString("calculat_amb");
                result.add(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CSVdb.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result.toArray(new String[elements][]);
    }
    
    public static String[][] generateCSVnaixements(String municipi){
        String query = "select u_propia.fitxa as fitxa, p.nom as nom, p.llinatge1 as llin1, "
                + "p.llinatge2 as llin2, pare.nom as npare, mare.nom as nmare, " +
                    "if (b.data_boda is not null, year(b.data_boda)-?,  " +
                    "if(nfill.data_naixement is not null,min(year(nfill.data_naixement)-?), " +
                    "year(p.data_defuncio)-?)) as data_approx, " +
                    "if (b.data_boda is not null, 'Matrimoni',  " +
                    "if(nfill.data_naixement is not null,'Naixement fill','Defunció')) as calculat_amb, " +
                    "lloc.id_municipi, naixement.data_naixement from persona as p  " +
                    "inner join naixement on " +
                    "naixement.id_fill = p.id_persona " +
                    "inner join lloc on " +
                    "lloc.id_lloc =  naixement.id_lloc " +
                    "left join unio as u on " +
                    "naixement.id_unio = u.id_unio  " +
                    "left join persona as pare on " +
                    "u.id_conjuge1 = pare.id_persona  " +
                    "left join persona as mare on " +
                    "u.id_conjuge2 = mare.id_persona " +
                    "left join unio as u_propia on " +
                    "p.id_persona = if(p.sexe='m',u_propia.id_conjuge1,u_propia.id_conjuge2) " +
                    "left join boda as b on " +
                    "u_propia.id_unio = b.id_unio " +
                    "left join naixement as nfill on " +
                    "u_propia.id_unio = nfill.id_unio " +
                    "left join persona as fill on " +
                    "nfill.id_fill = fill.id_persona " +
                    "group by p.id_persona " +
                    "having lloc.id_municipi = ? and naixement.data_naixement is null " +
                    "and u_propia.fitxa is not null and data_approx is not null;";
        ArrayList<String[]> result = new ArrayList<>();
        int elements=8;
        try {
            String[] row = new String[elements];
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, edatBodaMinima);
            pst.setInt(2, edatPrimerFillMinima);
            pst.setInt(3, edatDefuncioMinima);
            pst.setString(4, municipi);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                row[0] = String.valueOf(rs.getInt("fitxa"));
                row[1] = rs.getString("nom");
                row[2] = rs.getString("llin1");
                row[3] = rs.getString("llin2");
                row[4] = rs.getString("npare");
                row[5] = rs.getString("nmare");
                row[6] = rs.getString("data_approx");
                row[7] = rs.getString("calculat_amb");
                result.add(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CSVdb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toArray(new String[elements][]);
    }
    
    public static String[][] generateCSVdefuncions(String municipi){
        String query = "select u_propia.fitxa as  fitxa, p.nom as nom, p.llinatge1 as llin1, "
                + "p.llinatge2 as llin2, conjuge.nom as parella, conjuge.llinatge1 as parellallin, " +
                "if(naixement.data_naixement is not null,min(year(naixement.data_naixement)+ ? ), " +
                "if (b.data_boda is not null, year(b.data_boda)+ ?  ,  " +
                "year(max(nfill.data_naixement))+ ? )) as data_approx, " +
                "if(naixement.data_naixement is not null,'Naixement', " +
                "if (b.data_boda is not null, 'Matrimoni', 'Naixement fill')) as calculat_amb, " +
                "lloc.id_municipi, naixement.data_naixement from persona as p  " +
                "inner join naixement on " +
                "naixement.id_fill = p.id_persona " +
                "inner join lloc on " +
                "lloc.id_lloc =  naixement.id_lloc " +
                "left join unio as u on " +
                "naixement.id_unio = u.id_unio  " +
                "left join unio as u_propia on " +
                "p.id_persona = if(p.sexe='m',u_propia.id_conjuge1,u_propia.id_conjuge2) " +
                "left join persona as conjuge on " +
                "if(p.sexe='m',u_propia.id_conjuge2,u_propia.id_conjuge1) = conjuge.id_persona " +
                "left join boda as b on " +
                "u_propia.id_unio = b.id_unio " +
                "left join naixement as nfill on " +
                "u_propia.id_unio = nfill.id_unio " +
                "group by p.id_persona " +
                "having lloc.id_municipi = ? and naixement.data_naixement is null " +
                "and u_propia.fitxa is not null and data_approx is not null;";
        ArrayList<String[]> result = new ArrayList<>();
        int elements=8;
        try {
            String[] row = new String[elements];
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, edatDefuncioMaxima);
            pst.setInt(2, edatBodaMaxima);
            pst.setInt(3, anysDefuncioDespresDarrerFillMaxim);
            pst.setString(4, municipi);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                row[0] = String.valueOf(rs.getInt("fitxa"));
                row[1] = rs.getString("nom");
                row[2] = rs.getString("llin1");
                row[3] = rs.getString("llin2");
                row[4] = rs.getString("parella");
                row[5] = rs.getString("parellallin");
                row[6] = rs.getString("data_approx");
                row[7] = rs.getString("calculat_amb");
                result.add(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CSVdb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.toArray(new String[elements][]);
    }
    
    
}
