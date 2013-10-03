package se.chalmers.dat255.risk.controller;

import se.chalmers.dat255.risk.model.IGame;
import se.chalmers.dat255.risk.view.PopUp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PopUpListener extends ClickListener {

	IGame model;

	public PopUpListener(IGame model) {
		this.model = model;
		Gdx.app.log("construct", "popup ");
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		// I can't for the life of me figure out why I can't reach the buttons
		// at once
		if (event.getTarget() instanceof Label) {

			PopUp pop = (PopUp) event.getListenerActor();
			String name = event.getTarget().getParent().getName();

			if (pop.getTitle().equalsIgnoreCase("Attack")) {
				if (name.equals("confirm")) {
					System.out.println("In attack = " + pop.getValue());
					model.battle((int) pop.getValue());
				} else if (name.equals("cancel")) {
					// model.don'tDoSomething?
				}
			} else if (pop.getTitle().equalsIgnoreCase("Movement")) {
				if (name.equals("confirm")) {
					System.out.println("In movement = " + pop.getValue());
					model.moveToProvince((int) pop.getValue());
				} else if (name.equals("cancel")) {
					// model.don'tDoSomething?
				}
			} else if (pop.getTitle().equalsIgnoreCase("Again?")) {
				if (name.equals("confirm")) {
					System.out.println("In again = " + pop.getValue());
					model.battle((int) pop.getValue());
				} else if (name.equals("cancel")) {
					// model.don'tDoSomething?
				}
			}
		}
	}
}