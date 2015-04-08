package net.chpoon92.flashlight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
  private boolean hasFlash;
  private boolean isFlashOn;
  private Camera mCamera;
  private Camera.Parameters mParameters;
  private Switch flashlightSwitch;
  private TextView flashlightSwitchText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    flashlightSwitch = (Switch) findViewById(R.id.flashlightSwitch);
    flashlightSwitchText = (TextView) findViewById(R.id.flashlightSwitchText) ;

    // Check if the device does have flashlight
    hasFlash = getApplicationContext().getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    if (!hasFlash) {
      // Device doesn't support flash
      // Show alert message and close the application
      AlertDialog.Builder warningBuilder = new AlertDialog.Builder(MainActivity.this);
      warningBuilder.setTitle(R.string.warningTitle);
      warningBuilder.setMessage(R.string.warningMessage);
      warningBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          // User clicked OK button
          finish();
        }
      });
      AlertDialog warning = warningBuilder.create();
      warning.show();
    }

    // Create flashlight switch listener
    flashlightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          // Turn on the flashlight
          isFlashOn = true;
          turnOnFlashlight();
        } else {
          // Turn off the flashlight
          isFlashOn = false;
          turnOffFlashlight();
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Obtain the camera
    if (mCamera == null) {
      mCamera = Camera.open();
      mParameters = mCamera.getParameters();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();

    turnOffFlashlight();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if(isFlashOn)
      turnOnFlashlight();
  }

  @Override
  protected void onStop() {
    super.onStop();

    // Release the camera
    if (mCamera != null) {
      mCamera.release();
      mCamera = null;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void turnOnFlashlight() {
    mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
    mCamera.setParameters(mParameters);
    mCamera.startPreview();
    flashlightSwitchText.setText(R.string.switch_on);
  }

  private void turnOffFlashlight() {
    mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
    mCamera.setParameters(mParameters);
    mCamera.stopPreview();
    flashlightSwitchText.setText(R.string.switch_off);
  }
}
