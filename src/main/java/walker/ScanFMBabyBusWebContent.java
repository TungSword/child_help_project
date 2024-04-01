package walker;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author: tung
 * @date: 2024/3/5
 */
public class ScanFMBabyBusWebContent {
    private static final String FILTER_CLASS_NAME = "clearfix";
    private static final String DEFAULT_HTML_URL = "https://www.lizhi.fm/user/2537510764592001580/p/%s.html";
    private static final String DEFAULT_MP3_URL = "curl -o %s.mp3 http://cdn5.lizhi.fm/audio/2020/02/12/%s_hd.mp3";
    private static final String DEFAULT_MP3_URL2 = "curl -o %s.mp3 http://cdn5.lizhi.fm/audio/2020/02/11/%s_hd.mp3";


    public static void main(String[] args) throws Exception {
        Map<String, String> result = new HashMap<>();
        for (int i = 26; i <= 38; i++) {
            String url = String.format(DEFAULT_HTML_URL, i);
            String webContent = GetWebContent.getWebContent(url);

            Document document = Jsoup.parse(webContent);
            Elements select = document.select("a[href]");
            List<Element> filterElement = new ArrayList<>();
            for (Element element : select) {
                Set<String> classNames = element.classNames();
                if (classNames.contains(FILTER_CLASS_NAME)){
                    filterElement.add(element);
                }
            }
            for (Element element : filterElement) {
                String href = element.attr("href");
                String title = element.attr("title");
                result.put(title, href);
            }
        }

        FileUtils.writeStringToFile(new File("宝宝巴士.json"), JSON.toJSONString(result), StandardCharsets.UTF_8);

        List<String> downloadUrl = new ArrayList<>();

        Map<String, String> mp3Map = new HashMap<>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            String title = entry.getKey();
            String href = entry.getValue();
            String[] split = href.split("/");
            String id = split[split.length - 1];
            downloadUrl.add(String.format(DEFAULT_MP3_URL, id, id));
            downloadUrl.add(String.format(DEFAULT_MP3_URL2, id, id));
            mp3Map.put(title, id);
        }
        FileUtils.writeStringToFile(new File("宝宝巴士Map.json"), JSON.toJSONString(mp3Map), StandardCharsets.UTF_8);
        FileUtils.writeLines(new File("download.txt"), downloadUrl);


    }
}
