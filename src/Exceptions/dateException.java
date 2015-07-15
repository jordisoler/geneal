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

/**
 *
 * @author jordi
 */
public class dateException extends GException{
    private static final String message = "Hi ha una data incorrecte.";
    private static final String type = "Data incorrecte";
    private static final int severity = MEDIUM;
    
    public dateException(){
        super(message,type,severity);
    }
    
    public dateException(String msg){
        super(msg+"\n Corregeix els errors abans de procedir.",type, severity);
    }
    
    public dateException(db.date d){
        super("La data "+d+" és incorrecte. Corregeix els errors.",type, severity);
    }
    
    
    public void setContext(String context){
        this.setMessage(this.getMessage()+"\n Fa referència a : "+context);
    }

}
