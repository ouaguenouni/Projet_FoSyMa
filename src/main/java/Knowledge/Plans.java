package Knowledge;

import dataStructures.tuple.Couple;
import javafx.application.Platform;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class Plans {

    private Graph graph;
    //Données lues
    private HashMap<Integer, Couple<Integer,Integer>> arcs = new HashMap<>();
    private HashMap<Integer,String > noeuds = new HashMap<>();
    //Données calculées
    private HashMap<Integer, LinkedList<Integer>> successeurs = new HashMap<>();
    private HashMap<Integer, Double> values  = new HashMap<>();
    private HashMap<Integer, Boolean> penality  = new HashMap<>();

    private static double lambda = 0.9;
    private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
    private String nodeStyle_open = "node.positif {"+"fill-color: forestgreen;"+"}";
    private String nodeStyle_agent = "node.negatif {"+"fill-color: red;"+"}";
    private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

    int nbEdges=0;

    public Plans(){
        this.graph= new SingleGraph("My world vision");

        this.graph.setAttribute("ui.stylesheet",nodeStyle);
    }

    public void evaluation(){

    }

    public void loadFromTxt(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line;
        Pattern pattern1  = Pattern.compile("ae e(\\d+)\t(\\d+) (\\d+)( )?") ;
        Matcher matcher1;
        Pattern pattern2 = Pattern.compile("an (\\d+)\tlabel:( )?(\\w+)");
        Matcher matcher2 ;

        while ((line = in.readLine()) != null)
        {
            matcher1 = pattern1.matcher(line);
            matcher2 = pattern2.matcher(line);
            //System.out.println(line);
            if(matcher1.matches())
            {
                arcs.put(Integer.parseInt(matcher1.group(1)),new Couple<>(Integer.parseInt(matcher1.group(2)),Integer.parseInt(matcher1.group(3))));

            }
            else
            if(matcher2.matches())
            {
                noeuds.put(Integer.parseInt(matcher2.group(1)),matcher2.group(3));
                addNode(matcher2.group(3));
            }
            else
            {
                System.out.println("Ligne non reconnue : "+line);
            }
            for (Couple<Integer,Integer> i:arcs.values()){
                addEdge(""+i.getLeft(),""+i.getRight());
            }
            // Afficher le contenu du fichier
        }
        in.close();
    }

    public void genererSuccesseurs(){
        for (Integer i:noeuds.keySet())
        {
            successeurs.put(i,new LinkedList<>());
        }
        for (Couple <Integer,Integer> c:arcs.values())
        {
            if(!successeurs.get(c.getLeft()).contains(c.getRight()))
            {
                successeurs.get(c.getLeft()).add(c.getRight());
            }
            if(!successeurs.get(c.getRight()).contains(c.getLeft()))
            {
                successeurs.get(c.getRight()).add(c.getLeft());
            }
        }
    }



    public void remplirGraphe(Map_Representation MR){
        Graph g = MR.getG();
        Iterator<Node> It = g.iterator();
        while(It.hasNext())
        {
            String Nodeid = It.next().getId();
            addNode(Nodeid);
            noeuds.put(Integer.valueOf(Nodeid),Nodeid);
        }
        Iterator<Edge> I = g.edges().iterator();
        while (I.hasNext()){
            Edge E = I.next();
            String src = E.getSourceNode().getId();
            String dst = E.getTargetNode().getId();
            addEdge(src,dst);
            arcs.put(E.getIndex(),new Couple<Integer, Integer>(Integer.parseInt(src),Integer.parseInt(dst)));
        }
    }

    public void addNode(String id){
        Node n;
        if (this.graph.getNode(id)==null){
            n=graph.addNode(id);
        }else{
            n=graph.getNode(id);
        }
        n.clearAttributes();
        this.values.put(Integer.valueOf(id),0.0);
        this.penality.put(Integer.valueOf(id),false);
        //n.setAttribute("ui.label",val);
    }

    public void updateValue(String id, double val){
        Node n;
        values.put(Integer.parseInt(id),val);
        if (!(this.graph.getNode(id)==null)) {
            n = graph.getNode(id);
            if(val > 0)
            {
                n.setAttribute("ui.class", "positif");
            }
            if(val < 0)
            {
                n.setAttribute("ui.class", "negatif");
            }
            String s = Double.toString(val);
            if(s.length() > 3)
                n.setAttribute("ui.label",s.substring(0,4));
            else
                n.setAttribute("ui.label",s);
        }



    }

    public void setPenality(String id,boolean penality) {
        this.penality.put(Integer.valueOf(id),penality);
    }

    /**
     * Add the edge if not already existing.
     * @param idNode1 one side of the edge
     * @param idNode2 the other side of the edge
     */
    public void addEdge(String idNode1,String idNode2){
        try {
            this.nbEdges++;
            this.graph.addEdge(this.nbEdges+"", idNode1, idNode2);
        }catch (EdgeRejectedException e){
            //Do not add an already existing one
            this.nbEdges--;
        }

    }

    public void run(){
        System.setProperty("org.graphstream.ui", "javafx");
        Viewer viewer =new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);////GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(FxViewer.CloseFramePolicy.CLOSE_VIEWER);
        viewer.addDefaultView(true);
        graph.display();
    }

    public void propagerValeurs(){
        for (Integer i:successeurs.keySet()){
            LinkedList<Integer> S = successeurs.get(i);
            double m = values.get(S.get(0))*lambda;
            for (int j=1;j<S.size();j++)
            {
                double v = values.get(S.get(j))*lambda;;
                if(v>m)
                {
                    m = v;
                }
            }
            if((m > values.get(i)) && !penality.get(i))
                updateValue(i+"",m);
        }
    }

    public void calculerPlans(){
        HashMap<Integer,Double> old_values;
        do {
            old_values= new HashMap<>();
            old_values.putAll(values);
            propagerValeurs();
            //System.out.println("Comparaison  : ");
            //for (Integer i:old_values.keySet()){
            //    System.out.println(i + " : h1-"+old_values.get(i)+" h2-"+values.get(i));
            //}
        }
        while(difference(values,old_values) > 0.000001);
    }

    public static double difference(HashMap<Integer,Double> h1 , HashMap<Integer,Double> h2){
        double cpt = 0.0;
        for (Integer i : h1.keySet()){
            cpt = cpt + (Math.max(h1.get(i),h2.get(i)) - Math.min(h1.get(i),h2.get(i)));
        }

        return cpt;
    }

    public void normaliser(){
        double d = 0;
        for (Integer i : values.keySet())
        {
            d = d + values.get(i);
        }
        for (Integer i : values.keySet())
        {
            values.put(i,values.get(i)/d);
            updateValue(i+"",values.get(i));
        }

    }


    public static void main(String[] args) throws IOException {
        System.out.println("Hello");
        Plans P = new Plans();
        P.loadFromTxt("/home/mohamed/Bureau/Projets/Dedale_Project/resources/topology/map2019-topologyExam1");
        System.out.println(P.arcs);
        System.out.println(P.noeuds);
        P.genererSuccesseurs();
        for (Integer i:P.successeurs.keySet()){
            System.out.println(i + " : "+ P.successeurs.get(i));
        }
        P.updateValue("10",10);
        P.updateValue("8",-3);

        P.setPenality("8",true);
        P.setPenality("2",true);
        P.setPenality("106",true);

        P.updateValue("2",-5);
        P.updateValue("106",-10);
        P.run();
        Scanner sc = new Scanner(System.in);
        //for (int i = 0; i < 100; i++)
        //{
        //    System.out.println("Appuyez pour propager");
        //    String str = sc.nextLine();
        //    P.propagerValeurs();
        //}
        P.calculerPlans();


    }


}
