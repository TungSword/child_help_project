package walker;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import pinyin.CharPinyin;
import pinyin.PinyinMain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: tung
 * @date: 2024/3/20
 */
public class PoetryAddPinyin {
    public static void main(String[] args) throws IOException {
        File path = new File("./input/poetry");
        for (File file : path.listFiles()) {
            addPinyin(file);
        }
    }

    private static void addPinyin(File file) throws IOException {
        String name = file.getName();
        String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        List<PoetryEntity> poetryEntities = JSON.parseArray(json, PoetryEntity.class);
        for (PoetryEntity poetryEntity : poetryEntities) {
            List<CharPinyin> pinyinName = PinyinMain.convertChineseToPinyin(poetryEntity.getName());
            poetryEntity.setPinyinName(pinyinName);
            StringBuilder stb = new StringBuilder();
            for (String content : poetryEntity.getContents()) {
                stb.append(content).append("|");
            }
            while (stb.toString().contains("(") || stb.toString().contains(")")) {
                int i = stb.indexOf("(");
                int j = stb.indexOf(")");
                stb = new StringBuilder(stb.substring(0, i) + stb.substring(j + 1, stb.length()));
            }

            poetryEntity.setContents(new ArrayList<>());
            for (String content : stb.toString().split("\\|")) {
                if (StringUtils.isNotEmpty(content)) {
                    poetryEntity.getContents().add(content);
                }
            }

            for (String content : poetryEntity.getContents()) {
                List<CharPinyin> pinyinContent = PinyinMain.convertChineseToPinyin(content);
                poetryEntity.getPinyinContent().add(pinyinContent);
            }

            List<String> translations = new ArrayList<>();
            for (String line : poetryEntity.getTranslation()) {
                if (!line.equals("参考资料：完善")) {
                    translations.add(line);
                }
            }
            poetryEntity.setTranslation(translations);
        }

        FileUtils.writeStringToFile(new File("./pinyin古诗词/" + name), JSON.toJSONString(poetryEntities));
    }
}
