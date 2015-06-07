package com.mygdx.game;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.entity.StringEntity;

import javafx.scene.text.Text;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.managers.GroupManager;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.facebook.model.GraphUser;
import com.mygdx.game.android.MainActivity;
import com.mygdx.game.android.SelectionFragment;
import com.mygdx.game.android.WallBreackerApplication;
import com.mygdx.game.composants.Dimension;
import com.mygdx.game.composants.Etat;
import com.mygdx.game.composants.Forme;
import com.mygdx.game.composants.Position;
import com.mygdx.game.composants.Vitesse;
import com.sun.javafx.scene.control.SelectedCellsMap;

public class FabriqueEntite extends SelectionFragment {
		
    public static Entity creerBalle(World w, int xDep, int yDep, float incXDep, float incYDep) {
        //nous créons notre balle ici
        Entity balle = w.createEntity();
 
        //nous ajoutons ici les composants de notre balle
        balle.addComponent(new Position(xDep, yDep));
        balle.addComponent(new Vitesse(incXDep, incYDep));
 
        //ici 1 correspond à la forme "Cercle"
        balle.addComponent(new Forme(1));
 
        //notre balle sera un cercle de diamètre 10 pixels
        balle.addComponent(new Dimension(20, 20));
 
        //on enregistre notre balle dans le groupe des balle
        w.getManager(GroupManager.class).add(balle, "BALLES");
        return balle;
    }
 
    public static Entity creerRaquette(World w) {
        //nous créons notre raquette ici
        Entity raquette = w.createEntity();
        //nous ajoutons ici les composants de notre raquette
        //On va directement chercher la hauteur de notre fenêtre de jeu grâce Gdx.graphics.getHeight()
        raquette.addComponent(new Position(1, Gdx.graphics.getHeight()-80));
        raquette.addComponent(new Vitesse(0, 0));
 
        //ici 2 correspond à la forme "Rectangle"
        raquette.addComponent(new Forme(2));
 
        //notre raquette fera 70px de largeur et 10px de hauteur
        raquette.addComponent(new Dimension(100, 20));
        
        //on enregistre notre raquette de manière unique grâce au TagManager
        w.getManager(TagManager.class).register("RAQUETTE", raquette);
        return raquette;
    }
    
    public static Entity creerBrique(World w, int x, int y) {
     	//nous créons une brique
        Entity brique = w.createEntity();
        //nous ajouterons ici des composants à notre brique
        brique.addComponent(new Position(x, y));
 
        //notre forme est un rectangle.
        brique.addComponent(new Forme(2));

        //nos briques auront les mêmes dimensions que la raquette
        brique.addComponent(new Dimension(130, 20));
 
        //notre fameux "état", mis à fals pour indiquer "pas cassé"
        brique.addComponent(new Etat(false));
 
        //on enregistre notre brique dans le groupe des briques
        w.getManager(GroupManager.class).add(brique, "BRIQUES");
 
        return brique;
    }
}