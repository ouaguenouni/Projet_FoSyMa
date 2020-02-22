package eu.su.mas.dedaleEtu.Behavirours.Communication;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class EstablishingConnectionBehaviour extends SimpleBehaviour {

    private List<String> receiver;
    private HashMap<String, Object> content;


    public EstablishingConnectionBehaviour(Agent myAgent){
        super(myAgent);
    }

    @Override
    public void action() {

        final ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        msg.setSender(this.myAgent.getAID());
        if (!(myAgent.getLocalName().equals("Explo1")))
        msg.addReceiver(new AID("Explo1", AID.ISLOCALNAME));
        if (!(myAgent.getLocalName().equals("Explo2")))
        msg.addReceiver(new AID("Explo2", AID.ISLOCALNAME));
        if (!(myAgent.getLocalName().equals("Explo3")))
        msg.addReceiver(new AID("Explo3", AID.ISLOCALNAME));

        //2° compute the random value
        msg.setContent("Connection?");

        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
    }

    @Override
    public boolean done() {
        return false;
    }
}
