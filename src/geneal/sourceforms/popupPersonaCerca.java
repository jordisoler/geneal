/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneal.sourceforms;

/**
 *
 * @author jordi
 */
public class popupPersonaCerca extends popupPersona {
    public popupPersonaCerca(formFitxa f_, formLlista list_){
        super(f_);
        list = list_;
    }
    
    @Override
    public void eliminarPersona(java.awt.event.ActionEvent evt){
        list.dropPerson();
    }
    
    
    private final formLlista list;
}
