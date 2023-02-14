package Utils;

public class pause {

    public static void pause(int min) throws InterruptedException {
        System.out.println("*************Pause " + min + "min **************************" );
        Thread.sleep(60000*min);
    }
}
