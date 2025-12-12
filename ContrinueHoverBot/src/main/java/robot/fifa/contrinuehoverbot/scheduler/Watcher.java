package robot.fifa.contrinuehoverbot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import robot.fifa.contrinuehoverbot.utils.AppUtils;
import robot.fifa.contrinuehoverbot.utils.ImageUtils;
import robot.fifa.contrinuehoverbot.utils.TextBoxUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

@Service
public class Watcher {


    private static final Logger log = LoggerFactory.getLogger(Watcher.class);
    private final Random random = new java.util.Random();


    @Scheduled(fixedRate = 1000)
    void run() {
        try {
            watchContinueBtn();
        } catch (Exception ex) {
            log.error("Error in watcher", ex);
        }
    }


    void watchContinueBtn() throws IOException {
        int screenIndex = AppUtils.isWindows() ? 1 : 0;
        var screen = ImageUtils.snapshot(screenIndex);
        var scale = 2.1d;
//        var folder = new File("screenshots");
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }


        var bbox = TextBoxUtils.findTextBox(screen, "CONTINUE", scale);
        if (bbox == null) {
            log.info("No bbox found");
            ImageUtils.hover(random.nextInt(500, 730), random.nextInt(250, 931));
//            Files.write(new File(folder, "screen_" + System.currentTimeMillis() + ".png").toPath(), screen);
            return;
        }

        log.info("OCR bbox (scaled coordinates): {}", bbox);
        // bbox пришла в координатах ИСХОДНОГО изображения.
        // Переводим в координаты ТЕКУЩЕГО скрина (делим на scale)
        double centerXLocal = (bbox.x + bbox.width / 2.0);
        double centerYLocal = (bbox.y + bbox.height / 2.0);

        // Добавляем смещение монитора в глобальные координаты ОС
        Rectangle screenBounds = ImageUtils.getScreenBounds(screenIndex);
        double globalX = screenBounds.x + centerXLocal;
        double globalY = screenBounds.y + centerYLocal;

        globalY += random.nextInt(-3, 3);
        globalX += random.nextInt(-3, 3);

        log.info("Hovering over the text box at (global): {}, {}", globalX, globalY);

        ImageUtils.hover(globalX, globalY);

        log.info("\n\n\n\n\n\n\n\nHover executed!\n\n\n\n\n\n\n\n");
    }
}
