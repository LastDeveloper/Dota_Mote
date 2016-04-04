package com.example.phnx.dmote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //NonUiTaskFragment fragment;
    AsyncInterface mListener;
    boolean mBound;
    String description;
    private ListView mList;
    private ArrayList<String> arrayList;
    private MyCustomAdapter mAdapter;
    private TCPClient mTcpClient;
    public String Choice, Port;
    private TextView mOutput, mAutofitOutput,ConnectionBox ,IpBox,PortBox,Descript;
    public SharedPreferences settings;
    private Button Accept,Decline,Connect, Disconnect;
    private Boolean IntitalDescription,AnimationRunning , DialogOpen,AddDialogOpen,EditDialogOpen, Edited, Ready, notiReady,createNotiReady,NotiAcce,Ongoing,Vibrate,Sound,Animation,Flip,Start,End,Complete,Complete2,Running,Portrait,PrefAnimation,disctest, Meme, ChoiceReady;
    private String LastChoice,Tag, IpAddr, EditingName, EditingAddress, EditingPort;
    public NotificationManager mNotificationManager;
    public NotificationCompat.Builder mBuilder;
    public int o, Scene;
    public int prev_state, EditingInt;
    public int List_choice;
    private ImageView myAnimation,myAnimationR,myAnimationS,myAnimationE,myAnimationE2,myAnimationE3;
    private AnimationDrawable myAnimationDrawable,myAnimationDrawableR,myAnimationDrawableS,myAnimationDrawableE,myAnimationDrawableE2,myAnimationDrawableE3;
    public PendingIntent resultPendingIntent,resultPendingIntent2;
    private Intent intent;
    long startTime;
    long elapsedTime = 0L;
    private connectService mService;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    public static final String PREFS_NAME = "mypreferences";
    connectService mConnect;
     DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    IpAddressDatabaseHelper db ;
    private String StateArray[] = {"Connected","Looking For Match","Game Found", "Accept Or Decline","Checking...","Looking For Server","Checking If In Game...","In Game","Declined","Failed To Ready Up","Returning To Search...","Disconnect"};
    private ArrayList<String> StateList = new ArrayList<String>(Arrays.asList(StateArray));
    private LinearLayout AcceptDeclineLayout;
    private AlertDialog CreateDialog, AddDialog, EditDialog;

    Handler asyncHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            //What did that async task say?
            switch (msg.what) {
                case 1:
                    //foo();
                    break;
            }
        }
    };
    BroadcastReceiver call_method = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action_name = intent.getAction();
            if (action_name.equals("call_method")) {
                // call your method here and do what ever you want.
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DotaMote ");
        getSupportActionBar().setSubtitle("- Remote Matchmaking");

         db = IpAddressDatabaseHelper.getInstance(this);
        //Boolean
        IntitalDescription=true;
        AnimationRunning=false;
        DialogOpen = false;
        AddDialogOpen = false;
        EditDialogOpen = false;
        Edited = false;


        //Initial get Settings

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        String Info[] =  settings.getString("default_ip_address", "empty").split(":");
        if(Info.length==1){

            Port = settings.getString("Port", "Port not set ");
            IpAddr = settings.getString("IPaddr", "IP not set ");
        }
        else {

            IpAddr=Info[0];
            Port= Info[1];
        }


        Vibrate= settings.getBoolean("Vibrate",false);
        Sound= settings.getBoolean("Sound Alert",false);

        o=2;
        Tag="com.example.phnx.dmote.noti";
       // createNotiReady=false;

        //Initialize Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
     //  ((TextView) findViewById(R.id.main_toolbar_title)).setText("DotaMote - Remote Matchmaking!");
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Portrait=true;
        }
        else {
           Portrait=false;
        }


        //View Intitalization
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Accept = (Button) findViewById(R.id.Accept);
        Decline = (Button) findViewById(R.id.Decline);
        Disconnect = (Button) findViewById(R.id.Disconnect);
        Descript= (TextView) findViewById(R.id.InfoText);
        Connect = (Button) findViewById(R.id.Connect);
        PortBox = (TextView) findViewById(R.id.Porttext);
        AcceptDeclineLayout = (LinearLayout)findViewById(R.id.AcceptDeclineContainer) ;

        //Notification Intents

            //Setup receivers
        registerReceiver(broadcastReceiver, new IntentFilter("MEME"));
        registerReceiver(call_method, new IntentFilter("call_method"));

            //Initialize Intents
        Intent notificationIntent = new Intent("com.example.phnx.dmote.test_intent");
        Intent notification2Intent = new Intent("com.example.phnx.dmote.test2");

            //Put Accept or Decline inside the intents, so when the Notification Action is pressed, it sill Sent Accept
        notificationIntent.putExtra("Test","Accept");
        notification2Intent.putExtra("Test","Decline");

          //Initialize Pending Intents http://developer.android.com/intl/zh-cn/reference/android/app/PendingIntent.html
        resultPendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        resultPendingIntent2 = PendingIntent.getBroadcast(this, 0,
                notification2Intent,
                PendingIntent.FLAG_UPDATE_CURRENT );


        //Log Saved Ip
        Log.e("TCP Client", "MainActivity: LoadedIP: " + IpAddr+" LoadedPort: "+Port);

        if (savedInstanceState!=null)
        {

            AcceptDeclineLayout.setVisibility(savedInstanceState.getInt("buttonv"));
            Connect.setVisibility(savedInstanceState.getInt("buttonc"));
         //   Decline.setVisibility(savedInstanceState.getInt("buttonv"));
            prev_state = savedInstanceState.getInt("prev_state");
            IntitalDescription =savedInstanceState.getBoolean("InitialDescription");
            //Disconnect.setVisibility(savedInstanceState.getInt("buttond"));
           // createNotiReady=savedInstanceState.getBoolean("noti");
           // LastChoice=savedInstanceState.getString("notitext");
           // Running=savedInstanceState.getBoolean("Running");
          //  Scene=savedInstanceState.getInt("Scene", Scene);
            Descript.setText(savedInstanceState.getString("Des"));
            DialogOpen = savedInstanceState.getBoolean("DialogOpen");
            AddDialogOpen = savedInstanceState.getBoolean("AddDialogOpen");
            EditDialogOpen = savedInstanceState.getBoolean("EditDialogOpen");
            EditingName = savedInstanceState.getString("EditingName");
            EditingAddress = savedInstanceState.getString("EditingAddress");
            EditingPort = savedInstanceState.getString("EditingPort");
            Edited= savedInstanceState.getBoolean("Edited");


        }
        else {
            Descript.setText("Not Connected");
        }
        if(DialogOpen){
            createAlertDialogSQLite();
        }
        else if(AddDialogOpen){
            createAddDialogSQLite(EditingInt);
        }
        else if(EditDialogOpen){
            Log.e("WINDOWS" ,"Name: "+ EditingName +" Address:" + EditingAddress + " Port" +EditingPort );
            String EditingIpAddress = EditingAddress+":"+EditingPort;
            IpAddress IpInProgress = new IpAddress(EditingName,EditingIpAddress);
            DialogEditSetupSQLite(IpInProgress,EditingInt );
        }


        //Displaying IP
        String Txt=IpAddr+":"+Port;
        PortBox.setText(Txt);


        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Service", "Clicked "+IpAddr+" "+Port  );
                Intent connectIntent = new Intent(getBaseContext(),connectService.class);
                connectIntent.putExtra("ip_address",IpAddr);
                connectIntent.putExtra("port",Port);
                connectIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);

                startService(connectIntent);
                bindService(connectIntent,mConnection,BIND_AUTO_CREATE);


                }
                /*
                displayMemoryUsage("hi");
                        Log.e("TCP Client", "connect:");
                       // IpAddr = settings.getString("IPaddr", "IP Address not set ");
                       // Port = settings.getString("Port", "Port not set ");

                        fragment.beginTask(IpAddr, Port);
                        */
            }
        );




        Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fragment.endTask();
            }



        });



        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    //Choice = "Accept";
                Intent AcceptIntent = new Intent(getBaseContext(),connectService.class);
                AcceptIntent.setAction(Constants.ACTION.ACCEPT_ACTION);
                startService(AcceptIntent);
               //     fragment.sendMsg("Accept");

            }



        });




        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //    Choice = "Decline";
                  //  fragment.sendMsg("Decline");
                Intent DeclineIntent = new Intent(getBaseContext(),connectService.class);
                DeclineIntent.setAction(Constants.ACTION.DECLINE_ACTION);
                startService(DeclineIntent);

            }
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String val2 = intent.getStringExtra("test2");
            Log.e( "TCPCLIENT ","MainActivity: "+ "NotiActionReceived: "+ val2);
            // internet lost alert dialog method call from here...
            //fragment.sendMsg(val2);
        }
    };



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);//old

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Service","onStart: Bind");
        Intent connectIntent = new Intent(getBaseContext(),connectService.class);
        connectIntent.putExtra("ip_address",IpAddr);
        connectIntent.putExtra("port",Port);
        connectIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        bindService(connectIntent, mConnection,
                Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onStop() {
        super.onStop();
        //Unbound the Service
        if (mBound) {
            Log.e("Service","onStop: Unbound");
            unbindService(mConnection);
            mBound = false;
        }

    }
    @Override
    protected  void onDestroy() {
        super.onDestroy();
        //Unbound the Service
        if(CreateDialog!=null){
            CreateDialog.dismiss();
        }
        if(AddDialog!=null){
            AddDialog.dismiss();
        }
        if(EditDialog!=null){
            EditDialog.dismiss();
        }
        if (mBound) {
            Log.e("Service","onStop: Unbound");
            unbindService(mConnection);
            mBound = false;
        }
           unregisterReceiver(call_method);
        unregisterReceiver(broadcastReceiver);



        // }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("buttonv", AcceptDeclineLayout.getVisibility());
        outState.putInt("buttonc", Connect.getVisibility());
        outState.putString("Des",Descript.getText().toString());
        outState.putInt("prev_state",prev_state);
        outState.putBoolean("InitialDescription",IntitalDescription);
        outState.putBoolean("DialogOpen",DialogOpen);
        outState.putBoolean("AddDialogOpen",AddDialogOpen);
        outState.putBoolean("EditDialogOpen",EditDialogOpen);
        outState.putBoolean("Edited",Edited);


        if(AddDialogOpen || EditDialogOpen || Edited  ){
            outState.putInt("EditingInt",EditingInt);
            outState.putString("EditingName",EditingName);
            outState.putString("EditingAddress",EditingAddress);
            outState.putString("EditingPort",EditingPort);
        }
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    if(id == R.id.nav_ip_address){

        db.deleteAllIpAddress();
        }


        if(id == R.id.nav_port){
            if(mService!=null) {

            }
            createAlertDialogSQLite();
        }
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;


}
    public void getSQLite(){

        List<IpAddress> listIPAddress = db.getAllIpAddress();
        for (int i = 0; i<listIPAddress.size(); i++){
            IpAddress ip = listIPAddress.get(0);
            String ip_name = ip.name;
            Log.e("DATABASE", ip_name);
        }

    }
    public void createAlertDialogSQLite(){

        //Intialize Preferences to dave in default if saved, or even delete.
        DialogOpen= true;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        //Get layout to know which is selected
        final LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        //Setting Dialog Theme
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle(R.string.AlertTitle);

        ArrayList<String> arrayIpAddress = new ArrayList<String>(db.getAllIpAddressNames());
        final List<IpAddress> ipAddressList = db.getAllIpAddress();
        final List<String> ipListString = db.getAllIpAddressNames();
        final int nextAddition = ipAddressList.size() +1;
        //Dialogs need CharSequences, Converted fom List<String> from the database
       CharSequence[] ipArray = ipListString.toArray(new CharSequence[ipListString.size()]);
        final int defaultIpIndex = preferences.getInt("default_ip_address_index", -1);
        Log.e("WINDOWS",Integer.toString(defaultIpIndex));
        builder.setSingleChoiceItems(ipArray,defaultIpIndex , new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which){
                Log.e("Dialog",Integer.toString(which));
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
                setDefaultClick(ipAddressList, which);
            }


        });
        //Add button
        builder.setPositiveButton(R.string.AlertPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Edited= false;
                AddDialogOpen = false;
                createAddDialogSQLite(nextAddition);
                //createAddDialog();





            }
        });
        builder.setNegativeButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ListView lv = ((AlertDialog)dialog).getListView();
                Integer selected = (Integer)lv.getTag();
                if(selected != null) {
                    setDefaultClick(ipAddressList,defaultIpIndex);
                }
                dialog.cancel();


            }
        });
        builder.setNeutralButton(R.string.AlertNeutralButton,null);

        CreateDialog = builder.create();
        CreateDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                CreateDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                CreateDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));

                Button b = CreateDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setTextColor(getResources().getColor(R.color.white));
                if (ipListString.size() == 0) {
                    b.setVisibility(View.GONE);
                }
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        Edited= false;
                        EditDialogOpen = false;
                        ListView lv = ((AlertDialog) CreateDialog).getListView();
                        Integer selected = (Integer) lv.getTag();
                        Log.e("Dialog","default:  "+Integer.toString(defaultIpIndex));
                        if (selected != null ) {
                            CreateDialog.cancel();
                            DialogEditSetupSQLite(ipAddressList.get(selected),selected);
                            //Dismiss once everything is OK.
                            // dialog.dismiss();
                        }
                        else if(defaultIpIndex != -1){
                            CreateDialog.cancel();
                            DialogEditSetupSQLite(ipAddressList.get(defaultIpIndex),defaultIpIndex);
                        }

                    }

                });
            }});
        CreateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("WINDOWS" ,"CReateDialog: OnDismiss" );
                DialogOpen= false;
            }
        });







        CreateDialog.show();

       // IpAddress newIP = new IpAddress("Josiah's House", "192.168.2.72");
    //    db.addIpAddressOnly(newIP);

    }

    public void setDefaultClick(List<IpAddress> ipAddresses , int index){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        String stringAddress = ipAddresses.get(index).address;
        PortBox.setText(stringAddress);
        String splitter[] = stringAddress.split(":");
        IpAddr = splitter[0];
        Port = splitter[1];
        editor.putString("default_ip_address",ipAddresses.get(index).address);
        editor.putInt("default_ip_address_index",index);
        editor.apply();
    }

    public void setDefault(String address){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        PortBox.setText(address);
        String splitter[] = address.split(":");
        IpAddr = splitter[0];
        Port = splitter[1];
        editor.putString("default_ip_address",address);
       // editor.putInt("default_ip_address_index",index);
        editor.apply();
    }

    public void createAlertDialog(){

        final IpAddressPref Address = new IpAddressPref(this, new IpAddressPref.DefaultListener(){

            @Override
            public void onDefaultChange(String message){
               Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                String Info[] = message.substring(2).split(":");
                IpAddr = Info[0];
                Port = Info[1];
                PortBox.setText(message.substring(2));

            }
        });

        final boolean edit_mode = false;
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
         builder.setTitle(R.string.AlertTitle);
        for(int i =0 ;i<Address.getSize();i++){
            Log.e("Dialog","IpArray: " +Integer.toString(i)+":   "+Address.get(i));

        }

        final int prev_default = Address.getDefaultIndex();
        // int current_choice = prev_default;
        builder.setSingleChoiceItems(Address.getDialogSequence(),Address.getDefaultIndex(), new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which){

                Log.e("Dialog",Integer.toString(which));
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
                Address.setDefault(which);
            }


        });
                builder.setPositiveButton(R.string.AlertPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                    createAddDialog(Address);
                    //createAddDialog();





            }
        });
                        builder.setNegativeButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ListView lv = ((AlertDialog)dialog).getListView();
                Integer selected = (Integer)lv.getTag();
                if(selected != null) {
                    Address.setDefault(prev_default);
                }
                dialog.cancel();


            }
        });
        builder.setNeutralButton(R.string.AlertNeutralButton,null);
        /*
        builder.setNeutralButton(R.string.AlertNeutralButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ListView lv = ((AlertDialog)dialog).getListView();
                Integer selected = (Integer)lv.getTag();
                if(selected != null) {
                  //  DialogEditSetup(Address,selected);
                    // do something interesting
                }
                builder.setTitle(R.string.AlertEditTitle);



            }
        });
         */

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));

                Button b = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setTextColor(getResources().getColor(R.color.white));
                if (Address.getCount() == 0) {
                    b.setVisibility(View.GONE);
                }
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        AddDialogOpen= false;
                        ListView lv = ((AlertDialog) dialog).getListView();
                        Integer selected = (Integer) lv.getTag();
                        Log.e("Dialog","prev:  "+Integer.toString(prev_default));
                        if (selected != null ) {
                            dialog.cancel();
                            DialogEditSetup(Address, selected);
                            //Dismiss once everything is OK.
                            // dialog.dismiss();
                        }
                        else if(prev_default != -1){
                            dialog.cancel();
                            DialogEditSetup(Address, prev_default);
                        }

                    }

                });
            }});







        dialog.show();
    }

    public void createAddDialogSQLite(final int nextSize){
        Log.e("WINDOWS","AddDialogOpen: "+ AddDialogOpen + " Edited: "+ Edited);
        if(!AddDialogOpen && !Edited) {
            EditingPort= null;
            EditingAddress = null;
            EditingName = null;
        }
        AddDialogOpen = true;
        Edited= false;
        EditingInt = nextSize;


        //Intialize Preferences to dave in default if saved, or even delete.
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        //Setting Dialog Theme
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle("Add IP Address");
        //Get layout to know which is selected
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        builder.setView(dialogView).setNegativeButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                AddDialogOpen = false;
                createAlertDialogSQLite();
               // dialog.cancel();
            }
        });
      builder.setPositiveButton(R.string.AlertPositiveButton,new DialogInterface.OnClickListener(){
          @Override
          public void onClick(DialogInterface dialog, int id){

          }
      });
        AddDialog = builder.create();
        AddDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          AddDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                                          AddDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                                          Button add = AddDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                          FormEditText AddName = (FormEditText) dialogView.findViewById(R.id.addIpName);
                                          FormEditText AddIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                                          FormEditText AddPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);
                                          AddName.setText(EditingName);
                                          AddIp.setText(EditingAddress);
                                          AddPort.setText(EditingPort);
                                          AddName.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                  Edited= true;
                                                  EditingName= s.toString();
                                                  Log.e("WINDOWS","onTextChange: "+ s.toString() + " Edited: "+ Edited);
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {

                                              }
                                          });
                                          AddIp.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                  Edited= true;
                                                  EditingAddress= s.toString();
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {

                                              }
                                          });
                                          AddPort.addTextChangedListener(new TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                  Edited= true;
                                                  EditingPort= s.toString();
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {

                                              }
                                          });
                                          add.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View view) {
                                                  // TODO Do something
                                                  FormEditText AddName = (FormEditText) dialogView.findViewById(R.id.addIpName);
                                                  FormEditText AddIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                                                  FormEditText AddPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);



                                                  if (AddName.testValidity() && AddIp.testValidity() && AddPort.testValidity() ) {
                                                      String bufferName = AddName.getText().toString();
                                                      String bufferIp = AddIp.getText().toString();
                                                      String bufferPort= AddPort.getText().toString();
                                                      String buffer = bufferIp+":"+bufferPort;
                                                      IpAddress newIp = new IpAddress(bufferName,buffer);
                                                      db.addIpAddressOnly(newIp);
                                                      setDefault(buffer);
                                                      editor.putInt("default_ip_address_index", nextSize-1);
                                                      editor.apply();
                                                      AddDialog.cancel();
                                                      createAlertDialogSQLite();
                                                      AddDialogOpen = false;
                                                  }

                                              }

                                          });
                                      }
                                  });

        AddDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("WINDOWS" ,"AddDialog: OnDismiss: AddDialogOpen: " + AddDialogOpen);
               // AddDialogOpen= false;
            }
        });
        AddDialog.show();


    }

    public void DialogEditSetupSQLite(final IpAddress ipAddress, final int selected){
        if(!EditDialogOpen  || !Edited) {
            EditingName = ipAddress.name;
            String[] EditingBuffer = ipAddress.address.split(":");
            EditingAddress = EditingBuffer[0];
            EditingPort = EditingBuffer[1];
        }
       Edited= false;
        EditingInt = selected;
        EditDialogOpen = true;

        //Intialize Preferences to dave in default if saved, or even delete.
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        //Setting Dialog Theme
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle("Edit IP Address");
        //Get layout to know which is selected
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.AlertEditPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setNegativeButton(R.string.AlertEditNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
               db.deleteIpAddress(ipAddress);
                editor.putString("default_ip_address","Enter Ip Address");
                editor.putInt("default_ip_address_index", -1);
                PortBox.setText("Enter Ip Address");
                editor.apply();
                dialog.cancel();
                EditDialogOpen= false;
                createAlertDialogSQLite();

            }
        });
        builder.setNeutralButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
                createAlertDialogSQLite();
            }

        });
        EditDialog = builder.create();
        EditDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                final FormEditText EditName = (FormEditText) dialogView.findViewById(R.id.addIpName);
                final FormEditText EditIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                final FormEditText EditPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);

                EditIp.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        Edited= true;
                        EditingAddress= s.toString();
                        Log.e("WINDOWS","onTextChanged"+ "Char s "  + s.toString()+"EditAddress"+ EditingAddress  );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        Edited= true;
                        EditingAddress= s.toString();
                        Log.e("WINDOWS","onTextChanged"+ "Char s "  + s.toString()+"EditAddress"+ EditingAddress  );
                    }
                });

                EditName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Edited= true;
                        EditingName= s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                EditPort.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Edited= true;
                        EditingPort= s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                //Getting the name for the IpAddress object
                EditName.setText(EditingName);

                //Getting the IpAddress for the IpAddress object
                EditIp.setText(EditingAddress);
                EditPort.setText(EditingPort);

                EditDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));

                Button ok = EditDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setTextColor(getResources().getColor(R.color.white));
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something


                        //Test Validity using the attributes set in the .xml
                        if (EditName.testValidity() && EditIp.testValidity() && EditPort.testValidity() ) {
                            String bufferName = EditName.getText().toString();
                            String buffer1 = EditIp.getText().toString();
                            String buffer2 = EditPort.getText().toString();

                            // Combining the Ip and Port for the full address
                            String bufferAddress = buffer1+":"+buffer2;
                            ipAddress.name = bufferName;
                            ipAddress.address = bufferAddress;
                            //Update Database
                            db.updateEntireIpAddress(ipAddress);
                            //Place in Defaults
                            setDefault(bufferAddress);
                            EditDialogOpen= false;
                            editor.putInt("default_ip_address_index", selected);
                            editor.apply();
                            EditDialog.cancel();
                            createAlertDialogSQLite();

                        }

                    }

                });
                EditDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.white));

            }
        });
        EditDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                //EditDialogOpen= false;
            }
        });
        EditDialog.show();


    }



    public void createAddDialog(final IpAddressPref Address){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle("Add IP Address");
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        builder.setView(dialogView).setNegativeButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                createAlertDialog();
                // dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.AlertPositiveButton,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                Button add = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                add.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        FormEditText AddIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                        FormEditText AddPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);
                        if (AddIp.testValidity() && AddPort.testValidity() ) {

                            String buffer1 = AddIp.getText().toString();
                            String buffer2 = AddPort.getText().toString();
                            Address.add(buffer1, buffer2);
                            dialog.cancel();
                            createAlertDialog();

                        }

                    }

                });
            }
        });


        dialog.show();


    }




    public void DialogEditSetup(final IpAddressPref Address, final int index){
        Log.e("Dialog","EditSetup: "+Integer.toString(index));
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle("Edit IP Address");
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.AlertEditPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        builder.setNegativeButton(R.string.AlertEditNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Address.remove(index);
                dialog.cancel();
                createAlertDialog();

            }
        });
        builder.setNeutralButton(R.string.AlertNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
                createAlertDialog();
            }

            });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                FormEditText EditIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                FormEditText EditPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);
                    EditIp.setText(Address.getIp(index));
                    EditPort.setText(Address.getPort(index));

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));

                Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                       ok.setTextColor(getResources().getColor(R.color.white));
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        FormEditText EditIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                        FormEditText EditPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);
                        if (EditIp.testValidity() && EditPort.testValidity() ) {

                            String buffer1 = EditIp.getText().toString();
                            String buffer2 = EditPort.getText().toString();
                            Address.save(buffer1+":"+buffer2,index);
                            Address.setDefault(index);
                            dialog.cancel();
                            createAlertDialog();

                        }

                    }

                });
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.white));

            }
        });
        dialog.show();


    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            connectService.LocalBinder binder = (connectService.LocalBinder) service;
            mService = binder.getService();
            binder.setListener(new connectService.MainListener() {
                @Override
                public void getServiceMessage(final String message) {
                    Log.e("Service MAIN","MAIN:"+ message);
                    description=message;


                     MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(description.length()>2){
                                 mainUI(description.substring(1));
                            }
                            else
                            {
                                mainUI(description);
                            }
                        }
                    });


                   //Descript.setText(message.substring(1));

                }
            });

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;

        }
    };

    private int getIndexInt(String state){
        //   Log.e(" Notification STATE", "STATE CHECK: "+state+ "  Sub: "+state.substring(1) );



        if(state.length()>2) {
            //       Log.e(" Notification STATE", "    "+Integer.toString(StateList.indexOf(state)));
            return StateList.indexOf(state);
        }
        return -1;
    }


    public void mainUI(String message){
        int state = getIndexInt(message);
        if(IntitalDescription ){
            Log.e("MAIN_ACTIVITY", "State: "+message);
            Log.e("MAIN_ACTIVITY", "     Case: "+state);
                    Connect.setVisibility(View.INVISIBLE);
           // Log.e("MAIN_ACTIVITY", "     Animation Started");
                    startAnimation();
                switch (state){
                    case 0:case 1:case 2:case 4:case 5: case 6:case 7:case 8:case 9:case 10:
                    {
                        Descript.setText(message);
                        IntitalDescription=false;
                       // startAnimation();
                        break;
                    }
                    case 3:
                    {
                        Log.e("MAIN_ACTIVITY", "     AcceptDECLINE ON");
                        AcceptDeclineLayout.setVisibility(View.VISIBLE);
                        Descript.setText(message);
                        IntitalDescription=false;
                        break;
                    }
                    case -1:
                    {
                        if(message.contains("Accept Or Decline")){
                            Log.e("MAIN_ACTIVITY", "     Animation Started");
                            AcceptDeclineLayout.setVisibility(View.VISIBLE);
                            Descript.setText(message);
                            IntitalDescription=false;
                            break;
                        }
                    }
                    /*
                    case 11:{
                        if(AnimationRunning){
                            stopAnimation();
                        }
                        if(prev_state==8 || prev_state==7 || prev_state==9 ){

                        }
                        else {
                            Descript.setText(message);
                        }
                        IntitalDescription=false;

                    }*/
                }
        }
        else {
            Log.e("MAIN_ACTIVITY", "Update State: "+message);
            Log.e("MAIN_ACTIVITY", "     Case: "+state);
            switch (state){
                case 1:
                    if(AcceptDeclineLayout.getVisibility()==View.VISIBLE){
                        AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                    }
                case 0:case 2:case 5: case 6:case 7:case 10:
                {

                    Descript.setText(message);
                    prev_state=state;
                    break;
                }
                case 3:
                    Descript.setText(message);
                    AcceptDeclineLayout.setVisibility(View.VISIBLE);
                    prev_state=state;
                    break;
                case 4:case 8:case 9:
                {
                    Descript.setText(message);
                    AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                    prev_state=state;
                    break;
                }
                case 11:
                    if(AcceptDeclineLayout.getVisibility()==View.VISIBLE){
                        AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                    }
                    Connect.setVisibility(View.VISIBLE);
                    IntitalDescription=true;
                    if(AnimationRunning){
                        stopAnimation();
                    }
                    if(prev_state==8 || prev_state==7 || prev_state==9 ){

                    }
                    else {
                        Descript.setText(message);
                        break;
                    }
                case -1:
                {
                    if(message.contains("Accept Or Decline")){

                        AcceptDeclineLayout.setVisibility(View.VISIBLE);
                        Descript.setText(message);
                        prev_state=state;
                        break;
                    }
                }
            }
        }
    }

    public void startAnimation(){
        Log.e("MAIN_ACTIVITY", "     Animation Started");
        AnimationRunning = true;

    }
    public void stopAnimation(){
        Log.e("MAIN_ACTIVITY", "     Animation Stopped");
        AnimationRunning = true;

    }

}