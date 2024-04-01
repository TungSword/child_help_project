package englishstandard;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: tung
 * @date: 2024/3/19
 */
public class Main {
    public static void main(String[] args) throws IOException {
        List<String> list = FileUtils.readLines(new File("./input/英标.txt"), StandardCharsets.UTF_8);
        List<EnglishStandards> standards = new ArrayList<>();
        EnglishStandards englishStandard = null;
        for (String line : list) {
            if (line.endsWith(",")){
                englishStandard = new EnglishStandards();
                standards.add(englishStandard);
                englishStandard.setType(line.substring(0, line.length() - 1));
                continue;
            }
            String[] split = line.split(",");
            EngStandard engStandard = new EngStandard();
            engStandard.setIndex(NumberUtils.toInt(split[0]));
            engStandard.setName(split[1]);
            englishStandard.getData().add(engStandard);
        }

        System.out.println(JSON.toJSONString(standards));
    }
}
