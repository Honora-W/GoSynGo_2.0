package edu.scu.gsgapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.scu.core.ActionCallbackListener;
import edu.scu.gsgapp.R;
import edu.scu.gsgapp.adapter.dashboard.events.MemberHorizontalViewAdapter;
import edu.scu.gsgapp.adapter.propose.ProposeEventAddMemberAdapter;
import edu.scu.gsgapp.fragment.BaseWeekViewFragment;
import edu.scu.gsgapp.fragment.FragmentDateCommunicator;
import edu.scu.model.Event;
import edu.scu.model.EventLeaderDetail;
import edu.scu.model.EventMemberDetail;
import edu.scu.model.LeaderProposedTimestamp;

/**
 * Created by chuanxu on 5/26/16.
 */
public class EventDetailActivity extends GsgBaseActivity implements FragmentDateCommunicator {

    //host role to weekView fragment
    public static final int FOR_LEADER = 100;
    public static final int FOR_MEMBER = 101;

    // Common
    private Toolbar toolbar;

    // Leader not ready
    private EditText locationEditText;
    private EditText noteEditText;
    private TextView durationLeaderTextView;
    private TextView reminderLeaderTextView;
    private HorizontalGridView memberHorizontalViewForLeader;
    private Button nextButton;
    private Button cancelEventButton;

    // Member not ready
    private TextView locationTextView;
    private TextView noteTextView;
    private TextView durationTextView;
    private TextView reminderMemberTextView;
    private HorizontalGridView memberHorizontalViewForMember;
    private Button memberNextButton;
    private Button leaveButton;

    // Ready
    private TextView titleReadyTextView;
    private TextView locationReadyTextView;
    private TextView noteReadyTextView;
    private TextView durationReadyTextView;
    private TextView timeReadyTextView;
    private TextView reminderReadyTextView;
    private GridView eventRedyGridview;

    private Event event;
    //for leader usage
    private List<Date> leaderViewLeaderProposedTimestamps;
    private List<Date> leaderViewMemberProposedTimestamps;
    private List<Date> leaderViewAllMemberProposedTimestamps;
    //for member usage
    private List<Date> memberViewLeaderProposedTimestamps;
    private List<Date> memberViewMemberProposedTimestamps;
    //for leader proposed check
    public final static int LEADER_PROPOSED = 200;
    public final static int LEADER_NOT_PROPOSED = 201;
    private int ifLeaderProposed;



    private Date eventTimestamp;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        String eventState = intent.getStringExtra("eventState");

        boolean isEventLeader = isEventLeader(eventId);
        String hostRole = isEventLeader ? "leader" : "member";
        event = isEventLeader ? appAction.getHostPerson().getEventAsLeader(eventId) : appAction.getHostPerson().getEventAsMember(eventId);

        String eventDetailProperty = hostRole + eventState;
        switch (eventDetailProperty) {
            case "leaderNotReady":
                setContentView(R.layout.activity_event_detail_not_ready_leader);
                initWidgetable(eventDetailProperty);
                initListener(eventDetailProperty);

                // sichao
                BaseWeekViewFragment baseWeekViewFragmentLeader = new BaseWeekViewFragment();
                Bundle bundleLeader = new Bundle();

                if(event.getEventLeaderDetail().getProposedTimestamps().isEmpty()) {
                    ifLeaderProposed = EventDetailActivity.LEADER_NOT_PROPOSED;
                } else {
                    ifLeaderProposed = EventDetailActivity.LEADER_PROPOSED;
                    leaderViewLeaderProposedTimestamps = new ArrayList<>();
                    for(LeaderProposedTimestamp timestamp: event.getEventLeaderDetail().getProposedTimestamps()) {
                        leaderViewLeaderProposedTimestamps.add(timestamp.getTimestamp());
                    }
                }

                bundleLeader.putInt("hostRole", EventDetailActivity.FOR_LEADER);
                bundleLeader.putString("eventTitle", event.getTitle());
                bundleLeader.putInt("ifLeaderProposed", ifLeaderProposed);
                ArrayList<String> leaderViewLeaderProposedEncodedDates = encodeDate(leaderViewLeaderProposedTimestamps);
                bundleLeader.putStringArrayList("leaderViewLeaderProposedEncodedDates", leaderViewLeaderProposedEncodedDates);
                baseWeekViewFragmentLeader.setArguments(bundleLeader);
                getSupportFragmentManager().beginTransaction().add(R.id.event_detail_not_ready_leader_day_calendar_container, baseWeekViewFragmentLeader).commit();
                break;

            case "memberNotReady":
                setContentView(R.layout.activity_event_detail_not_ready_member);
                initWidgetable(eventDetailProperty);
                initListener(eventDetailProperty);

                //sichao
                BaseWeekViewFragment baseWeekViewFragmentMember = new BaseWeekViewFragment();
                Bundle bundleMember = new Bundle();

                if(event.getEventLeaderDetail().getProposedTimestamps().isEmpty()) {
                    ifLeaderProposed = EventDetailActivity.LEADER_NOT_PROPOSED;
                } else {
                    ifLeaderProposed = EventDetailActivity.LEADER_PROPOSED;
                    memberViewLeaderProposedTimestamps = new ArrayList<>();
                    for(LeaderProposedTimestamp timestamp: event.getEventLeaderDetail().getProposedTimestamps()) {
                        memberViewLeaderProposedTimestamps.add(timestamp.getTimestamp());
                    }
                }

                bundleMember.putInt("hostRole", EventDetailActivity.FOR_MEMBER);
                bundleMember.putString("eventTitle", event.getTitle());
                bundleMember.putInt("ifLeaderProposed", ifLeaderProposed);
                ArrayList<String> memberViewLeaderProposedEncodedDates = encodeDate(memberViewLeaderProposedTimestamps);
                bundleMember.putStringArrayList("memberViewLeaderProposedEncodedDates", memberViewLeaderProposedEncodedDates);
                baseWeekViewFragmentMember.setArguments(bundleMember);
                getSupportFragmentManager().beginTransaction().add(R.id.event_detail_not_ready_member_day_calendar_container, baseWeekViewFragmentMember).commit();
                break;
            default:    // "leaderReady" || "memberReady"
                setContentView(R.layout.activity_event_detail_ready);
                initWidgetable(eventDetailProperty);
                initListener(eventDetailProperty);
                break;
        }


    }

    private boolean isEventLeader(String eventId) {
        boolean isEventLeader = appAction.getHostPerson().getEventAsLeader(eventId) != null;
        return isEventLeader;
    }

    private void initWidgetable(String eventDetailProperty) {

        initWidgetableCommon();

        switch (eventDetailProperty) {
            case "leaderNotReady":
                ((TextView) findViewById(R.id.toolbar_event_detail_not_ready_leader_title)).setText(event.getTitle());

                this.locationEditText = (EditText) findViewById(R.id.edit_text_event_detail_not_ready_leader_location);
                this.locationEditText.setText(event.getLocation());

                this.noteEditText = (EditText) findViewById(R.id.edit_text_event_detail_not_ready_leader_note);
                this.noteEditText.setText(event.getNote());

                this.durationLeaderTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_leader_duration);
                this.durationLeaderTextView.setText(event.getDurationInMin().toString());

                this.reminderLeaderTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_leader_reminder);
                String reminderLeaderText = event.getHasReminder() ? event.getReminderInMin().toString() : "No reminder";
                this.reminderLeaderTextView.setText(reminderLeaderText);

                this.memberHorizontalViewForLeader = (HorizontalGridView) findViewById(R.id.event_detail_not_ready_leader_event_member_grid_view);
                List<String> memberList = new ArrayList<>();
                for(EventMemberDetail eventMemberDetail : event.getEventMemberDetail()) {
                    memberList.add(eventMemberDetail.getMember().getFirstName());
                }
                MemberHorizontalViewAdapter adapter = new MemberHorizontalViewAdapter(this, memberList);
                this.memberHorizontalViewForLeader.setAdapter(adapter);

                this.nextButton = (Button) findViewById(R.id.button_event_detail_not_ready_leader_next);
                this.nextButton.setEnabled(false);
                if (event.getEventLeaderDetail().getProposedTimestamps().isEmpty()) {
                    this.nextButton.setText("Start time vote");
                } else {
                    this.nextButton.setText("Notify final time");
                }

                this.cancelEventButton = (Button) findViewById(R.id.button_event_detail_not_ready_leader_cancel);
                break;

            case "memberNotReady":
                ((TextView) findViewById(R.id.toolbar_event_detail_not_ready_member_title)).setText(event.getTitle());

                this.locationTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_member_location);
                this.locationTextView.setText(event.getLocation());

                this.noteTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_member_note);
                this.noteTextView.setText(event.getNote());

                this.durationTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_member_duration);
                this.durationTextView.setText(event.getDurationInMin().toString());

                this.reminderMemberTextView = (TextView) findViewById(R.id.text_view_event_detail_not_ready_member_reminder);
                String reminderMemberText = event.getHasReminder() ? event.getReminderInMin().toString() : "No reminder";
                this.reminderMemberTextView.setText(reminderMemberText);

                this.memberHorizontalViewForMember = (HorizontalGridView) findViewById(R.id.event_detail_not_ready_member_event_member_grid_view);
                List<String> memberListInMember = new ArrayList<>();
                for(EventMemberDetail eventMemberDetail : event.getEventMemberDetail()) {
                    memberListInMember.add(eventMemberDetail.getMember().getFirstName());
                }
                MemberHorizontalViewAdapter adapterInmember = new MemberHorizontalViewAdapter(this, memberListInMember);
                this.memberHorizontalViewForMember.setAdapter(adapterInmember);

                this.memberNextButton = (Button) findViewById(R.id.button_event_detail_not_ready_member_next);
                this.memberNextButton.setEnabled(false);
                this.memberNextButton.setText("Send my preferred time");

                this.leaveButton = (Button) findViewById(R.id.button_event_detail_not_ready_member_leave);
                break;

            default:    // "leaderReady" || "memberReady"
                ((TextView) findViewById(R.id.toolbar_event_detail_ready_title)).setText(event.getTitle());

                this.locationReadyTextView = (TextView) findViewById(R.id.text_view_event_detail_ready_location);
                this.locationReadyTextView.setText((event.getLocation()));

                this.noteReadyTextView = (TextView) findViewById(R.id.text_view_event_detail_ready_note);
                this.noteReadyTextView.setText(event.getNote());

                this.durationReadyTextView = (TextView) findViewById(R.id.text_view_event_detail_ready_duration);
                this.durationReadyTextView.setText(event.getDurationInMin().toString());

                this.timeReadyTextView = (TextView) findViewById(R.id.text_view_event_detail_ready_time);
                this.timeReadyTextView.setText(event.getTimestamp().toString());

                this.reminderReadyTextView = (TextView) findViewById(R.id.text_view_event_detail_ready_reminder);
                this.reminderReadyTextView.setText(event.getReminderInMin().toString());

                this.eventRedyGridview = (GridView) findViewById(R.id.gridview_event_detail_ready);
                List<String> memberListInReady = new ArrayList<>();
                for(EventMemberDetail eventMemberDetail : event.getEventMemberDetail()) {
                    memberListInReady.add(eventMemberDetail.getMember().getFirstName());
                }
                ProposeEventAddMemberAdapter adapterInReady = new ProposeEventAddMemberAdapter(this, memberListInReady);
                this.eventRedyGridview.setAdapter(adapterInReady);
                break;
        }
    }

    private void initWidgetableCommon() {
        this.toolbar = (Toolbar) findViewById(R.id.toolbar_event_detail_not_ready_leader);
        //this.toolbar.setTitle(event.getTitle());
    }

    private void initListener(String eventDetailProperty) {

        initListenerCommon();

        switch (eventDetailProperty) {
            case "leaderNotReady":

                this.nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (event.getEventLeaderDetail().getProposedTimestamps().isEmpty()) {
                            proposeEventTimestampsAsLeader();
                        } else {
                            initiateEvent();
                        }
                    }
                });

                this.cancelEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelEvent();
                    }
                });

                break;
            case "memberNotReady":

                this.memberNextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectEventTimestampsAsMember();
                    }
                });

                this.leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        memberLeaveEvent();
                    }
                });
                break;
            default:    // "leaderReady" || "memberReady"

                break;
        }
    }

    private void initListenerCommon() {

    }

    private void selectEventTimestampsAsMember() {

        List<String> selectedEventTimestamps = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for(Date date: memberViewMemberProposedTimestamps) {
            selectedEventTimestamps.add(sdf.format(date));
        }

        appAction.proposeEventTimestampsAsMember(event.getObjectId(), selectedEventTimestamps, new ActionCallbackListener<EventMemberDetail>() {

            final ProgressDialog progressDialog = ProgressDialog.show( EventDetailActivity.this, "", "Sending...", true );
            @Override
            public void onSuccess(EventMemberDetail data) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Propose time success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proposeEventTimestampsAsLeader() {

        List<String> proposedEventTimestamps = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for(Date date: leaderViewLeaderProposedTimestamps) {
            proposedEventTimestamps.add(sdf.format(date));
        }

        appAction.proposeEventTimestampsAsLeader(event.getObjectId(), proposedEventTimestamps, new ActionCallbackListener<EventLeaderDetail>() {

            final ProgressDialog progressDialog = ProgressDialog.show( EventDetailActivity.this, "", "Sending...", true );
            @Override
            public void onSuccess(EventLeaderDetail data) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Propose time success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initiateEvent() {

        super.appAction.initiateEvent(event.getObjectId(), eventTimestamp, new ActionCallbackListener<Integer>() {

            final ProgressDialog progressDialog = ProgressDialog.show( EventDetailActivity.this, "", "Initiating...", true );
            @Override
            public void onSuccess(Integer data) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), "Initiate event success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelEvent() {

        super.appAction.cancelEvent(event.getObjectId(),new ActionCallbackListener<Boolean>() {

            final ProgressDialog progressDialog = ProgressDialog.show( EventDetailActivity.this, "", "Cancelling...", true );
            @Override
            public void onSuccess(Boolean data) {
                progressDialog.cancel();
                Toast.makeText(context, "Event cancelled", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                progressDialog.cancel();
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void memberLeaveEvent() {
        // TODO[later]
    }

    @Override
    public void updateLeaderSelectedDates(List<Date> leaderSelectedDates) {

        if (event.getEventLeaderDetail().getProposedTimestamps().isEmpty()) {
            // Vote time
            this.leaderViewLeaderProposedTimestamps = leaderSelectedDates;
            if (!leaderSelectedDates.isEmpty()) {
                this.nextButton.setEnabled(true);
            } else {
                this.nextButton.setEnabled(false);
            }
        } else {
            // Final time
            if (!leaderSelectedDates.isEmpty() && leaderSelectedDates.size() == 1) {
                this.eventTimestamp = leaderSelectedDates.get(0);
                this.nextButton.setEnabled(true);
            } else {
                this.nextButton.setEnabled(false);
            }
        }

        BaseWeekViewFragment baseWeekViewFragmentLeader = new BaseWeekViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("hostRole", EventDetailActivity.FOR_LEADER);
        bundle.putString("eventTitle", event.getTitle());
        bundle.putInt("ifLeaderProposed", ifLeaderProposed);
        ArrayList<String> leaderViewLeaderProposedEncodedDates = encodeDate(leaderViewLeaderProposedTimestamps);
        bundle.putStringArrayList("leaderViewLeaderProposedEncodedDates", leaderViewLeaderProposedEncodedDates);
        baseWeekViewFragmentLeader.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.event_detail_not_ready_leader_day_calendar_container, baseWeekViewFragmentLeader).commit();
    }

    @Override
    public void updateMemberSelectedDates(List<Date> memberSelectedDates) {

        EventMemberDetail eventMemberDetail = null;
        for(EventMemberDetail detail: event.getEventMemberDetail()) {
            if(detail.getMember().getObjectId().equals(appAction.getHostPerson().getObjectId())) {
                eventMemberDetail = detail;
            }
        }

        if(eventMemberDetail != null) {

            if(eventMemberDetail.getSelectedTimestamps().isEmpty()) {
                this.memberViewMemberProposedTimestamps = memberSelectedDates;
                if (!memberSelectedDates.isEmpty()) {
                    this.memberNextButton.setEnabled(true);
                } else {
                    this.memberNextButton.setEnabled(false);
                }
            }
        }

        BaseWeekViewFragment baseWeekViewFragmentMember = new BaseWeekViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("hostRole", EventDetailActivity.FOR_MEMBER);
        bundle.putString("eventTitle", event.getTitle());
        bundle.putInt("ifLeaderProposed", ifLeaderProposed);

        ArrayList<String> memberViewLeaderProposedEncodedDates = encodeDate(memberViewLeaderProposedTimestamps);
        bundle.putStringArrayList("memberViewLeaderProposedEncodedDates", memberViewLeaderProposedEncodedDates);

        ArrayList<String> memberViewMemberProposedEncodedDates = encodeDate(memberViewMemberProposedTimestamps);
        bundle.putStringArrayList("memberViewMemberProposedEncodedDates", memberViewMemberProposedEncodedDates);

        baseWeekViewFragmentMember.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.event_detail_not_ready_member_day_calendar_container, baseWeekViewFragmentMember).commit();

    }

    private ArrayList<String> encodeDate(List<Date> proposedTimestamps) {
        if (proposedTimestamps != null) {
            ArrayList<String> encodedDates = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(Date date: proposedTimestamps) {
                encodedDates.add(sdf.format(date));
            }

            return encodedDates;

        } else {
            return null;
        }
    }
}
