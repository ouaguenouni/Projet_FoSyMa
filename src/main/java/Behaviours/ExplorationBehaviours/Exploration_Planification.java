package Behaviours.ExplorationBehaviours;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.ParallelBehaviour;


public class Exploration_Planification extends Abstract_Exploration_Behaviour {

    private ParallelBehaviour parallel_queue;
    public Exploration_Planification(AbstractDedaleAgent myagent, ParallelBehaviour parallel_queue) {
        super(myagent);
        this.parallel_queue = parallel_queue;
    }


    public void setParallel_queue(ParallelBehaviour parallel_queue) {
        this.parallel_queue = parallel_queue;
    }


    @Override
    public void action() {
        super.action();

    }

    @Override
    public String howToMove() {
        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

        if(this.myAgent.point_land == 0)
            this.myAgent.point_land = Integer.parseInt(myPosition);

        if(super.nearest_open_node == null)
        {
            if( super.myAgent.openNodes.size() != 0)
            {
                //System.out.println("je trace un chemin vers : "+ super.myAgent.openNodes.get(0) );
                super.nearest_open_node =super.myAgent.
                        getMap().getShortestPath( myPosition, super.myAgent.openNodes.get(0) ).get(0);
                //System.out.println("Du coup je vais vers : " + nearest_open_node);
            }
            else
                finished = true;
        }

        return nearest_open_node;
    }

    @Override
    public int onEnd() {
        if(finished)
            return 2;
        else
            return 0;
    }

    @Override
    public boolean done() {
        return finished;
    }

}
