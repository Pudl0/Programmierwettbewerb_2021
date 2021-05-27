import com.google.gson.GsonBuilder;
import de.pauhull.discordbot.webserver.WebServer;

public class WebServerTest {

    public static void main(String[] args) {
        new WebServer(8000, new GsonBuilder().setPrettyPrinting().create());
    }
}
