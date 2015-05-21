package group8.com.application.Application;

import android.os.CountDownTimer;

import java.util.ArrayList;

import group8.com.application.Model.ConstantData;

/**
 * Class representing the logic for the grading of drivers performance.
 * To start the grading system, the method startGrading() has to be called.
 */
public abstract class GradingSystem {

    /* Private variables */
    private static int lastDistractionLevel;                // Stores the current distraction level(updates when the distraction level is changed)
    private static boolean running = false;                  // Flag used in the threads loop
    private static CountDownTimer brakeTimer;               // Timer used for grading the braking
    private static CountDownTimer speedTimer;
    private static CountDownTimer fuelTimer;
    private static ArrayList<Double> tempSpeedList;
    private static ArrayList<Double> tempFuelList;
    private static ArrayList<Integer> tempBrakeList;

    /**
     * Start the grading system.
     * */
    protected static void startGradingSystem() {

        if(MeasurementFactory.isMeasuring()) {

            running = true;

            // Create a new countdown. When the countdown has finished, the braking score increases by 1.
            tempBrakeList =  new ArrayList<>();
            brakeTimer = new CountDownTimer(5000, 5000) {

                public void onTick(long millisUntilFinished) {}
                public void onFinish() {
                    int currentScore = Session.getBrakeScore();
                    if (tempBrakeList.size()!=0)
                        Session.setBrake(brakingAverage());
                    int newScore = currentScore + evaluateBrake();
                    if (newScore <= 100 && newScore >= 0) {
                        Session.setBrakeScore(newScore);
                    }
                    tempBrakeList =  new ArrayList<>();
                    brakeTimer.start();
                }
            }.start();

            // Create a new countdown. When the countdown has finished, the speed score increases by 1 if the speed changes are reasonable.
            tempSpeedList =  new ArrayList<>();
            speedTimer = new CountDownTimer(10000, 5000) {
                int points = 0;
                public void onTick(long millisUntilFinished) {
                    points = points + evaluateSpeedAverage();
                    if (tempSpeedList.size()!=0)
                        Session.setSpeed(tempSpeedList.get(tempSpeedList.size()-1)); //Sets the speed measurement every 5 seconds.
                    tempSpeedList =  new ArrayList<>();
                }

                public void onFinish() {
                    int currentScore = Session.getSpeedScore();
                    int newScore = currentScore + points;
                    if (newScore>=0&&newScore<=100) {
                        Session.setSpeedScore(newScore); //Sets the speed points every 10 seconds.
                    }
                    points = 0;
                    speedTimer.start();
                }
            }.start();

            // Create a new countdown. When the countdown has finished, the fuel score increases by 1
            // if the fuel changes are reasonable.
            tempFuelList =  new ArrayList<>();
            fuelTimer = new CountDownTimer(5000, 5000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    int currentScore = Session.getFuelConsumptionScore();
                    if (tempFuelList.size()!=0)
                        Session.setFuelConsumption(tempFuelList.get(tempFuelList.size()-1));
                    int newScore = currentScore + evaluateFuel();
                    if (newScore<=100&&newScore>=0)
                    Session.setFuelConsumptionScore(newScore);
                    tempFuelList =  new ArrayList<>();
                    fuelTimer.start();
                }
            }.start();

        }

    }

    /**
     * Pause the grading system.
     * */
    protected static void stopGradingSystem() {

        running = false;
        brakeTimer.cancel();
        speedTimer.cancel();
        fuelTimer.cancel();
    }

    /**
     * Check if the GradingSystem is grading.
     *
     * @return true if the GradingSystem is grading, and false if it is not.
     * */
    protected static boolean isGrading() {

        return running;

    }

    /**
     * Sets the score to the current speed.
     * */
    protected static void updateSpeedScore(double speed) {

        if(running) {
            tempSpeedList.add(speed);
        }

    }

    /**
     * - 1 point when the fuel consumption is below 60.
     * + 1 point if the fuel consumption is above 60.
     * This updates every time the signal gets activated ie. when the fuel consumption changes.
     * */
    protected static void updateFuelConsumptionScore(double fuelConsumption) {

        if(running) {
            tempFuelList.add(fuelConsumption);
        }

    }

    /**
     * - 1 point when the brake is activated.
     * + 1 point every 10 seconds the brake is not activated.
     * */
    protected static void updateBrakeScore(int brake, boolean timerFinished) {

        if(running) {
            tempBrakeList.add(brake);
        }

    }

    /**
     * + 1 point when the distraction level changes to standstill(0).
     * - 1 point when the distraction level changes to high(3).
     * - 2 points when the distraction level changes to very high(4).
     * */
    protected static void updateDriverDistractionLevelScore(int distractionLevel) {

        if(running) {

            // Store the current score and the new score
            int currentScore = Session.getDriverDistractionLevelScore();
            int newScore = currentScore;

            // Evaluate the measurements
            if (distractionLevel == 3 && lastDistractionLevel < 3)
                newScore = currentScore - 1;

            if (distractionLevel == 4 && lastDistractionLevel < 4) {
                newScore = currentScore - 2;
            }

            if (distractionLevel == 0 && lastDistractionLevel != 0)
                newScore = currentScore + 1;

            lastDistractionLevel = distractionLevel;

            // Update the lists of measurements and scores
            if(newScore != currentScore) {
                if (newScore>=0&&newScore<=100) {
                    Session.setDriverDistractionLevel(distractionLevel);
                    Session.setDriverDistractionLevelScore(newScore);
                }
            }

        }

    }

    /**
     * Function to evaluate if the speed has too many numbers out of range. A number is considered out of range if it has 5km/h
     * out either way from the average of the list.
     * The function will return +2 if less than 2 out of 50 possible numbers are out of range, +1 if 2-4 are out of range and -1 otherwise.
     * @return +2/+1/-1/-2 depending on the amount of numbers out of range (<5/>5 respectively) or if the user exceeded the speed limit.
     */
    private static int evaluateSpeedAverage ()
    {
        //finds the average of the list.
        double total = 0;
        for (int i=0; i < tempSpeedList.size(); i++)
        {
            total = total + tempSpeedList.get(i);
            if (tempSpeedList.get(i) >120){
                return -2;
            }
        }
        double average = total /tempSpeedList.size();

        int outOfRange = 0;

        for (int i=0; i < tempSpeedList.size(); i++)
        {
            if (tempSpeedList.get(i) < average-ConstantData.outOfRangeSpeed || tempSpeedList.get(i) > average+ConstantData.outOfRangeSpeed)
                outOfRange= outOfRange+1;
        }

        if (outOfRange<=ConstantData.outOfRangeSpeedLowerMargin)
            return 2;
        else if (outOfRange <=ConstantData.outOfRangeSpeedMiddleMargin)
            return 1;
        else
            return -1;
    }

    /**
     * Function to evaluate the fuel Consumption.
     * @return +1/-1 depending if the values during 5 seconds go over the "good" fuel consumption.
     */
    private static int evaluateFuel() {
        for (int i =0; i<tempFuelList.size();i++) {
            if (tempFuelList.get(i)>ConstantData.goodFuelConsumption)
                return -1;
        }
        return 1;
    }

    /**
     * Function to find the average amongst the breaking measurements in the tempBrakeList
     * @return
     */
    private static int brakingAverage () {
        int total = 0;
        for (int i=0; i<tempBrakeList.size();i++) {
            total = total+tempBrakeList.get(i);
        }
        if (total >= tempBrakeList.size()/2)
            return 1;
        else
            return 0;
    }

    /**
     * Function to evaluate the braking. If the total measurement are greater or equal to 25 it returns -1
     * if they are lower than 25 it returns a 1.
     * @return
     */
    private static int evaluateBrake () {
        int total = 0;
        for (int i=0; i<tempBrakeList.size();i++) {
            total = total+tempBrakeList.get(i);
        }
        if (total >= 25)
            return -1;
        else
            return 1;
    }
}