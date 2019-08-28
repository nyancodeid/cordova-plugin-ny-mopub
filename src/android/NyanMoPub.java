package id.nyandev.cordova.mopub.android;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import com.mopub.common.MoPub;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubInterstitial.InterstitialAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class NyanMoPub extends CordovaPlugin {
  private final String LOG_TAG = "NyanMoPub";
  protected String appId;
  protected CallbackContext callbackContextGlobal;
  protected CallbackContext callbackContextTemp;
  protected boolean isSendEvent = false;

  private MoPubInterstitial mInterstitial;
  private boolean isInterstitialTest = true;
  private String INTERSTITIAL_UNIT_TEST = "24534e1901884e398f1253216226017e";

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("initialize")) {
      String appId = args.getString(0);
      this.initializeSDK(appId, callbackContext);
      return true;
    } else if (action.equals("load")) {
      String adsType = args.getString(0);
      String placementId = args.getString(1);

      this.loadAd(adsType, placementId, callbackContext);
      return true;
    } else if (action.equals("isLoaded")) {
      String adsType = args.getString(0);

      this.isLoaded(adsType, callbackContext);
      return true;
    } else if (action.equals("show")) {
      String adsType = args.getString(0);

      this.showAd(adsType, callbackContext);
      return true;
    } 
    return false;
  }

  private void initializeSDK(String appId, CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        _initializeMoPub(appId, callbackContext);
      }
    });
  }
  private void loadAd(String adsType, String adsId, CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (adsType.equals("interstitial")) {
          isInterstitialTest = false;
          _loadAd(adsId, callbackContext);
        } else if (adsType.equals("test")) {
          isInterstitialTest = true;
          _loadAd(INTERSTITIAL_UNIT_TEST, callbackContext);
        }
      }
    });
  }
  private void isLoaded(String adsType, CallbackContext callbackContext) {
    boolean isLoad = false;

    if (adsType.equals("interstitial") || adsType.equals("test")) {
      isLoad = mInterstitial.isReady();
    } 

    JSONObject response = new JSONObject();

    try {
      response.put("adsType", adsType);
      response.put("isLoaded", isLoad);
    } catch(JSONException e) {
      callbackContext.error("isLoaded E:" + e.getMessage());
    } 

    callbackContext.success(response);
  }
  private void showAd(String adsType, CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        _showAd(adsType, callbackContext);
      }
    });
  }

  private void _initializeMoPub(String appId, CallbackContext callbackContext) {
    this.appId = appId;
    callbackContextGlobal = callbackContext;

    SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(appId)
      .withLogLevel(MoPubLog.LogLevel.DEBUG)
      .withLegitimateInterestAllowed(false)
      .build();

    MoPub.initializeSdk(cordova.getActivity().getApplicationContext(), sdkConfiguration, new SdkInitializationListener() {
      @Override
      public void onInitializationFinished() {
        JSONObject response = new JSONObject();
        try {
          response.put("success", true);
          response.put("appId", appId);
        } catch (JSONException e) {
          _sendExceptionResponse(e);
        }
        _sendResponse(response);
      }
    });
  }
  private void _loadAd(String adsId, CallbackContext callbackContext) {
    try {
      mInterstitial = new MoPubInterstitial(cordova.getActivity(), adsId);
      mInterstitial.setInterstitialAdListener(interstitialAdCallback);
      mInterstitial.load();
    } catch(final Exception e) {
      callbackContext.error("MoPub.loadAd E: " + e.getMessage());
    }

    callbackContext.success();
  }
  private void _showAd(String adsType, CallbackContext callbackContext) {
    callbackContextTemp = callbackContext;

    if (adsType.equals("interstitial") || adsType.equals("test")) {
      if (mInterstitial.isReady()) {
        try {
          mInterstitial.show();
        } catch(final Exception e) {
          callbackContext.error("MoPub.playAd E:" + e.getMessage());
        }
      } else {
        callbackContext.error("showAd E: ads not loaded");
      }
    }

    callbackContext.success();
  }
  private void _sendExceptionResponse(Exception e) {
    JSONObject exception = new JSONObject();
    try {
      exception.put("success", false);
      exception.put("error", e.getMessage());
    } catch(JSONException err) {
      Log.d(LOG_TAG, "sendExecptionResponse E:" + err.getMessage());	
    }

    this._sendResponse(exception);
  }
  private void _sendExceptionResponse(JSONException e) {
    JSONObject exception = new JSONObject();
    try {
      exception.put("success", false);
      exception.put("error", e.getMessage());
    } catch(JSONException err) {
      Log.d(LOG_TAG, "sendExecptionResponse E:" + err.getMessage());	
    }

    this._sendResponse(exception);
  }
  private void _sendExceptionResponse(Throwable e) {
    JSONObject exception = new JSONObject();
    try {
      exception.put("success", false);
      exception.put("error", e.getMessage());
    } catch(JSONException err) {
      Log.d(LOG_TAG, "sendExecptionResponse E:" + err.getMessage());	
    }

    this._sendResponse(exception);
  }
  private void _sendResponse(JSONObject data) {
    PluginResult pr = new PluginResult(PluginResult.Status.OK, data);
    pr.setKeepCallback(true);
    callbackContextGlobal.sendPluginResult(pr);
  }
  private void _sendResponse(JSONObject data, CallbackContext callbackContext) {
    PluginResult pr = new PluginResult(PluginResult.Status.OK, data);
    pr.setKeepCallback(true);
    
    if (callbackContext != null) {
      callbackContextGlobal.sendPluginResult(pr);
    } else {
      callbackContext.sendPluginResult(pr);
    }
  }

  private final InterstitialAdListener interstitialAdCallback = new InterstitialAdListener() {
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
      // The interstitial has been cached and is ready to be shown.
      fireAdEvent("onInterstitialLoaded", "interstitial");
    }
    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
      // The interstitial has failed to load. Inspect errorCode for additional information.
      final String errorMessage = (errorCode != null) ? errorCode.toString() : "";
      fireAdEvent("onInterstitialFailed", "interstitial", errorMessage);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
      // The interstitial has been shown. Pause / save state accordingly..
      fireAdEvent("onInterstitialShown", "interstitial");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
      fireAdEvent("onInterstitialClicked", "interstitial");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
      // The interstitial has being dismissed. Resume / load state accordingly.
      fireAdEvent("onInterstitialDismissed", "interstitial");
    }
  };

  private void fireAdEvent(String event, String adType) {
    String obj = "MoPub";
    String json = String.format("{'adNetwork':'%s','adType':'%s','adEvent':'%s'}", new Object[] { obj, adType, event });
    fireEvent(obj, event, json);
  }
  private void fireAdEvent(String event, String adType, String data) {
    String obj = "MoPub";
    String json = String.format("{'adNetwork':'%s','adType':'%s','adEvent':'%s','adData':'%s'}", new Object[] { obj, adType, event, data });
    fireEvent(obj, event, json);
  }

  private void fireEvent(String obj, String eventName, String jsonData) {
    String js;
    if("window".equals(obj)) {
      js = "var evt=document.createEvent('UIEvents');evt.initUIEvent('" + eventName + "',true,false,window,0);window.dispatchEvent(evt);";
    } else {
      js = "javascript:cordova.fireDocumentEvent('" + eventName + "'";
      if(jsonData != null) {
        js += "," + jsonData;
      }
      js += ");";
    }
    webView.loadUrl(js);
  }

  @Override
  public void onDestroy() {
      super.onDestroy();

      if (mInterstitial != null) {
          mInterstitial.destroy();
          mInterstitial = null;
      }
  }
}
