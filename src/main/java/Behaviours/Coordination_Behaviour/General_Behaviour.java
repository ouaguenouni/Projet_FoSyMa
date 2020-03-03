package Behaviours.Coordination_Behaviour;

import Agents.Simple_Agent;
import Behaviours.ExplorationBehaviours.Explore_Multi_Behaviour;
import Behaviours.ExplorationBehaviours.end_Exploration;
import Behaviours.Hunting_Behaviour.trackingBehaviour;
import Behaviours.MapSharing.respondingPing;
import Behaviours.MapSharing.sendingPing;
import Behaviours.MapSharing.sendingPlans;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class General_Behaviour extends FSMBehaviour {
    private Simple_Agent myAgent;

    public General_Behaviour(Agent myAgent){
        super(myAgent);
        this.myAgent = (Simple_Agent) myAgent;

        General_Exploration_Behaviour GEP = new General_Exploration_Behaviour(this.myAgent,ParallelBehaviour.WHEN_ANY);
        sendingPlans SPP = new sendingPlans(this.myAgent,GEP.getResponding_ping_behaviour());
        end_Exploration EP = new end_Exploration(myAgent);
        trackingBehaviour TB = new trackingBehaviour(myAgent);


        this.registerState(SPP,"SHARING");
        this.registerState(EP,"ENDEXPLORE");

        this.registerState(TB,"HUNTING");

        this.registerFirstState(GEP,"EXPLORING");
        String [] exploring = {"EXPLORING"} ;
        String [] sharing = {"SHARING"} ;
        String [] hunting = {"HUNTING"} ;
        String [] both = {"SHARING","EXPLORING"} ;

        this.registerTransition("EXPLORING","EXPLORING",0,exploring);
        this.registerTransition("EXPLORING","SHARING",1,both);

        this.registerTransition("EXPLORING","ENDEXPLORE",2);

        this.registerTransition("EXPLORING","HUNTING",10,hunting);
        this.registerDefaultTransition("HUNTING","HUNTING",hunting);

        this.registerTransition("SHARING","EXPLORING",1,both);
        this.registerTransition("SHARING","EXPLORING",2,both);
        this.registerTransition("SHARING","EXPLORING",3,both);


    }




}
