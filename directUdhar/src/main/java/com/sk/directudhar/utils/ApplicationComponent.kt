package com.sk.directudhar.utils

import com.sk.directudhar.di.NetworkModule
import com.sk.directudhar.ui.adharcard.AadhaarCardFragment
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadhaarOtpFragment
import com.sk.directudhar.ui.agreement.EAgreementFragment
import com.sk.directudhar.ui.agreement.agreementOtp.EAgreementOtpFragment
import com.sk.directudhar.ui.applyloan.ApplyLoanFragment
import com.sk.directudhar.ui.cibilscore.CibilScoreFragment
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilOtpFragment
import com.sk.directudhar.ui.approvalpending.ApprovalPendingFragment
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.ui.mandate.EMandateFragment
import com.sk.directudhar.ui.myaccount.MyAccountFragment
import com.sk.directudhar.ui.pancard.PanCardFragment
import com.sk.directudhar.ui.success.SuccessFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(mainActivitySDk: MainActivitySDk)

    fun injectMyAccount(fragment: MyAccountFragment)
    fun injectApplyLoan(fragment: ApplyLoanFragment)
    fun injectPanCard(fragment: PanCardFragment)
    fun injectAadhaarCard(fragment: AadhaarCardFragment)
    fun injectAadhaarOtp(fragment: AadhaarOtpFragment)
    fun injectEMandate(fragment: EMandateFragment)
    fun injectCiBil(fragment: CibilScoreFragment)
    fun injectCiBilOTP(fragment: CiBilOtpFragment)
    fun injectApprovalPending(fragment: ApprovalPendingFragment)
    fun injectSuccessPending(fragment: SuccessFragment)
    fun injectAgreement(fragment: EAgreementFragment)
    fun injectAgreementOtp(fragment: EAgreementOtpFragment)



}