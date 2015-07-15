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

import Exceptions.DBException;
import db.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class Geneal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Hola, dw");
        try {
            unio u = new unio(30);
            System.out.println(u.getConjuge1());
        } catch (DBException ex) {
            Logger.getLogger(Geneal.class.getName()).log(Level.SEVERE, null, ex);
        }
        new App().setVisible(true);
        System.out.println("Benvingut a l'aplicació geneològica Geneal ");
    }
}
