import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class MainClass {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        OurTHBot ourTHBot = new OurTHBot();

        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);

        ReaderThread reader = new ReaderThread(queue);
        WriterThread writer = new WriterThread(queue);

        //new Thread(reader).start();
        //new Thread(writer).start();

        try {

            Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
            Integer min = rightNow.get(Calendar.MINUTE);
            Integer dayOfWeek = rightNow.get(Calendar.DAY_OF_WEEK);

            System.out.println("Hour - " + hour);
            System.out.println("Min - " + min);
            System.out.println("Day of the week - " + dayOfWeek);


            telegramBotsApi.registerBot(ourTHBot);
            ourTHBot.uploadOurTHBot();


        } catch (TelegramApiException e) {

            e.printStackTrace();

        }

    }

}

