package Agents;

import Behaviours.Exploration_Behaviour;
import Behaviours.Global.General_Behaviour;
import Behaviours.Knowledg_Sharing;
import Knowledge.Knowledg_Base;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Simple_Cognitif_Agent extends AbstractDedaleAgent {

    public Knowledg_Base Kb;
    public long stepCountor = 0;
    public HashMap<String, String> conversation_id = new HashMap<>();


    protected void setup(){
        super.setup();
        List<Behaviour> lb=new ArrayList<Behaviour>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );

        try {
            DFService.register( this, dfd );
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        /************************************************
         *
         * ADD the Default.behaviours of the Dummy Moving Agent
         *
         ************************************************/
        //lb.add(new Exploration_Behaviour(this,this.Kb));
        //lb.add(new Knowledg_Sharing(this,500));
        lb.add(new General_Behaviour(this));
        /***
         * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
         */
        addBehaviour(new startMyBehaviours(this,lb));
        System.out.println("the  agent "+this.getLocalName()+ " is started");

    }


}
