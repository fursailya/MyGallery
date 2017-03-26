package gallery.app.fursa.mygallery;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Fursa Ilya on 26.03.2017.
 */

public class FlickFetcher {
    public static final String TAG = "FlickFetcher";
    public static final String API_KEY = "d8668e55578ee2d3164bd25b6e3e066e";

    public String getJSON(String urlSpec) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlSpec)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String answer = response.body().string();
        return answer;
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getJSON(url);
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItems(galleryItems, jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return galleryItems;
    }

    private void parseItems(List<GalleryItem> galleryItemList, JSONObject jsonObject)
            throws IOException, JSONException {
        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if(!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            galleryItemList.add(item);
        }

    }

}
