package fanyi;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
