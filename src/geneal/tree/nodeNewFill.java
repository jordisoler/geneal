/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import static geneal.config.darkGray;
import geneal.sourceforms.genealEventHandler;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author jordi
 */
public class nodeNewFill extends node{
    private final genealEventHandler eh;
    
    public nodeNewFill(){
        super();
        eh = new genealEventHandler();
        
        final MouseListener newFillListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2){
                    eh.newFill();
                }
            }
        };
        
        this.addMouseListener(newFillListener);
        this.setBackground(darkGray);
        this.setText("Afegir fill");
    }
}
