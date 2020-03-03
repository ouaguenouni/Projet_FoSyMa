package Behaviours.ExplorationBehaviours;

import Agents.Simple_Agent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class broadcast_Rdv extends TickerBehaviour {


    private Simple_Agent myAgent;

    public broadcast_Rdv(Agent myAgent, long period){
        super(myAgent,period);
        this.myAgent = (Simple_Agent) myAgent;
        this.myAgent.points_rdv = new HashMap<>();
        DFAgentDescription dfd = new DFAgentDescription();
        try {
            DFAgentDescription[] result = DFService.search(this.myAgent, dfd);
            for (int i=0;i<result.length;i++)
            {
                if(!result[i].getName().getLocalName().equals(myAgent.getLocalName())) {
                    this.myAgent.points_rdv.put(result[i].getName().getLocalName(),0);
                }
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }

    }

    private void broadcast_rdv(){
        ACLMessage ping = new ACLMessage(ACLMessage.PROPAGATE);
        ping.setSender(myAgent.getAID());
        for (String s:this.myAgent.points_rdv.keySet())
        {
            ping.addReceiver(new AID(s,AID.ISLOCALNAME));
        }
        ping.setContent(Integer.toString(this.myAgent.point_land));
        myAgent.sendMessage(ping);
    }

    private void read_rdv(){
        MessageTemplate mst =  MessageTemplate.MatchPerformative(ACLMessage.PROPAGATE);
        ACLMessage ms = myAgent.receive(mst);
        if(ms != null){
            this.myAgent.points_rdv.put(ms.getSender().getLocalName(),Integer.parseInt(ms.getContent()));
        }

    }

    @Override
    protected void onTick() {
        read_rdv();
        broadcast_rdv();
    }
}
