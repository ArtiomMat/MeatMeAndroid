package com.example.meatme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class FaceActivty extends AppCompatActivity {

	private PreviewView previewView;
	// This is an EXTREMELY dirty method, by making it static we can just access the camera
	// from the startPreview cameraProviderFuture.addListener. Until I figure out an efficient
	// method to retreive the camera this is how it will be.
	private Camera camera = null;

	private static final int MY_CAMERA_REQUEST_CODE = 100;

	private void setCamera(Camera camera) {
		this.camera = camera;
	}

	private void setupCameraSettings() {
		if (camera == null)
			return;
		camera.getCameraControl().setLinearZoom(0.5f);
	}

	// Called by onRequestPermissionsResult() and onCreate() when we get premissions for camera.
	// It mainly binds the peview view to the camera for displaying information to the user.
	private void startPreview() {

		previewView = findViewById(R.id.preview_view);

		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

		cameraProviderFuture.addListener(() -> {
			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

				Preview preview = new Preview.Builder().build();

				preview.setSurfaceProvider(previewView.getSurfaceProvider());

				if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
					cameraProvider.unbindAll();

					Camera camera = cameraProvider.bindToLifecycle(
							this,
							CameraSelector.DEFAULT_FRONT_CAMERA,
							preview);

					//camera.getCameraControl().setLinearZoom(0.5f);

					setCamera(camera);
					setupCameraSettings();
				}
				else {
					Toast.makeText(this, "No front camera lol", Toast.LENGTH_SHORT).show();
				}
			} catch (ExecutionException | InterruptedException e){
				Toast.makeText(this, "Unexpected error.", Toast.LENGTH_SHORT).show();
			} catch (CameraInfoUnavailableException e) {
				Toast.makeText(this, "Failed to fetch information!", Toast.LENGTH_SHORT).show();
			}
		}, ContextCompat.getMainExecutor(this));
	}

	// Called by the super class I think, when the application is reopened, after running in
	// background, note, onStop is the opposite, called when the application is put on back
	@Override
	protected void onResume() {
		super.onResume();
		setupCameraSettings();
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == MY_CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				startPreview();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_activty);

		// Request premmisions if we don't have them
		if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
			requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
		else
			startPreview();

	}
}