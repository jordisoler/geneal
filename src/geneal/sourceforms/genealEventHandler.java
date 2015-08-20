/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import Exceptions.DBException;
import Exceptions.LEException;
import Exceptions.MUException;
import Exceptions.dateException;
import db.persona;
import geneal.nbforms.NewFill;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author jordi
 */
public class genealEventHandler {
    private static formFitxa f;
    private static JTabbedPane tPane;
    private static formLlista fills;
    private final static String warnChanges = "La fitxa actual ha estat "
                    + "modificada i té canvis  no guardats.\n\t Vols continuar i "
                    + "perdre els canvis?";
    private final static String[] changesOptions = {"Cancela", "Guarda els canvis", "Continua sense guardar"};
    
    
    public genealEventHandler(){};
    public genealEventHandler(formFitxa f_, JTabbedPane tPane_, formLlista fills_){
        f = f_;
        tPane = tPane_;
        fills = fills_;
    }
    
    public void setup(formFitxa f_){
        f = f_;
    }
    
    /**
     * Callback for click in a person.
     * @param p Person to be handled.
     */
    public void clickPerson(db.persona p){
        System.out.println("persona clicada: "+p);
        boolean b = f.checkChanges();
        if (b){
            int reply = JOptionPane.showOptionDialog(null, warnChanges, "Geneal - Canvis no  guardats",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
                    null, changesOptions, changesOptions[0]);
            if (reply == 0){
                return;
            }else if(reply == 1) {
                f.committ();
            }
        }
        try{                                    // Es vol introduir una  persona  amb unió
            f.setUnio(db.unio.fromConjuge(p.getId()));
            f.fill();
        }catch (MUException e){} catch (DBException ex) {
            f.newFitxaFromPersona(p);
        }
    }
    
    public void loadPares(boolean esHome){
        boolean b = f.checkChanges();
        if (b){
            int reply = JOptionPane.showOptionDialog(null, warnChanges, "Geneal - Canvis no  guardats",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, changesOptions, changesOptions[0]);
            if (reply == 0){
                return;
            }else if(reply == 1) {
                f.committ();
            }
        }
        db.persona p;
        formPersonaFitxa c;
        if (esHome){
            c = f.getC1();
            p = new db.persona(c.getId());
            if (c.newPares()){
                f.fill(c.getId());
                tPane.setSelectedIndex(0);
            }else{
                f.loadParesFromPerson(p);
            }
        }else{
            c = f.getC2();
            p = new db.persona(c.getId());
            if (c.newPares()){
                f.fill(c.getId());
                tPane.setSelectedIndex(0);
            }else{
                f.loadParesFromPerson(p);
            }
        }
    }
    
    public void loadNewPadrins(int padri){
        boolean b = f.checkChanges();
        if (b){
            int reply = JOptionPane.showOptionDialog(null, warnChanges, "Geneal - Canvis no  guardats",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, changesOptions, changesOptions[0]);
            if (reply == 0){
                return;
            }else if(reply == 1) {
                f.committ();
            }
        }
        db.persona p = new persona(), c1 = new persona(), c2 = new persona();
        try {
            c1 = f.getC1().getPerson();
            c2 = f.getC2().getPerson();
        } catch (LEException | dateException ex) {
            ex.show();
            Logger.getLogger(genealEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        switch (padri){
            case 0:
                p = c1.getPare();
                break;
            case 1:
                p = c2.getMare();
                break;
            case 2:
                p = c1.getPare();
                break;
            case 3:
                p = c2.getMare();
                break;
        }
        System.out.println("Padrí: "+padri+", pare de: "+p);
        f.fill(p.getId());
        tPane.setSelectedIndex(0);
    }
    
    public void reload(){
        f.reload();
    }

    public void addSonFromTree(persona p) {
        f.addSonFromTree(p);
    }
    
    public void replaceC1(persona p){
        f.getC1().replace(p);
    }
    public void replaceC2(persona p){
        f.getC2().replace(p);
    }

    void deleteFill() {
        f.deleteFill();
    }

    void reloadTree() {
        f.reloadTree();
    }

    public void newFill() {
        boolean b = f.checkChanges();
        if (b){
            int reply = JOptionPane.showOptionDialog(null, warnChanges, "Geneal - Canvis no  guardats",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, changesOptions, changesOptions[0]);
            if (reply == 0){
                return;
            }else if(reply == 1) {
                f.committ();
            }
        }
        new NewFill(fills,f.getUnio()).setVisible(true);
    }

}
