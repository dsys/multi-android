package com.distributedsystems.multi.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.MainActivity
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.db.Wallet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.component_card.*
import net.glxn.qrgen.android.QRCode
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<ProfileViewModel>
    private lateinit var viewModel : ProfileViewModel
    private var wallet : Wallet? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(ProfileViewModel::class.java, viewModelFactory)

        renderWalletDetails()
    }

    private fun renderWalletDetails() {
        compositeDisposable.add(viewModel.getDefaultWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    wallet = it
                    val publicKey = wallet!!.ethAddress
                    val qrBitmap = QRCode.from(publicKey).bitmap()
                    issue_date.text = resources.getString(R.string.wallet_issue_date,
                            formatIssuedDate(wallet!!.insertedAt!!))
                    wallet_name.text = wallet!!.name
                    eth_address.text = resources.getString(R.string.wallet_address_subtext,
                            publicKey.substring(publicKey.length - 5 until publicKey.length))
                    qr_code.setImageBitmap(qrBitmap)
                })
    }

    private fun formatIssuedDate(date: Date) : String {
        val sdf = SimpleDateFormat("MM/YY", Locale.getDefault())
        return sdf.format(date)
    }

    override fun onDetach() {
        super.onDetach()
        compositeDisposable.clear()
    }
}