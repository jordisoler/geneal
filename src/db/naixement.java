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
import Exceptions.dateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class naixement extends conexio{
    private date dataNaixement;
    private int idFill;
    private int idUnio;
    private int idLloc;
    
    public naixement(){
        super();
        this.dataNaixement = new date();
        this.idFill = -1;
        this.idLloc = -1;
        this.idUnio = -1;
    }
    
    public naixement(int fill) throws DBException{
        super();
        try {
            String str = "select * from naixement where id_fill=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, fill);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                this.idFill = fill;
                this.idLloc = nullify(rs.getInt("id_lloc"));
                this.idUnio = nullify(rs.getInt("id_unio"));
                try {
                    this.dataNaixement = new date(rs.getString("data_naixement"));
                } catch (dateException ex) {
                    this.dataNaixement = new date();
                }
            }else{
                throw new DBException("S'ha intentat aconseguir un naixement "
                    + "inexistent (id de la persona: "+fill+")");
            }
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getIdFill(){
        return this.idFill;
    }
    
    public int getIdLloc(){
        return this.idLloc;
    }
    
    public int getIdUnio(){
        return this.idUnio;
    }
    
    public persona getFill(){
        return new persona(this.idFill);
    }
    
    public lloc getLloc(){
        return new lloc(this.idLloc);
    }
    
    public unio getUnio() throws DBException{
        return new unio(this.idUnio);
    }
    
    public date getDate(){
        return this.dataNaixement;
    }
    
    public void setFill(int in){
        this.idFill = in;
    }
    
    public void setFill(persona in){
        this.idFill = in.getId();
    }
    
    public void setLloc(int in){
        this.idLloc = in;
    }
    
    public void setLloc(lloc in){
        this.idLloc = in.getId();
    }
    
    public void setUnio(int in){
        this.idUnio = in;
    }
    
    public void setUnio(unio in){
        this.idUnio = in.getId();
    }
    
    public void setDate(date in){
        this.dataNaixement = in;
    }
    
    public void addNaixement(){
        try {
            naixement n = new naixement(this.idFill);
            if (!this.equals(n)){
                this.updateNaixement();
            }
        } catch (DBException ex) {
            this.addNewNaixement();
        }
    }
    
    public boolean delete(){
        if(naixement.exist(this)){
            try {
                String str = "delete from  naixement where id_fill = ?";
                PreparedStatement pst = con.prepareStatement(str);
                pst.setInt(1, this.idFill);
                pst.execute();
            } catch (SQLException ex) {
                Logger.getLogger(naixement.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }else{
            return false;
        }
    }
    
    public boolean equals(naixement in){
        return this.idFill == in.getIdFill() && this.idLloc == in.getIdLloc() &&
                this.idUnio == in.getIdUnio() && this.dataNaixement == in.getDate();
    }
    
    public static boolean exist(int in){
        try {
            String str = "select * from naixement where id_fill=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, in);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    public static boolean exist(naixement in){
        if (in.getIdFill()==-1){
            return false;
        }
        return exist(in.getIdFill());
    }
    
    //----------PRIVATE methods----------
    private void addNewNaixement(){
        try {
            String str = "insert into naixement (data_naixement, id_fill, id_unio,"
                    + "id_lloc) values (?,?,?,?)";
            PreparedStatement pst=con.prepareStatement(str);
            pst.setString(1, this.dataNaixement.toQuery());
            if (this.idFill == -1){
                pst.setNull(2, Types.INTEGER);
            }else{
                pst.setInt(2, this.idFill);

            }
            if (this.idUnio == -1){
                pst.setNull(3, Types.INTEGER);
            }else{
                pst.setInt(3, this.idUnio);

            }
            if (this.idLloc == -1){
                pst.setNull(4, Types.INTEGER);
            }else{
                pst.setInt(4, this.idLloc);

            }
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateNaixement(){
        try {
            String str = "update naixement set data_naixement=?, id_unio=?,"
                    + "id_lloc=? where id_fill = ?";
            PreparedStatement pst=con.prepareStatement(str);
            pst.setString(1, this.dataNaixement.toQuery());
            pst.setInt(4, this.idFill);
            if (this.idUnio == -1){
                pst.setNull(2, Types.INTEGER);
            }else{
                pst.setInt(2, this.idUnio);

            }
            if (this.idLloc == -1){
                pst.setNull(3, Types.INTEGER);
            }else{
                pst.setInt(3, this.idLloc);

            }
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int nullify(int in){
        if (in == 0){
            return -1;
        }else{
            return in;
        }
    }
    
}
