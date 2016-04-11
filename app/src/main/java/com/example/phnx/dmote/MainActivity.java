package com.example.phnx.dmote;

import android.support.v7.app.AppCompatActivity;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;
import com.getkeepsafe.android.multistateanimation.MultiStateAnimation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MultiStateAnimation.AnimationSeriesListener {
    boolean mBound;
    String description;
    public String Port;
    private TextView PortBox,Descript;
    public SharedPreferences settings;
    public SharedPreferences.Editor editor;
    private Button Accept,Decline,Connect, Disconnect;
    private Boolean IntitalDescription,AnimationRunning , DialogOpen,AddDialogOpen,EditDialogOpen, AddDialogInterrupted,EditDialogInterrupted, Edited, Portrait, Vibrate,Sound;
    private String Tag, IpAddr, EditingName, EditingAddress, EditingPort;
    public int prev_state, EditingInt;
    private connectService mService;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    IpAddressDatabaseHelper db ;
    private MultiStateAnimation mAnimation1;
    private String StateArray[] = {"Connected","Looking For Match","Game Found", "Accept Or Decline","Checking...","Looking For Server","Checking If In Game...","In Game","Declined","Failed To Ready Up","Returning To Search...","Disconnect"};
    private ArrayList<String> StateList = new ArrayList<String>(Arrays.asList(StateArray));
    private LinearLayout AcceptDeclineLayout;
    private AlertDialog CreateDialog, AddDialog, EditDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Set Layout and Toolbar */
        setContentView(R.layout.drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DotaMote ");
        getSupportActionBar().setSubtitle("- Remote Matchmaking");

        /** Get the instance of the SQL database **/
         db = IpAddressDatabaseHelper.getInstance(this);

        /** Boolean Values
         * Intitial Description is used for the NotificationUpdates
         * AnimationRunning used for restarting Animation on lifecycle changes
         * */

        IntitalDescription=true;
        AnimationRunning=false;

        /**Boolean values that help wih the reconstruction of Dialogs */

        AddDialogInterrupted = false;
        EditDialogInterrupted = false;
        DialogOpen = false;
        AddDialogOpen = false;
        EditDialogOpen = false;



        /**Initialize SharedPreferences and  and SharedPreferences.Editor */
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        editor = settings.edit();

        /**Get the saved Settings; The Default IP Address, and the Vibrate/Sound Settings. */

        String Info[] =  settings.getString("default_ip_address", "empty").split(":");
        Vibrate= settings.getBoolean("Vibrate",false);
        Sound= settings.getBoolean("Sound Alert",false);


        /**Initialize Drawer  and ToolbarNavigationButton  */

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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



        /**View Initialization*/

            //Unused NavigationView
               // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
               // navigationView.setNavigationItemSelectedListener(this);
        //Buttons
        Accept = (Button) findViewById(R.id.Accept);
        Decline = (Button) findViewById(R.id.Decline);
        Disconnect = (Button) findViewById(R.id.Disconnect);
        Connect = (Button) findViewById(R.id.Connect);
        AcceptDeclineLayout = (LinearLayout)findViewById(R.id.AcceptDeclineContainer) ;

        //Description and IP Address Text
        Descript= (TextView) findViewById(R.id.InfoText);
        PortBox = (TextView) findViewById(R.id.Porttext);

        //Navigation Drawer, Contains LinearLayout
        TextView EnterIpAddress = (TextView) findViewById(R.id.nav_ip_text) ;
        SwitchCompat SoundSwitch = (SwitchCompat) findViewById(R.id.sound_switch);
        SwitchCompat VibrateSwitch = (SwitchCompat) findViewById(R.id.vibrate_swtich);


        /**Background Animation */
        ImageView animationView1 = (ImageView) findViewById(R.id.animationView);

        //Use correct animation for the corresponding orientation. The animation is created by the library MultiStateAnimation
        // https://github.com/KeepSafe/MultiStateAnimation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mAnimation1 = makeAnimationPortrait(animationView1);
        }
        else {
            mAnimation1 = makeAnimationLandscape(animationView1);
        }

        mAnimation1.setSeriesAnimationFinishedListener(this);

        /**UNUSED*/
        //Notification Intents

            //Setup receivers
        /*
        registerReceiver(broadcastReceiver, new IntentFilter("MEME"));
        registerReceiver(call_method, new IntentFilter("call_method"));

            //Initialize Intents
        Intent notificationIntent = new Intent("com.example.phnx.dmote.test_intent");
        Intent notification2Intent = new Intent("com.example.phnx.dmote.test2");

        //Put Accept or Decline inside the intents, so when the Notification Action is pressed, it will send Accept
        notificationIntent.putExtra("Test","Accept");
        notification2Intent.putExtra("Test","Decline");

          //Initialize Pending Intents http://developer.android.com/intl/zh-cn/reference/android/app/PendingIntent.html
        resultPendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT );
        resultPendingIntent2 = PendingIntent.getBroadcast(this, 0,
                notification2Intent,
                PendingIntent.FLAG_UPDATE_CURRENT );

        */

        //Log Saved Ip
        Log.e("MAIN", "MainActivity: LoadedIP: " + IpAddr+" LoadedPort: "+Port);

        /** Receive the visibility of the views for orientation change, and previous state for the Disconnect update state  */
        if (savedInstanceState!=null)
        {
            AcceptDeclineLayout.setVisibility(savedInstanceState.getInt("buttonv"));
            Connect.setVisibility(savedInstanceState.getInt("buttonc"));
           // Decline.setVisibility(savedInstanceState.getInt("buttond"));
            prev_state = savedInstanceState.getInt("prev_state");
        //    IntitalDescription =savedInstanceState.getBoolean("InitialDescription");
            Descript.setText(savedInstanceState.getString("Des"));
            if(savedInstanceState.getBoolean("AnimationRunning",AnimationRunning)){
                startAnimation();
            }

        }
        else {
            Descript.setText("Not Connected");
        }

        /** Display the IP Address, retrieved from SharedPreferences earlier
         * The default IP address is saved in a single string. Must be split by a ':' to be used
         * */
        if(Info.length==1){
            PortBox.setText("Enter IP Address");
        }
        else {

            IpAddr=Info[0];
            Port= Info[1];
            String Txt=IpAddr+":"+Port;
            PortBox.setText(Txt);
        }

        /** Set the State of Switches, retrieved from SharePreferences*/

        SoundSwitch.setChecked(settings.getBoolean("Sound Alert", false));
        VibrateSwitch.setChecked(settings.getBoolean("Vibrate", false));

        /** OnClicks of the Buttons inaisw rhw Navigation Drawer*/

        EnterIpAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
            createAlertDialogSQLite();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }

        });

        VibrateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Commit SharedPreferences on change
                editor.putBoolean("Vibrate", isChecked);
                editor.apply();

            }
        });
        SoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //Commit SharedPreferences on change
                editor.putBoolean("Sound Alert", isChecked);
                editor.apply();

            }
        });

        /** Onclicks of the Buttons*/
        Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Attempt to start the service, or reconnect
                Log.e("Service", "Clicked "+IpAddr+" "+Port  );
                Intent connectIntent = new Intent(getBaseContext(),connectService.class);
                connectIntent.putExtra("ip_address",IpAddr);
                connectIntent.putExtra("port",Port);
                connectIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(connectIntent);
                bindService(connectIntent,mConnection,BIND_AUTO_CREATE);

                }
            }
        );

        Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Attempt to send the 'Disconnect' command to the service
                Intent DisconnectIntent = new Intent(getBaseContext(),connectService.class);
                DisconnectIntent.setAction(Constants.ACTION.DISCONNECT_ACTION);
                startService(DisconnectIntent);
            }
        });

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Attempt to send the 'Accept Match' command to the service
                Intent AcceptIntent = new Intent(getBaseContext(),connectService.class);
                AcceptIntent.setAction(Constants.ACTION.ACCEPT_ACTION);
                startService(AcceptIntent);
            }
        });

        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Attempt to send the 'Decline Match' command to the service
                Intent DeclineIntent = new Intent(getBaseContext(),connectService.class);
                DeclineIntent.setAction(Constants.ACTION.DECLINE_ACTION);
                startService(DeclineIntent);

            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Main","onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        /**Attempt to rebeind to a running Service on Start*/

        Log.e("Main","onStart: Bind");
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
        Log.e("Main","onStop");

        /**Unbound the Service*/

        if (mBound) {
            Log.e("Service","onStop: Unbound");
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
        /** Dismiss the Dialogs if open, to prevent memory leaks */

        Log.e("Main","onDestroy");
        //Unbound the Service
        if(CreateDialog!=null){
            CreateDialog.dismiss();
        }
        if(AddDialog!=null){
            AddDialogInterrupted = true;
            AddDialog.dismiss();
        }
        if(EditDialog!=null){
            EditDialog.dismiss();
        }
        /** If Service is Bounded, unbind the Service, so rebinding the Service is possible, if the connection is still running */
        if (mBound) {
            Log.e("Service","onStop: Unbound");
            unbindService(mConnection);
            mBound = false;
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("WINDOWS", "onRestoreInstanceState");
        /** Restore Dialog States, to recreate Dialog if still open */

        DialogOpen = savedInstanceState.getBoolean("DialogOpen");
        AddDialogOpen = savedInstanceState.getBoolean("AddDialogOpen");
        EditDialogOpen = savedInstanceState.getBoolean("EditDialogOpen");
        DialogOpen = savedInstanceState.getBoolean("DialogOpen");
        AddDialogOpen = savedInstanceState.getBoolean("AddDialogOpen");
        EditDialogOpen = savedInstanceState.getBoolean("EditDialogOpen");

        /** Retreive the text inside the Dialog's currently open EditTexts, use the text to resume editing on reconstruction of Activity */
        EditingName = savedInstanceState.getString("EditingName");
        EditingAddress = savedInstanceState.getString("EditingAddress");
        EditingPort = savedInstanceState.getString("EditingPort");
         List<IpAddress> ipAddressList = db.getAllIpAddress();

        if(DialogOpen){
            createAlertDialogSQLite();
        }
        else if(AddDialogOpen){
            AddDialogInterrupted= true;
            createAddDialogSQLite(EditingInt);
        }
        else if(EditDialogOpen){
            EditDialogInterrupted= true;
            Log.e("WINDOWS" ,"Name: "+ EditingName +" Address:" + EditingAddress + " Port" +EditingPort );
            String EditingIpAddress = EditingAddress+":"+EditingPort;
            IpAddress IpInProgress = new IpAddress(EditingName,EditingIpAddress);
            DialogEditSetupSQLite(ipAddressList.get(EditingInt),EditingInt );
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("WINDOWS", "onSavedInstanceState");

        /** Reset Visibility of Views on Lifecycle changes */
        outState.putInt("buttonv", AcceptDeclineLayout.getVisibility());
        outState.putInt("buttonc", Connect.getVisibility());
        outState.putInt("buttond", Disconnect.getVisibility());


        /** Saved and Reset Description on Lifecycle changes */
        outState.putString("Des",Descript.getText().toString());
        outState.putInt("prev_state",prev_state);

     //   outState.putBoolean("InitialDescription",IntitalDescription);

        /** Save state (Open/Closed) of Dialog, to recreate if still open */
        outState.putBoolean("AnimationRunning",AnimationRunning);
        outState.putBoolean("DialogOpen",DialogOpen);
        outState.putBoolean("AddDialogOpen",AddDialogOpen);
        outState.putBoolean("EditDialogOpen",EditDialogOpen);
//        outState.putBoolean("Edited",Edited);

        /** Save the current text inside the EditText */
        //outState.putBoolean("AddDialogInterrupted",AddDialogInterrupted);
            outState.putInt("EditingInt",EditingInt);
            outState.putString("EditingName",EditingName);
            outState.putString("EditingAddress",EditingAddress);
            outState.putString("EditingPort",EditingPort);

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


    public void createAlertDialogSQLite(){


        DialogOpen= true;
        /**Initialize Preferences to dave in default if saved, or even delete.*/
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        /**Get layout to know which is selected */
        final LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_dialog_content, null);

        /**Setting Dialog Theme*/
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle(R.string.AlertTitle);

        ArrayList<String> arrayIpAddress = new ArrayList<String>(db.getAllIpAddressNames());
        final List<IpAddress> ipAddressList = db.getAllIpAddress();
        final List<String> ipListString = db.getAllIpAddressNames();
        final int nextAddition = ipAddressList.size() +1;

        /**Dialogs need CharSequences, Converted fom List<String> from the database */
        CharSequence[] ipArray = ipListString.toArray(new CharSequence[ipListString.size()]);
        final int defaultIpIndex = preferences.getInt("default_ip_address_index", -1);


        builder.setSingleChoiceItems(ipArray,defaultIpIndex , new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which){
                Log.e("Dialog",Integer.toString(which));
                ListView lv = ((AlertDialog)dialog).getListView();
                lv.setTag(new Integer(which));
                setDefaultClick(ipAddressList, which);
            }
        });


        /**Add button */
        builder.setPositiveButton(R.string.AlertPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
              //  Edited= false;
                AddDialogOpen = false;
                createAddDialogSQLite(nextAddition);
                //createAddDialog();
            }
        });

        /**Cancel Button*/
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

        /**Edit Button , Assigns Title,  Empty because button OnClick id done in OnShow */
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

        /** Set boolean to false, to indicate this dialog is closed */
        CreateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e("WINDOWS" ,"CreateDialog: OnDismiss" );
                DialogOpen= false;
            }
        });
        CreateDialog.show();
    }

    public void createAddDialogSQLite(final int nextSize){

        /**If the AddDialog wasn't interrupted */
        if(!AddDialogInterrupted) {
            EditingPort= null;
            EditingAddress = null;
            EditingName = null;
        }
        AddDialogOpen = true;
        AddDialogInterrupted = false;
        EditingInt = nextSize;


        /**Intialize Preferences to dave in default if saved, or even delete. */
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        /**Setting Dialog Theme*/
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));
        builder.setTitle("Add IP Address");

        /**Get layout to know which is selected*/
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
                                          /**Change Buttons color */
                                          AddDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
                                          AddDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white));
                                          Button add = AddDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                                          /**Initialize the custom EditTexts */
                                          FormEditText AddName = (FormEditText) dialogView.findViewById(R.id.addIpName);
                                          FormEditText AddIp = (FormEditText) dialogView.findViewById(R.id.addIpEdit);
                                          FormEditText AddPort = (FormEditText) dialogView.findViewById(R.id.addPortEdit);

                                          /**If Interrupted, set the EditTexts with the saved values **/
                                          AddName.setText(EditingName);
                                          AddIp.setText(EditingAddress);
                                          AddPort.setText(EditingPort);

                                          /** These listeners saved the current text inside the Dialog */
                                          AddName.addTextChangedListener(new TextWatcher() {

                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                  EditingName= s.toString();
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
                                              //    Edited= true;
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
                                             //     Edited= true;
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
                AddDialogOpen= false;
            }
        });
        AddDialog.show();


    }

    public void DialogEditSetupSQLite(final IpAddress ipAddress, final int selected){
        if(!EditDialogInterrupted) {
            EditingName = ipAddress.name;
            String[] EditingBuffer = ipAddress.address.split(":");
            EditingAddress = EditingBuffer[0];
            EditingPort = EditingBuffer[1];
        }
        //  Edited= false;
        EditDialogInterrupted = false;
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
                PortBox.setText("Enter IP Address");
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

                       // Edited= true;
                        EditingAddress= s.toString();
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
                      //  Edited= true;
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
                       // Edited= true;
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

                EditDialogOpen= false;
            }
        });
        EditDialog.show();
        
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
                    if(message.length()>2){
                        description=message.substring(1);
                    }
                    else {
                        description=message;
                    }

                    
                     MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(description.length()>2){
                                 mainUI(description);
                            }
                            else
                            {
                                mainUI(description);
                            }
                        }
                    });
                    
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
             if(state.length()>2) {
            return StateList.indexOf(state);
        }
        return -1;
    }
    
    public void mainUI(String message){
            int state = getIndexInt(message);
            if (IntitalDescription) {
                Log.e("MAIN_ACTIVITY", "State: " + message);
                Log.e("MAIN_ACTIVITY", "     Case: " + state);
                Connect.setVisibility(View.INVISIBLE);
                // Log.e("MAIN_ACTIVITY", "     Animation Started");
                switch (state) {
                    case 0:
                    case 1:
                    case 2:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10: {
                        if(state>0){
                            startAnimationRunning();

                        }
                        else
                        {
                            startAnimation();
                        }
                        if (Disconnect.getVisibility() == View.INVISIBLE) {
                            Disconnect.setVisibility(View.VISIBLE);
                        }
                        Descript.setText(message);
                        IntitalDescription = false;
                        // startAnimation();

                        break;
                    }
                    case 3: {
                        startAnimation();
                        Log.e("MAIN_ACTIVITY", "     AcceptDECLINE ON");
                        if (Disconnect.getVisibility() == View.VISIBLE) {
                            Disconnect.setVisibility(View.INVISIBLE);
                        }
                        AcceptDeclineLayout.setVisibility(View.VISIBLE);
                        Descript.setText(message);
                        IntitalDescription = false;
                        stopAnimation();
                        break;
                    }
                    case -1: {
                        if (message.contains("Accept Or Decline")) {
                            if(state!=0){
                                startAnimationRunning();

                            }
                            else
                            {
                                startAnimation();
                            }
                            if (Disconnect.getVisibility() == View.VISIBLE) {
                                Disconnect.setVisibility(View.INVISIBLE);
                            }

                            Log.e("MAIN_ACTIVITY", "     Animation Started");
                            AcceptDeclineLayout.setVisibility(View.VISIBLE);
                            Descript.setText(message);
                            IntitalDescription = false;
                            }
                            break;
                        }



                    case 11:
                        Connect.setVisibility(View.VISIBLE);
                        if(AnimationRunning){
                            stopAnimation();
                        }
                        if(prev_state==8 || prev_state==7 || prev_state==9 ){

                        }
                        else {
                            Descript.setText(message);
                        }


                      //  IntitalDescription=false;
                        break;


                }
            } else {
                Log.e("MAIN_ACTIVITY", "Update State: " + message);
                Log.e("MAIN_ACTIVITY", "     Case: " + state);
                switch (state) {
                    case 1:
                        if (AcceptDeclineLayout.getVisibility() == View.VISIBLE) {
                            AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                            Disconnect.setVisibility(View.VISIBLE);
                        }

                    case 0:
                    case 2:
                    case 5:
                    case 6:
                    case 7:
                    case 10: {
                        if (Disconnect.getVisibility() == View.INVISIBLE) {
                            Disconnect.setVisibility(View.VISIBLE);
                        }
                        Descript.setText(message);
                        prev_state = state;
                        updateAnimation();
                        break;
                    }
                    case 3:
                        Descript.setText(message);
                        Disconnect.setVisibility(View.INVISIBLE);
                        AcceptDeclineLayout.setVisibility(View.VISIBLE);
                        prev_state = state;
                        updateAnimation();
                        break;
                    case 4:
                    case 8:
                    case 9: {
                        if (Disconnect.getVisibility() == View.INVISIBLE) {
                            Disconnect.setVisibility(View.VISIBLE);
                        }

                        Descript.setText(message);
                        AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                        prev_state = state;
                        updateAnimation();
                        break;
                    }
                    case 11:
                        if (Disconnect.getVisibility() == View.VISIBLE) {
                            Disconnect.setVisibility(View.INVISIBLE);
                        }
                        if (AcceptDeclineLayout.getVisibility() == View.VISIBLE) {
                            AcceptDeclineLayout.setVisibility(View.INVISIBLE);
                        }
                        Connect.setVisibility(View.VISIBLE);
                        IntitalDescription = true;
                        if (AnimationRunning) {
                            stopAnimation();
                        }
                        if (prev_state == 8 || prev_state == 7 || prev_state == 9) {
                            Connect.setVisibility(View.VISIBLE);
                        } else {
                            Descript.setText(message);
                            break;
                        }
                        break;
                    case -1: {
                        if (message.contains("Accept Or Decline")) {

                            if (Disconnect.getVisibility() == View.VISIBLE) {
                                Disconnect.setVisibility(View.INVISIBLE);
                            }
                            AcceptDeclineLayout.setVisibility(View.VISIBLE);
                            Descript.setText(message);
                            prev_state = state;
                            break;
                        }
                    }
                }
            }

    }

    public void startAnimation(){
        Log.e("MAIN_ACTIVITY", "     Animation Started");
        AnimationRunning = true;

        if (mAnimation1.getCurrentSectionId() == null) {
            mAnimation1.transitionNow("pending");
            return;
        }



    }
    public void startAnimationRunning(){
        Log.e("MAIN_ACTIVITY", "     Animation Started");
        AnimationRunning = true;

        if (mAnimation1.getCurrentSectionId() == null) {
            mAnimation1.transitionNow("loading");
            return;
        }
        switch (mAnimation1.getCurrentSectionId()) {
            case "pending":
                mAnimation1.queueTransition("loading");
                break;
            case "loading":
                mAnimation1.queueTransition("finished");
                break;
            case "finished":
                mAnimation1.queueTransition("pending");
                break;
        }



    }

    public void updateAnimation(){
        Log.e("Main Activity","IntitialDescription: " + IntitalDescription  );
        if(mAnimation1!=null) {
            if (!IntitalDescription) {
                Log.e("MAIN_ACTIVITY", "     Animation Update");
                AnimationRunning = true;

                switch (mAnimation1.getCurrentSectionId()) {
                    case "pending":
                        mAnimation1.queueTransition("loading");
                        break;

                }
            }
        }

    }
    public void stopAnimation(){
        Log.e("MAIN_ACTIVITY", "     Animation Stopped");
        AnimationRunning = false;
        switch (mAnimation1.getCurrentSectionId()) {
            case "pending":
                mAnimation1.queueTransition("finished");
                break;
            case "loading":
                mAnimation1.queueTransition("finished");
                break;
        }
        
    }


    private MultiStateAnimation makeAnimationPortrait(View view){
        MultiStateAnimation.SectionBuilder startSection = new MultiStateAnimation.SectionBuilder("pending")
                .setOneshot(true)
                .setFrameDuration(30)
                .addFrame(R.drawable.portv200000)
                .addFrame(R.drawable.portv200001)
                .addFrame(R.drawable.portv200002)
                .addFrame(R.drawable.portv200003)
                .addFrame(R.drawable.portv200004)
                .addFrame(R.drawable.portv200005)
                .addFrame(R.drawable.portv200006)
                .addFrame(R.drawable.portv200007)
                .addFrame(R.drawable.portv200008)
                .addFrame(R.drawable.portv200009)
                .addFrame(R.drawable.portv200010)
                .addFrame(R.drawable.portv200011)
                .addFrame(R.drawable.portv200012)
                .addFrame(R.drawable.portv200013)
                .addFrame(R.drawable.portv200014)
                .addFrame(R.drawable.portv200015);

        MultiStateAnimation.SectionBuilder middleSection = new MultiStateAnimation.SectionBuilder("loading")
                .setOneshot(false)
                .setFrameDuration(30)
                .addFrame(R.drawable.portv200016)
                .addFrame(R.drawable.portv200017)
                .addFrame(R.drawable.portv200018)
                .addFrame(R.drawable.portv200019)
                .addFrame(R.drawable.portv200020)
                .addFrame(R.drawable.portv200021)
                .addFrame(R.drawable.portv200022)
                .addFrame(R.drawable.portv200023)
                .addFrame(R.drawable.portv200024)
                .addFrame(R.drawable.portv200025)
                .addFrame(R.drawable.portv200026)
                .addFrame(R.drawable.portv200027)
                .addFrame(R.drawable.portv200028)
                .addFrame(R.drawable.portv200029)
                .addFrame(R.drawable.portv200030)
                .addFrame(R.drawable.portv200031)
                .addFrame(R.drawable.portv200032)
                .addFrame(R.drawable.portv200033)
                .addFrame(R.drawable.portv200034)
                .addFrame(R.drawable.portv200035)
                .addFrame(R.drawable.portv200036)
                .addFrame(R.drawable.portv200037)
                .addFrame(R.drawable.portv200038)
                .addFrame(R.drawable.portv200039)
                .addFrame(R.drawable.portv200040)
                .addFrame(R.drawable.portv200041)
                .addFrame(R.drawable.portv200042)
                .addFrame(R.drawable.portv200043)
                .addFrame(R.drawable.portv200044)
                .addFrame(R.drawable.portv200045)
                .addFrame(R.drawable.portv200046)
                .addFrame(R.drawable.portv200047)
                .addFrame(R.drawable.portv200048)
                .addFrame(R.drawable.portv200049)
                .addFrame(R.drawable.portv200050)
                .addFrame(R.drawable.portv200051)
                .addFrame(R.drawable.portv200052)
                .addFrame(R.drawable.portv200053)
                .addFrame(R.drawable.portv200054)
                .addFrame(R.drawable.portv200055)
                .addFrame(R.drawable.portv200056)
                .addFrame(R.drawable.portv200057)
                .addFrame(R.drawable.portv200058)
                .addFrame(R.drawable.portv200057)
                .addFrame(R.drawable.portv200056)
                .addFrame(R.drawable.portv200055)
                .addFrame(R.drawable.portv200054)
                .addFrame(R.drawable.portv200053)
                .addFrame(R.drawable.portv200052)
                .addFrame(R.drawable.portv200051)
                .addFrame(R.drawable.portv200050)
                .addFrame(R.drawable.portv200049)
                .addFrame(R.drawable.portv200049)
                .addFrame(R.drawable.portv200048)
                .addFrame(R.drawable.portv200047)
                .addFrame(R.drawable.portv200046)
                .addFrame(R.drawable.portv200045)
                .addFrame(R.drawable.portv200044)
                .addFrame(R.drawable.portv200043)
                .addFrame(R.drawable.portv200042)
                .addFrame(R.drawable.portv200041)
                .addFrame(R.drawable.portv200040)
                .addFrame(R.drawable.portv200039)
                .addFrame(R.drawable.portv200038)
                .addFrame(R.drawable.portv200037)
                .addFrame(R.drawable.portv200036)
                .addFrame(R.drawable.portv200035)
                .addFrame(R.drawable.portv200034)
                .addFrame(R.drawable.portv200033)
                .addFrame(R.drawable.portv200032)
                .addFrame(R.drawable.portv200031)
                .addFrame(R.drawable.portv200030)
                .addFrame(R.drawable.portv200029)
                .addFrame(R.drawable.portv200028)
                .addFrame(R.drawable.portv200027)
                .addFrame(R.drawable.portv200026)
                .addFrame(R.drawable.portv200025)
                .addFrame(R.drawable.portv200024)
                .addFrame(R.drawable.portv200023)
                .addFrame(R.drawable.portv200022)
                .addFrame(R.drawable.portv200021)
                .addFrame(R.drawable.portv200020)
                .addFrame(R.drawable.portv200019)
                .addFrame(R.drawable.portv200018)
                .addFrame(R.drawable.portv200017)



                ;
        MultiStateAnimation.SectionBuilder endSection = new MultiStateAnimation.SectionBuilder("finished")
                .setOneshot(true)
                .setFrameDuration(30)
                .addFrame(R.drawable.portv200016)
                .addFrame(R.drawable.portv200015)
                .addFrame(R.drawable.portv200014)
                .addFrame(R.drawable.portv200013)
                .addFrame(R.drawable.portv200012)
                .addFrame(R.drawable.portv200011)
                .addFrame(R.drawable.portv200010)
                .addFrame(R.drawable.portv200009)
                .addFrame(R.drawable.portv200008)
                .addFrame(R.drawable.portv200007)
                .addFrame(R.drawable.portv200006)
                .addFrame(R.drawable.portv200005)
                .addFrame(R.drawable.portv200004)
                .addFrame(R.drawable.portv200003)
                .addFrame(R.drawable.portv200002)
                .addFrame(R.drawable.portv200001)
                .addFrame(R.drawable.portv200000)
                ;


        return new MultiStateAnimation.Builder(view)
                .addSection(startSection)
                .addSection(middleSection)
                .addSection(endSection)
                .build(this);




    }

    private MultiStateAnimation makeAnimationLandscape(View view){
        MultiStateAnimation.SectionBuilder startSection = new MultiStateAnimation.SectionBuilder("pending")
                .setOneshot(true)
                .setFrameDuration(30)
                .addFrame(R.drawable.landscape00000)
                .addFrame(R.drawable.landscape00001)
                .addFrame(R.drawable.landscape00002)
                .addFrame(R.drawable.landscape00003)
                .addFrame(R.drawable.landscape00004)
                .addFrame(R.drawable.landscape00005)
                .addFrame(R.drawable.landscape00006)
                .addFrame(R.drawable.landscape00007)
                .addFrame(R.drawable.landscape00008)
                .addFrame(R.drawable.landscape00009)
                .addFrame(R.drawable.landscape00010)
                .addFrame(R.drawable.landscape00011)
                .addFrame(R.drawable.landscape00012)
                .addFrame(R.drawable.landscape00013)
                .addFrame(R.drawable.landscape00014)
                .addFrame(R.drawable.landscape00015);

        MultiStateAnimation.SectionBuilder middleSection = new MultiStateAnimation.SectionBuilder("loading")
                .setOneshot(false)
                .setFrameDuration(30)
                .addFrame(R.drawable.landscape00016)
                .addFrame(R.drawable.landscape00017)
                .addFrame(R.drawable.landscape00018)
                .addFrame(R.drawable.landscape00019)
                .addFrame(R.drawable.landscape00020)
                .addFrame(R.drawable.landscape00021)
                .addFrame(R.drawable.landscape00022)
                .addFrame(R.drawable.landscape00023)
                .addFrame(R.drawable.landscape00024)
                .addFrame(R.drawable.landscape00025)
                .addFrame(R.drawable.landscape00026)
                .addFrame(R.drawable.landscape00027)
                .addFrame(R.drawable.landscape00028)
                .addFrame(R.drawable.landscape00029)
                .addFrame(R.drawable.landscape00030)
                .addFrame(R.drawable.landscape00031)
                .addFrame(R.drawable.landscape00032)
                .addFrame(R.drawable.landscape00033)
                .addFrame(R.drawable.landscape00034)
                .addFrame(R.drawable.landscape00035)
                .addFrame(R.drawable.landscape00036)
                .addFrame(R.drawable.landscape00037)
                .addFrame(R.drawable.landscape00038)
                .addFrame(R.drawable.landscape00039)
                .addFrame(R.drawable.landscape00040)
                .addFrame(R.drawable.landscape00041)
                .addFrame(R.drawable.landscape00042)
                .addFrame(R.drawable.landscape00043)
                .addFrame(R.drawable.landscape00044)
                .addFrame(R.drawable.landscape00045)
                .addFrame(R.drawable.landscape00046)
                .addFrame(R.drawable.landscape00047)
                .addFrame(R.drawable.landscape00048)
                .addFrame(R.drawable.landscape00049)
                .addFrame(R.drawable.landscape00050)
                .addFrame(R.drawable.landscape00051)
                .addFrame(R.drawable.landscape00052)
                .addFrame(R.drawable.landscape00053)
                .addFrame(R.drawable.landscape00054)
                .addFrame(R.drawable.landscape00055)
                .addFrame(R.drawable.landscape00056)
                .addFrame(R.drawable.landscape00057)
                .addFrame(R.drawable.landscape00058)
                .addFrame(R.drawable.landscape00057)
                .addFrame(R.drawable.landscape00056)
                .addFrame(R.drawable.landscape00055)
                .addFrame(R.drawable.landscape00054)
                .addFrame(R.drawable.landscape00053)
                .addFrame(R.drawable.landscape00052)
                .addFrame(R.drawable.landscape00051)
                .addFrame(R.drawable.landscape00050)
                .addFrame(R.drawable.landscape00049)
                .addFrame(R.drawable.landscape00049)
                .addFrame(R.drawable.landscape00048)
                .addFrame(R.drawable.landscape00047)
                .addFrame(R.drawable.landscape00046)
                .addFrame(R.drawable.landscape00045)
                .addFrame(R.drawable.landscape00044)
                .addFrame(R.drawable.landscape00043)
                .addFrame(R.drawable.landscape00042)
                .addFrame(R.drawable.landscape00041)
                .addFrame(R.drawable.landscape00040)
                .addFrame(R.drawable.landscape00039)
                .addFrame(R.drawable.landscape00038)
                .addFrame(R.drawable.landscape00037)
                .addFrame(R.drawable.landscape00036)
                .addFrame(R.drawable.landscape00035)
                .addFrame(R.drawable.landscape00034)
                .addFrame(R.drawable.landscape00033)
                .addFrame(R.drawable.landscape00032)
                .addFrame(R.drawable.landscape00031)
                .addFrame(R.drawable.landscape00030)
                .addFrame(R.drawable.landscape00029)
                .addFrame(R.drawable.landscape00028)
                .addFrame(R.drawable.landscape00027)
                .addFrame(R.drawable.landscape00026)
                .addFrame(R.drawable.landscape00025)
                .addFrame(R.drawable.landscape00024)
                .addFrame(R.drawable.landscape00023)
                .addFrame(R.drawable.landscape00022)
                .addFrame(R.drawable.landscape00021)
                .addFrame(R.drawable.landscape00020)
                .addFrame(R.drawable.landscape00019)
                .addFrame(R.drawable.landscape00018)
                .addFrame(R.drawable.landscape00017)



                ;
        MultiStateAnimation.SectionBuilder endSection = new MultiStateAnimation.SectionBuilder("finished")
                .setOneshot(true)
                .setFrameDuration(30)
                .addFrame(R.drawable.landscape00016)
                .addFrame(R.drawable.landscape00015)
                .addFrame(R.drawable.landscape00014)
                .addFrame(R.drawable.landscape00013)
                .addFrame(R.drawable.landscape00012)
                .addFrame(R.drawable.landscape00011)
                .addFrame(R.drawable.landscape00010)
                .addFrame(R.drawable.landscape00009)
                .addFrame(R.drawable.landscape00008)
                .addFrame(R.drawable.landscape00007)
                .addFrame(R.drawable.landscape00006)
                .addFrame(R.drawable.landscape00005)
                .addFrame(R.drawable.landscape00004)
                .addFrame(R.drawable.landscape00003)
                .addFrame(R.drawable.landscape00002)
                .addFrame(R.drawable.landscape00001)
                .addFrame(R.drawable.landscape00000)
                ;


        return new MultiStateAnimation.Builder(view)
                .addSection(startSection)
                .addSection(middleSection)
                .addSection(endSection)
                .build(this);




    }


    @Override
    public void onAnimationFinished() {
        if (mAnimation1.getCurrentDrawable().isOneShot()) {
          Log.e("Animation", mAnimation1.getCurrentSectionId());
        }
    }

    @Override
    public void onAnimationStarting() {
        if (mAnimation1.getTransitioningFromId() != null) {
            Log.e("Animation", mAnimation1.getCurrentSectionId());
        } else if (!mAnimation1.getCurrentDrawable().isOneShot()) {
            Log.e("Animation", mAnimation1.getCurrentSectionId());
        }
    }

}