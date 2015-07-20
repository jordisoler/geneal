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

import static db.conexio.con;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jordi
 */
public class municipi extends conexio{
    
    private final String nomMunicipi;
    
    public municipi() {
        this.nomMunicipi = null;
    }

    public municipi(String nomMunicipi) {
        this.nomMunicipi = nomMunicipi;
    }
    
    public void add(){
        if (!municipi.exist(this.nomMunicipi)){
            try {
                String str = "insert into municipi (nom_municipi) values (?)";
                PreparedStatement stm = con.prepareStatement(str);
                stm.setString(1, this.nomMunicipi);
            } catch (SQLException ex) {
                Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public String toString(){
        return this.nomMunicipi;
    }
    
    public static boolean exist(String m){
        municipi.connect();
        try{
            String str = "select * from municipi where nom_municipi=?";
            PreparedStatement stm = con.prepareStatement(str);
            stm.setString(1, m);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }catch (SQLException ex) {
            System.out.println(ex);
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static String[] getAll(){
        municipi.connect();
        ArrayList<String> municipis = new ArrayList<>();
        try{
            String str = "select * from municipi";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            while (rs.next()){
                municipis.add(rs.getString("nom_municipi"));
            }
        }catch (SQLException ex) {
            System.out.println(ex);
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] municipisArr = new String[municipis.size()];
        return municipis.toArray(municipisArr);
    }
}
