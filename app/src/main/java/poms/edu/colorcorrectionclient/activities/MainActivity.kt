package poms.edu.colorcorrectionclient.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import poms.edu.colorcorrectionclient.fragments.FiltersFragment
import poms.edu.colorcorrectionclient.fragments.ImageFragment
import poms.edu.colorcorrectionclient.R
import poms.edu.colorcorrectionclient.network.downloadFilterNamesAsyncAndDoOnSuccess
import poms.edu.colorcorrectionclient.network.parseFilterNames
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image.view.*
import okhttp3.*
import org.jetbrains.anko.*
import org.json.JSONObject
import poms.edu.colorcorrectionclient.images.getScaledBitmapForContainer
import poms.edu.colorcorrectionclient.network.ColorCorrectionHttpClient
import java.io.*


class MainActivity : Activity() {

    private lateinit var imageFragment: ImageFragment
    private lateinit var filtersFragment: FiltersFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createAndOpenImageFragment()

        downloadFilterNamesAsyncAndDoOnSuccess { _, response ->
            val itemNames = parseFilterNames(response)

            hideProgressBar()
            showFiltersInNewFragment(itemNames)

        }
    }

    private fun createAndOpenImageFragment() {
        imageFragment = ImageFragment.newInstance(onButtonPressedCallback = ::pickImageFromGallery)
        fragmentManager
            .beginTransaction()
            .replace(R.id.image_fragment_container, imageFragment)
            .commit()
    }

    private fun hideProgressBar() {
        progress_circular.visibility = View.GONE
    }

    private fun showFiltersInNewFragment(items: List<String>) {

        filtersFragment =
            FiltersFragment.newInstance(items, ::requestForApplyFilter)
        fragmentManager
            .beginTransaction()
            .replace(R.id.filters_fragment_container, filtersFragment)
            .commit()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fun retrieveImage(): Bitmap {
            val inputStream = contentResolver.openInputStream(data!!.data)
            return BitmapFactory.decodeStream(inputStream)
        }

        if (requestCode != REQUEST_PICK_IMAGE || resultCode != Activity.RESULT_OK)
            return

        val bitmap = retrieveImage()

        val imgContainer = imageFragment.view

        val scaledBitmap = getScaledBitmapForContainer(bitmap, imgContainer)

        imgContainer
            .main_image
            .imageBitmap = scaledBitmap

    }

    private fun getFilterInfo(itemName: String) {
        toast(itemName)
        val url = ColorCorrectionHttpClient.getAbsoluteUrl(
            "get_grid_by_name?name=$itemName"
        )
        ColorCorrectionHttpClient.get(url, object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call, response: Response) {
                val contents = response.body()!!.string()
                doAsync {
                    uiThread {
                        toast(contents)
                    }
                }
            }

        })
    }

    private fun getImgFile(): File {
        val img = imageFragment.view.main_image.image
        val imgBitmap = when(img) {
            is BitmapDrawable -> img.bitmap
            else -> throw NotImplementedError()
        }

        val tmpFile = File(filesDir, "tmp")
        val outputStream = BufferedOutputStream(FileOutputStream(tmpFile))
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return tmpFile
    }

    private fun uploadImageAndThen(imgFile: File, andThen: (String) -> Unit) {
        val url = ColorCorrectionHttpClient.getAbsoluteUrl("" +
                "send_image")


        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "a",
                RequestBody.create(MediaType.parse("image/jpg"), imgFile)
            )
            .build()

        ColorCorrectionHttpClient.post(
            url,
            requestBody,
            object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = response.body()?.string()
                    val imageToken = JSONObject(responseJson).getString("image_token")
                    andThen(imageToken)
                }

            }
        )
    }

    private fun requestForApplyFilter(filterName: String) {
        val imgFile = getImgFile()
        uploadImageAndThen(imgFile) { imageToken ->
            val url = ColorCorrectionHttpClient.getAbsoluteUrl(
                "process_image?image_token=$imageToken&grid_name=$filterName")
            runOnUiThread {
                Picasso
                    .get()
                    .load(url)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(
                        imageFragment.view.main_image
                    )
            }
        }

    }


    companion object {
        private const val REQUEST_PICK_IMAGE = 1
    }


}
