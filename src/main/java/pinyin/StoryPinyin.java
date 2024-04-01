package pinyin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: tung
 * @date: 2024/2/28
 */
@Getter
@Setter
public class StoryPinyin {
    private int id;
    private List<List<CharPinyin>> content = new ArrayList<>();

    private List<CharPinyin> name;
}
