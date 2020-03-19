package Knowledge;

import dataStructures.tuple.Couple;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Filtrage implements Serializable {

    private transient Graph graph;
    //Données lues
    private HashMap<Integer, Couple<Integer,Integer>> arcs = new HashMap<>();
    private HashMap<Integer,String > noeuds = new HashMap<>();
    private HashMap<Integer, LinkedList<Integer>> successeurs = new HashMap<>();
    //Données calculées
    private double[][] transitions;
    private HashMap<String,double[]> evidence = new HashMap<>();
    //Utiles pour l'affichage
    private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
    private String nodeStyle_open = "node.positif {"+"fill-color: forestgreen;"+"}";
    private String nodeStyle_agent = "node.negatif {"+"fill-color: red;"+"}";
    private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

    int nbEdges=0;
    private LinkedList<LinkedList<Integer>> plans;

    public Filtrage(){
        this.graph= new SingleGraph("My world vision");

        this.graph.setAttribute("ui.stylesheet",nodeStyle);
    }

    public void evaluation(){

    }


    public void addEdge(String idNode1,String idNode2){
        try {
            this.nbEdges++;
            this.graph.addEdge(this.nbEdges+"", idNode1, idNode2);
        }catch (EdgeRejectedException | IdAlreadyInUseException e ){
            //Do not add an already existing one
            this.nbEdges--;
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
        //n.setAttribute("ui.label",val);
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


    public void calculerMatriceAdjacence(){
        int n = new LinkedList<>(successeurs.keySet()).getLast()+1;

        transitions = new double[n][n];
        int N = successeurs.keySet().size()*successeurs.keySet().size();
        for (Couple<Integer,Integer> arc:arcs.values()){
            transitions[arc.getLeft()][arc.getRight()] = 1;
            transitions[arc.getRight()][arc.getLeft()] = 1;
        }
        for (int i = 0; i< transitions.length; i++){
            double sum = 0;
            for (int j = 0; j< transitions[i].length; j++)
            {
                sum = sum + transitions[i][j];
            }
            for (int j = 0; j< transitions[i].length; j++)
            {
                transitions[i][j] = transitions[i][j] / sum;
            }
        }

    }

    public void ajouterEvidence(String s,int ... evnts){
        evidence.put(s,new double[successeurs.keySet().size()]);
        for (int j:evnts){
            evidence.get(s)[j] = 1;
        }
    }

    public void afficherAdjacence(){
        for (double[] doubles : transitions) {
            double sum = 0;
            for (double aDouble : doubles) {
                System.out.print(aDouble + "\t");
            }
            System.out.print("\n");
        }
    }

    public void afficherEvidence(){
        for (String ag:evidence.keySet())
        {
            double[] ev = evidence.get(ag);
            System.out.println("Agent : "+ag);
            double cpt = 0;
            for (double aDouble:ev){
                System.out.print(aDouble + "\t");
                cpt = cpt + aDouble;
            }
            System.out.print("\n");
            System.out.println("Somme : "+cpt);
        }

    }

    public void miseAJourEvidence(String s){
        double[] resultat = new double[transitions[0].length];
        for (int i = 0; i< transitions.length; i++){
            resultat[i] = 0;
            for (int j = 0; j< transitions[i].length; j++)
            {
                resultat[i] = resultat[i]  + transitions[j][i] * evidence.get(s)[j];
            }
        }
        evidence.put(s,resultat);
    }

    public void miseAJourEvidences(int delta){
        for(int i=0;i<delta;i++) {
            LinkedList<String> L = new LinkedList<>(evidence.keySet());
            graph.setAttribute(i + "", L.get(0));
            for (String s : evidence.keySet()) {
                miseAJourEvidence(s);
            }
        }

    }

    public void displayEvidence(String s){
        double[] evd = evidence.get(s);
        for(int i=0;i<evd.length;i++)
        {
            Node n = graph.getNode(i);
            if(n == null)
                System.out.println("Bug, n est null pour i = "+i);
            n.setAttribute("ui.label",i + " : "+ n.getId());
            if(evd[i] > 0)
            {
                n.setAttribute("ui.class","positif");
            }
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



    public static void main(String[] args) throws IOException {
        System.out.println("Hello");
        Filtrage P = new Filtrage();
        P.loadFromTxt("/home/mohamed/Bureau/Projets/Dedale_Project/resources/topology/map2018-topology-ica");
        System.out.println(P.arcs);
        System.out.println(P.noeuds);
        P.genererSuccesseurs();
        for (Integer i:P.successeurs.keySet()){
            System.out.println(i + " : "+ P.successeurs.get(i));
        }

        P.calculerMatriceAdjacence();
        System.out.println("Adjacence : ");
        P.afficherAdjacence();


        Scanner sc = new Scanner(System.in);

        P.ajouterEvidence("Agent1",0);
        P.run();
        for (int i=0;i<20000;i++)
        {
            P.displayEvidence("Agent1");
            System.out.println("Appuyez sur <Entrée> pour continuer :");
            sc.nextLine();
            P.miseAJourEvidences(1);
        }


    }


}
