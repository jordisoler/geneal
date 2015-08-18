/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import Exceptions.DBException;
import Exceptions.GException;
import db.naixement;
import db.persona;
import db.unio;
import static geneal.config.*;
import geneal.sourceforms.formFitxa;
import static geneal.sourceforms.formutils.unknown;
import geneal.sourceforms.popupPersona;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author jordi
 */
public class node extends JPanel{
    
    public node (persona  p_, boolean small_){
        p = p_;
        small  = small_;
        initComponents();
    }
    public node (persona  p_){
        p = p_;
        small = false;
        initComponents();
    }
    public node (boolean small_){
        p = new persona();
        small = small_;
        initComponents();
    }
    public node (){
        p = new persona();
        small = false;
        initComponents();
    }
    public node (boolean small, formFitxa f_){
        this(small);
        f  = f_;
        pop = new popupPersona(f);
    }
    
    
    private void initComponents(){
        labels = new JLabel[4];
        scPane = new JScrollPane();
        content = new JPanel();
        f = new  formFitxa();
        
        this.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.setLayout(new java.awt.GridLayout(1, 1));
        this.setSize(140, 100);
        
        scPane.setViewportView(content);
        this.add(scPane);
        
        for (int i = 0; i<4; i++){
            labels[i] = new JLabel("");
        }
        
        isEnabled = true;
        final node n = this;
        scPane.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (isEnabled){
                    if (evt.getClickCount() == 2) {
                        unio u;
                        try {
                            u = unio.fromConjuge(p.getId());
                            int id = u.getId();
                            f.fillUnio(id);
                        } catch (GException ex) {
                            System.out.println("Unio desconeguda, de la persona "+p);
                            u = new unio();
                        }
                    }else if (SwingUtilities.isRightMouseButton(evt)){
                        pop.show(n, evt.getX(), evt.getY(), p);
                    }
                }
            }
        });
        
        content.setLayout(new java.awt.GridLayout(4, 1));
        for (int i=0; i<labels.length; i++){
            labels[i] = new JLabel();
            if (i==0){
                labels[i].setFont(boldFont);
            }else if(i==1 && small){
                labels[i].setFont(boldFont);
            }else {
                labels[i].setFont(normalFont);
            }
            content.add(labels[i]);
        }
        setBackgroundColour();
        
        load();
    }
    
    public void update(persona p_){
        p = p_;
        load();
    }
    
    public void addFf(formFitxa f_){
        f = f_;
        pop = new popupPersona(f);
    }
    
    private String[] getValues(){
        String nom, naixement = "", defuncio, comentaris;
        nom = p.toString();
        try {
            naixement = new naixement(p.getId()).toString();
        } catch (DBException ex) {}
        defuncio = p.defuncioToString();
        comentaris = getLastValue(true);
        String[] out = {nom, naixement, defuncio, comentaris};
        return out;
    }
    
    private void fillSmallNode(){
        labels[0].setText(p.getNom());
        labels[1].setText(p.getLlinatge1()+" "+p.getLlinatge2());
        labels[2].setText(String.valueOf(p.getLlocNaixement()));
        labels[3].setText(getLastValue(false));
       
        setBackgroundColour();
    }

    private void fillNode() {
        String[] values;
        values = getValues();
        for (int i=0; i<labels.length; i++){
            labels[i].setText(values[i]);
        }
        setBackgroundColour();
    }
    
    private persona p;
    private formFitxa f;
    private final boolean small;
    private JLabel[] labels;
    private JScrollPane scPane;
    private JPanel content;
    private popupPersona pop;
    private boolean isEnabled;

    public String getLastValue(boolean comentaris, boolean fitxa) {
        String def;
        if(comentaris){
            def = p.getComentaris();
        }else{
            def = "";
        }
        if (fitxa){
            try {
                labels[3].setFont(boldFont);
                unio u = unio.fromConjuge(p.getId());
                return "Fitxa: "+u.getFitxa();
            } catch (GException ex) { return "";}
        }else{
            return def;
        }
    }
    
    public String getLastValue(boolean comentaris){
        return this.getLastValue(comentaris, false);
    }

    private void setBackgroundColour() {
        String sexe;
        sexe = p.getSexe();
        try{
            switch(sexe){
                case "m":
                    content.setBackground(maleColour);
                    break;
                case "f":
                    content.setBackground(femaleColour);
                    break;
                default:
                    content.setBackground(defaultColour);
                    break;
            }
        }catch (NullPointerException e){
            content.setBackground(defaultColour);
        }
    }

    public void setEmpty() {
        for (JLabel l : labels){
            l.setText("");
        }
        labels[0].setText(unknown);
        content.setBackground(defaultColour);
    }
    
    public void setDark(){
        content.setBackground(darkGray);
        isEnabled = false;
    }

    private void load() {
        if (p.isNull()){
            setEmpty();
        }else{
            isEnabled = true;
            if (small){
                fillSmallNode();
            }else{
                fillNode();
            }
        }
    }
}
