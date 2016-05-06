package edu.scu.core.task;

import edu.scu.api.Api;
import edu.scu.api.ApiResponse;
import edu.scu.core.ActionCallbackListener;
import edu.scu.model.Person;

/**
 * Created by chuanxu on 5/6/16.
 */
public class UnregisterDeviceAsyncTask extends BaseAsyncTask {

    public UnregisterDeviceAsyncTask(Api api, ActionCallbackListener listener, Person hostPerson) {
        super(api, listener, hostPerson);
    }

    @Override
    protected ApiResponse<Void> doInBackground(Object... params) {
        return api.unregisterDevice();
    }

    @Override
    protected void onPostExecute(ApiResponse response) {
        // do nothing
    }

}
