package robot.fifa.contrinuehoverbot.utils;

import javax.imageio.ImageIO;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class ImageUtils {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Failed to initialize Robot", e);
        }
    }

    public static byte[] snapshot(int screenNumber) {
        // Capture a screenshot of the specified screen and return PNG bytes
        // Validate environment
        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalStateException("Cannot take snapshot in headless environment");
        }

        // Get all screens
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        if (screens == null || screens.length == 0) {
            throw new IllegalStateException("No screens detected");
        }

        if (screenNumber < 0 || screenNumber >= screens.length) {
            throw new IllegalArgumentException("Invalid screenNumber " + screenNumber + ", available screens: 0.." + (screens.length - 1));
        }

        GraphicsDevice target = screens[screenNumber];
        Rectangle bounds = target.getDefaultConfiguration().getBounds();

        try {
            Robot robot = new Robot(target);
            BufferedImage image = robot.createScreenCapture(bounds);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "png", baos);
                return baos.toByteArray();
            }
        } catch (AWTException e) {
            throw new RuntimeException("Failed to create Robot for screen capture", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode screenshot to PNG", e);
        }
    }

    /**
     * Returns OS global bounds of a given screen. Use to translate local OCR coords to global mouse coords.
     */
    public static Rectangle getScreenBounds(int screenNumber) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        if (screens == null || screens.length == 0) {
            throw new IllegalStateException("No screens detected");
        }
        if (screenNumber < 0 || screenNumber >= screens.length) {
            throw new IllegalArgumentException("Invalid screenNumber " + screenNumber + ", available screens: 0.." + (screens.length - 1));
        }
        return screens[screenNumber].getDefaultConfiguration().getBounds();
    }

    /**
     * 🖱️ Плавно наводит мышь на глобальные координаты (hover с анимацией)
     * @param globalX глобальная X координата
     * @param globalY глобальная Y координата
     */
    public static void hover(double globalX, double globalY) {
        smoothHover((int) globalX, (int) globalY, new Random().nextInt(400,503)); // 500ms по умолчанию
    }

    /**
     * 🖱️ Плавно наводит мышь с указанной длительностью
     * @param globalX глобальная X координата
     * @param globalY глобальная Y координата
     * @param durationMs продолжительность движения в миллисекундах
     */
    public static void hover(int globalX, int globalY, long durationMs) {
        smoothHover(globalX, globalY, durationMs);
    }

    /**
     * 🖱️ Плавное перемещение мыши от текущей позиции к целевой
     */
    private static void smoothHover(int targetX, int targetY, long durationMs) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) {
            robot.mouseMove(targetX, targetY);
            return;
        }

        Point currentPos = pointerInfo.getLocation();
        int startX = (int) currentPos.getX();
        int startY = (int) currentPos.getY();

        // Если уже на месте — не двигаем
        if (startX == targetX && startY == targetY) {
            return;
        }

        long startTime = System.currentTimeMillis();
        long endTime = startTime + durationMs;

        while (System.currentTimeMillis() < endTime) {
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, (double) elapsed / durationMs);

            // Плавное ускорение/замедление (easing)
            double eased = easeInOutCubic(progress);

            int newX = (int) (startX + (targetX - startX) * eased);
            int newY = (int) (startY + (targetY - startY) * eased);

            robot.mouseMove(newX, newY);

            // Небольшая задержка для плавности
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Финальное позиционирование точно в цель
        robot.mouseMove(targetX, targetY);
    }

    /**
     * 🔹 Функция easing для плавного движения (cubic in-out)
     */
    private static double easeInOutCubic(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    /**
     * 🖱️ Кликает на глобальные координаты
     * @param globalX глобальная X координата
     * @param globalY глобальная Y координата
     */
    public static void click(double globalX, double globalY) {
        click((int) globalX, (int) globalY);
    }

    /**
     * 🖱️ Кликает на целые глобальные координаты
     */
    public static void click(int globalX, int globalY) {
        robot.mouseMove(globalX, globalY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * 🖱️ Кликает с задержкой между press и release (для надежности)
     */
    public static void click(int globalX, int globalY, long delayMs) {
        robot.mouseMove(globalX, globalY);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * 🖱️ Двойной клик
     */
    public static void doubleClick(int globalX, int globalY) {
        click(globalX, globalY);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        click(globalX, globalY);
    }

    /**
     * 🖱️ Правый клик
     */
    public static void rightClick(int globalX, int globalY) {
        robot.mouseMove(globalX, globalY);
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }
}