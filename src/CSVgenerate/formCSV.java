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
package CSVgenerate;

import javax.swing.JOptionPane;

/**
 *
 * @author jordi
 */
public class formCSV extends javax.swing.JFrame {

    CSVgenerate h;
    
    /**
     * Creates new form formCSV
     */
    public formCSV() {
        initComponents();
        
        String[] municipis = db.municipi.getAll();
        geneal.sourceforms.formutils.fillCb(this.c_municipi, municipis);
        h = new CSVgenerate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        c_municipi = new javax.swing.JComboBox();
        c_defuncions = new javax.swing.JCheckBox();
        c_naixements = new javax.swing.JCheckBox();
        c_matrimonis = new javax.swing.JCheckBox();
        b_generar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Geneal - Generar CSV");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Municipi");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        c_municipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        c_municipi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_municipiActionPerformed(evt);
            }
        });
        getContentPane().add(c_municipi, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 396, -1));

        c_defuncions.setText("Defuncions");
        c_defuncions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_defuncionsActionPerformed(evt);
            }
        });
        getContentPane().add(c_defuncions, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        c_naixements.setText("Naixements");
        c_naixements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_naixementsActionPerformed(evt);
            }
        });
        getContentPane().add(c_naixements, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));

        c_matrimonis.setText("Matrimonis");
        c_matrimonis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c_matrimonisActionPerformed(evt);
            }
        });
        getContentPane().add(c_matrimonis, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        b_generar.setText("Generar");
        b_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_generarActionPerformed(evt);
            }
        });
        getContentPane().add(b_generar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void c_defuncionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_defuncionsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_defuncionsActionPerformed

    private void c_naixementsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_naixementsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_naixementsActionPerformed

    private void c_municipiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_municipiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_municipiActionPerformed

    private void c_matrimonisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_c_matrimonisActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_c_matrimonisActionPerformed

    private void b_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_generarActionPerformed
        if (this.c_municipi.getSelectedIndex()==0){
            new Exceptions.GException("No has seleccionat cap municipi.\n"
                    + "Selecciona'n un i tarna-ho a intentar.","Municipi no seleccionat").show();
        }else if(!c_naixements.isSelected() && !c_matrimonis.isSelected() &&
                !c_defuncions.isSelected()){
            new Exceptions.GException("No has seleccionat el que vols generar (naxements, matrimonis, defuncions).\n"
                    + "Selecciona-ho i tarna-ho a intentar.","Tipus no especificat").show();
        }else{
            String municipi = String.valueOf(this.c_municipi.getSelectedItem());
            String types = "";
            if (c_naixements.isSelected()){
                h.write(CSVgenerate.naixements, municipi);
                types = types + "\n \t Naixements";
            }
            if (c_matrimonis.isSelected()){
                h.write(CSVgenerate.matrimonis, municipi);
                types = types + "\n \t Matrimonis";
            }
            if (c_defuncions.isSelected()){
                h.write(CSVgenerate.defuncions, municipi);
                types = types + "\n \t Defuncions";
            }
            JOptionPane.showMessageDialog(null, "S'han generat els arxius corresponents a "
                    +types+"\n\n Del municipi: "+municipi,
                    "Geneal - CSV generats", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_b_generarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b_generar;
    private javax.swing.JCheckBox c_defuncions;
    private javax.swing.JCheckBox c_matrimonis;
    private javax.swing.JComboBox c_municipi;
    private javax.swing.JCheckBox c_naixements;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
