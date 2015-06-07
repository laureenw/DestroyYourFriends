package com.mygdx.game.composants;

import com.artemis.Component;

public class Vitesse extends Component {
	 
    public float incX, incY;
 
    public Vitesse(float incX, float incY) {
        this.incX = incX;
        this.incY = incY;
    }
}