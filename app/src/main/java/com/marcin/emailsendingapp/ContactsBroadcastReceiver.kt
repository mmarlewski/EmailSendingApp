package com.marcin.emailsendingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import java.util.*
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

fun BroadcastReceiver.goAsync(
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    block: suspend () -> Unit)
{
    val pendingResult = goAsync()
    coroutineScope.launch(dispatcher) {
        block()
        pendingResult.finish()
    }
}

class ContactsBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (context == null || intent == null) return

        if (intent.action == "com.marcin.CONTACTS_ACTION")
        {
            val contactsText = intent.getStringExtra("com.marcin.CONTACTS_TEXT")
            if (contactsText != null)
            {
                sendEmail(contactsText)
            }
        }
    }

    fun sendEmail(text:String)
    {
        val EMAIL="marcinmarlewski@onet.pl"
        val PASSWORD="YOxuYctNcZIvuNc8sc1X"

        val properties= Properties()
        properties.put("mail.smtp.host", "smtp.poczta.onet.pl")
        properties.put("mail.smtp.port", "465")
        properties.put("mail.smtp.auth", "true")
        properties.put("mail.smtp.ssl.enable", "true")
        properties.put("mail.smtp.user", EMAIL)
        properties.put("mail.smtp.password", PASSWORD)
        properties.put("mail.smtp.socketFactory.port", "465")
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        properties.put("mail.smtp.socketFactory.fallback", "false")

        val session= Session.getInstance(properties,object: Authenticator()
        {
            public override fun getPasswordAuthentication() : PasswordAuthentication
            {
                return  PasswordAuthentication(EMAIL, PASSWORD)
            }
        })
        session.setDebug(true)

        val mimeMessage= MimeMessage(session)
        mimeMessage.setFrom(InternetAddress(EMAIL))
        mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(EMAIL))
        mimeMessage.setSubject("contacts")
        mimeMessage.setText(text)

        val transport = session.getTransport("smtp")
        transport.connect(EMAIL, PASSWORD)
        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients())
        transport.close()
    }
}