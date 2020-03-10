package dev.chu.memo.view.activity

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chu.memo.R
import dev.chu.memo.base.BaseActivity
import dev.chu.memo.common.Const
import dev.chu.memo.common.Const.usingPermissions
import dev.chu.memo.data.local.ImageData
import dev.chu.memo.databinding.ActivityAddBinding
import dev.chu.memo.etc.extension.*
import dev.chu.memo.view.adapter.ImageAdapter
import dev.chu.memo.view_model.RoomViewModel
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : BaseActivity<ActivityAddBinding>() {
    @LayoutRes
    override fun getLayoutRes(): Int = R.layout.activity_add

    private val roomVM by lazy { ViewModelProvider(this)[RoomViewModel::class.java] }
    private val adapter by lazy { ImageAdapter(mutableListOf()) }

    private var photoUri: Uri? = null
    private var timeStamp: String? = null
    private var isBackPress: Boolean = false

    // region lifeCycle
    override fun initView() {
        Log.i(TAG, "initView")

        binding.activity = this
        binding.viewModel = roomVM

        setActionBarHome(binding.includeToolbar.toolbar, R.drawable.arrow_back_white)
        binding.includeToolbar.toolbarTv.text = ""
        binding.includeToolbar.toolbarTvEtc.text = getString(R.string.picture)
        binding.includeToolbar.toolbarTvEtc.setOnClickListener {
            showCameraAndGalleryDialog()
        }

        val isWritingMemo = intent.getBooleanExtra(Const.EXTRA.IS_WRITING_MEMO, false)
        if(isWritingMemo) {
            roomVM.title.value = getPrefString(Const.PREF.MEMO_TITLE, "")
            roomVM.content.value = getPrefString(Const.PREF.MEMO_CONTENT, "")
        }

        setRecyclerView()
        observeViewModel()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")

        if(!isBackPress) {
            setPrefString(Const.PREF.MEMO_TITLE, roomVM.title.value)
            setPrefString(Const.PREF.MEMO_CONTENT, roomVM.content.value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }
    // endregion

    private fun showCameraAndGalleryDialog() {
        if (isPermissionsVersion() && !hasPermissions(*usingPermissions)) {
            checkUsingPermission(usingPermissions, Const.REQUEST_CODE_PERMISSIONS)
            return
        }

        val info =
            if(isExternalStorageWritable()) arrayOf<CharSequence>(getString(R.string.camera), getString(R.string.gallery))
            else arrayOf<CharSequence>(getString(R.string.gallery))
        val builder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.get_image))
            setItems(info) { dialogInterface, which ->
                if(isExternalStorageWritable()) {
                    when (which) {
                        0 -> doTakePictureIntent()
                        1 -> doTakeGalleryAction()
                    }
                } else {
                    doTakeGalleryAction()
                }
                dialogInterface.dismiss()
            }
        }
        builder.show()
    }

    // region 사진 찍기
    private fun doTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(this, "dev.chu.memo.fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, Const.REQUEST_CODE_CAMERA_PERMISSION)
                }
            }
        }
    }
    // endregion

    // region 앨범에서 가져오기
    private fun doTakeGalleryAction() {
        startActivityForResult(Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            type = MediaStore.Images.Media.CONTENT_TYPE
        }, Const.REQUEST_CODE_GALLERY_PERMISSION)
    }
    // endregion

    // region 앨범에 사진 저장
    private fun galleryAddPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
//                put(MediaStore.Audio.Media.RELATIVE_PATH, "DCIM/Camera")     // 파일이 저장되는 위치
                put(MediaStore.Images.Media.DISPLAY_NAME, File(getCurrentPhotoPath()).name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                put(MediaStore.Images.Media.IS_PENDING, 1)      // 이 속성은 아직 내가 파일을 write하지 않았으니, 다른 곳에서 이 데이터를 요구하면 무시하라는 의미입니다. 파일을 모두 write한 뒤에 이 속성을 0으로 update해줘야 합니다.
            }

            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val item = contentResolver.insert(collection, values)!!

            contentResolver.openFileDescriptor(item, "w", null).use {
                // write something to OutputStream
                FileOutputStream(it!!.fileDescriptor).use { outputStream ->
                    val imageInputStream: InputStream = FileInputStream(getCurrentPhotoPath())
                    val data = imageInputStream.read()
                    outputStream.write(data)
                    imageInputStream.close()
                    outputStream.close()
                }
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(item, values, null, null)
        } else {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(getCurrentPhotoPath())
                mediaScanIntent.data = Uri.fromFile(f)
                sendBroadcast(mediaScanIntent)
            }
        }

        roomVM.addImageUrl(ImageData(imageUrl = photoUri.toString()))
        adapter.addItem(ImageData(imageUrl = photoUri.toString()))
        showToast(R.string.save_image)
    }
    // endregion

    private fun setRecyclerView() {
        binding.writeRvImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.writeRvImage.adapter = adapter
    }

    private fun observeViewModel() {
        roomVM.isSave.observe(this, Observer {
            if(it) {
                removePref(Const.PREF.MEMO_TITLE)
                removePref(Const.PREF.MEMO_CONTENT)

                roomVM.isSave.value = false
                showToast(R.string.save_memo)
                finish()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            Const.REQUEST_CODE_PERMISSIONS -> {
                if(grantResults.isNotEmpty() && grantResults.size == permissions.size) {
                    var isPermissionGranted = true
                    for (i in grantResults.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isPermissionGranted = false
                            break
                        }
                    }

                    if(isPermissionGranted) {
                        showCameraAndGalleryDialog()
                    } else {
                        alertDialog(R.string.please_need_permissions)
                    }
                }

                return
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            photoUri = null
            return
        }

        when (requestCode) {
            Const.REQUEST_CODE_CAMERA_PERMISSION -> {
                timeStamp = SimpleDateFormat("yyyy_MM_dd", Locale("ko")).format(Date())
                galleryAddPicture()
            }

            Const.REQUEST_CODE_GALLERY_PERMISSION -> {
                timeStamp = SimpleDateFormat("yyyy_MM_dd", Locale("ko")).format(Date())

                photoUri = if (data?.data != null) {
                    data.data
                } else
                    null

                roomVM.addImageUrl(ImageData(imageUrl = photoUri.toString()))
                adapter.addItem(ImageData(imageUrl = photoUri.toString()))
            }
        }
    }

    // region onClickEvent
    fun onClickFinish() { onBackPressed() }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!roomVM.title.value.isNullOrEmpty() ||
            !roomVM.content.value.isNullOrEmpty()) {
            confirmDialog(
                getString(R.string.back_memo),
                DialogInterface.OnClickListener { _, _ ->
                    isBackPress = true
                    finish()
                },
                negativeTextResId = R.string.cancel
            )
        } else finish()
    }
    // endregion
}