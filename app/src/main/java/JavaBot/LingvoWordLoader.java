package JavaBot;

import JavaBot.resources.WordAndTranslate;
import JavaBot.resources.WordList;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LingvoWordLoader implements Loader {
    public enum Lang {
        RU("1049"),
        EN("1033");

        public final String idx;

        Lang(String idx) {
            this.idx = idx;
        }
    }

    private final String apiKey;
    private final Set<String> themes;
    private final OkHttpClient client;
    private String bearerToken;

    public LingvoWordLoader(String key) {
        apiKey = key;
        themes = Set.of("car");
        client = new OkHttpClient();
        try {
            setBearerToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBearerToken() throws IOException {
        var httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("developers.lingvolive.com")
                .addPathSegment("api")
                .addPathSegment("v1.1")
                .addPathSegment("authenticate")
                .build();
        var body = new FormBody.Builder().build();
        var headers = Headers.of("Authorization", "Basic " + apiKey);
        var request = new Request.Builder()
                .url(httpUrl)
                .post(body)
                .headers(headers)
                .build();

        var response = client.newCall(request).execute();
        bearerToken = Objects.requireNonNull(response.body()).string();
    }

    public List<WordAndTranslate> getWordsByPrefix(
            String prefix,
            String srcLang,
            String dstLang
    ) throws IOException {
        var httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("developers.lingvolive.com")
                .addPathSegment("api")
                .addPathSegment("v1")
                .addPathSegment("WordList")
                .addQueryParameter("prefix", prefix)
                .addQueryParameter("srcLang", srcLang)
                .addQueryParameter("dstLang", dstLang)
                .addQueryParameter("pageSize", "20")
                .build();

        var headers = Headers.of("Authorization", "Bearer " + bearerToken);

        var request = new Request.Builder()
                .url(httpUrl)
                .get()
                .headers(headers)
                .build();

        var response = client.newCall(request).execute();

        var gson = new Gson();
        var entity = gson.fromJson(response.body().string(), WordList.class);

        return List.of();
    }


    @Override
    public void load(Store store) {
        for (var theme : themes)
            try {
                var words = getWordsByPrefix(theme, Lang.EN.idx, Lang.RU.idx);
                store.add(theme, words);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}