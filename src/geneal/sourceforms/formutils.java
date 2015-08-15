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

import com.jidesoft.swing.AutoCompletion;
import javax.swing.ImageIcon;

/**
 *
 * @author jordi
 */
public class formutils {
    
    final public static String unknown = "??";
    public static final ImageIcon icon = new ImageIcon("resources/icon.png");
    
    public static void fillDate(javax.swing.JComboBox dia, javax.swing.JComboBox mes,
            javax.swing.JTextField any, db.date d){
        dia.setSelectedItem(toUnknown(d.getDay()));
        mes.setSelectedItem(toUnknown(d.getMonth()));
        fillText(any,d.getYear());
    }
    
    public static void fillSexe(javax.swing.JComboBox cb, db.persona p){
        cb.setSelectedItem(sexe(p));
    }
    
    private static String toUnknown(String in){
        if (in == null || in.equals("") || in.equals("null")){
            return unknown;
        }else{
            return in;
        }
    }
    
    private static String toUnknown(int in){
        return toUnknown(String.valueOf(in));
    }
    
    public static void fillCbEditable(javax.swing.JComboBox cb, String[] data){
        cb.setEditable(true);
        fillCb(cb, data);
        AutoCompletion ac = new AutoCompletion(cb);
        ac.setStrict(false);
    }
    
    public static void fillCb(javax.swing.JComboBox cb, String[] data){
        String[] emplenar = new String[data.length+1];
        emplenar[0] = unknown;
        System.arraycopy(data, 0, emplenar, 1, data.length);
        javax.swing.ComboBoxModel cbm = new javax.swing.DefaultComboBoxModel(emplenar);
        cb.setModel(cbm);
    }
    
    
    public static String sexe(db.persona p){
        if (p==null || p.isNull()){
            return unknown;
        }
        try{
            if (p.getSexe().equals("m")){
                return "Home";
            }else if (p.getSexe().equals("f")){
                return "Dona";
            }else{
                return unknown;
            }
        }catch (NullPointerException e){
            return unknown;
        }
    }
    
    public static void fillText(javax.swing.JTextField t, String s){
        if (s == null || s.equals("null")){
            t.setText("");
        }else{
            t.setText(s);
        }
    }
    
    public static void fillText(javax.swing.JTextArea t, String s){
        if (s==null || s.equals("null")){
            t.setText("");
        }else{
            t.setText(s);
        }
    }
    
    public static void fillText(javax.swing.JTextField t, int i){
        fillText(t, String.valueOf(i));
    }
    
    public static void fillText(javax.swing.JTextArea t, int i){
        fillText(t, String.valueOf(i));
    }
    
    public static void carregarMunicipis(javax.swing.JComboBox cb){
        String[] municipis = db.municipi.getAll();
        fillCbEditable(cb, municipis);
    }
    
    public static void carregarLlogarets(javax.swing.JComboBox cb){
        String[] llogarets = db.lloc.getLlogarets();
        fillCbEditable(cb, llogarets);
    }
    
    public static void carregarParroquies(javax.swing.JComboBox cb){
        String[] parroquies = db.lloc.getParroquies();
        fillCbEditable(cb, parroquies);
    }
    
    public static void select(javax.swing.JComboBox cb,  String s){
        javax.swing.DefaultComboBoxModel m = (javax.swing.DefaultComboBoxModel) cb.getModel();
        if (m.getIndexOf(s)==-1){
            cb.setSelectedIndex(0);
        }else{
            cb.setSelectedItem(s);
        }
    }
    
    public static String s2q(String in){
        if (in == null || in.isEmpty() || in.equals("null")){
            return null;
        }else{
            return in;
        }
    }
    
    public static javax.swing.ComboBoxModel getModelSexe(){
        return  new javax.swing.DefaultComboBoxModel(
            new String[] {unknown, "Home", "Dona"});
    }
    
    public static boolean isEmptyCB(javax.swing.JComboBox b){
        return b.getSelectedIndex()==0 || b.getSelectedItem() == unknown ||
                b.getSelectedItem().equals("");
    }
    
    public static boolean isEmptyTF(javax.swing.JTextField t){
        return t.getText().equals(unknown) || t.getText().isEmpty();
    }
    
    public static boolean isEmptyTF(javax.swing.JTextArea t){
        return t.getText().equals(unknown) || t.getText().isEmpty();
    }
    
//    public static javax.swing.ComboBoxModel getModelDia(){
//        return new javax.swing.DefaultComboBoxModel(
//            new String[] {unknown, "1", "2", "3", "4", "5", "6", "7", "8", "9",
//            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", 
//            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});
//    }
//    
//    public static javax.swing.ComboBoxModel getModelMes(){
//        return new javax.swing.DefaultComboBoxModel(
//            new String[] {unknown, "1", "2", "3", "4", "5", "6", "7", "8", "9",
//            "10", "11", "12"});
//    }
    
    public static String getSexe(javax.swing.JComboBox cb){
        switch (String.valueOf(cb.getSelectedItem())){
            case "Home": return "m";
            case "Dona": return "f";
            default: return null;
        }
    }

    static String null2Void(String s) {
        if (s == null){
            return "";
        }else{
            return s;
        }
    }
}
