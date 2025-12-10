package robot.fifa.contrinuehoverbot.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import robot.fifa.contrinuehoverbot.utils.ImageUtils;
import robot.fifa.contrinuehoverbot.utils.TextBoxUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Service
public class Watcher {


    private static final Logger log = LoggerFactory.getLogger(Watcher.class);


    @Scheduled(fixedRate = 963)
    void run(){
        try {
            watchContinueBtn();
        }catch (Exception ex){
            log.error("Error in watcher",ex);
        }
    }


    void watchContinueBtn() throws IOException, InterruptedException {
        int screenIndex = 0;
        var screen = ImageUtils.snapshot(screenIndex);
        var scale = 2d;
        var folder = new File("screenshots");
        if (!folder.exists()) {
            folder.mkdirs();
        }


        var bbox = TextBoxUtils.findTextBox(screen, "CONTINUE", scale);
        if (bbox == null) {
            log.info("No bbox found");
            var random = new java.util.Random();
            ImageUtils.hover(random.nextInt(100,1030), random.nextInt(150,931));
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
        var random = new java.util.Random();
        globalY+= random.nextInt(-5,5);
        globalX+= random.nextInt(-7,10);

        log.info("Hovering over the text box at (global): {}, {}", globalX, globalY);

        // 🖱️ НАВОДИМ МЫШЬ НА КООРДИНАТУ
        ImageUtils.hover(globalX, globalY);

        log.info("Hover executed!");
    }
}
