package Behaviours.Coordination_Behaviour;

import Agents.Simple_Agent;
import Behaviours.ExplorationBehaviours.Explore_Multi_Behaviour;
import Behaviours.ExplorationBehaviours.broadcast_Rdv;
import Behaviours.Hunting_Behaviour.detectingWumpusBehaviour;
import Behaviours.MapSharing.respondingPing;
import Behaviours.MapSharing.sendingPing;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.ParallelBehaviour;

public class General_Exploration_Behaviour extends ParallelBehaviour {

    private Simple_Agent myAgent;
    private respondingPing responding_ping_behaviour;
    private sendingPing sendiing_ping_behaviour;
    private broadcast_Rdv broadcast_rdv_behaviour;
    private detectingWumpusBehaviour detectingWumpusBehaviour;
    private Explore_Multi_Behaviour exploration_behaviour;

    public General_Exploration_Behaviour(AbstractDedaleAgent myAgent,int param)
    {
        super(myAgent,param);
        this.myAgent = (Simple_Agent) myAgent;
        responding_ping_behaviour = new respondingPing(this.myAgent,150);
        sendiing_ping_behaviour = new sendingPing(this.myAgent,150);
        broadcast_rdv_behaviour = new broadcast_Rdv(myAgent,150);
        exploration_behaviour = new Explore_Multi_Behaviour(this.myAgent,this);
        detectingWumpusBehaviour = new detectingWumpusBehaviour(this.myAgent);
        this.addSubBehaviour(responding_ping_behaviour);
        this.addSubBehaviour(broadcast_rdv_behaviour);
        this.addSubBehaviour(sendiing_ping_behaviour);
        this.addSubBehaviour(exploration_behaviour);
        this.addSubBehaviour(detectingWumpusBehaviour);
    }


    @Override
    public int onEnd() {
        System.out.println("Le on end renvoie : "+responding_ping_behaviour.onEnd());
        int m = Math.max(responding_ping_behaviour.onEnd(),exploration_behaviour.onEnd());
        return Math.max(m,detectingWumpusBehaviour.onEnd());
    }

    public respondingPing getResponding_ping_behaviour() {
        return responding_ping_behaviour;
    }
}
