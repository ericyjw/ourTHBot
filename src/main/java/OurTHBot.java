import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.print.DocFlavor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class OurTHBot extends TelegramLongPollingBot {

  private static boolean MAINTENANCE = false;

  private static boolean hasReset = false;
  private static boolean hasNotify = false;
  private static boolean SEMESTER = true;

  private static int RESET_HOUR = 22;
  private static int RESET_MIN = 0;
  private static int NOTIFY_HOUR = 17;
  private static int NOTIFY_MIN = 0;


  // DataBases that do not need to reset
  private static HashMap<Long, Temasekian> temasekDataBase = new HashMap<>();
  private static HashMap<String, Long> matricToUserId = new HashMap<>();
  private static HashMap<Long, String> chatIdToTemasekianName = new HashMap<>(); // redundant
  private static Set<Long> chatIdList = new HashSet<>();
  private static Set<Long> bannedList = new HashSet<>();
  private static List<String> reportedList = new ArrayList<>();
  private static List<String> feedbackList = new ArrayList<>();
  private static Set<Long> admins = new HashSet<>();
  private static Set<Long> dev = new HashSet<>();

  // private static HashMap<Long, String> contactMessage = new HashMap<>(); // need to clear it after convo
  private static Set<String> contactedList = new HashSet<>();

  // DataBases that need to reset daily
  private static Queue<Temasekian> matricDataBase = new LinkedList<>();
  private static List<String> photos = new ArrayList<>();
  private static List<String> dinnerContributors = new ArrayList<>();
  private static String dinnerCaption = "";
  private static String broadcastMessage = "";


  // BotUser Information
  private String userFirstName = "";
  private String userLastName = "";
  private String userUsername = "";
  private Long userId;
  private Long chatId;


  private static String UPDATE = "We have just upgraded ourselves!\n\n" +
    "New Features:\n" +
    "- Timer corrected\n" +
    "- Admin able to delete inappropriate dinner pictures\n" +
    "- Admin can broadcast message to its user\n" +
    "- Added level of security to prevent non-user from using the bot\n" +
    "- Admin get instant notification from feedback and report\n" +
    "- Added ability to thank the person for extra matric\n" +
    "- Added PM function";


  // DONE & TESTED
  // Valid input method //
  // View Feedback //
  // Admin Clear Feedback //
  // Restructure main function //
  // Add admin - existing admin //
  // View admins //

  // Timing is wrong 0233 on comp -> 1833 on server //
  // Admin controls over dinner pic uploaded (number of pictures that can be uploaded, quality of photos uploaded) //
  // Maintenance but admin 24027079 can still use to test the bot //
  // "Invalid getuserId for not registered member //
  // Admin controls on broadcasting message //
  // User log in after /start //
  // Instant update of admin for reports and feedback //
  // Size and user //
  // Option to say thanks after obtaining the extra matric - sayThanks //
  // Dinner caption //
  // Pm & contact us //
  // Test out the verification and registeration //
  // Error feedback give /home option //
  // Broadcast header //
  // Test out message sent


  // DONE - TO BE TESTED

  // Qualitt check function
  // Vacation notification


  // TO DO
  // Export daily log to excel/google sheet
  // Calendar option
  // Lounge booking


  // Check for synchronised updates across all data structures!

  public void uploadOurTHBot() {
    // Log
    systemLog("ourTHBot uploaded...");
    systemLog("ourTHBot is online!!");

    do {

      if (SEMESTER) {

        if (isTimeToReset() && !hasReset) {

          systemLog("Reset Matric Queue & Dinner Pic List...");
          reset();

          /*
          SendMessage message = new SendMessage();
          long id = 24027079;
          message.setChatId(id)
            .setText("Bot Reset");
          tryExecute(message);
          */

          hasReset = true;

        } else if (isTimeToNotify() && !hasNotify) {

          systemLog("Daily notification to all bot users...");
          alertUser();

          /*
          SendMessage message = new SendMessage();
          long id = 24027079;
          message.setChatId(id)
            .setText("Bot Notify");
          tryExecute(message);
          */

          hasNotify = true;

        } else if (isAnyOtherTiming()) {

          hasReset = false;
          hasNotify = false;

        }
      }

    } while (true);

  }

  public void onUpdateReceived(Update update) {

    if (MAINTENANCE && !update.getMessage().getChatId().equals(Long.parseLong("24027079"))) {

      String text = "Sorry ourTHBot is getting some upgrades... Please come back later!";

      SendMessage message = new SendMessage();
      long id = update.getMessage().getChatId();
      message.setChatId(id)
        .setText(text);
      tryExecute(message);

      // Log
      systemLog("A user tried to use the bot during the maintenance...");

    } else {

        try {

          // Update User Telegram Information
          obtainTelegramUserInformation(update);


          // First time user
          if (!temasekDataBase.containsKey(userId)) {


            // Log
            systemLog("New user registering...");

            if (MAINTENANCE) {
              String maintain = "MAINTENANCE MODE: ON";
              displayMessage(maintain);
            }

            Temasekian temasekian = new Temasekian();

            // Check if the update has a message and the message has text
            if (update.hasMessage() && update.getMessage().hasText()) {

              String input = update.getMessage().getText();

              switch (input) {

                case "/start":
                  // 1st time registration
                  verifyUser(temasekian);
                  //startsRegistration(temasekian);
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

            //updateTemasekianChatId(temasekian);

            String input = update.getMessage().getText();

            if (update.hasMessage() && update.getMessage().hasText()) {

           /* if (!input.equals("/start")) {

              if(!temasekian.isVerified()) {

                verification(temasekian, input);

              } else {

                // Registering Information: Name, Matric Number & Block
                registerInformation(input, update, temasekian);

              }
            }
          */

              if (!temasekian.isVerified()) {

                verification(temasekian, input);

              } else if (!temasekian.isRegistered()) {

                registerInformation(input, temasekian);

              } else if (temasekian.isAdminMode()) {
                // Admin Mode
                mastercontrol(input, temasekian);

              } else {

                if (SEMESTER) {
                  // User Mode
                  userControl(input, temasekian);
                } else {

                  String text = "Vacation Time! The bot will not be active until school starts!";
                  displayMessage(text);

                  // Log
                  systemLog("Someone attempted to used the bot during vacation!");

                }

              }


            } else if (update.hasMessage() && update.getMessage().hasPhoto()) {

              uploadDinnerPic(update, temasekian);

            }

          }

        } catch (Exception e) {
          String input = update.getMessage().getText();
          String text = "Exception thrown... Please inform the admin about this issue!\n" +
            "Input that causes the exception - " + input;
          displayMessage(text);

          // Log
          systemLog("EXCEPTION: USER ENTERED INVALID COMMAND...");
          System.out.println("Exception:");
          System.out.println(e);
        }

    }


  }


  // Temasekian Verification
  private void verifyUser(Temasekian temasekian) {

    String text = "This bot is currently for TH residents only! Please enter the TH exclusive password:";
    displayMessage(text);

    temasekDataBase.put(userId, temasekian);

    // Log
    userLog(temasekian, "User getting verified...");

  }

  private void verification(Temasekian temasekian, String input) {

    String code = input;
    String text;

    switch (code) {

      case "thresident":
        startsRegistration(temasekian);
        temasekian.verified();
        break;

      default:
        text = "Sorry that is not the correct password! Please try again! Contact the blockheads if you are unsure of the password!";
        displayMessage(text);

        // Log
        userLog(temasekian, "User entered the incorrect verification code...\n" +
          "Incorrect Verification Code - " + code);
        break;

    }

  }


  // Temasekian Registration
  private void obtainTelegramUserInformation(Update update) {

    userFirstName = update.getMessage().getChat().getFirstName();
    userLastName = update.getMessage().getChat().getLastName();
    userUsername = update.getMessage().getChat().getUserName();
    userId = update.getMessage().getChat().getId();
    chatId = update.getMessage().getChatId();

  }

  private void registerInformation(String input, Temasekian temasekian) {

    //if (!input.equals("/start") && !temasekian.isRegistered()) {

    if (!temasekian.isTemasekianNameRegistered()) {
      // First time update name
      enterNameEntry(input, temasekian);

    } else if (!temasekian.isTemasekianMatricRegistered()) {
      // First time update matric
      enterMatricEntry(input, temasekian);

    } else if (!temasekian.isTemasekianBlkRegistered()) {

      //First time update block
      enterBlkEntry(input, temasekian);

    }
    //}
  }

  private void startsRegistration(Temasekian temasekian) {

    welcomeMessage();
    //temasekDataBase.put(userId, temasekian);

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

  private void enterNameEntry(String input, Temasekian temasekian) {

    String name = input;
    String text;

    if (isValidInput(name)) {
      temasekian.updateTemasekianName(name);
      temasekian.updateTemasekianChatId(chatId);
      temasekian.updateTemasekianTeleFirstName(userFirstName);
      temasekian.updateTemasekianTeleLastName(userLastName);
      temasekian.updateTemasekianTeleUsername(userUsername);
      temasekian.updateTemasekianUserId(userId);
      chatIdList.add(chatId);
      chatIdToTemasekianName.put(chatId, temasekian.getTemasekianName());

      text = "Ok " + name + "! So what's your matric number?";
      displayMessage(text);

      // Log
      userLog(temasekian, "Temasekian name updated: " + temasekian.getTemasekianName());

    } else {

      text = "That does not look like a legit name... Please key in another one....";
      displayMessage(text);

      userLog(temasekian, "Invalid name - " + name);
    }
  }

  private void enterMatricEntry(String input, Temasekian temasekian) {

    String matric = input.toUpperCase();

    String text;

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

  private void enterBlkEntry(String input, Temasekian temasekian) {

    String blk = input.toUpperCase();

    String text;

    if (isValidBlk(blk)) {

      temasekian.updateTemasekianBlk(blk);
      temasekian.registered();

      if (temasekian.getUserId().equals(Long.parseLong("24027079"))) {
        admins.add(temasekian.getUserId());
        dev.add(temasekian.getUserId());

        System.out.println("Eric got added to admin...");
      }

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


      //String text = "Oh no we have just finished some maintenance and require you to log in again...";

      displayMessage(UPDATE);

      String text = "To use the updated features, press /start";
      // text = "Please press /start to get started!";
      displayMessage(text);

      //text = "Sorry for the inconvenience caused...";
      //displayMessage(text);

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


  // User Mode
  private void userControl(String input, Temasekian temasekian) {

    if (temasekian.isReporting()) {
      // Report User
      reportUser(input, temasekian);
      pendingNotifications(temasekian);

    } else if (temasekian.isUpdating()) {
      // Updating Temasekian's Details
      updateTemasekianDetails(input, temasekian);

    } else if (temasekian.isGivingFeedback()) {

      giveFeedback(input, temasekian);
      pendingNotifications(temasekian);

    } else if (temasekian.isContactingAdmin()) {

      setContactMessage(temasekian, input);
      pendingNotifications(temasekian);

    } else if (temasekian.isReplying()) {

      setReply(temasekian, input);

    } else if (temasekian.isChangingTarget()) {

      pmNewTarget(temasekian, input);

    } else {

      // React to various commands
      mainBotFunctions(input, temasekian);
      // View reported/feedback list
      pendingNotifications(temasekian);

    }
  }

  private void pendingNotifications(Temasekian temasekian) {
    Long id = temasekian.getUserId();
    if (!temasekian.isAdminMode()) {
      for (Long i : admins) {
        if (id.equals(i)) {
          String text;
          if (!reportedList.isEmpty()) {
            text = "There are " + reportedList.size() + " reported case(s)";
            displayMessage(text);
          }
          if (!feedbackList.isEmpty()) {
            text = "There are " + feedbackList.size() + " feedback";
            displayMessage(text);
          }
          if (!contactedList.isEmpty()) {
            text = contactedList.size() + " people tried to contact you";
            displayMessage(text);
          }
        }
      }
    }
  }

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

      case "/thanks":
        sayThanks(temasekian);
        break;

      case "/report":
        report(temasekian);
        break;

      case "/feedback":
        feedback(temasekian);
        break;

      case "/contactus":
        contactAdmin(temasekian);
        break;

      case "/reply":
        reply(temasekian);
        break;

      case "/dm":
        setNewTarget(temasekian);
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

      case "/hidden":
        hiddenCmd(temasekian);
        break;

      case "/mastercontrol":
        adminControl(temasekian);
        break;

      case "/viewfeedback":
        viewFeedback(temasekian);
        break;

      case "/get":
        getInfo(temasekian);


      /*
      default:
        String msg = "\"" + input + "\" is not a valid command";
        displayMessage(msg);
        break;
       */
    }

  }

  private void getHelp(Temasekian temasekian) {

    String text =
      "Here are the common commands you can use: \n" +
        "/eating - You are consuming dinner today and do not wish to donate your matric number.\n" +
        "/noteating - You are feeling generous today and feel like helping out some hungry souls.\n" +
        "/whatsfordinner - You are unsure and want to see what's for dinner tonight before deciding\n" +
        "/numberpls - You are feeling extra hungry today and wish to have more food!\n\n" +
        "/report - You found someone using non-TH resident's matric and wish to report him\n" +
        "/feedback - You found some issue with the bot and wish to give us some feedback!\n" +
        "/contactus - You want some immediate reply to your question!\n\n" +
        "/updatename - Suddenly do not feel like being called " + temasekian.getTemasekianName() + " and wish to change your name.\n" +
        "/updatematric - For somewhat reason your matric number chances, you can update to your new matric number.\n" +
        "/updateblk - Change block but still one Temasek!";
    displayMessage(text);

    // Log
    userLog(temasekian, "User requesting for help...");

  }

  private void hiddenCmd(Temasekian temasekian) {

    String text = "Hidden Commands:\n" +
      "/mastercontrol - To access Admin Mode\n" +
      "/viewfeedback - To view the feedback given\n" +
      "/get - To obtain your user ID";
    displayMessage(text);

    // Log
    userLog(temasekian, "User viewing hidden admin commands...");

  }

  private void iseating(Temasekian temasekian) {

    String text;

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

  private void uploadDinnerPic(Update update, Temasekian temasekian) {

    List<PhotoSize> photosUploaded = update.getMessage().getPhoto();
    String text;

    String dinnerPic = photosUploaded.stream()
      .sorted(Comparator.comparing(PhotoSize::getFileId)
        .reversed())
      .findFirst()
      .orElse(null)
      .getFileId();

    photos.add(dinnerPic);

    if (!dinnerContributors.contains(temasekian.getTemasekianName())) {

      dinnerContributors.add(temasekian.getTemasekianName());

    }

    if (!temasekian.hasSharedDinnerPic()) {

      if (dinnerContributors.size() == 1) {

        dinnerCaption = temasekian.getTemasekianName() + " has kindly shared what's for today's dinner with everyone!";

      } else {

        String names = "Thank you " + dinnerContributors.get(0);

        for (int i = 1; i < dinnerContributors.size(); i++) {

          names = names + " & " + dinnerContributors.get(i);

        }

        dinnerCaption = names + " for sharing what's for dinner today!";

      }

      temasekian.shareDinnerPic();
      text = "Thank you " + temasekian.getTemasekianName() + " for sharing what is for dinner tonight";
      displayMessage(text);

      SendMessage message = new SendMessage();
      text = temasekian + " has just uploaded a dinner picture!";
      for (Long i : admins) {

        message.setChatId(i)
          .setText(text);
        tryExecute(message);

      }

    }


    // Log
    userLog(temasekian, temasekian.getTemasekianName() + " has shared what is for dinner tonight!" +
      "\nNumber of dinner pictures uploaded: " + photos.size());

  }

  private void queueMatric(Queue<Temasekian> matricDataBase, Temasekian temasekian) {

    String text;

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

  private void dequeueMatric(Temasekian temasekian, Queue<Temasekian> matricDataBase) {

    String text;

    if (!matricDataBase.isEmpty()) {

      if (temasekian.getMealCounter() > 0) {

        Temasekian donor = matricDataBase.poll();
        String donorMatric = donor.getTemasekianMatric();
        String donorName = donor.getTemasekianName();
        String donorBlk = donor.getTemasekianBlk();
        temasekian.decrMealCounter();

        text = "Here's a number for you: " + donorMatric;
        displayMessage(text);

        Long donorId = matricToUserId.get(donorMatric);
        temasekian.setDonorId(donorId);
        temasekian.setDonorName(donorName);

        //text = "If you see " + donorName + " from " + donorBlk + " blk around, remember to say thank you!";
        text = "Say /thanks to " + donorName + " from " + donorBlk + " blk!";
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

  private void sayThanks(Temasekian temasekian) {

    String text;

    if (!temasekian.getDonorId().equals(null)) {

      Long donorId = temasekian.getDonorId();
      String thankYouNote = "You have satisfied " + temasekian.getTemasekianName() + "'s crave for comm hall food! " +
        temasekian.getTemasekianName() + " thanked you!";

      SendMessage message = new SendMessage();

      message.setChatId(donorId)
        .setText(thankYouNote);
      tryExecute(message);


      text = "You have thanked " + temasekian.getDonorName() + "!";
      displayMessage(text);

      temasekian.setDonorName("");
      temasekian.setDonorId(null);

      // Log
      userLog(temasekian, "User thanks donor for extra matric!\nDonor received a thank you note!");

    } else {

      text = "You have not taken anyone's matric number today, so there is no one for you to thank!";
      displayMessage(text);

      // Log
      userLog(temasekian, "User attempted to thanks his donor but he did not get extra matric number today...");

    }
  }

  private void checkDinnerMenu(Temasekian temasekian) {

    String text;

    if (photos.isEmpty()) {

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
    String dinnerPic;

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

  private void report(Temasekian temasekian) {

    temasekian.reporting();
    String text = "Please use the report function responsibly! Those who abuse this function will be banned!";
    displayMessage(text);

    text = "Key in the matric number you wish to report:";
    displayMessage(text);

    // Log
    userLog(temasekian, "User attempt to report another user!");

  }

  private void reportUser(String input, Temasekian temasekian) {

    String text;

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
      if (isValidInput(reason)) {
        text = "Thank you for reporting! We will look into the matter and hope that this user will not be using this bot any more!";
        displayMessage(text);

        temasekian.updateReportedReason("\"" + reason + "\"");
        temasekian.submittedReason();
        temasekian.reported();

        reportedList.add("User " + userId + " - " + temasekian + " has reported " + temasekian.getReportedMatric() + " for " + temasekian.getReportedReason());

        String notification = "There is a new report! Total reported case(s): " + reportedList.size();
        SendMessage message = new SendMessage();
        for (Long i : admins) {
          message.setChatId(i)
            .setText(notification);
          tryExecute(message);
        }

        // Log
        userLog(temasekian, "Reported " + temasekian.getReportedMatric() + " for " + temasekian.getReportedReason() +
          "\nThere are " + reportedList.size() + " reported case(s)");

        temasekian.updateReportedReason("");
        temasekian.updateReportedMatric("");
      } else {
        text = "That does not look like a reason to ban someone... Please enter a legit reason...";
        displayMessage(text);

        // Log
        userLog(temasekian, "Invalid reason to ban another user - " + reason);
      }

    } else if (input.equals("/home")) {
      temasekian.reported();

      text = "Stopped reporting!";
      displayMessage(text);

      // Log
      userLog(temasekian, "User stopped reporting user...");

    }
  }

  private void feedback(Temasekian temasekian) {

    temasekian.givingFeedback();
    String text = "Thank you for taking your time to give us your valuable feedback! All your feedback will be reviewed and used to improve ourselves!";
    displayMessage(text);

    text = "Please enter your valuable feedback:";
    displayMessage(text);

    text = "Accidentally pressed feedback? Press /home to return to the main menu!";
    displayMessage(text);

    // Log
    userLog(temasekian, "User attempt to give a feedback!");
  }

  private void giveFeedback(String input, Temasekian temasekian) {

    String text;

    if (!input.equals("/home")) {
      if (isValidInput(input)) {
        String feedback = input;

        text = "Thank you for your feedback! Hope you will see an update in the near future!";
        displayMessage(text);

        temasekian.gaveFeedback();
        feedbackList.add("User " + userId + " - " + temasekian + " has given feedback - \"" + feedback + "\"");

        String notification = "There is a new feedback! Total feedback: " + feedbackList.size();
        SendMessage message = new SendMessage();
        for (Long i : admins) {
          message.setChatId(i)
            .setText(notification);
          tryExecute(message);
        }

        // Log
        userLog(temasekian, "User gave a feedback - \"" + feedback + "\"\n" +
          "There are " + feedbackList.size() + " feedback...");

      } else {
        text = "That does not look like a feedback to me? Please try again! Your feedback is valuable to us!";
        displayMessage(text);

        text = "Want to return to main menus? Press /home";
        displayMessage(text);

        // Log
        userLog(temasekian, "Invalid feedback - " + input);
      }

    } else if (input.equals("/home")) {
      temasekian.gaveFeedback();

      text = "So no feedback for now? If you think of anything, feel free to come back here! We will be listening!";
      displayMessage(text);

      // Log
      userLog(temasekian, "User stopped giving feedback...");

    }

  }

  private void viewFeedback(Temasekian temasekian) {

    String text;

    if (feedbackList.isEmpty()) {

      text = "There is no feedback from users...";
      displayMessage(text);

      // Log
      adminLog(temasekian, "Admin attempts to view feedback list but list is EMPTY...");

    } else {
      text = "There are " + feedbackList.size() + " feedback given. Here are the feedback:";
      displayMessage(text);

      for (String i : feedbackList) {
        text = i;
        displayMessage(text);
      }

      // Log
      adminLog(temasekian, "Admin is viewing feedback list...");

    }
  }

  private void reply(Temasekian temasekian) {
    String text = "Please enter your reply! To stop replying, /home";
    displayMessage(text);

    Long pmTargetId = temasekian.getPmTargetId();
    Temasekian pmTarget = temasekDataBase.get(pmTargetId);
    pmTarget.setPmTargetId(userId);

    temasekian.replying();

    // Log
    userLog(temasekian, "User replying admin...");
  }

  private void setReply(Temasekian temasekian, String input) {

    if (!input.equals("/home")) {

      String reply = input;

      SendMessage message = new SendMessage();
      Long pmTargetId = temasekian.getPmTargetId();
      Temasekian pmTarget = temasekDataBase.get(pmTargetId);

      if (admins.contains(pmTargetId)) {
        String text = "Sender: user " + userId + " - " + temasekian;
        message.setChatId(pmTargetId)
          .setText(text);
        tryExecute(message);

      }

      message.setChatId(pmTargetId)
        .setText(reply);
      tryExecute(message);

      String text = "Message sent!";
      displayMessage(text);

      if (admins.contains(pmTargetId)) {
        reply = "Current target user: user " + pmTarget.getPmTargetId() + " - " + temasekDataBase.get(pmTarget.getPmTargetId());
        message.setChatId(pmTargetId)
          .setText(reply);
        tryExecute(message);

        reply = "If user id matches, /reply , else /dm";
        message.setChatId(pmTargetId)
          .setText(reply);
        tryExecute(message);

      } else {
        reply = "To reply, /reply";
        message.setChatId(pmTargetId)
          .setText(reply);
        tryExecute(message);
      }
      temasekian.replied();
      //temasekian.setPmTargetId(null);

      // Log
      userLog(temasekian, "User replied to a pm from " + temasekDataBase.get(pmTargetId) + "\nReply - " + input);

    } else {

      temasekian.replied();
      //temasekian.setPmTargetId(null);

      String text = "You have stopped using the PM function!";
      displayMessage(text);

      // Log
      userLog(temasekian, "User stopped replying...");

    }

  }

  private void setNewTarget(Temasekian temasekian) {

    String text = "Please enter the user ID of the person: ";
    displayMessage(text);

    text = "No longer want to dm someone? /home to return to the main menu!";
    displayMessage(text);

    temasekian.changingTarget();

    // Log
    userLog(temasekian, "User setting target userId");

  }

  private void pmNewTarget(Temasekian temasekian, String input) {

    if (!input.equals("/home")) {

      if (isValidUserId(input)) {

        Long id = Long.parseLong(input);
        temasekian.setPmTargetId(id);

        Temasekian target = temasekDataBase.get(id);

        String text = "Target: user " + input + " - " + target;
        displayMessage(text);

        text = "To contact " + target + " /reply";
        displayMessage(text);

        temasekian.changedTarget();

        text = "To change the target user ID, /dm";
        displayMessage(text);

        // Log
        userLog(temasekian, "User changed his target user!");

      } else {

        String text = "That is an invalid user ID! Please try again!";
        displayMessage(text);

        text = "If you wish to stop changing the target user ID, /home";
        displayMessage(text);

        // Log
        userLog(temasekian, "User entered an invalid user ID when changing target user ID...");


      }

    } else {

      temasekian.changedTarget();

      String text = "Stop changing target user ID...";
      displayMessage(text);

      // Log
      userLog(temasekian, "User stopped changing target user ID");

    }

  }

  private void getInfo(Temasekian temasekian) {
    String text = "Your user ID: " + temasekian.getUserId();
    displayMessage(text);

    // Log
    userLog(temasekian, "User requested for User ID");
  }

  private void contactAdmin(Temasekian temasekian) {
    String text = "Please enter your queries! We will get back to you shortly!";
    displayMessage(text);

    temasekian.contactingAdmin();

    // Log
    userLog(temasekian, "User is contacting the admin...");

  }

  private void setContactMessage(Temasekian temasekian, String input) {

    String msg = "User " + userId + " - " + temasekian + ":\n" + input;

    contactedList.add(msg);

    String notification = "Someone contacted you! Total unclosed case(s): " + contactedList.size();
    SendMessage message = new SendMessage();
    for (Long i : admins) {
      message.setChatId(i)
        .setText(notification);
      tryExecute(message);
    }

    String text = "Thank you for contacting us! We will get back to you shortly!";
    displayMessage(text);

    temasekian.contactedAdmin();

    // Log
    userLog(temasekian, "User contacted the admins...\nMessage - " + input);

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

    String text;
    String name = input;

    if (isValidInput(name)) {

      temasekian.updateTemasekianName(name);
      temasekian.updated();

      text = "Ok now I shall call you " + name;
      displayMessage(text);

      // Log
      userLog(temasekian, "Changed Temasekian Name to: " + temasekian.getTemasekianName());

    } else {

      text = "Your name start with / meh? Enter your real name leh...";
      displayMessage(text);

      // Log
      userLog(temasekian, "Invalid name to update to - " + name);
    }
  }

  private void updateTemasekianMatric(String input, Temasekian temasekian) {

    String text;
    String matric = input.toUpperCase();

    if (isValidMatric(matric)) {
      if (!matricToUserId.containsKey(matric)) {

        String prevMatric = temasekian.getPreviousMatric();
        matricToUserId.remove(prevMatric, userId);
        matricToUserId.put(matric, userId);

        temasekian.updateTemasekianMatric(matric);
        temasekian.updated();

        text = "Ok got it!  " + matric;
        displayMessage(text);

        // Log
        userLog(temasekian, "User changing current matric number - " + prevMatric +
          "\nChanged Temasekian Matric to: " + temasekian.getTemasekianMatric());

      } else {
        text = "This matric number is already registered! Plesse key in a another one!";
        displayMessage(text);
        text = "If you wish to stop updating, press /home";
        displayMessage(text);


        // Log
        userLog(temasekian, "User keys in existing matric number...\nExisiting matric number: " + matric);
      }
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

    String text;
    String blk = input.toUpperCase();

    if (isValidInput(blk)) {

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

    } else {

      text = "This doesn't look like a block leh... Please type in a new block!";
      displayMessage(text);

      text = "If you wish to stop updating, press /home";
      displayMessage(text);

      // Log
      userLog(temasekian, "User used a command to update his block...");
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


  // Admin Mode
  private void mastercontrol(String input, Temasekian temasekian) {

    if (!temasekian.isAdmin()) {

      // Attempt Log In

      adminLogIn(input, temasekian);

    } else {

      // Admin Access Granted

      if (isAdminHome(input)) {

        adminHome(temasekian);

      } else {

        if (temasekian.isBanningUser()) {

          banUser(input, temasekian);

        } else if (temasekian.isUnbanningUser()) {

          unbanUser(input, temasekian);

        } else if (temasekian.isExchangingForUserId()) {

          requestUserId(temasekian, input);

        } else if (temasekian.isAddingAdmins()) {

          addingAdminToList(temasekian, input);

        } else if (temasekian.isRemovingAdmins()) {

          removingAdminFromList(temasekian, input);

        } else if (temasekian.isDoingQC()) {

          qcPhotos(temasekian, input);

        } else if (temasekian.isSettingPmId()) {

          if (!temasekian.isSettingPmMessage()) {

            setPmId(temasekian, input);

          } else {

            setPmMsg(temasekian, input);

          }

        } else if (temasekian.isBroadcasting()) {

          if (!temasekian.hasConfirmedBroadcast()) {

            confirmingMessage(temasekian, input);

          } else {

            broadcastMessage(temasekian, input);

          }

        } else {
          // All Admin Functions
          adminFunctions(input, temasekian);

        }

      }

    }

  }

  private void adminControl(Temasekian temasekian) {

    temasekian.activateAdminMode();

    String text = "Please enter admin password: ";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Attemping Admin Log In...");

  }

  private void adminLogIn(String input, Temasekian temasekian) {

    String text;

    if (temasekian.getRetryCounter() > 0) {

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

          temasekian.decrRetryCounter();
          text = "Sorry that is an invalid password! You have " + temasekian.getRetryCounter() + " attempt(s) left!";
          displayMessage(text);

          // Log
          adminLog(temasekian, "Invalid password - " + input + ", " + temasekian.getRetryCounter() + " attempt(s) left...");

          break;
      }

    } else {
      text = "Sorry you have used up all the available attempts! Please try again next time.";
      displayMessage(text);

      text = "You will be logged out of the master control!";
      displayMessage(text);

      temasekian.resetRetryCounter();
      temasekian.deactivateAdminMode();

      // Log
      adminLog(temasekian, "User used up all 3 attempts to log in...");

    }

  }

  private void adminHome(Temasekian temasekian) {

    temasekian.exchangedForUserId();
    temasekian.unbanned();
    temasekian.banned();
    temasekian.addedAdmins();
    temasekian.removedAdmins();
    temasekian.doneQC();
    temasekian.broadcasted();
    temasekian.confirmedBroadcast();
    temasekian.setPmId();
    temasekian.setPmMessage();

    String text = "You have returned to master control...";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin returned to Admin Home...");
  }

  private void adminFunctions(String input, Temasekian temasekian) {

    switch (input) {

      case "/adminhelp":
        adminHelp(temasekian);
        break;

      case "/admins":
        viewAdmins(temasekian);
        break;

      case "/addadmin":
        addAdmins(temasekian);
        break;

      case "/removeadmin":
        removeAdmins(temasekian);
        break;

      case "/banuser":
        ban(temasekian);
        break;

      case "/unbanuser":
        unban(temasekian);
        break;

      case "/viewreport":
        viewReportedUser(temasekian);
        break;

      case "/viewcontact":
        viewContact(temasekian);
        break;

      case "/clrrp":
        clearReportedUsers(temasekian);
        break;

      case "/clrfb":
        clearFeedback(temasekian);
        break;

      case "/clrcon":
        clearContact(temasekian);
        break;

      case "/broadcast":
        inputBroadcastMessage(temasekian);
        break;

      case "/pics":
        qualityCheck(temasekian);
        break;

      case "/getuserId":
        getUserId(temasekian);
        break;

      case "/pm":
        requestPmId(temasekian);
        break;

      case "/logout":
        logout(temasekian);
        break;

      case "/hidden":
        hiddenCommands(temasekian);
        break;

      case "/size":
        botUserSize(temasekian);
        break;

      case "/users":
        userList(temasekian);
        break;

      case "/maintain":
        maintenance();
        break;

      case "/off":
        turnOffBot(temasekian);
        break;

      case "/on":
        turnOnBot(temasekian);
        break;

      default:
        invalidCommand(temasekian, input);
        break;

    }

  }

  private void turnOffBot(Temasekian temasekian) {
    SEMESTER = false;

    String text = "Vacation Time! The bot is turned off!";
    displayMessage(text);

    // Log
    systemLog("Vacation!\n" + temasekian + " has turn off the bot");

  }

  private void turnOnBot(Temasekian temasekian) {
    SEMESTER = true;

    String text = "School has started! The bot is turned on!";
    displayMessage(text);

    // Log
    systemLog("School Time!\n" + temasekian + " has turn on the bot!");

  }

  private void adminHelp(Temasekian temasekian) {

    String text = "These are the available commands for ADMIN MODE: \n\n" +
      "/admins - To view all the admins\n" +
      "/addadmin - Add more admin\n" +
      "/removeadmin - Remove existing admin\n\n" +
      "/banuser - To ban users using their user ID\n" +
      "/unbanuser - To remove ban on user using their user ID\n\n" +
      "/viewreport - To view the list of reported user(s)\n" +
      "/viewcontact - To view the list of people who contacted you\n" +
      "/clrrp - To clear the list of reported users\n" +
      "/clrfb - To clear the list of feedback\n" +
      "/clrcon - To clear the list of people who contact the admin\n\n" +
      "/broadcast - To broadcast message to all ourTHBot users\n" +
      "/pics - To do quality check on the dinner pictures\n" +
      "/getuserId - To get the user ID from the matric number\n" +
      "/pm - To respond to the users who contacted the admin\n" +
      "/logout - To exit Admin mode and use the feature of ourTHBot\n";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin accessing help...");
  }

  private void hiddenCommands(Temasekian temasekian) {
    String text = "Hidden Commands:\n" +
      "/size - Size of bot users\n" +
      "/users - List of bot users\n" +
      "/maintain - Go into Maintenance Mode\n" +
      "/off - Turn off the bot during vacation\n" +
      "/on - Turn on the bot during school semester\n";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin viewing hidden commands...");

  }

  private void viewAdmins(Temasekian temasekian) {

    String text = "Admins:";

    for (Long i : admins) {

      Temasekian admin = temasekDataBase.get(i);
      text = text + "\nUser " + i + " - " + admin;

    }

    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin viewing the list of admins...");

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

  private void addingAdminToList(Temasekian temasekian, String input) {

    String text;

    if (isValidUserId(input)) {

      Long userId = Long.parseLong(input);

      if (temasekDataBase.containsKey(userId)) {

        if (!admins.contains(userId)) {

          admins.add(userId);
          Temasekian newAdmin = temasekDataBase.get(userId);
          temasekian.addedAdmins();

          text = "You have added " + newAdmin;
          displayMessage(text);

          // Log
          adminLog(temasekian, "Admin has added a new admin - " + newAdmin +
            "\nThere are " + admins.size() + " admin(s) now!");

        } else {

          text = "User - " + userId + " is already an admin!";
          displayMessage(text);

          text = "Enter a new user ID to add another admin, else press /adminhome";
          displayMessage(text);

          adminLog(temasekian, "Admin has entered the user ID of an existing admin...");
        }
      } else {

        text = "Sorry, the user ID you have entered is an invalid one! Please try again!";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin attempted to add new admin but keyed in an invalid user ID - " + userId + "...");
      }

    } else {

      text = "Sorry, the user ID you have entered is an invalid one!";
      displayMessage(text);

      // Log
      adminLog(temasekian, "EXCEPTION: Admin attempted to add new admin but keyed in an invalid user ID...");

    }

  }

  private void removeAdmins(Temasekian temasekian) {

    String text = "Please enter the user id of admin:";
    displayMessage(text);

    text = "If you wish to stop removing admins, press /adminhome";
    displayMessage(text);

    temasekian.removingAdmins();

    // Log
    adminLog(temasekian, "Admin removing more admins...");

  }

  private void removingAdminFromList(Temasekian temasekian, String input) {
    String text;

    if (isValidUserId(input)) {
      Long userId = Long.parseLong(input);

      if (temasekDataBase.containsKey(userId)) {
        admins.remove(userId);
        Temasekian removedAdmin = temasekDataBase.get(userId);
        temasekian.removedAdmins();

        text = "You have removed " + removedAdmin + " as admin!";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin has removed an admin - " + removedAdmin +
          "\nThere are " + admins.size() + " admin(s) now!");
      } else {

        text = "Sorry, the user ID you have entered is an invalid one!";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin attempted to remove an admin but keyed in an invalid user ID - " + userId + "...");

      }

    } else {
      text = "Sorry, the user ID you have entered is an invalid one!";
      displayMessage(text);

      // Log
      adminLog(temasekian, "EXCEPTION: Admin attempted to remove an admin but keyed in an invalid user ID ...");
    }
  }

  private void ban(Temasekian temasekian) {

    String text = "Please enter the user id to ban:";
    displayMessage(text);

    temasekian.banning();

    // Log
    adminLog(temasekian, "Admin is banning user...");

  }

  private void banUser(String input, Temasekian temasekian) {

    if (isValidUserId(input)) {
      Long banId = Long.parseLong(input);
      String text;

      if (temasekDataBase.containsKey(banId)) {
        Temasekian bannedTemaksian = temasekDataBase.get(banId);
        bannedList.add(banId);
        temasekDataBase.remove(banId);

        text = "User " + banId + " - " + bannedTemaksian + " has been banned...";
        displayMessage(text);
        temasekian.banned();

        // Log
        adminLog(temasekian, "Admin has banned User " + banId + " - " + bannedTemaksian);

      } else {
        text = "You have enter an invalid user id...";
        displayMessage(text);

        text = "If you wish to stop banning a user, /adminhome";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin attempts to ban a user but entered an invalid user ID...");
      }
    } else {

      String text;
      text = "You have enter an invalid user id...";
      displayMessage(text);

      text = "If you wish to stop banning a user, /adminhome";
      displayMessage(text);

      // Log
      adminLog(temasekian, "EXCEPTION: Admin attempts to ban a user but entered an invalid User ID...");

    }
  }

  private void unban(Temasekian temasekian) {


    String text = "Please enter the user id to un-ban:";
    displayMessage(text);

    temasekian.unbanning();

    // Log
    adminLog(temasekian, "Admin is removing ban on a user...");


  }

  private void unbanUser(String input, Temasekian temasekian) {

    String text;

    if (isValidUserId(input)) {

      Long bannedId = Long.parseLong(input);

      if (bannedList.contains(bannedId)) {
        bannedList.remove(bannedId);

        text = "User " + bannedId + " - " + " has been removed from the ban...";
        displayMessage(text);
        temasekian.unbanned();

        // Log
        adminLog(temasekian, "Admin has removed ban on User " + bannedId);

      } else {

        text = bannedId + " is an invalid user id ";
        displayMessage(text);

        text = "If you wish to stop un-banning a user, /adminhome";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin has attempted to remove ban on User " + bannedId + " but failed...");

      }
    } else {
      text = "You have enter an invalid user id...";
      displayMessage(text);

      text = "If you wish to stop un-banning a user, /adminhome";
      displayMessage(text);

      // Log
      adminLog(temasekian, "EXCEPTION: Admin attempts to remove the ban on a user but entered an invalid  user ID...");
    }

  }

  private void viewReportedUser(Temasekian temasekian) {

    String text;

    if (reportedList.isEmpty()) {

      text = "There is no reported user...";
      displayMessage(text);

      // Log
      adminLog(temasekian, "Admin attempts to view reported list but list is EMPTY...");

    } else {

      text = "There are " + reportedList.size() + " reported case(s). Here are the report(s): ";
      displayMessage(text);

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

  private void clearFeedback(Temasekian temasekian) {

    feedbackList.clear();

    String text = "Feedback list has been cleared...";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin cleared ALL feedback from the list!");

  }

  private void clearContact(Temasekian temasekian) {

    contactedList.clear();

    String text = "Contact list has been cleared...";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin cleared ALL contact case(s) from the list!");

  }

  private void inputBroadcastMessage(Temasekian temasekian) {

    String text = "Please type in message you want to broadcast:";
    displayMessage(text);

    text = "If you wish to stop broadcasting, /adminhome";
    displayMessage(text);

    temasekian.broadcasting();

    // Log
    adminLog(temasekian, "Admin is broadcasting a message...");

  }

  private void confirmingMessage(Temasekian temasekian, String input) {

    broadcastMessage = "BROADCAST MESSAGE\n\n " + input;
    String text = "Is the following broadcast message correct?\n\n" + broadcastMessage +
      "\n\n/yes - the message is correct" +
      "\n/no - the message has error in it";
    displayMessage(text);

    temasekian.confirmingBroadcast();

    // Log
    adminLog(temasekian, "Admin is confirming the broadcast message...\n" +
      "The broadcast message is:\n" + broadcastMessage);

  }

  private void broadcastMessage(Temasekian temasekian, String input) {

    String text;

    switch (input) {

      case "/yes":

        SendMessage message = new SendMessage();

        for (Long i : chatIdList) {

          message.setChatId(i)
            .setText(broadcastMessage);
          tryExecute(message);

        }

        text = "Your message have been broadcasted to " + chatIdList.size() + " user(s)!";
        displayMessage(text);

        temasekian.confirmedBroadcast();
        temasekian.broadcasted();

        // Log
        adminLog(temasekian, "Admin has broadcasted a message!\n" + broadcastMessage);

        break;

      case "/no":
        inputBroadcastMessage(temasekian);
        break;

      default:

        text = input + " is an invalid option... Please choose one of the following..." +
          "\n\n/yes - the message is correct" +
          "\n/no - the message has error in it";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin entered an invalid option during broadcast message confirmation...\n" +
          "Invalid option - " + input);

        break;
    }

  }

  private void qualityCheck(Temasekian temasekian) {

    String text = "There are the dinner photos: ";
    displayMessage(text);

    SendPhoto photo = new SendPhoto();

    for (String dinnerPic : photos) {

      photo.setChatId(chatId).setPhoto(dinnerPic);

      try {
        sendPhoto(photo);
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }

    }

    text = "To delete any photos, enter the index of the photo!";
    displayMessage(text);

    text = "For example," +
      "\nTo delete the first picture, type 1" +
      "\nTo delete the second and fourth picture, type 2,4" +
      "\nTo delete the first, second and third pic, type 1,2,3";
    displayMessage(text);

    text = "If there is no inappropriate photos uploaded, /adminhome";
    displayMessage(text);

    temasekian.doingQC();

    // Log
    adminLog(temasekian, "Admin is doing a quality check on the dinner pictures uploaded...");

  }

  private void qcPhotos(Temasekian temasekian, String input) {

    String text;

    if (!isValidFormat(input)) {

      text = "This is not a valid format! Please follow the following format:" +
        "\nTo delete the first picture, type 1" +
        "\nTo delete the second and fourth picture, type 2,4" +
        "\nTo delete the first, second and third pic, type 1,2,3";
      displayMessage(text);

      text = "If you wish to stop deleting selected few dinner photos, /adminhome";
      displayMessage(text);

      // Log
      adminLog(temasekian, "Admin entered an invalid format to while doing quality control on the dinner pictures uploaded..."
        + "Invalid input - " + input);

    } else {

      int count = 0;

      for (int i = 0; i < input.length(); i++) {

        Character c = input.charAt(i);

        if (!c.equals(',')) {

          Integer num = Integer.parseInt(String.valueOf(c));

          photos.remove(num - 1);
          count++;

          text = "Photo " + num + " has been delete!";
          displayMessage(text);

        }

      }

      temasekian.doneQC();

      // Log
      adminLog(temasekian, "Admin has deleted " + count + " photos after doing quality control on the dinner photos uploaded...");


    }

  }

  private void getUserId(Temasekian temasekian) {

    String text = "Key in the matric number:";
    displayMessage(text);

    temasekian.exchangingForUserId();

    // Log
    adminLog(temasekian, "Requesting for matric number to exchange for Telegram user id");

  }

  private void logout(Temasekian temasekian) {

    String text = "You have sucessfully logged out!";
    displayMessage(text);

    temasekian.resetRetryCounter();
    temasekian.adminPermissionRevoked();
    temasekian.deactivateAdminMode();

    // Log
    adminLog(temasekian, "Admin has logged out!");

  }

  private void requestUserId(Temasekian temasekian, String input) {
    String matric = input.toUpperCase();
    String text;

    System.out.println("matric in matricToUserId DB? " + matricToUserId.containsKey(matric));
    if (matricToUserId.containsKey(matric)) {

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

    } else {
      text = "This matric number have not been registered!";
      displayMessage(text);

      text = "If you wish to stop requesting for user ID, press /adminhome";
      displayMessage(text);

      // Log
      adminLog(temasekian, "Admin entered a matric number that is not registered...\n" +
        "Invalid matric - " + matric);

    }

  }

  private void botUserSize(Temasekian temasekian) {
    String text = "There are " + temasekDataBase.size() + " user(s) now!";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Admin views the size of the bot users...\nUsers: " + temasekDataBase.size());

  }

  private void userList(Temasekian temasekian) {
    String list = "Users who are using the bot:\n";

    for (Long i : chatIdList) {
      Temasekian user = temasekDataBase.get(i);
      list = list + "\n" + user;

    }

    list = list + "\n\nThere are a total of " + temasekDataBase.size() + " user(s)";
    displayMessage(list);

    // Log
    adminLog(temasekian, "Admin viewing the list of users...");

  }

  private void maintenance() {

    MAINTENANCE = true;

    String text = "Bot went offline!";
    displayMessage(text);

    // Log
    systemLog("ourTHBot is offline...");

  }

  private void viewContact(Temasekian temasekian) {

    String text;

    if (contactedList.isEmpty()) {

      text = "No one contacted the admin...";
      displayMessage(text);

      // Log
      adminLog(temasekian, "Admin attempts to view contacted list but list is EMPTY...");

    } else {

      text = "There are " + contactedList.size() + " unresolved case(s). Here are the report(s): ";
      displayMessage(text);

      for (String i : contactedList) {
        text = i;
        displayMessage(text);
      }

      // Log
      adminLog(temasekian, "Admin is viewing contact list...");
    }

  }

  private void requestPmId(Temasekian temasekian) {

    String text = "Please enter the user ID you want to PM:";
    displayMessage(text);

    temasekian.settingPmId();

  }

  private void setPmId(Temasekian temasekian, String input) {

    if (isValidUserId(input)) {
      Long pmId = Long.parseLong(input);
      String text;

      if (temasekDataBase.containsKey(pmId)) {

        temasekian.setPmTargetId(pmId);
        Temasekian pmTarget = temasekDataBase.get(pmId);
        pmTarget.setPmTargetId(userId);
        temasekian.settingPmMessage();

        text = "Please enter the message you want to send to user " + String.valueOf(pmId) + " - " +
          temasekDataBase.get(pmId);
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin set the PM Target to User " + pmId + " - " + pmTarget
          + "\nAdmin input message to be sent...");

      } else {
        text = "You have enter an invalid user id...";
        displayMessage(text);

        text = "If you wish to stop pm-ing, /adminhome";
        displayMessage(text);

        // Log
        adminLog(temasekian, "Admin attempts to ban a user but entered an invalid user ID...");
      }
    } else {

      String text;
      text = "You have enter an invalid user id...";
      displayMessage(text);

      text = "If you wish to stop pm-ing a user, /adminhome";
      displayMessage(text);

      // Log
      adminLog(temasekian, "EXCEPTION: Admin attempts to pm a user but entered an invalid User ID...");

    }

  }

  private void setPmMsg(Temasekian temasekian, String input) {

    String msg = input;

    SendMessage message = new SendMessage();

    message.setChatId(temasekian.getPmTargetId())
      .setText(msg);
    tryExecute(message);

    String text = "Message sent!";
    displayMessage(text);

    msg = "To reply, /reply";

    message.setChatId(temasekian.getPmTargetId())
      .setText(msg);
    tryExecute(message);

    temasekian.setPmMessage();
    temasekian.setPmId();
    //temasekian.setPmTargetId(null);

    // Log
    adminLog(temasekian, "Admin replied to User " + temasekian.getPmTargetId() + "\nMessage - " + input);

  }

  private void invalidCommand(Temasekian temasekian, String input) {

    String text = input + " is not a valid command in ADMIN MODE!";
    displayMessage(text);
    text = "Please /logout to use the features of the TH bot";
    displayMessage(text);

    // Log
    adminLog(temasekian, "Invalid command (" + input + ")");

  }


  // Daily Notification and Reset
  private boolean isTimeToReset() {

    Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
    Integer min = rightNow.get(Calendar.MINUTE);

/*
    System.out.println("Hour - " + hour);
    System.out.println("Min - " + min);
    System.out.println("RESET_HOUR - " + RESET_HOUR);
    System.out.println("RESET_MIN - " + RESET_MIN);
*/
    return hour.equals(RESET_HOUR) && min.equals(RESET_MIN);

  }

  private boolean isTimeToNotify() {

    Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
    Integer min = rightNow.get(Calendar.MINUTE);

/*
    System.out.println("Hour - " + hour);
    System.out.println("Min - " + min);
    System.out.println("NOTIFY_HOUR - " + NOTIFY_HOUR);
    System.out.println("NOTIFY_MIN - " + NOTIFY_MIN);
*/
    return hour.equals(NOTIFY_HOUR) && min.equals(NOTIFY_MIN);

  }

  private boolean isAnyOtherTiming() {
    Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
    Integer hour = rightNow.get(Calendar.HOUR_OF_DAY);
    Integer min = rightNow.get(Calendar.MINUTE);

    if (isTimeToReset()) {
      return false;
    }

    return !isTimeToNotify();
  }

  private void reset() {
    System.out.println("RESET!!!!!");
    matricDataBase.clear();
    photos.clear();
    dinnerContributors.clear();
    clearTemsekians();

  }

  private void clearTemsekians() {

    int count = 0;

    for (Long i : chatIdList) {
      Temasekian temasekian = temasekDataBase.get(i);
      temasekian.resetDonation();
      temasekian.resetMealCounter();
      temasekian.resetRetryCounter();
      count++;
    }

    // Log
    systemLog("There are " + temasekDataBase.size() + " people in the database...\n" +
      "Reset " + count + " temasekian's matric donation boolean, meal counters and admin retry counters");
  }

  private void alertUser() {

    for (Long i : chatIdList) {
      String text = "Hello " + chatIdToTemasekianName.get(i) + ", will you be eating tonight?\n" +
        "/eating - Yes! I love comm hall dinner!\n" +
        "/noteating - Neh... Not today...\n" +
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


  // Verifications
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

      return letter.equals(givenLetter);

    }

  }

  private boolean isValidBlk(String blk) {

    String[] blkId = {"A", "B", "C", "D", "E"};

    for (String i : blkId) {

      if (i.equals(blk)) {

        return true;

      }

    }

    return false;

  }

  private boolean isValidInput(String input) {

    Character first = input.charAt(0);

    return !first.equals('/');

  }

  private boolean isValidUserId(String input) {

    try {

      Long.parseLong(input);
      return temasekDataBase.containsKey(Long.parseLong(input));

    } catch (NumberFormatException e) {

      systemLog("EXCEPTION: Invalid User Id - " + input);
      return false;

    }
  }

  private boolean isAdminHome(String input) {

    return input.equals("/adminhome");

  }

  private boolean isValidFormat(String input) {

    for (int i = 0; i < input.length(); i++) {

      Character c = input.charAt(i);
      System.out.println(c);
      System.out.println(c.equals(','));
      System.out.println(isValidInt(c));

      if (!c.equals(',') && !isValidInt(c)) {

        return false;

      }

    }

    return true;

  }

  private boolean isValidInt(Character c) {

    boolean isValidInteger = false;

    String s = String.valueOf(c);

    try {
      Integer.parseInt(s);
      // s is a valid integer
      isValidInteger = true;
    } catch (NumberFormatException ex) {
      // s is not an integer
    }

    return isValidInteger;
  }


  //OurTHBot
  public String getBotUsername() {

    return "ourTHBot";

  }

  public String getBotToken() {

    return "593031383:AAHhl5lTr8_Rk36LwsMLYte6DoEByRxHR_c";

  }

/*
  //updateTHBot
  public String getBotUsername() {

      return "updateTHBot";

  }

  public String getBotToken() {

      return "574618937:AAEaafM-63dZNRQmVfZeBbdTmK5G9DorNfo";

  }
*/


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


  // Logging
  private void userLog(Temasekian temasekian, String message) {
    System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::");
    System.out.println("USER MODE");
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
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
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
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
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    System.out.println(dateFormat.format(date));
    System.out.println(message);
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

  }

}

//private static Set<Long> userIdList = new TreeSet<Long>();

/*
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
    */


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
