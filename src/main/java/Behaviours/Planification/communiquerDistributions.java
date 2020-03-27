package Behaviours.Planification;

import Agents.Planification_Agent;
import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import Knowledge.MarkovModel.Markov_Model;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.HashMap;

public class communiquerDistributions extends CyclicBehaviour {

    public Planification_Agent myAgent;

    public communiquerDistributions(Planification_Agent PA,long ms){
        super(PA);
        myAgent = PA;
    }

    public void sendDistributions() throws IOException {
        ACLMessage map = new ACLMessage(ACLMessage.SUBSCRIBE);
        map.setSender(this.myAgent.getAID());

        map.setContentObject(myAgent.general_monitoring);

        DFAgentDescription dfd = new DFAgentDescription();
        try {
            DFAgentDescription[] result = DFService.search(this.myAgent, dfd);
            for (int i=0;i<result.length;i++)
            {
                if(!result[i].getName().getLocalName().equals(myAgent.getLocalName())) {
                    map.addReceiver(result[i].getName());
                    System.out.println("Ajout de l'agent : "+result[i].getName().getLocalName() + "par "+ this.myAgent.getLocalName());
                }

            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }


        myAgent.sendMessage(map);

        System.out.println("Envoie de message");
    }

    public void receiveDistributions() throws UnreadableException {
        //TODO : Test this
        //MessageTemplate ms = MessageTemplate.and(MatchSender(this.calling_behaviour.getLast_sender()),MatchPerformative(ACLMessage.INFORM));
        MessageTemplate ms = MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE);
        ACLMessage msg = myAgent.receive(ms);
        if(msg != null)
        {
            System.out.println("J'ai pécho le model merci");
            Markov_Model content = (Markov_Model) msg.getContentObject();
            System.out.println("J'ai reçu une distribution : !!!!!!!!!!!!!!!!!!!!!!!!"+content.distribution);
        }
    }



    @Override
    public void action() {


        try {
            receiveDistributions();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }


        try {
            sendDistributions();

        } catch (IOException z) {
            z.printStackTrace();
        }

    }
}
