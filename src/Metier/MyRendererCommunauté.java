/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Metier;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 *
 * @author zakaria
 */
public class MyRendererCommunauté implements Renderer.Vertex<String, String> 
{
    Vector<Color> leadersColors;
    Vector<Color> followersColors;
    private Vector<Integer> leaders;
    private Vector<Integer> Followers;
    private Vector<Integer> UnFollowers;

    public MyRendererCommunauté(Vector<Integer> leader,Vector<Integer> follower,
            Vector<Color> leaders,Vector<Color> followers,Vector<Integer> unfollowers) 
    {
        this.leadersColors=leaders;
        this.followersColors=followers;
        this.leaders=leader;
        this.Followers=follower;
        this.UnFollowers=unfollowers;
    }
    @Override public void paintVertex(RenderContext<String, String> rc,
        Layout<String, String> layout, String vertex) {
      GraphicsDecorator graphicsContext = rc.getGraphicsContext();
      Point2D center = layout.transform(vertex);
      Shape shape = null;
      Color color = null;
      if(vertex.contains("L"))
      {
          shape = new Rectangle((int)center.getX()-10, (int)center.getY()-10, 30, 30);
          String node=vertex.replace("L", "");
          color = leadersColors.get(leaders.indexOf(Integer.parseInt(node)));
      }
      else
      {
          if(!UnFollowers.contains(Integer.parseInt(vertex)))
          {
              shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
              color = followersColors.get(Followers.indexOf(Integer.parseInt(vertex)));
          }
          else
          {
              shape = new Ellipse2D.Double(center.getX()-10, center.getY()-10, 20, 20);
              color =Color.WHITE;
          }
      }
      
      
      graphicsContext.setPaint(color);
      graphicsContext.fill(shape);
    }
  }
