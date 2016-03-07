package agent.planningagent;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import environnement.gridworld.ActionGridworld;
/**
 * Cet agent choisit une action aleatoire parmi toutes les autorisees dans chaque etat
 * @author lmatignon
 *
 */
public class AgentRandom extends PlanningValueAgent{
	
	private Random random;
	
	
	public AgentRandom(MDP _m) {
		super(_m);
		random = new Random();
	}

	@Override
	public Action getAction(Etat e) {
		List<Action> listeActionsPossibles = mdp.getActionsPossibles(e);
		if (listeActionsPossibles.size() == 0) {
			return null;
		}
		int index = random.nextInt(listeActionsPossibles.size());
		return listeActionsPossibles.get(index);
	}

	
	
	@Override
	public double getValeur(Etat _e) {
		return 0.0;
	}

	

	@Override
	public List<Action> getPolitique(Etat _e) {
		return mdp.getActionsPossibles(_e);
	}

	@Override
	public void updateV() {
		System.out.println("l'agent random ne planifie pas");
	}

	public void setGamma(double parseDouble) {
		// TODO Auto-generated method stub
		
	}




}
