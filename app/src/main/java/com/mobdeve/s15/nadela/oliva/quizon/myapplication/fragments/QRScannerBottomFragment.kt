package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.R
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ViewfinderView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminQrScannerBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel


open class QRScannerBottomFragment: BottomSheetDialogFragment() {

    private val cameraRequestCode = 101
    private lateinit var binding: AdminQrScannerBinding
    private lateinit var codeScanner: CodeScanner

    private var mListener: BottomSheetListener? = null
    interface BottomSheetListener {
        fun onDataSent(requestMode: Boolean, transaction: TransactionModel)
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        mListener = listener
    }
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
        setupPermissions()
        codeScanner()
    }

    private fun codeScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK //what camera to use
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    //store transaction id here
                    val transactionID = it.text
                    launchAdminTransactionFormFragment(transactionID)
                    Toast.makeText(
                        requireContext(),
                        transactionID,
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
            }

            errorCallback = ErrorCallback {
                requireActivity().runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${it.message}")
                }
            }
        }
    }

    private fun launchAdminTransactionFormFragment(transactionID: String) {
        val fragment = AdminTransactionFormFragment(transactionID)
        fragment.setBottomSheetListener(object : AdminTransactionFormFragment.BottomSheetListener{
            override fun onDataSent(requestMode: Boolean, transaction: TransactionModel) {
                mListener?.onDataSent(requestMode, transaction)
            }
        })
        val fragmentManager = requireActivity().supportFragmentManager
        fragment.show(fragmentManager, tag)
    }




    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), cameraRequestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraRequestCode -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (isAdded) {
                        Toast.makeText(
                            requireContext(),
                            "You need the camera permission to be able to use the QR Code Scanner",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Permission granted, you can perform actions here
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

//
//
//    private var capture: CaptureManager? = null
//    private var barcodeScannerView: DecoratedBarcodeView? = null
//    private var switchFlashlightButton: Button? = null
//    private var viewfinderView: ViewfinderView? = null
//    private lateinit var viewBinding: AdminQrScannerBinding
//
//
//    private val fragmentLauncher = registerForActivityResult(
//        ScanContract()
//    ) { result: ScanIntentResult ->
//        if (result.contents == null) {
//            Toast.makeText(context, "Cancelled from fragment", Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(
//                context,
//                "Scanned from fragment: " + result.contents,
//                Toast.LENGTH_LONG
//            ).show()
//        }
//    }
//
//    open fun ScanFragment() {}
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        viewBinding = AdminQrScannerBinding.inflate(inflater, container, false)
//
//
//        val scan = view?.findViewById<Button>(R.id.scan_from_fragment)
//        scan.setOnClickListener { v: View? -> scanFromFragment() }
//        return view
//    }
//
//    open fun scanFromFragment() {
//        fragmentLauncher.launch(ScanOptions())
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
////        scanCode()
//
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
////
////        barcodeScannerView = viewBinding.zxingBarcodeScanner;
////        barcodeScannerView!!.setTorchListener(this);
////
////
////        viewfinderView =  view.findViewById(R.id.zxing_viewfinder_view);
////
////
////        capture = CaptureManager(requireActivity(), barcodeScannerView)
////        capture!!.initializeFromIntent(requireActivity().intent, savedInstanceState)
////        capture!!.setShowMissingCameraPermissionDialog(false)
////        capture!!.decode()
////
////        changeMaskColor(null)
////        changeLaserVisibility(true)
//
//    }
//
////    open fun scanMarginScanner(view: View?) {
////        val options = ScanOptions()
////        options.setOrientationLocked(false)
////        options.captureActivity = viewBinding.zxingBarcodeScanner as DecoratedBarcodeView
////        barcodeLauncher.launch(options)
////    }
//
////    /**
////     * Check if the device's camera has a Flashlight.
////     * @return true if there is Flashlight, otherwise false.
////     */
////    private fun hasFlash(): Boolean? {
////        return context?.packageManager
////            ?.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
////    }
////
////     fun switchFlashlight(view: View?) {
////        if (getString(R.string.turn_on_flashlight) == switchFlashlightButton!!.text) {
////            barcodeScannerView!!.setTorchOn()
////        } else {
////            barcodeScannerView!!.setTorchOff()
////        }
////    }
////
////    open fun changeMaskColor(view: View?) {
////        val rnd = Random()
////        val color: Int = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
////        viewfinderView!!.setMaskColor(color)
////    }
////
////    open fun changeLaserVisibility(visible: Boolean) {
////        viewfinderView!!.setLaserVisibility(visible)
////    }
////
////
////
////     override fun onResume() {
////        super.onResume()
////        capture!!.onResume()
////    }
////
////     override fun onPause() {
////        super.onPause()
////        capture!!.onPause()
////    }
////
////     override fun onDestroy() {
////        super.onDestroy()
////        capture!!.onDestroy()
////    }
////
////    override fun onSaveInstanceState(outState: Bundle) {
////        super.onSaveInstanceState(outState)
////        capture!!.onSaveInstanceState(outState)
////    }
//
//
//    //
////    private lateinit var viewfinderView: ViewfinderView
////    private lateinit var decoratedBarcodeView: DecoratedBarcodeView
////

////
//
////
////    private final val barcodeLauncher = registerForActivityResult(ScanContract())
////    { result: ScanIntentResult ->
////        if (result.contents == null) {
////            Log.d("SCANNED", result.contents)
////
////            //                    Toast.makeText(this@MyActivity, "Cancelled", Toast.LENGTH_LONG).show()
////        } else {
////            Toast.makeText(
////                requireContext(),
////                "Scanned: " + result.contents,
////                Toast.LENGTH_LONG
////            ).show()
////        }
////    }
////
////    private fun scanCode() {
////
////        Log.d("OPENSCAN", "HERE")
////        val options = ScanOptions()
////
////        options.setPrompt("Volume up to flash on")
////        options.setBeepEnabled(true)
////        options.setOrientationLocked(true)
////
////        barcodeLauncher.launch(options)
////    }
////
////
////
////    override fun onTorchOn() {
////    switchFlashlightButton?.setText(R.string.turn_off_flashlight);
////    }
////
////    override fun onTorchOff() {
////        switchFlashlightButton?.setText(R.string.turn_on_flashlight);
////    }


}