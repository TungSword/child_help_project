package englishstandard;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: tung
 * @date: 2024/3/19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnglishStandards {
    private String type;
    private List<EngStandard> data = new ArrayList<>();
}
