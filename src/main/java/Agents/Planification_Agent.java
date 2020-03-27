package Agents;

import Behaviours.Coordination_Behaviour.General_Behaviour;
import Behaviours.ExplorationBehaviours.Abstract_Exploration_Behaviour;
import Behaviours.Planification.communiquerDistributions;
import Knowledge.Map_Representation;
import Knowledge.MarkovModel.Markov_Model;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;

import java.util.*;

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

public class Planification_Agent extends Simple_Agent {


    private static final long serialVersionUID = -6431752665590433727L;
    public HashMap<Integer,Double> values = new HashMap<>();
    public HashMap<Integer, HashSet<Integer>> successeurs = new HashMap<>();
    public double lambda = 0.9;
    public LinkedList<LinkedList<Integer>> plans = new LinkedList<>();
    public LinkedList<Integer> plan_courant = new LinkedList<>();
    public LinkedList<Integer> penality = new LinkedList<>();
    public Markov_Model general_monitoring = null;



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
