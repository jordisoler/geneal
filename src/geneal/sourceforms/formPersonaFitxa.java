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
package geneal.sourceforms;

import Exceptions.DBException;
import Exceptions.LEException;
import Exceptions.dateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class formPersonaFitxa extends formPersona{
    private javax.swing.JButton bpares;
    private javax.swing.JLabel[] tree;
    private formPersonaVisor visor;
    
    private final static String LoadPares = "Fitxa pares";
    private final static String NewPares = "Afegir pares";
    
    public formPersonaFitxa(){
        super();
    }
    public formPersonaFitxa(int id_, formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_, javax.swing.JLabel[] tree_, int conjuge_,
            javax.swing.JButton bpares_, formPersonaVisor visor_){
        super(id_, llnaixement_, lldefuncio_, dnaixement_, ddefuncio_, nom_, 
                comentaris_, sexe_, label_, conjuge_);
        bpares = bpares_;
        tree = tree_;
        visor = visor_;
        fillAncestors(this.getAncestors(), tree);
        visor.fill(new db.persona(id_), this.getAncestors(), this.getAncestorUnions());
    }
    
    @Override
    public void fill(db.persona p) throws DBException{
        super.fill(p);
        fillAncestors(this.getAncestors(), tree);
        visor.fill(p, this.getAncestors(), this.getAncestorUnions());
        bpares.setEnabled(true);
        try{
            db.naixement n = new db.naixement(p.getId());
            db.unio u = new db.unio(n.getIdUnio());
            bpares.setText(LoadPares);
        }catch (DBException e){
            bpares.setText(NewPares);
        }
    }
    
    @Override
    public void setEmpty(){
        super.setEmpty();
        bpares.setEnabled(false);
        db.persona nullP = new db.persona();
        for (javax.swing.JLabel l : tree){
            l.setText(nullP.toString());
        }
    }
    
    public void replace(db.persona p){
        if (this.isKnown()){
            try {
                int reply = JOptionPane.showConfirmDialog(null, "La persona "+this.getPerson()+
                        " serà reemplaçada per "+p+".\n\t Vols procedir?", "Geneal -"
                                + " Reemplaçar persona?", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                if (reply != JOptionPane.YES_OPTION){
                    return;
                }
            } catch (LEException | dateException ex) {
                ex.show();
                Logger.getLogger(formPersonaFitxa.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        try {
            fill(p);
        } catch (DBException ex) {
            ex.show();
            Logger.getLogger(formPersonaFitxa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean newPares(){
        return this.bpares.getText().equals(NewPares);
    }

    private void fillAncestors(String[] values, javax.swing.JLabel[] labels){
        for (int i=0; i<labels.length; i++){
            labels[i].setText(values[i]);
        }
    }
}
