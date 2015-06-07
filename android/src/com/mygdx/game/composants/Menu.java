package com.mygdx.game.composants;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.WallBreacker;

public class Menu implements Screen, ApplicationListener {
    
 Skin skin;
    Stage stage;
     
    WallBreacker game;

       
       // constructor to keep a reference to the main Game class
        public Menu(WallBreacker pgame){
                this.game = pgame;
                
                stage=new Stage();
                Gdx.input.setInputProcessor(stage);
                
                skin = new Skin( Gdx.files.internal( "skins/skin.json" ));

    Table table=new Table();
    table.setSize(800,480);

    final TextButton startGame=new TextButton("start game",skin);
    table.add(startGame).width(200).height(50);
    table.row();

    TextButton options=new TextButton("options",skin);
    table.add(options).width(150).padTop(10).padBottom(3);
    table.row();

    TextButton credits=new TextButton("credits",skin);
    table.add(credits).width(150);
    table.row();

    TextButton quit=new TextButton("quit",skin);
    table.add(quit).width(100).padTop(10);
    table.row();
    
    TextField text=new TextField("",skin);
    table.add(text).width(100).padTop(10);
    table.row();
    // [...]
    //String value=text.getText();

    CheckBox box=new CheckBox("done",skin);
    table.add(box).width(100);
    table.row();
    // [...]
    //boolean checked=box.isChecked();

    String[] items={"cool","mega","awesome"};    
    SelectBox selectbox=new SelectBox(skin);
    table.add(selectbox).width(150);
    // [...]
    //String selection=selectbox.getSelection();
    
    stage.addActor(table);
    
    startGame.addListener(new ClickListener(){
          @Override
          public void clicked(InputEvent event, float x, float y) {
          startGame.addAction(Actions.fadeOut(0.7f));
             // game.setScreen(game.anotherScreen);
          }
     });
        }
        
        @Override
        public void render(float delta) {
      // clear the screen
      Gdx.gl.glClearColor(1,1,1, 1);
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
  
      // let the stage act and draw
      stage.act(delta);
      stage.draw();
      //stage.setViewport2(800,480,false);
        }

       @Override
        public void resize(int width, int height) {
        }

       @Override
        public void show() {
             // called when this screen is set as the screen with game.setScreen();
        }

       @Override
        public void hide() {
             // called when current screen changes from this to a different screen
       stage.dispose();
        }

       @Override
        public void pause() {
        }

       @Override
        public void resume() {
        }

       @Override
        public void dispose() {
                // never called automatically
       stage.dispose();
        }

	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}
 }
