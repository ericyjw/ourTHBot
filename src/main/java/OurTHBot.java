import org.glassfish.grizzly.http.util.FastDateFormat;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class OurTHBot extends TelegramLongPollingBot {

    private static boolean hasReset = false;
    private static boolean hasNotify = false;

    private static int resetHour = 18;
    private static int resetMin = 32;
    private static int notifyHour = 18;
    private static int notifyMin = 33;


    private static HashMap<Long, Temasekian> temasekDataBase = new HashMap<Long, Temasekian>();
    private static HashMap<String, Long> matricToUserId = new HashMap<String, Long>();
    private static HashMap<Long, String> chatIdToTemasekianName = new HashMap<Long, String>();
    private static List<Long> chatIdList = new ArrayList<Long>();

    private static List<String> photos = new ArrayList<String>();
    private static String dinnerCaption = "";

    private static List<String> admins = new ArrayList<String>();
    private static int retryCounter = 3;

    // some may need to contain inside the temasekian

    // BotUser Information
    private String userFirstName = "";
    private String userLastName = "";
    private String userUsername = "";
    private long userId;
    private long chatId;


    private static Queue<Temasekian> matricDataBase = new LinkedList<Temasekian>();
    List<Long> bannedList = new ArrayList<Long>();
    List<String> reportedList = new ArrayList<String>();


    public void reset() {
        System.out.println("RESET!!!!!");
        matricDataBase.clear();
        photos.clear();

    }

    public void alertUser() {

        for (Long i : chatIdList) {
            String text = "Hello " + chatIdToTemasekianName.get(i) + ", will you be eating tonight?\n" +
                    "/eating - Yes! I love comm hall dinner!\n" +
                    "/noteating - Neh... Not today..." +
                    "/whatsfordinner - Not sure... I see the menu first?";

            SendMessage message = new SendMessage();

            message.setChatId(i)
                    .setText(text);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadOurTHBot() {
        // Log
        systemLog("ourTHBot uploaded...");
        systemLog("ourTHBot is online!!");

        while (true) {

            if (isTimeToReset() && !hasReset) {
                systemLog("Reset Matric Queue & Dinner Pic List...");
                reset();
                hasReset = true;
            } else if (isTimeToNotify() && !hasNotify) {
                systemLog("Daily notification to all bot users...");
                alertUser();
                hasNotify = true;
            } else if (isAnyOtherTiming()) {
                hasReset = false;
                hasNotify = false;
            }

        }

    }


    public void onUpdateReceived(Update update) {

        try {

            // Update User Telegram Information
            obtainTelegramUserInformation(update);


            // First time user
            if (!temasekDataBase.containsKey(userId)) {


                // Log
                systemLog("New user registering...");

                Temasekian temasekian = new Temasekian();

                // Check if the update has a message and the message has text
                if (update.hasMessage() && update.getMessage().hasText()) {

                    String input = update.getMessage().getText();

                    switch (input) {

                        case "/start":
                            // 1st time registration
                            startsRegistration(temasekian);
                            break;

                        default:
                            registrationError(temasekian);
                            break;

                    }

                } else if (update.hasMessage() && update.getMessage().hasPhoto()) {

                    picRegistrationError(temasekian);

                }

                // Not First Time User
            } else {

                Temasekian temasekian = temasekDataBase.get(userId);

                updateTemasekianChatId(temasekian);

                String input = update.getMessage().getText();
                String text = "";

                if (update.hasMessage() && update.getMessage().hasText()) {

                    if (!input.equals("/start")) {

                        // Registering Information: Name, Matric Number & Block
                        registerInformation(input, update, temasekian);

                    } else {

                        welcomeMessage();

                    }


                    if (temasekian.isAdminMode()) {
                        // Admin Control
                        mastercontrol(input, temasekian);
                    } else {

                        if (temasekian.isReporting()) {
                            // Report User
                            reportUser(input, temasekian);
                        }

                        if (temasekian.isUpdating()) {
                            // Updating Temasekian's Details
                            updateTemasekianDetails(input, temasekian);
                        }

                        // React to various commands
                        mainBotFunctions(input, temasekian);
                    }

                    // View reported list
                    pendingReportedUsers(temasekian);

                } else if (update.hasMessage() && update.getMessage().hasPhoto()) {

                    uploadDinnerPic(update, temasekian);

                }

            }
        } catch (Exception e) {
            String input = update.getMessage().getText();
            String text = "\'" + input + "\' is not a valid command...";
            displayMessage(text);

            // Log
            systemLog("EXCEPTION: USER ENTERED INVALID COMMAND...");
        }


    }

    private void updateTemasekianChatId(Temasekian temasekian) {
        if (!temasekian.getChatId().equals(chatId)) {
            Long outdatedChatId = temasekian.getChatId();

            chatIdList.remove(outdatedChatId);
            chatIdList.add(chatId);
            chatIdToTemasekianName.remove(outdatedChatId);
            chatIdToTemasekianName.put(chatId, temasekian.getTemasekianName());
            temasekian.updateTemasekianChatId(chatId);

            // Log
            systemLog("There is an outdated chat ID - " + outdatedChatId +
                    "\nIt has been updated to " + chatId +
                    "\nchatIdList updated" +
                    "\nchatIdToTemasekianName updated" +
                    "\ntemasekian chat Id updated");
        }
    }

    private void pendingReportedUsers(Temasekian temasekian) {
        String id = Long.toString(userId);

        if (id.equals("24027079") && !reportedList.isEmpty() && !temasekian.isAdminMode()) {
            System.out.println("verrified that is eric");

            String text = "There are " + reportedList.size() + " case(s)";
            displayMessage(text);
        }
    }


    private void reportUser(String input, Temasekian temasekian) {

        String text = "";

        if (!temasekian.isSubmittingReason() && !input.equals("/home")) {
            String matric = input.toUpperCase();

            if (isValidMatric(matric)) {
                text = "Reason to ban " + matric + ":";
                displayMessage(text);

                temasekian.updateReportedMatric(matric);
                temasekian.submittingReason();

                // Log
                userLog(temasekian, "User is banning " + temasekian.getReportedMatric());

            } else {
                text = "That looks like an incorrect matric number... Please key in a valid one!";
                displayMessage(text);
                text = "If you wish to stop reporting user, press /home";
                displayMessage(text);


                // Log
                userLog(temasekian, "Incorrect matric number: " + matric);

            }
        } else if (temasekian.isSubmittingReason() && !input.equals("/home")) {
            String reason = input;

            text = "Thank you for reporting! We will look into the matter and hope that this user will not be using this bot any more!";
            displayMessage(text);

            temasekian.updateReportedReason(reason);
            temasekian.submittedReason();
            temasekian.reported();

            reportedList.add("User " + userId + " - " + temasekian + " has reported " + temasekian.getReportedMatric() + " for " + temasekian.getReportedReason());

            // Log
            userLog(temasekian, "Reported " + temasekian.getReportedMatric() + " for " + temasekian.getReportedReason());

            temasekian.updateReportedReason("");
            temasekian.updateReportedMatric("");


        } else if (input.equals("/home")) {
            temasekian.reported();

            text = "Stopped reporting!";
            displayMessage(text);

            // Log
            userLog(temasekian, "User stopped reporting user...");

        }
    }


    private boolean isTimeToReset() {

        Calendar rightNow = Calendar.getInstance();
        Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
        Integer min = rightNow.get(Calendar.MINUTE);

        return hour.equals(resetHour) && min.equals(resetMin);

    }

    private boolean isTimeToNotify() {

        Calendar rightNow = Calendar.getInstance();
        Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
        Integer min = rightNow.get(Calendar.MINUTE);

        return hour.equals(notifyHour) && min.equals(notifyMin);

    }

    private boolean isAnyOtherTiming() {
        Calendar rightNow = Calendar.getInstance();
        Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
        Integer min = rightNow.get(Calendar.MINUTE);

        if (isTimeToReset()) {
            return false;
        }

        if (isTimeToNotify()) {
            return false;
        }

        return true;
    }


    // Temasekian Registration
    private void obtainTelegramUserInformation(Update update) {

        userFirstName = update.getMessage().getChat().getFirstName();
        userLastName = update.getMessage().getChat().getLastName();
        userUsername = update.getMessage().getChat().getUserName();
        userId = update.getMessage().getChat().getId();
        chatId = update.getMessage().getChatId();

    }

    private void registerInformation(String input, Update update, Temasekian temasekian) {

        if (!input.equals("/start") && !temasekian.isRegistered()) {

            if (!temasekian.isTemasekianNameRegistered()) {
                // First time update name
                enterNameEntry(update, temasekian);

            } else if (!temasekian.isTemasekianMatricRegistered()) {
                // First time update matric
                enterMatricEntry(update, temasekian);

            } else if (!temasekian.isTemasekianBlkRegistered()) {

                //First time update block
                enterBlkEntry(update, temasekian);

            }
        }
    }

    private void startsRegistration(Temasekian temasekian) {

        welcomeMessage();
        temasekDataBase.put(userId, temasekian);

        // Log
        userLog(temasekian, "Starts registration...");

    }

    private void welcomeMessage() {

        String text = "Welcome Temasekian " + userFirstName + "! I am your Dinner Number Generator!";
        displayMessage(text);

        text = "Please provide me with the necessary information to get things started!";
        displayMessage(text);

        text = "What will you want to be addressed as? ";
        displayMessage(text);

    }

    private void enterNameEntry(Update update, Temasekian temasekian) {

        String name = update.getMessage().getText();

        temasekian.updateTemasekianName(name);
        temasekian.updateTemasekianChatId(chatId);
        temasekian.updateTemasekianTeleFirstName(userFirstName);
        temasekian.updateTemasekianTeleLastName(userLastName);
        temasekian.updateTemasekianTeleUsername(userUsername);
        temasekian.updateTemasekianUserId(userId);
        chatIdList.add(chatId);
        chatIdToTemasekianName.put(chatId, temasekian.getTemasekianName());

        String text = "Ok " + name + "! So what's your matric number?";
        displayMessage(text);

        // Log
        userLog(temasekian, "Temasekian name updated: " + temasekian.getTemasekianName());

    }

    private void enterMatricEntry(Update update, Temasekian temasekian) {

        String matric = update.getMessage().getText().toUpperCase();

        String text = "";

        if (isValidMatric(matric)) {

            temasekian.updateTemasekianMatric(matric);
            matricToUserId.put(matric, userId);

            text = matric + " sounds like someone I will date! Lastly "
                    + temasekian.getTemasekianName() + ", which block you come from?";
            displayMessage(text);

            // Log
            userLog(temasekian, "Temasekian matric updated: " + temasekian.getTemasekianMatric());

        } else {

            text = "Don't try and scam me leh... Key in a valid matric can anot?";
            displayMessage(text);

            // Log
            userLog(temasekian, "Incorrect matric number: " + matric);

        }

    }

    private void enterBlkEntry(Update update, Temasekian temasekian) {

        String blk = update.getMessage().getText().toUpperCase();

        String text = "";

        if (isValidBlk(blk)) {

            temasekian.updateTemasekianBlk(blk);
            temasekian.registered();

            text = "Welcome " + temasekian.getTemasekianName() + " ("
                    + temasekian.getTemasekianMatric() + "), from Block "
                    + temasekian.getTemasekianBlk() + ", aboard!";
            displayMessage(text);

            text = "Do not worry if you are clueless now. You can always /help and you can definitely find some answers!";
            displayMessage(text);

            text = "If you are unsure, just press /help";
            displayMessage(text);

            // Log
            userLog(temasekian, "Temasekian blk updated: " + temasekian.getTemasekianBlk() + "\n" +
                    "Temasekian REGISTERED");

        } else {

            text = "Block " + blk + "? TH got Block " + blk + " meh? I think you may have typed wrongly... Please key in the correct block!";
            displayMessage(text);

            // Log
            userLog(temasekian, "Incorrect Block number: " + blk);

        }

    }

    private void registrationError(Temasekian temasekian) {

        if (bannedList.isEmpty()) {
            String text = "Oh no we have just finished some maintenance and require you to log in again...";

            displayMessage(text);

            text = "Please press /start to get started!";
            displayMessage(text);

            text = "Sorry for the inconvenience caused...";
            displayMessage(text);

            // Log
            userLog(temasekian, "ERROR: System has been updated... Database reset...\n" +
                    "User needs to registered himself again...");


        } else {

            for (Long i : bannedList) {
                if (i.equals(userId)) {
                    String text = "Sorry you have been banned! Please contact the admins for more information!";
                    displayMessage(text);

                    // Log
                    System.out.println("!!!");
                    System.out.println();
                    userLog(temasekian, "ALERT - Banned Bot User " + userId + " - " + userUsername + " (" + userFirstName + ", " + userLastName + ") tries to use ourTHBot...");
                    System.out.println();
                    System.out.println("!!!");
                }
            }

        }

    }

    private void picRegistrationError(Temasekian temasekian) {

        String text = "Oh no you have not register yourself... Please press /start to get started!";
        displayMessage(text);

        // Log
        userLog(temasekian, "ERROR: User used a photo for registration!");

    }


    // Main Function of ourTHBot
    private void mainBotFunctions(String input, Temasekian temasekian) {

        switch (input) {

            case "/start":
                if (temasekian.isRegistered()) {
                    String text = "You have registered! Do not worry! If you want to update your particulars, you can use /help to get more information!";
                    displayMessage(text);
                }
                break;

            case "/help":
                getHelp(temasekian);
                break;

            case "/eating":
                iseating(temasekian);
                break;

            case "/noteating":
                queueMatric(matricDataBase, temasekian);
                break;

            case "/whatsfordinner":
                checkDinnerMenu(temasekian);
                break;

            case "/numberpls":
                dequeueMatric(temasekian, matricDataBase);
                break;

            case "/updatename":
                updateName(temasekian);
                break;

            case "/updatematric":
                updateMatric(temasekian);
                break;

            case "/updateblk":
                updateBlk(temasekian);
                break;

            case "/mastercontrol":
                adminControl(temasekian);
                break;

            case "/report":
                report(temasekian);
                break;

            case "/feedback":
                break;

            case "/get":
                getInfo(temasekian);

                        /*default:
                            text = "-" + input + "- is not a valid command";
                            displayMessage(text);
                            break;
                    */
        }

    }

    private void getInfo(Temasekian temasekian) {
        String text = "Your user ID: " + temasekian.getUserId();
        displayMessage(text);

        // Log
        userLog(temasekian, "User requested for User ID");
    }

    private void getHelp(Temasekian temasekian) {

        String text =
                "Here are the common commands you can use: \n" +
                        "/eating - You are consuming dinner today and do not wish to donate your matric number.\n" +
                        "/noteating - You are feeling generous today and feel like helping out some hungry souls.\n" +
                        "/whatsfordinner - You are unsure and want to see what's for dinner tonight before deciding\n" +
                        "/numberpls - You are feeling extra hungry today and wish to have more food!\n\n" +
                        "/report - You found someone using non-TH resident's matric and wish to report him\n" +
                        "/feedback - You found some issue with the bot and wish to give us some feedback!\n\n " +
                        "/updatename - Suddenly do not feel like being called " + temasekian.getTemasekianName() + " and wish to change your name.\n" +
                        "/updatematric - For somewhat reason your matric number chances, you can update to your new matric number.\n" +
                        "/updateblk - Change block but still one Temasek!";
        displayMessage(text);

        // Log
        userLog(temasekian, "User requesting for help...");

    }

    private void iseating(Temasekian temasekian) {

        String text = "";

        if (!temasekian.isMatricDonated()) {

            text = temasekian.getTemasekianName() + ", enjoy your meal!";
            displayMessage(text);

            text = "So what's for dinner today? Upload a picture of today's dinner for others to see!";
            displayMessage(text);

            // Log
            userLog(temasekian, "User indicated that he/she is eating today's dinner!");

        } else {

            text = "You have donated your matric number and someone may have used it..." +
                    " Don't worry you can use /numberpls to get a number for your dinner!";
            displayMessage(text);

            // Log
            userLog(temasekian, "User has donated his/her number earlier on and thus prompted to use /numberpls to get his/her dinner...");

        }

    }

    private void queueMatric(Queue<Temasekian> matricDataBase, Temasekian temasekian) {

        String text = "";

        if (!temasekian.isMatricDonated()) {

            matricDataBase.add(temasekian);
            //String matric = temasekian.getTemasekianMatric();

            //matricDataBase.push(matric);

            temasekian.donateMatric();

            text = "Not eating dinner today? Thanks for donating your instead, someone will definitely appreciate it!";
            displayMessage(text);

            // Log
            userLog(temasekian, "Not Eating - Matric Number Donated: " + temasekian.getTemasekianMatric() +
                    "\nNumber of Matric in system: " + matricDataBase.size());

        } else {

            text = "Sorry I am abit slow and laggy but you have donated your matric number! Don't worry!";
            displayMessage(text);

            // Log
            userLog(temasekian, "Not Eating - Matric Number has been donated\n" +
                    "Number of Matric in system: " + matricDataBase.size());

        }

    }

    // Problem reset meal counter
    private void dequeueMatric(Temasekian temasekian, Queue<Temasekian> matricDataBase) {

        String text = "";

        if (!matricDataBase.isEmpty()) {

            if (temasekian.getMealCounter() > 1) {

                Temasekian donor = matricDataBase.poll();
                String donorMatric = donor.getTemasekianMatric();
                String donorName = donor.getTemasekianName();
                String donorBlk = donor.getTemasekianBlk();
                temasekian.decrMealCounter();

                text = "Here's a number for you: " + donorMatric;
                displayMessage(text);

                text = "If you see " + donorName + " from " + donorBlk + " blk around, remember to say thank you!";
                displayMessage(text);

                // Log
                userLog(temasekian, "Number Pls - Extra Matric Number: " + donorMatric +
                        "\nDonor - " + donor +
                        "\nNumber of Matric in system: " + matricDataBase.size() +
                        "\nNumber of Matric he still can take: " + temasekian.getMealCounter());


            } else {

                text = "Wa so hungry meh? Take 3 liao still hungry ar? Save some numbers for the others leh...";
                displayMessage(text);

                // Log
                userLog(temasekian, "Number Pls - User taken 3 matric and is NOT ALLOWED to take anymore for today!" +
                        "\nNumber of Matric in system: " + matricDataBase.size());

            }

        } else {

            text = "Sorry there is no more numbers.... Please wait for more people who are not eating to donate their numbers...";
            displayMessage(text);

            // Log
            userLog(temasekian, "Number Pls - NO MORE NUMBER IN THE SYSTEM" +
                    "\nNumber of Matric in system: " + matricDataBase.size());

        }

    }

    private void checkDinnerMenu(Temasekian temasekian) {

        String text = "";

        if (photos.size() == 0) {

            text = "No one has shared what's for dinner tonight... You can come back in awhile again to check if anyone has shared today's dinner menu!";
            displayMessage(text);

            // Log
            userLog(temasekian, "User checks for dinner menu..." +
                    "\nNo one has shared any dinner pictures yet...");

        } else {

            displayDinnerMessage(temasekian);

        }

    }

    private void displayDinnerMessage(Temasekian temasekian) {

        SendPhoto photo = new SendPhoto();
        String dinnerPic = "";

        for (int i = 0; i < photos.size(); i++) {

            dinnerPic = photos.get(i);
            photo.setChatId(chatId).setPhoto(dinnerPic);

            try {
                sendPhoto(photo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        displayMessage(dinnerCaption);

        // Log
        userLog(temasekian, "User requested for dinner pictures..." +
                "\nDisplay " + photos.size() + " dinner picture(s)");

    }

    // To do: Include multiple donor name in the pic caption
    private void uploadDinnerPic(Update update, Temasekian temasekian) {

        List<PhotoSize> photosUploaded = update.getMessage().getPhoto();
        String text = "";

        String dinnerPic = photosUploaded.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileId)
                        .reversed())
                .findFirst()
                .orElse(null)
                .getFileId();

        photos.add(dinnerPic);

        if (!temasekian.hasSharedDinnerPic()) {

            dinnerCaption = temasekian.getTemasekianName() + " has kindly shared what's for today's dinner with everyone!";
            temasekian.shareDinnerPic();
            text = "Thank you " + temasekian.getTemasekianName() + " for sharing what is for dinner tonight";

            displayMessage(text);

        }


        // Log
        userLog(temasekian, temasekian.getTemasekianName() + " has shared what is for dinner tonight!" +
                "\nNumber of dinner pictures uploaded: " + photos.size());

    }

    private void report(Temasekian temasekian) {

        temasekian.reporting();
        String text = "Please use the report function responsibly! Those who abuse this function will be banned!";
        displayMessage(text);

        text = "Key in the matric number you wish to report:";
        displayMessage(text);

        // Log
        userLog(temasekian, "User attempt to report another user!");

    }


    // Update Temasekian details
    private void updateTemasekianDetails(String input, Temasekian temasekian) {

        if (!input.equals("/home")) {
            if (temasekian.getTemasekianName().equals("")) {

                updateTemasekianName(input, temasekian);

            }

            if (temasekian.getTemasekianMatric().equals("")) {

                updateTemasekianMatric(input, temasekian);

            }

            if (temasekian.getTemasekianBlk().equals("")) {

                updateTemasekianBlk(input, temasekian);

            }
        } else if (temasekian.isUpdating() && input.equals("/home")) {
            String text = "Stopped updating!";
            displayMessage(text);

            if (temasekian.getTemasekianMatric().equals("")) {
                String previousMatric = temasekian.getPreviousMatric();
                temasekian.updateTemasekianMatric(previousMatric);
                temasekian.updatePreviousMatric("");
            }

            if (temasekian.getTemasekianBlk().equals("")) {
                String previousBlk = temasekian.getPreviousBlk();
                temasekian.updateTemasekianBlk(previousBlk);
                temasekian.updatePreviousBlk("");
            }

            temasekian.updated();

            // Log
            userLog(temasekian, "User stopped updating particulars");

        }

    }

    private void updateTemasekianName(String input, Temasekian temasekian) {

        String text = "";
        String name = input;
        temasekian.updateTemasekianName(name);
        temasekian.updated();

        text = "Ok now I shall call you " + name;
        displayMessage(text);

        // Log
        userLog(temasekian, "Changed Temasekian Name to: " + temasekian.getTemasekianName());

    }

    private void updateTemasekianMatric(String input, Temasekian temasekian) {

        String text = "";
        String matric = input.toUpperCase();

        if (isValidMatric(matric)) {

            temasekian.updateTemasekianMatric(matric);
            temasekian.updated();

            text = "Ok got it!  " + matric;
            displayMessage(text);

            // Log
            userLog(temasekian, "Changed Temasekian Matric to: " + temasekian.getTemasekianMatric());

        } else {

            text = "That matric number like wrong leh... Key in a valid one please!";
            displayMessage(text);
            text = "If you wish to stop updating, press /home";
            displayMessage(text);


            // Log
            userLog(temasekian, "Incorrect matric number: " + matric);

        }

    }

    private void updateTemasekianBlk(String input, Temasekian temasekian) {

        String text = "";
        String blk = input.toUpperCase();

        if (isValidBlk(blk)) {

            temasekian.updateTemasekianBlk(blk);
            temasekian.updated();

            text = "Nice! Updated to Block " + blk;
            displayMessage(text);

            // Log
            userLog(temasekian, "Changed Temasekian Block to: " + temasekian.getTemasekianBlk());

        } else {

            text = "Block " + blk + "? TH got Block " + blk + " meh? I think you may have typed wrongly... Please key in the correct block!";
            displayMessage(text);

            text = "If you wish to stop updating, press /home";
            displayMessage(text);


            // Log
            userLog(temasekian, "Incorrect Block number: " + blk);

        }

    }

    private void updateName(Temasekian temasekian) {

        String text = "Don't like being called " + temasekian.getTemasekianName()
                + " anymore? What will you like to be address as then?";
        displayMessage(text);

        temasekian.updateTemasekianName("");
        temasekian.updating();

        // Log
        userLog(temasekian, "Changing Temasekian name");

    }

    private void updateMatric(Temasekian temasekian) {

        String text = "Strange? Your matric number changed? What's your new matric now?";
        displayMessage(text);
        temasekian.updatePreviousMatric(temasekian.getTemasekianMatric());

        temasekian.updateTemasekianMatric("");
        temasekian.updating();

        // Log
        userLog(temasekian, "Changing Temasekian matric");

    }

    private void updateBlk(Temasekian temasekian) {

        String text = "So I heard you changed your Block? What's your new Block now?";
        displayMessage(text);
        temasekian.updatePreviousBlk(temasekian.getTemasekianBlk());

        temasekian.updateTemasekianBlk("");
        temasekian.updating();

        // Log
        userLog(temasekian, "Changing Temasekian block");

    }


    // Admin Controls
    private void mastercontrol(String input, Temasekian temasekian) {


        if (!temasekian.isAdmin()) {
            // Attempt Log In
            adminLogIn(input, temasekian);

        } else {
            // Admin Access Granted
            if (!temasekian.isBanningUser() && !temasekian.isUnbanningUser() && !temasekian.isExchangingForUserId()) {
                // All Admin Functions
                adminFunctions(input, temasekian);

            } else if (temasekian.isBanningUser()) {
                if (input.equals("/adminhome")) {
                    // Return to Master Control
                    adminHome(temasekian);
                } else {
                    // Attempt to ban user
                    banUser(input, temasekian);

                }
            } else if (temasekian.isUnbanningUser()) {

                if (input.equals("/adminhome")) {
                    // Return to Master Control
                    adminHome(temasekian);
                } else {
                    // Attempt to remove ban on user
                    unbanUser(input, temasekian);
                }
            } else if (temasekian.isExchangingForUserId()) {
                if (input.equals("/adminhome")) {
                    adminHome(temasekian);

                } else {
                    requestUserId(temasekian, input);
                }
            } else if (temasekian.isAddingAdmins()) {

            }


        }


    }

    private void requestUserId(Temasekian temasekian, String input) {
        String matric = input.toUpperCase();
        String text = "";

        if (isValidMatric(matric)) {
            long userId = matricToUserId.get(matric);
            text = "The user ID is: " + userId;
            displayMessage(text);

            temasekian.exchangedForUserId();

            // Log
            adminLog(temasekian, "Admin exchanged martic number for userId... " + matric + " -> " + userId);
        } else {
            text = "That's an invalid matric number! Please try again!";
            displayMessage(text);

            text = "If you wish to stop requesting for user ID, press /adminhome";
            displayMessage(text);

            // Log
            adminLog(temasekian, "Admin entered an invalid matric number...");
        }
    }

    private void unbanUser(String input, Temasekian temasekian) {

        try {
            Long bannedId = Long.parseLong(input);

            if (bannedList.contains(bannedId)) {
                bannedList.remove(bannedId);

                String text = "User " + bannedId + " - " + " has been removed from the ban...";
                displayMessage(text);
                temasekian.unbanned();

                // Log
                adminLog(temasekian, "Admin has removed ban on User " + bannedId);

            } else {

                String text = bannedId + " is an invalid user id ";
                displayMessage(text);

                text = "If you wish to stop un-banning a user, /adminhome";
                displayMessage(text);

                // Log
                adminLog(temasekian, "Admin has attempted to remove ban on User " + bannedId + " but failed...");

            }
        } catch (NumberFormatException e) {
            String text = "That is an invalid user id ";
            displayMessage(text);

            text = "If you wish to stop un-banning a user, /adminhome";
            displayMessage(text);

            // Log
            adminLog(temasekian, "EXCEPTION: Admin has attempted to remove ban on User but failed...");
        }
    }

    private void banUser(String input, Temasekian temasekian) {

        try {
            Long bannedId = Long.parseLong(input);
            String text = "";

            if (temasekDataBase.containsKey(bannedId)) {
                Temasekian bannedTemaksian = temasekDataBase.get(bannedId);
                bannedList.add(bannedId);
                temasekDataBase.remove(bannedId);

                text = "User " + bannedId + " - " + bannedTemaksian + " has been banned...";
                displayMessage(text);
                temasekian.banned();

                // Log
                adminLog(temasekian, "Admin has banned User " + bannedId + " - " + bannedTemaksian);

            } else {
                text = "You have enter an invalid user id...";
                displayMessage(text);

                text = "If you wish to stop banning a user, /adminhome";
                displayMessage(text);

                // Log
                adminLog(temasekian, "Admin attempts to ban a user but entered an invalid matric number...");
            }
        } catch (NumberFormatException e) {
            String text = "";
            text = "You have enter an invalid user id...";
            displayMessage(text);

            text = "If you wish to stop banning a user, /adminhome";
            displayMessage(text);

            // Log
            adminLog(temasekian, "EXCEPTION: Admin attempts to ban a user but entered an invalid matric number...");
        }
    }

    private void adminLogIn(String input, Temasekian temasekian) {

        String text = "";

        if (retryCounter > 1) {

            switch (input) {
                case "erome":
                case "thjcrc":
                    text = "Welcome Admin " + temasekian.getTemasekianName() + "! What will you like to do?";
                    displayMessage(text);

                    temasekian.adminPermissionGranted();

                    // Log
                    adminLog(temasekian, "Admin has logged in!");

                    break;

                default:

                    retryCounter--;
                    text = "Sorry that is an invalid password! You have " + retryCounter + " attempt(s) left!";
                    displayMessage(text);

                    // Log
                    adminLog(temasekian, "Invalid password, " + retryCounter + " attempt(s) left...");

                    break;
            }

        } else {
            text = "Sorry you have used up all the available attempts! Please try again next time.";
            displayMessage(text);

            text = "You will be logged out of the master control!";
            displayMessage(text);

            resetRetryCounter();
            temasekian.deactivateAdminMode();

            // Log
            adminLog(temasekian, "User used up all 3 attempts to log in...");

        }

    }

    private void adminFunctions(String input, Temasekian temasekian) {

        String text = "";

        switch (input) {
            case "/logout":
                text = "You have sucessfully logged out!";
                displayMessage(text);
                resetRetryCounter();
                temasekian.adminPermissionRevoked();
                temasekian.deactivateAdminMode();

                // Log
                adminLog(temasekian, "Admin has logged out!");
                break;

            case "/adminhelp":
                text = "These are the available commands for ADMIN MODE: \n" +
                        "/addAdmins - Add more admins\n" +
                        "/broadcast - To broadcase message to all ourTHBot users\n" +
                        "/viewreport - To view the list of reported user(s)\n" +
                        "/logout - To exit Admin mode and use the feature of ourTHBot\n";
                displayMessage(text);

                // Log
                adminLog(temasekian, "Admin accessing help...");

                break;

            case "/addAdmins":
                addAdmins(temasekian);
                break;

            case "/broadcast":

                break;

            case "/banuser":
                text = "Please enter the user id to ban:";
                displayMessage(text);

                temasekian.banning();

                // Log
                adminLog(temasekian, "Admin is banning user...");
                break;

            case "/unbanuser":
                text = "Please enter the user id to un-ban:";
                displayMessage(text);

                temasekian.unbanning();

                // Log
                adminLog(temasekian, "Admin is removing ban on a user...");
                break;

            case "/viewreport":

                viewReportedUser(temasekian);
                break;

            case "/clr":
                clearReportedUsers(temasekian);
                break;

            case "/getuserId":
                getUserId(temasekian);
                break;


            default:
                text = "-" + input + "- is not a valid command in ADMIN MODE!";
                displayMessage(text);
                text = "Please /logout to use the features of the TH bot";
                displayMessage(text);

                // Log
                adminLog(temasekian, "Invalid command (" + input + ")");
                break;
        }

    }

    private void addAdmins(Temasekian temasekian) {
        String text = "Please enter the user id of admin:";
        displayMessage(text);

        text = "If you wish to stop adding admins, press /adminhome";
        displayMessage(text);

        temasekian.addingAdmins();

        // Log
        adminLog(temasekian, "Admin adding more admins...");
    }

    private void getUserId(Temasekian temasekian) {
        String text = "Key in the matric number:";
        displayMessage(text);

        temasekian.exchangingForUserId();

        // Log
        adminLog(temasekian, "Requesting for matric number to exchange for Telegram user id");

    }

    private void viewReportedUser(Temasekian temasekian) {

        String text = "";

        if (reportedList.isEmpty()) {

            text = "There is no reported user...";
            displayMessage(text);

            // Log
            adminLog(temasekian, "Admin attempts to view reported list but list is EMPTY...");

        } else {
            for (String i : reportedList) {
                text = i;
                displayMessage(text);
            }

            // Log
            adminLog(temasekian, "Admin is viewing reported list...");

        }
    }

    private void clearReportedUsers(Temasekian temasekian) {

        reportedList.clear();

        String text = "Reported list has been cleared...";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin cleared ALL reported users!");

    }

    private void adminHome(Temasekian temasekian) {
        temasekian.exchangedForUserId();
        temasekian.unbanned();
        temasekian.banned();
        temasekian.addedAdmins();

        String text = "You have returned to master control...";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin returned to Admin Home...");
    }

    private void adminControl(Temasekian temasekian) {

        temasekian.activateAdminMode();

        String text = "Please enter admin password: ";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Attemping Admin Log In...");

    }


    // Verifications
    // Simple verification of matric numbers
    private boolean isValidMatric(String matric) {

        if (matric.charAt(0) != 'A' || matric.length() != 9) {

            return false;

        } else {

            Character[] checkLetter = {'Y', 'X', 'W', 'U', 'R', 'N', 'M', 'L', 'J', 'H', 'E', 'A', 'B'};

            int d1 = Character.getNumericValue(matric.charAt(1));
            int d2 = Character.getNumericValue(matric.charAt(2));
            int d3 = Character.getNumericValue(matric.charAt(3));
            int d4 = Character.getNumericValue(matric.charAt(4));
            int d5 = Character.getNumericValue(matric.charAt(5));
            int d6 = Character.getNumericValue(matric.charAt(6));
            int d7 = Character.getNumericValue(matric.charAt(7));

            int letterValue = (d1 + d2 + d3 + d4 + d5 + d6 + d7) % 13;

            Character letter = checkLetter[letterValue];
            Character givenLetter = matric.charAt(8);

            if (letter.equals(givenLetter)) {

                return true;

            }

        }

        return false;

    }

    // Simple verification of Block
    private boolean isValidBlk(String blk) {

        String[] blkId = {"A", "B", "C", "D", "E"};

        for (String i : blkId) {

            if (i.equals(blk)) {

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


    private void displayMessage(String text) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId)
                .setText(text);
        tryExecute(message);
    }

    private void tryExecute(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void resetRetryCounter() {

        retryCounter = 3;

    }


    // Logging
    private void userLog(Temasekian temasekian, String message) {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("USER MODE");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Chat ID - " + chatId);
        System.out.println("Bot User " + userId + " - " + userUsername + " (" + userFirstName + ", " + userLastName + ")");
        System.out.println("Temasekian - " + temasekian);
        System.out.println(message);
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::");
    }

    private void adminLog(Temasekian temasekian, String message) {

        System.out.println("=================================================");
        System.out.println("ADMIN MODE");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Chat ID - " + chatId);
        System.out.println("Bot User " + userId + " - " + userUsername + " (" + userFirstName + ", " + userLastName + ")");
        System.out.println("Temasekian - " + temasekian);
        System.out.println(message);
        System.out.println("=================================================");

    }

    private void systemLog(String message) {

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("SYSTEM");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println(message);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

}


// Extra Code

    /*
    // Temasekian Information
    private static String name = "";
    private static String matric = "";
    private static String blk = "";
    private static boolean isRegistered = false;
    private static boolean matricDonated = false;
*/

/*
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

            } else if (input.equals("/noteating")){

                queueMatric(matricDataBase);

            } else if (input.equals("/numberpls")) {

                dequeueMatric(matricDataBase);
            }

            // To be continued...
    private boolean confirmReset() {
        String input = "";
        String output = "";

        SendMessage message = new SendMessage();

        try {
            message.setChatId(chatId)
                    .setText("Are you sure that you want to reset the bot?");
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

        return false;
    }



    // Logging
    ==================================================
    private void log(String first_name, String last_name, String user_id, String txt, String bot_answer) {
        System.out.println("----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + first_name + " " + last_name + ". (id = " + user_id + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + bot_answer);
        System.out.println();
    }
    ==================================================
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
    ==================================================

*/

// Upload photos
/*                dinnerPic = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileId)
                        .reversed())
                        .findFirst()
                        .orElse(null)
                        .getFileId();
*/
                /*
                // Know file_id
                String f_id = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getFileId();
                // Know photo width
                int f_width = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getWidth();
                // Know photo height
                int f_height = photos.stream()
                        .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                        .findFirst()
                        .orElse(null).getHeight();

                // Set photo caption
                String caption = "file_id: " + f_id + "\nwidth: " + Integer.toString(f_width) + "\nheight: " + Integer.toString(f_height);
                SendPhoto msg = new SendPhoto()
                        .setChatId(chatId)
                        .setPhoto(f_id)
                        .setCaption(caption);
                try {
                    sendPhoto(msg); // Call method to send the photo with caption
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                */


                /*
                       System.out.println("updated the user infor");
        bannedList.add((long) 1);

        for (Long i : bannedList) {
            System.out.println("Enterfor loop...");
            if (temasekDataBase.containsKey(i)) {
                System.out.println("Enter if...");
                String text = "Sorry you have been banned! Please contact the admins for more information!";
                displayMessage(text);

                // Log
                System.out.println("ALERT - Banned Bot User " + userId + " - " + userUsername + " (" + userFirstName + ", " + userLastName + ") tries to use ourTHBot...");
            } else {

                System.out.println("Not banned");
                 */
