package eu.su.mas.dedaleEtu.Agent;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import eu.su.mas.dedaleEtu.Behavirours.Communication.ConfirmingConnection;
import eu.su.mas.dedaleEtu.Behavirours.Communication.EstablishingConnectionBehaviour;
import eu.su.mas.dedaleEtu.Behavirours.Communication.transmittingKnowledgBehaviour;
import eu.su.mas.dedaleEtu.Behavirours.Exploration.Explore_Solo_Exploration_Behaviour;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

import java.util.ArrayList;
import java.util.List;

public class TestAgent extends AbstractDedaleAgent {

    private static final long serialVersionUID = -6431752665590433727L;
    private MapRepresentation myMap;


    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 			1) set the agent attributes
     *	 		2) add the behaviours
     *
     */
    protected void setup(){

        super.setup();


        List<Behaviour> lb=new ArrayList<Behaviour>();

        /************************************************
         *
         * ADD the behaviours of the Dummy Moving Agent
         *
         ************************************************/
        Explore_Solo_Exploration_Behaviour E = new Explore_Solo_Exploration_Behaviour(this,this.myMap);
        lb.add(E);
        lb.add(new EstablishingConnectionBehaviour(this));
        lb.add(new ConfirmingConnection(this,E));
        lb.add(new transmittingKnowledgBehaviour(this,E));


        /***
         * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
         */


        addBehaviour(new startMyBehaviours(this,lb));

        System.out.println("the  agent "+this.getLocalName()+ " is started");

    }



}