package walker;

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
public class UrlEntity {
    private String name;
    private List<String> urls = new ArrayList<>();
}
