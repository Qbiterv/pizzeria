package pl.auctane.mail.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class HtmlFileDto {
    private String filename;
    private File file;

    public HtmlFileDto(String filename, File file) {

        System.out.println(file.getAbsolutePath());
        System.out.println(file.getAbsoluteFile());
        System.out.println(file.canRead());


        if (!file.exists())
            throw new IllegalArgumentException("File is not a file");
        this.filename = filename;
        this.file = file;
    }
}
