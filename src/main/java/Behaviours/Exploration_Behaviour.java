package Behaviours;

import Agents.Simple_Cognitif_Agent;
import Knowledge.Knowledg_Base;
import Knowledge.MapRepresentation;
import Knowledge.PolicyEvaluationExploration;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.OneShotBehaviour;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.system.Txn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

public class Exploration_Behaviour extends OneShotBehaviour {

    /**
     * Current knowledge of the agent regarding the environment
     */
    private Knowledg_Base myMap;
    public Simple_Cognitif_Agent myAgent;
    public PolicyEvaluationExploration policyEvaluationExploration = null;
    public boolean wumpusFound = false;



    /**
     * Nodes known but not yet visited
     */
    private List<String> openNodes;
    /**
     * Visited nodes
     */
    private Set<String> closedNodes;
    private boolean finished;


    public Exploration_Behaviour(final AbstractDedaleAgent myagent, Knowledg_Base myMap) {
        super(myagent);
        myAgent = (Simple_Cognitif_Agent) myagent;
        this.myMap=myMap;
        this.openNodes=new ArrayList<String>();
        this.closedNodes=new HashSet<String>();
    }

    public String processPlans(){
        if(policyEvaluationExploration == null)
        {
            policyEvaluationExploration = new PolicyEvaluationExploration(this.myAgent);
        }
        updateNodeStates();
        String next_step = policyEvaluationExploration.getNextStep();
        //System.out.println("Next move : "+next_step);
        //Scanner sc = new Scanner(System.in);
        //sc.nextLine();
        return next_step;
    }



    public void perceptionUpdates(String nextNode){
        this.myAgent.Kb.updateCount(this.myAgent,this.myAgent.stepCountor);
        this.myAgent.stepCountor = this.myAgent.stepCountor + 1;
        this.myAgent.Kb.assertKnowledg(myAgent);
        this.myAgent.Kb.updateAgentPosition(myAgent.getLocalName(),nextNode);


    }

    public void updateNodeStates() {
        //0) Retrieve the current position
        String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();

        if (myPosition != null) {
            //List of observable from the agent's current position
            List<Couple<String, List<Couple<Observation, Integer>>>> lobs = ((AbstractDedaleAgent) this.myAgent).observe();//myPosition

            /**
             * Just added here to let you see what the agent is doing, otherwise he will be too quick
             */
            try {
                this.myAgent.doWait(200);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //1) remove the current node from openlist and add it to closedNodes.
            this.closedNodes.add(myPosition);
            this.openNodes.remove(myPosition);

            this.myMap.addNode(myPosition, MapRepresentation.MapAttribute.closed);

            //2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
            Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter = lobs.iterator();
            while (iter.hasNext()) {
                String nodeId = iter.next().getLeft();
                if (!this.closedNodes.contains(nodeId)) {
                    if (!this.openNodes.contains(nodeId)) {
                        this.openNodes.add(nodeId);
                        this.myMap.addNode(nodeId, MapRepresentation.MapAttribute.open);
                        this.myMap.addEdge(myPosition, nodeId);
                    } else {
                        //the node exist, but not necessarily the edge
                        this.myMap.addEdge(myPosition, nodeId);
                    }
                }
            }

        }
    }

    public static boolean containsStench(List<Couple<Observation, Integer>> L){
        for (Couple<Observation, Integer> c:L)
        {
            if(c.getLeft().getName().equalsIgnoreCase("stench"))
                return true;
        }
        return false;
    }


    public LinkedList<String> seekForStench(){
        Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=myAgent.observe().iterator();
        LinkedList<String> L = new LinkedList<>();
        while(iter.hasNext())
        {
            Couple<String, List<Couple<Observation, Integer>>> observation_node = iter.next();
            //System.out.println("Noeud : " + observation_node.getLeft());
            //System.out.println("Liste d'observations : "+ observation_node.getRight()+ " : "+ containsStench(observation_node.getRight()));
            if(containsStench(observation_node.getRight()))
            {
                this.myAgent.Kb.addPerceptions(myAgent,observation_node.getLeft(),"Stench");
                L.add(observation_node.getLeft());
            }
        }
        return L;
    }

    public boolean seekForWumpus(){
        if(!seekForStench().isEmpty())
            return true;
        Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=myAgent.observe().iterator();
        LinkedList<String> L = new LinkedList<>();
        String query2  = "PREFIX :  <http://www.co-ode.org/ontologies/ont.owl#>\n" +
                "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX rdfs:   <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?lab1 \n" +
                "WHERE    { "+
                " ?n1 rdf:type :Node . " +
                " ?a1 :indicateStenche ?n1 . " +
                " ?a1 rdf:type :Agent ." +
                " ?n1 rdfs:label ?lab1 . " +
                "}";
        try ( RDFConnection conn = RDFConnectionFactory.connect(DatasetFactory.create(myAgent.Kb.beliefs)) ) {
            Txn.executeWrite(conn, () -> {
                ResultSet rs = conn.query(query2).execSelect();
                while(rs.hasNext()){
                    QuerySolution qs = rs.next();
                    //System.out.println("Récupérer : "+qs.get("lab1"));
                    L.add(qs.get("lab1").toString());
                }
            });
        }
        return !L.isEmpty();
    }


    @Override
    public void action() {

        if(this.myMap==null)
        {
            this.myMap= new Knowledg_Base();
            this.myAgent.Kb = this.myMap;
            this.myMap.updateAgents(myAgent);
        }
        updateNodeStates();
        String myPosition = ((AbstractDedaleAgent) this.myAgent).getCurrentPosition();
        perceptionUpdates(myPosition);
        //3) while openNodes is not empty, continues.
        if (this.openNodes.isEmpty()){
            //Explo finished
            finished=true;
            //TODO : Cas particulier a gérer
            System.out.println("Exploration successufully done, behaviour removed.");
        }else{
                String nextNode = processPlans();
            boolean success = false;
                try{
                     success = ((AbstractDedaleAgent)this.myAgent).moveTo(nextNode);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                while (!success) {
                    policyEvaluationExploration.penality.clear();
                    policyEvaluationExploration.addPenalised(nextNode);
                    nextNode = processPlans();
                    try {
                        success = ((AbstractDedaleAgent) this.myAgent).moveTo(nextNode);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
        }
        wumpusFound = seekForWumpus();
    }

    @Override
    public int onEnd() {
        if(wumpusFound)
            return 3;
        return 0;
    }
}
