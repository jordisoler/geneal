/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import db.persona;
import java.awt.Component;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author jordi
 */
public class popupPersonaCerca extends popupPersona {
    public popupPersonaCerca(formFitxa f_, formLlista list_){
        super(f_);
        list = list_;
        Afegir = new JMenu();
        Afegir.setText("Afegir...");
        aFill = new JMenuItem();
        aFill.setText("Fill");
        aC1 = new JMenuItem();
        aC1.setText("Conjuge 1 (home)");
        aC2 = new JMenuItem();
        aC2.setText("Conjuge 2 (dona)");
        Afegir.add(aFill);
        Afegir.add(aC1);
        Afegir.add(aC2);
        this.add(Afegir);
    }
    
    @Override
    public void eliminarPersona(java.awt.event.ActionEvent evt){
        list.dropPerson();
    }
    
    @Override
    public void show(Component c, int x, int y, persona p_){
        System.out.println("Cerca ");
        super.show(c, x, y, p_);
    }
    
    private final formLlista list;
    private final JMenu Afegir;
    private final JMenuItem aFill;
    private final JMenuItem aC1;
    private final JMenuItem aC2;
    
}
