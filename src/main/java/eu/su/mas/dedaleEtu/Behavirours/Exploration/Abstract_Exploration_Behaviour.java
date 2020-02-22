package eu.su.mas.dedaleEtu.Behavirours.Exploration;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.SimpleBehaviour;
import org.graphstream.graph.Edge;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class Abstract_Exploration_Behaviour extends SimpleBehaviour {

    private static final long serialVersionUID = 8567689731496787661L;

    protected boolean finished = false;
    protected String nearest_open_node = null;

    protected MapRepresentation myMap;
    protected List<String> openNodes;
    protected Set<String> closedNodes;

    protected boolean stop = false;

    public Abstract_Exploration_Behaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap) {
        super(myagent);
        this.myMap=myMap;
        this.openNodes=new ArrayList<String>();
        this.closedNodes=new HashSet<String>();
    }

    public MapRepresentation getMyMap() {
        return myMap;
    }

    public static String seralizeKnowledge(MapRepresentation E){
        JSONObject JS = new JSONObject();
        JS.putAll(E.getGraphData());
        return JS.toJSONString();
    }

    public static MapRepresentation unserialize(String S){
        MapRepresentation M = new MapRepresentation(true);
        JSONObject J = (JSONObject) JSONValue.parse(S);
        HashMap<String,String> nodes  = (HashMap<String, String>) J.get("NODES");
        System.out.println("J'arrive a désérialiser : " + nodes);
        return M;
    }

    public void updateWithKnowledg(JSONObject J) {
        //System.out.println("=====TEST MAJ CONNAISSANCES====");
//
        //System.out.println("Ouverts avant mise a jour de l'agent " + myAgent.getLocalName() + " : " + openNodes);
        //System.out.println("Fermés avant mise a jour de l'agent " + myAgent.getLocalName() + " : " + closedNodes);
        HashMap<String, String> nodes = (HashMap<String, String>) J.get("NODES");
        JSONArray ja = (JSONArray) J.get("EDGES");
        //System.out.println("Données prises en entrée : " );
        LinkedList<String> ouverts_entree = new LinkedList<>();
        LinkedList<String> fermee_entree = new LinkedList<>();
        List<Node> noeuds = this.myMap.getG().nodes().collect(Collectors.toList());
        List<String> id_noeuds = new LinkedList<>();

        for (Node n : noeuds) {
            id_noeuds.add("" + n.getId());
        }

        for (String s : nodes.keySet()) {
                switch (nodes.get(s)) {
                    case "open":
                        ouverts_entree.add(s);
                        if (!(openNodes.contains(s)))
                            openNodes.add(s);
                        break;
                    case "closed":
                        fermee_entree.add(s);
                        closedNodes.add(s);
                        openNodes.remove(s);
                        break;
                    }
                    if(openNodes.contains(s))
                        this.myMap.addNode(s, MapRepresentation.MapAttribute.open);
                    else
                        this.myMap.addNode(s, MapRepresentation.MapAttribute.closed);

            for (int i = 0; i < ja.size(); i++) {
                JSONArray edge = (JSONArray) ja.get(i);
                try{
                    this.myMap.addEdge(edge.get(1).toString(), edge.get(2).toString());
                }catch (ElementNotFoundException E){
                    this.myMap.addNode(edge.get(1).toString(), MapRepresentation.MapAttribute.valueOf(nodes.get(edge.get(1).toString())));
                    try{
                        this.myMap.addEdge(edge.get(1).toString(), edge.get(2).toString());
                    }catch (ElementNotFoundException E2)
                    {
                        this.myMap.addNode(edge.get(2).toString(), MapRepresentation.MapAttribute.valueOf(nodes.get(edge.get(2).toString())));
                        this.myMap.addEdge(edge.get(1).toString(), edge.get(2).toString());
                    }

                }

            }

        }
        //System.out.println("Ouverts en Entrée " + ouverts_entree);
        //System.out.println("Fermés en Entrée : "+ fermee_entree);
        //System.out.println("Ouverts aprés mise a jour de l'agent " + myAgent.getLocalName() + " : " + openNodes);
        //System.out.println("Fermés aprés mise a jour de l'agent " + myAgent.getLocalName() + " : " + closedNodes);
        //System.out.println("==================FIN DU TEST=============");
    }

    public List<String> getOpenNodes() {
        return openNodes;
    }

    protected void updateNodeStatu(){

        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        if(this.myMap==null)
            this.myMap= new MapRepresentation();
        if (myPosition!=null) {
            nearest_open_node = null;
            //List of observable from the agent's current position
            List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe(); //myPosition
            try {
                this.myAgent.doWait(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //1) remove the current node from openlist and add it to closedNodes.
            this.closedNodes.add(myPosition);
            this.openNodes.remove(myPosition);
            this.myMap.addNode(myPosition, MapRepresentation.MapAttribute.closed);

            //2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.

            Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
            while(iter.hasNext()){
                String nodeId=iter.next().getLeft();
                if (!this.closedNodes.contains(nodeId)){
                    if (!this.openNodes.contains(nodeId)){
                        this.openNodes.add(nodeId);
                        this.myMap.addNode(nodeId, MapRepresentation.MapAttribute.open);
                        this.myMap.addEdge(myPosition, nodeId);
                    }else{
                        //the node exist, but not necessarily the edge
                        this.myMap.addEdge(myPosition, nodeId);
                    }
                    if (nearest_open_node ==null) nearest_open_node = nodeId;
                }
            }

            if (this.openNodes.isEmpty()) {
                //Explo finished
                finished = true;
                System.out.println("Exploration successufully done, behaviour removed.");
            }


        }


    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public abstract String howToMove();

    @Override
    public void action(){
        updateNodeStatu();
        String next_pos = howToMove();
        System.out.println("Je suis  : "+myAgent.getLocalName()+" je veux aller en : "+next_pos);
        boolean success = ((AbstractDedaleAgent)this.myAgent).moveTo(next_pos);
        if (!success)
        {
            nearest_open_node = null;
            Collections.shuffle(openNodes);
        }
        System.out.println("Je suis  : "+myAgent.getLocalName()+" je veux aller en : "+next_pos+" et ça donne "+success);
        System.out.println("Ouverts de "+ myAgent.getLocalName()+" : "+ openNodes);
    }



    @Override
    public boolean done(){
        return finished;
    }


}
