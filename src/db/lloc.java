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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import Exceptions.*;
import static geneal.sourceforms.formutils.unknown;

/**
 *
 * @author jordi
 */
public class lloc extends conexio{
    
    private int idLloc;
    private String nomMunicipi;
    private String llogaret;
    private String parroquia;
    
    private final static String funknown = unknown;
    
    public lloc() {
        super();
        this.idLloc = -1;
    }

    public lloc(int id){
        super();
        if (id == -1){
            this.idLloc = -1;
        }else{
            loadLloc(id);
        }
    }
    
    public lloc(String nomMunicipi) {
        super();
        this.nomMunicipi = nomMunicipi;
        this.llogaret = null;
        this.parroquia = null;
    }
    
    public lloc(String nomMunicipi, String llogaret, String parroquia){
        super();
        if (!nomMunicipi.equals("")){
            this.nomMunicipi = nomMunicipi;
        } else{
            this.nomMunicipi = null;
        }
        if (!llogaret.equals("")){
            this.llogaret = llogaret;
        } else{
            this.llogaret = null;
        }
        if (!parroquia.equals("")){
            this.parroquia = parroquia;
        } else{
            this.parroquia = null;
        }
    }
    
    public static lloc loadLloc(String nomMunicipi, String parroquia, String llogaret) throws SQLException, LEException{
        lloc l = new lloc();
        boolean b_ll = false, b_p = false;
        if (nomMunicipi == null || "".equals(nomMunicipi)){
            throw new SQLException("S'ha intentat introduir un nom de municipi nul.");
        }   
        String query = "select * from lloc where id_municipi = ? ";
        
        if (llogaret != null && !llogaret.equals("") && !llogaret.equals(funknown)){
            query = query+" and llogaret=? ";
            b_ll = true;
        }
        if (parroquia != null && !parroquia.equals("") && !parroquia.equals(funknown)){
            query = query+" and parroquia=? ";
            b_p = true;
        }
        
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, nomMunicipi);
        if (b_ll){
            if(b_p){
                pst.setString(2, llogaret);
                pst.setString(3, parroquia);
            }else{
                pst.setString(2, llogaret);
            }
        }else{
            if (b_p){
                pst.setString(2, parroquia);
            }
        }
        
        ResultSet rs = pst.executeQuery();
        if (rs.next()){
            l.idLloc = getInt(rs,"id_lloc");
            l.nomMunicipi = rs.getString("id_municipi");
            l.llogaret = rs.getString("llogaret");
            l.parroquia = rs.getString("parroquia");
        }else{
            throw new LEException("El lloc introduit no existeix.");
        }
        
        return l;
    }
    
    public void setId(int in){
        this.idLloc = in;
    }
    
    public void setMunicipi(String in){
        this.nomMunicipi = in;
    }
    
    public void setLlogaret(String in){
        this.llogaret = in;
    }
    
    public void setParroquia(String in){
        this.parroquia = in;
    }
    
    public int getId(){
        return this.idLloc;
    }
    
    public String getMunicipi(){
        return this.nomMunicipi;
    }
    
    public String getLlogaret(){
        return this.llogaret;
    }    
    
    public String getParroquia(){
        return this.parroquia;
    }
    
    public void updateLlogaret(String in){
        try {
            String str = "update lloc llogaret=? where id_lloc=?";
            PreparedStatement pst=con.prepareStatement(str);
            pst.setString(1, in);
            pst.setInt(2, this.idLloc);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateParroquia(String in){
        try {
            String str = "update lloc parroquia=? where id_lloc=?";
            PreparedStatement pst=con.prepareStatement(str);
            pst.setString(1, in);
            pst.setInt(2, this.idLloc);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static lloc fromLlogaret(String in){
        lloc out = new lloc();
        try {
            String str = "select id_lloc from lloc where llogaret=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                out = new lloc(getInt(rs,"id_lloc"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    public static lloc fromParroquia(String in){
        lloc out = new lloc();
        try {
            String str = "select id_lloc from lloc where parroquia=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                out = new lloc(getInt(rs,"id_lloc"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }
    
    public static Boolean isAlreadyLlogaret(String in){
        try {
            String str = "select id_lloc from lloc where llogaret=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    public static Boolean isAlreadyLlogaret(String ll, String m){
        try {
            String str = "select id_lloc from lloc where llogaret=? and "
                    + "id_municipi = ?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, ll);
            pst.setString(2, m);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static Boolean isAlreadyParroquia(String in){
        try {
            String str = "select id_lloc from lloc where parroquia=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    public static Boolean isAlreadyParroquia(String p,  String m){
        try {
            String str = "select id_lloc from lloc where parroquia=? and "
                    + "id_municipi = ?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, p);
            pst.setString(2, m);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static boolean isAlready(int in){
        try {
            String str = "select id_lloc from lloc where id_lloc=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, in);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean isNull(){
        return this.idLloc == -1;
    }
    
    public boolean equals(lloc in){
        return this.idLloc == in.getId();
    }
    
    public boolean fromLlogaret2(String in){
        boolean hihes = false;
        try {
            String str = "select id_lloc from lloc where llogaret=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                hihes = true;
                int id = getInt(rs,"id_lloc");
                loadLloc(id);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hihes;
    }
    
    public boolean fromParroquia2(String in){
        boolean hihes = false;
        try {
            String str = "select id_lloc from lloc where parroquia=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setString(1, in);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                hihes = true;
                int id = getInt(rs,"id_lloc");
                loadLloc(id);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hihes;
    }
    
    public int addLloc(){
        try {
            String str = "insert into lloc (id_municipi, llogaret, parroquia)"
                    + " values (?,?,?)";
            PreparedStatement pst=con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            if (this.nomMunicipi == null){
                throw new SQLException("S'ha intentat introduir un lloc sense municipi.");
            }
            pst.setString(1, this.nomMunicipi);
            pst.setString(2, this.llogaret);
            pst.setString(3, this.parroquia);
            pst.execute();
            ResultSet rs = pst.getGeneratedKeys();
            int keys = -1;
            if (rs.next()){
                keys = rs.getInt(1);
            }
            this.idLloc =  keys;
            return keys;
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public static lloc[] getAll(){
        lloc.connect();
        ArrayList<lloc> llocs = new ArrayList<>();
        try{
            String str = "select id_lloc from lloc";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            while (rs.next()){
                lloc l = new lloc(getInt(rs,"id_lloc"));
                llocs.add(l);
            }
        }catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
        lloc[] llocsArr = new lloc[llocs.size()];
        return llocs.toArray(llocsArr);
    }
    
    public static String[] getParroquies(String municipi){
        lloc.connect();
        ArrayList<String> parroquies = new ArrayList<>();
        try{
            String condition;
            if (municipi==null || "".equals(municipi)){
                condition = "";
            }else{
                condition = " and id_municipi = ?";
            }
            String str = "select parroquia from lloc where parroquia is not null "
                    + condition+" group by parroquia";
            PreparedStatement pst = con.prepareStatement(str);
            if (!condition.isEmpty()){
                pst.setString(1, municipi);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                parroquies.add(rs.getString("parroquia"));
            }
        }catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] parroquiesArr = new String[parroquies.size()];
        return parroquies.toArray(parroquiesArr);
    }
    public static String[] getParroquies(){
        return getParroquies(null);
    }
    
    public static String[] getLlogarets(String municipi){
        lloc.connect();
        ArrayList<String> llogarets = new ArrayList<>();
        try{
            String condition;
            if (municipi==null || "".equals(municipi)){
                condition = "";
            }else{
                condition = " and id_municipi = ?";
            }
            String str = "select llogaret from lloc where llogaret is not null "
                    + condition+" group by llogaret";
            PreparedStatement pst = con.prepareStatement(str);
            if (!condition.isEmpty()){
                pst.setString(1, municipi);
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                llogarets.add(rs.getString("llogaret"));
            }
        }catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] llogaretsArr = new String[llogarets.size()];
        return llogarets.toArray(llogaretsArr);
    }
    public static String[] getLlogarets(){
        return getLlogarets(null);
    }
    
    @Override
    public String toString(){
        String llogaret_ = "";
        String parroquia_ = "";
        if (this.llogaret != null){
            llogaret_ = " ("+this.llogaret+")";
        }
        if (this.parroquia != null){
            llogaret_ = " ("+this.parroquia+")";
        }
        return this.nomMunicipi+llogaret_+parroquia_;
    }
    
    private void loadLloc(int id){
        try {
            this.idLloc = id;
            String str = "select id_municipi, llogaret, parroquia from lloc"
                    +" where id_lloc = ?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                this.llogaret=rs.getString("llogaret");
                this.parroquia=rs.getString("parroquia");
                this.nomMunicipi=rs.getString("id_municipi");                
            }
        } catch (SQLException ex) {
            Logger.getLogger(lloc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
