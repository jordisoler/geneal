/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

import geneal.nbforms.ModifyPerson;
import db.persona;

/**
 *
 * @author jordi
 */
public class ModifyPersonFitxa extends ModifyPerson {

    private final formFitxa f;
    
    public ModifyPersonFitxa(persona p, formFitxa f_) {
        super(p);
        f = f_;
    }
    
    @Override
    public void end(){
        formPersona p = this.getFormPerson();
        p.add();
        f.reload();
        this.dispose();
    }
    
}
