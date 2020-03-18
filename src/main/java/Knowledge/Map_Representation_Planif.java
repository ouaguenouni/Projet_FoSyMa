package Knowledge;

import dataStructures.tuple.Couple;
import javafx.application.Platform;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Map_Representation_Planif extends Map_Representation {

    //Données lues
    private HashMap<Integer, Couple<Integer,Integer>> arcs = new HashMap<>();
    private HashMap<Integer,String > noeuds = new HashMap<>();

    //Données calculées
    private HashMap<Integer, LinkedList<Integer>> successeurs = new HashMap<>();
    private HashMap<Integer, Double> values  = new HashMap<>();
    private HashMap<Integer, Boolean> penality  = new HashMap<>();
    private static double lambda = 0.9;


    int nbEdges=0;
    private LinkedList<LinkedList<Integer>> plans;


    public Map_Representation_Planif(){
        super();
        Graph g = super.getG();
        //Reinitialiser toutes les collections.
        arcs = new HashMap<>();
        noeuds = new HashMap<>();
        successeurs = new HashMap();
        values  = new HashMap<>();
        penality  = new HashMap<>();
        Iterator<Node> It = g.iterator();
        //Ajouter tout les noeuds
        while(It.hasNext())
        {
            Node N = It.next();
            String Nodeid = N.getId();
            addNode(Nodeid,MapAttribute.valueOf( N.getAttribute("ui.class").toString()));
            noeuds.put(Integer.valueOf(Nodeid),Nodeid);
            if(N.getAttribute("ui.class").toString().equals("open")){
                values.put(N.getIndex(),10.0);
            }
            else{
                values.put(N.getIndex(),0.0);
            }
        }
        Iterator<Edge> I = g.edges().iterator();
        while (I.hasNext()){
            Edge E = I.next();
            String src = E.getSourceNode().getId();
            String dst = E.getTargetNode().getId();
            addEdge(src,dst);
            arcs.put(E.getIndex(),new Couple<Integer, Integer>(Integer.parseInt(src),Integer.parseInt(dst)));
        }
        Platform.runLater(() -> {
            super.openGui();
        });
    }
}
