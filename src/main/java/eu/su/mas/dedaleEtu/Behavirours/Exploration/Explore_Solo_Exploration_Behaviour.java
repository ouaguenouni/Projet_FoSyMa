package eu.su.mas.dedaleEtu.Behavirours.Exploration;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

public class Explore_Solo_Exploration_Behaviour extends Abstract_Exploration_Behaviour {



    public Explore_Solo_Exploration_Behaviour(AbstractDedaleAgent myagent, MapRepresentation myMap) {
        super(myagent, myMap);
    }

    @Override
    public String howToMove() {
        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        if(nearest_open_node == null)
            if( super.openNodes.size() != 0)
                nearest_open_node =super.myMap.getShortestPath( myPosition, super.openNodes.get(0) ).get(0);
            else
            {
                myAgent.addBehaviour(new RandomWalkBehaviour((AbstractDedaleAgent) myAgent));
                finished = true;
            }

        return nearest_open_node;
    }




}
