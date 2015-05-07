package group8.com.application.Application;

import android.os.CountDownTimer;
import android.util.Log;

import group8.com.application.Model.ConstantData;

public class AlertSystem {

    private static boolean brakeAlert = false, brakeIsCounting = false,
                           driverDistractionAlert = false, driverDistractionIsCounting = false,
                           speedAlert = true,
                           fuelAlert = true,
                           shouldAlert = true;
    private static int brakeCount = 0, distractionCount = 0;
    private static CountDownTimer brakeCountDown = null;

    private static CountDownTimer coolDown = new CountDownTimer(10000, 10000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            shouldAlert = true;
        }
    };

    protected static boolean evaluateSpeed() {

        if(shouldAlert && speedAlert && Controller.getCurrentSpeed() >= ConstantData.extremeSpeed) {
            shouldAlert = false;
            speedAlert = false;
            coolDown.start();
            return true;
        } else if(Controller.getCurrentSpeed() < ConstantData.extremeSpeed) {
            speedAlert = true;
        }

        return false;

    }

    protected static boolean evaluateFuelConsumption() {

        if(shouldAlert && fuelAlert && Controller.getCurrentFuelConsumption() >= ConstantData.extremeFuelConsumption) {
            shouldAlert = false;
            fuelAlert = false;
            coolDown.start();
            return true;
        } else if(Controller.getCurrentFuelConsumption() < ConstantData.extremeFuelConsumption) {
            fuelAlert = true;
        }

        return false;

    }

    protected static boolean evaluateBrake() {

        if(!brakeIsCounting && Controller.getCurrentBrake() == 1 && brakeCount == 0) {

            brakeIsCounting = true;

            brakeCountDown = new CountDownTimer(5000, 100) {

                @Override
                public void onTick(long millisUntilFinished) {

                    Log.d("Brake countDown", "Counting");

                }

                @Override
                public void onFinish() {

                    brakeAlert = true;
                    brakeIsCounting = false;
                    Log.d("Brake countDown", "Finish");

                }

            }.start();

            brakeCount++;

        } else if(Controller.getCurrentBrake() == 0) {

            if(brakeCountDown != null) {

                brakeCountDown.cancel();
                Log.d("Brake countDown", "Cancelled");

            }

            brakeCount = 0;
            brakeIsCounting = false;
            brakeCountDown = null;

        }

        if(shouldAlert && brakeAlert) {

            shouldAlert = false;
            brakeAlert = false;
            coolDown.start();

            return true;

        }

        return false;

    }

    protected static boolean evaluateDriverDistractionLevel() {

        if(!driverDistractionIsCounting && Controller.getCurrentDistractionLevel() == 4 && distractionCount == 0) {

            driverDistractionIsCounting = true;

            new CountDownTimer(2000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {

                    if(Session.getLastDriverDistractionLevel() == 0) {

                        driverDistractionIsCounting = false;
                        this.cancel();

                    }

                }

                @Override
                public void onFinish() {

                    driverDistractionAlert = true;
                    driverDistractionIsCounting = false;

                }
            }.start();

            distractionCount++;

        } else if(Controller.getCurrentDistractionLevel() < 4)
            distractionCount = 0;

        if(shouldAlert && driverDistractionAlert) {

            shouldAlert = false;
            coolDown.start();
            driverDistractionAlert = false;
            return true;

        }

        return false;

    }

}