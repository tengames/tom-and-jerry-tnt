/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.tomandjerry.objects;

import java.util.ArrayList;

import woodyx.basicapi.camera.XCamera;
import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.physics.ObjectsJoint;
import woodyx.basicapi.screen.Asset;
import woodyx.basicapi.sound.SoundManager;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.tomandjerry.interfaces.GlobalVariables;
import com.tengames.tomandjerry.main.Assets;

public class Boom extends ObjectSprite {
	public static final byte POWER = 2;

	private static final float TIME_AFFECT = 0.05f;
	private static final short EXPLO_RANGE = 100;

	private XCamera camera;
	private ObjectsJoint jWeld;
	private ObjectModel model;
	private Circle circle, circleOut;
	private ParticleEffect efBurn, efExplosion;
	private float timeAffect, timeLife;
	private boolean isExplosed, canRemove, isProcExplo, isImmotal;

	public Boom(XCamera camera, World world, ObjectModel ground, TextureRegion texture, float x, float y, int number) {
		super(texture, x, y);
		this.camera = camera;
		isExplosed = false;
		isImmotal = false;
		isProcExplo = true;
		timeAffect = 0;
		timeLife = 0;

		setCanRemove(false);

		// create model
		model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE,
				new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, this.getPosition(),
				0, 1, 0.5f, 0.5f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, ("boom" + number));

		// create joint
		jWeld = new ObjectsJoint(model, ground, ObjectsJoint.WELD, false);
		jWeld.setAnchorA(0, 0);
		jWeld.setAnchorB(this.getX() + this.getWidth() / 2 - 400, this.getY() + this.getHeight() / 2 - 5 + 100);
		jWeld.createJoint(world);

		// create area explosion
		circle = new Circle(new Vector2(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2),
				EXPLO_RANGE / 2);
		circleOut = new Circle(new Vector2(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2),
				EXPLO_RANGE);

		// set filter
		model.getBody().getFixtureList().get(0).setSensor(true);

		// create effect
		efBurn = Asset.loadParticleEffect("drawable/objects/fire.p", Assets.taObjects);
		efExplosion = Asset.loadParticleEffect("drawable/objects/explosion.p", Assets.taExplosion);
		efBurn.setPosition(x, y + this.getHeight());
		efBurn.start();
		efExplosion.start();

	}

	public void kick() {
		if (!isExplosed) {
			// turn on flag
			isExplosed = true;
		}
	}

	public void checkInRange(ArrayList<CommonTool> targetCommonTools) {
		if (isExplosed) {
			// affect to common-tools
			for (CommonTool target : targetCommonTools) {
				if (circle.contains(target.getPosition()) || circle.contains(target.getPosCenter())
						|| circle.contains(target.getX(), (target.getY() + target.getHeight()))
						|| circle.contains((target.getX() + target.getWidth()), target.getY())
						|| circle.contains((target.getX() + target.getWidth()), (target.getY() + target.getHeight()))) {
					if (timeAffect > 0 && timeAffect < TIME_AFFECT) {
						target.affectExplosion(this.getPosition());
					}
				}
				if (circleOut.contains(target.getPosition()) || circleOut.contains(target.getPosCenter())
						|| circleOut.contains(target.getX(), (target.getY() + target.getHeight()))
						|| circleOut.contains((target.getX() + target.getWidth()), target.getY()) || circleOut
								.contains((target.getX() + target.getWidth()), (target.getY() + target.getHeight()))) {
					if (timeAffect > 0 && timeAffect < TIME_AFFECT) {
						target.affectExplosion(this.getPosition());
					}
				}
			}
		}
	}

	public void update(float deltaTime) {
		// update time life
		if (timeLife >= 0)
			timeLife += deltaTime;
		if (timeLife >= TIME_AFFECT) {
			isImmotal = true;
			timeLife = -1;
		}
		// update follow model
		if (model.getBody().isActive())
			this.updateFollowModel(model);
		// update effect
		if (!isExplosed) {
			// set new position
			efBurn.setPosition(this.getX(), this.getY() + this.getHeight());
			efBurn.update(deltaTime);
		} else {
			// count time affect
			if (timeAffect >= 0)
				timeAffect += deltaTime;
			if (timeAffect > TIME_AFFECT)
				timeAffect = -1;
			// effect vibration, sound
			if (isProcExplo) {
				if (camera.getFinishVibrate())
					camera.setVibrate(2, 5, 0.1f);
				// play sound
				SoundManager.playSound(Assets.soExplo);
				// turn off flag
				isProcExplo = false;
			}
			// set deactive model
			model.getBody().setActive(false);
			// set new position
			efExplosion.setPosition(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
			efExplosion.update(deltaTime);
		}
	}

	public void render(SpriteBatch batch) {
		// render effect
		if (!isExplosed) {
			this.draw(batch);
			efBurn.draw(batch);
		} else {
			efExplosion.draw(batch);
		}
	}

	public void renderDebug(ShapeRenderer shapeRender) {
		if (!isExplosed) {
			shapeRender.setProjectionMatrix(camera.combined);
			shapeRender.begin(ShapeType.Circle);
			shapeRender.setColor(Color.RED);
			shapeRender.circle(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, EXPLO_RANGE / 2);
			shapeRender.setColor(Color.BLUE);
			shapeRender.circle(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2, EXPLO_RANGE);
			shapeRender.end();
		}
	}

	public boolean checkCollision(Contact contact, ArrayList<Boom> booms, ArrayList<CommonTool> commonTools) {
		if (!canRemove) {
			// check contact with booms
			if (!booms.isEmpty()) {
				for (Boom boom : booms) {
					if (BoxUtility.detectCollision(contact, model, boom.getModel()))
						return true;
				}
			}
			// check contact with commonTools
			if (!commonTools.isEmpty()) {
				for (CommonTool commonTool : commonTools) {
					if (BoxUtility.detectCollision(contact, model, commonTool.getModel()))
						return true;
				}
			}
			return false;
		}
		return false;
	}

	public ObjectsJoint getJoint() {
		return jWeld;
	}

	public ObjectModel getModel() {
		return model;
	}

	public void setCanRemove(boolean canRemove) {
		this.canRemove = canRemove;
	}

	public boolean getCanRemove() {
		return canRemove;
	}

	public boolean getImmotal() {
		return isImmotal;
	}

	public boolean getExplosed() {
		return isExplosed;
	}
}
