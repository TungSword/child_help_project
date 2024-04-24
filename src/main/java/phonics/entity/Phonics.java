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
public class Phonics {
    private String letter;
    private String standard;
    private String type;
    private String audio;
    private List<Word> baseWords;
    private List<Word> moreWords;
}
