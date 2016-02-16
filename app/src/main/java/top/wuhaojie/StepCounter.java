package top.wuhaojie;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 计步类
 * Created by wuhaojie on 2016/2/16.
 */
public class StepCounter {

    /**
     * 计步器对象
     */
    private StepCounter mStepCounter = null;

    /**
     * 上下文对象
     */
    private Context mContext = null;


    /**
     * 步数
     */
    private int mStepNum = 0;

    /**
     * 总步数
     */
    private int mSumStepNum = 0;

    /**
     * 方向
     */
    private float mOrientation = 0.0F;

    /**
     * 气压
     */
    private float mPressure = 0.0F;

    /**
     * 高度
     */
    private float mHeight = 0.0F;
    private SensorEventListener mSensorListener;
    private SensorManager mSensorManager;
    private boolean isRegisted;

    /**
     * 单例模式 构造函数私有化
     */
    private StepCounter(Context context) {
        this.mContext = context;
    }

    /**
     * 单例模式 获取对象
     *
     * @return StepCounter对象
     */
    public synchronized StepCounter getInstance(Context context) {
        if (context == null) {
            throw new NullPointerException("Context参数不能为null");
        }
        if (mStepCounter == null) {
            mStepCounter = new StepCounter(context);
        }
        return mStepCounter;
    }

    /**
     * 重置计步数据
     *
     * @return
     */
    public boolean reset() {
        mHeight = 0;
        mOrientation = 0;
        mPressure = 0;
        mStepNum = 0;
//        mSumStepNum = 0; 总和不应该清零
        return false;
    }

    /**
     * 注册计步服务
     *
     * @return
     */
    public boolean register() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new MySensorListener();
        Sensor stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor pressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        Sensor accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        boolean a = mSensorManager.registerListener(mSensorListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean b = mSensorManager.registerListener(mSensorListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean c = mSensorManager.registerListener(mSensorListener, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean d = mSensorManager.registerListener(mSensorListener, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
        isRegisted = a && b && c && d;
        return isRegisted;
    }

    /**
     * 开始计步
     *
     * @return
     */
    public boolean start() {
        if (!isRegisted) throw new RuntimeException("开始之前请调用register()");
        return reset();
    }

    /**
     * 暂停计步
     *
     * @return
     */
    public boolean pause() {
        return false;
    }

    /**
     * 注销计步服务
     *
     * @return
     */
    public boolean unRegister() {
        if (mSensorManager != null && mSensorListener != null) {
            mSensorManager.unregisterListener(mSensorListener);
            mSensorManager = null;
            mSensorListener = null;
            return true;
        }
        return false;
    }

    private class MySensorListener implements SensorEventListener {

        private float[] accelerometerValues;

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelerometerValues = event.values.clone();
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    mSumStepNum++;
                    mStepNum++;
                    break;
                case Sensor.TYPE_PRESSURE:
                    float[] magnValues = event.values.clone();
                    float[] R = new float[9];
                    float[] values = new float[3];
                    SensorManager.getRotationMatrix(R, null, accelerometerValues, magnValues);
                    SensorManager.getOrientation(R, values);
                    // values为三个角
                    mOrientation = values[0];
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    public int getmStepNum() {
        return mStepNum;
    }

    public int getmSumStepNum() {
        return mSumStepNum;
    }

    public float getmOrientation() {
        return mOrientation;
    }

    public float getmPressure() {
        return mPressure;
    }

    public float getmHeight() {
        return mHeight;
    }


}
