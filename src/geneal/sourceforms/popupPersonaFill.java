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
public class popupPersonaFill extends popupPersona{
    public popupPersonaFill() {
        super();
    }
    
    @Override
    public void eliminarPersona(java.awt.event.ActionEvent evt){
        eh.deleteFill();
    }
}
