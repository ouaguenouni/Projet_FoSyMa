package Behaviours.Hunting_Behaviour;

import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import org.graphstream.graph.Node;

import java.util.List;

public class detectingWumpusBehaviour extends SimpleBehaviour {


    private Simple_Agent myAgent;
    private boolean found = false;

    public detectingWumpusBehaviour(Agent myAgent){
        this.myAgent = (Simple_Agent) myAgent;
    }


    public boolean blocked(List<Couple<String, List<Couple<Observation,Integer>>>> lobs){
        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        Map_Representation m = this.myAgent.getMap();
        if(m == null)
        {
            this.myAgent.setMap(new Map_Representation());
            m = this.myAgent.getMap();
        }
        Node n = m.getG().getNode(myPosition);
        if(n!=null)
        {
            for (Couple<String, List<Couple<Observation,Integer>>> c:lobs){
                {
                    if(c.getRight().size() > 0)
                    {
                        if(c.getRight().get(0).getLeft().getName().equals("Stench"))
                        {
                            //TODO Attention j'ai désacrivé ça
                            found = false;
                        }
                    }

                }
            }
        }
        return false;
    }


    @Override
    public void action() {
        List<Couple<String, List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
        blocked(lobs);
    }

    @Override
    public boolean done() {
        return found;
    }


    @Override
    public int onEnd() {
        if(found)
            return 10;
        else
            return 0;
    }
}
