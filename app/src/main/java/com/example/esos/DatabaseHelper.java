package com.example.esos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "emergency_sos.db";
    private static final String TABLE_NAME = "contacts";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "PHONE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the contacts table
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT NOT NULL, " +
                COL_3 + " TEXT NOT NULL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database by dropping the old table and creating a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to add a contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, contact.getName());
        contentValues.put(COL_3, contact.getPhone());
        db.insert(TABLE_NAME, null, contentValues);
        db.close(); // Close the database connection
    }

    // Method to retrieve all contacts
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Use getColumnIndex to get the index safely
                int nameIndex = cursor.getColumnIndex(COL_2);
                int phoneIndex = cursor.getColumnIndex(COL_3);

                // Check if the indices are valid
                if (nameIndex != -1 && phoneIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String phone = cursor.getString(phoneIndex);
                    contacts.add(new Contact(name, phone));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close(); // Close cursor to avoid memory leaks
        }
        db.close(); // Close the database connection
        return contacts;
    }

}
