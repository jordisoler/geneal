/*
 * Copyright (C) 2015 jordi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package geneal.sourceforms;

import Exceptions.*;
import db.naixement;
import db.persona;
import static geneal.sourceforms.formutils.fillText;
import static geneal.sourceforms.formutils.s2q;
import geneal.tree.tree;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class formFitxa {
    private formPersonaFitxa c1, c2;
    private javax.swing.JTextField fitxa;
    private javax.swing.JTextArea comentaris;
    private javax.swing.JCheckBox casament;
    private formData data;
    private formLloc lloc;
    private formLlista fills;
    private javax.swing.JLabel visorFitxa;
    private int estat, idfill;
    private tree t;
    
    private db.unio un;
    
    private final static int existent = 0;
    private final static int nousPares = 1;
    private final static int fromConjuge1 = 2;
    private final static int fromConjuge2 = 3;
    
    public static final boolean home = true;
    public static final boolean dona = false;
        
    public formFitxa(){
        un = new db.unio();
    }

    public formFitxa(db.unio un_,formPersonaFitxa c1_, formPersonaFitxa c2_, javax.swing.JTextField fitxa_,
            javax.swing.JTextArea comentaris_, javax.swing.JCheckBox casament_,
            formLlista fills_,formLloc lloc_, formData data_, javax.swing.JLabel visorFitxa_,
            tree t_){
        un = un_;
        c1 = c1_;
        c2 = c2_;
        fitxa = fitxa_;
        comentaris = comentaris_;
        casament = casament_;
        fills = fills_;
        lloc = lloc_;
        data = data_;
        visorFitxa = visorFitxa_;
        t = t_;
        t.setFormFitxaHandler(this);
    }
    public formFitxa(formPersonaFitxa c1_, formPersonaFitxa c2_, javax.swing.JTextField fitxa_,
            javax.swing.JTextArea comentaris_, javax.swing.JCheckBox casament_,
            formLlista fills_,formLloc lloc_, formData data_, javax.swing.JLabel visorFitxa_, tree t_){
        this(new db.unio(), c1_, c2_, fitxa_,  comentaris_, casament_, fills_,
                lloc_, data_, visorFitxa_, t_);
    }
    
    public void fill(){
        if(!un.isNull()){
            try {
                estat = existent;
                
                fitxa.setText(String.valueOf(un.getFitxa()));
                visorFitxa.setText("Fitxa: "+String.valueOf(un.getFitxa()));
                fillText(comentaris,String.valueOf(un.getComentaris()));
                db.persona[] pfills = un.getFills();
                fills.fillList(pfills);
                if(un.isMarriage()){
                    toggleCasament(true);
                    data.fill(un.getDataMatrimoni());
                    lloc.fill(un.getLlocMatrimoni());
                }else{
                    toggleCasament(false);
                    casament.setSelected(false);
                }
                db.persona p1 = un.getConjuge1(), p2 = un.getConjuge2();
            
                c1.fill(p1);
                c2.fill(p2);
                t.load(un);
                
            } catch (DBException ex) {
                Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
                ex.show();
            }
        }else{
            new GException("No s'ha pogut carregar la unió. (id null).","Problema de carrega").show();
        }
    }  
    public void fill(int id, boolean conj){
        setEmptyUnio();
        db.persona p = new db.persona(id);
        try {
            un = new db.unio();
            if (conj){
                p.setSexe("m");
                c1.fill(p);
                c2.setEmpty();
                estat = fromConjuge1;
                t.newFitxaFromC1(p);
            }else{
                p.setSexe("f");
                c1.setEmpty();
                c2.fill(p);
                estat = fromConjuge2;
                t.newFitxaFromC2(p);
            }
        } catch (DBException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            ex.show();
        }
    }
    public void fill(int id_fill){
        setEmpty();
        db.persona p = new db.persona(id_fill);
        db.persona[] ps = {p};
        fills.fillList(ps);
        estat = nousPares;
        un = new db.unio();
        c1.setEmpty(p.getLlinatge1());
        c2.setEmpty(p.getLlinatge2());
        idfill = id_fill;
        t.newFitxaFromFill(p);
    }
    public void fillFitxa(int ifitxa){
        un = db.unio.fromFitxa(ifitxa);
        fill();
    }
    public void fillUnio(int u){
        try {
            un = new db.unio(u);
        } catch (DBException ex) {
            un = new db.unio();
        }
        fill();
    }
    
    
    
    public void committ(){
        db.persona p1, p2;
        db.unio u;
        db.boda b;
        db.naixement n1, n2, n;
        switch (estat){
            case existent:
                try{
                    p1 = c1.getPerson();
                    p2 = c2.getPerson();
                    n1 = c1.getNaixement();
                    n2 = c2.getNaixement();
                    
                    u = getUnioFromData();
                    p1.addPersona();
                    p2.addPersona();
                    n1.addNaixement();
                    n2.addNaixement();
                    u.addUnio();
                    if (casament.isSelected()){
                        b = getBoda();
                        b.addBoda();
                    }else{
                        u.deleteBoda();
                    }
                    reload();
                    JOptionPane.showMessageDialog(null, "S'ha modificat fitxa existent. Fitxa: "+u.getFitxa(),
                    "Geneal - Fitxa modificada", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("S'ha modificat fitxa existent. Unió: "+u.getId());
                }catch (LEException |dateException e){
                    e.show();
                }
                break;
                
            case nousPares:
                try{
                    System.out.println("S'intentarà afegir els pares de la persona amb id: "+idfill+
                            ", "+new db.persona(idfill));
                    int id1,  id2;
                    p1 = c1.getPerson();
                    p2 = c2.getPerson();
                    
                    id1 = p1.addPersona();
                    id2 = p2.addPersona();
                    
                    System.out.println("S'han introduit els conjuges amb id: "+id1+
                            ", "+new db.persona(id1)+" i id: "+id2+
                            ", "+new db.persona(id2));

                    u = getUnioFromData();
                    u.setConjuge1(id1);
                    u.setConjuge2(id2);
                    u.addUnio();
                    System.out.println("S'ha introduit la unió:  "+u.getId());
                    

                    if(casament.isSelected()){
                        b = getBoda();
                        b.setUnio(u);
                        b.addBoda();
                    }

                    n = new db.naixement(idfill);
                    n.setFill(idfill);
                    n.setUnio(u);
                    n.addNaixement();
                    
                    // Partners' 'naixement'
                    n1 = c1.getNaixement();
                    n2 = c2.getNaixement();
                    n1.setFill(id1);
                    n2.setFill(id2);
                    n1.addNaixement();
                    n2.addNaixement();
                    
                    reload();
                    JOptionPane.showMessageDialog(null, "S'ha afegit una fitxa. Són els pares de: "
                            + new db.persona(idfill),
                    "Geneal - Fitxa nova", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("S'ha afegit el naixement del fill: "+new db.persona(idfill)+
                            " (id: "+idfill+") unió: "+u.getId());
                    System.out.println("S'ha afegit una fitxa. Són els pares de: "
                            + new db.persona(idfill)+". Unió: "+u.getId());
                }catch (GException e){
                    e.show();
                }
                break;
            
            case fromConjuge1:
                try{
                    int id1, id2;
                    p1 = c1.getPerson();
                    p2 = c2.getPerson();
                    id1 = p1.addPersona();
                    id2 = p2.addPersona();

                    u = getUnioFromData();
                    u.setConjuge1(id1);
                    u.setConjuge2(id2);
                    u.addUnio();

                    if(casament.isSelected()){
                        b = getBoda();
                        b.setUnio(u);
                        b.addBoda();
                    }
                    
                    reload();
                    JOptionPane.showMessageDialog(null, "S'ha afegit la  fitxa de "+p1+" i "+p2,
                    "Geneal - Fitxa nova", JOptionPane.INFORMATION_MESSAGE);
                    System.out.println("S'ha afegit la  fitxa de "+p1+" i "+p2);

                }catch (GException e){
                    e.show();
                }
                break;
            case fromConjuge2:
                try{
                    int id1, id2;
                    p1 = c1.getPerson();
                    p2 = c2.getPerson();
                    id1 = p1.addPersona();
                    id2 = p2.addPersona();

                    u = getUnioFromData();
                    u.setConjuge1(id1);
                    u.setConjuge2(id2);
                    u.addUnio();

                    if(casament.isSelected()){
                        b = getBoda();
                        b.setUnio(u);
                        b.addBoda();
                    }
                    System.out.println("S'ha afegit la  fitxa de "+p1+" i "+p2);

                }catch (GException e){
                    e.show();
                }
                break;
            default:
                System.err.println("ERROR: L'estat es desconegut: "+estat);
                
        }
    }
    
    public void deleteFill(){
        fills.dropPerson();
        reloadTree();
    }
       
    public db.unio getUnio(){
        return this.un;
    }
    
    public void setUnio(db.unio un_){
        this.un = un_;
    }
        
    private void setEmpty(){
        setEmptyUnio();
        c1.setEmpty();
        c2.setEmpty();
    }
    
    private void setEmptyUnio(){
        fitxa.setText("");
        comentaris.setText("");
        toggleCasament(false);
        db.persona[] ps = new db.persona[0];
        fills.fillList(ps);
    }
    
    private void toggleCasament(boolean on){
        casament.setSelected(on);
        if (on){
            lloc.enable();
            data.enable();
        }else{
            lloc.iniciar();
            data.iniciar();
            lloc.disable();
            data.disable();
        }
    }
    
    public void ActionPerformedCasament(){
        if (casament.isSelected()){
            lloc.enable();
            data.enable();
        }else{
            lloc.iniciar();
            data.iniciar();
            lloc.disable();
            data.disable();
        }
    }

    private boolean checkSexe(db.persona p) {
        try{
            switch(p.getSexe()){
                case "m":
                    return true;
                case "f":
                    return true;
                default:
                    return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }
    
    public void loadParesFromPerson(db.persona p){
        try {
            db.naixement n = new db.naixement(p.getId());
            un = new db.unio(n.getIdUnio());
            fill();
        } catch (DBException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            ex.show();
        }
    }
    
    private db.unio getUnioFromData(){
        db.unio u = new db.unio();
        if (db.unio.exist(un)){
            u = un;
        }
        try{
            try{
                u.setFitxa(Integer.parseInt(fitxa.getText()));
                u.setComentaris(s2q(comentaris.getText()));
                u.setConjuge1(c1.getId());
                u.setConjuge2(c2.getId());
            }catch (NumberFormatException e){
                if  (fitxa.getText().isEmpty()){
                    u.setFitxa(-1);
                }else{
                    throw new GException("El valor escrit a la fitxa és incorrecte.\n"
                        + "Canvia-ho abans de procedir", "Número incorrecte");
                }
            }
        }catch (GException e){
            e.show();
        }
        return u;
    }
    
    private db.boda getBoda(){
        db.boda b = un.getBoda();
        try {
            b.setData(data.getDate());
            b.setLloc(lloc.getLloc());
            b.setUnio(un);
        } catch (LEException |dateException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            ex.show();
        } catch (SQLException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }

    public void reload() {
        this.fill();
    }
    
    public void reloadTree(){
        t.reload();
    }
    
    public formPersonaFitxa getC1(){
        return this.c1;
    }
    public formPersonaFitxa getC2(){
        return this.c2;
    }

    public void addSonFromTree(persona p) {
        try {
            int reply = JOptionPane.showConfirmDialog(null, "Vols afegir "+p+" com a "
                    + "fill de "+c1.getPerson()+" i "+c2.getPerson()+"?","Unió nova",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(reply==JOptionPane.YES_OPTION){
                db.naixement n;
                if (naixement.exist(p.getId())){
                    n = new db.naixement(p.getId());
                    if(n.getIdUnio()!=-1){
                        new GException("La persona "+p+" ja consta com a fill/filla d'un matrimoni."
                                + " Fitxa: "+n.getUnio().getFitxa()).show();
                        return;
                    }
                }else{
                    n = new db.naixement();
                    n.setFill(p.getId());
                }
                n.setUnio(un);
                n.addNaixement();
                reload();
            }
        } catch (LEException | dateException | DBException ex) {
            ex.show();
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete() {
        int reply = JOptionPane.showConfirmDialog(null, "Segur que vols eliminar "
                + "la unio que estas vegent?\n \t Aixo no eliminara cap persona, "
                + "nomes les relacions entre elles.","Eliminar fitxa",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(reply==JOptionPane.YES_OPTION){
            un.delete();
            setEmpty();
        }
    }

    public boolean checkChanges() {
        boolean b = true;
        int nfitxa;
        if (!fitxa.getText().isEmpty()){
            nfitxa = Integer.parseInt(fitxa.getText());
        }else{
            nfitxa = -1;
        }
        
        try {
            b = c1.hasChanged() || c2.hasChanged() ||
                    nfitxa != un.getFitxa() ||
                    !lloc.getLloc().equals(un.getLlocMatrimoni()) ||
                    !data.getDate().equals(un.getDataMatrimoni()) ||
                    !comentaris.getText().equals(formutils.null2Void(un.getComentaris()));
        } catch (GException ex) {
            ex.show();
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (b){
            System.out.println("Hi ha hagut canvis en les persones");
            try {
                System.out.println("C1: "+c1.hasChanged()+", C2: "+c2.hasChanged()+
                        " fitxa: "+(nfitxa != un.getFitxa())+
                        ", Comentaris: "+!comentaris.getText().equals(formutils.null2Void(un.getComentaris()))+
                        ", lloc: "+(!lloc.getLloc().equals(un.getLlocMatrimoni()))+
                        ", data: "+(!data.getDate().equals(un.getDataMatrimoni())));
                System.out.println("Lloc: "+lloc.getLloc().getId()+", guardat: "+un.getLlocMatrimoni().getId());
            } catch (LEException | dateException ex) {
                ex.show();
                Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(formFitxa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            System.out.println("No hi ha hagut canvis");
        }
        return b;
    }

    public void newFitxaFromPersona(persona p) {
        int reply = JOptionPane.showConfirmDialog(null, "La persona "+p+" no "
                    + "està a cap unió. \nVols crear-ne una de nova?","Unió nova",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(reply==JOptionPane.YES_OPTION){  // Es vol introduir una persona sense unió
                
                if (checkSexe(p)){
                    if (p.getSexe().equals("m")){   // La persona té gènere
                        fill(p.getId(), home);
                    }else{
                        fill(p.getId(), dona);
                    }
                }else{                              // La persona no té gènere
                    String[] options = {"Home", "Dona", "Cancelar"};
                    int response = JOptionPane.showOptionDialog(null, "La persona "
                            +p+" no té sexe. \nElegeix-ne un abans de  continuar",
                            "Introduïr sexe", JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                    switch(response){
                        case 0:
                            fill(p.getId(), home);
                            break;
                        case 1:
                            fill(p.getId(), dona);
                            break;
                        default:
                            break;
                    }
                }
            }
    }
}
