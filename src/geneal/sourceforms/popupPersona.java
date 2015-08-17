/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import db.persona;
import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author jordi
 */
public class popupPersona extends JPopupMenu{
    
    public popupPersona(formFitxa f_){
        super();
        f = f_;
        p = new persona();
        elimina = new JMenuItem();
        modifica = new JMenuItem();
        
        elimina.setText("Eliminar");
        elimina.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarPersona(evt);
            }
        });
        this.add(elimina);
        
        modifica.setText("Modificar");
        
        modifica.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarPersona(evt);
            }
        });
        this.add(modifica);
    }
    
    public void show(Component c, int x, int y, persona p_){
        p = p_;
        System.out.println(p);
        this.show(c, x, y);
    }
    
    public void eliminarPersona(java.awt.event.ActionEvent evt){
        p.delete();
        f.reload();
    }
    
    public void modificarPersona(java.awt.event.ActionEvent evt){
        new ModifyPersonFitxa(p,f).setVisible(true);
    }
    
    public final formFitxa f;
    private persona p;
    private final JMenuItem elimina;
    private final JMenuItem modifica;
}
