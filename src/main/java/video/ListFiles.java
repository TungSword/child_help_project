package video;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: tung
 * @date: 2024/4/9
 */
public class ListFiles {
    private static final String PENELOPE_DEFAULT_URL = "//player.bilibili.com/player.html?aid=248463989&bvid=BV11v411V75C&danmaku=false&muted=false&autoplay=true&p=";
    private static final String GIGANTOSAURUS_DEFAULT_URL = "//player.bilibili.com/player.html?aid=699548502&bvid=BV1om4y1i7fU&danmaku=false&muted=false&autoplay=true&p=";

    private static final String TRADITIONAL_OPERA_URL = "//player.bilibili.com/player.html?aid=97474351&bvid=BV1R7411R7uh&danmaku=false&muted=false&autoplay=true&p=";
    //<iframe src="//player.bilibili.com/player.html?aid=97474351&bvid=BV1R7411R7uh&p=1" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"> </iframe>

    @Test
    public void traditionalOpera() throws IOException {
        List<String> list = FileUtils.readLines(new File("./input/traditionalOpera.txt"), StandardCharsets.UTF_8);
        List<VideoId> videos = new ArrayList<>();
        int index = 1;
        for (String line : list) {
            if (StringUtils.isNotEmpty(line)) {
                videos.add(new VideoId(line.trim(), index, TRADITIONAL_OPERA_URL + index));
                index++;
            }
        }
        System.out.println(JSON.toJSONString(videos));
    }

    public static void main(String[] args) {
        File file = new File("D:\\小於同学\\penelope蓝色小考拉");
        List<VideoId> videos = new ArrayList<>();
        int index = 1;
        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            String nameSub = name.substring("Penelope_13.".length()).replace(".mp4", "").replace("_", " ");
            videos.add(new VideoId(nameSub, index, PENELOPE_DEFAULT_URL + index));
            index++;
        }
        System.out.println(JSON.toJSONString(videos));
    }

    @Test
    public void readFile() throws IOException {
        List<String> list = FileUtils.readLines(new File("./input/video.txt"), StandardCharsets.UTF_8);
        List<VideoId> videos = new ArrayList<>();
        int index = 1;
        for (String line : list) {
            if (StringUtils.isNotEmpty(line)) {
                videos.add(new VideoId(line.trim(), index, GIGANTOSAURUS_DEFAULT_URL + index));
                index++;
            }
        }
        System.out.println(JSON.toJSONString(videos));
    }
}
