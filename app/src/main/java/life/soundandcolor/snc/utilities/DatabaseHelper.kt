package life.soundandcolor.snc.utilities

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import life.soundandcolor.snc.utilities.Helper.parseMessage
import org.json.JSONObject
import timber.log.Timber

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("drop table if exists $TABLE_NAME")
        db.execSQL("drop table if exists $TABLE_NAME2")
        db.execSQL("drop table if exists $TABLE_NAME3")
        db.execSQL("create table $TABLE_NAME ($COL_1 TEXT primary key, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 boolean);")
        db.execSQL("create table $TABLE_NAME2 ($COL_1 TEXT primary key, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 boolean);")
        db.execSQL("create table $TABLE_NAME3 (username TEXT, display_name TEXT, name TEXT, id TEXT, url TEXT, album TEXT, img TEXT, artist TEXT, duration TEXT, " +
                "type TEXT, timestamp LONG, primary key(username, timestamp));")
//        db.execSQL("create table $TABLE_NAME4 (username TEXT, name TEXT, id TEXT, url TEXT, img TEXT," +
//                "type TEXT, pop TEXT, genres TEXT, primary key(username, id));")
        db.execSQL("create table $TABLE_NAME5 (username TEXT, message TEXT, sent BIT);")
    }


    fun add(js: JSONObject, db:SQLiteDatabase, table: String) {
        var cv = ContentValues()
        for (key in js.keys()) {
            cv.put(key, js.getString(key))
        }
//        try {
            db.insert(table, null, cv)
//        }
//        catch (e: SQLiteConstraintException) {}
        Timber.e(cv.toString())
    }


    fun get_current(): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery("select $COL_1, $COL_2 from $TABLE_NAME2;", null)
        res.moveToFirst()
        return res
    }

    fun get(table: String, query:String? = null): Cursor {
        val db = this.writableDatabase
        if (query == null)
            return db.rawQuery("select * from $table;", null)
        else
            return db.rawQuery("select * from $table $query;", null)
    }


//    fun getUsers(): Cursor {
//        val db = this.writableDatabase
////        Cursor res = db.rawQuery("select " + COL_1 + " type " + TABLE_NAME +";", null);
//        return db.rawQuery("select * type users;", null)
//    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }


    fun change(user: String) {
        val db = this.writableDatabase
        db.execSQL("drop table if exists $TABLE_NAME2") // dont drop just update
        db.execSQL("create table $TABLE_NAME2 ($COL_1 TEXT primary key, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 boolean);")

        val res = db.rawQuery("select $COL_2,$COL_3,$COL_4,$COL_5 from $TABLE_NAME where username = '$user';", null)
        res.moveToFirst()

        val cv = ContentValues()
        cv.put(COL_1, user)
        cv.put(COL_2, res.getString(0))
        cv.put(COL_3, res.getString(1))
        cv.put(COL_4, res.getString(2))
        cv.put(COL_5, res.getString(3))
        db.insert(TABLE_NAME2, null, cv)
    }


    fun get_owner(): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery("select $COL_1, $COL_2, $COL_3 from $TABLE_NAME where owner is not null;", null)
        res.moveToFirst()
        return res
    }


    fun update(newToken: String, refresh: String) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_NAME2 SET $COL_2 = '$newToken' WHERE $COL_3 = '$refresh';")
        db.execSQL("UPDATE $TABLE_NAME SET $COL_2 = '$newToken' WHERE $COL_3 = '$refresh';")
    }

//    -------------------------------------------MESSAGES-----------------------------------------------

    fun chats(): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery("select distinct(username) from $TABLE_NAME;", null)
        return res
    }

    fun messages(user: String): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery("select message, sent from $TABLE_NAME5 where username = $user;", null)
        return res
    }

    fun send(user: String, message: String, sent: Int) {
        val js = JSONObject()
        js.put("username", user)
        js.put("message", parseMessage(message))
        js.put("sent", sent)
        add(js, this.writableDatabase, TABLE_NAME5)
    }



    companion object {

        val DATABASE_NAME = "users.db"
        val TABLE_NAME = "users"
        val TABLE_NAME2 = "current"
        val TABLE_NAME3 = "feed"
//        val TABLE_NAME4 = "following"
        val TABLE_NAME5 = "messages"

        val COL_1 = "username"
        val COL_2 = "name"
        val COL_3 = "img"
        val COL_4 = "email"
        val COL_5 = "owner"

    }
}
