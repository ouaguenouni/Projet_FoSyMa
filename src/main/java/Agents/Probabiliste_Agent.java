package Agents;

import Behaviours.Coordination_Behaviour.General_Behaviour;
import Knowledge.MarkovModel.Markov_Model;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Probabiliste_Agent extends Planification_Agent {

    public Markov_Model model = null;

    protected void setup(){

        super.setup();

        this.openNodes=new ArrayList<String>();
        this.closedNodes=new HashSet<String>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );

        List<Behaviour> lb=new ArrayList<Behaviour>();

        /************************************************
         *
         * ADD the behaviours of the Dummy Moving Agent
         *
         ************************************************/

        lb.add(new General_Behaviour(this));
        //lb.add(new Hunting_Test(this));
        //lb.add(new sendingPing(this,500));
        //lb.add(new creatingConversation(this));


        /***
         * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
         */


        addBehaviour(new startMyBehaviours(this,lb));
        System.out.println("the  agent "+this.getLocalName()+ " is started and has map : "+ this.myMap);

    }


}
