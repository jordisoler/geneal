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

import Exceptions.GException;
import db.persona;
import db.unio;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author jordi
 */
public class formPersonaVisor {
    private JLabel[] tree;
    private JLabel naixement;
    private JLabel defuncio;
    private JButton[] buttons;
    private int[] fitxes;
    
    public static int pares = 0;
    public static int padrinsPaterns = 1;
    public static int padrinsMaterns = 2;
    
    public formPersonaVisor(){}
    public formPersonaVisor(JLabel[] tree_, JButton[] buttons_, JLabel naixement_,
            JLabel defuncio_){
        tree = tree_;
        naixement = naixement_;
        defuncio = defuncio_;
        buttons = buttons_;
        fitxes = new int[3];
    }
    
    public void fill(db.persona p, String[] tree_, unio[] buttons_){
        int i;
        for (i=0; i< tree_.length; i++){
            tree[i].setText(tree_[i]);
        }
        for (i=0; i< buttons_.length; i++){
            fillButton(buttons[i], buttons_[i], i);
        }
        naixement.setText(stringNaixement(p));
        defuncio.setText(stringDefuncio(p));
    }

    public int getFitxa(int idx){
        System.out.println("Fitxes: "+fitxes[0]+", "+fitxes[1]+", "+fitxes[2]);
        if (idx==pares || idx==padrinsPaterns || idx==padrinsMaterns){
            return fitxes[idx];
        }else{
            new GException("S'ha intentat acoseguir una fitxa del visor incorrecte.",
                "Index icorrecte").show();
            return -1;
        }
    }
    
    private void fillButton(JButton button, unio unio_, int idx) {
        if(unio_.isNull()){
            button.setText("Fitxa no existent");
            button.setEnabled(false);
        }else{
            button.setText("Fitxa "+unio_.getFitxa());
            button.setEnabled(true);
            fitxes[idx] = unio_.getFitxa();
        }
    }

    private String stringNaixement(persona p) {
        db.lloc lloc = p.getLlocNaixement();
        db.date data = p.getDateNaixement();
        return formatLlocData(lloc, data);
    }
    
    private String stringDefuncio(persona p) {
        db.lloc lloc = p.getLlocDefuncio();
        db.date data = p.getDataDefuncio();
        return formatLlocData(lloc, data);
    }
    
    private String formatLlocData(db.lloc lloc, db.date data){
        String slloc = "", sdata = "", comma = "";
        if(!lloc.isNull()){
            slloc = lloc.toString();
        }
        if(!data.isNull()){
            sdata = data.toString();
        }
        if(!slloc.isEmpty() && !sdata.isEmpty()){
            comma = ", ";
        }
        return slloc+comma+sdata;
    }
}
