package Agents;

import Behaviours.Coordination_Behaviour.General_Behaviour;
import eu.su.mas.dedale.mas.agent.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Stupid_Agent extends Simple_Agent {


    protected void setup(){

        super.setup();

        this.openNodes=new ArrayList<String>();
        this.closedNodes=new HashSet<String>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );

        try {
            DFService.register( this, dfd );
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        List<Behaviour> lb=new ArrayList<Behaviour>();

        /************************************************
         *
         * ADD the behaviours of the Dummy Moving Agent
         *
         ************************************************/

        lb.add(new RandomWalkBehaviour(this));


        /***
         * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
         */


        addBehaviour(new startMyBehaviours(this,lb));
        System.out.println("the  agent "+this.getLocalName()+ " is started and has map : "+ this.myMap);

    }


}
