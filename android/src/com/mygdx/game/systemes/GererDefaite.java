package com.mygdx.game.systemes;

import java.util.Random;

import android.R;
import android.widget.EditText;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.FabriqueEntite;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Position;
import com.mygdx.game.composants.Vitesse;
import com.badlogic.gdx.graphics.Color;

public class GererDefaite extends VoidEntitySystem {
    
    @Mapper private ComponentMapper<Position> mapperPos;
    @Mapper private ComponentMapper<Vitesse> mapperVit;
    @Mapper private ComponentMapper<Dimension> mapperDim;
 
    @Override
    protected void processSystem() {
        //on récupère toutes les balles de notre jeu
        ImmutableBag balles = world.getManager(GroupManager.class).getEntities("BALLES");
         
        //On en déduit le nombre de balles.
        int nbBalles = balles.size();
         
        //S'il ne reste plus de balle...
        if(nbBalles == 0){
        	//on récupère la position de notre raquette...
        	Entity raquette = world.getManager(TagManager.class).getEntity("RAQUETTE");
        	Position pRaquette = mapperPos.get(raquette);
        	Dimension dRaquette = mapperDim.get(raquette);
        	//... pour pouvoir placer une nouvelle balle dessus
        	FabriqueEntite.creerBalle(world, (int) (pRaquette.posX + (dRaquette.largeur / 2) - 5), 
        	(int) (pRaquette.posY - 10), 0, 0).addToWorld();
        }
        //S'il ne reste qu'une seule balle...
        else if(nbBalles == 1) {
        	//On récupère notre balle restante
        	Entity balleRestante = (Entity) balles.get(0);
        	//on regarde sa vitesse
        	Vitesse vBalle = mapperVit.get(balleRestante);
        	 
        	//si elle n'a aucune vitesse verticale, c'est qu'elle est encore sur 
        	//la raquette
        	if(vBalle.incY == 0){
        	    Entity raquette = world.getManager(TagManager.class).getEntity("RAQUETTE");
        	    Vitesse vRaquette = mapperVit.get(raquette);
        	 
        	    //si la raquette se déplace, la balle se déplace avec elle
        	    vBalle.incX = vRaquette.incX;
        	   
        	    //si la touche "espace" est enfoncée...
        	    if(Gdx.input.isTouched()){
        	        //on donne une valeur aléatoire à la vitesse horizontale
        	        //entre -100 et 100
        	        vBalle.incX = new Random().nextInt(200) - 100;
        	 
        	        //on donne une valeur fixe à la vitesse verticale
        	        vBalle.incY = 50;
        	    }
        	}
        }
    }
}
