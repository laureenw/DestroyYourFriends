package com.mygdx.game.systemes;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.composants.Vitesse;
import com.mygdx.game.composants.Position;

public class Deplacer extends EntityProcessingSystem {
    
    @Mapper ComponentMapper<Position> mapperPos;
    @Mapper ComponentMapper<Vitesse> mapperVit;
 
    public Deplacer() {
        super(Aspect.getAspectForAll(Position.class, Vitesse.class));
    }
 
    @Override
    protected void process(Entity e) {
        float delta = Gdx.graphics.getDeltaTime();
     
        Position p = mapperPos.get(e);
        Vitesse v = mapperVit.get(e);
     
        p.posX += v.incX * delta;
        p.posY += v.incY * delta;
    }
     
}
