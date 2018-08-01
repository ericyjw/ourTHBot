public class Temasekian {

    private static int MEAL_COUNTER = 3;
    private static int RETRY_COUNTER = 3;

    // Temasekian Information
    private String firstName;
    private String matricNum;
    private String blk;

    // Telegram Information
    private long chatId;
    private String teleFirstName;
    private String teleLastName;
    private String teleUsername;
    private Long userId;

    private int retryCounter;

    private boolean verified = false;
    private boolean registered = false;
    private boolean matricDonated = false;
    private boolean updating = false;
    private boolean sharedDinnerPic = false;

    private boolean reporting = false;
    private boolean submittingReason = false;
    private String reportedMatric = "";
    private String reportedReason = "";

    private boolean contactAdmin = false;

    private boolean givingFeedback = false;

    private String previousMatric = "";
    private String previousBlk = "";

    private boolean exchangingForUserId = false;
    private boolean banning = false;
    private boolean unbanning = false;

    private boolean adminMode = false;
    private boolean admin = false;

    private boolean addingAdmins = false;
    private boolean removingAdmins = false;

    private boolean doingQC = false;
    private boolean broadcast = false;
    private boolean confirmBroadcast = false;

    private boolean setPmId = false;
    private boolean setPmMessage = false;
    private Long pmTargetId;

    private boolean replying = false;
    private boolean changeTarget = false;

    private int mealCounter;
    private boolean isEating;
    private boolean isNotEating;

    private Long donorId = null;
    private String donorName = "";


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

    public boolean isVerified() {

        return this.verified;

    }

    public void verified() {

        this.verified = true;

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

    public void resetDonation() {

        this.matricDonated = false;
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

        return !this.firstName.equals("");

    }

    public boolean isTemasekianMatricRegistered() {

        return !this.matricNum.equals("");

    }

    public boolean isTemasekianBlkRegistered() {

        return !this.blk.equals("");

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

    public void setDonorId(Long id) {

        this.donorId = id;

    }

    public Long getDonorId() {

        return this.donorId;

    }

    public void setDonorName(String name) {

        this.donorName = name;

    }

    public String getDonorName() {

        return this.donorName;

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

    public Long getUserId() {

        return this.userId;

    }

    public Long getChatId() {

        return this.chatId;

    }


    public boolean isRemovingAdmins() {

        return this.removingAdmins;

    }

    public void removingAdmins() {

        removingAdmins = true;

    }

    public void removedAdmins() {

        removingAdmins = false;

    }


    public boolean isGivingFeedback() {

        return this.givingFeedback;

    }

    public void givingFeedback() {

        this.givingFeedback = true;

    }

    public void gaveFeedback() {

        this.givingFeedback = false;

    }

    public boolean isDoingQC() {

        return this.doingQC;

    }

    public void doingQC() {

        this.doingQC = true;

    }

    public void doneQC() {

        this.doingQC = false;

    }

    public boolean isBroadcasting() {

        return this.broadcast;

    }

    public void broadcasting() {

        this.broadcast = true;

    }

    public void broadcasted() {

        this.broadcast = false;

    }

    public boolean hasConfirmedBroadcast() {

        return this.confirmBroadcast;

    }

    public void confirmingBroadcast() {

        this.confirmBroadcast = true;

    }

    public void confirmedBroadcast() {

        this.confirmBroadcast = false;

    }

    public boolean isContactingAdmin() {

        return this.contactAdmin;

    }

    public void contactingAdmin() {

        this.contactAdmin = true;

    }

    public void contactedAdmin() {

        this.contactAdmin = false;

    }

    public boolean isSettingPmId() {

        return this.setPmId;

    }

    public void settingPmId() {

        this.setPmId = true;

    }

    public void setPmId() {

        this.setPmId = false;

    }

    public boolean isSettingPmMessage() {

        return this.setPmMessage;

    }

    public void settingPmMessage() {

        this.setPmMessage = true;

    }

    public void setPmMessage() {

        this.setPmMessage = false;

    }

    public void setPmTargetId(Long id) {

        this.pmTargetId = id;

    }

    public Long getPmTargetId() {

        return this.pmTargetId;

    }

    public boolean isReplying() {

        return this.replying;

    }

    public void replying() {

        this.replying = true;

    }

    public void replied() {

        this.replying = false;

    }

    public boolean isChangingTarget() {

        return this.changeTarget;

    }

    public void changingTarget() {

        this.changeTarget = true;

    }

    public void changedTarget() {

        this.changeTarget = false;

    }

    @Override
    public String toString() {
        return this.firstName + " (" + this.matricNum + ") from Blk " + this.blk;
    }

/*    @Override
    public int compareTo(Temasekian other) {

        if (!other.blk.equals(this.blk)) {
            return this.firstName.compareToIgnoreCase(other.firstName);
        } else {
            return this.blk.compareToIgnoreCase(other.blk)
        }

    }
*/



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

    public void resetMealCounter() {

        this.mealCounter = MEAL_COUNTER;

    }

    public void resetRetryCounter() {

        this.retryCounter = RETRY_COUNTER;
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