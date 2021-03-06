package ru.aleksx.filedeleter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ABobrutskov on 04.05.2017.
 */
public class Application {
    private final PathDetector detector;
    private final Finder finder;
    private final Deleter deleter;
    private Timer timer;
    private boolean isService;
    private boolean isNeedDeleteToBin;
    private final List<String> fileExtensions;

    public Application(List<String> fileExtensions, List<String> ingoredExtensions) {
        detector = new PathDetector();
        finder = new Finder();
        deleter = new Deleter();
        this.fileExtensions = fileExtensions;
        finder.setFileExtensionsToFind(fileExtensions);
        finder.setIgnoredExtensions(ingoredExtensions);
    }

    private void doJob() throws IOException {
        String path = detector.getNormalizedCurrentDir();
        finder.setPathToFindIn(path);
        List<Path> files = finder.findFiles();
        if (!files.isEmpty()) {
            deleter.deleteFiles(files, isNeedDeleteToBin);
        }
        files = finder.findEmptyFolders();
        System.out.println("Delete Empty Folders if exists...");
        while (!files.isEmpty()) {
            deleter.deleteFiles(files, isNeedDeleteToBin);
            files = finder.findEmptyFolders();
        }


    }

    public void start() throws IOException {
        if (isService) {
            while (!timer.isInterrupt()) {
                doJob();
                timer.waitForNextJob();
            }
        } else {
            doJob();
        }
    }


    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }


    public void setService(boolean service) {
        isService = service;
    }

    public void setPeriodToDelete(String value) {
        if (value != null) {
            finder.setPeriodToDelete(value);
        }
    }

    public void setDeepSearch(boolean isDeepSearch) {
        finder.setDeepSearch(isDeepSearch);
    }

    public void setNeedDeleteToBin(boolean isNeedDeleteToBin) {
        this.isNeedDeleteToBin = isNeedDeleteToBin;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }
}
