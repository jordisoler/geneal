/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.tree;

/**
 *
 * @author jordi
 */
public class nodeFitxa extends node{

    public nodeFitxa(boolean small) {
        super(small);
    }
    
    @Override
    public String getLastValue(boolean comentaris){
        return super.getLastValue(comentaris, true);
    }
}
