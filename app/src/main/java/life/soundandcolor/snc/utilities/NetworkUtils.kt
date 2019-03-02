package life.soundandcolor.snc.utilities

import com.github.kittinunf.fuel.Fuel
import life.soundandcolor.snc.Analysis
import org.json.JSONObject
import timber.log.Timber
import java.net.URL

object NetworkUtils {

    fun getRequest(url: String, params: List<Pair<String, String>>?, token: String?, refresh: String, myDb: DatabaseHelper?): JSONObject? {

        val (_, response, _) = Fuel.get(url, params)
                .header("Authorization", "Bearer " + token)
                .response()

        val res = response.body().asString("application/json")
        if (res == "(empty)")
            return null
        var content = JSONObject(res)
        if (content.has("error")) {
            Timber.e("error " + content)
            val error = content.getJSONObject("error")
            var status = error.getInt("status")
            when (status) {
                401 -> {
                    if (error.getString("message").equals("Permissions missing")) {
//                    if (url.contains("current") == true) {
                        return null
                    }
                    else if (error.getString("message").equals("The access token expired")) {
                        var refresh_params = listOf("grant_type" to "refresh_token",
                                "refresh_token" to refresh,
                                "client_id" to "e6f5f053a682454ca4eb1781064d3881",
                                "client_secret" to "e4294f2365ec45c0be87671b0da16596")
                        return getRequest(url, params, refresh_token(refresh, refresh_params, myDb), refresh, null)
                    }
                }
                else ->
                    return null
            }
        }
        return content
    }

    fun refresh_token(refresh: String, params: List<Pair<String, String>>, myDb: DatabaseHelper?): String {

        val (_, response, _) = Fuel.post("https://accounts.spotify.com/api/token", params)
                .response()

        var content = JSONObject(response.body().asString("application/json"))
        var token = content.getString("access_token")
        Timber.e("refreshed_token " + token)
        myDb!!.update(token, refresh)
        return token
    }
}