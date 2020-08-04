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

import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.tengames.tomandjerry.interfaces.GlobalVariables;
import com.tengames.tomandjerry.main.Assets;
import com.tengames.tomandjerry.main.TomAndJerry;
import com.tengames.tomandjerry.objects.Boom;
import com.tengames.tomandjerry.objects.Cloud;
import com.tengames.tomandjerry.objects.CommonModelList;
import com.tengames.tomandjerry.objects.CommonTool;
import com.tengames.tomandjerry.objects.DynamicButton;
import com.tengames.tomandjerry.objects.DynamicDialog;
import com.tengames.tomandjerry.objects.IconModel;

public class ScreenGame extends XScreen implements Screen, InputProcessor {
	private static final byte STATE_NULL = 0;
	private static final byte STATE_WIN = 1;
	private static final byte STATE_LOOSE = 2;

	private Texture arrMaps[] = { Assets.txBg0, Assets.txBg1, Assets.txBg2, Assets.txBg3 };
	private Music arrMusic[] = { Assets.muGame1, Assets.muGame2 };
	private Music tempMusic;
	private Texture tempMap;
	private int mapBooms[] = { 1, 3, 2, 5, 4, 4, 1, 3, 3, 4, 2, 3, 6, 4, 6, 4, 4, 4, 5, 5, 5, 4, 4, 4, 4, 1, 2, 2, 5,
			5 };

	private TomAndJerry coreGame;
	private WindowStyle windowStyle;
	private World world;
	private ObjectModel ground;
	private Stage stage, stagePop;
	private DynamicDialog dgFinish;
	private DynamicButton[] buttons;
	private BufferedReader reader;
	private ArrayList<CommonTool> commonTools;
	private ArrayList<Boom> booms;
	private Cloud[] clouds;
	private String strJson;
	private float delayTime, sleepTime;
	private int number, count, numberBoom, numberTom, numberJerry, countTom, countJerry;
	private int countSleep;
	private byte state;
	private boolean isFinish, canCreate;
//	private ShapeRenderer shapeRender;
//	private Skin skin;

	public ScreenGame(TomAndJerry coreGame, String strJson, int number) {
		super(800, 480);
		this.coreGame = coreGame;
		this.strJson = strJson;
		this.number = number;
//		startDebugBox();
		initialize();
	}

	@Override
	public void initialize() {
		// create world
		world = new World(GlobalVariables.GRAVITY, true);

		// create stage
		stage = new Stage(800, 480, true);
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		InputMultiplexer input = new InputMultiplexer(stage, this);
		// set input
		Gdx.input.setInputProcessor(input);

		// create stage popup
		stagePop = new Stage(800, 480, true);
		((OrthographicCamera) stagePop.getCamera()).setToOrtho(false, 800, 480);

		// create smt
		initializeParams();
		createUI();
		createArrays();
		createModels();
		// check contact listener
		checkCollision();
	}

	private void createArrays() {
		commonTools = new ArrayList<CommonTool>();
		booms = new ArrayList<Boom>();
	}

	private void createBoom(float x, float y) {
		Boom boom = new Boom(camera, world, ground, Assets.taObjects.findRegion("boom"), x - 20, y - 20, count);
		booms.add(boom);
		count++;
		// set text for display
		buttons[4].setText("x" + numberBoom);
	}

	private void initializeParams() {

		/* loading data */
//		try {
//			reader = new BufferedReader(new FileReader("/home/woodyx/workspace-gdx/TomAndJerryAndroid/assets/data/tomandjerry.txt"));
//		} catch (FileNotFoundException e) {}
		reader = coreGame.androidListener.getData();

		// get music
		tempMusic = arrMusic[MathUtils.random(1)];

		// play music
		SoundManager.playMusic(tempMusic, 0.5f, true);

		// initialize params
		windowStyle = new WindowStyle();
		windowStyle.titleFont = Assets.fNumber;

		state = STATE_NULL;
		isFinish = false;
		canCreate = true;
		sleepTime = 0;
		delayTime = 0;
		count = 0;
		countSleep = 0;
		numberTom = 0;
		numberJerry = 0;
		numberBoom = mapBooms[number - 1];

		// trace
		if (number < 10) {
			coreGame.androidListener.traceScene("0" + number);
		} else {
			coreGame.androidListener.traceScene(number + "");
		}

		// show admob
		if (number % 5 == 0)
			coreGame.androidListener.showIntertitial();
	}

	private void createModels() {
		// create debug
//		shapeRender = new ShapeRenderer();

		// create ground
		ground = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(1000, 10), new Vector2(),
				0, new Vector2(-100, -100), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "ground");
		// create wall-left
		ObjectModel wallLeft = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(10, 900),
				new Vector2(), 0, new Vector2(-110, 0), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "wallleft");
		wallLeft.getBody().getFixtureList().get(0).setSensor(true);
		// create wall-right
		ObjectModel wallRight = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(10, 900),
				new Vector2(), 0, new Vector2(900, 0), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "wallright");
		wallRight.getBody().getFixtureList().get(0).setSensor(true);
		// create wall top
		ObjectModel wallTop = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(1000, 10),
				new Vector2(), 0, new Vector2(-110, 910), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY,
				GlobalVariables.MASK_SCENERY, "walltop");
		wallTop.getBody().getFixtureList().get(0).setSensor(true);

		// create models
		if (strJson != null)
			createJsonModels();

		// count number tom, jerry
		for (CommonTool com : commonTools) {
			switch (com.getType()) {
			case IconModel.TOM:
				numberTom++;
				break;

			case IconModel.JERRY:
				numberJerry++;
				break;

			default:
				break;
			}

		}

		countJerry = numberJerry;
		countTom = numberTom;

		// create clouds
		clouds = new Cloud[5];
		for (int i = 0; i < clouds.length; i++) {
			clouds[i] = new Cloud(Cloud.TYPE_GAME, -200 + MathUtils.random(100), 300 + MathUtils.random(180),
					new Vector2(150 / 2, 82 / 2));
		}
	}

	private void createUI() {
		// load skin
//		skin = new Skin(Gdx.files.internal("drawable/objects/uiskin.json"), Assets.taSkin);

		// creat buttons
		buttons = new DynamicButton[7];

		// label stage
		buttons[0] = new DynamicButton(("Level: " + number), new Vector2(10, 440), 0);

		// button menu
		buttons[1] = new DynamicButton(Assets.taObjects.findRegion("bt-menu"), new Vector2(200, 420), 0.6f, 0.1f);
		buttons[1].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// back to screen stage
				coreGame.setScreen(new ScreenRegion(coreGame));
			}
		});

		// button replay
		buttons[2] = new DynamicButton(Assets.taObjects.findRegion("bt-replay"), new Vector2(260, 420), 1, 0.2f);
		buttons[2].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// replay
				coreGame.setScreen(new ScreenGame(coreGame, strJson, number));
			}
		});

		// icon boom
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("boom"), new Vector2(640, 430), 1, 0.3f);

		// label number boom
		buttons[4] = new DynamicButton(("x" + numberBoom), new Vector2(680, 430), 0.4f);

		// button sound
		buttons[5] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(740, 420), 0.6f, 0.5f);
		if (!SoundManager.SOUND_ENABLE)
			buttons[5].getButton().setChecked(true);
		buttons[5].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soBtClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(tempMusic);
				} else {
					SoundManager.playMusic(tempMusic, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
			}
		});

		// button kick
		buttons[6] = new DynamicButton(Assets.taObjects.findRegion("bt-kick"), new Vector2(), 1f, 0.6f);
		buttons[6].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// restart sleep count
				sleepTime = 0;
				// explosed
				if (!booms.isEmpty()) {
					for (Boom boom : booms) {
						boom.kick();
					}
				}
			}
		});

		// back to screen scenario
		/*
		 * TextButton btBack = new TextButton("Back", skin); btBack.setPosition(20,
		 * 400); btBack.addListener(new ChangeListener() {
		 * 
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * coreGame.setScreen(new ScreenScenario(coreGame)); } });
		 */

		// add to stage
//		 stage.addActor(btBack);
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}
	}

	/* generate map */
	private void createJsonModels() {
		Json json = new Json();
		CommonModelList jsList = new CommonModelList();
		jsList = json.fromJson(CommonModelList.class, strJson);
		// generate map
		for (int i = 0; i < jsList.getSize(); i++) {
			switch (jsList.getModel(i).getType()) {
			/* create background */
			case IconModel.MAP:
				tempMap = arrMaps[(byte) jsList.getModel(i).getPosition().x];
				break;
			/* create tools */
			case IconModel.TOM:
			case IconModel.JERRY:
			case IconModel.ICE:
			case IconModel.STEEL:
			case IconModel.WOOD_1:
			case IconModel.WOOD_2:
			case IconModel.WOOD_3:
			case IconModel.WOOD_4:
				CommonTool tool = new CommonTool(world, ground, jsList.getModel(i).getType(), 2, 0.2f, 0.5f,
						jsList.getModel(i).getRotation(), jsList.getModel(i).getPosition(),
						jsList.getModel(i).getSize(), ("common-tool" + count));
				commonTools.add(tool);
				count++;
				break;

			default:
				break;
			}
		}
		// free models
		jsList.dispose();
		jsList = null;
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

	private void checkFinish(float deltaTime) {
		// normal
		if (numberJerry > 0) {
			if (countJerry < numberJerry) {
				if (state == STATE_NULL) {
					isFinish = true;
					state = STATE_LOOSE;
				}
			}
		}
		if (countTom == 0) {
			int totalBodies = commonTools.size();
			for (CommonTool com : commonTools) {
				if (com != null && com.getModel().getBody() != null) {
					if (!com.getModel().getBody().isAwake())
						countSleep++;
				}
			}
			if (countSleep == totalBodies) {
				if (state == STATE_NULL) {
					isFinish = true;
					state = STATE_WIN;
				}
			} else {
				countSleep = 0;
			}
		}
		// check if latest boom is explosed
		if (numberBoom == 0) {
			if (booms.get(mapBooms[number - 1] - 1).getExplosed()) {
				delayTime += deltaTime;
				if (delayTime >= 2) {
					/* do smt if all bodies is sleeping */
					int totalBodies = commonTools.size();
					for (CommonTool com : commonTools) {
						if (com != null && com.getModel().getBody() != null) {
							if (!com.getModel().getBody().isAwake())
								countSleep++;
						}
					}
					if (countSleep == totalBodies) {
						if (numberJerry > 0) {
							if (countJerry < numberJerry) {
								if (state == STATE_NULL) {
									state = STATE_LOOSE;
									isFinish = true;
								}
							}
						}
						if (countTom == 0) {
							if (state == STATE_NULL) {
								isFinish = true;
								state = STATE_WIN;
							}
						} else {
							if (state == STATE_NULL) {
								state = STATE_LOOSE;
								isFinish = true;
							}
						}
					} else {
						countSleep = 0;
					}
				}
			}
		}

		// show dialog
		if (isFinish) {
			switch (state) {
			case STATE_WIN:
				// show dialog
				dgFinish = new DynamicDialog(windowStyle, Assets.taObjects.findRegion("dialog"), new Vector2(374, 292),
						new Vector2(400, 700), new Vector2(400, 240), (mapBooms[number - 1] - numberBoom),
						mapBooms[number - 1], DynamicDialog.TYPE_WIN);
				// pause music
				SoundManager.pauseMusic(tempMusic);
				// play sound
				SoundManager.playSound(Assets.soWin);

				int boomUsed = mapBooms[number - 1] - numberBoom;
				int totalBoom = mapBooms[number - 1];
				if ((boomUsed / totalBoom) < 0.5f) {
					// save hscore
					coreGame.androidListener.saveHscore(100);
				} else {
					// save hscore
					coreGame.androidListener.saveHscore(50);
				}

				break;

			case STATE_LOOSE:
				// show dialog
				dgFinish = new DynamicDialog(windowStyle, Assets.taObjects.findRegion("dialog"), new Vector2(374, 292),
						new Vector2(400, 700), new Vector2(400, 240), (mapBooms[number - 1] - numberBoom),
						mapBooms[number - 1], DynamicDialog.TYPE_FAIL);
				// pause music
				SoundManager.pauseMusic(tempMusic);
				// play sound
				SoundManager.playSound(Assets.soFail);
				break;

			default:
				break;
			}
			// add buttons
			dgFinish.getBtMenu().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soBtClick);
					// return menu stage
					coreGame.setScreen(new ScreenRegion(coreGame));
				}
			});
			// replay
			dgFinish.getBtReplay().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soBtClick);
					// replay game
					coreGame.setScreen(new ScreenGame(coreGame, strJson, number));
				}
			});
			// next stage
			if (dgFinish.getBtNext() != null) {
				dgFinish.getBtNext().addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						// play sound
						SoundManager.playSound(Assets.soBtClick);
						if (number == 30) {
							// return stage menu
							coreGame.setScreen(new ScreenRegion(coreGame));
						} else {
							// save data
							coreGame.androidListener.setValue(number - 1, dgFinish.getValue());
							// next level
							coreGame.androidListener.setValue(number, 1);
							export(number);
						}
					}
				});
			}

			stagePop.addActor(dgFinish);
			Gdx.input.setInputProcessor(stagePop);
			// turn off flag
			isFinish = false;
		}
	}

	@Override
	public void update(float deltaTime) {
		updateWorld(deltaTime);
		updateStage(deltaTime);
		checkFinish(deltaTime);
		updateObjects(deltaTime);
		updateBooms(deltaTime);
	}

	private void updateWorld(float deltaTime) {
		world.step(deltaTime, 8, 3);
	}

	private void updateStage(float deltaTime) {
		// update dialog
		if (dgFinish != null)
			dgFinish.update(deltaTime);
		// update buttons
		for (DynamicButton button : buttons) {
			if (button != null)
				button.update(deltaTime);
		}
	}

	private void updateObjects(float deltaTime) {
		// update sleep time
		if (sleepTime >= 0)
			sleepTime += deltaTime;

		// update common-tools
		if (!commonTools.isEmpty()) {
			for (int i = 0; i < commonTools.size(); i++) {
				if (commonTools.get(i) != null) {
					// update
					if (!commonTools.get(i).getDie()) {
						commonTools.get(i).update(deltaTime);
						// set sleep
						if (sleepTime >= 5) {
							commonTools.get(i).getModel().getBody().setAwake(false);
							// check
							if (numberJerry > 0) {
								if (countJerry < numberJerry) {
									if (state == STATE_NULL) {
										isFinish = true;
										state = STATE_LOOSE;
									}
								}
							}
							if (countTom == 0) {
								if (state == STATE_NULL) {
									isFinish = true;
									state = STATE_WIN;
								}
							}
							sleepTime = -1;
						}
					}
					// remove die
					if (commonTools.get(i).getDie()) {
						// count tom & jerry
						switch (commonTools.get(i).getType()) {
						case IconModel.TOM:
							countTom--;
							break;

						case IconModel.JERRY:
							countJerry--;
							break;

						default:
							break;
						}
						world.destroyBody(commonTools.get(i).getModel().getBody());
						commonTools.remove(i);
					}
				}
			}
		}

		// update clouds
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.update(deltaTime);
		}
	}

	private void updateBooms(float deltaTime) {
		if (!booms.isEmpty()) {
			for (int i = 0; i < booms.size(); i++) {
				// update
				if (!booms.get(i).getCanRemove()) {
					booms.get(i).update(deltaTime);
					// check in range
					booms.get(i).checkInRange(commonTools);
				}
				// remove boom
				if (booms.get(i).getCanRemove()) {
					world.destroyJoint(booms.get(i).getJoint().getJoint());
					world.destroyBody(booms.get(i).getModel().getBody());
					booms.remove(i);
					// increase number boom
					numberBoom++;
					// set text for display
					buttons[4].setText("x" + numberBoom);
				}
			}
		}
	}

	@Override
	public void draw() {
		renderBackGround();
		renderObjects();
//		renderBoomDebug();
		renderStage();
		renderDark();
		renderStagePop();
	}

	private void renderBackGround() {
		bgDrawable(true);
		batch.draw(tempMap, -20, -11, 1320, 742);
		bgDrawable(false);
	}

	private void renderObjects() {
		objDrawable(true);
		renderClouds();
		renderCommonTools();
		renderHelp();
		renderBoom();
		objDrawable(false);
	}

	private void renderHelp() {
		if (number == 1) {
			batch.draw(Assets.taObjects.findRegion("note-jerry"), 500, 220);
			batch.draw(Assets.taObjects.findRegion("note-tom"), 10, 220);
			batch.draw(Assets.taObjects.findRegion("note-bomb"), 260, 300);
			batch.draw(Assets.taObjects.findRegion("boom"), 400, 410);
			batch.draw(Assets.taObjects.findRegion("note-tnt"), 100, 10);
		}
	}

	private void renderClouds() {
		for (Cloud cloud : clouds) {
			if (cloud != null)
				cloud.render(batch);
		}
	}

	private void renderCommonTools() {
		if (!commonTools.isEmpty()) {
			for (CommonTool tool : commonTools) {
				if (tool != null)
					tool.draw(batch);
			}
		}
	}

	private void renderBoom() {
		if (!booms.isEmpty()) {
			for (Boom boom : booms) {
				boom.render(batch);
			}
		}
	}

	private void renderDark() {
		if (state == STATE_WIN || state == STATE_LOOSE) {
			objDrawable(true);
			batch.draw(Assets.taObjects.findRegion("dark"), -50, -10, 900, 500);
			objDrawable(false);
		}
	}

	/*
	 * private void renderBoomDebug() { if (!booms.isEmpty()) { for (Boom boom :
	 * booms) { boom.renderDebug(shapeRender); } } }
	 */

	private void renderStage() {
		stage.draw();
	}

	private void renderStagePop() {
		stagePop.draw();
	}

	@Override
	public void render(float deltaTime) {
		clearScreen(deltaTime);
		clearWorld();
		update(deltaTime);
		draw();
//		renderDebug(world);
	}

	private void clearWorld() {
		world.clearForces();
	}

	private void checkCollision() {
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {
				// check if common-tools contact with ground, wall
				if (!commonTools.isEmpty()) {
					for (CommonTool commonTool : commonTools) {
						if (BoxUtility.detectCollision(contact, commonTool.getModel(), "ground")
								|| BoxUtility.detectCollision(contact, commonTool.getModel(), "wallleft")
								|| BoxUtility.detectCollision(contact, commonTool.getModel(), "wallright")
								|| BoxUtility.detectCollision(contact, commonTool.getModel(), "walltop")) {
							commonTool.setDie();
						}
					}
				}

				// check if boom contact with common-tools
				if (!booms.isEmpty()) {
					// get last boom
					int last = booms.size() - 1;
					if (booms.get(last).checkCollision(contact, booms, commonTools)) {
						if (!booms.get(last).getImmotal())
							booms.get(last).setCanRemove(true);
					}
				}
			}
		});
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(tempMusic, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music, sound
		SoundManager.pauseMusic(tempMusic);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void pause() {
		// pause music, sound
		SoundManager.pauseMusic(tempMusic);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(tempMusic, 0.5f, true);
	}

	@Override
	public void dispose() {
		world.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 touchPoint = new Vector3();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));

		if (!booms.isEmpty()) {
			for (Boom boom : booms) {
				if (!boom.getCanRemove()) {
					if (BoxUtility.touchObject(screenX, screenY, world, camera, ground, boom.getModel())) {
						canCreate = false;
						boom.setCanRemove(true);
					}
				}
			}
		}

		if (numberBoom - 1 >= 0 && canCreate) {
			numberBoom--;
			// create new boom
			createBoom(touchPoint.x, touchPoint.y);
		}

		canCreate = true;

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
