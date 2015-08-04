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

import Exceptions.DBException;
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
public class familia extends unio{
    
    private ArrayList<Integer> fills;
    
    
    public familia(){
        super();
        this.fills = new ArrayList<>();
    }
    
    public familia(int id) throws DBException{
        super(id);
        this.fills = new ArrayList<>();
        try {
            String str = "select id_fill from naixement where id_unio=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                int fill = rs.getInt("id_fill");
                this.fills.add(fill);
            }
        } catch (SQLException ex) {
            Logger.getLogger(familia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<Integer> getListFills(){
        return this.fills;
    }
    
    public void addFill(int fill, date d, int lloc){
        this.fills.add(fill);
        try {
            String str = "insert into naixement (data_naixement, id_fill, id_unio,"
                    + "id_lloc) values (?,?,?,?)";
            PreparedStatement pst=con.prepareStatement(str);
            pst.setString(1, d.toQuery());
            pst.setInt(2, fill);
            pst.setInt(3, this.getId());
            pst.setInt(4, lloc);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
