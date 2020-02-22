package eu.su.mas.dedaleEtu.Behavirours.Communication;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.Behavirours.Exploration.Abstract_Exploration_Behaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConfirmingConnection extends SimpleBehaviour {

    private boolean finished = false;
    private Abstract_Exploration_Behaviour exploration_behaviour;

    public ConfirmingConnection(Agent myAgent, Abstract_Exploration_Behaviour E){
        super(myAgent);
        exploration_behaviour = E;
    }

    @Override
    public void action() {
        final MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        final ACLMessage msg = this.myAgent.receive(msgTemplate);
        if (msg != null) {
            //System.out.println(this.myAgent.getLocalName()+"<----Result received from "+msg.getSender().getLocalName()+" ,content= "+msg.getContent());
            final ACLMessage m2 = new ACLMessage(ACLMessage.AGREE);
            m2.setSender(this.myAgent.getAID());
            //TODO : Codé en dur, a retirer !
            m2.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));
            //2° compute the random value
            m2.setContent("Ack");
            ((AbstractDedaleAgent)this.myAgent).sendMessage(m2);
            //System.out.println("Acknoledgement Sent by " + myAgent.getLocalName() + " to " + msg.getSender().getLocalName());
            exploration_behaviour.setStop(true);

            myAgent.addBehaviour(new returningPlansBehaviour(myAgent,msg.getSender().getLocalName(),exploration_behaviour));
            this.finished=true;
        }else{
            block();// the behaviour goes to sleep until the arrival of a new message in the agent's Inbox.
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
