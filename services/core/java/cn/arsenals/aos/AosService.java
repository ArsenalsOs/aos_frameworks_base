package cn.arsenals.aos;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

public class AosService extends IAosService.Stub {
    private static final String TAG = "AosService";

    private Context mContext;

    public AosService(Context context) {
        mContext = context;
    }

    @Override
    public int getAosVersionNumber() throws RemoteException {
        int version = -1;
        Log.i(TAG, "getAosVersionNumber version " + version);
        return version;
    }
}
