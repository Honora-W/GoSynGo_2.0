package edu.scu.core.callback;

import android.util.Log;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;

import java.util.List;
import java.util.Map;

import edu.scu.model.Event;

/**
 * Created by chuanxu on 5/20/16.
 */
public class DefaultChannelMessageResponder implements AsyncCallback<List<Message>> {

    private String personId;
    private List<Event> undecidedEventList;

    public DefaultChannelMessageResponder(String personId, List<Event> undecidedEventList) {
        this.personId = personId;
        this.undecidedEventList = undecidedEventList;
    }

    @Override
    public void handleResponse(List<Message> messages) {

        for (Message message : messages) {
            if (!message.getHeaders().get(personId).equals("true")) {
                continue;
            }

            Map<String, Object> eventDataMap = ((Map<String, Object>) message.getData());

            Event undecidedEvent = new Event();
            undecidedEvent.setTitle((String) eventDataMap.get("title"));
            undecidedEvent.setNote((String) eventDataMap.get("note"));
            undecidedEvent.setLocation((String) eventDataMap.get("location"));
            undecidedEvent.setDurationInMin((Integer) eventDataMap.get("durationInMin"));
            undecidedEvent.setStatusEvent((Integer) eventDataMap.get("statusEvent"));
            undecidedEvent.setHasReminder((Boolean) eventDataMap.get("hasReminder"));
            undecidedEvent.setReminderInMin((Integer) eventDataMap.get("reminderInMin"));
            undecidedEvent.setObjectId((String) eventDataMap.get("objectId"));
            undecidedEvent.setOwnerId((String) eventDataMap.get("ownerId"));
            undecidedEventList.add(undecidedEvent);
        }
    }

    @Override
    public void handleFault(BackendlessFault backendlessFault) {
        Log.i("cxu", backendlessFault.getMessage());
    }

}
