package pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author: tung
 * @date: 2024/2/28
 */
public class PinyinUtil {
    public static String convertToPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();
        char[] chars = chinese.toCharArray();

        for (char c : chars) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);

            if (pinyinArray != null) {
                pinyin.append(pinyinArray[0]);
            } else {
                pinyin.append(c);
            }
        }
        return pinyin.toString();
    }
}
