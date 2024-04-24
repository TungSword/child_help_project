package fanyi;

import com.alibaba.fastjson.JSON;
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
import phonics.entity.Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author: tung
 * @date: 2024/4/1
 */
public class RequestFanyi {
    public static void main(String[] args) throws Exception {
        System.out.println(JSON.toJSONString(getTranslate("Tibet")));
        System.out.println(JSON.toJSONString(getTranslate("tink")));
    }

    public static Word getTranslate(String word) throws Exception {
        word = word.replaceAll(" ", "+");
        String url = "http://m.youdao.com/dict?le=eng&q=" + word;
        //创建一个可关闭的客户端
        CloseableHttpClient client = HttpClients.createDefault();
        //创建post方法
        HttpGet hp = new HttpGet(url);
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
        int i = web.indexOf("该词条暂未被收录");
        if (i == -1) {
            Document doc = Jsoup.parse(web, "utf-8");
            Elements con = doc.getElementsByTag("ul");
            Elements phonetic = doc.getElementsByClass("phonetic");
            String chinese = con.get(2).text();
            int size = phonetic.size();
            List<String> englishStandards = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                englishStandards.add(phonetic.get(j).parent().text());
            }
            return new Word(word, chinese, englishStandards);
        } else {
            return null;

        }
    }

    public static String getTranslateStr(String word) throws Exception {
        Word translate = getTranslate(word);
        System.out.println(JSON.toJSONString(translate));
        if (translate == null) {
            return "该词条暂未被收录";
        }
        return translate.getChinese();
    }

    public static boolean downloadAudio(String word, String path) {
        String backupPath = getBackupPath(word);
        try {
            String url = "http://dict.youdao.com/dictvoice?type=0&audio=";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            System.out.println("error:"+ word);
            HttpGet httpGet = new HttpGet(url + word);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(EntityUtils.toByteArray(entity));
            fos.flush();
            fos.close();
            FileUtils.copyFile(new File(path), new File(backupPath));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyPath(String word, String path) throws IOException {
        String backupPath = getBackupPath(word);
        File backUpAudio = new File(backupPath);
        if (backUpAudio.exists()) {
            FileUtils.copyFile(new File(backupPath), new File(path));
            return true;
        }
        return false;
    }

    private static String getBackupPath(String word) {
        String first = word.substring(0, 1).toUpperCase(Locale.ROOT);
        return "./output/english/" + first + File.separator + word + ".mp3";
    }


}
