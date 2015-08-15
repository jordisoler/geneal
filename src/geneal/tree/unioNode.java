/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import db.unio;
import static geneal.config.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

/**
 *
 * @author jordi
 */
public class unioNode extends JPanel{
    
    public unioNode(){
        initComponents();
        setEmpty();
    }
    public unioNode(unio un_){
        initComponents();
        load(un_);
    }
    
    public void load(unio u_){
        un = u_;
        fitxa.setText(String.valueOf(un.getFitxa()));
        mi_boda.setText(un.getBoda().toString());
        mi_boda.setVisible(true);
    }
    
    public void setEmpty(){
        un = new unio();
        fitxa.setText("");
        mi_boda.setVisible(false);
    }
    
    private unio un;
    private JLabel fitxa, boda;
    private JPopupMenu popup;
    private JMenuItem mi_boda;

    private void initComponents() {
        popup = new JPopupMenu();
        mi_boda = new JMenuItem();
            
        fitxa = new JLabel();
        boda = new JLabel();
        
        this.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.setLayout(new java.awt.GridLayout(1, 1));
        this.setBackground(defaultColour);
        this.setSize(100, 50);
        fitxa.setFont(boldFont);
        fitxa.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(fitxa);
        
        //mi_boda.setEnabled(false);
        mi_boda.setFont(normalFont);
        mi_boda.setFocusable(false);
        popup.add(mi_boda);
        
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    popup.show(fitxa, getX()-getWidth(), getY());
                }
            }
        });
    }
}
