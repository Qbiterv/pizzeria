package pl.auctane.mail.dtos.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
public class HtmlFileDto {
    private String filename;
    private Resource file;

    public HtmlFileDto(String filename, Resource file) {
        this.filename = filename;
        this.file = file;
    }
}
