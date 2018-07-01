public class Temasekian {

    private static int MEAL_COUNTER = 3;
    private static int RETRY_COUNTER = 3;

    // Temasekian Information
    private String firstName = "";
    private String matricNum = "";
    private String blk = "";

    // Telegram Information
    private long chatId;
    private String teleFirstName;
    private String teleLastName;
    private String teleUsername;
    private long userId;

    private int retryCounter;


    private boolean registered = false;
    private boolean matricDonated = false;
    private boolean updating = false;
    private boolean sharedDinnerPic = false;

    private boolean reporting = false;
    private boolean submittingReason = false;
    private String reportedMatric = "";
    private String reportedReason = "";

    private String previousMatric = "";
    private String previousBlk = "";

    private boolean exchangingForUserId = false;
    private boolean banning = false;
    private boolean unbanning = false;

    private boolean adminMode = false;
    private boolean admin = false;

    private boolean addingAdmins = false;

    private int mealCounter;
    private boolean isEating;
    private boolean isNotEating;


    public Temasekian() {
        this.firstName = "";
        this.matricNum = "";
        this.blk = "";

        this.retryCounter = RETRY_COUNTER;
        this.mealCounter = MEAL_COUNTER;

        //this.registered = false;
        //this.matricDonated = false;
        //this.updating = false;
    }


    public boolean isRegistered() {

        return this.registered;

    }

    public void registered() {

        this.registered = true;

    }

    public boolean isMatricDonated() {

        return this.matricDonated;

    }

    public void donateMatric() {

        this.matricDonated = true;
    }

    public boolean shareDinnerPic() {

        return this.sharedDinnerPic = true;

    }

    public boolean hasSharedDinnerPic() {

        return this.sharedDinnerPic;

    }


    public void updateTemasekianName(String name) {

        this.firstName = name;

    }

    public void updateTemasekianMatric(String matric) {

        this.matricNum = matric;

    }

    public void updateTemasekianBlk(String blk) {

        this.blk = blk;

    }


    public void updateTemasekianTeleFirstName(String firstName) {

        this.teleFirstName = firstName;

    }

    public void updateTemasekianTeleLastName(String lastName) {

        this.teleLastName = lastName;

    }

    public void updateTemasekianTeleUsername(String username) {

        this.teleUsername = username;

    }

    public void updateTemasekianChatId(long chatId) {

        this.chatId = chatId;

    }

    public void updateTemasekianUserId(long userId) {

        this.userId = userId;

    }


    public boolean isTemasekianNameRegistered() {

        if (this.firstName.equals("")) {
            return false;
        }

        return true;

    }

    public boolean isTemasekianMatricRegistered() {

        if (this.matricNum.equals("")) {
            return false;
        }

        return true;

    }

    public boolean isTemasekianBlkRegistered() {

        if (this.blk.equals("")) {
            return false;
        }

        return true;

    }

    public boolean isUpdating() {

        return this.updating;

    }


    public String getTemasekianName() {

        return this.firstName;

    }

    public String getTemasekianMatric() {

        return this.matricNum;

    }

    public String getTemasekianBlk() {

        return this.blk;

    }


    public void updating() {

        this.updating = true;

    }

    public void updated() {

        this.updating = false;

    }

    public void updatePreviousMatric(String matric) {

        this.previousMatric = matric;

    }

    public String getPreviousMatric() {

        return this.previousMatric;

    }

    public void updatePreviousBlk(String blk) {

        this.previousBlk = blk;

    }

    public String getPreviousBlk() {

        return this.previousBlk;

    }


    public boolean isReporting() {

        return this.reporting;

    }

    public void reporting() {

        this.reporting = true;

    }

    public void reported() {

        this.reporting = false;

    }


    public boolean isSubmittingReason() {

        return this.submittingReason;

    }

    public void submittingReason() {

        submittingReason = true;

    }

    public void submittedReason() {

        submittingReason = false;

    }

    public void updateReportedMatric(String matric) {

        this.reportedMatric = matric;

    }

    public String getReportedMatric() {

        return this.reportedMatric;

    }

    public void updateReportedReason(String reason) {

        this.reportedReason = reason;

    }

    public String getReportedReason() {

        return this.reportedReason;

    }


    public boolean isExchangingForUserId() {

        return this.exchangingForUserId;

    }

    public void exchangingForUserId() {

        this.exchangingForUserId = true;

    }

    public void exchangedForUserId() {

        this.exchangingForUserId = false;

    }


    public boolean isBanningUser() {

        return this.banning;

    }

    public void banning() {

        this.banning = true;

    }

    public void banned() {

        this.banning = false;

    }


    public boolean isUnbanningUser() {

        return this.unbanning;

    }

    public void unbanning() {

        this.unbanning = true;

    }

    public void unbanned() {

        this.unbanning = false;

    }


    public boolean isAdminMode() {

        return this.adminMode;

    }

    public void activateAdminMode() {

        this.adminMode = true;

    }

    public void deactivateAdminMode() {

        this.adminMode = false;

    }


    public boolean isAdmin() {

        return this.admin;

    }

    public void adminPermissionGranted() {

        this.admin = true;

    }

    public void adminPermissionRevoked() {

        this.admin = false;

    }


    public int getRetryCounter() {

        return this.retryCounter;

    }

    public void decrRetryCounter() {

        this.retryCounter--;

    }

    public int getMealCounter() {

        return this.mealCounter;

    }

    public void decrMealCounter() {

        this.mealCounter--;

    }

    public void resetTemasekianCounter() {

        this.retryCounter = RETRY_COUNTER;
        this.mealCounter = MEAL_COUNTER;

    }

    public long getUserId() {

        return this.userId;

    }

    public Long getChatId() {

        return this.getChatId();

    }


    public String toString() {
        return this.firstName + " (" + this.matricNum + ") from Blk " + this.blk;
    }






    // Temasekian is eating
    public void isEating() {
        this.isEating = true;
        this.isNotEating = false;
    }

    // Temasekian is not eating
    public void isNotEating() {
        this.isEating = false;
        this.isNotEating = true;
    }


    public boolean isAddingAdmins() {

        return this.addingAdmins;

    }

    public void addingAdmins() {

        this.addingAdmins = true;

    }

    public void addedAdmins() {

        this.addingAdmins = false;

    }
}

/*
  <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>antlr</groupId>
            <artifactId>antlr</artifactId>
            <version>2.7.2</version>
        </dependency>
 */

/*



<build>
        <pluginManagement>
            <plugins>

                <plugin>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <executions>
                        <execution>
                            <phase>package</phase>
                            <goals>
                                <goal>single</goal>
                            </goals>
                        </execution>
                    </executions>
                    <configuration>
                        <archive>
                            <manifest>
                                <addClasspath>true</addClasspath>
                                <mainClass>MainClass</mainClass>
                            </manifest>
                        </archive>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                    </configuration>
                </plugin>
                <plugin>
                    <artifactId>maven-assembly-plugin</artifactId>
                    <version>3.1.0</version>
                    <configuration>
                        <descriptorRefs>
                            <descriptorRef>jar-with-dependencies</descriptorRef>
                        </descriptorRefs>
                    </configuration>
                </plugin>

            </plugins>
        </pluginManagement>
    </build>
 */