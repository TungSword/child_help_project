package walker;

import lombok.*;
import pinyin.CharPinyin;

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
public class PoetryEntity {
    private String name;
    private List<CharPinyin> pinyinName = new ArrayList<>();
    private String author;
    private String dynasty;
    private List<String> contents = new ArrayList<>();
    private List<List<CharPinyin>> pinyinContent = new ArrayList<>();
    private List<String> translation = new ArrayList<>();
}
