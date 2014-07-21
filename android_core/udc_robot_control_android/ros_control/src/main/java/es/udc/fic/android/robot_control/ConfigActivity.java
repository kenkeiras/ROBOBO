/*
 * Copyright (C) 2013 Amancio Díaz Suárez
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package es.udc.fic.android.robot_control;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.ros.node.NodeConfiguration;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

public class ConfigActivity extends Activity {
    /**
     * The key with which the last used {@link java.net.URI} will be stored as a
     * preference.
     */
    public static final String PREFS_KEY_URI  = "URI_KEY";
    public static final String PREFS_KEY_ROBOT_NAME = "ROBOT_NAME_KEY";
    private static final String DEFAULT_ROBOT_NAME = "robot1";

    private String masterUri;
    private String robotName;

    private EditText uriText;
    private EditText robotText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        uriText = (EditText) findViewById(R.id.etRosMasterUrl);
        robotText = (EditText) findViewById(R.id.etRobotName);

        masterUri = getPreferences(MODE_PRIVATE).getString(PREFS_KEY_URI,
                        NodeConfiguration.DEFAULT_MASTER_URI.toString());

        robotName = getPreferences(MODE_PRIVATE).getString(PREFS_KEY_ROBOT_NAME, DEFAULT_ROBOT_NAME);

        uriText.setText(masterUri);
        robotText.setText(robotName);
    }


    public void okButtonClicked(View unused) {
        // Get the current text entered for URI.
        String userUri = uriText.getText().toString();
        String userRobotName = robotText.getText().toString();

        boolean allok = true;


        if (userUri.length() == 0) {
            // If there is no text input then set it to the default URI.
            userUri = NodeConfiguration.DEFAULT_MASTER_URI.toString();
            uriText.setText(userUri);
            Toast.makeText(this, "La URL de roscore no es válida.", Toast.LENGTH_SHORT).show();
            allok = false;
        }

        if (userRobotName.length() == 0) {
            userRobotName = DEFAULT_ROBOT_NAME;
            robotText.setText(userRobotName);
            Toast.makeText(this, "El nombre del robot no es válido.", Toast.LENGTH_SHORT).show();
            allok = false;
        }

        // Make sure the URI can be parsed correctly.
        try {
            new URI(userUri);
        } catch (URISyntaxException e) {
            Toast.makeText(this, "Invalid URI.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (allok) {
            // If the displayed URI is valid then pack that into the intent.
            masterUri = userUri;
            robotName = userRobotName;

            SharedPreferences.Editor editor = getSharedPreferences(
                ConfigActivity.class.getName(), MODE_PRIVATE).edit();
            Log.v("UDC", "MASTER URI: " + masterUri);
            editor.putString(PREFS_KEY_URI, masterUri);
            editor.putString(PREFS_KEY_ROBOT_NAME, robotName);
            editor.commit();

            // Package the intent to be consumed by the calling activity.
            Intent intent = new Intent();
            intent.putExtra("ROS_MASTER_URI", masterUri);
            intent.putExtra("ROS_ROBOT_NAME", robotName);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
