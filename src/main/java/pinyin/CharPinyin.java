package pinyin;

import lombok.*;

/**
 * @author: tung
 * @date: 2024/2/28
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CharPinyin {
    private String name;
    private String pinyin;
}
