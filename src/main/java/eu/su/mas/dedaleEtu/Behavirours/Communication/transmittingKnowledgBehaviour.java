package eu.su.mas.dedaleEtu.Behavirours.Communication;

import eu.su.mas.dedaleEtu.Behavirours.Exploration.Abstract_Exploration_Behaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class transmittingKnowledgBehaviour extends SimpleBehaviour {

    private Abstract_Exploration_Behaviour exploration_behaviour;


    public transmittingKnowledgBehaviour(Agent myAgent, Abstract_Exploration_Behaviour AEB){
        super(myAgent);
        this.exploration_behaviour = AEB;
    }

    @Override
    public void action() {

        final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
        final ACLMessage msg1 = this.myAgent.receive(msgTemplate);
        if(msg1 != null && msg1.getContent().equals("Ack"))
        {
            final ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
            msg2.setSender(this.myAgent.getAID());
            msg2.addReceiver(new AID(msg1.getSender().getLocalName(),AID.ISLOCALNAME));
            //2° compute the random value
            msg2.setContent(Abstract_Exploration_Behaviour.seralizeKnowledge(exploration_behaviour.getMyMap()));
            this.myAgent.send(msg2);
        }



    }

    @Override
    public boolean done() {
        return false;
    }


}
