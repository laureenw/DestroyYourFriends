package com.mygdx.game.systemes;

import sun.rmi.runtime.Log;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Position;
import com.mygdx.game.composants.Vitesse;

public class CommanderRaquette extends VoidEntitySystem {
		 
    @Mapper ComponentMapper<Vitesse> mapperVitesse;
    private @Mapper ComponentMapper<Position> mapperPos;
    private @Mapper ComponentMapper<Dimension> mapperDim;
      
    @Override
    protected void processSystem() {
        Entity raquette = world.getManager(TagManager.class).getEntity("RAQUETTE");
        Vitesse vRaquette = mapperVitesse.get(raquette);
        Position pRaquette = mapperPos.get(raquette);
        Dimension dRaquette = mapperDim.get(raquette);
        float incX = 0;
        float incY = 0;
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();        
         
        float xMinRaquette = pRaquette.posX;
        float xMaxRaquette = pRaquette.posX + dRaquette.largeur;
         
        //Si la balle touche à gauche
        if(xMinRaquette < 0){
            incX += 200;
        }
        
        //à droite
        if(xMaxRaquette > Gdx.graphics.getWidth()){
        	incX -= 200;
        }
        
        if(Gdx.input.isTouched() && mouseX < width * 0.25f){
            incX -= 200;
        }
        if(Gdx.input.isTouched() && mouseY < height * 0.25f){
            incX += 200;
        }
        vRaquette.incX = incX;
    }
    
    
}