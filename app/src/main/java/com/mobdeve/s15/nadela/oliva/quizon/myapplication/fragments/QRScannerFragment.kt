package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminQrScannerBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel

class QRScannerFragment : BottomSheetDialogFragment() {
////    private val cameraRequestCode = 101
//    private lateinit var binding: AdminQrScannerBinding
////    private lateinit var codeScanner: CodeScanner
//    private var mListener: BottomSheetListener? = null
//    interface BottomSheetListener {
//        fun onDataSent(requestMode: Boolean, transaction: TransactionModel)
//    }
//
//    fun setBottomSheetListener(listener: BottomSheetListener) {
//        mListener = listener
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = AdminQrScannerBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        setupPermissions()
//        codeScanner()
//    }
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


}