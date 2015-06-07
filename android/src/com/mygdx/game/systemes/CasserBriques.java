package com.mygdx.game.systemes;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.mygdx.game.composants.Etat;

public class CasserBriques extends EntityProcessingSystem {
    
    @Mapper private ComponentMapper<Etat> mapperEtat;
 
    public CasserBriques() {
        super(Aspect.getAspectForAll(Etat.class));
    }
 
    @Override
    protected void process(Entity e) {
        Etat etatEntite = mapperEtat.get(e);
        if(etatEntite.casse){
            e.deleteFromWorld();
        }
    }
     
}
