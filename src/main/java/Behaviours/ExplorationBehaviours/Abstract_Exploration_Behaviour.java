package Behaviours.ExplorationBehaviours;

import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Abstract_Exploration_Behaviour extends SimpleBehaviour {

    private static final long serialVersionUID = 8567689731496787661L;

    protected boolean finished = false;
    protected String nearest_open_node = null;
    protected Simple_Agent myAgent;
    protected boolean stop = false;

    public Abstract_Exploration_Behaviour(final AbstractDedaleAgent myagent) {
        super(myagent);
        this.myAgent = (Simple_Agent) myagent;

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
        List<Node> noeuds = this.myAgent.getMap().getG().nodes().collect(Collectors.toList());
        List<String> id_noeuds = new LinkedList<>();

        for (Node n : noeuds) {
            id_noeuds.add("" + n.getId());
        }

        for (String s : nodes.keySet()) {
            switch (nodes.get(s)) {
                case "open":
                    ouverts_entree.add(s);
                    if (!(this.myAgent.openNodes.contains(s)))
                        this.myAgent.openNodes.add(s);
                    break;
                case "closed":
                    fermee_entree.add(s);
                    this.myAgent.closedNodes.add(s);
                    this.myAgent.openNodes.remove(s);
                    break;
            }
            if(this.myAgent.openNodes.contains(s))
                this.myAgent.getMap().addNode(s, Map_Representation.MapAttribute.open);
            else
                this.myAgent.getMap().addNode(s, Map_Representation.MapAttribute.closed);

            for (int i = 0; i < ja.size(); i++) {
                JSONArray edge = (JSONArray) ja.get(i);
                try{
                    this.myAgent.getMap().addEdge(edge.get(1).toString(), edge.get(2).toString());
                }catch (ElementNotFoundException E){
                    this.myAgent.getMap().addNode(edge.get(1).toString(), Map_Representation.MapAttribute.valueOf(nodes.get(edge.get(1).toString())));
                    try{
                        this.myAgent.getMap().addEdge(edge.get(1).toString(), edge.get(2).toString());
                    }catch (ElementNotFoundException E2)
                    {
                        this.myAgent.getMap().addNode(edge.get(2).toString(), Map_Representation.MapAttribute.valueOf(nodes.get(edge.get(2).toString())));
                        this.myAgent.getMap().addEdge(edge.get(1).toString(), edge.get(2).toString());
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

    protected void updateNodeStatu(){

        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        if(this.myAgent.getMap()==null)
        {
            this.myAgent.setMap(new Map_Representation());
            this.myAgent.setExploratory_behaviour(this);
        }

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
            this.myAgent.closedNodes.add(myPosition);
            this.myAgent.openNodes.remove(myPosition);

            this.myAgent.getMap().addNode(myPosition, Map_Representation.MapAttribute.closed);

            //2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.

            Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
            while(iter.hasNext()){
                String nodeId=iter.next().getLeft();
                if (!this.myAgent.closedNodes.contains(nodeId)){
                    if (!this.myAgent.openNodes.contains(nodeId)){
                        this.myAgent.openNodes.add(nodeId);
                        this.myAgent.getMap().addNode(nodeId, Map_Representation.MapAttribute.open);
                        this.myAgent.getMap().addEdge(myPosition, nodeId);
                    }else{
                        //the node exist, but not necessarily the edge
                        this.myAgent.getMap().addEdge(myPosition, nodeId);
                    }
                    if (nearest_open_node ==null) nearest_open_node = nodeId;
                }
            }

            if (this.myAgent.openNodes.isEmpty()) {
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
        Simple_Agent SA = (Simple_Agent) myAgent;
        for (String s:SA.getActive_conversations().keySet())
            if(SA.getActive_conversations().get(s) > 0)
                SA.getActive_conversations().put(s,SA.getActive_conversations().get(s)-1);
        updateNodeStatu();
        String next_pos = howToMove();
        try{
            boolean success = ((AbstractDedaleAgent)this.myAgent).moveTo(next_pos);
            while(!success){
                next_pos = howToMove();
                success = ((AbstractDedaleAgent)this.myAgent).moveTo(next_pos);
                nearest_open_node = null;

                //System.out.println("Je shuffle et donc");
                //System.out.println("La liste deviens : "+this.myAgent.openNodes);
                Collections.shuffle(this.myAgent.openNodes);
            }
        }catch (RuntimeException E){
            finished = true;
        }

    }



    @Override
    public boolean done(){
        return finished;
    }


}
