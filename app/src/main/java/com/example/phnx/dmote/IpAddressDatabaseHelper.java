package com.example.phnx.dmote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josiah on 4/2/2016.
 */
public class IpAddressDatabaseHelper extends SQLiteOpenHelper {
    //Databsse Info
    private static final  String DATABASE_NAME = "ipDatabase";
    private static final int DATABASE_VERSION =1;

    //Table Names
    private static final String Table_IP = "ip_address";

    //IpAddress Table Columns
    private static final String KEY_IP_ID = "id";
    private static final String KEY_IP_NAME= "name";
    private static final String KEY_IP_ADDRESS = "address";
  //  private static final String KEY_IP_PORT = "port";

    private static IpAddressDatabaseHelper sInstance;

    public static synchronized IpAddressDatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new IpAddressDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;


    }

    private IpAddressDatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    //
    @Override
    public void onConfigure(SQLiteDatabase db){
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IP_TABLE = "CREATE TABLE " + Table_IP +
                "(" +
                    KEY_IP_ID + " INTEGER PRIMARY KEY," +
                    KEY_IP_NAME + " TEXT," +
                    KEY_IP_ADDRESS + " TEXT" +
                ")";

        db.execSQL(CREATE_IP_TABLE);
    }
    @Override
    public void onUpgrade ( SQLiteDatabase db, int oldVerision, int newVersion ){
        if(oldVerision != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Table_IP);
            onCreate(db);

        }
    }

    public long addOrUpdateIpAddress (IpAddress ipAddress) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_IP_NAME, ipAddress.name);
            values.put(KEY_IP_ADDRESS, ipAddress.address);
            int rows = db.update(Table_IP, values, KEY_IP_NAME + "= ?", new String[]{ipAddress.name});

            if (rows == 1) {
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_IP_ID, Table_IP, KEY_IP_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(ipAddress.name)});
                try {
                    // Get the primary key of the user we jsut updated
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();

                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }

                }


            } else {
                userId = db.insertOrThrow(Table_IP, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("TAG", "Error while trying to add or update ip address");
        } finally {
            db.endTransaction();





            }
        return userId;

        }

    public void addIpAddressOnly(IpAddress ipAddress){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP_NAME, ipAddress.name);
        values.put(KEY_IP_ADDRESS, ipAddress.address);

        db.insert(Table_IP, null, values);
        db.close();

    }
    public int updateIpAddressOnly(IpAddress ipAddress){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP_ADDRESS, ipAddress.address);

        return db.update(Table_IP, values, KEY_IP_ID + " = ?",
                new String[] { String.valueOf(ipAddress.id)});

    }
    public int updateEntireIpAddress(IpAddress ipAddress){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP_NAME, ipAddress.name);
        values.put(KEY_IP_ADDRESS, ipAddress.address);


        return db.update(Table_IP, values, KEY_IP_ID + " = ?",
                new String[] { String.valueOf(ipAddress.id)});

    }
    public int updateIpName(IpAddress ipAddress){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IP_NAME, ipAddress.name);

        return db.update(Table_IP, values, KEY_IP_NAME + " = ?",
                new String[] { String.valueOf(ipAddress.name)});
    }
    public List<IpAddress> getAllIpAddress() {
        List<IpAddress> ipAddresses = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Table_IP;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try{
            if(cursor.moveToFirst())  {
                do {
                    IpAddress newIp = new IpAddress();
                    newIp.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_IP_ID)));
                    newIp.name = cursor.getString(cursor.getColumnIndex(KEY_IP_NAME));
                    newIp.address = cursor.getString(cursor.getColumnIndex(KEY_IP_ADDRESS));
                    ipAddresses.add(newIp);
                }while (cursor.moveToNext());

            }

        } catch (Exception e){
            Log.e("DATABASE","Error while tring to get Ip Addresses from database");

        } finally {
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }

        }
        return ipAddresses;
    }
    public List<String> getAllIpAddressNames() {
        List<String> ipAddressesNames = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Table_IP;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try{
            if(cursor.moveToFirst())  {
                do {
                    String newIpName  = cursor.getString(cursor.getColumnIndex(KEY_IP_NAME));
                    ipAddressesNames.add(newIpName);
                }while (cursor.moveToNext());

            }

        } catch (Exception e){
            Log.e("DATABASE","Error while tring to get Ip Addresses from database");

        } finally {
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }

        }
        return ipAddressesNames;
    }

    public void deleteIpAddress(IpAddress ipAddress){
        //boolean result = false;

      //  String deleteQuery = "Select * FROM " + Table_IP + " WHERE " + KEY_IP_ID

        SQLiteDatabase db = getWritableDatabase();
        db.delete(Table_IP, KEY_IP_ID + " = ?",
                new String[]{ String.valueOf(ipAddress.getId()) } );
        db.close();
    }

    public void deleteAllIpAddress() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(Table_IP, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DATABASE", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }





}
