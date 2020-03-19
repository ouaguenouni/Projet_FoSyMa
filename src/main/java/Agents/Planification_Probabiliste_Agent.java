package Agents;

import Behaviours.Coordination_Behaviour.General_Behaviour;
import Behaviours.ExplorationBehaviours.Abstract_Exploration_Behaviour;
import Knowledge.Map_Representation;
import eu.su.mas.dedale.mas.agent.behaviours.startMyBehaviours;
import jade.core.behaviours.Behaviour;
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

public class Planification_Probabiliste_Agent extends Planification_Agent {


    private static final long serialVersionUID = -6431752665590433727L;
    private HashMap<String,double[]> distibutions_agents = new HashMap<>();
    private HashMap<String,double[]> distribution_wumpus = new HashMap<>();
    private double[][] TransitionMatrice = null;
    private HashMap<Integer,LinkedList<Integer>> predictions_utilites = new HashMap<>();


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
