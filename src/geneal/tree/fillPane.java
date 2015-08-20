/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import db.persona;
import geneal.sourceforms.formFitxa;
import java.awt.Dimension;
import java.awt.GridLayout;
import static java.lang.Math.min;
import javax.swing.JPanel;

/**
 *
 * @author jordi
 */
public class fillPane extends JPanel{
    private final java.awt.event.MouseListener ml;
    
    public fillPane(int maxW_, java.awt.event.MouseListener ml_){
        maxW = maxW_;
        ml = ml_;
        setEmpty();
    }
    
    public void update(persona[] ps){
        this.removeAll();
                
        this.setVisible(true);
        number = ps.length;
        int Width = min(maxW, number*width);
        boolean small = Width == maxW;
        
        this.setSize(new Dimension(Width, height));
        this.setPreferredSize(new Dimension(Width,  height));
        
        this.setLayout(new  GridLayout(1,number));
        
        if (ps.length==0){
            setEmpty();
        }else{
            for (persona  p : ps){
                nodeFitxa n = new nodeFitxa(small, f);
                this.add(n);
                n.update(p);
                n.addMouseListener(ml);
            }
        }
    }
    
    public persona getClickedPerson(JPanel panel){
        for (int i=0; i<this.getComponentCount(); i++){
            nodeFitxa n = (nodeFitxa) this.getComponent(i);
            if (n.contains(panel)){
                return n.getPerson();
            }
        }
        return new persona();
    }
    
    public void addFf(formFitxa f_){
        f  = f_;
    }
    
    public int getNumber(){
        return this.number;
    }
    
    public int  getHeight(){
        return this.height;
    }
    
    private int number;
    private final int maxW;
    private final int height = 120;
    private final int width = 120;
    private formFitxa f;

    public void setEmpty() {
        number = 1;
        boolean small = false;
        this.removeAll();
        this.setSize(width, height);
        this.setPreferredSize(new Dimension(width, height));
        this.add(new node(small));
        
        //this.setVisible(false);
    }
}
