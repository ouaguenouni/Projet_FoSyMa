package Behaviours.MapSharing;

import Agents.Simple_Agent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

public class respondingPing extends WakerBehaviour {

    private Simple_Agent myAgent;
    private boolean b;
    private AID last_sender;

    public respondingPing(Agent myAgent,long timeout){
        super(myAgent,timeout);
        this.myAgent = (Simple_Agent) myAgent;
    }

    @Override
    public void onWake() {
        MessageTemplate msg = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage ms = this.myAgent.receive(msg);
        if(ms != null){
            b = this.myAgent.createConversation(ms.getSender().getLocalName(),Simple_Agent.getRestarting_conversation());
            //System.out.println("Création d'une conversation : "+b);
            if(b)
            {
                System.out.println("Je suis "+myAgent.getLocalName()+" je réponds a : "+ms.getSender().getLocalName());
                /*ACLMessage msg2 = new ACLMessage(ACLMessage.AGREE);
                msg2.setConversationId(Double.toString(d));
                msg2.setSender(this.myAgent.getAID());
                msg2.addReceiver(ms.getSender());*/
                last_sender = ms.getSender();
                //myAgent.addBehaviour(new sendingPlans(myAgent,ms.getSender().getLocalName()));
            }
        }
    }

    public AID getLast_sender() {
        return last_sender;
    }

    @Override
    public int onEnd() {
        int  i = 0;
        if(b)
            i = 1;
        return i;
    }

}
