package com.example.cordova.plugin;

// The native Toast API
import android.widget.Toast;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RTCPlugin extends CordovaPlugin {

  private static final String DURATION_LONG = "long";
  @Override
  public boolean execute(String action, JSONArray args,
    final CallbackContext callbackContext) {
      // Verify that the user sent a 'show' action
      if (!action.equals("echo")) {
        callbackContext.error("===  \"" + action + "\" is not a recognized action.");
        return false;
      }
      System.out.println("=== in rtcplugin");
      String message;
      String duration;
      try {
        message = args.getString(0);
      } catch (JSONException e) {
        callbackContext.error("=== Error encountered: " + e.getMessage());
        return false;
      }
      // Create the toast
      Toast toast = Toast.makeText(cordova.getActivity(), message,         Toast.LENGTH_LONG );
      // Display toast
      toast.show();
      // Send a positive result to the callbackContext
    //   PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
      callbackContext.success(message);
    //   callbackContext.sendPluginResult(pluginResult);
      return true;
  }
}