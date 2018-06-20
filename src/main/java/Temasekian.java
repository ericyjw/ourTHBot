public class Temasekian {

    private String firstName;
    private String matricNum;
    private char blk;
    private int mealCounter;
    private boolean isEating;
    private boolean isNotEating;

    public Temasekian (String name, String matric, char blk) {
        this.firstName = name;
        this.matricNum = matric;
        this.blk = blk;
        this.mealCounter = 0;
        this.isEating = false;
        this.isNotEating = false;
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
}
