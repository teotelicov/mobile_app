package cs.ubbcluj.ro.deliveryservice.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cs.ubbcluj.ro.deliveryservice.MainActivity;

/**
 * Created by Teo on 28.12.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column products
    public static final String DB_NAME = "ServiceDB";
    public static final String TABLE_NAME = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";


    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_DESCRIPTION +
                " VARCHAR, " +COLUMN_STATUS +
                " TINYINT );";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
    * This method is taking two arguments
    * first one is the name that is to be saved
    * second one is the description that is to be saved
    * second one is the status
    * 0 means the product is synced with the server
    * 1 means the product is not synced with the server
    * */
    public boolean addProduct(String name, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
    * This method taking two arguments
    * first one is the id of the name for which
    * we have to update the sync status
    * and the second one is the status that will be changed
    * */
    public boolean updateProductStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    public boolean updateProductStatus(String name, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_NAME + " LIKE '%" + name + "%' AND " + COLUMN_DESCRIPTION + " LIKE '%" +description + "%'", null);
        db.close();
        return true;
    }

    /*
    * this method will give us all the products stored in sqlite
    * */
    public Cursor getProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * this method will give us all the products stored in sqlite
    * */
    public Cursor getProductId(String name, String description) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " LIKE '%" + name + "%' AND " + COLUMN_DESCRIPTION + " LIKE '%" +description + "%'";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * this method is for getting all the unsynced product
    * so that we can sync it with database
    * */
    public Cursor getUnsyncedProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 3 OR " + COLUMN_STATUS + " = 2;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public boolean deleteProduct(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,COLUMN_ID + " = " + id ,null);
        db.close();
        return true;
    }

}