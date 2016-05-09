package com.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import data.Statics;

public class MainView extends ApplicationAdapter {
	SpriteBatch spriteBatch;
	Texture img;
	Skin skin;
	Pixmap bg;
	Texture text;

	public MainView(boolean android){
		Statics.isAndroid = android;
	}

	public MainView(){
		Statics.isAndroid = false;
	}

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
		//create colored texture
		bg = new Pixmap(1820, 980, Pixmap.Format.RGBA8888);
		bg.setColor(Color.BLUE);
		bg.fill();
		text = new Texture(bg);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//Test
		testRender(spriteBatch, skin, Color.RED, "HEJSAN", 100f, 100f);
        spriteBatch.begin();
		spriteBatch.draw(img, 0, 0);
		spriteBatch.draw(text, 100 ,100);
		spriteBatch.end();
	}

	public void testRender(SpriteBatch sb, Skin skin, Color c, String s, float h, float w){
		Array<Disposable> trash = new Array<Disposable>(2);

		int width = (int) (Statics.ACTIVITY_TEXTURE_HEIGHT_MODIFIER * w);
		int height = (int) (Statics.ACTIVITY_TEXTURE_WIDTH_MODIFIER * h);
		//Create label and stage
		Stage stage = new Stage();
		trash.add(stage);
		Label label = new Label(s,skin);
		Table table = new Table();
		stage.addActor(table);
		table.add(label);

		Texture bgText = new Texture( bg);
		trash.add(bgText);
		TextureRegion fboRegion = new TextureRegion(bgText);
		fboRegion.flip(false, true);
		//Create framebuffer
		//System.out.println("H: " + height + " W:" + width);
		FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888
				,1820 ,980 , false);
		trash.add(fb);
		//Capture to framebuffer
		//fb.begin();
		//Render
		sb.begin();
		//draw color background
		sb.draw(bgText, 0, 0);
		sb.draw(fboRegion, 0, 0);

		//draw text.
		stage.draw();
		sb.end();
		//fb.end();
		//Texture texture = fb.getColorBufferTexture();
		//texture = new Texture(bg);
		//Dispose of trash
		for(Disposable d: trash){
			d.dispose();
		}


	}
}
