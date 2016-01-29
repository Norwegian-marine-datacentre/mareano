package no.imr.geoexplorer.printmap;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

public class DownloadConcurrent {
    private final static ExecutorService pool = Executors.newCachedThreadPool();

    public static Future<BufferedImage> startDownloading(final URL url) throws IOException {
        return pool.submit(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                URLConnection con = url.openConnection();
                con.setConnectTimeout(4000);
                con.setReadTimeout(4000);
                InputStream input = con.getInputStream();
                BufferedImage buf = ImageIO.read(input);
                input.close();
                return buf;
            }
        });
    }
}
