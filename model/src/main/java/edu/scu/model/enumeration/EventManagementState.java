package edu.scu.model.enumeration;

/**
 * Created by chuanxu on 5/9/16.
 */
public enum EventManagementState {

    // case 0: push all members through "default" channel to invite
    SEND_INVITATION("0"),
    // case 1: push all members through event channel to select time
    REMIND_TO_VOTE("1"),
    // case 2: push all members through event channel to notify final time
    NOTIFY_FINAL_TIME("2"),
    // case 3(delay): push all members through event channel to remind
    EVENT_REMINDER("3"),
    // case 4(delay): push all members through event channel to checkin or estimate
    CHECKIN_OR_ESTIMATE("4"),
    // case 5: push all members through event channel to notify event's cancelled
    CANCEL_EVENT("5");


    private String status;

    EventManagementState(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
