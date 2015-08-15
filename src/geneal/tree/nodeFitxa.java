/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

import db.persona;
import geneal.sourceforms.formFitxa;

/**
 *
 * @author jordi
 */
public class nodeFitxa extends node{

    public nodeFitxa(boolean small, formFitxa f) {
        super(small, f);
    }
    
    @Override
    public String getLastValue(boolean comentaris){
        return super.getLastValue(comentaris, true);
    }
}
