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
public class LEException extends GException{
    private static final String message = "Hi ha un lloc incorrecte.\n";
    private static final String type = "Lloc incorrecte";
    private static final int severity = MEDIUM;
    
    public LEException(){
        super(message,type,severity);
    }
    
    public LEException(String msg){
        super(message+msg,type, severity);
    }
    
    public LEException(String msg, String context){
        super(message+msg+"\n Fa referència a : "+context,type, severity);
    }
    
    public void setContext(String context){
        this.setMessage(this.getMessage()+"\n Fa referència a : "+context);
    }

}
