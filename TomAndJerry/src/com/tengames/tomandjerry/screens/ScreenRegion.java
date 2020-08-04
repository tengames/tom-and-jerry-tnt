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
package com.tengames.tomandjerry.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tengames.tomandjerry.main.Assets;
import com.tengames.tomandjerry.main.TomAndJerry;
import com.tengames.tomandjerry.objects.Cloud;
import com.tengames.tomandjerry.objects.DynamicButton;

public class ScreenRegion extends XScreen implements Screen {
	public static boolean canNextLevel11 = false, canNextLevel21 = false;

	private TomAndJerry coreGame;
	private Stage stage;
	private ScrollPane scroll;
	private BufferedReader reader;
	private String strJson;
	private ArrayList<DynamicButton> maps;
	private DynamicButton[] buttons;
	private Cloud[] clouds;
	private ArrayList<Integer> mapInfor;

	public ScreenRegion(TomAndJerry coreGame) {
		super(800, 480);
		this.coreGame = coreGame;
		initialize();
	}

	@Override
	public void initialize() {
		/* loading data */
//		try {
//			reader = new BufferedReader(new FileReader("/home/woodyx/workspace-gdx/TomAndJerryAndroid/assets/data/tomandjerry.txt"));
//		} catch (FileNotFoundException e) {}
		reader = coreGame.androidListener.getData();

		mapInfor = new ArrayList<Integer>();

		// get data
		for (int i = 0; i < 30; i++) {
			int value = coreGame.androidListener.getValue(i);
			if (value == 0)
				break;
			mapInfor.add(value);
		}

		// play music
		SoundManager.playMusic(Assets.muStage, 0.5f, true);

		// create stage
		stage = new Stage(800, 480, true) {
			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Keys.BACK) {
					// back to menu
					coreGame.setScreen(new ScreenMenu(coreGame));

					// show admob
					if (MathUtils.random(100) > 50)
						coreGame.androidListener.showIntertitial();
				}
				return super.keyUp(keyCode);
			}

		};
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		// set inputprocessor
		Gdx.input.setInputProcessor(stage);

		// create buttons
		buttons = new DynamicButton[3];

		// button menu
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("text-menu"), new Vector2(20, 410), 0.6f, 0.1f);
		buttons[0].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// back to menu
				coreGame.setScreen(new ScreenMenu(coreGame));

				// show admob
				if (MathUtils.random(100) > 50)
					coreGame.androidListener.showIntertitial();
			}
		});

		// logo
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("text-stage"), new Vector2(200, 600),
				new Vector2(200, 400), 0);

		// button sound
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(740, 420), 0.6f, 0.2f);
		if (!SoundManager.SOUND_ENABLE)
			buttons[2].getButton().setChecked(true);
		buttons[2].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muStage);
				} else {
					SoundManager.playMusic(Assets.muStage, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
			}
		});

		// add to stage
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}

		// create maps
		maps = new ArrayList<DynamicButton>();
		float timeDelay = 0;

		Table table = new Table();
		table.setSize(600, 240);
		table.setBounds(100, 30, 600, 240);
		for (int i = 0; i < 15; i++) {
			DynamicButton button = null;
			if (mapInfor.size() > 15) {
				button = new DynamicButton(Assets.taObjects.findRegion("stage"),
						Assets.taObjects.findRegion("stage-lock"), mapInfor.get(i).intValue(), maps.size() + 1,
						timeDelay);
			} else {
				if (i < mapInfor.size())
					button = new DynamicButton(Assets.taObjects.findRegion("stage"),
							Assets.taObjects.findRegion("stage-lock"), mapInfor.get(i).intValue(), maps.size() + 1,
							timeDelay);
				else
					button = new DynamicButton(Assets.taObjects.findRegion("stage"),
							Assets.taObjects.findRegion("stage-lock"), 0, maps.size() + 1, timeDelay);
			}
			maps.add(button);
			timeDelay += maps.size() * 0.01f;
			table.add(button).pad(10, 25, 10, 25);
			if ((i + 1) % 5 == 0)
				table.row();
		}

		Table table2 = new Table();
		table2.setSize(600, 240);
		table2.setBounds(100, 30, 600, 240);
		for (int i = 0; i < 15; i++) {
			DynamicButton button = null;
			if (mapInfor.size() < 15) {
				button = new DynamicButton(Assets.taObjects.findRegion("stage"),
						Assets.taObjects.findRegion("stage-lock"), 0, maps.size() + 1, timeDelay);
			} else {
				if (i < mapInfor.size() - 15)
					button = new DynamicButton(Assets.taObjects.findRegion("stage"),
							Assets.taObjects.findRegion("stage-lock"), mapInfor.get(i).intValue(), maps.size() + 1,
							timeDelay);
				else
					button = new DynamicButton(Assets.taObjects.findRegion("stage"),
							Assets.taObjects.findRegion("stage-lock"), 0, maps.size() + 1, timeDelay);
			}
			maps.add(button);
			timeDelay += maps.size() * 0.01f;
			table2.add(button).pad(10, 25, 10, 25);
			if ((i + 1) % 5 == 0)
				table2.row();
		}

		Table bTable = new Table();
		bTable.add(table).padLeft(100).padRight(80);
		bTable.add(table2).padLeft(120).padRight(150);

		scroll = new ScrollPane(bTable);
		scroll.setSmoothScrolling(true);
		scroll.setBounds(0, 0, 800, 480 - 100);
		stage.addActor(scroll);

		// add listener for stages
		for (int i = 0; i < maps.size(); i++) {
			final int map = i;
			maps.get(map).getButton().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soBtClick);
					// go to screen game
					if (maps.get(map).getValue() != 0)
						export(map);
				}
			});
		}

		// create clouds
		clouds = new Cloud[5];
		for (int i = 0; i < clouds.length; i++) {
			clouds[i] = new Cloud(Cloud.TYPE_MENU, -200 + MathUtils.random(100), 300 + MathUtils.random(180),
					new Vector2(150 / 2, 82 / 2));
		}
	}

	private void export(int number) {
		String line = null, strExport = "MAP: " + (number + 1);
		try {
			while ((line = reader.readLine()) != null) {
				if (line.equals(strExport)) {
					// export strJson
					strJson = reader.readLine();
					break;
				}
			}
			// close reader
			reader.close();
			// set new Screen
			coreGame.setScreen(new ScreenGame(coreGame, strJson, (number + 1)));
		} catch (IOException e) {
		}
	}

	@Override
	public void update(float deltaTime) {
		// check can next stage
		if (canNextLevel11) {
			export(10);
			canNextLevel11 = false;
		}
		if (canNextLevel21) {
			export(20);
			canNextLevel21 = false;
		}

		// update stage
		stage.act(deltaTime);
		// update for scroll
		if (!scroll.isFlinging() && !scroll.isPanning()) {
			float min = 800 / 2;
			float x = scroll.getScrollX();

			for (int i = 0; i < 2; i++)
				if (Math.abs(x - 800 * i) < Math.abs(min))
					min = x - 800 * i;
			if (min != 0) {
				scroll.setScrollX(x - min);
			}
		}
		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null)
				button.update(deltaTime);
		}
		// update maps
		for (DynamicButton map : maps) {
			if (map != null)
				map.update(deltaTime);
		}
		// update clouds
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.update(deltaTime);
		}
	}

	@Override
	public void draw() {
		renderBackground();
		renderObjects();
		renderStage();
	}

	private void renderBackground() {
		bgDrawable(true);
		batch.draw(Assets.txBg0, 0, 0, 800, 480);
		bgDrawable(false);
	}

	private void renderStage() {
		stage.draw();
	}

	private void renderObjects() {
		objDrawable(true);
		// render clouds
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.render(batch);
		}
		// render arrow
		batch.draw(Assets.taObjects.findRegion("arrow-left"), 20, (480 - 44) / 2);
		batch.draw(Assets.taObjects.findRegion("arrow-right"), (800 - 51), (480 - 44) / 2);
		objDrawable(false);
	}

	@Override
	public void render(float deltaTime) {
		clearScreen(deltaTime);
		update(deltaTime);
		draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muStage, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music
		SoundManager.pauseMusic(Assets.muStage);
	}

	@Override
	public void pause() {
		// pause music
		SoundManager.pauseMusic(Assets.muStage);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muStage, 0.5f, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
