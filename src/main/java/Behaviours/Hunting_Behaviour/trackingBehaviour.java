package Behaviours.Hunting_Behaviour;

import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import org.graphstream.graph.Node;

import java.util.List;

public class trackingBehaviour extends CyclicBehaviour {

    private Simple_Agent myAgent;


    public trackingBehaviour(Agent A){
        this.myAgent = (Simple_Agent) A;
    }

    @Override
    public int onEnd() {
        return 0;
    }

    @Override
    public void action() {
        System.out.println("QUE LA TRAQUE COMMENCE ! ");
        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        List<Couple<String, List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
        Map_Representation m = this.myAgent.getMap();
        if(m == null)
        {
            this.myAgent.setMap(new Map_Representation());
            m = this.myAgent.getMap();
        }
            for (Couple<String, List<Couple<Observation,Integer>>> c:lobs){
                {
                    if(c.getRight().size() > 0)
                    {
                        if(c.getRight().get(0).getLeft().getName().equals("Stench"))
                        {
                            boolean success = myAgent.moveTo(c.getLeft());
                            while(!success) {
                                System.out.println("J'arrive pas a avancer il me bloque surement ");
                                success = myAgent.moveTo(c.getLeft());
                            }
                        }
                    }

                }
            }
        }


    }
