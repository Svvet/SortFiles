import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.*;
import java.io.FileWriter;

public class App {
    static boolean toFolder(File fl) {
        String name = fl.getName();
        String type = name.substring(name.length() - 3, name.length());
        BasicFileAttributes attr;
        try {
            attr = Files.readAttributes(fl.toPath(), BasicFileAttributes.class);

            if (type.equals("xml")) {
                Path copied = Paths.get("Dirs/DEV/" + name);
                Path originalPath = fl.toPath();
                Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                fl.delete();
                return true;

            } else if (type.equals("jar")) {

                int hour = Integer.parseInt(attr.creationTime().toString().substring(12, 13));
                
                if (hour % 2 == 0) {
                    Path copied = Paths.get("Dirs/DEV/" + name);
                    Path originalPath = fl.toPath();
                    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                    fl.delete();
                    return true;
                } else {
                    Path copied = Paths.get("Dirs/TEST/" + name);
                    Path originalPath = fl.toPath();
                    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                    fl.delete();
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Błąd: " + e.getMessage());
        }
        return false;

    }

    public static void main(String[] args) {
        new File("Dirs").mkdirs();
        new File("Dirs/HOME").mkdirs();
        new File("Dirs/DEV").mkdirs();
        new File("Dirs/TEST").mkdirs();

        int counter = 0;
        try {
            File myObj = new File("Dirs/HOME/count.txt");
            if (myObj.createNewFile()) {
                System.out.println("count.txt created: " + myObj.getName());
            } else {
                System.out.println("count.txt already exists.");
            }
            FileWriter myWriter = new FileWriter("Dirs/HOME/count.txt", false);
                    
                    myWriter.write(Integer.toString(counter));
                    myWriter.close();
        } catch (Exception e) {
        }
        

        
        
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get("Dirs/HOME");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() != StandardWatchEventKinds.ENTRY_CREATE)
                        break;
                   
                    WatchEvent<Path> ev = cast(event);
                    Path filename = ev.context();
                    File fll = new File("Dirs/HOME/" + filename);
                    boolean success = toFolder(fll);
                    if (!success)
                        break;
                    FileWriter myWriter = new FileWriter("Dirs/HOME/count.txt", false);
                    counter++;
                    myWriter.write(Integer.toString(counter));
                    myWriter.close();

                }
                key.reset();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
}
