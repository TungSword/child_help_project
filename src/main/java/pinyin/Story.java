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
public class Story {
    private String name;
    private List<String> story = new ArrayList<>();
}
