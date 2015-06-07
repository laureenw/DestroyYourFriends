package com.mygdx.game.systemes;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.artemis.systems.VoidEntitySystem;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Etat;
import com.mygdx.game.composants.Position;
import com.mygdx.game.composants.Vitesse;

public class GererCollision extends VoidEntitySystem {
	 
    @Mapper
    private ComponentMapper<Position> mapperPos;
    @Mapper
    private ComponentMapper<Dimension> mapperDim;
    @Mapper
    private ComponentMapper<Etat> mapperEtat;
    @Mapper
    private ComponentMapper<Vitesse> mapperVit;
 
    private static float coefAngle = 5f;
    private static float maxIncX = 300;
 
    @Override
    protected void processSystem() {
        float delta = Gdx.graphics.getDeltaTime();
 
        //on récupère l'instance de notre GroupManager
        GroupManager gManager = world.getManager(GroupManager.class);
 
        //on récupère notre groupe de balles
        ImmutableBag<Entity> balles = gManager.getEntities("BALLES");
 
        //on récupère notre groupe de briques
        ImmutableBag<Entity> briques = gManager.getEntities("BRIQUES");
 
        //on récupère notre raquette
        Entity raquette = world.getManager(TagManager.class).getEntity("RAQUETTE");
 
        //on récupère tous les composants utiles de notre raquette
        Position pRaquette = mapperPos.get(raquette);
        Dimension dRaquette = mapperDim.get(raquette);
 
        //on n'a pas besoin de récupérer le composant "forme" puisqu'on sait 
        //déjà sur quelle entité on travaille. La raquette est un Rectangle
        Rectangle rectRaquette
                = new Rectangle(pRaquette.posX, pRaquette.posY, dRaquette.largeur, dRaquette.hauteur);
        float centreRaquetteX = pRaquette.posX + (dRaquette.largeur / 2);
        float centreRaquetteY = pRaquette.posY + (dRaquette.hauteur / 2);
 
        //Calcul du ratio Hauteur Largeur de la raquette
        float ratioHLRaquette = Math.abs(dRaquette.hauteur / dRaquette.largeur);
 
        for (int i = 0; i < balles.size(); i++) {
            //On récupère notre balle
            Entity balle = balles.get(i);
            //On récupère les composants utiles de notre balle
            Position pBalle = mapperPos.get(balle);
            Dimension dBalle = mapperDim.get(balle);
            float rayonBalle = dBalle.hauteur / 2;
 
            //on n'a pas besoin de récupérer le composant "forme" puisqu'on sait 
            //déjà sur quelle entité on travaille. La balle est un cercle.
            Circle cercleBalle = new Circle(pBalle.posX + rayonBalle, pBalle.posY + rayonBalle, rayonBalle);
 
            //On regarde si la balle est rentrée en collision avec la raquette
            if (Intersector.overlaps(cercleBalle, rectRaquette)) {
                //La vitesse actuelle de la balle nous sera utile pour annuler son dernier mouvement
                Vitesse vBalle = mapperVit.get(balle);
                Vitesse vRaquette = mapperVit.get(raquette);
 
                //obtention des coordonnées du centre de la balle
                float centreBalleX = pBalle.posX + rayonBalle;
                float centreBalleY = pBalle.posY + rayonBalle;
 
                //calcul de la pente
                float pente = Math.abs((centreBalleY - centreRaquetteY)
                        / (centreBalleX - centreRaquetteX));
 
                //Si on est dans le cas A et que la balle se situe "en dessous"
                //de la raquette...
                if (pente >= ratioHLRaquette
                        && centreBalleY < centreRaquetteY) {
 
                    //...on annule le dernier mouvement de la balle...
                    pBalle.posX -= (vBalle.incX * delta);
                    pBalle.posY -= (vBalle.incY * delta);
 
                    //... on "inverse" la vitesse verticale de la balle...
                    vBalle.incY = -vBalle.incY;
 
                    //... et on recalcule sa vitesse horizontale en fonction de
                    //du décalage et d'un coefficient.
                    vBalle.incX -= (centreRaquetteX - centreBalleX) * coefAngle;
 
                    //on plafonne la vitesse horizontale à une valeur 
                    //max raisonnable.
                    vBalle.incX = (vBalle.incX < 0)
                            ? Math.max(vBalle.incX, -maxIncX)
                            : Math.min(vBalle.incX, maxIncX);
                } //si on est dans le cas B...
                else {
                    //...on annule le déplacement horizontal uniquement...
                    pBalle.posX -= vBalle.incX * delta;
                    //...on s'assure que la balle "ressorte" de la raquette...
                    pBalle.posX += vRaquette.incX * delta;
                    //... on transmet la vitesse de la raquette à la balle
                    vBalle.incX = vRaquette.incX;
                }
            } // si ce n'est pas le cas, on teste la collision avec une des briques
            else {
                for (int j = 0; j < briques.size(); j++) {
                    Entity brique = briques.get(j);
                    Position pBrique = mapperPos.get(brique);
                    Dimension dBrique = mapperDim.get(brique);
                    //on n'a pas besoin de récupérer le composant "forme" puisqu'on sait 
                    //déjà sur quelle entité on travaille. La brique est un rectangle.
                    Rectangle rectBrique = new Rectangle(pBrique.posX, pBrique.posY,
                            dBrique.largeur, dBrique.hauteur);
                    if (Intersector.overlaps(cercleBalle, rectBrique)) {
                        //même raisonnement que précédemment (avec la raquette)
                        float centreBriqueX = pBrique.posX + (dBrique.largeur / 2);
                        float centreBriqueY = pBrique.posY + (dBrique.hauteur / 2);
                        float ratioHLBrique = Math.abs(dBrique.hauteur / dBrique.largeur);
                        float centreBalleX = pBalle.posX + rayonBalle;
                        float centreBalleY = pBalle.posY + rayonBalle;
                        float pente
                                = Math.abs((centreBalleY - centreBriqueY)
                                        / (centreBalleX - centreBriqueX));
                        Vitesse vBalle = mapperVit.get(balle);
                        pBalle.posX -= (vBalle.incX * delta);
                        pBalle.posY -= (vBalle.incY * delta);
 
                        //on change l'état de notre brique en "cassé"
                        Etat etatBrique = mapperEtat.get(brique);
                        etatBrique.casse = true;
 
                        //le calcul de collision est plus simple puisque les 
                        //briques ne bougent pas et qu'on a pas besoin de tenir
                        //compte du décalage
                        if (pente >= ratioHLBrique) {
                            vBalle.incY = -vBalle.incY;
                        } else {
                            vBalle.incX = -vBalle.incX;
                        }
                    }
                }
            }
        }
    }
}
