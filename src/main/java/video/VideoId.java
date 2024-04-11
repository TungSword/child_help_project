package video;

import lombok.*;

/**
 * @author: tung
 * @date: 2024/4/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoId {
    private String name;
    private int id;
    private String url;
}
