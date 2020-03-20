package Knowledge.MarkovModel;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Markov_Model implements Serializable {
    public HashMap<Integer,Double> distribution = new HashMap<>();
    public HashMap<Integer, HashSet<Integer>> successeurs;
    public HashSet<Integer> noeuds = new HashSet<>();

    public Markov_Model(HashMap<Integer, HashSet<Integer>> successeurs){
        this.successeurs = successeurs;
        noeuds.addAll(successeurs.keySet());
        for (Integer i:successeurs.keySet()){
            noeuds.addAll(successeurs.get(i));
        }
    }

    public Double getTransition(Integer src,Integer dst){
        if(successeurs.get(src).contains(dst))
        {
            return (1.0/successeurs.get(src).size());
        }
        else
            return 0.0;
    }

    public void setDistribution(int position_encombree,int ma_position){
        if(noeuds.contains(position_encombree )){
            for (Integer i:noeuds)
            {
                if(i == position_encombree)
                {
                    distribution.put(i,1.0);
                }
                else
                {
                    distribution.put(i,0.0);
                }
            }
        }
        else{
            for (Integer i:noeuds)
            {
                distribution.put(i,1.0/(noeuds.size()-1));
            }
            distribution.put(ma_position,0.0);
        }
    }

    public boolean nodePresent(HashMap<Integer,HashSet<Integer>> successeurs){
        HashSet<Integer> nodes = new HashSet<>();
        nodes.addAll(successeurs.keySet());
        for (Integer i:successeurs.keySet()){
            nodes.addAll(successeurs.get(i));
        }
        return this.noeuds.containsAll(nodes);
    }

    public void updateModele(HashMap<Integer,HashSet<Integer>> successeurs,int ma_position){
        if(!nodePresent(successeurs))
        {
            this.successeurs = successeurs;
            noeuds.addAll(successeurs.keySet());
            for (Integer i:successeurs.keySet()){
                noeuds.addAll(successeurs.get(i));
            }
            this.distribution = new HashMap<>();
            setDistribution(-1,ma_position);
        }
    }

    public int tailleModele(){
        return distribution.keySet().size();
    }


    public static void main(String[] args){
        HashMap<Integer,HashSet<Integer>> succ = new HashMap<>();
        HashSet L1;
        L1 = new HashSet();
        L1.add(2);
        L1.add(3);
        succ.put(1,L1);
        L1 = new HashSet();
        L1.add(4);
        succ.put(3,L1);
        Markov_Model M = new Markov_Model(succ);
        M.setDistribution(10,2);
        System.out.println(M.distribution);

    }



}
