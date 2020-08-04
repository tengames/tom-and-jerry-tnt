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

import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tengames.tomandjerry.main.Assets;

public class Cloud extends ObjectSprite {
	public static final byte TYPE_MENU = 0;
	public static final byte TYPE_GAME = 1;

	private Vector2 initPos;
	private int velocity;
	private byte type;

	public Cloud(byte type, float x, float y, Vector2 size) {
		super(x, y, size.x, size.y);
		this.type = type;
		this.initPos = this.getPosition();
		setRandomRegion();
	}

	private void setRandomRegion() {
		switch (type) {
		case TYPE_MENU:
			if (this.getY() < 350)
				this.setPosition(initPos.x, initPos.y + MathUtils.random(100));
			else
				this.setPosition(initPos.x, initPos.y);
			this.setRegion(Assets.taObjects.findRegion("cloud"));
			this.setScale(1f - MathUtils.random(0.2f));
			velocity = 20 + MathUtils.random(20);
			break;

		case TYPE_GAME:
			if (this.getY() < 350)
				this.setPosition(initPos.x, initPos.y + MathUtils.random(100));
			else
				this.setPosition(initPos.x, initPos.y);
			int index1 = 1 + MathUtils.random(4);
			String cloud = "cloud-" + index1;
			this.setRegion(Assets.taObjects.findRegion(cloud));
			this.setScale(1f - MathUtils.random(0.2f));
			velocity = 20 + MathUtils.random(20);
			break;

		default:
			break;
		}
	}

	public void update(float deltaTime) {
		// update position
		float x = this.getX();
		this.setPosition(x + velocity * deltaTime, this.getY());
		// reset position
		if (this.getX() >= 900) {
			setRandomRegion();
		}
	}

	public void render(SpriteBatch batch) {
		this.draw(batch);
	}
}
