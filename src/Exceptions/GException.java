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
package Exceptions;

import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class GException extends Exception {
    private final String title = "Geneal Error - ";
    private String type, Emessage;
    private int level;
    
    public static final int LOW = 0;
    public static final int MEDIUM = 1;
    public static final int HIGH = 2;
    public static final int FATAL = 3;
    
    public GException(){}
    
    public GException(String msg){
        super(msg);
        Emessage = msg;
    }
    
    public GException(String msg, int levelin){
        super(msg);
        Emessage = msg;
        level = levelin;
    }
    
    public GException(String msg, String t){
        super(msg);
        Emessage = msg;
        type = t;
    }
    
    public GException(String msg, String t, int levelin){
        super(msg);
        Emessage = msg;
        type = t;
        level = levelin;
    }
    
    public int getLevel(){
        return this.level;
    }
    
    public String getMessage(){
        return this.Emessage;
    }
    
    public void setMessage(String msg){
        this.Emessage = msg;
    }
    
    public void show(){
        JOptionPane.showMessageDialog(null, Emessage, title+type, JOptionPane.OK_OPTION);
    }
}
