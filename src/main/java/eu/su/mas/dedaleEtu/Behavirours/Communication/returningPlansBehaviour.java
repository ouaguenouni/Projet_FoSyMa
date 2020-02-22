package eu.su.mas.dedaleEtu.Behavirours.Communication;

import eu.su.mas.dedaleEtu.Behavirours.Exploration.Abstract_Exploration_Behaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


import java.io.IOException;

public class returningPlansBehaviour extends SimpleBehaviour {
    private Abstract_Exploration_Behaviour exploration_behaviour;
    private String receiver;


    public returningPlansBehaviour(Agent myAgent, String receiver, Abstract_Exploration_Behaviour AEB){
        super(myAgent);
        this.exploration_behaviour = AEB;
        this.receiver = receiver;

    }

    @Override
    public void action() {

        final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        final ACLMessage msg1 = this.myAgent.receive(msgTemplate);

        if (msg1 != null ) {
            System.out.println( myAgent.getLocalName() + " a reçu les données suivante de la part de " + msg1.getSender().getLocalName() + " : " + msg1.getContent());
            exploration_behaviour.updateWithKnowledg((JSONObject) JSONValue.parse(msg1.getContent()));
        }
    }

    @Override
    public boolean done() {
        return false;
    }


}
