package proyecto.clases;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class LinkPreview {
    private String url;

    public LinkPreview(String url) {
        this.url = url;
    }

    public void generatePreview() {
        try {
            Document doc = Jsoup.connect(url).get();
            Element metaOgImage = doc.select("meta[property=og:image]").first();
            if (metaOgImage != null) {
                String content = metaOgImage.attr("content");
                System.out.println("Open Graph Image: " + content);
            }
            // Extract other Open Graph tags similarly
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}