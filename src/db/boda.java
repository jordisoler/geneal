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
public class boda extends conexio{
    
    private int idUnio;
    private int idLloc;
    private date data;
    
    public boda(){
        super();
        this.idUnio = -1;
        this.idLloc = -1;
        this.data = new date();
    }
    
    public boda(int unio){
        super();
        try {
            String str = "select * from boda where id_unio=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, unio);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                this.idUnio  = unio;
                this.idLloc = getInt(rs,"id_lloc");
                try {
                    this.data = new date(rs.getCharacterStream("data_boda"));
                } catch (dateException ex) {
                    this.data = new date();
                }
            }else{
                this.idUnio = -1;
                this.idLloc = -1;
                this.data = new date();
            }
        } catch (SQLException ex) {
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int getIdUnio(){
        return this.idUnio;
    }
    
    public unio getUnio() throws DBException{
        return new unio(this.idUnio);
    }
    
    public int getIdLloc(){
        return this.idLloc;
    }
    
    public lloc getLloc(){
        return new lloc(this.idLloc);
    }
    
    public date getData(){
        return this.data;
    }
    
//    public java.sql.Date getjData(){
//        return this.data.tojDate();
//    }
    
    public boolean isNull(){
        return this.idUnio == -1;
    }
    
    public void setUnio(int in){
        this.idUnio = in;
    }
    
    public void setUnio(unio in){
        this.idUnio = in.getId();
    }
    
    public void setLloc(int in){
        this.idLloc = in;
    }
    
    public void setLloc(lloc in){
        this.idLloc = in.getId();
    }
    
    public void setData(date in){
        this.data = in;
    }
    
    public void setData(java.sql.Date d){
        try {
            this.data = new date(d);
        } catch (dateException ex) {
            this.data = new date();
        }
    }
    
    public void addBoda(){
        if (boda.exist(this)){
            this.updateBoda();
        }else{
            this.addNewBoda();
        }
    }
    
    public static boolean exist(int in){
        try {
            String str = "select * from boda where id_unio=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, in);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean exist(boda in){
        return exist(in.getIdUnio());
    }
    
    public boolean equals(boda in){
        return this.idUnio == in.idUnio &&
                this.idLloc == in.idLloc &&
                this.data == in.data;
    }
    
    public String toString(){
        lloc ll = new lloc(idLloc);
        return "Boda: lloc:"+ll+", data:"+data+", unio: "+idUnio;
    }
    
    private void addNewBoda(){
        try {
            String str = "insert into boda (id_unio, id_lloc, data_boda)"
                    + " values (?,?,?)";
            PreparedStatement pst=con.prepareStatement(str);
            if (this.idUnio == -1){
                pst.setNull(1, Types.INTEGER);
            }else{
                pst.setInt(1, this.idUnio);
            }
            if (this.idLloc == -1){
                pst.setNull(2, Types.INTEGER);
            }else{
                pst.setInt(2, this.idLloc);
            }
            pst.setString(3, this.data.toQuery());
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateBoda(){
        try {
            String str = "update boda set id_lloc=?, data_boda=?"
                    + " where id_unio=?";
            PreparedStatement pst=con.prepareStatement(str);
            if (this.idLloc == -1){
                pst.setNull(1, Types.INTEGER);
            }else{
                pst.setInt(1, this.idLloc);
            }
            pst.setString(2, this.data.toQuery());
            pst.setInt(3, this.idUnio);
            System.out.println(pst);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
