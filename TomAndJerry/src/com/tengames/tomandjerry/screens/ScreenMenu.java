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
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.tengames.tomandjerry.interfaces.GlobalVariables;
import com.tengames.tomandjerry.json.JSONObject;
import com.tengames.tomandjerry.main.Assets;
import com.tengames.tomandjerry.main.TomAndJerry;
import com.tengames.tomandjerry.objects.Cloud;
import com.tengames.tomandjerry.objects.DynamicButton;
import com.tengames.tomandjerry.objects.DynamicDialogHScore;

@SuppressWarnings("deprecation")
public class ScreenMenu extends XScreen implements Screen {
	private TomAndJerry coreGame;
	private Stage stage;
	private DynamicButton[] buttons;
	private Cloud[] clouds;
	private float countPlayMusic;
	private DynamicDialogHScore dialog;

	public ScreenMenu(TomAndJerry coreGame) {
		super(800, 480);
		this.coreGame = coreGame;
		initialize();
	}

	@Override
	public void initialize() {
		// play sound butter
		SoundManager.playSound(Assets.soButter);
		// init params
		countPlayMusic = 0;

		// create stage
		stage = new Stage(800, 480, true) {
			private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back
															// presses.
			private long mBackPressed;

			@Override
			public boolean keyUp(int keyCode) {
				if (keyCode == Keys.BACK) {
					// play sound
					SoundManager.playSound(Assets.soBtClick);

					if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
						// exit game
						System.exit(0);
					} else {
						coreGame.androidListener.showToast("Tap back button in order to exit !");
					}

					mBackPressed = System.currentTimeMillis();
				}
				return super.keyUp(keyCode);
			}

		};
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		// set input processor
		Gdx.input.setInputProcessor(stage);
		Gdx.input.setCatchBackKey(true);

		// create buttons
		buttons = new DynamicButton[10];

		// first phomat
		buttons[0] = new DynamicButton(Assets.taObjects.findRegion("butter"), new Vector2(500, 600),
				new Vector2(500, 40), 0);

		// tom & jerry
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("jerry-menu"), new Vector2(-300, 40),
				new Vector2(350, 40), 2, 1.3f);
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("tom-menu"), new Vector2(-300, 40),
				new Vector2(100, 40), 2, 1.5f);

		// logo
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("logo"), new Vector2(200, 600),
				new Vector2(200, 350), 3);

		// button play
		buttons[5] = new DynamicButton(Assets.taObjects.findRegion("bt-play"), new Vector2(400 - 146 / 2, 200), 4);
		buttons[5].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// go to stage screen
				coreGame.setScreen(new ScreenRegion(coreGame));
			}
		});

		// button highscore
		buttons[6] = new DynamicButton(Assets.taObjects.findRegion("bt-more"), new Vector2(300 - 146 / 2, 200 - 90),
				4.2f);
		buttons[6].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);

				// show dialog
				final Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
				imgDark.setSize(800, 480);
				imgDark.setPosition(0, 0);

				WindowStyle windowStyle = new WindowStyle();
				windowStyle.titleFont = Assets.fNumber;
				dialog = new DynamicDialogHScore(windowStyle, Assets.taObjects.findRegion("dialog"),
						new Vector2(374, 292), new Vector2(400, 900), new Vector2(400, 240),
						coreGame.androidListener.getHscore());

				dialog.getBtSubmit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soBtClick);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;

						// TODO: highscore
						try {
							sendRequest();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				dialog.getBtExit().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
//						 play sound
						SoundManager.playSound(Assets.soBtClick);

						// remove
						imgDark.remove();
						dialog.remove();
						dialog = null;
					}
				});

				// add to stage
				stage.addActor(imgDark);
				stage.addActor(dialog);
			}
		});

		// button store
		buttons[7] = new DynamicButton(Assets.taObjects.findRegion("bt-about"), new Vector2(500 - 146 / 2, 200 - 90),
				4.3f);
		buttons[7].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// TODO: go to store
				coreGame.androidListener.gotoStore(GlobalVariables.STORE_URL);
			}
		});

		// button sound
		buttons[8] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(740, 420), 0.6f, 4.3f);
		if (!SoundManager.SOUND_ENABLE)
			buttons[8].getButton().setChecked(true);
		buttons[8].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muMenu);
				} else {
					SoundManager.playMusic(Assets.muMenu, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
			}
		});

		// add to stage
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}

		// create clouds
		clouds = new Cloud[5];
		for (int i = 0; i < clouds.length; i++) {
			clouds[i] = new Cloud(Cloud.TYPE_MENU, -200 + MathUtils.random(100), 300 + MathUtils.random(180),
					new Vector2(150 / 2, 82 / 2));
		}
	}

	@SuppressWarnings({ "resource", "unused" })
	private void sendRequest() throws Exception {
		coreGame.androidListener.showLoading(true);

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(getHighScoreReq(coreGame.androidListener.getDeviceId(), "6277112956715008",
				"Tom%20And%20Jerry%20TNT", coreGame.androidListener.getHscore(), ""));

		// add request header
		request.addHeader("User-Agent", "Android");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		coreGame.androidListener.showLoading(false);

		// show result and code
		if (response == null) {
			coreGame.androidListener.showToast("Network Error !");
		}

		if (response.getStatusLine().getStatusCode() == 600) { // error code
			coreGame.androidListener.showDialog(result.toString());
		} else if (response.getStatusLine().getStatusCode() == 200) { // valid code
			JSONObject json = new JSONObject(result.toString());

			if (!json.getString(GlobalVariables.RES_INFORM).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_BONUS).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_MESSAGE).equals("")) {

			}

			if (!json.getString(GlobalVariables.RES_LINK).equals("")) {
				coreGame.androidListener.gotoHighscore(json.getString(GlobalVariables.RES_LINK));
			}

		}

	}

	private String getHighScoreReq(String deviceId, String gameId, String gameName, int score, String params) {
		return (GlobalVariables.HSCORE_URL + "?" + GlobalVariables.REQ_DEVICEID + "=" + deviceId + "&"
				+ GlobalVariables.REQ_GAMEID + "=" + gameId + "&" + GlobalVariables.REQ_GAMENAME + "=" + gameName + "&"
				+ GlobalVariables.REQ_DEVICETYPE + "=" + GlobalVariables.DEV_ADR + "&" + GlobalVariables.REQ_SCORE + "="
				+ score + "&" + GlobalVariables.REQ_PARAMS + "=" + params);
	}

	@Override
	public void update(float deltaTime) {
		// for play music
		if (countPlayMusic >= 0)
			countPlayMusic += deltaTime;
		if (countPlayMusic >= 2f) {
			// stop sound
			SoundManager.stopSound(Assets.soButter);
			// play music
			SoundManager.playMusic(Assets.muMenu, 0.5f, true);
			// turn off
			countPlayMusic = -1;
		}
		// update clouds
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.update(deltaTime);
		}

		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null)
				button.update(deltaTime);
		}

		// update dialog
		if (dialog != null)
			dialog.update(deltaTime);
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
	}

	@Override
	public void hide() {
		// stop music, sound
		SoundManager.stopSound(Assets.soButter);
		SoundManager.pauseMusic(Assets.muMenu);
	}

	@Override
	public void pause() {
		// stop music, sound
		SoundManager.stopSound(Assets.soButter);
		SoundManager.pauseMusic(Assets.muMenu);
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muMenu, 0.5f, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
