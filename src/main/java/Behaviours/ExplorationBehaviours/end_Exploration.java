package Behaviours.ExplorationBehaviours;

import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SimpleBehaviour;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class end_Exploration extends SimpleBehaviour {

    public Simple_Agent myAgent;

    public end_Exploration(Agent myAgent){
        super(myAgent);
        this.myAgent = (Simple_Agent) myAgent;
    }

    @Override
    public void action() {
        Map_Representation M = this.myAgent.getMap();
        Graph G = M.getG();
        int min = myAgent.point_land;
        for (String s:myAgent.points_rdv.keySet())
        {
            if(myAgent.points_rdv.get(s) < min)
                min = myAgent.points_rdv.get(s);
        }
        Node n = G.getNode(min);
        Stream<Node> neighborNodes = n.neighborNodes();
        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        List<String> path =  M.getShortestPath(myPosition,n.getId());
        while (!path.isEmpty()){
            String next_step = path.get(0);
            try{
                this.myAgent.moveTo(next_step);
                path.remove(next_step);
            }catch (RuntimeException RE){
                Optional<Node> optionalNode = neighborNodes.findAny();
                if(optionalNode.isPresent()){
                    myPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
                    path = M.getShortestPath(myPosition,optionalNode.get().getId());
                    neighborNodes = G.getNode(optionalNode.get().getId()).neighborNodes();
                }
            }
        }
        System.out.println("POINT DE RENDEZ VOUS ATTEINT ! ");
        this.myAgent.doWait();
    }

    @Override
    public boolean done() {
        return false;
    }
}
