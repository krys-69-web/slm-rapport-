package com.example.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

object ReportExporter {

    fun exportAndShareWord(context: Context, wordHtml: String, companyName: String): Boolean {
        return try {
            val safeName = companyName.replace("[^a-zA-Z0-9_-]".toRegex(), "_").ifBlank { "Entreprise" }
            val fileName = "Rapport_Stage_${safeName}.doc"
            val cacheDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val file = File(cacheDir, fileName)

            FileOutputStream(file).use { fos ->
                fos.write(wordHtml.toByteArray(StandardCharsets.UTF_8))
                fos.flush()
            }

            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/msword"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Rapport de stage - $companyName")
                putExtra(Intent.EXTRA_TEXT, "Voici le rapport de stage officiel 28 pages généré par SLM RAPPORT BUILDER SMARTLY.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Exporter et Ouvrir le Rapport Word (.doc)")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur export Word : ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun printOrSavePdf(activity: Activity, htmlContent: String, jobName: String = "Rapport_Stage_28Pages") {
        try {
            activity.runOnUiThread {
                val webView = WebView(activity)
                webView.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                        if (printManager != null) {
                            val printAdapter = webView.createPrintDocumentAdapter(jobName)
                            val printAttributes = PrintAttributes.Builder()
                                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                                .setResolution(PrintAttributes.Resolution("res1", "A4", 300, 300))
                                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                                .build()
                            printManager.print(jobName, printAdapter, printAttributes)
                        } else {
                            Toast.makeText(activity, "Service d'impression indisponible sur cet appareil", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Erreur préparation impression PDF : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Texte copié dans le presse-papier !", Toast.LENGTH_SHORT).show()
    }
}
