package Behaviours.MapSharing;

import Agents.Simple_Agent;
import Knowledge.Map_Representation;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.io.Serializable;

import static jade.lang.acl.MessageTemplate.*;

public class sendingPlans extends SimpleBehaviour {

    private double discussion_id;
    private Simple_Agent myAgent;

    private String target = null;
    private int tyyouts = 0;

    private boolean sended = false;
    private boolean acknoledged = false;
    private boolean received = false;
    private respondingPing calling_behaviour;



    public sendingPlans(Agent myAgent,String target){
        super(myAgent);
        this.myAgent = (Simple_Agent) myAgent;
        this.target = target;
        sended = false;
        acknoledged = false;
        received = false;
        tyyouts = 0;
    }

    public sendingPlans(Agent myAgent,respondingPing calling_behaviour){
        super(myAgent);
        this.myAgent = (Simple_Agent) myAgent;
        this.calling_behaviour = calling_behaviour;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void sendMap()
    {
        ACLMessage map = new ACLMessage(ACLMessage.INFORM);
        map.setConversationId(Double.toString(this.discussion_id));
        map.setSender(this.myAgent.getAID());

        map.addReceiver(new AID(this.calling_behaviour.getLast_sender().getLocalName(),AID.ISLOCALNAME));

        Map_Representation myMap = myAgent.getMap();
        map.setContent(Map_Representation.seralizeKnowledge(myMap));
        myAgent.sendMessage(map);
        sended = true;
    }

    public void lookForAck(){
        MessageTemplate ms = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage msg = myAgent.receive(ms);
        if(msg != null){
            acknoledged = true;
        }
    }


    public void receivingMap(){
        //TODO : Test this
        //MessageTemplate ms = MessageTemplate.and(MatchSender(this.calling_behaviour.getLast_sender()),MatchPerformative(ACLMessage.INFORM));
        MessageTemplate ms = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(ms);
        if(msg != null)
        {
            received = true;
            String content = msg.getContent();
            System.out.println("J'ai reçu une maapeuuuh ");
            Simple_Agent SA = (Simple_Agent) myAgent;
            System.out.println("EXPLORATORY BEHAVIOUR : "+SA.getExploratory_behaviour());
            SA.getExploratory_behaviour().updateWithKnowledg((JSONObject) JSONValue.parse(msg.getContent()));
            ACLMessage msg2 = new ACLMessage(ACLMessage.CONFIRM);
            msg2.setSender(myAgent.getAID());
            msg2.addReceiver(msg.getSender());
            myAgent.sendMessage(msg2);
        }



    }

    @Override
    public void action() {
        System.out.println("On rentre dans sending plans :"+myAgent.getLocalName()+" sachant que sended, received, et akc valent "+sended+received+acknoledged+ " et tryout = " + tyyouts);
        sendMap();
        lookForAck();
        receivingMap();
        tyyouts++;

        /*if(!sended){
            sendMap();
        if(!acknoledged)
        {
            lookForAck();
        }
        if(!received)
        {
            receivingMap();
        }*/
    }

    @Override
    public boolean done() {
        if(tyyouts > 100)
        {
            tyyouts = 0;
            return true;
        }
        if(acknoledged && received && sended)
        {
            return true;
        }
        else
            return false;
    }

    public int onEnd(){
        int i = 0;
        if(sended){
            i++;
        }
        if(acknoledged)
        {
            i++;
        }
        if(received)
        {
            i++;
        }
        if(i>1)
            i = 1;
        if(tyyouts > 100)
        {
            System.out.println("Normalement je suis débloqué par le tryouts");
            tyyouts = 0;
            return 2;
        }
        System.out.println("Le on end de l'envoie des plans renvoie : "+i);
        return i;
    }

}
