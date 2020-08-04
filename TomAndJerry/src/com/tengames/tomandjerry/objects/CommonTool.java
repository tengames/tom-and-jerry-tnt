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

import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.physics.ObjectsJoint;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.tomandjerry.interfaces.GlobalVariables;
import com.tengames.tomandjerry.main.Assets;

public class CommonTool extends ObjectSprite {
	private ObjectModel mdObject;
	private String name;
	private int type;
	private boolean isDie;

	public CommonTool(World world, ObjectModel ground, int type, float weight, float restitution, float friction,
			float angle, Vector2 position, Vector2 size, String user) {
		super(position.x, position.y);
		this.type = type;
		this.isDie = false;

		switch (type) {
		case IconModel.TOM:
			name = "tom";
			break;

		case IconModel.JERRY:
			name = "jerry";
			break;

		case IconModel.ICE:
			name = "ice";
			break;

		case IconModel.STEEL:
			name = "steel";
			break;

		case IconModel.WOOD_1:
			name = "wood-1";
			break;

		case IconModel.WOOD_2:
			name = "wood-2";
			break;

		case IconModel.WOOD_3:
			name = "wood-3";
			break;

		case IconModel.WOOD_4:
			name = "wood-4";
			break;

		default:
			break;
		}

		this.setRegion(Assets.taObjects.findRegion(name));
		this.setSize(size.x, size.y);

		switch (type) {
		case IconModel.STEEL:
			mdObject = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, size, new Vector2(), 0,
					position, angle, 100, friction, restitution, GlobalVariables.CATEGORY_SCENERY,
					GlobalVariables.MASK_SCENERY, user);
			ObjectsJoint jWeld = new ObjectsJoint(ground, mdObject, ObjectsJoint.WELD, false);
			jWeld.getWeldJointDef().referenceAngle = angle * MathUtils.degreesToRadians;
			jWeld.setAnchorB(0, 0);
			jWeld.setAnchorA(this.getX() + this.getWidth() / 2 - 500 + 100,
					this.getY() + this.getHeight() / 2 - 5 + 100);
			jWeld.createJoint(world);
			break;

		case IconModel.TOM:
		case IconModel.JERRY:
			mdObject = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE, size, new Vector2(), size.y / 2,
					position, angle, weight, friction, restitution, GlobalVariables.CATEGORY_SCENERY,
					GlobalVariables.MASK_SCENERY, user);
			mdObject.getBody().setFixedRotation(true);
			break;

		default:
			mdObject = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.POLYGON, size, new Vector2(), 0,
					position, angle, weight, friction, restitution, GlobalVariables.CATEGORY_SCENERY,
					GlobalVariables.MASK_SCENERY, user);
			break;
		}
	}

	public void update(float deltaTime) {
		// update follow
		this.updateFollowModel(mdObject);
	}

	public void affectExplosion(Vector2 posExplo) {
		Vector2 oldPower = mdObject.getBody().getLinearVelocity();
		if (posExplo.x <= this.getX() + this.getWidth() / 2) {
			if (posExplo.y <= this.getY() + this.getHeight() / 2) {
				// x <= center, y <= center
				mdObject.getBody().setLinearVelocity(Boom.POWER + oldPower.x, Boom.POWER + oldPower.y);
			} else {
				// x <= center, y > center
				mdObject.getBody().setLinearVelocity(Boom.POWER + oldPower.x, -Boom.POWER + oldPower.y);
			}
		} else {
			if (posExplo.y <= this.getY() + this.getHeight() / 2) {
				// x > center, y <= center
				mdObject.getBody().setLinearVelocity(-Boom.POWER + oldPower.x, Boom.POWER + oldPower.y);
			} else {
				// x > center, y > center
				mdObject.getBody().setLinearVelocity(-Boom.POWER + oldPower.x, -Boom.POWER + oldPower.y);
			}
		}
	}

	public Vector2 getPosCenter() {
		return new Vector2(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
	}

	public ObjectModel getModel() {
		return this.mdObject;
	}

	public int getType() {
		return this.type;
	}

	public void setDie() {
		this.isDie = true;
	}

	public boolean getDie() {
		return this.isDie;
	}
}
