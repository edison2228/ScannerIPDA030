package cordova.plugin.ipda030.scanner;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class Scanner extends CordovaPlugin {
	ScanDevice sm;
	private final static String SCAN_ACTION = "scan.rcv.message";
	private final static String EVENT_PREFIX = "scanner";
    private CallbackContext mMainCallback;
    
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			byte[] barocode = intent.getByteArrayExtra("barocode");
			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			String barcodeStr = new String(barocode, 0, barocodelen);

			JSONArray jsEvent = new JSONArray();
			jsEvent.put(EVENT_PREFIX + "Scan");
			jsEvent.put(barcodeStr);
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsEvent);
			pluginResult.setKeepCallback(true);
			mMainCallback.sendPluginResult(pluginResult);

			sm.stopScan();
		}
	};
	protected void onResume(boolean multitasking) {
        

        super.onResume(multitasking);
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SCAN_ACTION);
		registerReceiver(mScanReceiver, filter);
	}
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("init".equals(action)) {
			mMainCallback = callbackContext;
			this.onResume(false);
			return true;
		} else if(action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}
