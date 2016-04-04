package com.example.phnx.dmote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Josiah on 3/25/2016.
 */
public class IpAddressPref {
    private SharedPreferences settings;
    private Set<String> IpAddressSet;
    private ArrayList<String> IpArray;
    private DefaultListener mDefaultListener = null;


        IpAddressPref(Context activity, DefaultListener listener){
            settings = PreferenceManager.getDefaultSharedPreferences(activity);
        IpAddressSet = settings.getStringSet("IpSet",  new HashSet<String>());
             IpArray = new ArrayList<>(IpAddressSet);
            if(IpArray.size()>1){
            Collections.sort(IpArray);
            }

            Log.e("Dialog",Integer.toString(IpArray.size()));
            Log.e("Dialog","Constructor: IpArray: " );
            for(int i = 0; i< IpArray.size(); i++){
                Log.e("Dialog","    " +IpArray.get(i));

            }
            mDefaultListener=listener;

    }

    protected void reload()   {
        if(IpArray.size()>1){
            Collections.sort(IpArray);
        }
    }

    protected  String getDefault(){
        String default_ip = settings.getString("default_ip_address","Empty");


        Log.e("Dialog","getDefault: "+default_ip);
        return default_ip;
    }
    protected  int getDefaultIndex(){
        /*
        ArrayList<String> subArray = new ArrayList<>(IpArray);
        for(int i = 0 ;i<subArray.size();i++) {
            String temp = IpArray.get(i);
            subArray.set(i, temp.substring(2));
        }*/
        int index = IpArray.indexOf(getDefault());
        Log.e("Dialog","getDefaultIndex: "+Integer.toString(index));
        return index;
    }

    protected String getDefaultIp(){
        String temp = getDefault();
        String IPsplit[] = temp.split(":");
        return IPsplit[0];
    }
    protected String getDefaultPort(){
        String temp = getDefault();
        String IPsplit[] = temp.split(":");
        return IPsplit[1];
    }

    protected void add(String val1, String val2){
        Log.e("Dialog","Add: ");
        String temp = Integer.toString(IpArray.size())+" "+val1+":"+val2;
        IpArray.add(IpArray.size(),temp);
        Log.e("Dialog","Add: Inside IpArray: ");
        for(int i = 0; i< IpArray.size(); i++){
            Log.e("Dialog","    "+IpArray.get(i));

        }
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("default_ip_address",temp);

        editor.apply();
        mDefaultListener.onDefaultChange(temp);
        checkout();
    }

    protected void setDefault(int index){
        Log.e("Dialog", "SetDefaultIndex: " + Integer.toString(index));
        Log.e("Dialog", "SetDefault: "+IpArray.get(index));
        if(IpArray.size()>0 && index>-1) {

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("default_ip_address", IpArray.get(index));
            Log.e("Dialog", "SetDefault: " +settings.getString("default_ip_address","Empty"));
            editor.apply();
            mDefaultListener.onDefaultChange( IpArray.get(index));
        }
    }

    protected CharSequence[] getDialogSequence(){
        Log.e("Dialog","getDialogSequence: B4 substr IpArray");
        ArrayList<String> subArray = new ArrayList<>(IpArray);
        for(int i = 0 ;i<IpArray.size();i++){
            Log.e("Dialog","    "+ IpArray.get(i));
        }Log.e("Dialog","getDialogSequence:AF substr IpArray");
        for(int i = 0 ;i<subArray.size();i++){
            String temp = IpArray.get(i);
            subArray.set(i,temp.substring(2));
            Log.e("Dialog","    "+ IpArray.get(i));
        }
        CharSequence[] charArray = subArray.toArray(new CharSequence[subArray.size()] );
        Log.e("Dialog","getDialogSequence: CharArray ");
        for(int i = 0 ; i<charArray.length;i++){
            Log.e("Dialog","    "+charArray[i]);
        }

        return charArray;
    }


    protected String getIp(int index){
        String temp = IpArray.get(index);
        Log.e("Dialog","GetIp: index:"+IpArray.get(index));
        String IPsplit[] = temp.split(":");
        Log.e("Dialog","GetIp: "+temp);
        if (IPsplit[0].length()>0) {
            return IPsplit[0].substring(2);
        }
        else
            return "";
    }
    protected String getPort(int index){
        String temp = IpArray.get(index);
        String IPsplit[] = temp.split(":");
        Log.e("Dialog","GetPort; "+temp);
       // Log.e("Dialog",IPsplit[1]+"[1] Length: "+ IPsplit.length+"[0]: "+IPsplit[0] );
        if (IPsplit.length>1) {
            return IPsplit[1];
        }
        else {
            return "";
        }
    }

    protected void remove(int index){
        Log.e("Dialog","Remove: ");
        IpArray.remove(index);
        checkout();
    }
    protected void save(String str1, int index){
        Log.e("Dialog","Save: Inside B4 IpArray:");
        for(int i = 0; i< IpArray.size(); i++){
            Log.e("Dialog","     " +IpArray.get(i));
        }
        Log.e("Dialog","Save: str1: "+str1);
        String temp = Integer.toString(index)+" "+str1;
        Log.e("Dialog","Save: Temp: "+temp);
        IpArray.set(index,temp);
        Log.e("Dialog","Save: Inside Af IpArray:");
        for(int i = 0; i< IpArray.size(); i++){
            Log.e("Dialog","    "+IpArray.get(i));
        }
        String saving = Integer.toString(index)+" "+IpArray.get(index);
        Log.e("Dialog","     Saving:"+saving);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("default_ip_address",saving);

        editor.apply();
        mDefaultListener.onDefaultChange(saving);
        checkout();
    }
    protected int getCount(){
        return IpArray.size();
    }

    protected void checkout(){
        IpAddressSet = new HashSet<>(IpArray);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("IpSet",IpAddressSet);
        editor.apply();
    }
    protected int getSize(){
        return IpArray.size();
    }
    protected String get(int index){
        return IpArray.get(index);
    }

    public interface DefaultListener{
       public void onDefaultChange(String message);
    }


}
