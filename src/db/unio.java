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
import Exceptions.MUException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class unio extends conexio{
    
    private int idUnio;
    private int fitxa;
    private int idConjuge1;
    private int idConjuge2;
    private String comentaris;
    
    public unio(){
        super();
        this.idUnio  = -1;
        this.fitxa = -1;
        this.idConjuge1 = -1;
        this.idConjuge2 = -1;
        this.comentaris = null;
    }
    
    public unio(int id) throws DBException{
        super();
        try {
            String str = "select * from unio where id_unio="+id;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            if (rs.next()){
                this.idUnio = id;
                this.fitxa = rs.getInt("fitxa");
                if (rs.wasNull()){
                    this.fitxa = -1;
                }
                this.idConjuge1 = rs.getInt("id_conjuge1");
                if (rs.wasNull()){
                    this.idConjuge1 = -1;
                }
                this.idConjuge2 = rs.getInt("id_conjuge2");
                if (rs.wasNull()){
                    this.idConjuge2 = -1;
                }
                this.comentaris = rs.getString("unio_comentaris");
            }else{
                throw new DBException("S'ha intentat aconseguir una unió "
                        + "inexistent (id="+id+")");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getSQLState());
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static unio fromFitxa(int fitxa){
        unio u = new unio();
        try {
            String str = "select * from unio where fitxa="+fitxa;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            if (rs.next()){
                u.idUnio = rs.getInt("id_unio");
                if (rs.wasNull()){
                    u.fitxa = -1;
                }
                u.fitxa = rs.getInt("fitxa");
                if (rs.wasNull()){
                    u.fitxa = -1;
                }
                u.idConjuge1 = rs.getInt("id_conjuge1");
                if (rs.wasNull()){
                    u.idConjuge1 = -1;
                }
                u.idConjuge2 = rs.getInt("id_conjuge2");
                if (rs.wasNull()){
                    u.idConjuge2 = -1;
                }
                u.comentaris = rs.getString("unio_comentaris");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getSQLState());
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
    
    public static unio fromConjuge(int id) throws MUException, DBException{
        unio u = new unio();
        try {
            String str = "select * from unio where id_conjuge1="+id+" or "
                    +"id_conjuge2="+id;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            if (rs.next()){
                u.idUnio = rs.getInt("id_unio");
                if (rs.wasNull()){
                    u.fitxa = -1;
                }
                u.fitxa = rs.getInt("fitxa");
                if (rs.wasNull()){
                    u.fitxa = -1;
                }
                u.idConjuge1 = rs.getInt("id_conjuge1");
                if (rs.wasNull()){
                    u.idConjuge1 = -1;
                }
                u.idConjuge2 = rs.getInt("id_conjuge2");
                if (rs.wasNull()){
                    u.idConjuge2 = -1;
                }
                u.comentaris = rs.getString("unio_comentaris");
                
                rs.beforeFirst();  
                rs.last();
                if (rs.getRow()>1){
                    u = new unio();
                    throw new MUException(new persona(id));
                }
            }else{
                persona p = new persona(id);
                throw new DBException("S'ha intentat carregar una unió inexistent"
                        + " a partir del conjuge "+p);
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getSQLState());
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
    
    public boolean isNull(){
        return this.idUnio == -1;
    }
    
    public int getId(){
        return this.idUnio;
    }
    
    public int getIdConjuge1(){
        return this.idConjuge1;
    }
    
    public int getIdConjuge2(){
        return this.idConjuge2;
    }
    
    public String getComentaris(){
        return this.comentaris;
    }
    
    public persona getConjuge1(){        
        return new persona(this.idConjuge1);
    }
    
    public persona getConjuge2(){        
        return new persona(this.idConjuge2);
    }
    
    public lloc getLlocMatrimoni(){
        boda b = new boda(this.idUnio);
        return b.getLloc();
    }
    
    public date getDataMatrimoni(){
        boda b = new boda(this.idUnio);
        return b.getData();
    }
    
    public boolean equals(unio u){
        try{
            return (u.getComentaris().equals(this.comentaris)) &&
                    u.getId() == this.idUnio &&
                    u.getIdConjuge1() == this.idConjuge1 &&
                    u.getIdConjuge2() == this.idConjuge2 &&
                    u.getFitxa() == this.fitxa;
        }catch (NullPointerException e){
            return u.getComentaris()==null && this.comentaris == null &&
                    u.getId() == this.idUnio &&
                    u.getIdConjuge1() == this.idConjuge1 &&
                    u.getIdConjuge2() == this.idConjuge2 &&
                    u.getFitxa() == this.fitxa;
        }
    }
    
    public ArrayList<Integer> getIdFills(){
        try {
            familia f = new familia(this.idUnio);
            return f.getListFills();
        } catch (DBException ex) {
            Logger.getLogger(unio.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }
    
    public persona[] getFills(){
        ArrayList<Integer> idfills = this.getIdFills();
        ArrayList<persona> fills = new ArrayList<>();
        for (int id : idfills){
            fills.add(new persona(id));
        }
        persona[] fillsArr = new persona[fills.size()];
        return fills.toArray(fillsArr);
    }
    
    public int getFitxa(){
        return this.fitxa;
    }
    
    public boolean isMarriage(){
        return boda.exist(this.idUnio);
    }
    
    public void setId(int in){
        this.idUnio = in;
    }
    
    public void setConjuge1(int in){
        this.idConjuge1 = in;
    }
    
    public void setConjuge1(persona in){
        this.idConjuge1 = in.getId();
    }
    
    public void setConjuge2(int in){
        this.idConjuge2 = in;
    }
    
    public void setConjuge2(persona in){
        this.idConjuge2 = in.getId();
    }
    
    public void setFitxa(int in){
        this.fitxa = in;
    }
    
    public void setComentaris(String in){
        this.comentaris = in;
    }
    
    public void addUnio(){
        if (unio.exist(this)){
            this.updateUnio();
        }else{
            this.addNewUnio();
        }
    }
    
    
    public static boolean existFitxa(int fitxa){
        try {
            String str = "select * from unio where fitxa="+fitxa;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean exist(int id_unio){
        try {
            String str = "select * from unio where id_unio="+id_unio;
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean exist(unio in){
        return unio.exist(in.getId());
    }
    
    public static void deletePersona(int id){
        String[] conjuges = {"conjuge1", "conjuge2"};
        for (int i=0; i<2; i++){
            try {
                String str = "update unio set "+conjuges[i]+" = null where "+conjuges[i]
                        +" = "+id;
                Statement pst = con.createStatement();
                pst.executeQuery(str);
            } catch (SQLException ex) {}
            
        }
    }
    
    private void addNewUnio(){
        try {
            String str = "insert into unio (fitxa, id_conjuge1, id_conjuge2,"
                    + "unio_comentaris) values (?,?,?,?)";
            PreparedStatement pst=con.prepareStatement(str);
            if (this.fitxa == -1){
                pst.setNull(1, Types.INTEGER);
            }else{
                pst.setInt(1, this.fitxa);
            }
            if (this.idConjuge1 == -1){
                pst.setNull(2, Types.INTEGER);
            }else{
                pst.setInt(2, this.idConjuge1);

            }
            if (this.idConjuge2 == -1){
                pst.setNull(3, Types.INTEGER);
            }else{
                pst.setInt(3, this.idConjuge2);

            }
            pst.setString(4, this.comentaris);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateUnio(){
        try {
            String str = "update unio set fitxa=?, id_conjuge1=?, id_conjuge2=?,"
                    + "unio_comentaris=? where id_unio=?";
            PreparedStatement pst=con.prepareStatement(str);
            if (this.fitxa == -1){
                pst.setNull(1, Types.INTEGER);
            }else{
                pst.setInt(1, this.fitxa);

            }
            if (this.idConjuge1 == -1){
                pst.setNull(2, Types.INTEGER);
            }else{
                pst.setInt(2, this.idConjuge1);

            }
            if (this.idConjuge2 == -1){
                pst.setNull(3, Types.INTEGER);
            }else{
                pst.setInt(3, this.idConjuge2);

            }
            pst.setString(4, this.comentaris);
            pst.setInt(5, this.idUnio);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boda getBoda() {
        return new boda(this.idUnio);
    }
}
