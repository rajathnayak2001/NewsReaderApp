package com.example.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME="LINKS";
  private static final String DB_Table="LINK_Table";
  private static final String Col1="Titles";
  private static final String Col2="Links";
  private static final  String Create_table="CREATE TABLE "+DB_Table+" (Titles VARCHAR, Links VARCHAR)";
    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

       sqLiteDatabase.execSQL(Create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
          sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+DB_Table);
          onCreate(sqLiteDatabase);
    }
    public void addData(String name1,String name2)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Col1,name1);
        contentValues.put(Col2,name2);
        Long result=db.insert(DB_Table,null,contentValues);

    }
    public Cursor viewdata()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+DB_Table;
        Cursor cursor=db.rawQuery(query,null);
        return cursor;
    }
   public void delete()
   {
       SQLiteDatabase db=this.getWritableDatabase();
       db.execSQL("DELETE FROM "+ DB_Table);
   }

}
