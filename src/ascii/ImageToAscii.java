package ascii;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author tool-taro.com
 */
public class ImageToAscii {

    public static void main(String[] args) throws IOException, InterruptedException {

        //Javaのパス
        String javaPath = "/Applications/Eclipse_2020-06.app/Contents/java/11/Home/bin/java";
        //jave5のディレクトリ
        String javeDir = "/Users/nozaryo8/art/jave5";
        //jave5.jarのパス
        String javePath = "/Users/nozaryo8/art/jave5/jave5.jar";

        //読み取りたい画像ファイルの保存場所
        String inputFilePath = "/Users/nozaryo8/art/doraemon.jpg";

        //Ascii文字数(横) width default is 72
        int width = 200;

        //Ascii倍率(縦) shape default is 1.0
        double shape = 1.0;

        //変換アルゴリズム
        /**
         * jave_algorithm (new since JavE5.0)
         * 4_pixels_per_character
         * 1_pixel_per_character (new since JavE5.0, old one is renamed to 'Gradient')
         * gradient
         * edge_detection
         * edge_tracing
         * felt_pen
         */
        String algorithm = "4_pixels_per_character";


        //画像の濃淡を変更する
        String table = "hyperfont";

        //よくわからん
        String charfile="dito";

        String[] commandArray = new String[10];
        int index = 0;
        commandArray[index++] = javaPath;
        commandArray[index++] = "-jar";
        commandArray[index++] = javePath;
        commandArray[index++] = "image2ascii";
        commandArray[index++] = inputFilePath;
        commandArray[index++] = "width=" + width;
        commandArray[index++] = "shape=" + shape;
        commandArray[index++] = "algorithm=" + algorithm;
        commandArray[index++] = "table=" + table;
        commandArray[index++] = "charfile=" + charfile;

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        StringBuilder logBuilder = new StringBuilder();
        StringBuilder errorBuilder = new StringBuilder();
        int status = 0;

        try {
            //作業ディレクトリをjave5ディレクトリに移して処理する
            process = runtime.exec(commandArray, null, new File(javeDir));
            final InputStream in = process.getInputStream(); //
            final InputStream ein = process.getErrorStream();

            Runnable inputStreamThread = () -> {
//                BufferedReader reader = null;
                String line;
                try ( BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {//
//                    reader = new BufferedReader(new InputStreamReader(in));
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        logBuilder.append(line).append("\n");

                    }
                }
                catch (Exception e) {
                	e.printStackTrace(); //エラーの情報を表示する
                	System.out.println("エラーです");
                }
                finally {

                }//この時点でドラえもんはできている
            };
            Runnable errorStreamThread = () -> {
                BufferedReader reader = null;
                String line;
                try {
                    reader = new BufferedReader(new InputStreamReader(ein));
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        errorBuilder.append(line).append("\n");
                    }
                }
                catch (Exception e) {
                }
                finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Exception e) {
                        }
                    }
                }
            };

            Thread inThread = new Thread(inputStreamThread);
            Thread errorThread = new Thread(errorStreamThread);

            inThread.start();
            errorThread.start();

            status = process.waitFor();
            inThread.join();
            errorThread.join();
        }
        finally {
            if (process != null) {
                try {
                    process.destroy();
                }
                catch (Exception e) {
                }
            }
        }
        System.out.format("変換結果\n%1$s", logBuilder.toString());
    }
}