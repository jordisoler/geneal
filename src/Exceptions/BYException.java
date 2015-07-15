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
public class BYException extends dateException{
    private static final String message = "Hi ha un any incorrecte.";
    private static final String messageFull = "Hi ha un any incorrecte corresponent a: \n \t ";
    
    public BYException(){
        super(message);
    }
    
    public BYException(String msg){
        super(messageFull+msg);
    }
    
//    public void setContext(String context){
//        this.setMessage(messageFull+context);
//    }
}
