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
import javax.swing.JComboBox;
import javax.swing.JTextField;
import static geneal.formutils.*;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import Exceptions.*;

/**
 *
 * @author jordi
 */
public class formData {
    private final JComboBox Dia;
    private final JComboBox Mes;
    private final JTextField Any;
        
    public final int BAD_YEAR = -1;
    public final int NULL_YEAR = -2;
    
    private static final String funknown = unknown;
    
    public formData(){
        Dia = new JComboBox();
        Mes = new JComboBox();
        Any = new JTextField();
        Dia.setModel(formData.getModelDia());
        Mes.setModel(formData.getModelMes());
        Any.setText(funknown);
    }
    
    public formData(JComboBox d, JComboBox m, JTextField a){
        Dia = d;
        Mes = m;
        Any = a;
        Dia.setModel(formData.getModelDia());
        Mes.setModel(formData.getModelMes());
        Any.setText(funknown);
    }
        
    public void fill(db.date d){
        try{
            Dia.setSelectedIndex(d.getDay());
            Mes.setSelectedIndex(d.getMonth());
            if (d.isNull()){
                Any.setText(funknown);
            }else{
                Any.setText(String.valueOf(d.getYear()));
            }
        }catch (NullPointerException e){
            iniciar();
        }        
    }
    
    public db.date getDate() throws BYException, dateException{
        db.date d = new db.date();
        int n = this.getAny();
        if (n == this.BAD_YEAR){
            throw new BYException();
        }else if (n==this.NULL_YEAR){
            if (Dia.getSelectedIndex()!=0){
                throw new dateException("L'any és nul però el dia no.");
            }
            if (Mes.getSelectedIndex() !=0){
                throw new dateException("L'any és nul però el mes no.");
            }
        }else{
            if (db.date.checkMonthDay(Mes.getSelectedIndex(), Dia.getSelectedIndex())){
                d.setDay(Dia.getSelectedIndex());
                d.setMonth(Mes.getSelectedIndex());
                d.setYear(Integer.parseInt(Any.getText()));
            }else{
                throw new dateException("La data introduida és incorrecte.");
            }
        }
        return d;
    }

    
    private int getAny(){
        if (Any.getText().isEmpty() || Any.getText().equals(funknown)){
            return this.NULL_YEAR;
        }
        try{
            int a = Integer.parseInt(Any.getText());
            if (a<1000){
                System.out.println("L'any introduit no és vàlid. És menor que 1000.");
                return this.BAD_YEAR;
            }
            int year = Calendar.getInstance().get(Calendar.YEAR);
            if (a>year){
                System.out.println("L'any introduit no és vàlid. Correspon al futur.");
                return this.BAD_YEAR;
            }
            return a;
        }catch (Exception e){
            System.out.println("L'any introduit no és vàlid. No és un número.");
            return this.BAD_YEAR;
        }
    }
    
    public void iniciar(){
        this.Any.setText(funknown);
        this.Mes.setSelectedIndex(0);
        this.Dia.setSelectedIndex(0);
    }
    
    public void disable(){
        this.Any.setEnabled(false);
        this.Mes.setEnabled(false);
        this.Dia.setEnabled(false);
    }
    
    public void enable(){
        this.Any.setEnabled(true);
        this.Mes.setEnabled(true);
        this.Dia.setEnabled(true);
    }
    
    public boolean isEmpty(){
        return Mes.getSelectedIndex()==0 && Dia.getSelectedIndex()==0 && 
                isEmptyTF(Any);
    }
    
    private static javax.swing.ComboBoxModel getModelDia(){
        return new javax.swing.DefaultComboBoxModel(
            new String[] {funknown, "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", 
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});
    }
    
    private static javax.swing.ComboBoxModel getModelMes(){
        return new javax.swing.DefaultComboBoxModel(
            new String[] {funknown, "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12"});
    }
}
