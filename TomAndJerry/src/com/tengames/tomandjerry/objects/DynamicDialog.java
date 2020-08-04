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

import woodyx.basicapi.gui.XDialog;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tengames.tomandjerry.main.Assets;

public class DynamicDialog extends XDialog {
	public static final byte TYPE_WIN = 0;
	public static final byte TYPE_FAIL = 1;
	public static final byte TYPE_CONFIRM = 2;

	private Button btMenu, btReplay, btNext;
	private int value;

	public DynamicDialog(WindowStyle windowStyle, TextureRegion trBackground, Vector2 size, Vector2 position,
			Vector2 target, int boomUsed, int totalBoom, byte type) {
		super(windowStyle, trBackground, size, position, target, TYPE_0);
		dialog.setTouchable(Touchable.disabled);
		switch (type) {
		case TYPE_WIN:
			Image imgJerry = new Image(Assets.taObjects.findRegion("jerry-menu"));
			imgJerry.setPosition(-10, 240);
			this.addActor(imgJerry);
			Image imgButter = new Image(Assets.taObjects.findRegion("butter"));
			imgButter.setPosition(150, 260);
			this.addActor(imgButter);
			Image imgText = new Image(Assets.taObjects.findRegion("text-win"));
			imgText.setSize(310 * 0.8f, 91 * 0.8f);
			imgText.setPosition((374 - imgText.getWidth()) / 2, 160);
			this.addActor(imgText);
			Image imgCup = null;
			if ((boomUsed / totalBoom) < 0.5f) {
				imgCup = new Image(Assets.taObjects.findRegion("cup"));
				imgCup.setScale(0.8f);
				setValue(2);
			} else {
				imgCup = new Image(Assets.taObjects.findRegion("star"));
				setValue(1);
			}
			imgCup.setPosition((374 - imgCup.getWidth()) / 2, 110);
			imgCup.setRotation(-15);
			this.addActor(imgCup);
			// alert text
			Label lbUse = new Label("Boom used: " + boomUsed + "/" + totalBoom, Assets.lbSNumber);
			lbUse.setPosition((trBackground.getRegionWidth() - lbUse.getWidth()) / 2, 60);
			this.addActor(lbUse);

			// button
			btMenu = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-menu")));
			btMenu.setSize(83 * 0.6f, 83 * 0.6f);
			btMenu.setPosition(50, -5);
			this.addActor(btMenu);

			btReplay = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-replay")));
			btReplay.setPosition(162, -5);
			this.addActor(btReplay);

			btNext = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-next")));
			btNext.setPosition(270, -5);
			this.addActor(btNext);
			break;

		case TYPE_FAIL:
			Image imgTom = new Image(Assets.taObjects.findRegion("tom-menu"));
			imgTom.setPosition(-10, 160);
			this.addActor(imgTom);
			Image imgButters = new Image(Assets.taObjects.findRegion("butter"));
			imgButters.setPosition(160, 180);
			this.addActor(imgButters);
			Image imgTextf = new Image(Assets.taObjects.findRegion("text-fail"));
			imgTextf.setSize(310 * 0.8f, 91 * 0.8f);
			imgTextf.setPosition((374 - imgTextf.getWidth()) / 2, 90);
			this.addActor(imgTextf);

			// button
			btMenu = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-menu")));
			btMenu.setSize(83 * 0.6f, 83 * 0.6f);
			btMenu.setPosition(50, -5);
			this.addActor(btMenu);

			btReplay = new Button(new TextureRegionDrawable(Assets.taObjects.findRegion("bt-replay")));
			btReplay.setPosition(270, -5);
			this.addActor(btReplay);
			break;

		case TYPE_CONFIRM:

			break;

		default:
			break;
		}
	}

	public Button getBtMenu() {
		return btMenu;
	}

	public Button getBtReplay() {
		return btReplay;
	}

	public Button getBtNext() {
		return btNext;
	}

	public int getValue() {
		return value;
	}

	private void setValue(int value) {
		this.value = value;
	}

}
