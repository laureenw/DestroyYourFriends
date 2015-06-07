package com.mygdx.game;

import java.util.List;

import android.util.DisplayMetrics;
import android.view.Display;

import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.facebook.model.GraphUser;
import com.mygdx.game.systemes.CasserBriques;
import com.mygdx.game.systemes.CommanderRaquette;
import com.mygdx.game.systemes.Deplacer;
import com.mygdx.game.systemes.DessinerFormes;
import com.mygdx.game.systemes.GererBords;
import com.mygdx.game.systemes.GererCollision;
import com.mygdx.game.systemes.GererDefaite;
import com.mygdx.game.android.BaseListElement;
import com.mygdx.game.android.R;
import com.mygdx.game.android.SelectionFragment;
import com.mygdx.game.android.WallBreackerApplication;

public class WallBreacker extends Game implements ApplicationListener {
	 
    private World world;
    OrthographicCamera camera;
    public static final String TITLE="Wall Breacker";
    public static int WIDTH=1280, HEIGHT=724;
    private SpriteBatch batch;
    private Texture texture;
    private Texture texture2;
    private BitmapFont font;
    WallBreackerApplication sf = new WallBreackerApplication();
   
	@Override
    public void create() {
    	
    	//texture2 = new Texture(Gdx.files.internal("fond.png"));
    	
        //initialisation de la camera libGDX
		texture = new Texture(Gdx.files.internal("fonts/font.png"));
    	font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
    	font.setColor(Color.RED);
    	texture2 = new Texture(Gdx.files.internal("fond.png"));
    	
    	batch = new SpriteBatch();
        //initialisation de la camera libGDX
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        sf = new WallBreackerApplication();
      
        // on initialise notre world artemis
        world = new World();
        
        //on ajoute les managers dont on va se servir par la suite
        world.setManager(new GroupManager());
        world.setManager(new TagManager());
        
        world.initialize();
         
        //on assigne nos systèmes à notre world
        world.setSystem(new DessinerFormes());
         
        //creation de notre balle et ajout dans le world
        FabriqueEntite.creerBalle(world, 150, 150, 50f, 50f).addToWorld();
 
        //creation de notre raquette
        FabriqueEntite.creerRaquette(world).addToWorld();

        //creation de nos briques
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                FabriqueEntite.creerBrique(world, i * 160, j *30).addToWorld();
            }
        }
       
        world.setSystem(new Deplacer());
        
        world.setSystem(new CommanderRaquette());
        world.setSystem(new GererCollision());
        world.setSystem(new GererBords());
        world.setSystem(new GererDefaite());
        world.setSystem(new CasserBriques());
        
        world.initialize();
 
       
    }
 
    @Override
    public void render() {
    	Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        	batch.draw(texture2, 0, 0, texture2.getWidth(), texture2.getHeight());
        	if (sf.getSelectedUsers() != null) {
    	       	font.draw(batch, sf.getSelectedUsers().get(0).getName(), 20, 20);
        	}
    	batch.end();
        
        //mise à jour cam
        camera.update();
         
        //mise à jour du monde. Exécution des processing systèmes
        world.process();
    }
    
    @Override
    public void dispose() {
    	batch.dispose();
    	texture.dispose();
    	font.dispose();
    }
    
 
    @Override
    public void resize(int width, int height) {
    	camera.setToOrtho(false, WIDTH, HEIGHT);
    }
 
    @Override
    public void pause() {

    }
 
    @Override
    public void resume() {
 
    }

}