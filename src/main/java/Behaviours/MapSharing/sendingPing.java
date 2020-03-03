package Behaviours.MapSharing;

import Agents.Simple_Agent;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import javax.xml.transform.Result;

public class sendingPing extends WakerBehaviour {

    private Simple_Agent myAgent;

    public sendingPing(Agent a, long period) {
        super(a, period);
        myAgent = (Simple_Agent) a;
    }

    @Override
    protected void onWake() {
        ACLMessage ping = new ACLMessage(ACLMessage.CFP);
        ping.setSender(myAgent.getAID());
        DFAgentDescription dfd = new DFAgentDescription();
        try {
            DFAgentDescription[] result = DFService.search(this.myAgent, dfd);
            for (int i=0;i<result.length;i++)
            {
                if(!result[i].getName().getLocalName().equals(myAgent.getLocalName())) {
                    ping.addReceiver(result[i].getName());
                }
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        myAgent.sendMessage(ping);
    }

}
