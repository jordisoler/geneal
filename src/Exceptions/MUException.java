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
public class MUException extends GException{
    private static final String message = "Una persona apareix a més de una unió.\n";
    private static final String type = "Multiples unions";
    private static final int severity = MEDIUM;
    
    public MUException(){
        super(message,type,severity);
    }
    
    public MUException(db.persona p){
        super("La persona "+p+" apareix a més de una unió.\n",type,severity);
    }
    
    public MUException(String msg){
        super(message+msg,type, severity);
    }
    
    public MUException(db.persona p, String msg){
        super("La persona "+p+" apareix a més de una unió.\n"+msg,type, severity);
    }
    
    public MUException(String msg, String context){
        super(message+msg+"\n Fa referència a : "+context,type, severity);
    }
    
    public void setContext(String context){
        this.setMessage(this.getMessage()+"\n Fa referència a : "+context);
    }

}
