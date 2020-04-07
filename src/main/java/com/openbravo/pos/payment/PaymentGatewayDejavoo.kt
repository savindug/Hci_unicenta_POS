package com.openbravo.pos.payment

import uk.co.pos_apps.PosApps
import uk.co.pos_apps.payment.dejavoo.DejavooProcessor

class PaymentGatewayDejavoo :PaymentGateway {

    val PAYMENT_PROCESSOR = "Dejavoo"

    override fun execute(payinfo: PaymentInfoMagcard?) {
        DejavooProcessor.INSTANCE.paymentComplete = false

        var timer = 0
        val timeout = 120

        PosApps.initPayment(PAYMENT_PROCESSOR, payinfo?.total)

        while (!DejavooProcessor.INSTANCE.paymentComplete) {
            Thread.sleep(1000)
            timer += 1
            if (timer > timeout) break
        }

        if (DejavooProcessor.INSTANCE.response == null) {
            payinfo?.paymentError("Transaction Error ... Please try again", "No Response")
        }
        else if (DejavooProcessor.INSTANCE.response.success == "0"){
            payinfo?.cardName = DejavooProcessor.INSTANCE.response.cardType
            payinfo?.setVerification(DejavooProcessor.INSTANCE.response.verification)
            payinfo?.chipAndPin = true
            payinfo?.paymentOK(DejavooProcessor.INSTANCE.response.authCode, DejavooProcessor.INSTANCE.response.transactionId, DejavooProcessor.INSTANCE.response.message)
        }
        else if (DejavooProcessor.INSTANCE.response.success == "1"){
            payinfo?.paymentError("Transaction Error ... Please try again", DejavooProcessor.INSTANCE.response.message)
        }
    }

}