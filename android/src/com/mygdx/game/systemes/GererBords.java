package com.mygdx.game.systemes;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.artemis.annotations.Mapper;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Position;
import com.mygdx.game.composants.Vitesse;

public class GererBords extends VoidEntitySystem {
    
    private @Mapper ComponentMapper<Position> mapperPos;
    private @Mapper ComponentMapper<Dimension> mapperDim;
    private @Mapper ComponentMapper<Vitesse> mapperVit;
 
    @Override
    protected void processSystem() {
        //Récupération de nos balles
        ImmutableBag balles = world.getManager(GroupManager.class).getEntities("BALLES");
         
        int nbBalles = balles.size();
        for(int i=0; i < nbBalles; i++){
             
            //récupération des composants de chacunes de nos balles.
            Entity balle = (Entity) balles.get(i);
            Position pBalle = mapperPos.get(balle); 
            Vitesse vBalle = mapperVit.get(balle);
            Dimension dBalle = mapperDim.get(balle);
             
            float xMinBalle = pBalle.posX;
            float xMaxBalle = pBalle.posX + dBalle.largeur;
            float yMinBalle = pBalle.posY;
             
            //Si la balle touche à gauche ou à droite...
            if(xMinBalle < 0 || xMaxBalle > Gdx.graphics.getWidth()){
                //... on change la vitesse horizontale
                vBalle.incX = - vBalle.incX;
            }
             
            // Si la balle a touché en bas...
            if(yMinBalle < 0){
                //... on change la vitesse verticale
                vBalle.incY = - vBalle.incY;
            }
             
            // Si la balle sort par en haut...
            else if(yMinBalle > Gdx.graphics.getHeight()){
                //...elle est supprimée !
                balle.deleteFromWorld();
            }
        }
    }
     
}