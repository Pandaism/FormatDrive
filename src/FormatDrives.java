import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class FormatDrives {
    public enum State {
        INITIALIZE, WAITING_FOR_DRIVE, FORMATTING, CHECK_FORMATTING
    }

    public static void main(String[] args) {
        int units = 1;
        Map<Character, File> driveLetters = null;

        int initialSize;

        long startTime = 0;
        long deltaTime;

        char newDriveLetter = '-';
        String driveName = "";

        boolean running = true;
        State state = State.INITIALIZE;

        while(running) {
            switch (state) {
                case INITIALIZE:
                    driveLetters = new HashMap<>();

                    File[] roots = File.listRoots();
                    for(File root : roots) {
                        driveLetters.put(root.getAbsolutePath().charAt(0), root);
                    }

                    startTime = System.currentTimeMillis();
                    newDriveLetter = '-';
                    driveName = "";
                    state = State.WAITING_FOR_DRIVE;
                    break;
                case WAITING_FOR_DRIVE:
                    initialSize = driveLetters.size();

                    while(driveLetters.size() == initialSize) {
                        deltaTime = System.currentTimeMillis() - startTime;
                        if(deltaTime > 1000) {
                            File[] searchRoots = File.listRoots();
                            boolean searchingFlag = false;
                            for(File root : searchRoots) {
                                newDriveLetter = root.getAbsolutePath().charAt(0);
                                if(!driveLetters.containsKey(newDriveLetter)) {
                                    driveLetters.put(newDriveLetter, root);
                                    String temp = FileSystemView.getFileSystemView().getSystemDisplayName(root);

                                    driveName = temp.substring(0, temp.indexOf("(") - 1);
                                    state = State.FORMATTING;
                                    break;
                                } else {
                                    if(!searchingFlag) {
                                        System.out.println("Searching for new drive");
                                        searchingFlag = true;
                                    }
                                }
                            }
                            startTime = System.currentTimeMillis();
                        }
                    }
                    break;

                case FORMATTING:
                    if(newDriveLetter == 'C' || newDriveLetter == 'N' || newDriveLetter == 'R' || newDriveLetter == 'V' || newDriveLetter == 'X') {
                        System.out.println("Invalid System Drives. These cannot be formatted");
                    } else {
                        System.out.println("Drive " + driveName + "("+ newDriveLetter + ":/) has been found. Formatting to NTFS.");

                        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "C:\\Users\\MikeN\\Desktop\\format" +
                                ".bat.lnk",
                                String.valueOf(newDriveLetter), driveName);

                        try {
                            pb.start();
                            state = State.CHECK_FORMATTING;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CHECK_FORMATTING:
                    startTime = System.currentTimeMillis();
                    while(true) {
                        deltaTime = System.currentTimeMillis() - startTime;
                        if(deltaTime > 1000) {
                            ArrayList<Character> checkList = new ArrayList<>();
                            for(File file : File.listRoots()) {
                                checkList.add(file.getAbsolutePath().charAt(0));
                            }

                            if(!checkList.contains(newDriveLetter)) {
                                state = State.INITIALIZE;
                                try {
                                    GregorianCalendar calendar = new GregorianCalendar();
                                    DateFormat dateFormat = new SimpleDateFormat("[MM-dd-yyyy HH:mm:ss:SS] ");
                                    String date = dateFormat.format(calendar.getTime());

                                    File data = new File("./FormatDrives/data.log");
                                    BufferedWriter writer = new BufferedWriter(new FileWriter(data, true));
                                    writer.write("====== " + date + " ======\n");
                                    writer.write("Formatted Drive: (" + newDriveLetter + ") " + driveName + "\n");
                                    writer.write("============" + units + "============\n\n");
                                    writer.close();

                                    units++;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            } else {
                                System.out.println("Waiting for drive " + driveName + "(" + newDriveLetter + ") to eject." +
                                        "..");
                            }

                            startTime = System.currentTimeMillis();
                        }
                    }

                    break;
            }
        }

    }

}
