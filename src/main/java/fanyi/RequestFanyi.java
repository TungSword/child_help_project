package fanyi;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author: tung
 * @date: 2024/4/1
 */
public class RequestFanyi {
    @Test
    public void trans() throws Exception {
        List<String> list = FileUtils.readLines(new File("./input/englishWorld.txt"), StandardCharsets.UTF_8);

        for (int i = 0; i < list.size(); i++) {
            String word = list.get(i);
            System.out.println(word + ":" + getTranslate(word));
            if (i > 10){
                return;
            }
        }
    }

    public static String getTranslate(String word) throws Exception {
        word = word.replaceAll(" ", "+");
        String url = "http://m.youdao.com/dict?le=eng&q=" + word;
        CloseableHttpClient client = HttpClients.createDefault(); //创建一个可关闭的客户端
        HttpGet hp = new HttpGet(url);//创建post方法
        //设置头信息
        hp.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        hp.setHeader("Accept-Encoding", "gzip, deflate");
        hp.setHeader("Accept-Language", "zh-cn");
        hp.setHeader("Connection", "keep-alive");
        hp.setHeader("Cookie", "___rl__test__cookies=1526297207528; JSESSIONID=abc8T9npcpuvChUlnzEnw; _yd_newbanner_day=14; OUTFOX_SEARCH_USER_ID_NCOO=1006420317.710858; OUTFOX_SEARCH_USER_ID=1297994902@220.180.56.52");
        hp.setHeader("Host", "m.youdao.com");
        hp.setHeader("Referer", "http://m.youdao.com");
        hp.setHeader("Upgrade-Insecure-Requests", "1");
        hp.setHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1");
        CloseableHttpResponse response = client.execute(hp);
        HttpEntity entity = response.getEntity();
        String web = EntityUtils.toString(entity, "UTF-8");
        //System.out.println(web);
        int i = web.indexOf("该词条暂未被收录");
        if (i == -1) {
            Document doc = Jsoup.parse(web, "utf-8");
            Elements con = doc.getElementsByTag("ul");


            return con.get(2).text();
        } else {
            return "该词条暂未被收录";

        }
    }

    @Test
    public void getWords() throws IOException {
        File path = new File("C:\\Users\\TungS\\Downloads\\Lingoes English");
        List<String> words = new ArrayList<>();
        for (File file : path.listFiles()) {
            if (file.isDirectory()) {
                for (File listFile : file.listFiles()) {
                    String word = listFile.getName().replace(".mp3", "");
                    words.add(word);
                }
            }
        }
        FileUtils.writeLines(new File("./input/englishWorld.txt"), words);
    }

    @Test
    public void createDir() {
        String path = "./output/english/";
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : str.toCharArray()) {
            new File(path + String.valueOf(c)).mkdirs();
        }
        System.out.println(str.length());
    }

    @Test
    public void downloadAudio() throws IOException {
        String url = "http://dict.youdao.com/dictvoice?type=0&audio=";
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        List<String> list = FileUtils.readLines(new File("./input/englishWorld.txt"), StandardCharsets.UTF_8);

        for (String word : list) {
            HttpGet httpGet = new HttpGet(url + word);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            System.out.println(response.toString());
            HttpEntity entity = response.getEntity();
            System.out.println(entity.getContentLength());
            System.out.println(entity.getContentType().getValue());
            String first = word.substring(0, 1).toUpperCase(Locale.ROOT);
            FileOutputStream fos = new FileOutputStream("./output/english/" + first + File.separator + word + ".mp3");
            fos.write(EntityUtils.toByteArray(entity));
            fos.flush();
            fos.close();
        }


    }


}
