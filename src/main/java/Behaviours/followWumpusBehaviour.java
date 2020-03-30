package Behaviours;

import Agents.Simple_Cognitif_Agent;
import Knowledge.MapRepresentation;
import Knowledge.PolicyEvaluationHunting;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.SimpleBehaviour;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class followWumpusBehaviour extends SimpleBehaviour {

    public Simple_Cognitif_Agent myAgent;
    public PolicyEvaluationHunting policyEvaluationHunting = null;


    public followWumpusBehaviour(Simple_Cognitif_Agent myAgent){
        this.myAgent = myAgent;
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
            this.myAgent.Kb.addNode(myPosition, MapRepresentation.MapAttribute.closed);
            //2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
            Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter = lobs.iterator();
            while (iter.hasNext()) {
                String nodeId = iter.next().getLeft();
                this.myAgent.Kb.addNode(nodeId, MapRepresentation.MapAttribute.open);
                this.myAgent.Kb.addEdge(myPosition, nodeId);
                //the node exist, but not necessarily the edge
                this.myAgent.Kb.addEdge(myPosition, nodeId);
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



    public LinkedList<String> seekForStench()  {
        Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=myAgent.observe().iterator();
        LinkedList<String> L = new LinkedList<>();
        while(iter.hasNext())
        {
            Couple<String, List<Couple<Observation, Integer>>> observation_node = iter.next();
            //System.out.println("Noeud : " + observation_node.getLeft());
            //ystem.out.println("Liste d'observations : "+ observation_node.getRight()+ " : "+ containsStench(observation_node.getRight()));
            if(containsStench(observation_node.getRight()))
            {
                this.myAgent.Kb.addPerceptions(myAgent,observation_node.getLeft(),"Stench");
                this.myAgent.stepCountor = this.myAgent.stepCountor + 1;
                try {
                    this.myAgent.Kb.beliefs.write(new FileOutputStream("/home/mohamed/IdeaProjects/Dedale_Cognitif/src/main/java/Knowledge/output.rdf"),"TURTLE");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                L.add(observation_node.getLeft());
            }
        }
        return L;
    }


    @Override
    public void action() {
        updateNodeStates();
        if(policyEvaluationHunting == null)
            policyEvaluationHunting = new PolicyEvaluationHunting(this.myAgent);
        this.myAgent.Kb.updateAgentPosition(myAgent.getLocalName(),this.myAgent.getCurrentPosition());
        LinkedList<String> L = seekForStench();
        this.myAgent.Kb.addStenche(myAgent.getLocalName(),L);
        if(!L.isEmpty())
        {

            policyEvaluationHunting.initialiserValeur();
            policyEvaluationHunting.getNextStep();
            for (LinkedList<Integer> plan:policyEvaluationHunting.plans)
            {
                if(!plan.isEmpty())
                {
                    String nextStep = plan.getFirst()+"";
                    try
                    {
                        boolean sucess = this.myAgent.moveTo(nextStep);
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
