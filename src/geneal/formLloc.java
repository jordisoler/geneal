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
import Exceptions.LEException;
import static geneal.formutils.fillCbEditable;
import static geneal.formutils.isEmptyCB;
import static geneal.formutils.select;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;


/**
 *
 * @author jordi
 */
public class formLloc {
    public static final int BAD_LLOC = -1;
    public static final int NULL_LLOC = -2;
    private final JComboBox Municipi;
    private final JComboBox Parroquia;
    private final JComboBox Llogaret;
    
    
    public formLloc(JComboBox m, JComboBox p,  JComboBox ll){
        this.Municipi = m;
        this.Parroquia  = p;
        this.Llogaret = ll;
        
        String[] municipis = db.municipi.getAll();
        String[] llogarets = db.lloc.getLlogarets();
        String[] parroquies = db.lloc.getParroquies();
        
        fillCbEditable(this.Municipi,  municipis);
        fillCbEditable(this.Parroquia,  parroquies);
        fillCbEditable(this.Llogaret,  llogarets);
    }
    
    public void fill(db.lloc l){
        if (!l.isNull()){
            select(Municipi, l.getMunicipi());
            this.selectedMunicipi();
            select(Llogaret,  l.getLlogaret());
            select(Parroquia, l.getParroquia());
        }else{
            this.iniciar();
        }
    }
    
    public db.lloc getLloc() throws LEException, SQLException{
        db.lloc out = new db.lloc();
        boolean newmunicipi, newparroquia, newllogaret;
        String sparroquia ="", sllogaret="", queryparroquia = "", queryllogaret ="";
        String m = String.valueOf(Municipi.getSelectedItem());
        String p = Parroquia.getSelectedItem().toString();
        String ll = Llogaret.getSelectedItem().toString();
        newmunicipi = (!db.municipi.exist(m) && Municipi.getSelectedIndex() !=0) &&
                !m.equals("null");
        newparroquia = !db.lloc.isAlreadyParroquia(p,m) && Parroquia.getSelectedIndex() !=0;
        newllogaret = !db.lloc.isAlreadyLlogaret(ll,m) && Llogaret.getSelectedIndex() !=0;
        
        if (Municipi.getSelectedIndex() == 0){
            if (Parroquia.getSelectedIndex() == 0 && Llogaret.getSelectedIndex()==0){
                return out;
            }else{
                throw new LEException("Un lloc té parroquia o llogaret però no municipi. "
                        + "Corregi-ho abans de procedir.");
            }
        }else{
            out.setMunicipi(m);
        }
        if (newmunicipi){
            System.out.println("Municipi '"+Municipi.getSelectedItem()+"'");
            if (Parroquia.getSelectedIndex()!=0){
                out.setParroquia(p);
                sparroquia = " (amb la parroquuia '"+p+"')";
            }
            if (Llogaret.getSelectedIndex() !=0){
                out.setLlogaret(ll);
                sllogaret = " (amb el llogaret '"+ll+"')";
            }
            String msg = "El municipi '"+m+sparroquia
                    +sllogaret+"' no existeix. Vols afegir-lo?";
            int reply = JOptionPane.showConfirmDialog(null, msg, "Nou municipi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply == JOptionPane.YES_OPTION){
                db.municipi dbm = new db.municipi(m);
                dbm.add();
                out.addLloc();
                return out;
            }else{
                throw new LEException("Hi ha errors en el lloc. Corregeix-ho abans de procedir.");
            }
        }
        if (newparroquia){
            out.setParroquia(p);
            if (Llogaret.getSelectedIndex() !=0){
                out.setLlogaret(ll);
                sllogaret = " (al llogaret '"+ll+"')";
            }
            String msg = "La parroquia '"+p+"' no consta com a parroquia del municipi '"
                    + m +"'. Vols afegir '"+p+"' "+sllogaret+" com a nou lloc?";
            int reply = JOptionPane.showConfirmDialog(null, msg, "Nova parroquia",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply==JOptionPane.YES_OPTION){
                out.addLloc();
                return out;
            }else{
                throw new LEException("Hi ha errors en el lloc. Corregeix-ho abans de procedir.");
            }
        }
        if (newllogaret){
            out.setLlogaret(ll);
            if (Parroquia.getSelectedIndex() !=0){
                out.setParroquia(p);
                sparroquia = " (a la parroquia '"+p+"')";
            }
            String msg = "El llogaret '"+ll+"' no consta com a llogaret del municipi '"
                    + m +"'. Vols afegir '"+ll+"' "+sparroquia+" com a nou lloc?";
            int reply = JOptionPane.showConfirmDialog(null, msg, "Nou llogaret",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply==JOptionPane.YES_OPTION){
                int newid = out.addLloc();
                System.out.println("El lloc esta al municipi "+out.getMunicipi()+
                        " amb id "+out.getId());
                return new db.lloc(newid);
            }else{
                throw new LEException("Hi ha errors en el lloc. Corregeix-ho abans de procedir.");
            }
        }
        if (Parroquia.getSelectedIndex()!=0){
            queryparroquia = p;
        }
        if (Llogaret.getSelectedIndex()!=0){
            queryllogaret = ll;
        }
        try{
            if (m.equals("null")){
                out = new db.lloc();
            }else{
                out = db.lloc.loadLloc(m, queryparroquia, queryllogaret);
            }
        }catch (LEException e){
            String msg = "El lloc amb municipi "+m+" llogaret "+ll+" i parroquia "+
                    p+" no existex. Vols introduir-ho?";
            int reply = JOptionPane.showConfirmDialog(null, msg, "Nou llogaret",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply==JOptionPane.YES_OPTION){
                out.setLlogaret(ll);
                out.setParroquia(p);
                out.addLloc();
                return out;
            }else{
                throw new LEException("Selecciona un altre lloc.");
            }
        }
        return out;
    }
    
    public void selectedMunicipi(){
        String[] sparroquies = db.lloc.getParroquies(String.valueOf(Municipi.getSelectedItem()));
        String[] sllogarets = db.lloc.getLlogarets(String.valueOf(Municipi.getSelectedItem()));
        if (!Arrays.asList(sparroquies).contains(Parroquia.getSelectedItem())){
            fillCbEditable(Parroquia, sparroquies);
        }
        if (!Arrays.asList(sllogarets).contains(Llogaret.getSelectedItem())){
            fillCbEditable(Llogaret, sllogarets);
        }
    }
    public void selectedMunicipi(String municipi){
        String[] sparroquies = db.lloc.getParroquies(String.valueOf(municipi));
        String[] sllogarets = db.lloc.getLlogarets(String.valueOf(municipi));
        if (Parroquia.getSelectedIndex()==0){
            fillCbEditable(Parroquia, sparroquies);
        }
        if (Llogaret.getSelectedIndex()==0){
            fillCbEditable(Llogaret, sllogarets);
        }
    }
    public void selectedParroquia(){
        if (Municipi.getSelectedIndex() == 0){
            db.lloc lloc = db.lloc.fromParroquia(String.valueOf(Parroquia.getSelectedItem()));
            Municipi.setSelectedItem(lloc.getMunicipi());
        }        
    }
    public void selectedLlogaret(){
        if (Municipi.getSelectedIndex() == 0){
            db.lloc lloc = db.lloc.fromLlogaret(String.valueOf(Llogaret.getSelectedItem()));
            Municipi.setSelectedItem(lloc.getMunicipi());
        }        
    }
    
    public boolean isEmpty(){
        return isEmptyCB(Municipi) && isEmptyCB(Llogaret) && isEmptyCB(Parroquia);
    }
    
    public void iniciar(){
        Municipi.setSelectedIndex(0);
        Llogaret.setSelectedIndex(0);
        Parroquia.setSelectedIndex(0);
    }
    
    public void disable(){
        Municipi.setEnabled(false);
        Llogaret.setEnabled(false);
        Parroquia.setEnabled(false);
    }
    
    public void enable(){
        Municipi.setEnabled(true);
        Llogaret.setEnabled(true);
        Parroquia.setEnabled(true);
    }
    
   
}
