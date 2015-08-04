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
import Exceptions.dateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class persona extends conexio{
    private int idPersona;
    private String nom;
    private String llinatge1;
    private String llinatge2;
    private date dataDefuncio;
    private int idLlocDefuncio;
    private String sexe;
    private String comentaris;
    
    //--------CONSTRUCTORS---------
    public persona(){
        super();
        this.idPersona = -1;
        this.nom = null;
        this.llinatge1 = null;
        this.llinatge2 = null;
        this.dataDefuncio = new date();
        this.idLlocDefuncio = -1;
        this.sexe = null;
        this.comentaris = null;
    }
    
    public persona(int id){
        this();
        try {
            String str = "select * from persona where id_persona=?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
                this.idPersona = id;
                this.nom = rs.getString("nom");
                this.llinatge1 = rs.getString("llinatge1");
                this.llinatge2 = rs.getString("llinatge2");
                try {
                    this.dataDefuncio = new date(rs.getCharacterStream("data_defuncio"));
                } catch (dateException ex) {
                    this.dataDefuncio = new date();
                }
                this.idLlocDefuncio = rs.getInt("lloc_defuncio");
                if (rs.wasNull()){
                    this.idLlocDefuncio = -1;
                }
                this.sexe = rs.getString("sexe");
                this.comentaris = rs.getString("comentaris");
            }
        } catch (SQLException ex) {
            System.err.println("Excepció amb id_persona: "+id);
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //----------PUBLIC methods----------
    //----------Getting properties----------
    public int getId(){
        return this.idPersona;
    }
    
    public String getNom(){
        return this.nom;
    }
    
    public String getLlinatge1(){
        return this.llinatge1;
    }
    
    public String getLlinatge2(){
        return this.llinatge2;
    }
    
    public date getDataDefuncio(){
        return this.dataDefuncio;
    }
    
    public int getIdLlocDefuncio(){
        return this.idLlocDefuncio;
    }
    public lloc getLlocDefuncio(){
        lloc l = new lloc(this.idLlocDefuncio);
        return l;
    }
    
    public String getSexe(){
        return this.sexe;
    }
    
    public String getComentaris(){
        return this.comentaris;
    }
    
    //----------Additional gets----------
    public unio getUnioPares(){
        try {
            if (this.isNull()){
                return new unio();
            }
            naixement n = new naixement(this.idPersona);
            return new unio(n.getIdUnio());
        } catch (DBException ex) {
            return new unio();
        }
    }
    
    public persona getPare(){
        try {
            if (this.isNull()){
                return new persona();
            }
            naixement n = new naixement(this.idPersona);
            unio u = new unio(n.getIdUnio());
            persona p = new persona(u.getIdConjuge1());
            return p;
        } catch (DBException ex) {
            return new persona();
            //Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public persona getMare() throws DBException{
        try {
            if (this.isNull()){
                return new persona();
            }
            naixement n = new naixement(this.idPersona);
            unio u = new unio(n.getIdUnio());
            persona p = new persona(u.getIdConjuge2());
            return p;
        } catch (DBException ex) {
            return new persona();
            //Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<familia> getFamilies(){
        ArrayList<familia> families = new ArrayList<>();
        try {
            String str = "select id_unio from unio where id_conjuge1=? or"
                    + " id_conjuge2 = ?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, this.idPersona);
            pst.setInt(2, this.idPersona);
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                try {
                    families.add(new familia(rs.getInt("id_unio")));
                } catch (DBException ex) {ex.show();}
            }
        } catch (SQLException ex) {
            Logger.getLogger(familia.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
        return families;
    }
    
    public ArrayList<Integer> getIdFills(){
        ArrayList<familia> families = this.getFamilies();
        ArrayList<Integer> fills = new ArrayList<>();
        
        for(familia f: families){
            fills.addAll(f.getListFills());
        }
        return fills;
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
    
    public int getIdLlocNaixement(){
        naixement n =  new naixement();
        try {
            n = new naixement(this.idPersona);
        } catch (DBException ex) {System.out.println("La persona "+this+" no té "
                + "naixement i s'hi ha intentat accedir");}
        return n.getIdLloc();
    }
    
    public lloc getLlocNaixement(){
        try {
            naixement n = new naixement(this.idPersona);
            return n.getLloc();
        } catch (DBException ex) {
            return  new lloc();
        }
    }
    
    public date getDateNaixement(){
        try {
            naixement n = new naixement(this.idPersona);
            return n.getDate();
        } catch (DBException ex) {
            return new date();
        }
    }
    
    //----------Setting data----------
    public void setId(int in){
        this.idPersona = in;
    }
    
    public void setNom(String in){
        this.nom = in;
    }
    
    public void setLlinatge1(String in){
        this.llinatge1 = in;
    }
    
    public void setLlinatge2(String in){
        this.llinatge2 = in;
    }
    
    public void setDataDefuncio(date in){
        this.dataDefuncio = in;
    }
    
    public void setLlocDefuncio(int in){
        this.idLlocDefuncio = in;
    }
    
    public void setSexe(String in){
        if (in == null || in.equals("m") || in.equals("f")){
            this.sexe = in;
        }else{
            System.out.println("Gènere no vàlid.");
            try {
                throw new Exception();
            } catch (Exception ex) {
                Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setComentaris(String in){
        this.comentaris = in;
    }
    
    //----------Adding data to database----------
    public int addPersona(){
        if (this.idPersona == -1){
            int id = addNewPersona();
            this.idPersona = id;
            return id;
        }else{
            updatePersona();
            return this.idPersona;
        }
    }
    public int addPersona(naixement n){
        int idf = addPersona();
        n.setFill(idf);
        n.addNaixement();
        return idf;
    }
    
    //---------Deleting data from  database----------
    public boolean delete(){
        if(persona.exist(this)){
            try {
                unio u;
                try {
                    u = unio.fromConjuge(this.getId());
                } catch (DBException ex) {
                    u  =  new unio();
                }
                if (!u.isNull()){
                    int reply = JOptionPane.showConfirmDialog(null, "La persona "+this
                        + " forma part d'una unió (matrimoni). Segur que la vols "
                        + "eliminar?", "Eliminar persona?", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    if (reply==JOptionPane.YES_OPTION){
                        unio.deletePersona(this.getId());
                    }else{
                        return false;
                    }
                }
            } catch (MUException ex) {
                int reply = JOptionPane.showConfirmDialog(null, "La persona "+this+" es troba "
                        + "en dideferents unions (matrimonis). Segur que la vols "
                        + "eliminar?", "Eliminar persona?", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (reply==JOptionPane.YES_OPTION){
                    unio.deletePersona(this.getId());
                }else{
                    return false;
                }
            }
            try{
                naixement n = new naixement(this.getId());
                boolean i = n.delete();
            }catch (Exception e){
                System.out.println("No s'ha eliminat  cap naixement per a la id: "+this.getId());
            }
            try{
                String str = "delete from persona where id_persona = ?";
                PreparedStatement pst = con.prepareStatement(str);
                pst.setInt(1, this.getId());
                pst.execute();
                return true;
            }catch (Exception e){
                Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, e);
                return false;
            }
        }else{
            return false;
        }
    }
    
    //----------Boolean methods-----------
    public boolean innerTree(){
        Tree t = new Tree(this);
        ArrayList<ArrayList<persona>> branches = t.getBranches(idroot1);
        ArrayList<ArrayList<persona>> branches2 = t.getBranches(idroot2);
        return !branches.isEmpty() || !branches2.isEmpty();
    }
    
    public boolean isNull(){
        return this.idPersona == -1;
    }
    
    //----------Generation methods-----------
    public ArrayList<Integer> getGenerations(){
        ArrayList<Integer> generations = new ArrayList<>();
        Tree t = new Tree(this);
        ArrayList<ArrayList<persona>> branches = t.getBranches(idroot1);
        for (ArrayList<persona> branch : branches){
            generations.add(branch.size());
        }
        return generations;
    }
    public String getGenerationsString(){
        ArrayList<Integer> generations = this.getGenerations();
        if (generations.isEmpty()){
            return geneal.formutils.unknown;
        }else{
            String sout = "";
            for (int i : generations){
                if (generations.indexOf(i) != 0){
                    sout = sout + " / ";
                }
                sout = sout+i;
            }
            return sout;
        }
    }
    
    @Override
    public String toString(){
        if ((this.nom == null && this.llinatge1 == null && this.llinatge2 == null) ||
                this.isNull()){
            return geneal.formutils.unknown;
        }else{
            String l1 ="",  l2="";
            if (this.llinatge1 != null){
                l1 = " "+this.llinatge1;
            }
            if (this.llinatge2 != null){
                l2 = " "+this.llinatge2;
            }
            return this.nom+l1+l2;
        }
    }
    
    // ----------STATIC methods (public)----------
    public static boolean exist(persona p){
        try {
            String str = "select * from  persona where id_persona = ?";
            PreparedStatement pst = con.prepareStatement(str);
            pst.setInt(1, Types.INTEGER);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static int count(){
        persona.connect();
        try {
            String str = "select count(*) as num from  persona;";
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(str);
            if (rs.next()){
                return rs.getInt("num");
            }else{
                throw new SQLException();
            }
        } catch (SQLException ex) {
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    public static persona[] search(String[] valors){
        persona.connect();
        ArrayList<persona> people = new ArrayList<>();
        String llocn = "llocn", llocd = "llocd", id_p = "id";
        String[] camps = {"persona.nom", "persona.llinatge1", "persona.llinatge2",
            llocn+".id_municipi",llocd+".id_municipi"};
        try{
            String str = "select persona.id_persona as "+id_p+" from  persona\n" +
                "left join naixement on\n" +
                "persona.id_persona = naixement.id_fill\n" +
                "left join lloc as "+llocn+" on\n" +
                "naixement.id_lloc = "+llocn+".id_lloc\n" +
                "left join lloc as "+llocd+" on\n" +
                "persona.lloc_defuncio = "+llocd+".id_lloc\n" +
                "where true ";
            
            int idx = 0;
            for (String s : camps){
                if (valors[idx]!=null){
                    str = str + "and "+s+" like ? ";
                }
                idx ++;
            }
            
            PreparedStatement pst = con.prepareStatement(str);
            int counter = 0;
            for (String s : valors){
                if (s!=null){
                    counter++;
                    pst.setString(counter, "%"+s+"%");
                }
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                people.add(new persona(rs.getInt(id_p)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
        persona[] peopleArr = new persona[people.size()];
        return people.toArray(peopleArr);
    }
    
    /**
     *  Cerca persones a la base de dades a partir del seu nom complet.
     * S'ha d'afegir tractament per a camps nuls.
     * @param name Nom
     * @param l1 Primer llinatge
     * @param l2 Segon llinatge
     * @return Array amb les persones trobades
     */
    public static persona[] getPeopleLike(String name, String l1, String l2){
        persona.connect();
        ArrayList<persona> people = new ArrayList<>();
        try {
            String stm = "select * from persona where nom like ?"
                    + " and (llinatge1 like ?) and (llinatge2 like ?)";
            PreparedStatement pst  =  con.prepareStatement(stm);
            pst.setString(1, "%"+name+"%");
            pst.setString(2, "%"+l1+"%");
            pst.setString(3, "%"+l2+"%");
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                people.add(new persona(rs.getInt("id_persona")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(persona.class.getName()).log(Level.SEVERE, null, ex);
        }
        persona[] peopleArr = new persona[people.size()];
        return people.toArray(peopleArr);
    }
     
    // ----------PRIVATE methods---------
    private int addNewPersona(){
        try {
            String str = "insert into persona (nom, llinatge1, llinatge2,"
                    + "data_defuncio, lloc_defuncio, sexe, comentaris)"
                    + " values (?,?,?,?,?,?,?)";
            PreparedStatement pst=con.prepareStatement(str,  Statement.RETURN_GENERATED_KEYS);
            setString(pst,1, this.nom);
            setString(pst,2, this.llinatge1);
            setString(pst,3, this.llinatge2);
            pst.setString(4, this.dataDefuncio.toQuery());
            if (this.idLlocDefuncio == -1){
                pst.setNull(5, Types.INTEGER);
            }else{
                pst.setInt(5, this.idLlocDefuncio);
            }
            setString(pst,6, this.sexe);
            setString(pst,7, this.comentaris);
            pst.execute();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
    
    private void updatePersona(){
        try {
            String str = "update persona set nom=?, llinatge1=?, llinatge2=?,"
                    + "data_defuncio=?, lloc_defuncio=?, sexe=?, comentaris=?"
                    + " where id_persona=?";
            PreparedStatement pst=con.prepareStatement(str);
            setString(pst,1, this.nom);
            setString(pst,2, this.llinatge1);
            setString(pst,3, this.llinatge2);
            pst.setString(4, this.dataDefuncio.toQuery());
            if (this.idLlocDefuncio == -1){
                pst.setNull(5, Types.INTEGER);
            }else{
                pst.setInt(5, this.idLlocDefuncio);

            }
            setString(pst,6, this.sexe);
            setString(pst,7, this.comentaris);
            pst.setInt(8, this.idPersona);
            pst.execute();
        } catch (SQLException ex) {
            Logger.getLogger(municipi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
