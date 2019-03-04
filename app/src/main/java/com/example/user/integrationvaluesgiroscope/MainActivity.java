package com.example.user.integrationvaluesgiroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static android.util.Half.EPSILON;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class MainActivity extends AppCompatActivity  implements SensorEventListener {
    TextView textX, textY, textZ;
    TextView tv_accX, tv_accY, tv_accZ, alpha_text, betta_text, gamma_text;
    TextView tv_or_0, tv_or_1, tv_or_2, tv_or_3;//++
    SensorManager gyroManager, accManager;
    Sensor gyroSensor, accSensor;
    private static final float NS2S = 1.0f / 1000000000.0f;//+++
    //вычисление угла поворота по гироскопу
    float alpha, betta, gamma;

   // TextView ;
    private final float[] deltaRotationVector = new float[4];//+++
    private long timestamp;//+++
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_scroll);
        ImageView imageView = (ImageView) findViewById(R.id.icon);
        imageView.setImageResource(R.drawable.icon72);
        gyroManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroSensor = gyroManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        accManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = accManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        textX = (TextView) findViewById(R.id.GirtextX);
        textY = (TextView) findViewById(R.id.GirtextY);
        textZ = (TextView) findViewById(R.id.GirtextZ);

        tv_accX = (TextView) findViewById(R.id.AcctextX);
        tv_accY = (TextView)findViewById(R.id.AcctextY);
        tv_accZ = (TextView) findViewById(R.id.AcctextZ);

        tv_or_0 = (TextView) findViewById(R.id.OrintX);//+++
        tv_or_1 = (TextView) findViewById(R.id.OrintY);//+++
        tv_or_2 = (TextView) findViewById(R.id.OrintZ);////++++++
        tv_or_3 = (TextView) findViewById(R.id.Orint4);//+++


        alpha_text = (TextView) findViewById(R.id.alpha);
        betta_text = (TextView) findViewById(R.id.betta);
        gamma_text = (TextView) findViewById(R.id.gamma);



    }
    public void onResume() {
        super.onResume();
        gyroManager.registerListener(gyroListener, gyroSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        accManager.registerListener(accListener, accSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        gyroManager.unregisterListener(gyroListener);
        accManager.unregisterListener(accListener);

    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) {
        }

        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            textX.setText("X : " + x + " rad/s");
            textY.setText("Y : " + y + " rad/s");
            textZ.setText("Z : " + z + " rad/s");


            if (timestamp != 0) {//++
                final float dT = (event.timestamp - timestamp) * NS2S;//+++
                float alpha, betta, gamma;
                float axisX = event.values[0];//+++
                float axisY = event.values[1];//+++
                float axisZ = event.values[2];//+++
                // Calculate the angular speed of the sample
                float omegaMagnitude = (float) sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);//+++
                if (omegaMagnitude > EPSILON) {//+++
                    axisX /= omegaMagnitude;//+++
                    axisY /= omegaMagnitude;//+++
                    axisZ /= omegaMagnitude;//+++
                    // Integrate around this axis with the angular speed by the timestep
                    // in order to get a delta rotation from this sample over the timestep
                    // We will convert this axis-angle representation of the delta rotation
                    // into a quaternion before turning it into the rotation matrix.
                }
                float thetaOverTwo = omegaMagnitude * dT / 2.0f;//+++
                float sinThetaOverTwo = (float) sin(thetaOverTwo);//+++
                float cosThetaOverTwo = (float) cos(thetaOverTwo);//+++
                deltaRotationVector[0] = sinThetaOverTwo * axisX;//+++
                deltaRotationVector[1] = sinThetaOverTwo * axisY;//+++
                deltaRotationVector[2] = sinThetaOverTwo * axisZ;//+++
                deltaRotationVector[3] = cosThetaOverTwo;//+++

                 alpha=axisX*dT;
                 betta=axisY*dT;
                 gamma = axisZ * dT;

            }
            timestamp = event.timestamp;//+++
            float[] deltaRotationMatrix = new float[9];//+++
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);//+++
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;


           // }//+++

            tv_or_0.setText("OR X:" + deltaRotationVector[0]); //++
            tv_or_1.setText("OR Y:" + deltaRotationVector[1]); //++
            tv_or_2.setText("OR Z:" + deltaRotationVector[2]); //++
            tv_or_3.setText("OR  :" + deltaRotationVector[3]); //++
        };
    };

    public SensorEventListener accListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Вычисление угла наклона по акслерометру
            float  alpha_t= (float) (x/(sqrt((y*y)+(z*z))));
            float betta_t = (float) (y/(sqrt((x*x)+z*z)));
            float gamma_t = (float) (z/(sqrt(x*x)+(y*y)));
            //
            tv_accX.setText("X Acc : " + x + " m/s2");
            tv_accY.setText("Y Acc: " + y + " m/s2");
            tv_accZ.setText("Z Acc: " + z + " m/s2");



            alpha_text.setText("Угол alpha:" + alpha_t);
            betta_text.setText("Угол B:" + betta_t);
            gamma_text.setText("Угол гама:" + gamma_t);

        }







        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
