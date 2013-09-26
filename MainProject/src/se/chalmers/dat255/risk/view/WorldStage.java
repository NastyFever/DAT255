package se.chalmers.dat255.risk.view;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import se.chalmers.dat255.risk.model.IProvince;
import se.chalmers.dat255.risk.view.resource.Resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WorldStage extends AbstractStage implements GestureListener {
	private Image background;
	private Group provinceGroup;
	private OrthographicCamera camera;
	private float initialZoom;
	private BoundingBox[] bounds;
	private float width;
	private float height;
	private InputMultiplexer multi;
	
	public WorldStage(List<IProvince> provinces) {
		
		background = new Image(Resource.getInstance().backGround);
		camera = new OrthographicCamera();
		provinceGroup = new Group();
		
		multi = new InputMultiplexer(new GestureDetector(this), this);
		camera.setToOrtho(false);
		setCamera(camera);
		
		width = background.getWidth();
		height = background.getHeight();

		camera.position.set(background.getWidth() / 2,
				background.getHeight() / 2, 0); //
		// Texture.setEnforcePotImages(false); Solves power of two?

		actor = new ArrayList<AbstractView>();

		for (int i = 0; i < provinces.size(); i++) {
			Gdx.app.log("ProvinceView", "create ProvinceView");
			ProvinceView provinceView = new ProvinceView(provinces.get(i));
			actor.add(provinceView);

		}
		
		
		addActor(background);
		
		// cardButton = new TextButton("Cards", new
		// TextButton.TextButtonStyle());
		// cardButton.setBackground("Cards");
		// cardButton.setColor(Color.GREEN);
		// addActor(cardButton);

		for (int i = 0; i < provinces.size(); i++) {
			provinceGroup.addActor(actor.get(i));
		}
		addActor(provinceGroup);
		// addActor(new ChangePhase());

		// add bounds for map
		bounds = new BoundingBox[4];
		bounds[0] = new BoundingBox(new Vector3(0, 0, 0), new Vector3(0,
				height, 0));
		bounds[1] = new BoundingBox(new Vector3(0, 0, 0), new Vector3(width, 0,
				0));
		bounds[2] = new BoundingBox(new Vector3(0, height, 0), new Vector3(
				width, height, 0));
		bounds[3] = new BoundingBox(new Vector3(width, 0, 0), new Vector3(0,
				height, 0));
		for (Vector3 d : bounds[2].getCorners()) {
			Gdx.app.log("tag", "" + d);
		}

		Dialog d = new Dialog("ksbfkjsbf", new WindowStyle(new BitmapFont(),
				Color.RED, new TextureRegionDrawable(new TextureRegion(
						Resource.getInstance().bucket))));
		d.size(50, 50);
		d.setPosition(200, 200);
	}
	
	@Override
	public InputProcessor getProcessor(){
		return multi;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		initialZoom = camera.zoom;
		super.touchDown((int) x,(int) y, pointer, button);
		
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		Gdx.app.log("movment", "X: " + x + " Y: " + y + " inbounds: "
				+ inBounds());
		if (inBounds()) {
			getCamera().position.x -= deltaX;
			getCamera().position.y += deltaY;
		} else {
			// getCamera().position.x = x;
			// getCamera().position.y = y;
		}
		return false;
	}

	private boolean inBounds() {

		for (int i = 0; i < bounds.length; i++) {
			if (camera.frustum.boundsInFrustum(bounds[i])) {
				// return false;
			}
		}
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {

		float ratio = initialDistance / distance;

		camera.zoom = initialZoom * ratio;

		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}
}
