/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IHM;

import GraphAlgo.BreadthFirstDirectedPaths;
import GraphAlgo.Digraph;
import Jama.Matrix;
import Metier.ContextMenuMouseListener;
import Metier.InputData;
import Metier.MyCustomFilter;
import Metier.IndexMaxPQ;
import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

/**
 *
 * @author zakaria
 */
public class MainWindow extends javax.swing.JFrame {

    private InputData data;
    private int nb_ligne;
    private int nb_colone;
    private Matrix Matrice;
    private Digraph G;
    private Graph<String, String> JungGraph;
    private Graph<String, String> JungCommunauté;
    private String filename;
    private Vector<Integer> leaders;
    private Vector<Integer> Followers;
    private Vector<Color> LeadersColor;
    private Vector<Color> FollowersColor;
    private Vector<Integer> UnFollowers;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        UIManager.put("Synthetica.window.decoration", Boolean.FALSE);
        try
        {
          UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
        }
        catch (ParseException | UnsupportedLookAndFeelException e)
        {
          e.printStackTrace();
        }
        initComponents();
        jMenuVoirGraph.setEnabled(false);
        jMenuVoirCommunauté.setEnabled(false);
        this.setLocationRelativeTo(null);
        LabelValiditéMatrice.setVisible(false);
    }

    public boolean StoreMatrice(String tmp)
    {
        boolean valid=true;
        try {
            String d[] = tmp.split("\\s+");
            nb_ligne = (int) Math.sqrt(d.length);
            nb_colone = (int) Math.sqrt(d.length);

            data = new InputData(nb_ligne, nb_colone);
            data.data = new int[nb_ligne][nb_colone];

            this.Matrice = new Matrix(nb_ligne, nb_colone);

            G = new Digraph(nb_ligne);
            JungGraph = new SparseGraph<>();

            for (int i = 0; i < d.length; i++) {
                data.data[i / nb_ligne][i % nb_colone] = Integer.parseInt(d[i]);

            }
            for (int i = 0; i < nb_ligne; i++) {
                JungGraph.addVertex(String.valueOf(i));
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format de matrice invalid", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            valid=false;
        }
        return valid;
    }

    public boolean VerifierMatrice(boolean isvalid,InputData d) 
    {
        if(isvalid==false) return false; 
        boolean valid=true;
        try {

            for (int i = 0; i < d.getRows(); i++) {

                for (int j = 0; j < d.getColumns(); j++) {
                    int n = d.data[i][j];
                    if(i==j && n==1)throw new NumberFormatException();
                    if(n!=1 && n!=0) throw  new NumberFormatException();
                    this.Matrice.set(i, j, n);
                    if (n != 0) 
                    {
                        JungGraph.addEdge("E(" + String.valueOf(i) + "," + String.valueOf(j) + ")", String.valueOf(i), String.valueOf(j));
                        G.addEdge(i, j);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Format de matrice invalid", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            valid=false;
        }
        return valid;
    }

    public Vector<Integer> FirstOrderNodes(Digraph graph, int node) {
        Vector<Integer> list = new Vector<Integer>();
        BreadthFirstDirectedPaths depth = new BreadthFirstDirectedPaths(G, node);
        for (int i = 0; i < graph.V(); i++) {
            if (i != node) {
                if (depth.pathTo(i) != null) {
                    String path = depth.pathTo(i).toString();
                    String[] tmp = path.split(" ");
                    if (tmp.length == 2) {
                        list.add(i);
                    }
                }
            }
        }

        return list;
    }

    ////////////////////////////////////////////////////////////////////   Centralité    ///////////////////////////////////////////////
    public IndexMaxPQ<Double> CalculeCentralité() {
        DoubleMatrix matrix = new DoubleMatrix(Matrice.getArray());

        ComplexDoubleMatrix eigenvalues = Eigen.eigenvalues(matrix);
        List<Double> principalEigenvector = getPrincipalEigenvector(matrix);

        List<Double> NormalisedEigenVectors=normalised(principalEigenvector);
        
        Double[] a=new Double[NormalisedEigenVectors.size()];
        IndexMaxPQ<Double> pq=new IndexMaxPQ<Double>(a.length);
        for (int i = 0; i < a.length; i++) 
        {
            a[i]=NormalisedEigenVectors.get(i);
            pq.insert(i, a[i]);
        }
        return pq;
    }

    private  List<Double> getPrincipalEigenvector(DoubleMatrix matrix) {
        int maxIndex = getMaxIndex(matrix);
        ComplexDoubleMatrix eigenVectors = Eigen.eigenvectors(matrix)[0];
        return getEigenVector(eigenVectors, maxIndex);
    }

    private  int getMaxIndex(DoubleMatrix matrix) {
        ComplexDouble[] doubleMatrix = Eigen.eigenvalues(matrix).toArray();
        int maxIndex = 0;
        for (int i = 0; i < doubleMatrix.length; i++) {
            double newnumber = doubleMatrix[i].abs();
            if ((newnumber > doubleMatrix[maxIndex].abs())) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private  List<Double> getEigenVector(ComplexDoubleMatrix eigenvector, int columnId) {
        ComplexDoubleMatrix column = eigenvector.getColumn(columnId);

        List<Double> values = new ArrayList<Double>();
        for (ComplexDouble value : column.toArray()) {
            values.add(value.abs());
        }
        return values;
    }

    private  List<Double> normalised(List<Double> principalEigenvector) {
        double total = sum(principalEigenvector);
        List<Double> normalisedValues = new ArrayList<Double>();
        for (Double aDouble : principalEigenvector) {
            normalisedValues.add(aDouble / total);
        }
        return normalisedValues;
    }

    private  double sum(List<Double> principalEigenvector) {
        double total = 0;
        for (Double aDouble : principalEigenvector) {
            total += aDouble;
        }
        return total;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////:
    
    public ArrayList<List<Integer>> Leader_Voisins_List(IndexMaxPQ<Double> pq)
    {
        ArrayList<List<Integer>> Result =new ArrayList <>();
        while(!pq.isEmpty()) 
        {
            List<Integer> temp=new ArrayList<>();
            int l=pq.maxIndex();
            Vector<Integer> voisin=FirstOrderNodes(G, l);
            for (List<Integer> Result1 : Result) 
            {
                voisin.removeAll(Result1);
            }
            temp.add(l);
            temp.addAll(voisin);
            pq.delete(l);
            for (Integer voisin1 : voisin) 
            {
                pq.delete(voisin1);
            }
            Result.add(temp);
        }
        return Result;
    }
    public void GenerateCommunauté(ArrayList<List<Integer>> Result)
    {
        leaders=new Vector<>();
        Followers=new Vector<>();
        LeadersColor=new Vector<>();
        FollowersColor=new Vector<>();
        UnFollowers=new Vector<>();
        
        this.JungCommunauté=new SparseGraph<>();
        for (List<Integer> ListNodes : Result) 
        {
            if(ListNodes.size()!=1)
            {
                Color randomColour;
                do
                {
                    Random randomGenerator = new Random();
                    int red = randomGenerator.nextInt(256);
                    int green = randomGenerator.nextInt(256);
                    int blue = randomGenerator.nextInt(256);

                    randomColour = new Color(red,green,blue);
                }
                while(LeadersColor.contains(randomColour));

                JungCommunauté.addVertex("L"+ListNodes.get(0));
                leaders.add(ListNodes.get(0));
                LeadersColor.add(randomColour);
                for (int i = 1; i < ListNodes.size(); i++)
                {
                    JungCommunauté.addVertex(""+ListNodes.get(i));
                    Followers.add(ListNodes.get(i));
                    FollowersColor.add(randomColour);
                }
            }
            else
            {
                UnFollowers.add(ListNodes.get(0));
            }
        }
        for (int i = 0; i < data.getRows(); i++) 
        {

            for (int j = 0; j < data.getColumns(); j++) 
            {
                int n = data.data[i][j];
                if (n != 0) 
                {
                    if(leaders.contains(i))
                    {
                        if(leaders.contains(j))
                        {
                            JungCommunauté.addEdge("E(" + "L"+String.valueOf(i) + "," + "L"+String.valueOf(j) + ")",
                                    "L"+String.valueOf(i), "L"+String.valueOf(j));
                        }
                        else
                        JungCommunauté.addEdge("E(" + "L"+String.valueOf(i) + "," + String.valueOf(j) + ")",
                                "L"+String.valueOf(i), String.valueOf(j));
                    }
                    else
                    {
                        if(leaders.contains(j))
                        {
                            JungCommunauté.addEdge("E(" + String.valueOf(i) + "," + "L"+String.valueOf(j) + ")",
                                    String.valueOf(i), "L"+String.valueOf(j));
                        }
                        else
                        JungCommunauté.addEdge("E(" + String.valueOf(i) + "," + String.valueOf(j) + ")",
                                String.valueOf(i), String.valueOf(j));
                    }
                    
                    
                }
            }
        }
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMatrice = new javax.swing.JTextArea();
        ButtonVerifierMatrice = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        LabelValiditéMatrice = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItemQuitter = new javax.swing.JMenuItem();
        jMenuVoirGraph = new javax.swing.JMenu();
        jMenuVoirCommunauté = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(820, 385));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtMatrice.setColumns(20);
        txtMatrice.setRows(5);
        txtMatrice.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtMatriceCaretUpdate(evt);
            }
        });
        txtMatrice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMatriceMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(txtMatrice);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 16, 343, 310));

        ButtonVerifierMatrice.setText("Vérifier la matrice");
        ButtonVerifierMatrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonVerifierMatriceActionPerformed(evt);
            }
        });
        getContentPane().add(ButtonVerifierMatrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 330, -1, -1));

        jLabel1.setText("Du blabla sur le projet ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, 450, 140));

        LabelValiditéMatrice.setText("jLabel2");
        getContentPane().add(LabelValiditéMatrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 334, 108, -1));

        jMenu1.setText("Fichier");

        jMenuItem1.setText("Importer la matrice");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItemQuitter.setText("Quitter");
        jMenuItemQuitter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitterActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemQuitter);

        jMenuBar1.add(jMenu1);

        jMenuVoirGraph.setText("Voir le graphe initial");
        jMenuVoirGraph.setToolTipText("Veuillez vérifier la matrice");
        jMenuVoirGraph.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuVoirGraphMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuVoirGraph);

        jMenuVoirCommunauté.setText("Voir les communautés");
        jMenuVoirCommunauté.setToolTipText("Veuillez vérifier la matrice");
        jMenuVoirCommunauté.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuVoirCommunautéMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuVoirCommunauté);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonVerifierMatriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonVerifierMatriceActionPerformed
        // TODO add your handling code here:
        
        if(VerifierMatrice(StoreMatrice(txtMatrice.getText()),data)==true)
        {
            LabelValiditéMatrice.setText("Matrice valid !");
            LabelValiditéMatrice.setForeground(Color.BLUE);
            LabelValiditéMatrice.setVisible(true);
            jMenuVoirCommunauté.setEnabled(true);
            jMenuVoirGraph.setEnabled(true);
        }
        else
        {
            LabelValiditéMatrice.setText("Matrice invalid !");
            LabelValiditéMatrice.setForeground(Color.red);
            LabelValiditéMatrice.setVisible(true);
        }
    }//GEN-LAST:event_ButtonVerifierMatriceActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        JFileChooser filechooser = new JFileChooser();
        filechooser.setFileFilter(new MyCustomFilter());
        int showOpenDialog = filechooser.showOpenDialog(null);
        if (showOpenDialog != 1) {
            filename = filechooser.getSelectedFile().getAbsolutePath();
            try {
                BufferedReader Br = new BufferedReader(new FileReader(filename));
                String tmp = "";
                String line;
                while ((line = Br.readLine()) != null) {
                    tmp += (line + "\r\n");
                }
                txtMatrice.setText(tmp);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItemQuitterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemQuitterActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jMenuItemQuitterActionPerformed

    private void txtMatriceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMatriceMouseClicked
        // TODO add your handling code here:
        ContextMenuMouseListener menu = new ContextMenuMouseListener(txtMatrice);
        menu.mouseClicked(evt);
    }//GEN-LAST:event_txtMatriceMouseClicked

    private void txtMatriceCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtMatriceCaretUpdate
        // TODO add your handling code here:
        jMenuVoirGraph.setEnabled(false);
        jMenuVoirCommunauté.setEnabled(false);
        LabelValiditéMatrice.setVisible(false);
    }//GEN-LAST:event_txtMatriceCaretUpdate

    private void jMenuVoirGraphMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuVoirGraphMouseClicked
        // TODO add your handling code here:
        if (jMenuVoirGraph.isEnabled()) {
            GialogGraph Fr;
            Fr = new GialogGraph(new Dimension(this.size().width, this.size().height), JungGraph, this);
            this.disable();
            Fr.Show();
        }
    }//GEN-LAST:event_jMenuVoirGraphMouseClicked

    private void jMenuVoirCommunautéMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuVoirCommunautéMouseClicked
        // TODO add your handling code here:
        if (jMenuVoirCommunauté.isEnabled()) {
            ArrayList<List<Integer>> Leader_Voisins_List = Leader_Voisins_List(CalculeCentralité());
            GenerateCommunauté(Leader_Voisins_List);
            GialogCommunauté Fr;
            Fr = new GialogCommunauté(new Dimension(this.size().width, this.size().height), JungCommunauté, this,
                    leaders,Followers,LeadersColor,FollowersColor,UnFollowers);
            this.disable();
            Fr.Show();
        }
    }//GEN-LAST:event_jMenuVoirCommunautéMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonVerifierMatrice;
    private javax.swing.JLabel LabelValiditéMatrice;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemQuitter;
    private javax.swing.JMenu jMenuVoirCommunauté;
    private javax.swing.JMenu jMenuVoirGraph;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtMatrice;
    // End of variables declaration//GEN-END:variables
}
