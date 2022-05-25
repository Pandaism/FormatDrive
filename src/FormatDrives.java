import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FormatDrives {
    public enum State {
        INITIALIZE, WAITING_FOR_DRIVE, FORMATTING, CHECK_FORMATTING
    }

    public static void main(String[] args) {
        ArrayList<Character> driveLetters = null;

        int initialSize = 0;

        long startTime = System.currentTimeMillis();
        long deltaTime;

        char newDriveLetter = '-';
        String driveName = "";

        boolean running = true;
        State state = State.WAITING_FOR_DRIVE;

        while(running) {
            switch (state) {
                case INITIALIZE:
                    driveLetters = new ArrayList<>();
                    initialSize = driveLetters.size();
                    File[] roots = File.listRoots();
                    for(File root : roots) {
                        driveLetters.add(root.getAbsolutePath().charAt(0));
                    }

                    newDriveLetter = '-';
                    driveName = "";
                    state = State.WAITING_FOR_DRIVE;
                    break;
                case WAITING_FOR_DRIVE:
                    while(driveLetters.size() == initialSize) {
                        deltaTime = System.currentTimeMillis() - startTime;
                        if(deltaTime > 1000) {
                            File[] searchRoots = File.listRoots();
                            boolean searchingFlag = false;
                            for(File root : searchRoots) {
                                newDriveLetter = root.getAbsolutePath().charAt(0);
                                if(!driveLetters.contains(newDriveLetter)) {
                                    driveLetters.add(newDriveLetter);
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

                        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "C:\\Users\\MikeN\\Desktop\\format.bat.lnk",
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
                    boolean checking = true;

                    while(checking) {
                        
                    }
                    break;
            }
        }








    }

}
