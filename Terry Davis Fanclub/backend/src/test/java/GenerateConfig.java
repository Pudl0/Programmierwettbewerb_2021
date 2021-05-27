import com.google.gson.GsonBuilder;
import de.pauhull.discordbot.Config;

public class GenerateConfig {

    public static void main(String[] args) {
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(new Config()));
    }
}
