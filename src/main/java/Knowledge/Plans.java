package Knowledge;

import dataStructures.serializableGraph.SerializableNode;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import javafx.application.Platform;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.view.Viewer;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class Plans implements Serializable {

    private transient Graph graph;
    //Données lues
    private HashMap<Integer, Couple<Integer,Integer>> arcs = new HashMap<>();
    private HashMap<Integer,String > noeuds = new HashMap<>();

    //Données calculées
    private HashMap<Integer, LinkedList<Integer>> successeurs = new HashMap<>();
    private HashMap<Integer, Double> values  = new HashMap<>();
    private HashMap<Integer, Boolean> penality  = new HashMap<>();
    private static double lambda = 0.9;

    //Utiles pour l'affichage
    private String defaultNodeStyle= "node {"+"fill-color: black;"+" size-mode:fit;text-alignment:under; text-size:14;text-color:white;text-background-mode:rounded-box;text-background-color:black;}";
    private String nodeStyle_open = "node.positif {"+"fill-color: forestgreen;"+"}";
    private String nodeStyle_agent = "node.negatif {"+"fill-color: red;"+"}";
    private String nodeStyle=defaultNodeStyle+nodeStyle_agent+nodeStyle_open;

    int nbEdges=0;
    private LinkedList<LinkedList<Integer>> plans;

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
        if (MR == null)
            return;
        graph = MR.getG();
        Graph g = graph;
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
            addNode(Nodeid);
            noeuds.put(Integer.valueOf(Nodeid),Nodeid);
            if(N.getAttribute("ui.class") != null && N.getAttribute("ui.class").toString().equals("open")){
                values.put(N.getIndex(),10.0);
            }
            else{
                values.put(N.getIndex(),0.0);
            }
        }
        Iterator<Edge> I = g.edges().iterator();
        while (I.hasNext()){
            Edge E = I.next();
            if(E == null)
            {
                System.out.println("NULL");
                continue;
            }
            String src = E.getSourceNode().getId();
            String dst = E.getTargetNode().getId();
            graph.removeEdge(E.getId());
            addEdge(src,dst);
            arcs.put(Integer.valueOf(E.getId()),new Couple<Integer, Integer>(Integer.parseInt(src),Integer.parseInt(dst)));
        }
        run();
    }

    public void addNode(String id){
        Node n;
        System.out.println("Graphe = "+this.graph);
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
                n.setAttribute("ui.label",id + " : " + s.substring(0,4));
            else
                n.setAttribute("ui.label",id + " : " + s);
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
        }catch (EdgeRejectedException | IdAlreadyInUseException e ){
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

    public LinkedList<LinkedList<Integer>> genererPlans(int source)
    {
        LinkedList<LinkedList<Integer>> plans = new LinkedList<>();
        boolean stop = false;
        LinkedList<Integer> tmp = new LinkedList<>();
        Stack<LinkedList<Integer>> pistes = new Stack<>();
        LinkedList<Integer> explored = new LinkedList<>();
        tmp.add(source);
        pistes.push(tmp);
        while(!pistes.empty()){
            //Dernier chemin empilé
            tmp = pistes.pop();
            //On récupére le dernier élément du chemin
            Integer last = tmp.getLast();
            //On l'ajoute aux explorés
            explored.add(last);
            //On énumère ces successeurs
            LinkedList<Integer> possibilites = successeurs.get(last);
            if(possibilites == null)
                return plans;
            //On calcule la valeur maximale des successeurs qui ne sont pas explorés pour pas créer de cycles
            double max = -100;
            //Cette variable sert a ce que si le noeud n'a pas de successeurs non explorés ce qui signie que le chemin est fini on l'ajoute au plans
            boolean no_successors = true;
            for (Integer possibilite : possibilites) {
                if (!(explored.contains(possibilite))) {
                    no_successors = false;
                    if (values.get(possibilite) > max) {
                        max = values.get(possibilite);
                    }
                }
            }
            if(no_successors){
                //Chemin sans successeurs on l'ajoute aux plans
                //System.out.println("Ajout du plan : "+tmp);
                plans.add(tmp);
            }
            else{
                final double m = max;
                //On efface les successeurs dont la valeur est inférieure au maximum
                possibilites.removeIf(integer -> values.get(integer)<m);
                //On génère des chemins en accumulant le possibilités intéressantes au chemin qu'on avait dépilé et on les empilent
                LinkedList<Integer> pos;
                for (Integer p:possibilites) {
                    //si le chemin intéressant en question est mieux que le chemin courant on l'ajoute et on empile
                    if(values.get(p) > values.get(last))
                    {
                        pos = new LinkedList<>(tmp);
                        pos.add(p);
                        System.out.println("Empilement de : "+p);
                        pistes.push(pos);
                    }
                    //Sinon on considére que le chemin est terminé et par conséquent on l'enregistre
                    else{
                        //Chemin sans successeurs on l'ajoute aux plans
                        //System.out.println("Ajout du plan : "+tmp);
                        if(!(plans.contains(tmp)))
                            plans.add(tmp);
                    }
                }
            }

            }
        //Boucle d'affichage servant au test :
        //System.out.println("Liste des plans : ");
        //for (LinkedList plan:plans){
        //    System.out.println(plan);
        //}
        this.plans = plans;
        return this.plans;
    }



    public static void main(String[] args) throws IOException {
        System.out.println("Hello");
        Plans P = new Plans();
        P.loadFromTxt("/home/mohamed/Bureau/Projets/Dedale_Project/resources/topology/BinaryTree1");
        System.out.println(P.arcs);
        System.out.println(P.noeuds);
        P.genererSuccesseurs();
        for (Integer i:P.successeurs.keySet()){
            System.out.println(i + " : "+ P.successeurs.get(i));
        }
        P.updateValue("13",10);
        P.updateValue("14",10);
        P.updateValue("15",10);
        P.updateValue("16",10);

        //P.setPenality("2",true);

        //P.updateValue("2",-5);
        P.run();
        Scanner sc = new Scanner(System.in);
        //for (int i = 0; i < 100; i++)
        //{
        //    System.out.println("Appuyez pour propager");
        //    String str = sc.nextLine();
        //    P.propagerValeurs();
        //}
        P.calculerPlans();
        System.out.println(P.genererPlans(0));
    }

    /**
     * A node is open, closed, or agent
     * @author hc
     *
     */

    public enum MapAttribute {
        agent,open,closed
    }

    private static final long serialVersionUID = -1333959882640838272L;

    /*********************************
     * Parameters for graph rendering
     ********************************/


    private Viewer viewer; //ref to the display,  non serializable

    private SerializableSimpleGraph<String, Map_Representation.MapAttribute> sg;//used as a temporary dataStructure during migration



    public SerializableSimpleGraph<String, Map_Representation.MapAttribute> getSg() {
        return sg;
    }

    /**
     * Add or replace a node and its attribute
     * @param id Id of the node
     * @param mapAttribute associated state of the node
     */
    public void addNode(String id, Map_Representation.MapAttribute mapAttribute){
        Node n;
        if (this.graph.getNode(id)==null){
            n=this.graph.addNode(id);
        }else{
            n=this.graph.getNode(id);
        }
        n.clearAttributes();
        n.setAttribute("ui.class", mapAttribute.toString());
        n.setAttribute("ui.label",id);
    }

    /**
     * Before the migration we kill all non serializable components and store their data in a serializable form
     */
    public void prepareMigration(){
        this.sg= new SerializableSimpleGraph<String, Map_Representation.MapAttribute>();
        Iterator<Node> iter=this.graph.iterator();
        while(iter.hasNext()){
            Node n=iter.next();
            sg.addNode(n.getId(), Map_Representation.MapAttribute.valueOf((String) n.getAttribute("ui.class")));
            System.out.println("Ajout au graphe d'un noeud en "+n.getId());
        }
        Iterator<Edge> iterE=this.graph.edges().iterator();
        while (iterE.hasNext()){
            Edge e=iterE.next();
            Node sn=e.getSourceNode();
            Node tn=e.getTargetNode();
            System.out.println("Ajout au graphe d'un arc en "+sn.getId()+ "-" + tn.getId());
            sg.addEdge(e.getId(), sn.getId(), tn.getId());
            System.out.println("Juste pour voire : "+sg.getEdges(sn.getId()) + " - " + sg.getEdges(sn.getId()));
        }
        closeGui();
        this.graph=null;
    }

    public Graph getG() {
        return graph;
    }

    public HashMap<String,Object> getGraphData(){

        HashMap<String,Object> data = new HashMap<>();


        HashMap<String,String> nodes = new HashMap<>();

        Iterator<Node> iter=this.graph.iterator();
        while(iter.hasNext()){
            Node n=iter.next();
            nodes.put(n.getId(),n.getAttribute("ui.class").toString());
        }

        LinkedList<LinkedList<String> > edges = new LinkedList<>();
        Iterator<Edge> iterE=this.graph.edges().iterator();
        while (iterE.hasNext()){
            Edge e=iterE.next();
            Node sn=e.getSourceNode();
            Node tn=e.getTargetNode();
            LinkedList<String> edge = new LinkedList<>();
            edge.add(e.getId());
            edge.add(sn.getId());
            edge.add(tn.getId());
            edges.add(edge);
        }

        data.put("NODES",nodes);
        data.put("EDGES",edges);

        return data;

    }


    public static String seralizeKnowledge(Map_Representation E){
        JSONObject JS = new JSONObject();
        JS.putAll(E.getGraphData());
        return JS.toJSONString();
    }




    /**
     * After migration we load the serialized data and recreate the non serializable components (Gui,..)
     */
    public void loadSavedData(){

        this.graph= new SingleGraph("My world vision");
        this.graph.setAttribute("ui.stylesheet",nodeStyle);

        openGui();

        Integer nbEd=0;
        for (SerializableNode<String, Map_Representation.MapAttribute> n: this.sg.getAllNodes()){
            this.graph.addNode( n.getNodeId()).setAttribute("ui.class", n.getNodeContent().toString() );
            System.out.println( "ça merde pour l'id "+ n.getNodeId() );
            for(String s:this.sg.getEdges(n.getNodeId())){
                this.graph.addEdge(nbEd.toString(),n.getNodeId(),s);
                nbEd++;
            }
        }
        System.out.println("Loading done");
    }

    /**
     * Method called before migration to kill all non serializable graphStream components
     */
    private void closeGui() {
        //once the graph is saved, clear non serializable components
        if (this.viewer!=null){
            try{
                this.viewer.close();
            }catch(NullPointerException e){
                System.err.println("Bug graphstream viewer.close() work-around - https://github.com/graphstream/gs-core/issues/150");
            }
            this.viewer=null;
        }
    }




    /**
     * Method called after a migration to reopen GUI components
     */
    public void openGui() {
        this.viewer =new FxViewer(this.graph, FxViewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);////GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(FxViewer.CloseFramePolicy.CLOSE_VIEWER);
        viewer.addDefaultView(true);
        graph.display();
    }


}
