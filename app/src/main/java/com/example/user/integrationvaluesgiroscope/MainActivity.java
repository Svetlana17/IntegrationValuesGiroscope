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
    TextView fiX;
    TextView fiY;
    TextView fiZ;
     float alphaAcc = (float) 0.8;
     float x_high_pass_f, y_high_pass_f, z_high_pass_f;
   // TextView ;
    private final float[] deltaRotationVector = new float[4];//+++
   // private long timestamp;//+++
  //  private float[] gravity = new float[3];
    //фильтр верхних частот
    TextView x_high_pass_x;
    TextView y_high_pass_y;
    TextView z_high_pass_z ;
    float Sx;
    TextView Sxf, Sy;
    TextView Sz;
    TextView tan, cren;
    float x_high_pass, y_high_pass, z_high_pass;
    private float[] gravity = new float[]
            { 0, 0, 0 };
    //Для расчета шага дискретизации
    // Raw accelerometer data
    private float[] input = new float[]
            { 0, 0, 0 };

    private int count = 0;
   // private float dt = 0;
    private float dt;
    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();
    // Gravity and linear accelerations components for the
// Wikipedia low-pass filter

    int descritazation = 10;
    private float[] linearAcceleration = new float[]
            { 0, 0, 0 };

    float vx, vy, vz;
    private MySensorEvent prefaccEvent;
    float fi, l;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_scroll);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
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

        fiX=(TextView)findViewById(R.id.alphaGirX);
        fiY=(TextView)findViewById(R.id.bettaGirY);
        fiZ=(TextView)findViewById(R.id.gammaGirZ);



        x_high_pass_x=(TextView)findViewById(R.id.FiltX);
        y_high_pass_y=(TextView)findViewById(R.id.FiltY);
        z_high_pass_z=(TextView)findViewById(R.id.FiltZ);

        Sxf=(TextView)findViewById(R.id.Sxg);
        Sy=(TextView)findViewById(R.id.Sy);
        Sz=(TextView)findViewById(R.id.Sz);

        //Для углов акселерометра
        tan=(TextView) findViewById(R.id.tan);
        cren=(TextView)findViewById(R.id.cren);


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

                //Вычисление угла поворота по гироскопу
                float fiX =(float) (x*dT);
                float fiY =(float) (y*dT);
                float fiz = (float)(z*dT);

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

    //расчет скорости
    public void addSamples(float[] acceleration) {
// Get a local copy of the sensor values
        System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);
        timestamp = System.nanoTime();

// Find the sample period (between updates).
// Convert from nanoseconds to seconds
        dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));
        count++;
    }

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
            tv_accX.setText("X  : " + x + " м/с2");
            tv_accY.setText("Y : " + y + " м/с2");
            tv_accZ.setText("Z : " + z + " м/с2");


            alpha_text.setText("Угол alpha:" + alpha_t);
            betta_text.setText("Угол B:" + betta_t);
            gamma_text.setText("Угол гама:" + gamma_t);

            // Применение фильтра верхних частот к акселерометру

            x_high_pass= (alphaAcc*gravity[0]+(1-alphaAcc)*event.values[0]);
            y_high_pass= (float) (alphaAcc*gravity[1]+(1-alphaAcc)*event.values[1]);
            z_high_pass= (float) (alphaAcc*gravity[2]+(1-alphaAcc)*event.values[2]);


            // Remove the gravity contribution with the high-pass filter.
            x_high_pass_f = event.values[0] - x_high_pass;
            y_high_pass_f = event.values[1] - y_high_pass;
            z_high_pass_f = event.values[2] - z_high_pass;


            x_high_pass_x.setText("X High"+x_high_pass_f);
            y_high_pass_y.setText("Y High"+y_high_pass_f);
            z_high_pass_z.setText("Z High"+z_high_pass_f);

            // 2.Вычисление углов Эйлера
//                По показаниям акселерометров можно вычислить значения углов  тангажа и крена[5]:
//                θа = arcsin(ax/sqrt(ax^2+ay2+az^2)    θа – угол тангажа (pitch),
//                  ϕа = −arcsin(ay/sqrt(ax^2+ay2+az^2))/cos(θа)   ϕа – угол крена (roll),

               //вычисление тангажа  по акслерометру
                l = (float) Math.asin(event.values[0]/sqrt((event.values[0]*event.values[0]+event.values[1]+event.values[1]+event.values[2]*event.values[2])));
                //вычисление угла крена
                fi = (float) -Math.asin(event.values[1]/sqrt((event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]))/cos(l));


                tan.setText("Угол тангажа" + l);
                cren.setText("Угол крена" + fi);
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
