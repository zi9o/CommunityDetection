/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IHM;

import Metier.MyRendererCommunauté;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.JDialog;
import org.apache.commons.collections15.Transformer;

/**
 *
 * @author zakaria
 */
public class GialogCommunauté {

    private  Dimension dim;
    private  Graph<String, String> JungGraph;
    private MainWindow MymainWidow;
    Vector<Color> leadersColors;
    Vector<Color> followersColors;
    private Vector<Integer> leaders;
    private Vector<Integer> Followers;
    private Vector<Integer> UnFollowers;

    public GialogCommunauté(Dimension dim, Graph<String, String> JungGraph,MainWindow mainWindow,
            Vector<Integer> leader,Vector<Integer> follower,Vector<Color> leaders,Vector<Color> followers,Vector<Integer> unfollowers) 
    {
        this.dim = dim;
        this.JungGraph = JungGraph;
        this.MymainWidow=mainWindow;
        this.leaders=leader;
        this.Followers=follower;
        this.leadersColors=leaders;
        this.followersColors=followers;
        this.UnFollowers=unfollowers;
    }
    public  void Show()
    {
        VisualizationViewer<String, String> vv;
        vv = new VisualizationViewer<>(
                new CircleLayout<String, String>(JungGraph), dim);

        Transformer<String, String> transformer = new Transformer<String, String>() {
            @Override
            public String transform(String arg0) {
                return arg0;
            }
        };
        vv.getRenderContext().setVertexLabelTransformer(transformer);
        transformer = new Transformer<String, String>() {
            @Override
            public String transform(String arg0) {
                return arg0;
            }
        };
        vv.getRenderContext().setEdgeLabelTransformer(transformer);
        vv.getRenderer().setVertexRenderer(new MyRendererCommunauté(leaders,Followers,leadersColors,followersColors,UnFollowers));
        DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<String, Number>();
        vv.setGraphMouse(graphMouse);
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);

        JDialog frame = new JDialog(MymainWidow);
        frame.getContentPane().add(vv);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                
            }

            @Override
            public void windowClosing(WindowEvent e) {
                MymainWidow.enable();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                MymainWidow.enable();
            }

            @Override
            public void windowIconified(WindowEvent e) {
                
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                
            }

            @Override
            public void windowActivated(WindowEvent e) {
                
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(MymainWidow);
        
        frame.setVisible(true);
    }
    
}
