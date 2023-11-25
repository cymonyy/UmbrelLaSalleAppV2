package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.common.Barcode
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Surface
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminQrScannerBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QRScannerFragment : BottomSheetDialogFragment() {


    private val cameraRequestCode = 101
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var binding: AdminQrScannerBinding

     // Register the permissions callback, which handles the user's response to the
// system permissions dialog. Save the return value, an instance of
// ActivityResultLauncher. You can use either a val, as shown in this snippet,
// or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }
////    private lateinit var codeScanner: CodeScanner
    private var mListener: BottomSheetListener? = null
    interface BottomSheetListener {
        fun onDataSent(requestMode: Boolean, transaction: TransactionModel)
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        mListener = listener
    }
//
//
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AdminQrScannerBinding.inflate(inflater, container, false)


    return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasCameraPermission())
            Handler(Looper.getMainLooper()).postDelayed({
                setupCamera()
            }, 500) // Delay in milliseconds
        else requestPermission()

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            cameraRequestCode
        )
    }


    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == cameraRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set up the camera
                Handler(Looper.getMainLooper()).postDelayed({
                    setupCamera()
                }, 500) // Delay in milliseconds
            } else {
                // Permission denied, handle accordingly
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // User denied without checking "Don't ask again", explain the need for the permission
                    showPermissionExplanationSnackbar()
                    dismiss()
                } else {
                    // User denied with "Don't ask again", direct them to the settings
                    showSettingsSnackbar()
                    dismiss()
                }
            }
        }
    }

    @OptIn(ExperimentalGetImage::class) private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())


        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_PDF417
        ).build()
        // getClient() creates a new instance of the MLKit barcode scanner with the specified options
        barcodeScanner = BarcodeScanning.getClient(options)



        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // setting up the preview use case
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            val analysisUseCase = ImageAnalysis.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysisUseCase.setAnalyzer(cameraExecutor) { image ->
                val inputImage =
                    InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
                processImageProxy(inputImage)
            }

            // configure to use the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    //TODO: Add Image analysis use case
                    analysisUseCase
                )
            } catch (illegalStateException: IllegalStateException) {
                // If the use case has already been bound to another lifecycle or method is not called on main thread.
                Log.e("CAMERA", illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                Log.e("CAMERA", illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @OptIn(ExperimentalGetImage::class) private fun processImageProxy(imageProxy: InputImage) {

            barcodeScanner.process(imageProxy)
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)
                    // `rawValue` is the decoded value of the barcode
                    barcode?.rawValue?.let {value ->
                        // update our textView to show the decoded value
//                        binding.bottomText.text =
//                            getStringvalue

                        Toast.makeText(requireContext(), value, Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    // This failure will happen if the barcode scanning model
                    // fails to download from Google Play Services
                    Log.e("BARCODE", it.message.orEmpty())
                }.addOnCompleteListener {
                    // When the image is from CameraX analysis use case, must
                    // call image.close() on received images when finished
                    // using them. Otherwise, new images may not be received
                    // or the camera may stall.
                }
        }


    private fun showSettingsSnackbar() {
        Snackbar.make(
            requireView(),
            "Camera permission is required to scan QR codes. Open settings?",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            openAppSettings()
        }.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    private fun showPermissionExplanationSnackbar() {
        Snackbar.make(
            requireView(),
            "Camera permission is required to scan QR codes.",
            Snackbar.LENGTH_LONG
        ).show()
    }
}





//

//
//
//
//
//
//    override fun onResume() {
//        super.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//    }
//
////    private fun setupPermissions(){
////        val permission = ContextCompat.checkSelfPermission(requireContext(),
////            android.Manifest.permission.CAMERA)
////
////        if (permission != PackageManager.PERMISSION_GRANTED){
////            makeRequest()
////        }
////    }
//
////    private fun makeRequest(){
////        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), cameraRequestCode)
////    }
//
////    @Deprecated("Deprecated in Java")
////    override fun onRequestPermissionsResult(
////        requestCode: Int,
////        permissions: Array<out String>,
////        grantResults: IntArray
////    ) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
////        when (requestCode) {
////            cameraRequestCode -> {
////                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
////                    if (isAdded) {
////                        Toast.makeText(
////                            requireContext(),
////                            "You need the camera permission to be able to use the QR Code Scanner",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    }
////                } else {
////                    // Permission granted, you can perform actions here
////                }
////            }
////        }
////    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
//        dialog.setOnShowListener {
//            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
//            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
//        }
//        return dialog
//    }


