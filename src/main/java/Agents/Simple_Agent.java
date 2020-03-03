package Agents;

import Behaviours.Coordination_Behaviour.General_Behaviour;
import Behaviours.ExplorationBehaviours.Abstract_Exploration_Behaviour;
import Knowledge.Map_Representation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import java.util.*;

import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;

import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

/**
 * <pre>
 * ExploreSolo agent.
 * It explore the map using a DFS algorithm.
 * It stops when all nodes have been visited.
 *  </pre>
 *
 * @author hc
 *
 */

public class Simple_Agent extends AbstractDedaleAgent {


    public HashMap<String, Integer> points_rdv = null;
    public int point_land;

    private static final long serialVersionUID = -6431752665590433727L;

    protected Map_Representation myMap;
    protected HashMap<String,Integer> active_conversations = new HashMap<>();
    protected static final int restarting_conversation = 5;

    protected Abstract_Exploration_Behaviour exploratory_behaviour;

    public List<String> openNodes;
    public Set<String> closedNodes;

    public void setExploratory_behaviour(Abstract_Exploration_Behaviour exploratory_behaviour) {
        this.exploratory_behaviour = exploratory_behaviour;
    }

    public static int getRestarting_conversation() {
        return restarting_conversation;
    }

    public HashMap<String, Integer> getActive_conversations() {
        return active_conversations;
    }

    public Abstract_Exploration_Behaviour getExploratory_behaviour() {
        return exploratory_behaviour;
    }

    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 			1) set the agent attributes
     *	 		2) add the behaviours
     *
     */


    public boolean createConversation(String agent,int number){
        if(!active_conversations.containsKey(agent))
            active_conversations.put(agent,0);
        double n = active_conversations.get(agent);
        if(n == 0)
        {
            active_conversations.put(agent,number);
            return true;
        }
        return false;
    }



    protected void setup(){

        super.setup();

        this.openNodes=new ArrayList<String>();
        this.closedNodes=new HashSet<String>();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName( getAID() );

        try {
            DFService.register( this, dfd );
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        List<Behaviour> lb=new ArrayList<Behaviour>();

        /************************************************
         *
         * ADD the behaviours of the Dummy Moving Agent
         *
         ************************************************/

        lb.add(new General_Behaviour(this));
        //lb.add(new Hunting_Test(this));
        //lb.add(new sendingPing(this,500));
        //lb.add(new creatingConversation(this));


        /***
         * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
         */


        addBehaviour(new startMyBehaviours(this,lb));
        System.out.println("the  agent "+this.getLocalName()+ " is started and has map : "+ this.myMap);

    }


    public Map_Representation getMap() {
        return myMap;
    }

    public void setMap(Map_Representation map){
        myMap = map;
    }


}
