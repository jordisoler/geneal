/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal;

import Exceptions.LEException;
import Exceptions.dateException;
import db.persona;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jordi
 */
public class ModifyPersonTaula extends ModifyPerson{
    private static taula t;
    private static int idx;
    
    public ModifyPersonTaula(persona p, taula t_,  int idx_) {
        super(p);
        t = t_;
        idx = idx_;
    }
    
    @Override
    public void end(){
        formPersona p = this.getFormPerson();
        p.add();
        try {
            db.persona per = p.getPerson();
            t.updateRow(per, idx);
        } catch (LEException | dateException ex) {
            Logger.getLogger(ModifyPersonTaula.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
    }
}
