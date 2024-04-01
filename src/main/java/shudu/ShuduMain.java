package shudu;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: tung
 * @date: 2024/3/21
 */
public class ShuduMain {
    boolean flag = false;
    int[][] array = new int[9][9];
    ShuduEntity entity = new ShuduEntity();

    public static void main(String[] args) {
        ShuduMain shuduMain = new ShuduMain();
        shuduMain.start();
    }

    public void start() {
        List<ShuduEntity> list = new ArrayList<>();
        List<Integer> grades = new ArrayList<>();
        grades.add(20);
        grades.add(25);
        grades.add(30);
        grades.add(35);
        grades.add(40);
        grades.add(45);

        int index = 1;
        for (Integer grade : grades) {
            for (int i = 0; i < 100; i++) {
                flag = false;
                array = new int[9][9];
                entity = new ShuduEntity();
                entity.setGrade(grade);
                entity.setLevel(index);
                array = new int[9][9];
                rand(array);
                dfs(array, 0);
                diger(entity.getGrade(), entity.getAnswer());
                list.add(entity);
                index++;
            }
        }
        System.out.println(JSON.toJSONString(list));

    }

    public void rand(int[][] array) {
        int t = 0;
        //t=14不随机性太高，容易产生没有解的数独，经过参考资料发现，当t=6的时候，几乎100%有解
        while (t < 6) {
            int x = new Random().nextInt(9);
            int y = new Random().nextInt(9);
            int i = new Random().nextInt(9) + 1;
            if (array[x][y] == 0) {
                if (isTrue(array, x, y, i)) {
                    array[x][y] = i;
                    t++;
                }
            }
        }
    }

    public void dfs(int[][] array, int n) {
        if (n < 81) {
            if (flag == true) {
                return;
            }
            int x = n / 9;
            int y = n % 9;
            if (array[x][y] == 0) {
                //若第N个数为0，没有被填过，则判断0~9是否能被填
                for (int i = 1; i < 10; i++) {
                    if (isTrue(array, x, y, i)) {
                        //第N个数可以填i，填入然后dfs
                        array[x][y] = i;
                        dfs(array, n + 1);
                        //dfs回溯
                        array[x][y] = 0;
                    }
                }
            } else {
                dfs(array, n + 1);
            }
        } else {
            flag = true;
            System.out.println("###################");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    entity.getAnswer()[i][j] = array[i][j];
                    System.out.print(array[i][j]);
                }
                System.out.println();
            }
        }
    }

    boolean emptyrow = false, emptycol = false, emptyplaces = false;

    public void diger(int grade, int[][] array) {
        int[][] shudu = new int[9][9];
        int[][] game = new int[9][9];
        int[] row = new int[9];
        int[] col = new int[9];
        int[] places = new int[9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                shudu[i][j] = array[i][j];
            }
        }
        int t = grade;
        while (t > 0) {
            //随机抽到x,y
            int x = new Random().nextInt(9);
            int y = new Random().nextInt(9);
            //若x,y没有被挖空则挖空x,y
            if (shudu[x][y] != 0) {
                row[x]--;
                col[y]--;
                places[(y / 3) * 3 + x / 3]--;
                if ((row[x] == 0 && emptyrow) || (col[y] == 0 && emptycol) || (places[(y / 3) * 3 + x / 3] == 0 && emptyplaces)) {
                    System.out.println(x + " " + y + " 不可以");
                    continue;
                } else {
                    shudu[x][y] = 0;
                    t = t - 1;
                }
                if (row[x] == 0) {
                    emptyrow = true;
                }
                if (col[y] == 0) {
                    emptycol = true;
                }
                if (places[(y / 3) * 3 + x / 3] == 0) {
                    emptyplaces = true;
                }
            }
        }


        System.out.println("-----------------------");
        //获得最终游戏数独
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                game[i][j] = shudu[i][j];
                entity.getProblem()[i][j] = game[i][j];
                System.out.print(game[i][j]);
            }
            System.out.println();
        }

    }

    public boolean isTrue(int[][] array, int x, int y, int num) {
        //横竖是否有num
        for (int i = 0; i < 9; i++) {
            if (array[x][i] == num || array[i][y] == num) {
                return false;
            }
        }

        for (int i = (x / 3) * 3; i < (x / 3 + 1) * 3; i++) {
            for (int j = (y / 3) * 3; j < (y / 3 + 1) * 3; j++) {
                if (array[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }
}
