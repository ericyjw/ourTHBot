import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sun.util.logging.LoggingSupport.log;

public class OurTHBot extends TelegramLongPollingBot {
    // Temasekian Information
    private static String name = "";
    private static String matric = "";
    private static String blk = "";

    // BotUser Information
    private static String userFirstName = "";
    private static String userLastName = "";
    private static String userUsername = "";
    private static long userId;
    private static long chatId;
    private Update update;

    public void onUpdateReceived(Update update) {

        // Input and output on telegram
        String input = "";
        String output = "";

        // Check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            input = update.getMessage().getText();

            // Update User Information
            userFirstName = update.getMessage().getChat().getFirstName();
            userLastName = update.getMessage().getChat().getLastName();
            userUsername = update.getMessage().getChat().getUserName();
            userId = update.getMessage().getChat().getId();
            chatId = update.getMessage().getChatId();



            // Reacting to various commands
            if (input.equals("/start")) {
                // First time use
                welcome_message();

            } else if (name.equals("")) {
                // First time update name
                enterNameEntry(update);

            } else if (matric.equals("")) {
                // First time update matric
                enterMatricEntry(update);

            } else if (blk.equals("")) {
                //First time update block
                enterBlkEntry(update);

            } else if (input.equals("/start") && !name.equals("")) {

                SendMessage message = new SendMessage();
                message.setChatId(chatId)
                        .setText("Are you sure that you want to reset the bot?");

               // To be continued...

            } else if (input.equals("/help")){
                // Send out a list of commands for the user to see
                getHelp();

            } else if (input.equals("/eating")){

            }

        } else {
            SendMessage errorMessage = new SendMessage();
            long chatID = update.getMessage().getChatId();
            errorMessage.setChatId(chatID).setText("Error!");
        }
    }




    private void welcome_message(){

        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            message.setChatId(chatId)
                    .setText("Welcome Temasekian " + userFirstName +
                            "! I am your Dinner Number Generator!");
            output = message.toString();
            log(userFirstName, userLastName, Long.toString(userId), input, output);
            execute(message);

            message.setChatId(chatId)
                    .setText("Please provide me with the necessary information to get things started!");
            output = message.toString();
            log(userFirstName, userLastName, Long.toString(userId), input, output);
            execute(message);

            message.setText("What will you want to be addressed as? ");
            output = message.toString();
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void enterNameEntry(Update update) {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            input = update.getMessage().getText();
            name = input;
            log(userFirstName, userLastName, Long.toString(userId), output, input);
            message.setChatId(chatId)
                    .setText("Ok " + name + "! So what's your matric number?");
            output = message.toString();
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void enterMatricEntry(Update update) {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try{
            input = update.getMessage().getText();
            matric = input.toUpperCase();

            if(validMatric(matric)) {
                log(userFirstName, userLastName, Long.toString(userId), output, input);
                message.setChatId(chatId)
                        .setText(matric + " sounds like someone I will date! Lastly " + name + ", which block you come from?");
                output = message.toString();
                execute(message);
            } else {
                log(userFirstName, userLastName, Long.toString(userId), output, input);
                message.setChatId(chatId)
                        .setText("That looks like an incorrect matric number... Please key in a valid one!");
                matric = "";
                output = message.toString();
                execute(message);

            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // To include inline keyboard
    // To include get help
    private void enterBlkEntry(Update update) {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            input = update.getMessage().getText();
            blk = input.toUpperCase();
            log(userFirstName, userLastName, Long.toString(userId), output, input);
            message.setChatId(chatId)
                    .setText("Welcome " + name + " , " + matric + " from Block " + blk + " aboard!");
            execute(message);
            message.setChatId(chatId)
                    .setText("Do not worry if you are clueless now. You can always /help and you can definitely find some answers!");
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // To be continued...
    private boolean confirmReset() {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            message.setChatId(chatId)
                    .setText("Are you sure that you want to reset the bot?);
            output = message.toString();
            log(userFirstName, userLastName, Long.toString(userId), input, output);
            execute(message);

            message.setChatId(chatId)
                    .setText("Please provide me with the necessary information to get things started!");
            output = message.toString();
            log(userFirstName, userLastName, Long.toString(userId), input, output);
            execute(message);

            message.setText("What will you want to be addressed as? ");
            output = message.toString();
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getHelp() {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            message.setChatId(chatId)
                    .setText("Here are the common commands you can use: \n" +
                            "/eating - You are consuming dinner today and do not wish to donate your matric number.\n" +
                            "/noteating - You are feeling generous today and feel like helping out some hungry souls.\n" +
                            "/numberpls - You are feeling extra hungry today and wish to have more food!\n\n" +
                            "/updatename - Suddenly do not feel like being called " + name + " and wish to change your name.\n" +
                            "/updatematric - For somewhat reason your matric number chances, you can update to your new matric number.\n" +
                            "/updateblk - Change block but still one Temasek!");
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Simple verification of matric numbers
    private boolean validMatric(String matric) {
        if (matric.charAt(0) != 'A' || matric.length() != 9) {
            return false;
        } else {
            Character[] checkLetter = {'Y', 'X', 'W', 'U', 'R', 'N', 'M', 'L', 'J', 'H', 'E', 'A', 'B'};
            int d1 = Character.getNumericValue(matric.charAt(1));
            System.out.println(d1);
            int d2 = Character.getNumericValue(matric.charAt(2));
            System.out.println(d2);
            int d3 = Character.getNumericValue(matric.charAt(3));
            System.out.println(d3);
            int d4 = Character.getNumericValue(matric.charAt(4));
            System.out.println(d4);
            int d5 = Character.getNumericValue(matric.charAt(5));
            System.out.println(d5);
            int d6 = Character.getNumericValue(matric.charAt(6));
            System.out.println(d6);
            int d7 = Character.getNumericValue(matric.charAt(7));
            System.out.println(d7);

            int letterValue = (d1 + d2 + d3 + d4 + d5 + d6 + d7) % 13;
            Character letter = checkLetter[letterValue];
            Character givenLetter = matric.charAt(8);
            System.out.println(letterValue);
            System.out.println(letter);
            System.out.println(givenLetter);
            if (letter.equals(givenLetter)) {
                return true;
            }
        }
        return false;
    }

    public String getBotUsername() {
        return "ourTHBot";
    }

    public String getBotToken() {
        return "593031383:AAHhl5lTr8_Rk36LwsMLYte6DoEByRxHR_c";
    }

    private void log(String first_name, String last_name, String user_id, String txt, String bot_answer) {
        System.out.println("----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
        System.out.println();
    }

}
