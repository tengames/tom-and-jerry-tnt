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
package com.tengames.tomandjerry.main;

import woodyx.basicapi.screen.Asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Assets extends Asset {
	public static Texture txBg0, txBg1, txBg2, txBg3;
	public static TextureAtlas taObjects, taExplosion, taSkin;
	public static BitmapFont fNumber, fNormal;
	public static LabelStyle lbSNumber, lbSNormal;
	public static Music muGame1, muGame2, muMenu, muStage;
	public static Sound soButter, soBtClick, soExplo, soFail, soWin;

	public static void loadResLoading() {
		loading("drawable/", "atlas", "loading");
		assetManager.finishLoading();
	}

	public static void load() {
		// loading backgrounds
		loading("drawable/backgrounds/", "jpg", "bg0", "bg1", "bg2", "bg3");
		// loading objects
		loading("drawable/objects/", "atlas", "objects", "explosion", "uiskin");
		// loading fonts
		loading("fonts/", "png", "numfont");
		// loading sound, music
		loading("raw/", "mp3", "background1", "background2", "backgroundmenu", "backgroundstage");
		loading("raw/", "ogg", "butterdrop", "buttonclick", "explosion", "fail", "win");
	}

	public static void unload() {

	}

	public static void loadDone() {
		// loaded backgrounds
		txBg0 = assetManager.get("drawable/backgrounds/bg0.jpg");
		txBg0.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txBg1 = assetManager.get("drawable/backgrounds/bg1.jpg");
		txBg1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txBg2 = assetManager.get("drawable/backgrounds/bg2.jpg");
		txBg2.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		txBg3 = assetManager.get("drawable/backgrounds/bg3.jpg");
		txBg3.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		// loaded objects
		taObjects = assetManager.get("drawable/objects/objects.atlas");
		taExplosion = assetManager.get("drawable/objects/explosion.atlas");
		taSkin = assetManager.get("drawable/objects/uiskin.atlas");

		// loaded fonts
		Texture txNumFont = assetManager.get("fonts/numfont.png");
		txNumFont.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fNumber = new BitmapFont(Gdx.files.internal("fonts/numfont.fnt"), new TextureRegion(txNumFont), false);
		fNumber.setScale(0.7f);
		fNormal = new BitmapFont(Gdx.files.internal("fonts/numfont.fnt"), new TextureRegion(txNumFont), false);
		lbSNumber = new LabelStyle();
		lbSNumber.font = fNumber;
		lbSNormal = new LabelStyle();
		lbSNormal.font = fNormal;

		// loaded sound, music
		muGame1 = assetManager.get("raw/background1.mp3");
		muGame2 = assetManager.get("raw/background2.mp3");
		muMenu = assetManager.get("raw/backgroundmenu.mp3");
		muStage = assetManager.get("raw/backgroundstage.mp3");

		soBtClick = assetManager.get("raw/buttonclick.ogg");
		soButter = assetManager.get("raw/butterdrop.ogg");
		soExplo = assetManager.get("raw/explosion.ogg");
		soFail = assetManager.get("raw/fail.ogg");
		soWin = assetManager.get("raw/win.ogg");
	}

}
