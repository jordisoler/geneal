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

import Exceptions.dateException;
import java.sql.Date;

/**
 *
 * @author jordi
 */
public class date {
    private int day;
    private int month;
    private int year;
    
    public date (){
        setNull();
    }
    
    public date (int y){
        this.year = y;
        this.month = 0;
        this.day = 0;
    }
    
    public date (int y, int m, int d) throws dateException{
        fillDate(y,m,d);
    }
    
    public date (String mysqld) throws dateException{
        if (mysqld == null){
            setNull();
        }else{
            int y = Integer.parseInt(mysqld.substring(0, 4));
            int m = Integer.parseInt(mysqld.substring(5, 7));
            int d = Integer.parseInt(mysqld.substring(8,10));
            fillDate(y,m,d);
        }
    }
    
    public date (java.sql.Date d) throws dateException{
        fillDate(d.getYear(),d.getMonth(), d.getDay());
    }
    
    public void setYear(int y){
        this.year = y;
    }
    
    public void setMonth(int m){
        this.month = m;
    }
    
    public void setDay(int d){
        this.day = d;
    }
    
    public int getYear(){
        return this.year;
    }
    
    public int getMonth(){
        return this.month;
    }
    
    public int getDay(){
        return this.day;
    }
    
    /**
     *
     * @param d2
     * @return Difference between dates.
     * [year-d2.year, month-d2.month, day-d2.day]
     */
    public int[] dateDiff(date d2){
        int [] result = new int [3];
        result[1] = this.day-d2.year;
        result[2] = this.month-d2.month;
        result[3] = this.day-d2.day;
        return result;
    }
    
    public String toString(){
        String d = "", m ="", y = "<desconegut>";
        if (this.year != -1){
            y = String.valueOf(this.year);
            if (this.month != 0){
                m = this.month+"-";
                if (this.day != 0){
                    d = this.day +"-";
                }
            }
        }else{
            return null;
        }
        return d+m+y;
    }
    
    public Date tojDate(){
        System.out.println("Dia: "+this.day+", mes: "+this.month+", any: "+this.year);
        if (this.isNull()){
            System.out.println("Dia: "+this.day+", mes: "+this.month+", any: "+this.year);
            return null;
        }else{
            System.out.println("Data en forma Query: "+ this.toQuery());
            //java.sql.Date sdate = new java.sql.Date(year, month, day);
            return java.sql.Date.valueOf(this.toQuery());
        }
    }
    
    public String toQuery(){
        if (this.year == -1){
            return null;
        }else{
            return this.year+"-"+formatDate(this.month)+"-"+formatDate(this.day);
        }
    }
    
    public boolean isNull(){
        return this.year == -1;
    }
    
    private String formatDate(int in){
        if (in == 0){
            return "00";
        }else{
            if (in<10){
                return "0"+in;
            }else{
                return String.valueOf(in);
            }
        }
    }
    
    private void fillDate(int y, int m, int d) throws dateException{
        if (checkMonthDay(m,d)){
            this.year = y;
            this.month = m;
            this.day = d;
        }else{
            throw new dateException(this);
        }
    }
    
    private static boolean checkMonth(int m){
        return m>=0 && m<=12;
    }
    
    private static boolean checkDay(int d){
        return d>=0 && d<=31;
    }
    
    public static boolean checkMonthDay(int m, int d){
        boolean result = false;
        int lim;
        if (checkMonth(m) && checkDay(d)){
            switch (m){
                case 2: lim = 29;
                    break;
                case 4: lim = 30;
                    break;
                case 6: lim = 30;
                    break;
                case 9: lim = 30;
                    break;
                case 11: lim = 30;
                    break;
                default: lim = 31;
                    break;
            }
            result = d<=lim;
        }
        return result;
    }
    
    private void setNull(){
        this.year = -1;
        this.month = 0;
        this.day  = 0;
    }
}
