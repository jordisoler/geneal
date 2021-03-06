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
import Exceptions.MUException;
import db.unio;
import java.util.ArrayList;

/**
 *
 * @author jordi
 */
public class formLlista {
    private javax.swing.JList list;
    private ArrayList<Integer> people;
    
    public formLlista(javax.swing.JList list_){
        list = list_;
        people = new ArrayList<>();
        db.persona[] ps = {};
        fillList(ps);
    }
    
    public db.persona getPerson(int i){
        return new db.persona(people.get(i));
    }
    
    public db.persona getSelectedPerson(){
        int idx = list.getSelectedIndex();
        return getPerson(idx);
    }
    
    public void add(db.persona p){
        people.add(p.getId());
        javax.swing.DefaultListModel<String> lm = (javax.swing.DefaultListModel<String>) list.getModel();
        lm.addElement(formatList(p));
        list.setModel(lm);
    }
    
    public void dropPerson(){
        int i = list.getSelectedIndex();
        db.persona p = getPerson(i);
        p.delete();
        people.remove(i);
        javax.swing.DefaultListModel<String> lm = (javax.swing.DefaultListModel<String>) list.getModel();
        lm.removeElementAt(i);
        list.setModel(lm);
    }
    
    public void fillList(db.persona[] ps){
        javax.swing.DefaultListModel<String> lm = new javax.swing.DefaultListModel<>();
        people = new ArrayList<>();
        
        for (db.persona p : ps){
            lm.addElement(formatList(p));
        }
        list.setModel(lm);
    }
  
    private String formatList(db.persona p){
        String naixement = "", sdn = "", sdd="", p1="", p2="", p3="", sfitxa="";
        db.unio u = new unio();
        try {
            u = db.unio.fromConjuge(p.getId());
        } catch (MUException |DBException ex) {}
        
        if (!u.isNull()){
            sfitxa="[Fitxa "+u.getFitxa()+"] ";
        }
        people.add(p.getId());
        db.lloc n = p.getLlocNaixement();
        db.date dn = p.getDateNaixement();
        db.date dd = p.getDataDefuncio();
        if (!n.isNull()){
            naixement = ", "+n;
        }
        if (!dn.isNull() || !dd.isNull()){
            p1 = " (";
            p2 = "/";
            p3 = ")";
        }
        if (!dn.isNull()){
            sdn = dn.toString();
        }
        if (!dd.isNull()){
            sdd = dd.toString();
        }
        return sfitxa+p+naixement+p1+sdn+p2+sdd+p3;
    }
}
