
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.LongPollingBot;

import java.util.*;


public final class MainClass {
    public static void main(String[] args) throws InterruptedException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        OurTHBot ourTHBot = new OurTHBot();
        try {
            telegramBotsApi.registerBot(ourTHBot);
            ourTHBot.uploadOurTHBot();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

/*
        TimerTask resetDataBase = new MainClass();
        // perform the task once a day at 4 a.m., starting tomorrow morning
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(resetDataBase, getTomorrowEvening5pm(), fONCE_PER_DAY);
    }

    @Override
    public void run() {
        System.out.println("doing");
    }

    private final static long fONCE_PER_DAY = 1000 * 60 * 60 * 24;

    private final static int fONE_DAY = 1;
    private final static int fFIVE_PM = 5;
    private final static int fZERO_MINUTES = 0;

    private static Date getTomorrowEvening5pm() {
        Calendar tomorrow = new GregorianCalendar();
        tomorrow.add(Calendar.DATE, fONE_DAY);
        Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE), fFIVE_PM,
                fZERO_MINUTES);
        return result.getTime();
    }
 */







/*
  TimerTask resetDataBase = new MainClass();
        // perform the task once a day at 4 a.m., starting tomorrow morning
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(resetDataBase, getTomorrowEvening5pm(), fONCE_PER_DAY);
        Calendar today = new GregorianCalendar();
        System.out.println(today.getTime());
        //today.add(Calendar.DATE, fONE_DAY);
        //System.out.println(today.getTime());
        Calendar result = new GregorianCalendar(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH), today.get(Calendar.DATE), fFIVE_PM,
                fZERO_MINUTES);
        System.out.println(result.getTime());







    }


    private final static long fONCE_PER_DAY = 1000 * 60 * 60 * 24;

    private final static int fONE_DAY = 1;
    private final static int fFIVE_PM = 14;
    private final static int fZERO_MINUTES = 01;

    private static Date getTomorrowEvening5pm() {
        Calendar tomorrow = new GregorianCalendar();
        //tomorrow.add(Calendar.DATE, fONE_DAY);
        Calendar result = new GregorianCalendar(tomorrow.get(Calendar.YEAR),
                tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DATE), fFIVE_PM,
                fZERO_MINUTES);
        return result.getTime();
    }
 */


