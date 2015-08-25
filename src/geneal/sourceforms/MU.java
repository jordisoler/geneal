/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import Exceptions.DBException;
import db.persona;
import db.unio;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.SwingConstants.CENTER;

/**
 *
 * @author jordi
 */
public class MU extends JFrame {
    
    public MU (persona p_){
        super();
        
        p = p_;
        
        this.setSize(600, 500);
        
        title  = new JLabel();
        pUnions = new JPanel();
        
        eh = new genealEventHandler();
        
        unio[] unions_ = p.getUnions();
        
        this.setLayout(new java.awt.GridLayout(2, 1));
        
        title.setText("<html><body><center>La persona "+p+"<br>pertany a vàries unions."
                + "<br>Elegeix la que vols carregar.</center></body></html>");
        title.setHorizontalAlignment(CENTER);
        this.add(title);
        
        pUnions.setLayout(new java.awt.GridLayout(1, unions_.length));
        for (final unio u : unions_){
            JButton un = getPanel(u);
            un.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e){
                    load(u);
                }
            });
            pUnions.add(un);
        }
        this.add(pUnions);
    }
    
    private final genealEventHandler eh;
    private final persona p;
    private final JPanel pUnions;
    private final JLabel title;

    private JButton getPanel(unio u) {
        JLabel fitxa = new JLabel();
        JLabel nom = new JLabel();
        JButton panel = new JButton();
        panel.setLayout(new java.awt.GridLayout(2, 1));
        
        fitxa.setText("Fitxa "+u.getFitxa());
        fitxa.setHorizontalAlignment(CENTER);
        panel.add(fitxa);
        try {
            nom.setText(u.getConjuge(p).toString());
        } catch (DBException ex) {
            nom.setText("Conjuge desconegut");
        }
        nom.setHorizontalAlignment(CENTER);
        panel.add(nom);
        return panel;
    }
    
    private void load(unio u){
        eh.load(u);
        this.dispose();
    }
}
