package com.mygdx.game.systemes;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Forme;
import com.mygdx.game.composants.Position;

public class DessinerFormes extends EntityProcessingSystem {
    
    @Mapper private ComponentMapper<Position> mapperPos;
    @Mapper private ComponentMapper<Dimension> mapperDim;
    @Mapper private ComponentMapper<Forme> mapperForme;
     
    private ShapeRenderer renderer;
 
    public DessinerFormes() {
        super(Aspect.getAspectForAll(Position.class, Dimension.class, Forme.class));
    }
 
    @Override
    protected void initialize() {
        renderer = new ShapeRenderer();
    }
 
    @Override
    protected void begin() {
        // on d�marre notre renderer de fa�on � ce qu'il dessine des contours
        renderer.begin(ShapeRenderer.ShapeType.Line);
    }
 
    @Override
    protected void end() {
        //� la fin de l'�xecution de notre syst�me, on referme notre renderer
        renderer.end();
    }
 
    @Override
    protected void process(Entity e) {
        // on r�cup�re la position � l'aide du mapper de position
        Position p = mapperPos.get(e);
        // on r�cup�re la dimension � l'aide du mapper de dimensions
        Dimension d = mapperDim.get(e);
        // on r�cup�re la forme � l'aide du mapper de forme
        Forme forme = mapperForme.get(e);
 
        switch(forme.formeId){
            case 1 : 
                float rayon = d.hauteur / 2;
                renderer.circle(p.posX, p.posY, rayon);
                break;
            case 2 : renderer.rect(p.posX, p.posY, d.largeur, d.hauteur);
                break;
        }
    }
     
}
