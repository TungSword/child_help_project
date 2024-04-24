package phonics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fanyi.RequestFanyi;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import phonics.entity.Phonics;
import phonics.entity.Word;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: tung
 * @date: 2024/4/23
 */
public class ParsePhonics {
    private static final String PHONICS_LETTER = "LETTER";
    private static final String PHONICS_STANDARD = "STANDARD";
    private static final String PHONICS_TYPE = "TYPE";
    private static final String PHONICS_AUDIO = "AUDIO";
    private static final String PHONICS_BASE_WORDS = "BASEWORDS";
    private static final String PHONICS_MORE_WORDS = "MOREWORDS";

    private final Map<String, Word> wordMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ParsePhonics parsePhonics = new ParsePhonics();
        parsePhonics.parseResultJson();
        parsePhonics.parse();
    }

    public void parseResultJson() throws IOException {

        for (int i = 1; i <= 8; i++) {
            String path = "./output/phonics/json/" + i + ".json";
            if (!new File(path).exists()) {
                continue;
            }
            String json = FileUtils.readFileToString(new File(path));
            List<Phonics> list = JSON.parseArray(json, Phonics.class);
            for (Phonics phonics : list) {
                List<Word> moreWords = phonics.getMoreWords();
                if (moreWords != null && moreWords.size() > 0) {
                    for (Word moreWord : moreWords) {
                        String word = moreWord.getWord();
                        String audioPath = getAudioPath(word);
                        moreWord.setAudio(audioPath);
                        wordMap.put(moreWord.getWord(), moreWord);
                    }
                }
            }
        }
    }

    public void parse() throws IOException {
        for (int i = 1; i <= 8; i++) {
            List<Phonics> phonicsList = new ArrayList<>();
            List<String> lines = FileUtils.readLines(new File("./input/phonics/" + i + ".txt"), StandardCharsets.UTF_8);
            String titles = lines.remove(0);
            Map<Integer, String> titleMap = parseTitle(titles);
            for (String line : lines) {
                if (StringUtils.isNotEmpty(line)) {
                    phonicsList.add(parseLine(titleMap, line));
                }
            }
            FileUtils.writeStringToFile(new File("./output/phonics/json/" + i + ".json"), JSONObject.toJSON(phonicsList).toString());
            System.out.println(JSONObject.toJSON(phonicsList).toString());
        }
    }

    private Phonics parseLine(Map<Integer, String> titleMap, String line) {
        Phonics phonics = new Phonics();
        String[] split = line.split("\\|");
        for (int i = 0; i < split.length; i++) {
            String key = titleMap.get(i);
            setPhonics(phonics, key, split[i]);
        }
        Set<String> baseWords = phonics.getBaseWords().stream().map(Word::getWord).collect(Collectors.toSet());
        List<Word> moreWords = phonics.getMoreWords();
        List<Word> newMoreWords = new ArrayList<>();
        for (Word moreWord : moreWords) {
            if (!baseWords.contains(moreWord.getWord())) {
                newMoreWords.add(moreWord);
            }
        }
        phonics.setMoreWords(newMoreWords);
        return phonics;
    }

    private void setPhonics(Phonics phonics, String key, String value) {
        switch (key) {
            case PHONICS_LETTER:
                phonics.setLetter(value);
                break;
            case PHONICS_STANDARD:
                phonics.setStandard(value);
                break;
            case PHONICS_TYPE:
                phonics.setType(value);
                break;
            case PHONICS_AUDIO:
                phonics.setAudio(value);
                break;
            case PHONICS_BASE_WORDS:
                phonics.setBaseWords(handleWords(value));
                break;
            case PHONICS_MORE_WORDS:
                phonics.setMoreWords(handleWords(value));
                break;
            default:
                break;
        }
    }

    private List<Word> handleWords(String words) {
        List<Word> results = new ArrayList<>();
        String[] split = words.split(",");
        for (String word : split) {
            if (StringUtils.isEmpty(word)) {
                continue;
            }
            if (wordMap.containsKey(word)) {
                results.add(wordMap.get(word));
            } else {
                try {
                    Word translate = RequestFanyi.getTranslate(word);
                    if (translate == null) {
                        continue;
                    }
                    String audioPath = downloadAudio(word);
                    if (StringUtils.isNotEmpty(audioPath)) {
                        translate.setAudio(audioPath);
                        results.add(translate);
                        wordMap.put(word, translate);
                        System.out.println("create word success:【" + word + "】");
                    }

                } catch (Exception e) {
                    System.out.println("word【" + word + "】 request exception");
                    e.printStackTrace();
                }
            }

        }
        return results;
    }

    private String downloadAudio(String word) {
        String firstLetter = word.substring(0, 1).toLowerCase(Locale.ROOT);
        String path = "./output/phonics/audio/" + firstLetter.toLowerCase(Locale.ROOT) + File.separator + word + ".mp3";
        String audioPath = getAudioPath(word);
        try {
            File currentFile = new File(path);
            if (currentFile.exists()) {
                return audioPath;
            }
            boolean copyFlag = RequestFanyi.copyPath(word, path);
            if (copyFlag) {
                return audioPath;
            }
            boolean downloadFlag = RequestFanyi.downloadAudio(word, path);
            if (downloadFlag) {
                return audioPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    private String getAudioPath(String word) {
        String firstLetter = word.substring(0, 1).toLowerCase(Locale.ROOT);
        return firstLetter.toLowerCase(Locale.ROOT) + "/" + word + ".mp3";
    }

    private Map<Integer, String> parseTitle(String titles) {
        Map<Integer, String> titleMap = new HashMap<>();
        String[] words = titles.split("\\|");
        for (int i = 0; i < words.length; i++) {
            titleMap.put(i, words[i].toUpperCase(Locale.ROOT));
        }
        return titleMap;
    }
}
