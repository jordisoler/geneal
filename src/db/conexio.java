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
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author jordi
 */
public class conexio {
    
    public static final String db = "arbre_nou";
    public static final String usuari = "root";
    public static final String contrasenya = "jordi";
    public static final int idroot1 = 2361;
    public static final int idroot2 = 3212;

    /**
     * Connection to the genealogic database.
     */
    public static Connection con = null;
    
    //Mètode per a crear la conexió.
    public conexio() {
        if (con==null){
            try {
                Class.forName("org.gjt.mm.mysql.Driver");
                con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                        +db+"?zeroDateTimeBehavior=convertToNull", usuari, contrasenya);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(conexio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    public static void connect(){
        if (con==null){
            try {
                Class.forName("org.gjt.mm.mysql.Driver");
                con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                        +db, usuari, contrasenya);
            } catch (SQLException ex) {
                Logger.getLogger(conexio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(conexio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static int getInt(ResultSet rs, String field) throws SQLException{
        int out = rs.getInt(field);
        if(rs.wasNull()){
            out = -1;
        }
        return out;
    }
    
    public static int getInt(ResultSet rs, int field) throws SQLException{
        int out = rs.getInt(field);
        if(rs.wasNull()){
            out = -1;
        }
        return out;
    }
    
    public static void setString(PreparedStatement stm, int position, String in) throws SQLException{
        if (in == null || in.equals(geneal.formutils.unknown) || in.isEmpty()){
            stm.setNull(position, Types.VARCHAR);
        }else{
            stm.setString(position, in);
        }
    }
}
