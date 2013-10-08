package test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.chalmers.dat255.risk.model.Deck;
import se.chalmers.dat255.risk.model.Game;
import se.chalmers.dat255.risk.model.IProvince;
import se.chalmers.dat255.risk.model.Player;
import se.chalmers.dat255.risk.model.TurnAndPhaseManager;
import se.chalmers.dat255.risk.model.TurnAndPhaseManager.Phase;

public class GameTest {

	Game game;
	Game game1;
	Game gameNoNeighbors;
	Game game2;
	Game game3;
	Game game4;

	@Before
	public void beforeAllTest() {

		String provinces = "A-B-C-D-E" + "\n" + "B-A-C-D-E" + "\n"
				+ "C-B-E-A-D" + "\n" + "D-A-C-E-B" + "\n" + "E-A-B-D-C";
		String provinces1 = "A" + "\n" + "B" + "\n" + "C" + "\n" + "D" + "\n"
				+ "E";
		String continents = "3-A-B-C" + "\n" + "2-D-E";
		String[] name = new String[] { "Linnea", "Andreas", "Emil", "Bergman",
				"Christoffer", "Emma" };
		game = new Game();
		game.setupGame(name, provinces, continents);

		String[] name1 = new String[] { "Christoffer", "Emma" };
		game1 = new Game();
		game1.setupGame(name1, provinces, continents);

		gameNoNeighbors = new Game();
		gameNoNeighbors.setupGame(name1, provinces1, continents);

		String[] name2 = new String[] { "Andreas", "Emil", "Bergman" };
		game2 = new Game();
		game2.setupGame(name2, provinces, continents);

		String[] name3 = new String[] { "Andreas", "Emil", "Bergman",
				"Christoffer" };
		game3 = new Game();
		game3.setupGame(name3, provinces, continents);
		String[] name4 = new String[] { "Andreas", "Emil", "Bergman",
				"Christoffer", "Emma" };

		game4 = new Game();
		// This is because we for every new testMethod wants the same deck.
		Deck.getInstance().getDeckList().clear();
		game4.setupGame(name4, provinces, continents);
	}

	@Test
	public void testActivePlayer() {
		// Checks if the active player is correct
		assertTrue(game.getActivePlayer().getName() == "Linnea");
		assertTrue(game1.getActivePlayer().getName() == "Christoffer");
		assertTrue(game2.getActivePlayer().getName() == "Andreas");
	}

	@Test
	public void testSizeOfPlayerList() {
		// Check if number of player is correct,
		assertTrue(game.getPlayers().size() == 6);
		assertTrue(game1.getPlayers().size() == 2);
		assertTrue(game2.getPlayers().size() == 3);
		assertTrue(game3.getPlayers().size() == 4);
		assertTrue(game4.getPlayers().size() == 5);

	}

	@Test
	public void testSetUpDeck() {
		Deck deck = Deck.getInstance();

		// Because we have to know we have created the deck on the right way in
		// game constructor
		assertTrue(deck.getSize() == game.getGameProvinces().size() + 6);
	}

	@Test
	public void testChangePhase() {
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.FBuild);
		game4.handlePhaseEvent();

		// First we have to place out our bonusUnits.
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.FBuild);

		// We can just place units at the province that the active player owns.
		// Therefor
		// Game4 because the number of players are equal to the number of
		// provinces
		IProvince myProvince = null;
		for (int i = 0; i < game4.getPlayers().size(); i++) {
			myProvince = getPlayerProvince(game4.getActivePlayer(),
					game4.getGameProvinces(), game4);

			this.looseAllBonusUnitsLeft(myProvince, game4);

			assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.FBuild);
			game4.handlePhaseEvent();
		}

		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.F1);

		// Have to place out bonus units first!
		game4.handlePhaseEvent();
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.F1);

		myProvince = getPlayerProvince(game4.getActivePlayer(),
				game4.getGameProvinces(), game4);

		this.looseAllBonusUnitsLeft(myProvince, game4);

		game4.handlePhaseEvent();
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.F2);

		game4.handlePhaseEvent();
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.F3);

		game4.handlePhaseEvent();
		assertTrue(game4.getCurrentPhase() == TurnAndPhaseManager.Phase.F1);
	}

	@Test
	public void testDealCard() {
		int nmbCard = game.getActivePlayer().getCards().size();
		game.getActivePlayer().addCard();
		assertTrue(nmbCard + 1 == game.getActivePlayer().getCards().size());
	}

	@Test
	public void testProvinceHandle() {
		// In Phase Build we will set out bonus units to the incoming province
		// if there are any left. We will only place the bonus if the active
		// player owns the incoming province
		IProvince myProvince = null;
		IProvince notMine = null;
		ArrayList<IProvince> provinces = game4.getGameProvinces();
		myProvince = getPlayerProvince(game4.getActivePlayer(), provinces,
				game4);
		notMine = getPlayerProvince(game4.getPlayers().get(1), provinces, game4);

		// Test so bonus not change with a province that not belongs to another
		// player.
		int bonusFromStart = game4.getBonusUnitsLeft();
		game4.handleProvinceEvent(notMine);
		assertTrue(bonusFromStart == game4.getBonusUnitsLeft());

		// Test so bonus change with a province that the active payer owns.
		this.looseAllBonusUnitsLeft(myProvince, game4);
		assertTrue(0 == game4.getBonusUnitsLeft());
		assertTrue(myProvince.getUnits() == 1 + bonusFromStart);

		// When the bonus has been placed nothing will happen when you press at
		// a province
		game4.handleProvinceEvent(myProvince);
		assertTrue(0 == game4.getBonusUnitsLeft());

		// changing phase until we get to phase1.
		game4.handlePhaseEvent();
		for (int i = 0; i < game4.getPlayers().size() - 1; i++) {
			myProvince = getPlayerProvince(game4.getActivePlayer(), provinces,
					game4);
			this.looseAllBonusUnitsLeft(myProvince, game4);
			game4.handlePhaseEvent();
		}

		// In phase1 you will place your bonus once again.
		// Test so bonus not change with a province that not belongs to another
		// player.
		int bonusFromF1 = game4.getBonusUnitsLeft();
		game4.handleProvinceEvent(notMine);
		assertTrue(bonusFromF1 == game4.getBonusUnitsLeft());

		// Test so bonus change with a province that the active payer owns.

		myProvince = getPlayerProvince(game4.getActivePlayer(), provinces,
				game4);
		this.looseAllBonusUnitsLeft(myProvince, game4);
		assertTrue(myProvince.getUnits() == 1 + bonusFromF1 + bonusFromStart);
		assertTrue(0 == game4.getBonusUnitsLeft());

		game4.handlePhaseEvent();

		// In Phase F2 you can attack another province, unless its not your own
		// and you are neighbors.
		// The first province we must "handle", must be the active players
		// province, and the next must be
		// the first Province neighbor and not owned by the activ player.
		// Nothing will happen until we use
		// the method attack. Attack can only be called in this moment.
		game4.handleProvinceEvent(myProvince);
		assertTrue(0 == game4.getBonusUnitsLeft());
		assertTrue(myProvince.getUnits() == 1 + bonusFromF1 + bonusFromStart);

		// In Phase 3 you can move units from one of your province to one other.
		// They must be owned by active player and they also must be neighbors.
		// Because every player just have got one Province each we test this
		// with game1 (there are all Provinces neighbors) and gameNoNeighbors.

		provinces = game1.getGameProvinces();
		ArrayList<IProvince> provinces1 = gameNoNeighbors.getGameProvinces();

		// Set up 2 provinces, for each game, that belongs to the active player
		// and one that belongs to the other player.
		myProvince = getPlayerProvince(game1.getActivePlayer(), provinces,
				game1);
		IProvince myProvince1 = getPlayerProvince(
				gameNoNeighbors.getActivePlayer(), provinces1, gameNoNeighbors);

		IProvince player2P = getPlayerProvince(game1.getPlayers().get(1),
				provinces1, game1);
		IProvince player2P1 = getPlayerProvince(gameNoNeighbors.getPlayers()
				.get(1), provinces1, gameNoNeighbors);

		IProvince mP1 = getAnotherProvinceFromPlayer(game1, myProvince);
		IProvince mPNotNeighbors = getAnotherProvinceFromPlayer(
				gameNoNeighbors, myProvince1);

		// Now we will get to phase 3
		getToPhase3(game1);
		getToPhase3(gameNoNeighbors);

		// Testing move units from noNeighbors- this should not work!
		gameNoNeighbors.handleProvinceEvent(myProvince1);
		gameNoNeighbors.handleProvinceEvent(mPNotNeighbors);
		try {
			gameNoNeighbors.moveToProvince(5);
			fail("should've thrown an exception");
		} catch (Throwable expected) {
			assertEquals(NullPointerException.class, expected.getClass());
		}

		game1.handleProvinceEvent(myProvince);
		game1.handleProvinceEvent(mP1);
		game1.moveToProvince(1);

	}

	/*
	 * Returns a IProvince that player owns
	 */
	private IProvince getPlayerProvince(Player player,
			ArrayList<IProvince> provinces, Game game) {
		IProvince province = null;
		int i = 0;

		while (province == null) {
			if (player.getId() == game.getOwner(provinces.get(i).getId())) {
				province = provinces.get(i);
			}
			i++;
		}
		return province;
	}

	// For not so much duplicated code
	private void getToPhase1(Game game) {
		ArrayList<IProvince> belongsToPlayer = new ArrayList<IProvince>();
		for (Player player : game.getPlayers()) {
			belongsToPlayer.add(getPlayerProvince(player,
					game.getGameProvinces(), game));
		}
		for (int i = 0; i < game.getPlayers().size(); i++) {
			this.looseAllBonusUnitsLeft(belongsToPlayer.get(i), game);
			game.handlePhaseEvent();
		}
	}

	// For not so much duplicated code
	private void getToPhase2(Game game) {
		IProvince myProvince = getPlayerProvince(game.getActivePlayer(),
				game.getGameProvinces(), game);
		getToPhase1(game);
		looseAllBonusUnitsLeft(myProvince, game);
		game.handlePhaseEvent();
	}

	// For not so much duplicated code
	private void getToPhase3(Game game) {
		getToPhase2(game);
		game.handlePhaseEvent();

	}

	/*
	 * place out all bonus at the province
	 */
	private void looseAllBonusUnitsLeft(IProvince myProvince, Game game) {
		if (game.getActivePlayer().getId() == game.getOwner(myProvince.getId())) {
			while (game.getBonusUnitsLeft() > 0) {
				game.handleProvinceEvent(myProvince);
			}
		}
	}

	private IProvince getAnotherProvinceFromPlayer(Game game,
			IProvince myProvince) {
		for (IProvince province : game.getGameProvinces()) {
			int owner = game.getOwner(myProvince.getId());
			if (owner == game.getOwner(province.getId()) && myProvince != province) {
				return province;
			}
		}
		return null;
	}

	@Test
	public void testFlushProvinces() {
		// To test flush we need to be in phase 2 or 3, this time we choose 3
		// because it is easy.
		ArrayList<IProvince> provinces = game1.getGameProvinces();
		getToPhase3(game1);
		IProvince myProvince = getPlayerProvince(game1.getActivePlayer(),
				provinces, game1);
		IProvince myProvince1 = getAnotherProvinceFromPlayer(game1, myProvince);

		game1.handleProvinceEvent(myProvince);
		game1.handleProvinceEvent(myProvince1);
		game1.flushProvinces();
		//now you cannot move Provinces because you have no where to move them.
		try {
			gameNoNeighbors.moveToProvince(1);
			fail("should've thrown an exception");
		} catch (Throwable expected) {
			assertEquals(NullPointerException.class, expected.getClass());
		}
		
		//Without flush this should work
		game1.handleProvinceEvent(myProvince);
		game1.handleProvinceEvent(myProvince1);
		game1.moveToProvince(1);
		

	}

	@Test
	public void testMoveTroops() {
		ArrayList<IProvince> provinces = game1.getGameProvinces();
		getToPhase3(game1);
		IProvince myProvince = getPlayerProvince(game1.getActivePlayer(),
				provinces, game1);
	}

}
