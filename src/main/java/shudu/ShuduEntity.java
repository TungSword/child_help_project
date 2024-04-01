package shudu;

import lombok.*;

/**
 * @author: tung
 * @date: 2024/3/21
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShuduEntity {
    private int grade;
    private int level;
    private int[][] answer = new int[9][9];
    private int[][] problem = new int[9][9];
}
