package com.mygdx.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.surfaceview.RatioResolutionStrategy;
import com.facebook.UiLifecycleHelper;
import com.mygdx.game.WallBreacker;
import com.sun.javafx.scene.control.SelectedCellsMap;

public class LaunchWallBreacker extends AndroidApplication {
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        SelectionFragment sf = new SelectionFragment();
	        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
	        /*int width = 200;
	        int height = 200;
	        cfg.resolutionStrategy = new RatioResolutionStrategy(width, height);*/
	        initialize(new WallBreacker(), cfg);
	    }
}
