package de.pauhull.discordbot.util;

import delight.fileupload.FileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FormData {

    public static BufferedImage readImage(InputStream input, String contentType) throws IOException {

        byte[] bytes = IOUtils.toByteArray(input);
        List<FileItem> fileItems = FileUpload.parse(bytes, contentType);

        for (FileItem fileItem : fileItems) {
            if (!fileItem.isFormField() && fileItem.getContentType().startsWith("image/")) {
                return ImageIO.read(fileItem.getInputStream());
            }
        }

        return null;
    }
}