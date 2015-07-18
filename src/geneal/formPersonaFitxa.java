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

/**
 *
 * @author jordi
 */
public class formPersonaFitxa extends formPersona{
    private javax.swing.JButton bpares;
    
    private final static String LoadPares = "Fitxa pares";
    private final static String NewPares = "Afegir pares";
    
    public formPersonaFitxa(){
        super();
    }
    public formPersonaFitxa(int id_, formLloc llnaixement_, formLloc lldefuncio_,
            formData dnaixement_, formData ddefuncio_, javax.swing.JTextField[] nom_,
            javax.swing.JTextArea comentaris_, javax.swing.JComboBox sexe_,
            javax.swing.JLabel label_, javax.swing.JLabel[] tree_, int conjuge_,
            javax.swing.JButton bpares_){
        super(id_, llnaixement_, lldefuncio_, dnaixement_, ddefuncio_, nom_, 
                comentaris_, sexe_, label_, tree_, conjuge_);
        bpares = bpares_;
    }
    
    @Override
    public void fill(db.persona p) throws DBException{
        super.fill(p);
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
    }
    
    public boolean newPares(){
        return this.bpares.getText().equals(NewPares);
    }

}
