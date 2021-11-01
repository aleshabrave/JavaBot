package JavaBot;

import JavaBot.db.MongoDBOperator;
import JavaBot.resources.Config;
import com.google.gson.Gson;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String [] args) {
        try {
            var config = getConfig();

            /* Это для того, чтобы дебажить(delete comments)
            var s = new WordStore();
            var a = new WordSimilarityLoader(config.extKey);
            a.load(s);
            */
            var operator = new MongoDBOperator(config.uriMongoDB, config.dbName);
            var store = new WordStore();
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(
                    new EnglishWordStudyBot(
                            config.token,
                            config.botName,
                            operator,
                            store
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Config getConfig() throws IOException, URISyntaxException {
        var configURL = ClassLoader.getSystemClassLoader().getResource("config.json");
        assert configURL != null;
        var file = new File(configURL.toURI());
        var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        var gson = new Gson();
        return gson.fromJson(String.join("", lines), Config.class);
    }
}
