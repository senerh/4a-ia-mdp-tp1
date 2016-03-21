package agent.planningagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.gridworld.ActionGridworld;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;
	private Map<Etat, Double> V;
	private Random random;

	/**
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma, MDP mdp) {
		super(mdp);
		this.gamma = gamma;
		random = new Random(System.currentTimeMillis());
		
		V = new HashMap<Etat, Double>();
		for (Etat etat : mdp.getEtatsAccessibles()) {
			V.put(etat, 0.0);
		}
	}

	public ValueIterationAgent(MDP mdp) {
		this(0.9, mdp);
	}
	
	/** 
	 * Mise a jour de V: effectue UNE iteration de value iteration 
	 */
	@Override
	public void updateV() {
		delta = 0.0;
		
		HashMap<Etat, Double> Vk = new HashMap<Etat, Double>();
		
		for (Etat etat : mdp.getEtatsAccessibles()) {
			List<Action> listActions = mdp.getActionsPossibles(etat);
			double max = 0;
			for (Action action : listActions) {
				Map<Etat, Double> listEtatsVoisins = null;
				try {
					listEtatsVoisins = mdp.getEtatTransitionProba(etat, action);
				} catch (IllegalActionException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				double somme = 0;
				for (Entry<Etat, Double> entry : listEtatsVoisins.entrySet()) {
					double t = entry.getValue();
					double r = mdp.getRecompense(etat, action, entry.getKey());
					double v = V.get(entry.getKey());
					somme += t * (r + gamma  * v);
				}
				max = (somme > max) ? somme : max; 
			}
			Vk.put(etat, max);
			double d = Math.abs(V.get(etat) - Vk.get(etat));
			delta = (d > delta) ? d : delta;
		}
		
		V = Vk;
		vmax = Double.MIN_VALUE;
		vmin = Double.MAX_VALUE;
		for (Double value : V.values()) {
			vmax = (value > vmax) ? value : vmax; 
			vmin = (value < vmin) ? value : vmin;
		}

		this.notifyObs();
	}
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 */
	@Override
	public Action getAction(Etat e) {
		List<Action> listActions = getPolitique(e);
		if (listActions.isEmpty()) {
			return ActionGridworld.NONE;
		} else if (listActions.size() == 1) {
			return listActions.get(0);
		} else {
			return listActions.get(random.nextInt(listActions.size()));
		}
	}
	
	@Override
	public double getValeur(Etat _e) {
		if (V.containsKey(_e)) {
			return V.get(_e);
		}
		return 0.0;
	}
	
	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		List<Action> l = new ArrayList<Action>();

		List<Action> listActions = mdp.getActionsPossibles(_e);
		
		double max = 0;
		for (Action action : listActions) {
			Map<Etat, Double> listEtatsVoisins = null;
			try {
				listEtatsVoisins = mdp.getEtatTransitionProba(_e, action);
			} catch (IllegalActionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			double somme = 0;
			for (Entry<Etat, Double> entry : listEtatsVoisins.entrySet()) {
				double t = entry.getValue();
				double r = mdp.getRecompense(_e, action, entry.getKey());
				double v = V.get(entry.getKey());
				somme += t * (r + gamma  * v);
			}
			if (somme > max) {
				l.clear();
				max = somme;
			}
			if (somme == max) {
				l.add(action);
			}
		}
		
		return l;
	}
	
	@Override
	public void reset() {
		super.reset();
		V.clear();
		for (Etat etat : mdp.getEtatsAccessibles()) {
			V.put(etat, 0.0);
		}
		this.notifyObs();
	}

	public void setGamma(double arg0) {
		this.gamma = arg0;
	}
	
}
