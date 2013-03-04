/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 Gephi is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 Gephi is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame;
import org.gephi.io.generator.plugin.DynamicGraph;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import processing.core.PApplet;

//import org.openide.util.ToolUI; 


/**
 * Demo how to generate a graph with generators. <p> The code shows how to use
 * <code>RandomGraph</code> and
 * <code>WattsStrogatz</code> generators and push result into the graph
 * structure using
 * <code>ImportController</code>. <p> In Gephi import and generate are not
 * directly appened to the main graph structure for consistency reasons. New
 * data are pushed in a
 * <code>Container</code> and then appened to the graph structure with the help
 * of a
 * <code>Processor</code>. <p> In this demo, two workspaces are created.
 * Manipulate workspaces from
 * <code>ProjectController</code>
 *
 * @author Mathieu Bastian
 */
public class Gephi_Plugin {
    
    private String file1 = "Resources/karate.gml";
    private String file2 = "Resources/LesMiserables.gexf";
    private String file3 = "Resources/internet_routers-22july06.gml";
    private String file4 = "Resources/guo_fb.gexf";
    
    public void script() {
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();
        
        //----------------------------------------------------------------------------------------------

        /*
         * Import File
         */
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            
            /*
             *  Gets file~!
             */
            File file = Utilities.toFile(getClass().getResource(file4).toURI());
        	//URL url = getClass().getResource(file4);
        	//File file = new File(url.getPath());
        	
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
            container.setAllowAutoNode(false);  //Don’t create missing nodes
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        /*
         * Append imported data to GraphAPI
         */
        importController.process(container, new DefaultProcessor(), workspace);
        
        
        //-----------------------------------------------------------------------------------------------


        /*
         * Preview configuration
         */
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
        previewController.refreshPreview();

        /*
         * New Processing target, get the PApplet
         */
        ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
        PApplet applet = target.getApplet();
        applet.init();
        
        /*
         * Add the applet to a JFrame and display
         */
        JFrame frame = new JFrame("Test Preview");
        frame.setLayout(new BorderLayout());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(applet, BorderLayout.CENTER);
        
        frame.pack();
        frame.setVisible(true);
        
        /*
         * Refresh the preview and reset the zoom
         */
        previewController.render(target);
        target.refresh();
        target.resetZoom();
        
    }
    
    public static void main(String args[]) {
        Gephi_Plugin obj = new Gephi_Plugin();
        obj.script();
    }
}
