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
    public static final String TABLE_PRODUCT = "products";
    public static final String TABLE_DELIVERIES = "deliveries";
    public static final String TABLE_OFFERS = "offers";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_DELIVERY_ID = "delivery_id";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_ADDED_AT= "added_at";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";
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
        String sql = "CREATE TABLE " + TABLE_PRODUCT
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_DESCRIPTION +
                " VARCHAR, " +COLUMN_STATUS +
                " TINYINT );";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE " + TABLE_DELIVERIES
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_ADDRESS +
                " VARCHAR);";
        db.execSQL(sql2);

        String sql3 = "CREATE TABLE " + TABLE_OFFERS + "("
                + COLUMN_PRODUCT_ID  +" INTEGER, "
                + COLUMN_DELIVERY_ID +" INTEGER, "
                + COLUMN_PRICE + " DOUBLE, " +
                COLUMN_ADDED_AT + " DATE, " +
                COLUMN_STATUS + " TINYINT,"
                + "PRIMARY KEY (" + COLUMN_PRODUCT_ID + "," + COLUMN_DELIVERY_ID +"),"
                + "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ")REFERENCES " + TABLE_PRODUCT + " (" + COLUMN_ID +") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY (" + COLUMN_DELIVERY_ID + ") REFERENCES " + TABLE_DELIVERIES + " (" + COLUMN_ID +") ON DELETE CASCADE ON UPDATE CASCADE );";


        db.execSQL(sql3);

        String sql_insert1 = "INSERT INTO deliveries (name,address) VALUES ('Pizza Hut', 'str. Lacramioarelor nr.3')";
        db.execSQL(sql_insert1);
        String sql_insert2 = "INSERT INTO deliveries (name,address) VALUES ('Pizza Venezia', 'str. Lacramioarelor nr.3')";
        db.execSQL(sql_insert2);
        String sql_insert3 = "INSERT INTO deliveries (name,address) VALUES ('Pizza Grande', 'str. Lacramioarelor nr.3')";
        db.execSQL(sql_insert3);
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

        db.insert(TABLE_PRODUCT, null, contentValues);
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
        db.update(TABLE_PRODUCT, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    public boolean updateOfferStatus(int product_id, int delivery_id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_OFFERS, contentValues, COLUMN_PRODUCT_ID + "=" + product_id + " AND " + COLUMN_DELIVERY_ID + "=" + delivery_id, null);
        db.close();
        return true;
    }

    public boolean updateProductStatus(String name, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_PRODUCT, contentValues, COLUMN_NAME + " LIKE '%" + name + "%' AND " + COLUMN_DESCRIPTION + " LIKE '%" +description + "%'", null);
        db.close();
        return true;
    }

    public boolean updateProduct(int id, String name, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_STATUS, status);

        db.update(TABLE_PRODUCT, contentValues, COLUMN_ID + "=" + id + " AND " + COLUMN_STATUS + " = " + MainActivity.DATA_SYNCED_WITH_SERVER, null);

        contentValues.put(COLUMN_STATUS, MainActivity.ADD_NOT_SYNCED_WITH_SERVER);
        db.update(TABLE_PRODUCT, contentValues, COLUMN_ID + "=" + id + " AND " + COLUMN_STATUS + " = " + MainActivity.ADD_NOT_SYNCED_WITH_SERVER, null);
        db.close();
        return true;
    }

    /*
    * this method will give us all the products stored in sqlite
    * */
    public Cursor getProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCT + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_ID + "=" +id;
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getProductId(String name, String description) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_NAME + " LIKE '%" + name + "%' AND " + COLUMN_DESCRIPTION + " LIKE '%" +description + "%'";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getProductByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_NAME + " LIKE '%" + name + "%'";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getDeliveryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DELIVERIES + " WHERE " + COLUMN_NAME + " LIKE '%" + name + "%'";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * this method is for getting all the unsynced product
    * so that we can sync it with database
    * */
    public Cursor getUnsyncedProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT * FROM " + TABLE_PRODUCT + " WHERE " + COLUMN_STATUS + " = 3 OR "+ COLUMN_STATUS + " = 4 OR " + COLUMN_STATUS + " = 2;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    public Cursor getUnsyncedOffers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT * FROM " + TABLE_OFFERS + " WHERE " + COLUMN_STATUS + " = 5;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean deleteProduct(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT,COLUMN_ID + " = " + id ,null);
        db.close();
        return true;
    }

    /*
    * this method will give us all the deliveries stored in sqlite
    * */
    public Cursor getDeliveries() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_DELIVERIES + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getOffers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_OFFERS + " ;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor getEntriesByProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT offers.product_id, offers.delivery_id, offers.price, offers.added_at, deliveries.name FROM offers" +
                " INNER JOIN deliveries ON offers.delivery_id=deliveries.id WHERE offers.product_id=" + id + ";";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public boolean addOffer(int product_id, int delivery_id, double price, String added_at,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_PRODUCT_ID, product_id);
        contentValues.put(COLUMN_DELIVERY_ID, delivery_id);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_ADDED_AT, added_at);
        contentValues.put(COLUMN_STATUS, status);

        db.insert(TABLE_OFFERS, null, contentValues);
        db.close();
        return true;
    }

}