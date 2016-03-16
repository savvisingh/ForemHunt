package com.example.sarabjeetsingh.beacondetector.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.sarabjeetsingh.beacondetector.R;
import com.example.sarabjeetsingh.beacondetector.adapters.MembersAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class GroupRegistration extends AppCompatActivity implements View.OnClickListener{

    List<ParseUser> listMembers, listInvites;
    RecyclerView recyclerViewMembers, recyclerViewInvite;
    LinearLayout invitesLoaderLayout, invitesNullCaseLayout;
    CheckBox panelToggle;
    MembersAdapter invitesAdapter, membersAdapter;
    private SlidingUpPanelLayout mLayout;

    private LinearLayout membersLoaderLayout, membersNullCaseLayout, membersNoItemLayout;
    private ParseUser user;

    EditText groupName;
    LinearLayout createGroupLayout, editGroupLayout;
    private Button createGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = ParseUser.getCurrentUser();

        createGroupLayout = (LinearLayout) findViewById(R.id.create_group_layout);
        editGroupLayout  = (LinearLayout) findViewById(R.id.edit_group_layout);
        createGroup = (Button) findViewById(R.id.button_create_group);
        groupName = (EditText) findViewById(R.id.group_name);
        membersLoaderLayout = (LinearLayout) findViewById(R.id.loader_layout);
        membersNoItemLayout =(LinearLayout) findViewById(R.id.noitemLayout);
        membersNullCaseLayout = (LinearLayout) findViewById(R.id.Null_case_layout);
        mLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);



        groupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               String str = groupName.getText().toString();
                if(!str.isEmpty()){
                   ParseObject group = user.getParseObject("group");
                    group.put("groupName", str);
                    group.saveInBackground();
                }
            }
        });


        createGroup.setOnClickListener(this);
        ParseUser user = ParseUser.getCurrentUser();


        listInvites = new ArrayList<>();
        listMembers = new ArrayList<>();

        recyclerViewInvite = (RecyclerView) findViewById(R.id.recyclerViewInvite);
        recyclerViewMembers = (RecyclerView) findViewById(R.id.recyclerViewMembers);

        invitesNullCaseLayout = (LinearLayout) findViewById(R.id.Null_case_layout_invites);
        invitesLoaderLayout = (LinearLayout) findViewById(R.id.loader_layout_invites);

        // 2. set layoutManger
        recyclerViewInvite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        // 3. craete adapter
        invitesAdapter = new MembersAdapter(listInvites, this, 1);
        membersAdapter = new MembersAdapter(listMembers, this, 2);
        // 4. set adapter
        recyclerViewInvite.setAdapter(invitesAdapter);
        recyclerViewMembers.setAdapter(membersAdapter);
        // 5. set item animator to DefaultAnimator
        recyclerViewInvite.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMembers.setItemAnimator(new DefaultItemAnimator());

        panelToggle = (CheckBox) findViewById(R.id.action_panel);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("Group Registration", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("Group Registration", "onPanelStateChanged " + newState);

            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mLayout.setAnchorPoint(0.7f);

        findViewById(R.id.button_invite_friends).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });


        if(user.getParseObject("group") != null){
            editGroupLayout.setVisibility(View.VISIBLE);
            createGroupLayout.setVisibility(View.GONE);
            ParseObject group =user.getParseObject("group");
            group.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(object.get("groupName") != null) {
                        groupName.setText(object.get("groupName").toString());
                    }
                }
            });

            mLayout.setVisibility(View.VISIBLE);
            loadGroupMembers();
            loadavailableMembers();

        }else {
            mLayout.setVisibility(View.GONE);
        }


        invitesAdapter.SetOnItemClickListener(new MembersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ParseObject group = ParseUser.getCurrentUser().getParseObject("group");
                ParseRelation<ParseUser> groupMembers = group.getRelation("invites");
                groupMembers.add(listInvites.get(position));
                group.saveInBackground();

                listInvites.remove(position);
                invitesAdapter.notifyItemRemoved(position);

            }
        });
    }

    public void loadGroupMembers(){

        membersLoaderLayout.setVisibility(View.VISIBLE);
        membersNoItemLayout.setVisibility(View.GONE);
        membersNullCaseLayout.setVisibility(View.GONE);
        recyclerViewMembers.setVisibility(View.GONE);

        ParseObject group = user.getParseObject("group");

        ParseRelation<ParseUser> groupMembers = group.getRelation("members");
        ParseRelation<ParseUser> groupInvites = group.getRelation("invites");

        Log.d("GroupRegitration", "Loading Members");
        groupMembers.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                membersLoaderLayout.setVisibility(View.GONE);
                if(e!=null){
                    membersNullCaseLayout.setVisibility(View.VISIBLE);
                }else if(objects != null){
                    if(objects.size() > 0) {
                        recyclerViewMembers.setVisibility(View.VISIBLE);
                        listMembers.clear();
                        listMembers.addAll(objects);
                        membersAdapter.notifyDataSetChanged();
                    }else membersNoItemLayout.setVisibility(View.VISIBLE);
                }else{
                    membersNoItemLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void loadavailableMembers(){
        invitesLoaderLayout.setVisibility(View.VISIBLE);
        recyclerViewInvite.setVisibility(View.GONE);
        invitesNullCaseLayout.setVisibility(View.GONE);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("email", ParseUser.getCurrentUser().getEmail());
        query.whereEqualTo("available", "1");
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                invitesLoaderLayout.setVisibility(View.GONE);
                if(e!=null){
                    invitesNullCaseLayout.setVisibility(View.VISIBLE);
                }else if(objects != null){
                    recyclerViewInvite.setVisibility(View.VISIBLE);
                    listInvites.clear();
                    listInvites.addAll(objects);
                    invitesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        editGroupLayout.setVisibility(View.VISIBLE);
        createGroupLayout.setVisibility(View.GONE);

        mLayout.setVisibility(View.VISIBLE);

        ParseObject group = new ParseObject("Group");
        user.put("group", group);
        user.saveInBackground();

        groupName.requestFocus();
        loadavailableMembers();
        loadGroupMembers();
    }

    @Override
    public void onBackPressed() {
        if(mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if(item.getItemId() == R.id.action_reload){
            loadGroupMembers();
            loadavailableMembers();
        }
        return true;
    }
}
