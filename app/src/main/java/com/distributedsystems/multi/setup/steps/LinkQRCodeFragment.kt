package com.distributedsystems.multi.setup.steps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.distributedsystems.multi.R
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import kotlinx.android.synthetic.main.fragment_link_qr_code.*

class LinkQRCodeFragment : Fragment(), QRCodeReaderView.OnQRCodeReadListener {

    private val REQUEST_PERMISSION_CODE = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_link_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAndRequestPermissions()
        setOnClicks()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    enableCamera()
                } else {
                    qr_scanner.setOnClickListener {
                        checkAndRequestPermissions()
                    }
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if(ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CODE)
        } else {
            enableCamera()
        }
    }

    private fun enableCamera() {
        qr_scanner.invalidate()
        qr_scanner.setBackCamera()
        qr_scanner.setOnQRCodeReadListener(this)
        qr_scanner.startCamera()
    }

    private fun setOnClicks() {
        no_device_btn.setOnClickListener {
            findNavController().navigate(R.id.setPassphraseFragment)
        }
    }

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
    }

}