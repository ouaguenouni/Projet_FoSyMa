package Behaviours.MapSharing;

import Agents.Simple_Agent;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class creatingConversation extends SimpleBehaviour {

    private Simple_Agent myAgent;

    public creatingConversation(Agent myAgent){
        super(myAgent);
        this.myAgent = (Simple_Agent) myAgent;
    }


    @Override
    public void action() {
        MessageTemplate tem = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
        ACLMessage msg = myAgent.receive(tem);
        if(msg != null)
        {
            System.out.println("Je suis "+myAgent.getLocalName()+" J'ai réçu une réponse de ping donc je crée une conv");
            boolean b = this.myAgent.createConversation(msg.getSender().getLocalName(),4);
            if(b)
                myAgent.addBehaviour(new sendingPlans(myAgent,msg.getSender().getLocalName()));
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
