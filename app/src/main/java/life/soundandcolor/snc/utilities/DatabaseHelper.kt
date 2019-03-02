package life.soundandcolor.snc.utilities

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import life.soundandcolor.snc.utilities.Helper.parse_message
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

//    val users: Cursor
//        get() {
//            val db = this.writableDatabase
//            return db.rawQuery("select * from users;", null)
//        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("drop table if exists $TABLE_NAME")
        db.execSQL("drop table if exists $TABLE_NAME2")
        db.execSQL("drop table if exists $TABLE_NAME3")
        db.execSQL("create table $TABLE_NAME ($COL_1 TEXT primary key, $COL_2 TEXT, $COL_3 TEXT, $COL_4 TEXT, $COL_5 TEXT, $COL_6 TEXT, $COL_7 boolean);")
        db.execSQL("create table $TABLE_NAME2 ($COL_1 TEXT primary key, $COL_2 TEXT, $COL_3 TEXT);")
        db.execSQL("create table $TABLE_NAME3 (user TEXT, name TEXT, id TEXT, url TEXT, album TEXT, img TEXT, artist TEXT, duration TEXT, " +
                "type TEXT, timestamp LONG, primary key(user, timestamp));")
        db.execSQL("create table $TABLE_NAME4 (user TEXT, name TEXT, id TEXT, url TEXT, img TEXT," +
                "type TEXT, pop TEXT, genres TEXT, primary key(user, id));")
        db.execSQL("create table $TABLE_NAME5 (user TEXT, message TEXT, sent BIT);")

        li = JSONArray(
                "[\n" +
                        "  {\n" +
                        "    \"username\": \"mannmann2\",\n" +
                        "    \"refresh\": \"AQArozoXK9xKgXzJQm0-M48HCf0R9jvppJoZd1y-ARnO67K8j-H2a1Fk1Slx581sqZmeZiUZILT9pFMCY1UTKRK9woY0RLxWrM2j3Ex36V3FvJMP-HW8FwoFjJDIyDc8EacJgw\",\n" +
                        "    \"token\": \"BQAcTQeLNL8Y4-64OZpX9Ix6rMQ9ZILQvp7DEc--u17ulpiVf5Sstg1752OZ4kNd5_7PuOKZ2gVXj-RzQo50wKNzOAhVMVN0nUkrsGaOuR-8ZH2632DfhYYCsFwmuj8g7Ml_e831t9SamC0ndmttvCwJgl8qjzzta0WIn77zqvptQVN2GZjzywAy-LrlZjSP2okiJT6JzOhgIWTAwZvx8oce1vY6HeRQR0Vo199PlpIRmA\",\n" +
                        "    \"owner\": true\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"abhirup_7\",\n" +
                        "    \"token\": \"BQBPCSulVpOk06lDNdo4HyWtN0FdVT-Ba4-iSwNlGY3Tq4uyo5UIT7GBpaqtkbaQpw-zoyGWa9S3W4jt9OYYKPydpb7NKIiJm5UJXFixRnJxyGgRurmZgJjXuLzuQEP3hVvQXWpFL85_oychvaDwBMgOP10G8kxTV5KzI4A0\",\n" +
                        "    \"refresh\": \"AQCpEXKTWb2yFXnkCmiXCQLiVhwnr0fraasKYiKZyRWX06ll_ly1ogH2Rr15N8JKGlBcjphrN7r7qnP2TTjkil_jvnGwX5VvBVae6MLgpw8pxlgkiWizdTrjRvDoR3AaBWQ\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"gaganberi95\",\n" +
                        "    \"token\": \"BQD-1IKHr2qLFpic9qHkFVaIS3T35KBPY2DLxV5RpdyOKgetsbPPT9xoU49lqWOPmtC4bw_XFBDLhC4fYR7mcHWqnr95R2G9VLQHGbbnjdTXmM3XIBE1gJKZtG0YxmFMoM71dLAZmkFWRqPSJT8tAcUFWSc0Yggzlk0DRKo\",\n" +
                        "    \"refresh\": \"AQA984TVnm3BUh3eVBNk7-BTH2UCW_rej2ZClBSXjnGWoBD4ovr5V0T8z8jKbTDGwPemKXOGpzE-Kr2bOcipnJQsXTuioo0SxSlI0Ufm2lBIYUKOWphG3lK6Tmw7B2Mkn0E\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"Rebecca Sam Verghese\",\n" +
                        "    \"token\": \"BQCLAApahFGXebZqUO4c-_4JdnLF2TqHFEOsgW2VDsGEs3oXV0PX1rmWoFCpiJYgxVOBW9mymIOamXetf-HH_L5DNajPd5alS4IXRcjzbv2VcNgl2I5N89blu7MxE5tlXw4IQpKciP5QQDvgI-yNeBC7mY74yhs7Zw-ScMGEEscU2cFUShxtvxecsQ\",\n" +
                        "    \"refresh\": \"AQCeca9kcvfog8c1ENToBtqOB2GwYOilo2N6fwRsRBQclxzeh83CYFObPgRE_Uq3F5hlOmvsTqG7bePEBlZmw2vTJf04USXwPRI9j3mbWInM6-GJAfX395cDW95ilVD1FqU\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"Sahana Ganesh\",\n" +
                        "    \"token\": \"BQAPTfLUjOhSVKLMscKgC_rnsr1DHykiLpAHW8tsYFDVZhs6ebuXrCaRpEoENtresNeV1t9RiTw16B-FtnL5WAh1xYxDsRDC63DTnFq1BFAZkLpw-W80AD5PfxxVGtCseKZwY9PJ0N4GzShJi989Q6Oco3G0DSEWfUjY_-TaInGNWM-l5sDY21tESjCEXQ\",\n" +
                        "    \"refresh\": \"AQArWylA7NGHBkmRztL-Fe5whykZyFjH15Xk1885in3uBFN8w6-lG6NwqrX6ZVGTZe3yfqs0wdEFsqUXiySwHVcI9aEVB8Mgwvl7CtirG33JlnCWfyEW_oOPzyaXyLiER4s\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"aniformer\",\n" +
                        "    \"token\": \"BQBCf8JdGIpz2lsTeTvTo7XIeKGHHaJQC5-q0eU3_I5xbTduBiYf-ljm-SRSHU_2UPYJ8bOavsF170xu9AsTvbup2O1uNQzE8XYr6LN2jZK1KuY9xyWflYG1vg0USDIxZTwgI-2QY8Rnv6gVCuPC23SUXYcCGVFy18gX\",\n" +
                        "    \"refresh\": \"AQCyh2I_wza5fD8B74TK6YBZURWx_QqIz_zrjDz03JaYO_GGAH7HABiqGhzs2343B8KFU15GVAPprRjWf8ABGuaWgHhjuDNocEVLtajD_f5xV0izRbJA24TZtmiyIo-69u4\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"cage.reaper420\",\n" +
                        "    \"token\": \"BQCzUDLx9eOKW0IqC9okPsOvXfi49f-S042-6QdOmynyoUouwNHk8jTOhRCUR5NjxJ6iGbaq_RcXzgsgUBl9diwrmbkktfI3TMAjUfLb21ArVZUBMpcTuVv_E36kTVeaNeNgfl89APYWltL7toSueEghsvqixZhAPpvZ0nn8_k3gIf8I70i5gz1OYXdeMKa6vOQYjTBln5jAAe330w8Ekt9x7PWkt-Lmv5JBdpGTy7Y_oLz4sw\",\n" +
                        "    \"refresh\": \"AQDvuPVwCh9QBT2Wr14QN3spJ_gnEoXANkAG3wbZULD0jslj_G6lrNiboL5EUigKqR3wx_ePz1hhCHturArKbf1I8tSh6FnjSXIDz649GGkMxVCkSMg7v1nqknUkSXtByVI\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"Ishaan Chaturvedi\",\n" +
                        "    \"token\": \"BQDNnItT8giMa4-jmKYcTYbA90wYWgtSlpTt9AHEuym0CE4pxF3r2orhE8tZhcNRhA2R1aWtLLk6zDMdjtYNatHCfvCVh0uQKyPKx20iS_s8O5Z0qwsPatR0dXzIPpKSqulQ6R091N11cUu-SoAZXWJ5nxL-3bEER6j9LVX5IQBTAAoqeWV2UNlEVGi4ZC39iChmC7ClI71vmTrHSa-CEYvafu2G979BtrGNnj496KWFD_NjZm8S-Yg6ts2-apG6F4pH\",\n" +
                        "    \"refresh\": \"AQDlWUs7jbRiMR5E6wmGRbA8wCxmoyeSj517XaUiJPEcyA5aMVzV3mwn9xuvUtBI3pMs8NX5oFQewWJxzHYc6BozLb9jH9opVFmZ8ins2v_3pXvNXkrOX_mS323egazKBFo\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"jai747banerjee\",\n" +
                        "    \"token\": \"BQAHV0eUz6GWp1l_q2BrnJbb8lFOSi4QWX7_h8ItZ8Em-TQ-UvqiMPmUd3auyt7AQJAwvgZBHwvT4OWhu1QLJRjbSLt3WOz52Bj3O51wqPPzxTL14ZQ2zEwAynyKaBxqOPKsJzeore_XWYaYs_AUR0ru43dWHkvv1qsxBxVv5GGohu8\",\n" +
                        "    \"refresh\": \"AQBsnOzqKHhVDWv_-tz_uQsnEtSaEfujw8UYWGexDZ_5gcHOs05o5puaUSN9kxkwYO6Y-pUIBA2wmnwMDYZVSiGjBIqAYhy8jYnKC6QQLVBJhYX2iVUpe6fqMg-EUTLOR4M\"\n" +
                        "  },\n" +
//                        "  {\n" +
//                        "    \"username\": \"periyalpa\",\n" +
//                        "    \"token\": \"BQAVO1y00JaYd1bWcxc4CFfOmVJoRI29FnVcdlQfQjtogWFo1vsiAASKZ6FJcI-YhbIbtWbGWOwiXqnWDXinVdYMt2I7rxiINB6o4Gp6_HB9n42TbVpEoKfoJ-BoiKb5yC59S7G8jZFeNzFpYSnHQXgEh00xriF_197J\",\n" +
//                        "    \"refresh\": \"AQD6P26f6Hlb5w3663Tfqcd1PV5ccHzX_W3fiz2wdf7CFCrwzhEmqRISSMLp0KMr2Q9rm-zVo-vAYLby8qwiDteXW6IkImIjCuR6HUyQqXtI3eUw0-K8ba07pWS-iHRSsBQ\"\n" +
//                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"smarak.poricha\",\n" +
                        "    \"token\": \"BQAgj6TTaJj9HRhmCokBlFa1NoLeesryKb5Yv7BQp2CeKFlSuvHWP0flInC5-_uf0ZgLFgOiGFXp-s6D6hwm344M1r_htahF9Tb1zKt7bfDshYxD9qdZ8DeDYvRn9TgQ5TE_EMQYoWmC-RTr9QPcwl-WM8eRKdZk2Wuc0_deVOU\",\n" +
                        "    \"refresh\": \"AQDDKQTYgpjke5M12YaUWYc_boxX1oPKxi_JZ18Dx1aqw7LqTjLlg-qAqXHroSwmSWnVrPri5X3RWihaHFFtMbOSnHNMBY6mZ0neYMiAyv2-0Zk9nh-RYnMVG_QUwSw6od4\"\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"username\": \"violentbunnies\",\n" +
                        "    \"token\": \"BQA3s0W_o5rPM01_uvJMtPQeuG3r16XKTxigyLhUzdbjndHa4O-cPea3JB9qGu8NU8Q9QgjO8RJYxkzGDG8Go0hm8dgv8_V4Kl0Ryf3nXZTmtJH8hpbJ2ml1uFBk99CTp3Nes-QxO-j80OCkdHtU-fxBzWEwkhdwCVmqVrgjy70\",\n" +
                        "    \"refresh\": \"AQAq27shtkXw_lR5JUpsk8dYC4W2HsjuqyvyqzZsthTjrPx9SN-DZeg40hL8vqvDiqJ6YBba4NIilLFVDq1SzNBtZbWSxWMy4aTHHClPVS98TbgC_hIK3hUeQhudtsZ0T2c\"\n" +
                        "  }\n" +
                        "]"
        )

        for (i in 0 until li.length())
            add(li.getJSONObject(i), db, TABLE_NAME)

        li.getJSONObject(0).remove("owner")
        add(li.getJSONObject(0), db, TABLE_NAME2)
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


    fun get(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("select $COL_1,$COL_2,$COL_3 from $TABLE_NAME2;", null)
    }

    fun get2(table: String, query:String? = null): Cursor {
        val db = this.writableDatabase
        if (query == null)
            return db.rawQuery("select * from $table;", null)
        else
            return db.rawQuery("select * from $table $query;", null)
    }


//    fun getUsers(): Cursor {
//        val db = this.writableDatabase
////        Cursor res = db.rawQuery("select " + COL_1 + " from " + TABLE_NAME +";", null);
//        return db.rawQuery("select * from users;", null)
//    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }


    fun change(user: String) {
        val db = this.writableDatabase
        db.execSQL("drop table if exists $TABLE_NAME2") // dont drop just update
        db.execSQL("create table $TABLE_NAME2 (username text primary key, token text, refresh text);")

        val res = db.rawQuery("select $COL_2,$COL_3 from $TABLE_NAME where username = '$user';", null)
        res.moveToFirst()

        val cv = ContentValues()
        cv.put(COL_1, user)
        cv.put(COL_2, res.getString(0))
        cv.put(COL_3, res.getString(1))
        db.insert(TABLE_NAME2, null, cv)
    }


    fun check(): Cursor {
        val db = this.writableDatabase
        val res = db.rawQuery("select $COL_1 from $TABLE_NAME where owner is not null;", null)

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
        val res = db.rawQuery("select message, sent from $TABLE_NAME5 where user = '$user';", null)
        return res
    }

    fun send(user: String, message: String, sent: Int) {
        val js = JSONObject()
        js.put("user", user)
        js.put("message", parse_message(message))
        js.put("sent", sent)
        add(js, this.writableDatabase, TABLE_NAME5)
    }



    companion object {

        val DATABASE_NAME = "users.db"
        val TABLE_NAME = "users"
        val TABLE_NAME2 = "current"
        val TABLE_NAME3 = "feed"
        val TABLE_NAME4 = "following"
        val TABLE_NAME5 = "messages"

        val COL_1 = "username"
        val COL_2 = "token"
        val COL_3 = "refresh"
        val COL_4 = "name"
        val COL_5 = "email"
        val COL_6 = "uri"
        val COL_7 = "owner"

        lateinit var li: JSONArray
    }
}
