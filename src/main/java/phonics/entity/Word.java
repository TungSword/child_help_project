package phonics.entity;

import lombok.*;

import java.util.List;

/**
 * @author: tung
 * @date: 2024/4/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Word {
    private String word;
    private String chinese;
    private List<String> englishStandards;
    private String audio;

    public Word(String word, String chinese, List<String> englishStandards) {
        this.word = word;
        this.chinese = chinese;
        this.englishStandards = englishStandards;
    }
}
