package com.mygdx.game.android;

import java.util.List;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;

import android.app.Application;

public class WallBreackerApplication extends Application {
	private List<GraphUser> selectedUsers;
	
	public WallBreackerApplication() {
    	this.selectedUsers = selectedUsers;
    }

    public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<GraphUser> users) {
        selectedUsers = users;
    }

}
