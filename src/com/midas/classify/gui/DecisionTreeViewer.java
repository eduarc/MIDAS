/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.midas.classify.gui;

import com.midas.classify.util.TreeEdge;
import com.midas.classify.util.TreeNode;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import say.swing.JFontChooser;

/**
 *
 * @author eduarc (Eduar Castrillo)
 */
public class DecisionTreeViewer extends JPanel {

  private DelegateTree tree;
  private VisualizationViewer<TreeNode, TreeEdge> vv;
  private DefaultModalGraphMouse<TreeNode, TreeEdge> gm;
  private TreeMap<String, Color> classColors;
  private TreeSet<String> leafNodes;
  
  private JFontChooser fontChooser;
  private JComboBox cbClasses;
  private JComboBox cbMode;
  private JComboBox cbLabelPosition;
  private JButton bZoomIn;
  private JButton bZoomOut;
  private JButton bNodeFont;
  private JButton bEdgeFont;
  private JButton bNodeColor;
  private JCheckBox chkShowEdgeNames;
  private JCheckBox chkShowNodeNames;
  
  private String[] classNames;
  private Color nodeColor;
  private Font nodeFont;
  private Font edgeFont;
  
  public DecisionTreeViewer(DelegateTree<TreeNode, TreeEdge> tr, String[] cls) {    
    setLayout(new BorderLayout());
    
    // ---------------------------------------------- //
    // INICIALIZACION DE COMPONENTES DE VISUALIZACION //
    // ---------------------------------------------- //
    tree = tr;
    classNames = cls;
    classColors = new TreeMap();
    
    nodeColor = Color.BLUE;
    for (int i = 0; i < cls.length; ++i) {
      classColors.put(cls[i], Color.RED);
    }
    
    leafNodes = new TreeSet();
    for (TreeNode n : tr.getVertices()) {
      if (tr.isLeaf(n)) {
        for (String s : cls) {
          if (n.toString().startsWith(s)) {
            leafNodes.add(s);
            break;
          }
        }
      }
    }
    
    TreeLayout<TreeNode, TreeEdge> layout = new TreeLayout(tr);

    vv = new VisualizationViewer(layout);
    vv.setBackground(Color.white);

    vv.getRenderContext().setEdgeFontTransformer(new EdgeFontTransformer());
    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<TreeNode, TreeEdge>());
    
    PickedState<TreeNode> picked_state = vv.getPickedVertexState();
    
    vv.getRenderContext().setVertexFillPaintTransformer(new VertexFillColorTransformer<TreeNode>(picked_state));
    vv.getRenderContext().setVertexDrawPaintTransformer(new VertexDrawColorTransformer<TreeNode>(picked_state));
    vv.getRenderContext().setVertexFontTransformer(new VertexFontTransformer());
    
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    
    GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
    gm = new DefaultModalGraphMouse();
    vv.setGraphMouse(gm);
    
    final ScalingControl scaler = new CrossoverScalingControl();
    
    // ---------------------------------------- //
    // INICIALIZACION DE COMPONENTES DE CONTROL //
    // ---------------------------------------- //
    
    fontChooser = new JFontChooser();
    
    JPanel pControl = new JPanel(new GridLayout(7, 1, 4, 2));
    
    JPanel pZoom = new JPanel(new GridLayout(1, 2, 2, 2));
    pZoom.setBorder(new TitledBorder("Zoom"));
    
    bZoomIn = new JButton("+");
    bZoomIn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        scaler.scale(vv, 1.1f, vv.getCenter());
      }
    });
    
    bZoomOut = new JButton("-");
    bZoomOut.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        scaler.scale(vv, 1/1.1f, vv.getCenter());
      }
    });
    pZoom.add(bZoomIn);
    pZoom.add(bZoomOut);
    
    JPanel pFont = new JPanel(new GridLayout(1, 2, 2, 2));
    pFont.setBorder(new TitledBorder("Fonts"));
    
    bNodeFont = new JButton("Node Font");
    bNodeFont.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int r = fontChooser.showDialog(null);
        if (r == JFontChooser.OK_OPTION) {
          nodeFont = fontChooser.getSelectedFont();
          vv.repaint();
        }
      }
    });
    
    bEdgeFont = new JButton("Edge Font");
    bEdgeFont.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int r = fontChooser.showDialog(null);
        if (r == JFontChooser.OK_OPTION) {
          edgeFont = fontChooser.getSelectedFont();
          vv.repaint();
        }
      }
    });
    
    pFont.add(bNodeFont);
    pFont.add(bEdgeFont);
    
    bNodeColor = new JButton("Node Color");
    bNodeColor.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        nodeColor = JColorChooser.showDialog(null, "Select color for intern nodes", Color.RED);
        vv.repaint();
      }
    });
    
    JPanel pMode = new JPanel();
    pMode.setBorder(new TitledBorder("Mode"));
    
    cbMode = new JComboBox();
    cbMode.addItem(ModalGraphMouse.Mode.TRANSFORMING);
    cbMode.addItem(ModalGraphMouse.Mode.PICKING);
    cbMode.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        gm.setMode((ModalGraphMouse.Mode)e.getItem());
      }
    });
    
    pMode.add(cbMode);
    
    JPanel pColors = new JPanel(new GridLayout(1, 2, 2, 2));
    pColors.setBorder(new TitledBorder("Node Colors"));
    
    cbClasses = new JComboBox(classNames);
    cbClasses.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Color c = JColorChooser.showDialog(null, "Select class color for "+cbClasses.getSelectedItem(), Color.RED);
        classColors.put(cbClasses.getSelectedItem().toString(), c);
        vv.repaint();
      }
    });
    pColors.add(cbClasses);
    pColors.add(bNodeColor);
    
    JPanel pLabelPosition = new JPanel();
    pLabelPosition.setBorder(new TitledBorder("Label Position"));
    
    cbLabelPosition = new JComboBox();
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.N);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.NE);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.E);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.SE);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.S);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.SW);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.W);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.NW);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.N);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.CNTR);
    cbLabelPosition.addItem(Renderer.VertexLabel.Position.AUTO);
    
    cbLabelPosition.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
				Renderer.VertexLabel.Position position = 
					(Renderer.VertexLabel.Position)e.getItem();
				vv.getRenderer().getVertexLabelRenderer().setPosition(position);
				vv.repaint();
			}});
    cbLabelPosition.setSelectedItem(Renderer.VertexLabel.Position.SE);
    
    pLabelPosition.add(cbLabelPosition);
            
    chkShowEdgeNames = new JCheckBox("Show Edge Name");
    chkShowEdgeNames.addActionListener(new ActionListener() {
      ToStringLabeller strLabeller = new ToStringLabeller();
      ConstantTransformer none = new ConstantTransformer(null);
      @Override
      public void actionPerformed(ActionEvent e) {
        AbstractButton src = (AbstractButton) e.getSource();
        if (src.isSelected()) {
          vv.getRenderContext().setEdgeLabelTransformer(strLabeller);
        } else {
          vv.getRenderContext().setEdgeLabelTransformer(none);
        }
        vv.repaint();
      }
    });
    chkShowEdgeNames.setSelected(true);
    
    chkShowNodeNames = new JCheckBox("Show Node Name");
    chkShowNodeNames.addActionListener(new ActionListener() {
      ToStringLabeller strLabeller = new ToStringLabeller();
      ConstantTransformer none = new ConstantTransformer(null);
      @Override
      public void actionPerformed(ActionEvent e) {
        AbstractButton src = (AbstractButton) e.getSource();
        if (src.isSelected()) {
          vv.getRenderContext().setVertexLabelTransformer(strLabeller);
        } else {
          vv.getRenderContext().setVertexLabelTransformer(none);
        }
        vv.repaint();
      }
    });
    chkShowNodeNames.setSelected(true);
    
    pControl.add(pZoom);
    pControl.add(pFont);
    pControl.add(pMode);
    pControl.add(pLabelPosition);
    pControl.add(pColors);
    pControl.add(chkShowNodeNames);
    pControl.add(chkShowEdgeNames);
    
    // Agregar componentes al panel
    add(scrollPane);
    
    JPanel p = new JPanel(new FlowLayout());
    p.add(pControl);
    add(p, BorderLayout.EAST);
  }

  public DelegateTree getTree() {
    return tree;
  }
  
  // --------------- //
  // TRANSFORMADORES //
  // --------------- //
  
  class VertexFillColorTransformer<V> implements Transformer<V, Paint> {

    PickedInfo<V> picked;
    
    public VertexFillColorTransformer(PickedInfo picked) {
      this.picked = picked;
    }
    
    @Override
    public Paint transform(V i) {
      
      String name = i.toString();
      for (String nname : leafNodes) {
        if (name.startsWith(nname)) {
          return classColors.get(nname);
        }
      }
      return nodeColor;
    }
  }

  class VertexDrawColorTransformer<V> implements Transformer<V, Paint> {

    PickedInfo<V> picked;
    
    public VertexDrawColorTransformer(PickedInfo picked) {
      this.picked = picked;
    }
    
    @Override
    public Paint transform(V i) {
      return Color.BLACK;
    }
  }

  class VertexFontTransformer<V> implements Transformer<V, Font> {

    @Override
    public Font transform(V i) {
      return nodeFont;
    }

  }

  class EdgeFontTransformer<E> implements Transformer<E, Font> {

    @Override
    public Font transform(E i) {
      return edgeFont;
    }
  }
  
}
