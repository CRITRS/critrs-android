package org.govhack.critrs

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import java.io.*

class SharePromptFragment: DialogFragment {
    constructor() : super() {
        arguments = Bundle()
    }

    fun setArguments(imagePath: String, animalName: String): SharePromptFragment {
        this.imagePath = imagePath
        this.animalName = animalName
        return this
    }

    var animalName: String
        get() = arguments.getString("name")
        set(value) = arguments.putString("name", value)

    var imagePath: String
        get() = arguments.getString("image")
        set(value) = arguments.putString("image", value)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val imageView = ImageView(context)
        if (savedInstanceState == null) {
            // TODO: Move this to background thread
            val file = File(imagePath)
            while (!file.exists()) {
                Thread.sleep(100)
            }
            val bitmap = BitmapFactory.decodeFile(imagePath).squareCropTop()
            imageView.setImageBitmap(bitmap)
            saveFile(bitmap)?.let {
                file.delete()
                imagePath = it
            }
        }
        else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath))
        }
        return AlertDialog.Builder(context)
                .setTitle(getString(R.string.share_title, animalName))
                .setView(imageView)
                .setNegativeButton(android.R.string.ok, null)
                .setPositiveButton(R.string.share) { d: DialogInterface, i: Int ->
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(imagePath)))
                    startActivity(Intent.createChooser(intent, getString(R.string.share)))
                }
                .create()
    }

    private fun saveFile(capturedBitmap: Bitmap): String? {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        var fOutputStream: OutputStream? = null
        val saveDir = File(path + "/${getString(R.string.app_name)}/")
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }
        val file = File(saveDir, "${System.currentTimeMillis()}.jpg")
        try {
            fOutputStream = FileOutputStream(file)
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream)
            fOutputStream.flush()
            fOutputStream.close()
            MediaStore.Images.Media.insertImage(context.contentResolver,
                    file.absolutePath, file.name, file.name)
            return file.absolutePath
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }


    }
}