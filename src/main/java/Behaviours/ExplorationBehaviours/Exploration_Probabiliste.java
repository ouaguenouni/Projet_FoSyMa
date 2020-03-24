package Behaviours.ExplorationBehaviours;

import Agents.Planification_Agent;
import Agents.Probabiliste_Agent;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.behaviours.ParallelBehaviour;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;


public class Exploration_Probabiliste extends Abstract_Exploration_Behaviour {

    public ParallelBehaviour parallel_queue;
    public Probabiliste_Agent myAgent;



    public Exploration_Probabiliste(AbstractDedaleAgent myagent, ParallelBehaviour parallel_queue) {
        super(myagent);
        this.myAgent = (Probabiliste_Agent) myagent;
        this.parallel_queue = parallel_queue;
    }


    public void setParallel_queue(ParallelBehaviour parallel_queue) {
        this.parallel_queue = parallel_queue;
    }


    @Override
    public void action() {
        super.action();
    }

    public void updateStructurePlanification(){
        Graph g = this.myAgent.getMap().getG();

        for (Iterator<Node> it = g.nodes().iterator(); it.hasNext(); ) {
            Node N = it.next();
            if(!this.myAgent.successeurs.containsKey(Integer.valueOf(N.getId())))
            {
                this.myAgent.values.put(Integer.valueOf(N.getId()),0.0);
                this.myAgent.successeurs.put(Integer.valueOf(N.getId()),new HashSet<>());
            }
        }

        for (Iterator<Edge> it = this.myAgent.getMap().getG().edges().iterator(); it.hasNext(); ) {
            Edge e = it.next();
            this.myAgent.successeurs.get(Integer.valueOf(e.getSourceNode().getId())).add(Integer.valueOf(e.getTargetNode().getId()));
            this.myAgent.successeurs.get(Integer.valueOf(e.getTargetNode().getId())).add(Integer.valueOf(e.getSourceNode().getId()));
        }

    }

    public void intialiserValeurs(){
        for(Integer i : this.myAgent.values.keySet()){
            if(!(this.myAgent.model.distribution.get(i) == 1.0) && myAgent.openNodes.contains(i+""))
            {
                this.myAgent.values.put(i,10.0-10*this.myAgent.model.distribution.get(i));
            }
            else{
                System.out.println("Rez : "+this.myAgent.penality);
                if(!(this.myAgent.model.distribution.get(i) == 1.0))
                {
                    this.myAgent.values.put(i,0.0);
                }
                else{
                    this.myAgent.values.put(i,-10.0);
                }
            }
        }
        System.out.println("Fin de l'initialisation avec : "+this.myAgent.values);
    }

    public void propagerValeurs(){
        for (Integer i:this.myAgent.successeurs.keySet()){
            HashSet<Integer> S = this.myAgent.successeurs.get(i);
            double m = -5;
            for (Integer n:S)
            {
                double v = this.myAgent.values.get(n)*this.myAgent.lambda;;
                if(v>m)
                {
                    m = v;
                }
            }
            //System.out.println("Penality = "+penality);
            //System.out.println("Values : "+values);
            if((m > this.myAgent.values.get(i)) && this.myAgent.model.distribution.get(i) != 1.0)
                this.myAgent.values.put(i,m);
        }
    }

    public void calculerPlans(){
        HashMap<Integer,Double> old_values;
        do {
            old_values= new HashMap<>();
            old_values.putAll(this.myAgent.values);
            propagerValeurs();
            //System.out.println("Comparaison  : ");
            //for (Integer i:old_values.keySet()){
            //    System.out.println(i + " : h1-"+old_values.get(i)+" h2-"+values.get(i));
            //}
        }
        while(difference(this.myAgent.values,old_values) > 0.000001);
    }

    public LinkedList<LinkedList<Integer>> genererPlans(int source)
    {
        LinkedList<LinkedList<Integer>> plans = new LinkedList<>();
        boolean stop = false;
        LinkedList<Integer> tmp = new LinkedList<>();
        Stack<LinkedList<Integer>> pistes = new Stack<>();
        LinkedList<Integer> explored = new LinkedList<>();
        tmp.add(source);
        pistes.push(tmp);
        while(!pistes.empty()){
            //Dernier chemin empilé
            tmp = pistes.pop();
            //On récupére le dernier élément du chemin
            Integer last = tmp.getLast();
            //On l'ajoute aux explorés
            explored.add(last);
            //On énumère ces successeurs
            HashSet<Integer> possibilites = this.myAgent.successeurs.get(last);
            if(possibilites == null)
                return plans;
            //On calcule la valeur maximale des successeurs qui ne sont pas explorés pour pas créer de cycles
            double max = -5;
            //Cette variable sert a ce que si le noeud n'a pas de successeurs non explorés ce qui signie que le chemin est fini on l'ajoute au plans
            boolean no_successors = true;
            for (Integer possibilite : possibilites) {
                if (!(explored.contains(possibilite))) {
                    no_successors = false;
                    if (this.myAgent.values.get(possibilite) > max) {
                        max = this.myAgent.values.get(possibilite);
                    }
                }
            }
            if(no_successors){
                //Chemin sans successeurs on l'ajoute aux plans
                //System.out.println("Ajout du plan : "+tmp);
                plans.add(tmp);
            }
            else{
                final double m = max;
                //On efface les successeurs dont la valeur est inférieure au maximum
                possibilites.removeIf(integer -> this.myAgent.values.get(integer)<m);
                //On génère des chemins en accumulant le possibilités intéressantes au chemin qu'on avait dépilé et on les empilent
                LinkedList<Integer> pos;
                for (Integer p:possibilites) {
                    //si le chemin intéressant en question est mieux que le chemin courant on l'ajoute et on empile
                    if(this.myAgent.values.get(p) > this.myAgent.values.get(last))
                    {
                        pos = new LinkedList<>(tmp);
                        pos.add(p);
                        //System.out.println("Empilement de : "+p);
                        pistes.push(pos);
                    }
                    //Sinon on considére que le chemin est terminé et par conséquent on l'enregistre
                    else{
                        //Chemin sans successeurs on l'ajoute aux plans
                        //System.out.println("Ajout du plan : "+tmp);
                        if(!(plans.contains(tmp)))
                            plans.add(tmp);
                    }
                }
            }

        }
        //Boucle d'affichage servant au test :
        //System.out.println("Liste des plans : ");
        //for (LinkedList plan:plans){
        //    System.out.println(plan);
        //}
        this.myAgent.plans = plans;
        return this.myAgent.plans;
    }

    public static double difference(HashMap<Integer,Double> h1 , HashMap<Integer,Double> h2){
        double cpt = 0.0;
        for (Integer i : h1.keySet()){
            cpt = cpt + (Math.max(h1.get(i),h2.get(i)) - Math.min(h1.get(i),h2.get(i)));
        }

        return cpt;
    }

    public Integer meilleurSuccesseur(Integer i){
        Double max = -100.0;
        Integer meilleur = 0;
        for (Integer j:this.myAgent.successeurs.get(i)){
            if(this.myAgent.values.get(j)>meilleur)
            {
                max = this.myAgent.values.get(j);
                meilleur = j;
            }
        }
        return meilleur;
    }

    public void move(){
        String next_pos = howToMove();
        this.myAgent.model.avancerDansLeTemps();
        this.myAgent.model.updateModele(this.myAgent.successeurs, Integer.parseInt(this.myAgent.getCurrentPosition()));
        try{
            boolean success = ((AbstractDedaleAgent)this.myAgent).moveTo(next_pos);
            while(!success){
                this.myAgent.penality.add(Integer.valueOf(next_pos));
                this.myAgent.model.setDistribution(Integer.parseInt(next_pos),Integer.parseInt(this.myAgent.getCurrentPosition()));
                next_pos = howToMove();
                success = ((AbstractDedaleAgent)this.myAgent).moveTo(next_pos);
                nearest_open_node = null;
                Collections.shuffle(this.myAgent.openNodes);
            }
        }catch (RuntimeException E){
            finished = true;
        }
        //this.myAgent.penality.clear();
    }


    @Override
    public String howToMove() {

        String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
        String prochain_noeud = myPosition;
        if(this.myAgent.point_land == 0)
            this.myAgent.point_land = Integer.parseInt(myPosition);
        if(this.myAgent.plan_courant.isEmpty())
        {
            this.myAgent.plans.clear();
            updateStructurePlanification();
            intialiserValeurs();
            calculerPlans();
            genererPlans(Integer.parseInt(myPosition));
            this.myAgent.plan_courant = this.myAgent.plans.getFirst();
            System.out.println("Plan calculé : "+this.myAgent.plan_courant);
            this.myAgent.plan_courant.removeFirst();
        }
        System.out.println("=====================Position : "+myPosition);
        System.out.println("Successerus de la position : "+this.myAgent.successeurs.get(Integer.valueOf(myPosition)));
        System.out.println("Plan courant : "+this.myAgent.plan_courant);
        if(this.myAgent.plan_courant.size() == 0)
        {
            System.out.println("VIDE !!!!! ");
            LinkedList<Integer> L = new LinkedList<>(this.myAgent.successeurs.get(Integer.valueOf(myPosition)));
            prochain_noeud = L.get(0).toString();
        }
        else
        {
            prochain_noeud = String.valueOf(this.myAgent.plan_courant.getFirst());
            this.myAgent.plan_courant.removeFirst();
        }


        System.out.println("Noeud suivant : "+prochain_noeud);
        this.myAgent.penality.clear();
        return prochain_noeud;
    }

    @Override
    public int onEnd() {
        if(finished)
            return 0;
        else
            return 0;
    }

    @Override
    public boolean done() {
        return finished;
    }

}
