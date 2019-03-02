package life.soundandcolor.snc.utilities

import android.content.Context
import life.soundandcolor.snc.utilities.Helper.to_timestamp

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object ParseUtils {

    @Throws(JSONException::class)
    fun getSimpleStringsFromJson(context: Context, json: JSONObject, id: String, user: String): JSONArray? {

        val myDb = DatabaseHelper(context)

        val parsedData: JSONArray

        parsedData = JSONArray()
        when (id) {
            "Top Artists" -> {

                val weatherArray = json.getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val dayForecast = weatherArray.getJSONObject(i)
                    val temp = JSONObject()
                    temp.put("name", dayForecast.getString("name"))
                    temp.put("url", dayForecast.getJSONObject("external_urls").getString("spotify"))
                    temp.put("pop", dayForecast.getString("popularity"))
                    temp.put("id", dayForecast.getString("id"))

                    if (dayForecast.getJSONArray("images").length() > 0)
                        temp.put("img", dayForecast.getJSONArray("images")
                                .getJSONObject(0).getString("url"))
                    else {
                        temp.put("img", "")
                    }
                    temp.put("type", "artist")
                    parsedData.put(i, temp)
                }
                return parsedData
            }

            "Following" -> {

                val weatherArray = json.getJSONObject("artists").getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val dayForecast = weatherArray.getJSONObject(i)
                    val temp = JSONObject()
                    temp.put("name", dayForecast.getString("name"))
                    temp.put("url", dayForecast.getJSONObject("external_urls").getString("spotify"))
                    temp.put("pop", dayForecast.getString("popularity"))
                    temp.put("id", dayForecast.getString("id"))
                    temp.put("genres", dayForecast.getJSONArray("genres"))
                    if (dayForecast.getJSONArray("images").length() > 0)
                        temp.put("img", dayForecast.getJSONArray("images")
                                .getJSONObject(0).getString("url"))
                    else
                        temp.put("img", "")
                    temp.put("type", "artist")
                    parsedData.put(i, temp)

                    temp.put("user", user)
                    myDb.add(temp, myDb.writableDatabase, "following")
                }
                return parsedData
            }

            "Top Tracks" -> {

                val weatherArray = json.getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val track = weatherArray.getJSONObject(i)
                    val temp = JSONObject()
                    temp.put("name", track.getString("name"))
                    temp.put("id", track.getString("id"))
                    temp.put("url", track.getJSONObject("external_urls").getString("spotify"))
                    temp.put("album", track.getJSONObject("album").getString("name"))
                    temp.put("album_id", track.getJSONObject("album").getString("id"))
                    temp.put("img", track.getJSONObject("album").getJSONArray("images")
                            .getJSONObject(0).getString("url"))
                    temp.put("artist", track.getJSONObject("album").getJSONArray("artists")
                            .getJSONObject(0).getString("name"))
                    temp.put("artist_id", track.getJSONObject("album").getJSONArray("artists")
                            .getJSONObject(0).getString("id"))
                    temp.put("duration", track.getInt("duration_ms"))
                    temp.put("type", "track")
                    parsedData.put(i, temp)
                }
                return parsedData
            }
            "Recent" -> {
                val weatherArray = json.getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val dayForecast = weatherArray.getJSONObject(i)
//                    val dayForecast = weatherArray.getJSONObject(weatherArray.length() - i - 1)

                    val track = dayForecast.getJSONObject("track")
                    val temp = JSONObject()
                    temp.put("name", track.getString("name"))
                    temp.put("id", track.getString("id"))
                    temp.put("url", track.getJSONObject("external_urls").getString("spotify"))
                    temp.put("album", track.getJSONObject("album").getString("name"))
//                    temp.put("album_id", track.getJSONObject("album").getString("id"))
                    temp.put("img", track.getJSONObject("album").getJSONArray("images")
                            .getJSONObject(0).getString("url"))
                    temp.put("artist", track.getJSONObject("album").getJSONArray("artists")
                            .getJSONObject(0).getString("name"))
//                    temp.put("artist_id", track.getJSONObject("album").getJSONArray("artists")
//                            .getJSONObject(0).getString("id"))
                    temp.put("duration", track.getInt("duration_ms"))
                    temp.put("timestamp", to_timestamp(dayForecast.getString("played_at")))
                    temp.put("type", "track")
                    parsedData.put(i, temp)

                    temp.put("user", user)
                    myDb.add(temp, myDb.writableDatabase, "feed")
                }
                return parsedData
            }
            "Saved Tracks" -> {
                val weatherArray = json.getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val dayForecast = weatherArray.getJSONObject(i)
                    val track = dayForecast.getJSONObject("track")
                    val temp = JSONObject()
                    temp.put("name", track.getString("name"))
                    temp.put("url", track.getJSONObject("external_urls").getString("spotify"))
                    temp.put("album", track.getJSONObject("album").getString("name"))
                    temp.put("album_id", track.getJSONObject("album").getString("id"))
                    temp.put("img", track.getJSONObject("album").getJSONArray("images")
                            .getJSONObject(0).getString("url"))
                    temp.put("artist", track.getJSONObject("album").getJSONArray("artists")
                            .getJSONObject(0).getString("name"))
                    temp.put("artist_id", track.getJSONObject("album").getJSONArray("artists")
                            .getJSONObject(0).getString("id"))
                    temp.put("duration", track.getInt("duration_ms"))
                    temp.put("type", "track")
                    parsedData.put(i, temp)
                }
                return parsedData
            }

            "Saved Albums" -> {
                val weatherArray = json.getJSONArray("items")
                for (i in 0 until weatherArray.length()) {

                    val dayForecast = weatherArray.getJSONObject(i)

                    val a = dayForecast.getJSONObject("album")
                    val temp = JSONObject()
                    temp.put("name", a.getString("name"))
                    temp.put("url", a.getJSONObject("external_urls").getString("spotify"))
                    temp.put("id", a.getString("id"))
                    temp.put("release", a.getString("release_date"))
                    temp.put("pop", a.getJSONArray("artists")
                            .getJSONObject(0).getString("name"))
                    temp.put("artist_id", a.getJSONArray("artists")
                            .getJSONObject(0).getString("id"))
                    temp.put("img", a.getJSONArray("images").getJSONObject(0)
                            .getString("url"))
                    temp.put("type", "artist")
                    parsedData.put(i, temp)
                }
                return parsedData
            }
            else -> return null
        }
    }
}