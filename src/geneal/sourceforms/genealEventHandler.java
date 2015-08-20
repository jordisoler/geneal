/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import Exceptions.DBException;
import Exceptions.MUException;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class genealEventHandler {
    private static formFitxa f;
    private final static String warnChanges = "La fitxa actual ha estat "
                    + "modificada i té canvis  no guardats.\n\t Vols continuar i "
                    + "perdre els canvis?";
    private final static String[] changesOptions = {"Cancela", "Guarda els canvis", "Continua sense guardar"};
    
    
    public genealEventHandler(){};
    public genealEventHandler(formFitxa f_){
        f = f_;
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
            }else{
                f.loadParesFromPerson(p);
            }
        }else{
            c = f.getC2();
            p = new db.persona(c.getId());
            if (c.newPares()){
                f.fill(c.getId());
            }else{
                f.loadParesFromPerson(p);
            }
        }
        
    }
}
