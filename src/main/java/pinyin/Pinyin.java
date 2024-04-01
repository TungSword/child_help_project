package pinyin;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: tung
 * @date: 2024/2/28
 */
@Getter
@Setter
public class Pinyin {
    private String label;
    private List<String> value;
}
