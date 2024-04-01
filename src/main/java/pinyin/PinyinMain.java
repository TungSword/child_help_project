package pinyin;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tung
 * @date: 2024/2/28
 */
public class PinyinMain {
    private static final String START = "START";
    private static final String END = "END";
    private static final int START_MODE = 0;
    private static final int CREATE_MODE = 1;
    private static final int UPDATE_NAME_MODE = 2;
    private static final int UPDATE_CONTENT_MODE = 3;
    private static final int FINISH_MODE = 4;

    private static int selectMode(String line, int mode) {
        if (START.equals(line)) {
            return CREATE_MODE;
        }
        if (END.equals(line)) {
            return FINISH_MODE;
        }
        if (mode == CREATE_MODE) {
            return UPDATE_NAME_MODE;
        }
        if (mode == UPDATE_NAME_MODE) {
            return UPDATE_CONTENT_MODE;
        }
        if (mode == FINISH_MODE) {
            return START_MODE;
        }
        return mode;
    }

    public static void main(String[] args) throws IOException {
        List<String> list = FileUtils.readLines(new File("./input/奥特曼故事.txt"));
        List<Story> storyList = new ArrayList<>();
        Story story = null;

        int mode = 0;
        for (String line : list) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            mode = selectMode(line, mode);
            switch (mode) {
                case CREATE_MODE:
                    story = new Story();
                    break;
                case UPDATE_NAME_MODE:
                    story.setName(line);
                    break;
                case UPDATE_CONTENT_MODE:
                    story.getStory().add(line);
                    break;
                case FINISH_MODE:
                    storyList.add(story);
                    break;
            }
        }

        List<StoryPinyin> storyPinyins = new ArrayList<>();
        int index = 0;
        for (Story story1 : storyList) {
            StoryPinyin storyPinyin = convertStoryPinyin(story1);
            storyPinyin.setId(index);
            storyPinyins.add(storyPinyin);
            index++;
        }
        FileUtils.writeStringToFile(new File("./output/奥特曼故事.json"), JSON.toJSONString(storyPinyins));
    }

    private static Map<String, List<String>> getPinyinMap() throws IOException {
        String string = FileUtils.readFileToString(new File("./config/pinyin.json"));
        PinyinData pinyinData = JSON.parseObject(string, PinyinData.class);
        Map<String, List<String>> pinyinMap = new HashMap<>();
        for (Pinyin pinyin : pinyinData.getPinyin()) {
            pinyinMap.put(pinyin.getLabel(), pinyin.getValue());
        }
        return pinyinMap;
    }

    private static StoryPinyin convertStoryPinyin(Story story) throws IOException {
        StoryPinyin storyPinyin = new StoryPinyin();
        List<CharPinyin> name = convertChineseToPinyin(story.getName());
        storyPinyin.setName(name);
        for (String content : story.getStory()) {
            List<CharPinyin> storyContent = convertChineseToPinyin(content);
            storyPinyin.getContent().add(storyContent);
        }
        return storyPinyin;
    }

    /**
     * 转换
     *
     * @param content
     * @return
     * @throws IOException
     */
    public static List<CharPinyin> convertChineseToPinyin(String content) throws IOException {
        Map<String, List<String>> pinyinMap = getPinyinMap();
        List<CharPinyin> charPinyins = new ArrayList<>();
        char[] chars = content.toCharArray();
        String chineseRegex = "[\\u4e00-\\u9fff]";
        String NumberOrEnglishRegex = "[a-zA-Z0-9]";
        boolean isChinese = false;
        boolean isNumberOrEnglish = false;
        StringBuilder numEngStr = new StringBuilder();
        boolean isFirstChar = false;
        String firstChar = "";
        for (int i = 0; i < chars.length; i++) {
            char cChar = chars[i];

            // 英文或数字拼接
            isNumberOrEnglish = String.valueOf(cChar).matches(NumberOrEnglishRegex);
            if (isNumberOrEnglish) {
                numEngStr.append(cChar);
                continue;
            } else if (numEngStr.length() > 0) {
                charPinyins.add(CharPinyin.builder().name(String.valueOf(numEngStr)).build());
                numEngStr = new StringBuilder();
                continue;
            }

            isChinese = String.valueOf(cChar).matches(chineseRegex);

            if (isChinese || String.valueOf(cChar).equals("㸌")) {
                if (String.valueOf(cChar).equals("㸌")){
                    charPinyins.add(CharPinyin.builder().name(String.valueOf(cChar)).pinyin("huò").build());
                }else {
                    String charPinyin = PinyinUtil.convertToPinyin(String.valueOf(cChar));
                    String cPinyin = convertPinyin(charPinyin, pinyinMap);
                    charPinyins.add(CharPinyin.builder().name(String.valueOf(cChar)).pinyin(cPinyin).build());
                }
                if (isFirstChar){
                    isFirstChar = false;
                    int size = charPinyins.size();
                    CharPinyin prePinyin = charPinyins.get(size - 1);
                    prePinyin.setName(firstChar + prePinyin.getName());

                }

            } else if (charPinyins.size() > 0) {
                int size = charPinyins.size();
                CharPinyin prePinyin = charPinyins.get(size - 1);
                prePinyin.setName(prePinyin.getName() + cChar);
            } else {
                isFirstChar = true;
                firstChar = String.valueOf(cChar);
//                throw new NullPointerException();
            }
        }

        return charPinyins;
    }


    private static String convertPinyin(String pinyin, Map<String, List<String>> pinyinMap) {
        int shendiao = Integer.parseInt(pinyin.substring(pinyin.length() - 1));
        String subPinyin = pinyin.substring(0, pinyin.length() - 1);
        for (int i = 0; i < subPinyin.length(); i++) {
            String key = subPinyin.substring(i);
            List<String> list = pinyinMap.get(key);
            if (list != null && list.size() != 0) {
                if (shendiao > list.size()) {
                    return subPinyin.substring(0, i) + key;
                }
                return subPinyin.substring(0, i) + list.get(shendiao - 1);
            }
        }

        return pinyin;
    }
}
