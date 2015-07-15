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
package geneal;

import Exceptions.*;
import javax.swing.UIManager;
import db.unio;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.UnsupportedLookAndFeelException;
import static geneal.formutils.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author jordi
 */
public class App extends javax.swing.JFrame {

    private db.unio un; // Padrins paterns 25
    private int idc1, idc2, idfill;
    private ArrayList<Integer> lfills = new ArrayList<>(), lcerca = new ArrayList<>();
    
    private final static String newPares = "Afegir pares";
    private final static String newUnio = "Crear fitxa";
    /**
     * Creates new Geneal window
     */
    public App() {
        initialize();
        try {
            un = new db.unio(31);
            fillForm();
        } catch (DBException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(this.find_list.getSize());
    }
    
      
    public void loadFitxa(int f){
        if (db.unio.existFitxa(f)){
            un = db.unio.fromFitxa(f);
            try {
                fillForm();
            } catch (DBException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                ex.setMessage("Hi ha hagut problemes al carregar la fitxa "+f);
                ex.show();
            }
        }else{
            String msg = "S'ha intentat carregar una fitxa inexistent. ";
            if (f!=-1){
                msg = msg+"\n Fitxa: "+f;
            }
            try {
                throw new GException(msg);
            } catch (GException ex) {
                ex.show();
            }
        }
    }
    
    // Methods to fill people's lists
    private void fillList(javax.swing.JList l, db.persona[] ps, boolean fills){
        javax.swing.DefaultListModel<String> lm = new javax.swing.DefaultListModel<>();
        
        lfills = new ArrayList<>();
        for (db.persona p : ps){
            lm.addElement(formatList(p, fills));
        }
        l.setModel(lm);
    }
  
    private String formatList(db.persona p, boolean fills){
        String naixement = "", sdn = "", sdd="", p1="", p2="", p3="", sfitxa="";
        db.unio u = new unio();
        try {
            u = db.unio.fromConjuge(p.getId());
        } catch (MUException |DBException ex) {
            System.out.println("La persona "+p+" no té unió.");
            //Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (!u.isNull()){
            sfitxa="[Fitxa "+u.getFitxa()+"] ";
        }
        if (fills){
            lfills.add(p.getId());
        }else{
            lcerca.add(p.getId());
        }
        db.lloc n = p.getLlocNaixement();
        db.date dn = p.getDateNaixement();
        db.date dd = p.getDataDefuncio();
        if (!n.isNull()){
            naixement = ", "+n;
        }
        if (!dn.isNull() || !dd.isNull()){
            p1 = " (";
            p2 = "/";
            p3 = ")";
        }
        if (!dn.isNull()){
            sdn = dn.toString();
        }
        if (!dd.isNull()){
            sdd = dd.toString();
        }
        return sfitxa+p+naixement+p1+sdn+p2+sdd+p3;
    }
    
    // Callbacks lists' item clicked
    private void fillClicat(int idx){
        db.persona p = new db.persona(this.lfills.get(idx));
        clickLlista(p);
    }
    private void cercaFillClicat(int idx){
        db.persona p = new db.persona(lcerca.get(idx));
        clickLlista(p);
    }
    
    private void clickLlista(db.persona p){
        db.unio u;
        try{
            u=unio.fromConjuge(p.getId());
            int f = u.getFitxa();
            loadFitxa(f);
        }catch (MUException e){} catch (DBException ex) {     
            int reply = JOptionPane.showConfirmDialog(null, "La persona "+p+" no "
                    + "està a cap unió. \nVols crear-ne una de nova?","Unió nova",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply == JOptionPane.YES_OPTION){
                if (p.getSexe()!=null){
                    switch (p.getSexe()){
                        case "m":
                            try {
                                fillConjuge1(p);
                                fillConjuge2(new db.persona());
                                idc1 = p.getId();
                                idc2 = -1;
                            } catch (DBException ex1) {
                                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            break;
                        case "f":
                            try {
                                fillConjuge2(p);
                                fillConjuge1(new db.persona());
                                idc2 = p.getId();
                                idc1 = -1;
                            } catch (DBException ex1) {
                                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        default:
                            String[] options = {"Home", "Dona", "Cancelar"};
                            int response = JOptionPane.showOptionDialog(null, "La persona "
                                    +p+" no té sexe. \nElegeix-ne un abans de  continuar",
                                    "Introduïr sexe", JOptionPane.DEFAULT_OPTION, 
                                    JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                            switch (response){
                                case 0:
                                    try {
                                        p.setSexe("m");
                                        fillConjuge1(p);
                                        fillConjuge2(new db.persona());
                                        idc1 = p.getId();
                                        idc2 = -1;
                                    } catch (DBException ex1) {
                                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                                    }
                                    break;
                                case 1:
                                    try {
                                        p.setSexe("f");
                                        fillConjuge2(p);
                                        fillConjuge1(new db.persona());
                                        idc2 = p.getId();
                                        idc1 = -1;
                                    } catch (DBException ex1) {
                                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                                    }
                                    break;
                                case 2:
                                    return;
                            }
                    }
                }else{
                    String[] options = {"Home", "Dona", "Cancelar"};
                    int response = JOptionPane.showOptionDialog(null, "La persona "
                            +p+" no té sexe. \nElegeix-ne un abans de  continuar",
                            "Introduïr sexe", JOptionPane.DEFAULT_OPTION, 
                            JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
                    switch (response){
                        case 0:
                            try {
                                p.setSexe("m");
                                fillConjuge1(p);
                                fillConjuge2(new db.persona());
                                idc1 = p.getId();
                                idc2 = -1;
                            } catch (DBException ex1) {
                                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            break;
                        case 1:
                            try {
                                p.setSexe("f");
                                fillConjuge2(p);
                                fillConjuge1(new db.persona());
                                idc2 = p.getId();
                                idc1 = -1;
                            } catch (DBException ex1) {
                                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            break;
                        case 2:
                            return;
                    }
                }
                un = new unio();
                iniciarUnio();
                this.b_eafegir.setText("Afegir fitxa");
                this.b_eafegir.setEnabled(true);
                this.b_eborrar.setEnabled(false);
            }
        }
    }
    
    private int getRow(javax.swing.JList list, Point point){
	return list.locationToIndex(point);
    }
    
    // ComboBox Models
    final private javax.swing.ComboBoxModel cbsexe = getModelSexe();
    final private javax.swing.ComboBoxModel cbsexe2 = getModelSexe();
    
    
    private formLloc fmatrimoni, fnmarit, fdmarit, fnmuller,  fdmuller;
    private formData fdmatrimoni, fdnmarit, fddmarit, fdnmuller, fddmuller;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane6 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        popuplist = new javax.swing.JPopupMenu();
        Seleccionar = new javax.swing.JMenuItem();
        Eliminar = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        l_e_conjuge1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        t_e_1nom = new javax.swing.JTextField();
        t_e_1llinatge1 = new javax.swing.JTextField();
        t_e_1llinatge2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        c_1municipi = new javax.swing.JComboBox();
        c_1parroquia = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        c_1llogaret = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        c_1dia = new javax.swing.JComboBox();
        c_1mes = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        t_1any = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        c_1dmunicipi = new javax.swing.JComboBox();
        c_1dparroquia = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        c_1dllogaret = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        c_1ddia = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        c_1dmes = new javax.swing.JComboBox();
        t_1dany = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        c_1sexe = new javax.swing.JComboBox();
        b_1pares = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        t_1comentaris = new javax.swing.JTextArea();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jPanel7 = new javax.swing.JPanel();
        l_e_conjuge2 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        t_2nom = new javax.swing.JTextField();
        t_2llinatge1 = new javax.swing.JTextField();
        t_2llinatge2 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        c_2nmunicipi = new javax.swing.JComboBox();
        c_2nparroquia = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        c_2nllogaret = new javax.swing.JComboBox();
        jLabel28 = new javax.swing.JLabel();
        c_2ndia = new javax.swing.JComboBox();
        c_2nmes = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        t_2nany = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        c_2dmunicipi = new javax.swing.JComboBox();
        c_2dparroquia = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        c_2dllogaret = new javax.swing.JComboBox();
        jLabel36 = new javax.swing.JLabel();
        c_2ddia = new javax.swing.JComboBox();
        jLabel37 = new javax.swing.JLabel();
        c_2dmes = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        t_2dany = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        c_2sexe = new javax.swing.JComboBox();
        b_2pares = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        t_2comentaris = new javax.swing.JTextArea();
        jLabel40 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        c_casament = new javax.swing.JCheckBox();
        jLabel44 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        t_ucomentaris = new javax.swing.JTextArea();
        jLabel51 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        c_umunicipi = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        c_uparroquia = new javax.swing.JComboBox();
        jLabel45 = new javax.swing.JLabel();
        c_ullogaret = new javax.swing.JComboBox();
        jLabel46 = new javax.swing.JLabel();
        c_udia = new javax.swing.JComboBox();
        c_umes = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        c_uany = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        ll_ufills = new javax.swing.JList();
        b_afegirfill = new javax.swing.JButton();
        jLabel49 = new javax.swing.JLabel();
        t_fitxa = new javax.swing.JTextField();
        l_generacio = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        find_nom = new javax.swing.JTextField();
        find_llinatge1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        find_llinatge2 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        b_cerca = new javax.swing.JButton();
        jScrollPane17 = new javax.swing.JScrollPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        find_list = new javax.swing.JList();
        b_eafegir = new javax.swing.JButton();
        b_eborrar = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jScrollPane16 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        l_pare = new javax.swing.JLabel();
        l_ppare = new javax.swing.JLabel();
        l_pppare = new javax.swing.JLabel();
        l_mppare = new javax.swing.JLabel();
        l_mpare = new javax.swing.JLabel();
        l_pmpare = new javax.swing.JLabel();
        l_mmpare = new javax.swing.JLabel();
        l_mare = new javax.swing.JLabel();
        l_pmare = new javax.swing.JLabel();
        l_ppmare = new javax.swing.JLabel();
        l_mpmare = new javax.swing.JLabel();
        l_mmare = new javax.swing.JLabel();
        l_pmmare = new javax.swing.JLabel();
        l_mmmare = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList();
        jScrollPane12 = new javax.swing.JScrollPane();
        jList7 = new javax.swing.JList();
        jLabel59 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel64 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        jLabel84 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        jList8 = new javax.swing.JList();
        jScrollPane14 = new javax.swing.JScrollPane();
        jList9 = new javax.swing.JList();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jLabel87 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel101 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        jLabel107 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        jList3.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(jList3);

        Seleccionar.setText("Veure");
        popuplist.add(Seleccionar);

        Eliminar.setText("Eliminar");
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });
        popuplist.add(Eliminar);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Geneal");
        setResizable(false);

        jTabbedPane1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N

        jPanel6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        l_e_conjuge1.setFont(new java.awt.Font("Ubuntu", 1, 26)); // NOI18N
        l_e_conjuge1.setText("Conjuge1");
        jPanel6.add(l_e_conjuge1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jLabel2.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel2.setText("Nom");
        jPanel6.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 50, -1, -1));

        t_e_1nom.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(t_e_1nom, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 230, -1));

        t_e_1llinatge1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(t_e_1llinatge1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 260, -1));

        t_e_1llinatge2.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(t_e_1llinatge2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 310, -1));

        jLabel4.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel4.setText("Primer llinatge");
        jPanel6.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, -1, -1));

        jLabel5.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel5.setText("Segon llinatge");
        jPanel6.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

        jLabel6.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel6.setText("Naixement:");
        jPanel6.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 130, -1, -1));

        jLabel7.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel7.setText("Municipi");
        jPanel6.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 165, -1, -1));

        c_1municipi.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1municipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1municipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1municipiActionPerformed(evt);
            }
        });
        c_1municipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                c_1municipiFocusLost(evt);
            }
        });
        jPanel6.add(c_1municipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 192, 150, -1));

        c_1parroquia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1parroquia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1parroquia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1parroquiaActionPerformed(evt);
            }
        });
        jPanel6.add(c_1parroquia, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 165, -1));

        jLabel8.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel8.setText("Parroquia");
        jPanel6.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 94, -1));

        c_1llogaret.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1llogaret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1llogaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1llogaretActionPerformed(evt);
            }
        });
        jPanel6.add(c_1llogaret, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 190, 148, -1));

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel9.setText("Llogaret");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 160, 94, -1));

        c_1dia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(c_1dia, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 190, 90, -1));

        c_1mes.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1mes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1mesActionPerformed(evt);
            }
        });
        jPanel6.add(c_1mes, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 190, 100, -1));

        jLabel10.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel10.setText("Dia");
        jPanel6.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 160, 94, -1));

        jLabel11.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel11.setText("Mes");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 160, 94, -1));

        jLabel12.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel12.setText("Any");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 160, 94, -1));

        t_1any.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(t_1any, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 190, 120, -1));

        jLabel13.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel13.setText("Defunció");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 237, -1, -1));

        jLabel14.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel14.setText("Municipi");
        jPanel6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 272, -1, -1));

        c_1dmunicipi.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1dmunicipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1dmunicipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1dmunicipiActionPerformed(evt);
            }
        });
        c_1dmunicipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                c_1dmunicipiFocusLost(evt);
            }
        });
        jPanel6.add(c_1dmunicipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 299, 150, -1));

        c_1dparroquia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1dparroquia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1dparroquia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1dparroquiaActionPerformed(evt);
            }
        });
        jPanel6.add(c_1dparroquia, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 170, -1));

        jLabel15.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel15.setText("Parroquia");
        jPanel6.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 270, 94, -1));

        jLabel16.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel16.setText("Llogaret");
        jPanel6.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 94, -1));

        c_1dllogaret.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1dllogaret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_1dllogaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1dllogaretActionPerformed(evt);
            }
        });
        jPanel6.add(c_1dllogaret, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 300, 148, -1));

        jLabel17.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel17.setText("Dia");
        jPanel6.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 270, 94, -1));

        c_1ddia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(c_1ddia, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 300, 90, -1));

        jLabel18.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel18.setText("Mes");
        jPanel6.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 270, 94, -1));

        c_1dmes.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1dmes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1dmesActionPerformed(evt);
            }
        });
        jPanel6.add(c_1dmes, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 300, 100, -1));

        t_1dany.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel6.add(t_1dany, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 120, -1));

        jLabel19.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel19.setText("Any");
        jPanel6.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 270, 94, -1));

        jLabel21.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel21.setText("Sexe");
        jPanel6.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 344, -1, -1));

        c_1sexe.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_1sexe.setModel(cbsexe);
        c_1sexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_1sexeActionPerformed(evt);
            }
        });
        jPanel6.add(c_1sexe, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 379, 130, -1));

        b_1pares.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_1pares.setText("Fitxa pares");
        b_1pares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_1paresActionPerformed(evt);
            }
        });
        jPanel6.add(b_1pares, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 424, 160, -1));

        t_1comentaris.setColumns(20);
        t_1comentaris.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        t_1comentaris.setRows(5);
        jScrollPane1.setViewportView(t_1comentaris);

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 379, 670, 86));

        jLabel22.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel22.setText("Comentaris");
        jPanel6.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 344, -1, -1));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList1);

        jPanel6.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, -1, 100));

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList2);

        jPanel6.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, 550, 100));

        jPanel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        l_e_conjuge2.setFont(new java.awt.Font("Ubuntu", 1, 26)); // NOI18N
        l_e_conjuge2.setText("Conjuge1");
        jPanel7.add(l_e_conjuge2, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jLabel20.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel20.setText("Nom");
        jPanel7.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 50, -1, -1));

        t_2nom.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(t_2nom, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 230, -1));

        t_2llinatge1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(t_2llinatge1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 260, -1));

        t_2llinatge2.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(t_2llinatge2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 310, -1));

        jLabel23.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel23.setText("Primer llinatge");
        jPanel7.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 50, -1, -1));

        jLabel24.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel24.setText("Segon llinatge");
        jPanel7.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 50, -1, -1));

        jLabel25.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel25.setText("Naixement:");
        jPanel7.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 130, -1, -1));

        jLabel26.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel26.setText("Municipi");
        jPanel7.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 165, -1, -1));

        c_2nmunicipi.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2nmunicipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2nmunicipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2nmunicipiActionPerformed(evt);
            }
        });
        c_2nmunicipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                c_2nmunicipiFocusLost(evt);
            }
        });
        jPanel7.add(c_2nmunicipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 192, 150, -1));

        c_2nparroquia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2nparroquia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2nparroquia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2nparroquiaActionPerformed(evt);
            }
        });
        jPanel7.add(c_2nparroquia, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 165, -1));

        jLabel27.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel27.setText("Parroquia");
        jPanel7.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 94, -1));

        c_2nllogaret.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2nllogaret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2nllogaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2nllogaretActionPerformed(evt);
            }
        });
        jPanel7.add(c_2nllogaret, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 190, 148, -1));

        jLabel28.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel28.setText("Llogaret");
        jPanel7.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 160, 94, -1));

        c_2ndia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(c_2ndia, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 190, 90, -1));

        c_2nmes.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2nmes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2nmesActionPerformed(evt);
            }
        });
        jPanel7.add(c_2nmes, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 190, 100, -1));

        jLabel29.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel29.setText("Dia");
        jPanel7.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 160, 94, -1));

        jLabel30.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel30.setText("Mes");
        jPanel7.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 160, 94, -1));

        jLabel31.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel31.setText("Any");
        jPanel7.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 160, 94, -1));

        t_2nany.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(t_2nany, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 190, 120, -1));

        jLabel32.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel32.setText("Defunció");
        jPanel7.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 237, -1, -1));

        jLabel33.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel33.setText("Municipi");
        jPanel7.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 272, -1, -1));

        c_2dmunicipi.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2dmunicipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2dmunicipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2dmunicipiActionPerformed(evt);
            }
        });
        c_2dmunicipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                c_2dmunicipiFocusLost(evt);
            }
        });
        jPanel7.add(c_2dmunicipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 299, 150, -1));

        c_2dparroquia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2dparroquia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2dparroquia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2dparroquiaActionPerformed(evt);
            }
        });
        jPanel7.add(c_2dparroquia, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 300, 170, -1));

        jLabel34.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel34.setText("Parroquia");
        jPanel7.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 270, 94, -1));

        jLabel35.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel35.setText("Llogaret");
        jPanel7.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 270, 94, -1));

        c_2dllogaret.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2dllogaret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_2dllogaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2dllogaretActionPerformed(evt);
            }
        });
        jPanel7.add(c_2dllogaret, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 300, 148, -1));

        jLabel36.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel36.setText("Dia");
        jPanel7.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 270, 94, -1));

        c_2ddia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(c_2ddia, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 300, 90, -1));

        jLabel37.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel37.setText("Mes");
        jPanel7.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 270, 94, -1));

        c_2dmes.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2dmes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2dmesActionPerformed(evt);
            }
        });
        jPanel7.add(c_2dmes, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 300, 100, -1));

        jLabel38.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel38.setText("Any");
        jPanel7.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 270, 94, -1));

        t_2dany.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel7.add(t_2dany, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 300, 120, -1));

        jLabel39.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel39.setText("Sexe");
        jPanel7.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 344, -1, -1));

        c_2sexe.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_2sexe.setModel(cbsexe2);
        c_2sexe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_2sexeActionPerformed(evt);
            }
        });
        jPanel7.add(c_2sexe, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 379, 120, -1));

        b_2pares.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_2pares.setText("Fitxa pares");
        b_2pares.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_2paresActionPerformed(evt);
            }
        });
        jPanel7.add(b_2pares, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 424, 160, -1));

        t_2comentaris.setColumns(20);
        t_2comentaris.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        t_2comentaris.setRows(5);
        jScrollPane2.setViewportView(t_2comentaris);

        jPanel7.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 379, 670, 86));

        jLabel40.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel40.setText("Comentaris");
        jPanel7.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 344, -1, -1));

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel43.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel43.setText("Unió");
        jPanel3.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        c_casament.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_casament.setText("Casament");
        c_casament.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_casamentActionPerformed(evt);
            }
        });
        jPanel3.add(c_casament, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, -1, -1));

        jLabel44.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel44.setText("Comentaris");
        jPanel3.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        t_ucomentaris.setColumns(20);
        t_ucomentaris.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        t_ucomentaris.setRows(5);
        jScrollPane3.setViewportView(t_ucomentaris);

        jPanel3.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 700, 72));

        jLabel51.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel51.setText("Fills");
        jPanel3.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, -1, -1));

        jButton4.setText("Afegir fill");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1056, 333, -1, -1));

        jLabel41.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel41.setText("Municipi");
        jPanel3.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        c_umunicipi.setEditable(true);
        c_umunicipi.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_umunicipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_umunicipiActionPerformed(evt);
            }
        });
        c_umunicipi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                c_umunicipiFocusLost(evt);
            }
        });
        c_umunicipi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                c_umunicipiKeyPressed(evt);
            }
        });
        jPanel3.add(c_umunicipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 150, -1));

        jLabel42.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel42.setText("Parroquia");
        jPanel3.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 94, -1));

        c_uparroquia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_uparroquia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_uparroquiaActionPerformed(evt);
            }
        });
        jPanel3.add(c_uparroquia, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 165, -1));

        jLabel45.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel45.setText("Llogaret");
        jPanel3.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 94, -1));

        c_ullogaret.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_ullogaret.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_ullogaret.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_ullogaretActionPerformed(evt);
            }
        });
        jPanel3.add(c_ullogaret, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 148, -1));

        jLabel46.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel46.setText("Dia");
        jPanel3.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, 94, -1));

        c_udia.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel3.add(c_udia, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, 90, -1));

        c_umes.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        c_umes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_umesActionPerformed(evt);
            }
        });
        jPanel3.add(c_umes, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 120, 100, -1));

        jLabel47.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel47.setText("Mes");
        jPanel3.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 90, 94, -1));

        jLabel48.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel48.setText("Any");
        jPanel3.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 90, 94, -1));

        c_uany.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel3.add(c_uany, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 120, 110, -1));

        ll_ufills.setFont(new java.awt.Font("Ubuntu", 0, 22)); // NOI18N
        ll_ufills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ll_ufillsMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(ll_ufills);

        jPanel3.add(jScrollPane7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 700, 130));

        b_afegirfill.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_afegirfill.setText("Afegir fill");
        b_afegirfill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_afegirfillActionPerformed(evt);
            }
        });
        jPanel3.add(b_afegirfill, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 350, -1, -1));

        jLabel49.setFont(new java.awt.Font("Ubuntu", 0, 30)); // NOI18N
        jLabel49.setText("Fitxa");
        jPanel3.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        t_fitxa.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel3.add(t_fitxa, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 110, -1));

        l_generacio.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        l_generacio.setText("Generació");
        jPanel3.add(l_generacio, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 10, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cercador de persones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Ubuntu", 0, 18))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel52.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel52.setText("Nom");
        jPanel2.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 47, -1, -1));

        find_nom.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel2.add(find_nom, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 36, 280, -1));

        find_llinatge1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel2.add(find_llinatge1, new org.netbeans.lib.awtextra.AbsoluteConstraints(127, 88, 320, -1));

        jLabel53.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel53.setText("Llinatge 1");
        jPanel2.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 98, -1, -1));

        find_llinatge2.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jPanel2.add(find_llinatge2, new org.netbeans.lib.awtextra.AbsoluteConstraints(127, 145, 320, -1));

        jLabel54.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel54.setText("Llinatge 2");
        jPanel2.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 155, -1, -1));

        b_cerca.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_cerca.setText("Cercar");
        b_cerca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_cercaActionPerformed(evt);
            }
        });
        jPanel2.add(b_cerca, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 40, -1, -1));

        jScrollPane17.setMaximumSize(new java.awt.Dimension(440, 178));

        jScrollPane8.setMaximumSize(new java.awt.Dimension(440, 175));

        find_list.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        find_list.setMaximumSize(new java.awt.Dimension(435, 173));
        find_list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                find_listMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(find_list);

        jScrollPane17.setViewportView(jScrollPane8);

        jPanel2.add(jScrollPane17, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 202, 450, 177));

        b_eafegir.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_eafegir.setText("Afegir fitxa");
        b_eafegir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_eafegirActionPerformed(evt);
            }
        });

        b_eborrar.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        b_eborrar.setText("Esborrar");

        l_pare.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        l_pare.setText("jLabel1");

        l_ppare.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        l_ppare.setText("jLabel3");

        l_pppare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_pppare.setText("jLabel3");

        l_mppare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_mppare.setText("jLabel3");

        l_mpare.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        l_mpare.setText("jLabel3");

        l_pmpare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_pmpare.setText("jLabel3");

        l_mmpare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_mmpare.setText("jLabel3");

        l_mare.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        l_mare.setText("jLabel1");

        l_pmare.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        l_pmare.setText("jLabel3");

        l_ppmare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_ppmare.setText("jLabel3");

        l_mpmare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_mpmare.setText("jLabel3");

        l_mmare.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        l_mmare.setText("jLabel3");

        l_pmmare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_pmmare.setText("jLabel3");

        l_mmmare.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        l_mmmare.setText("jLabel3");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(l_pare)
                    .addComponent(l_mare)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(l_pmare)
                            .addComponent(l_mmare)
                            .addComponent(l_ppare)
                            .addComponent(l_mpare)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(l_mpmare)
                                    .addComponent(l_ppmare)
                                    .addComponent(l_pmmare)
                                    .addComponent(l_mmmare)
                                    .addComponent(l_mppare)
                                    .addComponent(l_pppare)
                                    .addComponent(l_pmpare)
                                    .addComponent(l_mmpare))))))
                .addContainerGap(299, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(l_pare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_ppare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_pppare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mppare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mpare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_pmpare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mmpare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(l_pmare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_ppmare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mpmare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mmare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_pmmare)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(l_mmmare)
                .addGap(5, 5, 5))
        );

        jScrollPane16.setViewportView(jPanel9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 870, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(b_eafegir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(b_eborrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(b_eafegir)
                    .addComponent(b_eborrar)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Editor", jPanel1);

        jLabel55.setFont(new java.awt.Font("Ubuntu", 0, 36)); // NOI18N
        jLabel55.setText("Fitxa");

        jLabel56.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel56.setText("Generació");

        jPanel10.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel58.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel58.setText("Naixement");
        jPanel10.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jList6.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane11.setViewportView(jList6);

        jPanel10.add(jScrollPane11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, -1, 100));

        jList7.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane12.setViewportView(jList7);

        jPanel10.add(jScrollPane12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, 550, 100));

        jLabel59.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel59.setText("Pares");
        jPanel10.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        jLabel61.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel61.setText("Defunció");
        jPanel10.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jButton7.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton7.setText("Fitxa pares");
        jPanel10.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, -1, -1));

        jLabel62.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel62.setText("Nom complet");
        jPanel10.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel65.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel65.setText("Nom complet");

        jLabel66.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel66.setText("Defunció");

        jLabel67.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel67.setText("Naixement");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65)
                    .addComponent(jLabel66)
                    .addComponent(jLabel67))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel66)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, -1, -1));

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel68.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel68.setText("Nom complet");

        jLabel69.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel69.setText("Defunció");

        jLabel70.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel70.setText("Naixement");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel68)
                    .addComponent(jLabel69)
                    .addComponent(jLabel70))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel69)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel74.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel74.setText("Nom complet");

        jLabel75.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel75.setText("Defunció");

        jLabel76.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel76.setText("Naixement");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel74)
                    .addComponent(jLabel75)
                    .addComponent(jLabel76))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel76)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel75)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, -1, -1));

        jLabel63.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel63.setText("Padrins paterns");
        jPanel10.add(jLabel63, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        jButton8.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton8.setText("Fitxa pares");
        jPanel10.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 280, -1, -1));

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel71.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel71.setText("Nom complet");

        jLabel72.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel72.setText("Defunció");

        jLabel73.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel73.setText("Naixement");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel71)
                    .addComponent(jLabel72)
                    .addComponent(jLabel73))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel73)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel72)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, -1));

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel77.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel77.setText("Nom complet");

        jLabel78.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel78.setText("Defunció");

        jLabel79.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel79.setText("Naixement");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel77)
                    .addComponent(jLabel78)
                    .addComponent(jLabel79))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel77)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel79)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel78)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel80.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel80.setText("Nom complet");

        jLabel81.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel81.setText("Defunció");

        jLabel82.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel82.setText("Naixement");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel80)
                    .addComponent(jLabel81)
                    .addComponent(jLabel82))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel80)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel82)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel81)
                .addGap(0, 0, 0))
        );

        jPanel10.add(jPanel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 450, -1, -1));

        jButton9.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton9.setText("Fitxa pares");
        jPanel10.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 410, -1, -1));

        jLabel64.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel64.setText("Padrins paterns");
        jPanel10.add(jLabel64, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, -1));

        jLabel60.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel60.setText("Conjuge1");
        jPanel10.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jPanel17.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel17.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel83.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel83.setText("Conjuge1");
        jPanel17.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 15, -1, -1));

        jLabel84.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel84.setText("Naixement");
        jPanel17.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, -1, -1));

        jList8.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane13.setViewportView(jList8);

        jPanel17.add(jScrollPane13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, -1, 100));

        jList9.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane14.setViewportView(jList9);

        jPanel17.add(jScrollPane14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -120, 550, 100));

        jLabel85.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel85.setText("Pares");
        jPanel17.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        jLabel86.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel86.setText("Defunció");
        jPanel17.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jButton10.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton10.setText("Fitxa pares");
        jPanel17.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, -1, -1));

        jLabel87.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel87.setText("Nom complet");
        jPanel17.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jPanel18.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel88.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel88.setText("Nom complet");

        jLabel89.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel89.setText("Defunció");

        jLabel90.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel90.setText("Naixement");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel88)
                    .addComponent(jLabel89)
                    .addComponent(jLabel90))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel88)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel90)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel89)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, -1, -1));

        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel91.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel91.setText("Nom complet");

        jLabel92.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel92.setText("Defunció");

        jLabel93.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel93.setText("Naixement");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel91)
                    .addComponent(jLabel92)
                    .addComponent(jLabel93))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel91)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel93)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel92)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, -1, -1));

        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel94.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel94.setText("Nom complet");

        jLabel95.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel95.setText("Defunció");

        jLabel96.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel96.setText("Naixement");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel94)
                    .addComponent(jLabel95)
                    .addComponent(jLabel96))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel94)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel96)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel95)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 320, -1, -1));

        jLabel97.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel97.setText("Padrins paterns");
        jPanel17.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        jButton11.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton11.setText("Fitxa pares");
        jPanel17.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 280, -1, -1));

        jPanel21.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel98.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel98.setText("Nom complet");

        jLabel99.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel99.setText("Defunció");

        jLabel100.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel100.setText("Naixement");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel98)
                    .addComponent(jLabel99)
                    .addComponent(jLabel100))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel100)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel99)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, -1, -1));

        jPanel22.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel101.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel101.setText("Nom complet");

        jLabel102.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel102.setText("Defunció");

        jLabel103.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel103.setText("Naixement");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel101)
                    .addComponent(jLabel102)
                    .addComponent(jLabel103))
                .addGap(0, 279, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jLabel101)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel103)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel102)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jPanel23.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel104.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel104.setText("Nom complet");

        jLabel105.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel105.setText("Defunció");

        jLabel106.setFont(new java.awt.Font("Ubuntu", 0, 20)); // NOI18N
        jLabel106.setText("Naixement");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel104)
                    .addComponent(jLabel105)
                    .addComponent(jLabel106))
                .addGap(0, 283, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jLabel104)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel106)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel105)
                .addGap(0, 0, 0))
        );

        jPanel17.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 450, -1, -1));

        jButton12.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jButton12.setText("Fitxa pares");
        jPanel17.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 410, -1, -1));

        jLabel107.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel107.setText("Padrins paterns");
        jPanel17.add(jLabel107, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 420, -1, -1));

        jPanel24.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel57.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jLabel57.setText("Fills");

        jScrollPane10.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N

        jTable1.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setRowHeight(27);
        jScrollPane10.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 844, Short.MAX_VALUE)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel57)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane15.setViewportView(jTextArea4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                            .addComponent(jScrollPane15)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55)
                            .addComponent(jLabel56))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Visor", jPanel5);

        fileMenu.setMnemonic('f');
        fileMenu.setText("Fitxa");

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Obrir fitxa");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Surt");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void c_1sexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1sexeActionPerformed
        this.l_e_conjuge1.setText(String.valueOf(this.c_1sexe.getSelectedItem()));
    }//GEN-LAST:event_c_1sexeActionPerformed

    private void b_1paresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_1paresActionPerformed
        paresClicked(true);
    }//GEN-LAST:event_b_1paresActionPerformed

    private void c_1mesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1mesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_1mesActionPerformed

    private void c_1dparroquiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1dparroquiaActionPerformed
        this.fdmarit.selectedParroquia();
    }//GEN-LAST:event_c_1dparroquiaActionPerformed

    private void c_1dmesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1dmesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_1dmesActionPerformed

    private void c_2nmesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2nmesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_2nmesActionPerformed

    private void c_2dparroquiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2dparroquiaActionPerformed
        this.fdmuller.selectedParroquia();
    }//GEN-LAST:event_c_2dparroquiaActionPerformed

    private void c_2dmesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2dmesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_2dmesActionPerformed

    private void c_2sexeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2sexeActionPerformed
        this.l_e_conjuge2.setText(String.valueOf(this.c_2sexe.getSelectedItem()));
    }//GEN-LAST:event_c_2sexeActionPerformed

    private void b_2paresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_2paresActionPerformed
        paresClicked(false);
    }//GEN-LAST:event_b_2paresActionPerformed

    private void c_umesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_umesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_umesActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void b_eafegirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_eafegirActionPerformed
        int l = 5;
        String sc1 = "conjuge 1 ("+this.l_e_conjuge1.getText()+")", 
                sc2 = "conjuge 2 ("+this.l_e_conjuge2.getText()+")";
        String[] referencia = {"matrimoni", "naixement "+sc1,
            "naixement "+sc2, "defunció "+sc1, "defunció "+sc2};
        
        db.lloc[] llocs = new db.lloc [l];
        db.date [] dates = new db.date [l];
        formLloc[] fllocs = {this.fmatrimoni, this.fnmarit, this.fnmuller, 
            this.fdmarit, this.fdmuller};
        formData[] fdates = {this.fdmatrimoni, this.fdnmarit, this.fdnmuller,
            this.fddmarit, this.fddmuller};
        javax.swing.JComboBox[] fsexes = {this.c_1sexe,  this.c_2sexe};
        
        // Aconseguir tots els llocs i dates necessaris
        for (int i = 0; i<l; i++){
            try {
                llocs[i] = fllocs[i].getLloc();
                dates[i] = fdates[i].getDate();
                System.out.println(dates[i]);
            } catch (dateException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                ex.setContext(referencia[i]);
                ex.show();
                dates[i] = new db.date();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                return;
            } catch (LEException ex) {
                ex.show();
                llocs[i] = new db.lloc();
            }
        }
        
        // Confirmar introducció de persona sense sexe
        javax.swing.JTextField[] ts = {this.t_e_1nom, this.t_2nom};
        for(int i = 0; i<2; i++){
            if (fsexes[i].getSelectedIndex() == 0 && !ts[i].getText().isEmpty() ){
                int r = JOptionPane.showConfirmDialog(null, "La persona que vols "
                        + "introduir (Conjuge "+i+") no té sexe. \n Vols introduir-la igualment?", 
                        "Sexe no especificat", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (r != JOptionPane.YES_OPTION){
                    JOptionPane.showMessageDialog(null, "Corregeix l'error i "
                            + "tora-ho  a provar.", "Corregeig l'error", 
                            JOptionPane.OK_OPTION);
                    return;
                }
            }
        }
        
        // Modificar fitxa
        if (unio.exist(un)){
            db.boda boda = new db.boda(un.getId());
            db.persona p1 = new db.persona(), p2 = new db.persona(), 
                    c1 = un.getConjuge1(), c2 = un.getConjuge2();
            db.naixement n1 = new db.naixement(), n2 = new db.naixement(), 
                    n3 = new db.naixement(), n4 = new db.naixement();
            javax.swing.JTextField[] tf = {this.t_e_1nom,  this.t_2nom, this.t_e_1llinatge1,
                this.t_2llinatge1, this.t_e_1llinatge2,  this.t_2llinatge2};
            javax.swing.JTextArea[] comentaris = {this.t_1comentaris,  this.t_2comentaris};
        
            try {
                n3 = new db.naixement(c1.getId());
            } catch (DBException ex) {}
            try {
                n4 = new db.naixement(c2.getId());
            } catch (DBException ex) {}
            db.persona[] persones= {p1, p2}, p_original = {c1, c2};
            db.naixement[] ns = {n1, n2}, n_original = {n3, n4};
            
            for (int i = 0; i<2; i++){
                ns[i].setFill(p_original[i].getId());
                ns[i].setDate(dates[i+1]);
                ns[i].setLloc(llocs[i+1]);
                ns[i].setUnio(n_original[i].getIdUnio());
                if (!ns[i].equals(n_original[i])){
                    ns[i].addNaixement();
                }
                persones[i].setId(p_original[i].getId());
                persones[i].setNom(tf[i].getText());
                persones[i].setLlinatge1(tf[i+2].getText());
                persones[i].setLlinatge2(tf[i+4].getText());
                persones[i].setLlocDefuncio(llocs[i+3].getId());
                persones[i].setDataDefuncio(dates[i+3]);
                persones[i].setComentaris(comentaris[i].getText());
                persones[i].setSexe(getSexe(fsexes[i]));
                if (!persones[i].equals(p_original[i])){
                    persones[i].addPersona();
                }                
            }
            db.unio u = new db.unio();
            u.setId(un.getId());
            u.setConjuge1(c1);
            u.setConjuge2(c2);
            try{
                u.setFitxa(Integer.parseInt(this.t_fitxa.getText()));
            }catch (Exception e){
                new GException("Número de fitxa incorrecte.\n Canvia-ho abans de "
                        + "procedir.","Fitxa incorrecte").show();
                return;
            }
            u.setComentaris(this.t_ucomentaris.getText());
            if (!u.equals(un)){
                u.addUnio();
                un = u;
            }
            boda.setUnio(un);
            boda.setData(dates[0]);
            boda.setLloc(llocs[0]);
            if (!boda.equals(un.getBoda())){
                boda.addBoda();
            }
        }else{  // Introduir fitxa nova
            if ()
        }
    }//GEN-LAST:event_b_eafegirActionPerformed

    private void c_umunicipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_umunicipiActionPerformed
        this.fmatrimoni.selectedMunicipi();
    }//GEN-LAST:event_c_umunicipiActionPerformed

    private void c_umunicipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_c_umunicipiFocusLost
        this.fmatrimoni.selectedMunicipi();
    }//GEN-LAST:event_c_umunicipiFocusLost

    private void c_1municipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_c_1municipiFocusLost
        this.fnmarit.selectedMunicipi();
    }//GEN-LAST:event_c_1municipiFocusLost

    private void c_1dmunicipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_c_1dmunicipiFocusLost
        this.fdmarit.selectedMunicipi();
    }//GEN-LAST:event_c_1dmunicipiFocusLost

    private void c_2nmunicipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_c_2nmunicipiFocusLost
        this.fnmuller.selectedMunicipi();
    }//GEN-LAST:event_c_2nmunicipiFocusLost

    private void c_2dmunicipiFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_c_2dmunicipiFocusLost
        this.fdmuller.selectedMunicipi();
    }//GEN-LAST:event_c_2dmunicipiFocusLost

    private void c_umunicipiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_c_umunicipiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_umunicipiKeyPressed

    private void c_1municipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1municipiActionPerformed
        this.fnmarit.selectedMunicipi();
    }//GEN-LAST:event_c_1municipiActionPerformed

    private void c_2nmunicipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2nmunicipiActionPerformed
        this.fnmuller.selectedMunicipi();
    }//GEN-LAST:event_c_2nmunicipiActionPerformed

    private void c_2dmunicipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2dmunicipiActionPerformed
        this.fdmuller.selectedMunicipi();
    }//GEN-LAST:event_c_2dmunicipiActionPerformed

    private void b_cercaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_cercaActionPerformed
        fillFind();
    }//GEN-LAST:event_b_cercaActionPerformed

    private void b_afegirfillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_afegirfillActionPerformed
        new NewFill(this,un).setVisible(true);
    }//GEN-LAST:event_b_afegirfillActionPerformed

    private void c_1dmunicipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1dmunicipiActionPerformed
        this.fdmarit.selectedMunicipi();
    }//GEN-LAST:event_c_1dmunicipiActionPerformed

    private void c_uparroquiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_uparroquiaActionPerformed
        this.fmatrimoni.selectedParroquia();
    }//GEN-LAST:event_c_uparroquiaActionPerformed

    private void c_ullogaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_ullogaretActionPerformed
        this.fmatrimoni.selectedLlogaret();
    }//GEN-LAST:event_c_ullogaretActionPerformed

    private void c_1parroquiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1parroquiaActionPerformed
        this.fnmarit.selectedParroquia();
    }//GEN-LAST:event_c_1parroquiaActionPerformed

    private void c_1llogaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1llogaretActionPerformed
        this.fnmarit.selectedLlogaret();
    }//GEN-LAST:event_c_1llogaretActionPerformed

    private void c_1dllogaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_1dllogaretActionPerformed
        this.fdmarit.selectedLlogaret();
    }//GEN-LAST:event_c_1dllogaretActionPerformed

    private void c_2nparroquiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2nparroquiaActionPerformed
        this.fnmuller.selectedParroquia();
    }//GEN-LAST:event_c_2nparroquiaActionPerformed

    private void c_2nllogaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2nllogaretActionPerformed
        this.fnmuller.selectedLlogaret();
    }//GEN-LAST:event_c_2nllogaretActionPerformed

    private void c_2dllogaretActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_2dllogaretActionPerformed
        this.fdmuller.selectedLlogaret();
    }//GEN-LAST:event_c_2dllogaretActionPerformed

    private void find_listMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_find_listMouseClicked
        if ( SwingUtilities.isRightMouseButton(evt)){
            this.find_list.setSelectedIndex(getRow(this.find_list, evt.getPoint()));
            popuplist.show(this.find_list, evt.getX(), evt.getY());
            popuplist.setLabel("Cerca");
        }
    }//GEN-LAST:event_find_listMouseClicked

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        if(null != popuplist.getLabel())switch (popuplist.getLabel()) {
            case "Cerca":{
                db.persona p = new db.persona(this.lcerca.get(this.find_list.getSelectedIndex()));
                p.delete();
                fillFind();
                break;
                }
            case "Fills":{
                db.persona p = new db.persona(this.lfills.get(this.ll_ufills.getSelectedIndex()));
                p.delete();
                db.persona[] fills = un.getFills();
                fillList(this.ll_ufills,fills, true);
                break;
            }
        }
    }//GEN-LAST:event_EliminarActionPerformed

    private void ll_ufillsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ll_ufillsMouseClicked
        if ( SwingUtilities.isRightMouseButton(evt)){
            this.ll_ufills.setSelectedIndex(getRow(this.ll_ufills, evt.getPoint()));
            popuplist.show(this.ll_ufills, evt.getX(), evt.getY());
            popuplist.setLabel("Fills");
        }
    }//GEN-LAST:event_ll_ufillsMouseClicked

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        String sf = JOptionPane.showInputDialog(null, "Número de fitxa a carregar",
                "Carregar fitxa", JOptionPane.OK_CANCEL_OPTION);
        try{
            int f = Integer.parseInt(sf);
            if(!db.unio.existFitxa(f)){
                throw new DBException();
            }else{
                loadFitxa(f);
            }
        }catch (NumberFormatException | DBException e){
            new GException("La fitxa introduida no és correcte.", "Fitxa incorrete").show();
        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void c_casamentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_casamentActionPerformed
        if (this.c_casament.isSelected()){
            this.fdmatrimoni.enable();
            this.fmatrimoni.enable();
        }else{
            this.fmatrimoni.iniciar();
            this.fdmatrimoni.iniciar();
            this.fmatrimoni.disable();
            this.fdmatrimoni.disable();
        }
    }//GEN-LAST:event_c_casamentActionPerformed
    
    
    private void initialize(){
        try { 
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); 
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException ex) {}
        initComponents();
        
        this.setVisible(true);
        
        ll_ufills.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                javax.swing.JList list = (javax.swing.JList)evt.getSource();
                if (evt.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    fillClicat(index);
                } else if (evt.getClickCount() == 3) {

                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                }
            }
        });
        
        this.find_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                javax.swing.JList list = (javax.swing.JList)evt.getSource();
                if (evt.getClickCount() == 2) {

                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    cercaFillClicat(index);
                } else if (evt.getClickCount() == 3) {

                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                }
            }
        });
        

        fmatrimoni = new formLloc(this.c_umunicipi, this.c_uparroquia,  this.c_ullogaret);
        fnmarit = new formLloc(this.c_1municipi, this.c_1parroquia,  this.c_1llogaret);
        fdmarit = new formLloc(this.c_1dmunicipi, this.c_1dparroquia,  this.c_1dllogaret);
        fnmuller = new formLloc(this.c_2nmunicipi, this.c_2nparroquia,  this.c_2nllogaret);
        fdmuller = new formLloc(this.c_2dmunicipi, this.c_2dparroquia,  this.c_2dllogaret);

        fdmatrimoni = new formData(this.c_udia, this.c_umes, this.c_uany);
        fdnmarit = new formData(this.c_1dia, this.c_1mes, this.t_1any);
        fddmarit = new formData(this.c_1ddia, this.c_1dmes, this.t_1dany);
        fdnmuller = new formData(this.c_2ndia, this.c_2nmes, this.t_2nany);
        fddmuller = new formData(this.c_2ddia, this.c_2dmes, this.t_2dany);
        
    }
    
    private void newParesForm(boolean c1) {
        try {
            db.persona p = new db.persona();
            fillConjuge1(p);
            fillConjuge2(p);
            iniciarUnio();
            if (c1){
                p = new db.persona(idc1);
            }else{
                p = new db.persona(idc2);
            }
            System.out.println("Afegint pares de: "+p+", id: "+p.getId());
            db.persona[] ps = {p};
            fillList(this.ll_ufills,ps,true);
            this.b_eafegir.setText(newUnio);
            
        } catch (DBException ex) {
            ex.show();
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void fillForm() throws DBException {
        if (!un.isNull()){
            idc1 = -1;
            idc2 = -1;
            //this.l_generacio.setText("Generació "+un.getConjuge1().getGenerationsString());
            if (un.isMarriage()){
                this.c_casament.setSelected(true);
                this.fmatrimoni.fill(un.getLlocMatrimoni());
                fdmatrimoni.fill(un.getDataMatrimoni());
            }else{
                this.c_casament.setSelected(false);
                this.c_umunicipi.setEnabled(false);
                this.c_uparroquia.setEnabled(false);
                this.c_ullogaret.setEnabled(false);
                this.c_udia.setEnabled(false);
                this.c_umes.setEnabled(false);
                this.c_uany.setEnabled(false);
            }
            fillText(this.t_fitxa,un.getFitxa());
            fillText(this.t_ucomentaris, un.getComentaris());
            db.persona[] fills = un.getFills();
            fillList(this.ll_ufills,fills, true);

            db.persona c1 = un.getConjuge1();
            db.persona c2 = un.getConjuge2();
            
            fillConjuge1(c1);
            fillConjuge2(c2);
            
        }else{
            if(idc1 != 0){
                db.persona p = new db.persona(idc1);
                fillConjuge1(p);
                fillConjuge2(new db.persona());
            }else if (idc2 != 0){
                db.persona p = new db.persona(idc2);
                fillConjuge1(new db.persona());
                fillConjuge2(p);
            }else{
                throw new DBException("S'ha produit un error en la carrega de dades.");
            }
        }
        this.b_eafegir.setText("Modificar");
        this.b_eafegir.setEnabled(true);
        this.b_eborrar.setEnabled(true);
    }
    
    
    public void updateFills(){
        db.persona[] fills = un.getFills();
        fillList(this.ll_ufills,fills, true);
    }
    
    private void fillConjuge1(db.persona c1) throws DBException{
        if (c1.isNull()){
            javax.swing.JTextField[] ts = {t_e_1nom, t_e_1llinatge1, t_e_1llinatge2};
            javax.swing.JLabel[] ls = {l_pare, l_ppare, l_mppare, l_pppare,
            l_mpare, l_pmpare, l_mmpare};
            for (JTextField t : ts) {
                t.setText("");
            }
            for (javax.swing.JLabel jl:  ls){
                jl.setText(unknown);
            }
            this.c_1sexe.setSelectedIndex(0);
            this.t_1comentaris.setText("");
            fnmarit.iniciar();
            fdmarit.iniciar();
            fdnmarit.iniciar();
            fddmarit.iniciar();
            this.b_1pares.setEnabled(false);
            
        }else{
            fillText(this.t_e_1nom,c1.getNom());
            fillText(this.t_e_1llinatge1, c1.getLlinatge1());
            fillText(this.t_e_1llinatge2, c1.getLlinatge2());
            this.fnmarit.fill(c1.getLlocNaixement());
            fdnmarit.fill(c1.getDateNaixement());
            this.fdmarit.fill(c1.getLlocDefuncio());
            fddmarit.fill(c1.getDataDefuncio());
            fillSexe(this.c_1sexe, c1);
            fillText(this.t_1comentaris, c1.getComentaris());
            this.l_e_conjuge1.setText(sexe(un.getConjuge1()));

            db.persona cp1 = c1.getPare();
            db.persona cm1 = c1.getMare();
            this.l_pare.setText(c1.toString());
            this.l_ppare.setText(cp1.toString());
            this.l_pppare.setText(cp1.getPare().toString());
            this.l_mppare.setText(cp1.getMare().toString());
            this.l_mpare.setText(cm1.toString());
            this.l_pmpare.setText(cm1.getPare().toString());
            this.l_mmpare.setText(cm1.getMare().toString());

            setupButton(c1.getId(),this.b_1pares);
        }
    }
    
    private void fillConjuge2(db.persona c2) throws DBException{
        if (c2.isNull()){
            javax.swing.JTextField[] ts = {t_2nom, t_2llinatge1, t_2llinatge2};
            javax.swing.JLabel[] ls = {l_mare, l_pmare, l_mpmare, l_ppmare,
            l_mmare, l_pmmare, l_mmmare};
            for (JTextField t : ts) {
                t.setText("");
            }
            for (javax.swing.JLabel jl:  ls){
                jl.setText(unknown);
            }
            this.t_2comentaris.setText("");
            this.c_2sexe.setSelectedIndex(0);
            fnmuller.iniciar();
            fdmuller.iniciar();
            fdnmuller.iniciar();
            fddmuller.iniciar();
            this.b_2pares.setEnabled(false);
            
        }else{
            fillText(this.t_2nom,c2.getNom());
            fillText(this.t_2llinatge1, c2.getLlinatge1());
            fillText(this.t_2llinatge2, c2.getLlinatge2());
            this.fnmuller.fill(c2.getLlocNaixement());
            fdnmuller.fill(c2.getDateNaixement());
            this.fdmuller.fill(c2.getLlocDefuncio());
            fddmuller.fill(c2.getDataDefuncio());
            fillSexe(this.c_2sexe, c2); 
            fillText(this.t_2comentaris, c2.getComentaris());
            this.l_e_conjuge2.setText(sexe(un.getConjuge2()));

            db.persona cp2 = c2.getPare();
            db.persona cm2 = c2.getMare();
            this.l_mare.setText(c2.toString());
            this.l_pmare.setText(cp2.toString());
            this.l_ppmare.setText(cp2.getPare().toString());
            this.l_mpmare.setText(cp2.getMare().toString());
            this.l_mmare.setText(cm2.toString());
            this.l_pmmare.setText(cm2.getPare().toString());
            this.l_mmmare.setText(cm2.getMare().toString());


            setupButton(c2.getId(),this.b_2pares);
        }
    }
    
    private void setupButton(int id_p, javax.swing.JButton b){
        try{
                db.naixement n = new db.naixement(id_p);
                if (n.getIdUnio()==-1){
                    b.setText(newPares);
                    System.out.println("No unió. Id fill: "+id_p+", Id unió: "+n.getIdUnio());
                }else{
                    b.setText("Fitxa pares");
                }
            }catch (DBException e){
                b.setText(newPares);
                System.out.println("Excepció");
            }
    }
    
    
    
    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JMenuItem Seleccionar;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton b_1pares;
    private javax.swing.JButton b_2pares;
    private javax.swing.JButton b_afegirfill;
    private javax.swing.JButton b_cerca;
    private javax.swing.JButton b_eafegir;
    private javax.swing.JButton b_eborrar;
    private javax.swing.JComboBox c_1ddia;
    private javax.swing.JComboBox c_1dia;
    private javax.swing.JComboBox c_1dllogaret;
    private javax.swing.JComboBox c_1dmes;
    private javax.swing.JComboBox c_1dmunicipi;
    private javax.swing.JComboBox c_1dparroquia;
    private javax.swing.JComboBox c_1llogaret;
    private javax.swing.JComboBox c_1mes;
    private javax.swing.JComboBox c_1municipi;
    private javax.swing.JComboBox c_1parroquia;
    private javax.swing.JComboBox c_1sexe;
    private javax.swing.JComboBox c_2ddia;
    private javax.swing.JComboBox c_2dllogaret;
    private javax.swing.JComboBox c_2dmes;
    private javax.swing.JComboBox c_2dmunicipi;
    private javax.swing.JComboBox c_2dparroquia;
    private javax.swing.JComboBox c_2ndia;
    private javax.swing.JComboBox c_2nllogaret;
    private javax.swing.JComboBox c_2nmes;
    private javax.swing.JComboBox c_2nmunicipi;
    private javax.swing.JComboBox c_2nparroquia;
    private javax.swing.JComboBox c_2sexe;
    private javax.swing.JCheckBox c_casament;
    private javax.swing.JTextField c_uany;
    private javax.swing.JComboBox c_udia;
    private javax.swing.JComboBox c_ullogaret;
    private javax.swing.JComboBox c_umes;
    private javax.swing.JComboBox c_umunicipi;
    private javax.swing.JComboBox c_uparroquia;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JList find_list;
    private javax.swing.JTextField find_llinatge1;
    private javax.swing.JTextField find_llinatge2;
    private javax.swing.JTextField find_nom;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JList jList6;
    private javax.swing.JList jList7;
    private javax.swing.JList jList8;
    private javax.swing.JList jList9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel l_e_conjuge1;
    private javax.swing.JLabel l_e_conjuge2;
    private javax.swing.JLabel l_generacio;
    private javax.swing.JLabel l_mare;
    private javax.swing.JLabel l_mmare;
    private javax.swing.JLabel l_mmmare;
    private javax.swing.JLabel l_mmpare;
    private javax.swing.JLabel l_mpare;
    private javax.swing.JLabel l_mpmare;
    private javax.swing.JLabel l_mppare;
    private javax.swing.JLabel l_pare;
    private javax.swing.JLabel l_pmare;
    private javax.swing.JLabel l_pmmare;
    private javax.swing.JLabel l_pmpare;
    private javax.swing.JLabel l_ppare;
    private javax.swing.JLabel l_ppmare;
    private javax.swing.JLabel l_pppare;
    private javax.swing.JList ll_ufills;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JPopupMenu popuplist;
    private javax.swing.JTextField t_1any;
    private javax.swing.JTextArea t_1comentaris;
    private javax.swing.JTextField t_1dany;
    private javax.swing.JTextArea t_2comentaris;
    private javax.swing.JTextField t_2dany;
    private javax.swing.JTextField t_2llinatge1;
    private javax.swing.JTextField t_2llinatge2;
    private javax.swing.JTextField t_2nany;
    private javax.swing.JTextField t_2nom;
    private javax.swing.JTextField t_e_1llinatge1;
    private javax.swing.JTextField t_e_1llinatge2;
    private javax.swing.JTextField t_e_1nom;
    private javax.swing.JTextField t_fitxa;
    private javax.swing.JTextArea t_ucomentaris;
    // End of variables declaration//GEN-END:variables

    private void fillFind() {
        lcerca = new ArrayList<>();
        db.persona[] people = db.persona.getPeopleLike(this.find_nom.getText(),
                this.find_llinatge1.getText(), this.find_llinatge2.getText());
        this.fillList(this.find_list, people, false);
    }

    private void iniciarUnio() {
        this.fmatrimoni.iniciar();
        this.fdmatrimoni.iniciar();
        this.fmatrimoni.disable();
        this.fdmatrimoni.disable();
        this.t_ucomentaris.setText("");
        this.t_fitxa.setText("");
        this.c_casament.setSelected(false);
        fillList(this.ll_ufills,new db.persona [0], true);
    }

    private void paresClicked(boolean c1) {
        javax.swing.JButton b;
        String idc;
        int id, id2;
        if (c1){
            b = this.b_1pares;
            idc = "idc1";
            id = idc1;
            id2 = idc2;
        }else{
            b = this.b_2pares;
            idc = "idc2";
            id = idc2;
            id2  =idc1;
        }
        
        // S'han d'afegir pares nous
        if (b.getText().equals(newPares)){
            if (c1){
                idc1 = un.getConjuge1().getId();
                idc2 = -1;
            }else{
                idc2 = un.getConjuge2().getId();
                idc1 = -1;
            }
            un = new unio();
            this.newParesForm(c1);
        }else{  // S'ha de carregar una fitxa de pares
            try {
                if (un.isNull()){
                    if(id != -1){
                        if (b.getText().equals(newPares)){  // Afegir pares sabent persona
                            un = new unio();
                            this.newParesForm(c1);
                        }else{                              // Carregar pares sabent persona
                            un = new db.unio(new db.persona(id).getPare().getId());
                            idc1 = -1;
                            idc2 = -1;
                            this.fillForm();
                        }
                    }else{                                  // Error: no es sap ni persona ni unió
                        new GException("S'ha perdut la pista de l'arbre.\n No hi ha "
                                + "cap ID de referència.","Error greu.").show();
                    }
                }else{                                      // Carregar pares sabent unió
                    if (c1){
                        un = unio.fromConjuge(un.getConjuge1().getPare().getId());
                    }else{
                        un = unio.fromConjuge(un.getConjuge2().getPare().getId());
                    }
                    this.fillForm();
                }

            } catch (DBException ex) {} catch (MUException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    

}
