package com.mygdx.game.android;

import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.facebook.*;
import com.facebook.internal.Utility;
import com.facebook.model.*;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.ProfilePictureView;
import com.mygdx.game.FabriqueEntite;
import com.mygdx.game.WallBreacker;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionFragment extends Fragment {

    private static final String TAG = "SelectionFragment";
    private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
    private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
    private static final int USER_GENERATED_MIN_SIZE = 480;

    private static final int REAUTH_ACTIVITY_CODE = 100;
    private static final String PERMISSION = "publish_actions";

    private ListView listView;
    private List<BaseListElement> listElements;
    private ProfilePictureView profilePictureView;
    private boolean pendingAnnounce;
    private MainActivity activity;
    private Uri photoUri;
    private ImageView photoThumbnail;
    private List<GraphUser> selectedUsers;

    private UiLifecycleHelper uiHelper;
    
    private Session.StatusCallback sessionCallback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private FacebookDialog.Callback nativeDialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            boolean resetSelections = true;
            if (FacebookDialog.getNativeDialogDidComplete(data)) {
                if (FacebookDialog.COMPLETION_GESTURE_CANCEL
                        .equals(FacebookDialog.getNativeDialogCompletionGesture(data))) {
                    // Leave selections alone if user canceled.
                    resetSelections = false;
                    showCancelResponse();
                } else {
                    showSuccessResponse(FacebookDialog.getNativeDialogPostId(data));
                }
            }

            if (resetSelections) {
                init(null);
            }
        }

        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.error_dialog_button_text, null)
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(error.getLocalizedMessage())
                    .show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        uiHelper = new UiLifecycleHelper(getActivity(), sessionCallback);
        uiHelper.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.selection, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.selection_profile_pic);
        profilePictureView.setCropped(true);
        listView = (ListView) view.findViewById(R.id.selection_list);
        photoThumbnail = (ImageView) view.findViewById(R.id.selected_image);
        
        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showSettingsFragment();
            }
        });

        init(savedInstanceState);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode >= 0 && requestCode < listElements.size()) {
            listElements.get(requestCode).onActivityResult(data);
        } else {
            uiHelper.onActivityResult(requestCode, resultCode, data, nativeDialogCallback);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        for (BaseListElement listElement : listElements) {
            listElement.onSaveInstanceState(bundle);
        }
        bundle.putBoolean(PENDING_ANNOUNCE_KEY, pendingAnnounce);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
        activity = null;
    }

    /**
     * Notifies that the session token has been updated.
     */
    private void tokenUpdated() {
        
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                tokenUpdated();
            } else {
                makeMeRequest(session);
            }
        } else {
            profilePictureView.setProfileId(null);
        }
    }

    private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        profilePictureView.setProfileId(user.getId());
                    }
                }
                if (response.getError() != null) {
                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();

    }

    /**
     * Resets the view to the initial defaults.
     */
    private void init(Bundle savedInstanceState) {
    	listElements = new ArrayList<BaseListElement>();

        listElements.add(new PeopleListElement(0));

        if (savedInstanceState != null) {
            for (BaseListElement listElement : listElements) {
                listElement.restoreState(savedInstanceState);
            }
            pendingAnnounce = savedInstanceState.getBoolean(PENDING_ANNOUNCE_KEY, false);
        }

        listView.setAdapter(new ActionListAdapter(getActivity(), R.id.selection_list, listElements));

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            makeMeRequest(session);
        }
    }

    private Pair<File, Integer> getImageFileAndMinDimension() {
        File photoFile = null;
        String photoUriString = photoUri.toString();
        if (photoUriString.startsWith("file://")) {
            photoFile = new File(photoUri.getPath());
        } else if (photoUriString.startsWith("content://")) {
            String [] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(photoUri, filePath, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePath[0]);
                String filename = cursor.getString(columnIndex);
                cursor.close();

                photoFile = new File(filename);
            }
        }

        if (photoFile != null) {
            InputStream is = null;
            try {
                is = new FileInputStream(photoFile);

                // We only want to get the bounds of the image, rather than load the whole thing.
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);

                return new Pair<File, Integer>(photoFile, Math.min(options.outWidth, options.outHeight));
            } catch (Exception e) {
                return null;
            } finally {
                Utility.closeQuietly(is);
            }
        }
        return null;
    }

    /**
     * Creates a GraphObject with the following format:
     * {
     *     url: ${uri},
     *     user_generated: true
     * }
     */
    private GraphObject getImageObject(String uri, boolean userGenerated) {
        GraphObject imageObject = GraphObject.Factory.create();
        imageObject.setProperty("url", uri);
        if (userGenerated) {
            imageObject.setProperty("user_generated", "true");
        }
        return imageObject;
    }

    private List<JSONObject> getImageListForAction(String uri, boolean userGenerated) {
        return Arrays.asList(getImageObject(uri, userGenerated).getInnerJSONObject());
    }

    private void requestPublishPermissions(Session session) {
        if (session != null) {
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSION)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }

    private void showSuccessResponse(String postId) {
        String dialogBody;
        if (postId != null) {
            dialogBody = String.format(getString(R.string.result_dialog_text_with_id), postId);
        } else {
            dialogBody = getString(R.string.result_dialog_text_default);
        }
        showResultDialog(dialogBody);
    }

    private void showCancelResponse() {
        showResultDialog(getString(R.string.result_dialog_text_canceled));
    }

    private void showResultDialog(String dialogBody) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.result_dialog_button_text, null)
                .setTitle(R.string.result_dialog_title)
                .setMessage(dialogBody)
                .show();
    }

    private void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case PERMISSION:
                    // request the publish permission
                    dialogBody = getString(R.string.error_permission);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pendingAnnounce = true;
                            requestPublishPermissions(Session.getActiveSession());
                        }
                    };
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        String title = error.getErrorUserTitle();
        String message = error.getErrorUserMessage();
        if (message == null) {
            message = dialogBody;
        }
        if (title == null) {
            title = getResources().getString(R.string.error_dialog_title);
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

    private void startPickerActivity(Uri data, int requestCode) {
        Intent intent = new Intent();
        intent.setData(data);
        intent.setClass(getActivity(), PickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    /**
     * Used to inspect the response from posting an action
     */
    private interface PostResponse extends GraphObject {
        String getId();
    }

    public class PeopleListElement extends BaseListElement {

        private static final String FRIENDS_KEY = "friends";

		public PeopleListElement(int requestCode) {
            super(getActivity().getResources().getDrawable(R.drawable.add_friends),
                    getActivity().getResources().getString(R.string.action_people),
                    null,
                    requestCode);
        }

        @Override
        protected View.OnClickListener getOnClickListener() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Session.getActiveSession() != null &&
                            Session.getActiveSession().isOpened()) {
                        startPickerActivity(PickerActivity.FRIEND_PICKER, getRequestCode());
                    } else {
                        activity.showSettingsFragment();
                    }
                }
            };
        }

        @Override
        protected void onActivityResult(Intent data) {
            selectedUsers = ((WallBreackerApplication) getActivity().getApplication()).getSelectedUsers();
            setUsersText();
            notifyDataChanged();
        }

        @Override
        protected void populateOGAction(OpenGraphAction action) {
            if (selectedUsers != null) {
                action.setTags(selectedUsers);
            }
        }

        @Override
        protected void onSaveInstanceState(Bundle bundle) {
            if (selectedUsers != null) {
                bundle.putByteArray(FRIENDS_KEY, getByteArray(selectedUsers));
            }
        }

        @Override
        protected boolean restoreState(Bundle savedState) {
            byte[] bytes = savedState.getByteArray(FRIENDS_KEY);
            if (bytes != null) {
                selectedUsers = restoreByteArray(bytes);
                setUsersText();
                return true;
            }
            return false;
        }

        private void setUsersText() {
            String text = null;
            if (selectedUsers != null) {
                if (selectedUsers.size() == 1) {
                    text = String.format(getResources().getString(R.string.single_user_selected),
                            selectedUsers.get(0).getName());
                } else if (selectedUsers.size() == 2) {
                    text = String.format(getResources().getString(R.string.two_users_selected),
                            selectedUsers.get(0).getName(), selectedUsers.get(1).getName());
                } else if (selectedUsers.size() > 2) {
                	text = String.format(getResources().getString(R.string.users_selected))+"\n";
                    for (int i = 0 ; i < selectedUsers.size(); i++) {
                    	text += selectedUsers.get(i).getName()+"\n";
                    }
                }
                
            }
            if (text == null) {
                text = getResources().getString(R.string.action_people_default);
            }
            setText2(text);
        }

		private byte[] getByteArray(List<GraphUser> users) {
            // convert the list of GraphUsers to a list of String where each element is
            // the JSON representation of the GraphUser so it can be stored in a Bundle
            List<String> usersAsString = new ArrayList<String>(users.size());

            for (GraphUser user : users) {
                usersAsString.add(user.getInnerJSONObject().toString());
            }
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                new ObjectOutputStream(outputStream).writeObject(usersAsString);
                return outputStream.toByteArray();
            } catch (IOException e) {
                Log.e(TAG, "Unable to serialize users.", e);
            }
            return null;
        }

        private List<GraphUser> restoreByteArray(byte[] bytes) {
            try {
                @SuppressWarnings("unchecked")
                List<String> usersAsString =
                        (List<String>) (new ObjectInputStream(new ByteArrayInputStream(bytes))).readObject();
                if (usersAsString != null) {
                    List<GraphUser> users = new ArrayList<GraphUser>(usersAsString.size());
                    for (String user : usersAsString) {
                        GraphUser graphUser = GraphObject.Factory
                                .create(new JSONObject(user), GraphUser.class);
                        users.add(graphUser);
                    }
                    return users;
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (IOException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            } catch (JSONException e) {
                Log.e(TAG, "Unable to deserialize users.", e);
            }
            return null;
        }
    }

    private class ActionListAdapter extends ArrayAdapter<BaseListElement> {
        private List<BaseListElement> listElements;

        public ActionListAdapter(Context context, int resourceId, List<BaseListElement> listElements) {
            super(context, resourceId, listElements);
            this.listElements = listElements;
            for (int i = 0; i < listElements.size(); i++) {
                listElements.get(i).setAdapter(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater =
                        (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listitem, null);
            }

            BaseListElement listElement = listElements.get(position);
            if (listElement != null) {
                view.setOnClickListener(listElement.getOnClickListener());
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                TextView text1 = (TextView) view.findViewById(R.id.text1);
                TextView text2 = (TextView) view.findViewById(R.id.text2);
                if (icon != null) {
                    icon.setImageDrawable(listElement.getIcon());
                }
                if (text1 != null) {
                    text1.setText(listElement.getText1());
                }
                if (text2 != null) {
                    if (listElement.getText2() != null) {
                        text2.setVisibility(View.VISIBLE);
                        text2.setText(listElement.getText2());
                    } else {
                        text2.setVisibility(View.GONE);
                    }
                }
            }
            return view;
        }

    }
        
	/**
	 * @return the selectedUsers
	 */
	public List<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	/**
	 * @param selectedUsers the selectedUsers to set
	 */
	public void setSelectedUsers(List<GraphUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
	
	
}
