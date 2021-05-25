package de.pauhull.discordbot.api.handlers;

import com.sun.net.httpserver.HttpExchange;
import de.pauhull.discordbot.api.Api;
import de.pauhull.discordbot.api.response.SuccessResponse;
import de.pauhull.discordbot.api.response.UnauthorizedResponse;
import de.pauhull.discordbot.bot.DiscordBot;
import de.pauhull.discordbot.util.FormData;
import de.pauhull.discordbot.webserver.session.Session;
import discord4j.rest.http.client.ClientException;
import discord4j.rest.util.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BotImageHandler implements RequestHandler {

    @Override
    public String handleRequest(String[] path, Session session, HttpExchange httpExchange, Api api) {

        if (!session.isAuthorized()) {
            return api.getGson().toJson(new UnauthorizedResponse());
        }

        if (httpExchange.getRequestMethod().equals("POST")) {

            try {
                BufferedImage image = FormData.readImage(httpExchange.getRequestBody(), httpExchange.getRequestHeaders().getFirst("Content-Type"));

                if (image == null) {
                    return api.getGson().toJson(new SuccessResponse(false));
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, "png", output);
                output.flush();
                output.close();

                try {
                    DiscordBot.getInstance().getClient().edit(spec -> {
                        spec.setAvatar(Image.ofRaw(output.toByteArray(), Image.Format.PNG));
                    }).block();
                    return api.getGson().toJson(new SuccessResponse(true));

                } catch (ClientException ignored) {
                    return api.getGson().toJson(new SuccessResponse(false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return api.getGson().toJson(new SuccessResponse(false));
    }
}
