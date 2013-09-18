package se.chalmers.dat255.risk.model;

import java.io.File;

/**
 * The top game class. Controls flow between our lower classes, such as the
 * battle handler and the WorldMap.
 * 
 */

public class Game implements IGame {
	private Player[] players;
	private int activePlayer;
	private int currentPhase;
	private WorldMap worldMap;
	private int bonus;
	private BattleHandler battle;

	/**
	 * Creates a new Game.
	 * 
	 * @param playersId
	 *            The ids of the players
	 */
	public Game(String[] playersId) {
		for (int i = 0; i < playersId.length; i++) {
			players[i] = new Player(i, playersId[i]);
		}
		currentPhase = 1;

		worldMap = new WorldMap(new File("neighbours.txt"), players);

		battle = new BattleHandler();
	}

	@Override
	public void changePhase() {
		currentPhase++;
	}

	@Override
	public void changeTurn() {
		// TODO: Check this!
		activePlayer = (activePlayer + 1) % players.length;
		currentPhase = 1;
	}
	
	@Override
	public void moveToProvince(int nbrOfUnits, IProvince from, IProvince goTo){
		if(worldMap.getOwner(goTo.getId()) ==  getActivePlayer()){
			from.moveUnits(nbrOfUnits, goTo);
		}		
	}

	@Override
	public void attack(int offensiveDice, IProvince offensive, IProvince defensive) {
		// TODO decide number of attackers
		//		check if ok in another method
		
		// Counts the number of defending units
		int defensiveDice = defensive.getUnits() == 1 ? 1 : 2;
		
		int[] result = battle.doBattle(offensiveDice,
				defensiveDice);

		offensive.removeUnits(result[0]);
		defensive.removeUnits(result[1]);

		if (defensive.getUnits() == 0) {
			//TODO	move attacking units into 'defensive'
			worldMap.changeOwner(defensive.getId(), getActivePlayer());
			
		}

	}

	@Override
	public Player getActivePlayer() {
		return players[activePlayer];
	}

	@Override
	public void dealCard() {
		getActivePlayer().addCard();
	}

	@Override
	public void calcBonusUnits() {
		int provinces = getActivePlayer().getNrOfProvinces();
		if(provinces <= 9){
			this.bonus = 3; 
		} else {
			this.bonus = provinces/3;
		}
	}

	@Override
	public void placeBonusUnits(int units, IProvince province) {
		province.addUnits(units);
		bonus = bonus - units;
	}

	@Override
	public int getBonusUnitsLeft() {
		return bonus;
	}

}
