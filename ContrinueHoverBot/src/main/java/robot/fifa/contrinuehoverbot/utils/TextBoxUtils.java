package robot.fifa.contrinuehoverbot.utils;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class TextBoxUtils {

    private static final String TESSDATA_DIR =
            AppUtils.getFilePrefixByOs() + "ContrinueHoverBot/configs/tessdata";

    // 🔹 Minimum viable dimensions for Tesseract
    private static final int MIN_WIDTH = 10;
    private static final int MIN_HEIGHT = 10;

    // ------------------------------------------
    // 🔥 ОСНОВНОЙ МЕТОД — НАХОДИТ КНОПКУ / ТЕКСТ
    // ------------------------------------------
    public static Rectangle findTextBox(byte[] content, String target, double scale) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(content));
        if (img == null) throw new IOException("Invalid or empty image content");

        // 🔥 FIX: принудительная конвертация в RGB
        BufferedImage rgb = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return findTextBox(rgb, target, scale);
    }

    public static Rectangle findTextBox(BufferedImage img, String target, double scale) {
        // 🔹 FIX: Increase minimum scale to ensure text is readable
        double effectiveScale = Math.max(scale, 3.0);

        BufferedImage pre = upscale(img, effectiveScale);

        Tesseract t = new Tesseract();
        t.setDatapath(TESSDATA_DIR);
        t.setLanguage("eng");

        // 🔹 FIX: Use AUTO page segmentation for better text detection
        // PSM_SINGLE_LINE can be too restrictive for variable-sized UI elements
        t.setPageSegMode(ITessAPI.TessPageSegMode.PSM_AUTO);
        t.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY);

        List<Word> words;
        try {
            words = t.getWords(pre, ITessAPI.TessPageIteratorLevel.RIL_WORD);
        } catch (Exception e) {
            System.err.println("OCR failed: " + e.getMessage());
            return null;
        }

        String normTarget = normalize(target);
        Rectangle best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Word w : words) {
            String raw = w.getText();
            if (raw == null || raw.isEmpty()) continue;

            String norm = normalize(raw);
            if (norm.isEmpty()) continue;

            Rectangle bbox = w.getBoundingBox();

            // 🔹 FIX: Filter out boxes that are too small to be valid UI elements
            if (bbox.width < MIN_WIDTH || bbox.height < MIN_HEIGHT) {
                continue;
            }

            // 1) точное совпадение
            if (norm.equals(normTarget)) {
                // Scale back to original coordinates
                return scaleRectangleDown(bbox, effectiveScale);
            }

            // 2) fuzzy-поиск по расстоянию Левенштейна
            int dist = levenshtein(norm, normTarget);
            if (dist < bestScore && dist <= 3) {
                bestScore = dist;
                best = bbox;
            }
        }

        if (best != null) {
            return scaleRectangleDown(best, effectiveScale);
        }

        return null;
    }

    // ------------------------------------------
    // 🔹 Апскейл картинки для лучшего OCR
    // ------------------------------------------
    private static BufferedImage upscale(BufferedImage src, double scale) {
        if (scale == 1.0) return src;

        int w = (int) (src.getWidth() * scale);
        int h = (int) (src.getHeight() * scale);

        // 1) Конвертация в совместимый формат
        BufferedImage compatible = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g0 = compatible.createGraphics();
        g0.drawImage(src, 0, 0, null);
        g0.dispose();

        // 2) Масштабирование
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(compatible, 0, 0, w, h, null);
        g.dispose();

        return dst;
    }

    // ------------------------------------------
    // 🔹 Scale rectangle coordinates back down
    // ------------------------------------------
    private static Rectangle scaleRectangleDown(Rectangle bbox, double scale) {
        return new Rectangle(
                (int) (bbox.x / scale),
                (int) (bbox.y / scale),
                (int) (bbox.width / scale),
                (int) (bbox.height / scale)
        );
    }

    // ------------------------------------------
    // 🔹 Мягкая нормализация текста
    // ------------------------------------------
    private static String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase()
                .replaceAll("[^a-z]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // ------------------------------------------
    // 🔹 Своя реализация расстояния Левенштейна
    // ------------------------------------------
    private static int levenshtein(String a, String b) {
        int lenA = a.length();
        int lenB = b.length();

        if (lenA == 0) return lenB;
        if (lenB == 0) return lenA;

        int[] prev = new int[lenB + 1];
        int[] curr = new int[lenB + 1];

        for (int j = 0; j <= lenB; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= lenA; i++) {
            curr[0] = i;
            char ca = a.charAt(i - 1);

            for (int j = 1; j <= lenB; j++) {
                char cb = b.charAt(j - 1);
                int cost = (ca == cb) ? 0 : 1;

                int del = prev[j] + 1;
                int ins = curr[j - 1] + 1;
                int sub = prev[j - 1] + cost;

                curr[j] = Math.min(Math.min(del, ins), sub);
            }

            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }

        return prev[lenB];
    }
}