/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import db.persona;
import db.unio;
import geneal.sourceforms.formFitxa;
import geneal.tree.family.size;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author jordi
 */
public class tree extends JPanel{
    private final int Height;
    private final int Width;
    
    public tree(JPanel parent){
        super();
        families = new family[7];
               
        Width = parent.getWidth();
        Height = parent.getHeight();
        
        this.setPreferredSize(new Dimension(Width, Height));
        this.setBounds(0, 0, Width, Height);
        parent.setLayout(new  GridLayout(1,1));
        this.setLayout(new AbsoluteLayout());
        
        fills = new fillPane(Width);
        
        ss = new size[] {size.SMALL, size.SMALL, size.SMALL, size.SMALL, size.MEDIUM,
            size.MEDIUM, size.BIG};
        Xs = getXs();
        Ys  = getYs();
        for (int i =0; i<families.length; i++){
            families[i] = new family(i);
            this.add(families[i], new AbsoluteConstraints(Xs[i], Ys[i], -1, -1));
        }
        
        
        this.add(fills, new AbsoluteConstraints((Width-fills.getWidth())/2,Ys[7],-1,-1));
        
    }
    
    public void load(unio u){
        this.setLayout(new AbsoluteLayout());
        persona p1 = u.getConjuge1(), p2 = u.getConjuge2();
        unio u1 = p1.getUnioPares(), u2 = p2.getUnioPares();
        persona p11 = u1.getConjuge1(), p21 = u1.getConjuge2(),
                p12 = u2.getConjuge1(), p22 = u2.getConjuge2();
        unio u11 = p11.getUnioPares(), u21 = p21.getUnioPares(),
                u12 = p12.getUnioPares(), u22 = p22.getUnioPares();
        
        unio[] us = {u11, u21, u12,  u22, u1, u2, u};
        
        for (int i = 0; i<families.length;  i++){
            families[i].load(us[i]);
            if (i==4 || i == 5){
                if (us[i].isNull()){
                    int idx = (i-4)*2;
                    families[idx].disable();
                    families[idx+1].disable();
                }
            }
        }
        
        persona[] pfills = u.getFills();
        fills.update(pfills);
        fills.setLocation((Width-fills.getWidth())/2,Ys[7]);
    }
    
    public void reload(){
        load(families[6].getUnio());
    }
    
    public void setEmpty(){
        for (family fam : families){
            fam.setEmpty();
        }
        fills.setEmpty();
    }
    
    public void setFormFitxaHandler(formFitxa f_){
        f = f_;
        for (family familie : families) {
            familie.addFf(f);
        }
        fills.addFf(f);
    }
    
    public void newFitxaFromC1(persona p){
        Integer[] idxs = {0,1,4};
        boolean male = true;
        
        newFitxa(p, idxs, male);
    }
    
    public void newFitxaFromC2(persona p){
        Integer[] idxs = {2,3,5};
        boolean male = false;
        
        newFitxa(p, idxs, male);
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for (int i=0; i<6; i++){
            int x1 = families[i].getX()+ss[i].width()/2;
            int y1 = families[i].getY()+ss[i].height();
            int x2, y2, idx;
            if ( (i & 1) == 0 ) {
                idx = 4+(i/2);
                x2 = families[idx].getX()+ss[idx].width()/6;
                
            }else{
                idx = 3+((i+1)/2);
                x2 = families[idx].getX()+ss[idx].width()*5/6;
            }
            y2 = families[idx].getY();
            //System.out.println(y1);
            int[] points = {x1, y1, x2, y2};
            //g.drawLine(x1, y1, x2, y2);
            rectLine(points,g);
        }
        
        for (int i=0; i<fills.getNumber(); i++){
            linkFill(i, g);
        }
    }
    
    private void rectLine(int[] points, Graphics g){
        int mp = (points[1]+points[3])/2;
        g.drawLine(points[0], points[1], points[0], mp);
        g.drawLine(points[0], mp, points[2], mp);
        g.drawLine(points[2], mp, points[2], points[3]);
    }
    
    private int[] getYs(){
        int[] Ys_ = new int [8];
        int incy = Height/4, hbox = fills.getHeight();
        int offset = (incy-hbox)/2;
        for (int i=0; i<Ys_.length; i++){
            if(i<4){
                Ys_[i] = 0+offset;
            }else if (i<6){
                Ys_[i] = incy+offset;
            }else if (i==6){
                Ys_[i] = 2*incy+offset;
            }else{
                Ys_[i] = 3*incy+offset;
            }
        }
        return Ys_;
    }
    
    private int[] getXs(){
        int[] Xs_ = new  int [8];
        int incx, first;
        // First row
        incx = Width/4;
        first = (incx-size.SMALL.width())/2;
        for (int i = 0; i<4; i++){
            Xs_[i] = first+i*incx;
        }
        // Second row
        incx = Width/2;
        first = (incx-size.MEDIUM.width())/2;
        for (int i = 4; i<6; i++){
            Xs_[i] = first+(i-4)*incx;
        }
        //Third row
        first = (Width-size.BIG.width())/2;
        Xs_[6] = first;
        // 4th row
        Xs_[7] = 0;
        
        return Xs_;
    }
    
    private final size[] ss;
    private final int[] Xs, Ys;
    private formFitxa f;
    private final family[] families;
    private final fillPane fills;

    private void linkFill(int i, Graphics g) {
        int x1 = fills.getX()+fills.getWidth()/2, y1 = families[6].getY()+size.BIG.height();
        int incx = fills.getWidth()/fills.getNumber();
        int x2 = (Width-fills.getWidth())/2+incx/2+incx*i;
        int y2 = fills.getY();
        int[] points = {x1, y1, x2, y2};
        rectLine(points, g);
    }

    private void newFitxa(persona p, Integer[] idxs, boolean male) {
        unio pares = p.getUnioPares();
        unio[] unions = {pares.getConjuge1().getUnioPares(), pares.getConjuge2().getUnioPares(),pares};
        this.setLayout(new AbsoluteLayout());
        int c = 0;
        for (int i = 0; i<families.length; i++){
            if (Arrays.asList(idxs).contains(i)){
                families[i].load(unions[c]);
                c++;
            }else if (i==6){
                families[i].load(p, male);
            }else{
                families[i].setEmpty();
            }
        }
        fills.setEmpty();
        fills.setLocation((Width-fills.getWidth())/2,Ys[7]);
    }

    public void newFitxaFromFill(persona p) {
        setEmpty();
        persona [] pfills = {p};
        fills.update(pfills);
        fills.setLocation((Width-fills.getWidth())/2,Ys[7]);
    }
}
