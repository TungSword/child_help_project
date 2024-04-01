package walker;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tung
 * @date: 2024/3/13
 */
public class ScanPoetry {
    static Map<String, String> poetryMap = new HashMap<>();

    private static final String MORE_URL = "https://so.gushiwen.cn/nocdn/ajaxfanyi.aspx?id=%s&idjm=%s";

    private static final String DEFAULT_URL = "https://so.gushiwen.cn";

    static {
        poetryMap.put("小学古诗文", "https://so.gushiwen.cn/gushi/xiaoxue.aspx");
        poetryMap.put("小学文言文", "https://so.gushiwen.cn/wenyan/xiaowen.aspx");
        poetryMap.put("初中古诗文", "https://so.gushiwen.cn/gushi/chuzhong.aspx");
        poetryMap.put("初中文言文", "https://so.gushiwen.cn/wenyan/chuwen.aspx");
        poetryMap.put("高中古诗文", "https://so.gushiwen.cn/gushi/gaozhong.aspx");
        poetryMap.put("高中文言文", "https://so.gushiwen.cn/wenyan/gaowen.aspx");
        poetryMap.put("唐诗三百首", "https://so.gushiwen.cn/gushi/tangshi.aspx");
        poetryMap.put("宋词三百首", "https://so.gushiwen.cn/gushi/songsan.aspx");
        poetryMap.put("古诗三百首", "https://so.gushiwen.cn/gushi/sanbai.aspx");
        poetryMap.put("宋词精选", "https://so.gushiwen.cn/gushi/songci.aspx");
        poetryMap.put("诗经", "https://so.gushiwen.cn/gushi/shijing.aspx");

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ScanPoetry scanPoetry = new ScanPoetry();
        List<UrlEntity> urlEntityList = scanPoetry.getUrlEntityList();
        for (UrlEntity urlEntity : urlEntityList) {
            String name = urlEntity.getName();
            System.out.println(name + " start");
            List<PoetryEntity> poetryEntities = new ArrayList<>();
            for (String url : urlEntity.getUrls()) {
                try {
                    PoetryEntity poetryEntity = scanPoetry.parsePoetry(url);
                    poetryEntities.add(poetryEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println(name + "finish");
            FileUtils.writeStringToFile(new File("./output/" + name + ".json"), JSON.toJSONString(poetryEntities));
        }
    }

    private PoetryEntity parsePoetry(String url) throws Exception {
        PoetryEntity poetryEntity = new PoetryEntity();
        String[] urlSplit = url.split("_");
        String key = urlSplit[urlSplit.length - 1].split("\\.")[0];

        String webContent = GetWebContent.getWebContent(url);
        Document document = Jsoup.parse(webContent);
        Element titleElement = document.getElementById("zhengwen" + key);
//        名称
        poetryEntity.setName(titleElement.select("h1").text());
        System.out.println(poetryEntity.getName());
//        作者，朝代
        Elements infos = titleElement.select("a");
        for (int i = 0; i < infos.size(); i++) {
            if (i == 0) {
                poetryEntity.setAuthor(infos.get(i).text());
            }
            if (i == 1) {
                poetryEntity.setDynasty(infos.get(i).text());
            }
        }
// 内容
        Element contentElement = document.getElementById("contson" + key);
        StringBuilder stb = new StringBuilder();
        for (String line : contentElement.text().split(" ")) {
            stb.append(line).append(" ");
        }
        int start = stb.indexOf("(");
        int end = stb.indexOf(")");
        StringBuilder result = new StringBuilder();
        if (start > 0) {
            result.append(stb.substring(0, start));
        } else {
            result.append(stb);
        }
        if (end < stb.length() && end > 0) {
            result.append(stb.substring(end + 1, stb.length()));
        }
        for (String line : result.toString().split(" ")) {
            poetryEntity.getContents().add(replace(line.trim()));
        }
//译文
        Elements translations = document.getElementsByClass("contyishang");
        for (Element translation : translations) {
            String content = translation.select("div").text();
            if (content.contains("译文及注释")) {
                boolean searchMore = false;
                Elements aTags = translation.select("a");
                for (Element aTag : aTags) {
                    if (aTag.text().startsWith("展开阅读全文")) {
                        searchMore = true;
                        String href = aTag.attr("href");
                        String[] params = href.replace("javascript:fanyiShow(", "")
                                .replace(")", "")
                                .replace("'", "")
                                .split(",");

                        String moreUrl = String.format(MORE_URL, params[0], params[1]);

                        String moreContent = GetWebContent.getWebContent(moreUrl);
                        Document moreDoc = Jsoup.parse(moreContent);
                        for (Element p : moreDoc.select("p")) {
                            poetryEntity.getTranslation().add(replace(p.text()));
                        }
                    }
                }


                if (!searchMore) {
                    for (Element p : translation.select("p")) {
                        poetryEntity.getTranslation().add(replace(p.text()));
                    }
                }

            }
        }
//        System.out.println(JSON.toJSONString(poetryEntity));
        return poetryEntity;
    }

    public String replace(String line) {
        String replace = line.replace(" ", "")
                .replace("　", "")
                .replace("▲", "");
        if (replace.startsWith("译文")) {
            return replace.replace("译文", "译文：");
        }
        if (replace.startsWith("注释")) {
            return replace.replace("注释", "注释：");
        }
        return replace;
    }

    public List<UrlEntity> getUrlEntityList() throws Exception {
        List<UrlEntity> urlEntityList = new ArrayList<>();
        // 获取url列表
        for (Map.Entry<String, String> entry : poetryMap.entrySet()) {
            String name = entry.getKey();
            UrlEntity urlEntity = new UrlEntity();
            urlEntityList.add(urlEntity);
            urlEntity.setName(name);
            String url = entry.getValue();
            String webContent = GetWebContent.getWebContent(url);
            Document document = Jsoup.parse(webContent);
            Elements elements = document.select("div.typecont");
            for (Element element : elements) {
                Elements links = element.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    if (linkHref.startsWith(DEFAULT_URL)) {
                        urlEntity.getUrls().add(linkHref);
                    } else {
                        urlEntity.getUrls().add(DEFAULT_URL + linkHref);
                    }
                }
            }
        }
        return urlEntityList;
    }
}
