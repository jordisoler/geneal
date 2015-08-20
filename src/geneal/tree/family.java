/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import db.persona;
import db.unio;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import static java.lang.Math.round;
import javax.swing.JPanel;

/**
 *
 * @author jordi
 */
public class family  extends JPanel{
    
    public family(int position_){
        position = position_;
        size s_;
        if (position < 4){
            s_ = size.SMALL;
        }else if (position == 6){
            s_ = size.BIG;
        }else{
            s_ = size.MEDIUM;
        }
        
        boolean small = s_ == size.SMALL;
        n1 = new node(small);
        n2 = new node(small);
        un = new unioNode();
        central = new JPanel();
        
        this.setLayout(new java.awt.GridLayout(1, 3));
        this.setSize(s_.width(), s_.height());
        this.setPreferredSize(new Dimension(s_.width(), s_.height()));
        
        if (s_ == size.BIG){
            //this.setBorder(new javax.swing.border.LineBorder(Color.gray, 2));
        }
        
        this.add(n1);
        
        central.setLayout(new java.awt.GridLayout(3, 3));
        for (int i = 0; i<9; i++){
            if (i==4){
                central.add(un);
            }else if (i==3 || i==5){
                central.add(new hLinePane());
            }else if (i==7){
                central.add(new vLinePane());
            }else{
                central.add(new JPanel());
            }
        }
        
        this.add(central);
        this.add(n2);
    }
    
    public void load(unio u){
        n1.update(u.getConjuge1());
        n2.update(u.getConjuge2());
        un.load(u);
    }
    public void load(persona p, boolean male){
        if(male){
            n1.update(p);
            n2.setEmpty();
        }else{
            n1.setEmpty();
            n2.update(p);
        }
        un.setEmpty();
    }

    public void setEmpty(){
        n1.setEmpty();
        n2.setEmpty();
        un.setEmpty();
    }
    
    @Override
    public void disable(){
        n1.setDark();
        n2.setDark();
    }
    
    public unio getUnio(){
        return un.getUnio();
    }
    
    @Override
    public void addMouseListener(MouseListener ma){
        n1.addMouseListener(ma);
        n2.addMouseListener(ma);
    }
    
    public node getNodeFromPanel(JPanel p){
        if (n1.contains(p)){
            return n1;
        }else if (n2.contains(p)){
            return n2;
        }else{
            return new node();
        }
    }
    
    public boolean contains(JPanel p){
        return n1.contains(p) || n2.contains(p);
    }
    
    private static class vLinePane extends JPanel {

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            
            int x1 = getWidth()/2;
            int x2 = x1;
            int y1 = getHeight();
            int y2 = 0;
                     
            g.drawLine(x1, y1, x2, y2);
        }
    }
    
    private class hLinePane extends JPanel {
        
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            
            int x1 = 0;
            int x2 = getWidth();
            int y1 = getHeight()/2;
            int y2 = y1;
                        
            g.drawLine(x1, y1, x2, y2);
        }
    }
        
    public enum size{
        SMALL (round((float) (preferredWidth*0.5)), (int) round(preferredHeight*0.8)),
        MEDIUM (round((float) (preferredWidth*0.9)), (int) round(preferredHeight*0.9)),
        BIG (preferredWidth, preferredHeight);
        
        private final int height;
        private final int width;
        size(int width_, int height_){
            width = width_;
            height = height_;
        }
        int width() {return width; }
        int height() {return height; }
    }
    
    private final int position;
    private final unioNode un;
    private final node n1, n2;
    private final JPanel central;
    public static final int preferredWidth = 420;
    public static final int preferredHeight = 120;
    public static final int conj1 = 1;
    public static final int conj2 = 2;
    public static final int cUnknown = 0;
}
