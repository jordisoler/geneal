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

import Exceptions.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class formFitxa {
    private formPersona c1, c2;
    private javax.swing.JTextField fitxa;
    private javax.swing.JTextArea comentaris;
    private javax.swing.JCheckBox casament;
    private formData data;
    private formLloc lloc;
    private formLlista fills;
    private int estat;
    
    private db.unio un;
    
    private final static int existent = 0;
    private final static int nousPares = 1;
    private final static int fromConjuge1 = 2;
    private final static int fromConjuge2 = 3;
    
    private static final boolean home = true;
    private static final boolean dona = false;
    
    public formFitxa(){
        un = new db.unio();
    }
    public formFitxa(db.unio un_,formPersona c1_, formPersona c2_, javax.swing.JTextField fitxa_,
            javax.swing.JTextArea comentaris_, javax.swing.JCheckBox casament_,
            formLlista fills_,formLloc lloc_, formData data_){
        un = un_;
        c1 = c1_;
        c2 = c2_;
        fitxa = fitxa_;
        comentaris = comentaris_;
        casament = casament_;
        fills = fills_;
        lloc = lloc_;
        data = data_;
    }
    public formFitxa(formPersona c1_, formPersona c2_, javax.swing.JTextField fitxa_,
            javax.swing.JTextArea comentaris_, javax.swing.JCheckBox casament_,
            formLlista fills_,formLloc lloc_, formData data_){
        this(new db.unio(), c1_, c2_, fitxa_,  comentaris_, casament_, fills_,
                lloc_, data_);
    }
    
    public void fill(){
        if(!un.isNull()){
            try {
                estat = existent;
                fitxa.setText(String.valueOf(un.getFitxa()));
                comentaris.setText(String.valueOf(un.getComentaris()));
                if(un.isMarriage()){
                    toggleCasament(true);
                    data.fill(un.getDataMatrimoni());
                    lloc.fill(un.getLlocMatrimoni());
                    db.persona[] pfills = un.getFills();
                    fills.fillList(pfills);
                }else{
                    toggleCasament(false);
                    casament.setSelected(false);
                }
                db.persona p1 = un.getConjuge1(), p2 = un.getConjuge2();
            
                c1.fill(p1);
                c2.fill(p2);
            } catch (DBException ex) {
                Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
                ex.show();
            }
        }else{
            new GException("No s'ha pogut carregar la unió. (id null).","Problema de carrega").show();
        }
    }  
    public void fill(int id, boolean conj){
        setEmptyUnio();
        db.persona p = new db.persona(id);
        try {
            if (conj){
                c1.fill(p);
                c2.fill(new db.persona());
                estat = fromConjuge1;
            }else{
                c1.fill(new db.persona());
                c2.fill(p);
                estat = fromConjuge2;
            }
        } catch (DBException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            ex.show();
        }
    }
    public void fill(int id_fill){
        setEmpty();
        db.persona[] ps = {new db.persona(id_fill)};
        fills.fillList(ps);
        estat = nousPares;
    }
    
    /**
     * Callback for click in a person.
     * @param p Person to be handled.
     */
    public void clickLlista(db.persona p){
        try{                                    // Es vol introduir una  persona  amb unió
            un=db.unio.fromConjuge(p.getId());
            fill();
        }catch (MUException e){} catch (DBException ex) {
            int reply = JOptionPane.showConfirmDialog(null, "La persona "+p+" no "
                    + "està a cap unió. \nVols crear-ne una de nova?","Unió nova",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(reply==JOptionPane.YES_OPTION){  // Es vol introduir una persona sense unió
                
                if (checkSexe(p)){
                    if (p.getSexe().equals("m")){   // La persona té gènere
                        fill(p.getId(), home);
                    }else{
                        fill(p.getId(), dona);
                    }
                }else{                              // La persona no té gènere
                    String[] options = {"Home", "Dona", "Cancelar"};
                    int response = JOptionPane.showOptionDialog(null, "La persona "
                            +p+" no té sexe. \nElegeix-ne un abans de  continuar",
                            "Introduïr sexe", JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                    switch(response){
                        case 0:
                            p.setSexe("m");
                            fill(p.getId(), home);
                            break;
                        case 1:
                            p.setSexe("f");
                            fill(p.getId(), dona);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
    
    private void setEmpty(){
        setEmptyUnio();
        c1.setEmpty();
        c2.setEmpty();
    }
    
    private void setEmptyUnio(){
        fitxa.setText("");
        comentaris.setText("");
        toggleCasament(false);
        db.persona[] ps = new db.persona[0];
        fills.fillList(ps);
    }
    
    private void toggleCasament(boolean on){
        casament.setSelected(on);
        if (on){
            lloc.enable();
            data.enable();
        }else{
            lloc.iniciar();
            data.iniciar();
            lloc.disable();
            data.disable();
        }
    }

    private boolean checkSexe(db.persona p) {
        try{
            switch(p.getSexe()){
                case "m":
                    return true;
                case "f":
                    return true;
                default:
                    return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }
    
}
