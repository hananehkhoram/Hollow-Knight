package hana.HollowKnight.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class SpriteExtractor {

    private static class Box {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        void add(int x, int y) {
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
        int getWidth() { return (maxX - minX) + 1; }
        int getHeight() { return (maxY - minY) + 1; }
    }

    public static void main(String[] args) {
        // استخراج هوشمند و پیکسل‌به-پیکسل اطلس باران دایره‌ای
        autoExtract("SpriteAtlasTexture-Ruin_rain_glow-1024x1024-fmt12.jpg", "extracted_rain_glow_perfect");

        // استخراج اطلس باران مثلثی درها
        autoExtract("SpriteAtlasTexture-Ruin_Door_Rain_Glow-512x512-fmt12.png", "extracted_door_rain_perfect");
    }

    public static void autoExtract(String imagePath, String outputFolder) {
        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                System.out.println("فایل پیدا نشد: " + file.getAbsolutePath());
                return;
            }

            BufferedImage img = ImageIO.read(file);
            int w = img.getWidth();
            int h = img.getHeight();

            boolean[][] visited = new boolean[w][h];
            List<Box> foundBoxes = new ArrayList<>();

            // آستانه تشخیص رنگ (چون پس‌زمینه کاملاً سیاه است، هر پیکسلی که روشنایی داشته باشد یعنی جزو فریم است)
            int threshold = 15;

            // الگوریتم اسکن لایه‌ای برای پیدا کردن جزیره‌های گرافیکی (دایره‌ها)
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (!visited[x][y]) {
                        int rgb = img.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;

                        // اگر پیکسل سیاه نباشد و قبلاً اسکن نشده باشد
                        if (r > threshold || g > threshold || b > threshold) {
                            Box box = new Box();
                            floodFill(img, x, y, visited, box, threshold);

                            // فیلتر کردن نویزهای خیلی کوچک زیر ۵۰ پیکسل
                            if (box.getWidth() > 50 && box.getHeight() > 50) {
                                foundBoxes.add(box);
                            }
                        }
                    }
                }
            }

            // ساخت پوشه خروجی و ذخیره فریم‌های کاملاً دقیق
            File dir = new File(outputFolder);
            if (!dir.exists()) dir.mkdirs();

            System.out.println("--- استخراج هوشمند برای: " + imagePath + " ---");
            System.out.println("تعداد فریم‌های دقیقاً کشف شده: " + foundBoxes.size());

            for (int i = 0; i < foundBoxes.size(); i++) {
                Box b = foundBoxes.get(i);
                BufferedImage sub = img.getSubimage(b.minX, b.minY, b.getWidth(), b.getHeight());
                File out = new File(dir, "frame_" + i + ".png");
                ImageIO.write(sub, "png", out);
                System.out.println("ذخیره شد: " + out.getName() + " در ابعاد دقیق: " + b.getWidth() + "x" + b.getHeight());
            }
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // متد کمکی برای ردیابی پیوسته لبه‌های دایره
    private static void floodFill(BufferedImage img, int startX, int startY, boolean[][] visited, Box box, int threshold) {
        int w = img.getWidth();
        int h = img.getHeight();
        List<int[]> queue = new ArrayList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        int head = 0;
        while (head < queue.size()) {
            int[] curr = queue.get(head++);
            int cx = curr[0];
            int cy = curr[1];

            box.add(cx, cy);

            // بررسی ۴ جهت همسایگی پیکسل
            int[][] neighbors = {{cx+1, cy}, {cx-1, cy}, {cx, cy+1}, {cx, cy-1}};
            for (int[] n : neighbors) {
                int nx = n[0];
                int ny = n[1];

                if (nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny]) {
                    int rgb = img.getRGB(nx, ny);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;

                    if (r > threshold || g > threshold || b > threshold) {
                        visited[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }
}
