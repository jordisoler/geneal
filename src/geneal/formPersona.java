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
package geneal;

import Exceptions.DBException;
import Exceptions.LEException;
import Exceptions.dateException;
import static geneal.formutils.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class formPersona {
    private formLloc llnaixement, lldefuncio;
    private formData dnaixement, ddefuncio;
    private javax.swing.JTextField[] nom;
    private javax.swing.JTextArea comentaris;
    private javax.swing.JComboBox sexe;
    private javax.swing.JLabel label;
    private String[] ancestors;
            /* Order:
                - Persona
                - Pare
                - Padri patern
                - Padrina paterna
                - Mare
                - Padri matern
                - Padrina materna
            */
    private db.unio[] ancestorUnions;
            /* Order:
                - Pares marit
                - Padrins paterns
                - Padrins materns
            */
    private int conjuge;
    
    private final static int Cunknown = 0;
    public final static int Conjuge1 = 1;
    public final static int Conjuge2 = 2;
    private final static int idNull = -1;
    private int id;
    
    public formPersona(){
        id = idNull;
    }
    
    public formPersona(int id_, formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_, int conjuge_){
        id = id_;
        llnaixement = llnaixement_;
        lldefuncio = lldefuncio_;
        dnaixement =  dnaixement_;
        ddefuncio = ddefuncio_;
        nom = nom_;
        comentaris = comentaris_;
        sexe = sexe_;
        label =  label_;
        ancestors = new String [7];
        ancestorUnions = new db.unio [3];
        if (conjuge_ == Conjuge1 || conjuge_==Conjuge2){
            conjuge = conjuge_;
        }else{
            conjuge = Cunknown;
        }
        
        db.persona p = new db.persona(id_);
        getAncestorsFromDB(p);
        
        sexe.setModel(formutils.getModelSexe());
        this.setEmptyPrivate();
    }
    public formPersona(formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_, int conjuge_){
        this(idNull, llnaixement_, lldefuncio_, dnaixement_, ddefuncio_, nom_, 
                comentaris_, sexe_, label_, conjuge_);
    }
    public formPersona(int id_, formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_){
        this(id_, llnaixement_, lldefuncio_, dnaixement_, ddefuncio_, nom_, 
                comentaris_, sexe_, label_, Cunknown);
    }
    public formPersona(formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_){
        this(idNull, llnaixement_, lldefuncio_, dnaixement_, ddefuncio_, nom_, 
                comentaris_, sexe_, label_, Cunknown);
    }
    
    public boolean isNew(){
        return this.id==idNull;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void fill(db.persona p) throws DBException{
        if (p.isNull()){
            id = idNull;
            setEmpty();            
        }else{
            id = p.getId();
            fillText(nom[0],p.getNom());
            fillText(nom[1], p.getLlinatge1());
            fillText(nom[2], p.getLlinatge2());
            llnaixement.fill(p.getLlocNaixement());
            lldefuncio.fill(p.getLlocDefuncio());
            dnaixement.fill(p.getDateNaixement());
            ddefuncio.fill(p.getDataDefuncio());
            fillSexe(sexe,p);
            fillText(comentaris,  p.getComentaris());
            label.setText(sexe(p));
            getAncestorsFromDB(p);
        }
    }
    
    public void add(){
        db.persona p = new db.persona(),  anterior  = new db.persona(id);
        db.naixement n = new db.naixement(), nanterior;
        try {
            nanterior = new db.naixement(id);
        } catch (DBException ex) {
            nanterior= new db.naixement();
        }
        try {
            p = getPerson();
            n = getNaixement();
        } catch (dateException ex) {
            Logger.getLogger(formPersona.class.getName()).log(Level.SEVERE, null, ex);
            ex.show();
        } catch (LEException ex) {
            Logger.getLogger(formPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!checkSexe()){
            return;
        }
        if (!anterior.equals(p)){
            System.out.println("S'afegirà la persona "+p);
            id = p.addPersona();
            n.setFill(id);
        }        
        if (!n.equals(nanterior)){
            n.addNaixement();
        }
    }
    
    public db.persona getPerson() throws LEException, dateException {
        db.persona p = new db.persona();
        try {
            p.setNom(getString(nom[0]));
            p.setLlinatge1(getString(nom[1]));
            p.setLlinatge2(getString(nom[2]));
            try {
                p.setDataDefuncio(ddefuncio.getDate());
            } catch (dateException ex) {
                ex.setContext("defunció "+conj());
                throw ex;
            }
            try {
                p.setLlocDefuncio(lldefuncio.getLloc().getId());
            } catch (LEException ex) {
                ex.setContext("defunció "+conj());
                throw ex;
            }
            p.setComentaris(getString(comentaris));
            p.setSexe(getSexe(sexe));
            p.setId(id);
        } catch (SQLException ex) {
            Logger.getLogger(formPersona.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;
    }
    
    public void setEmpty(){
        this.setEmptyPrivate();
    }
    
    public void setEmpty(String s){
        this.setEmptyPrivate();
        nom[1].setText(s);
    }
    
    public boolean isEmpty(){
        for(javax.swing.JTextField t: nom){
            if(!isEmptyTF(t)){
                return false;
            }
        }
        return llnaixement.isEmpty() && lldefuncio.isEmpty() && dnaixement.isEmpty() &&
                ddefuncio.isEmpty() && isEmptyTF(comentaris) && isEmptyCB(sexe);
    }
    
    public db.naixement getNaixement(int idu) throws dateException, LEException {
        db.naixement n;
        if (id!=-1){
            try {
                n = new db.naixement(idu);
            } catch (DBException ex) {
                n = new db.naixement();
            }
        }else{
            n = new db.naixement();
        }
        try {
            n.setDate(dnaixement.getDate());
        } catch (dateException ex) {
            ex.setContext("naixement "+conj());
            throw ex;
        }
        try {
            n.setLloc(llnaixement.getLloc());
        } catch (SQLException ex) {
            Logger.getLogger(formPersona.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LEException ex) {
            ex.setContext("naixement "+conj());
            throw ex;
        }
        n.setFill(idu);
        return n;
    }
    public db.naixement getNaixement() throws dateException, LEException{
        return getNaixement(id);
    }
    
    public String[] getAncestors(){
        return this.ancestors;
    }
    public db.unio[] getAncestorUnions(){
        return this.ancestorUnions;
    }
    
    public boolean isKnown(){
        return this.id != -1;
    }
    
    private void setEmptyPrivate(){
        id = idNull;
        label.setText(unknown);
        llnaixement.iniciar();
        lldefuncio.iniciar();
        dnaixement.iniciar();
        ddefuncio.iniciar();
        for (javax.swing.JTextField t: nom){
            t.setText("");
        }
        comentaris.setText("");
        sexe.setSelectedIndex(0);
    }
    
    private String getString(javax.swing.JTextField tf){
        if (tf.getText() == null || tf.getText().isEmpty()|| tf.getText().equals(unknown)){
            return null;
        }else{
            return tf.getText();
        }
    }
    private String getString(javax.swing.JTextArea tf){
        if (tf.getText() == null || tf.getText().isEmpty()|| tf.getText().equals(unknown)){
            return null;
        }else{
            return tf.getText();
        }
    }
    
    private boolean checkSexe(){
        if (sexe.getSelectedIndex() == 0 && !nom[0].getText().isEmpty() ){
            int r = JOptionPane.showConfirmDialog(null, "La persona que vols "
                    + "introduir "+conj()+" no té sexe. \n Vols introduir-la igualment?", 
                    "Sexe no especificat", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (r != JOptionPane.YES_OPTION){
                JOptionPane.showMessageDialog(null, "Corregeix l'error i "
                        + "tora-ho  a provar.", "Corregeig l'error", 
                        JOptionPane.OK_OPTION);
                return false;
            }
        }
        return true;
    }
    
    private String conj(){
        switch (conjuge){
            case Conjuge1:
                return "(conjuge 1)";
            case Conjuge2:
                return "(conjuge 2)";
            default:
                return "";
        }
    }

    private void getAncestorsFromDB(db.persona p) {
        db.persona cp = p.getPare();
        db.persona cm;
        try {
            cm = p.getMare();
        } catch (DBException ex) {
            cm = new db.persona();
        }
        
        ancestorUnions[0] = p.getUnioPares();
        ancestorUnions[1] = cp.getUnioPares();
        ancestorUnions[2] = cm.getUnioPares();
        
        ancestors[0]=p.toString();
        ancestors[1]=cp.toString();
        ancestors[2]=cp.getPare().toString();
        try {
            ancestors[3]=cp.getMare().toString();
        } catch (DBException ex) {
            ancestors[3] = new db.persona().toString();
        }
        ancestors[4]=cm.toString();
        ancestors[5]=cm.getPare().toString();
        try {
            ancestors[6]=cm.getMare().toString();
        } catch (DBException ex) {
            ancestors[6] = new db.persona().toString();
        }
    }
    
}
