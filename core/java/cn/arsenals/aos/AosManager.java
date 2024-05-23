package cn.arsenals.aos;

import android.os.RemoteException;
import android.util.Log;

public class AosManager {
    private static final String TAG = "AosManager";

    private IAosService mIAosService;

    public AosManager(IAosService service) {
        mIAosService = service;
    }

    public int getAosVersionNumber() {
        int ret = -1;
        try {
            ret = mIAosService.getAosVersionNumber();
        } catch (RemoteException e) {
            Log.e(TAG, "getAosVersionNumber RemoteException");
        }
        return ret;
    }
}
