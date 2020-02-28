package dev.chu.memo.etc.extension

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction


fun AppCompatActivity.setActionBarHome(toolbar: Toolbar, @DrawableRes res: Int? = null) {
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        res?.let {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(it)
        } ?: run {
            setDisplayHomeAsUpEnabled(false)
            setHomeButtonEnabled(false)
            setHomeAsUpIndicator(null)
        }
    }
}

fun AppCompatActivity.hideActionBar() {
    supportActionBar?.hide()
}

// region 권한 관련
fun isPermissionsVersion(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun AppCompatActivity.checkUsingPermission(permissions: Array<String>, requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

// vararg : 가변인자 : 갯수가 정해지지 않는 인자로 인자의 개수를 유동적으로 받을 수 있습니다.
fun AppCompatActivity.hasPermissions(vararg permissions: String): Boolean =
    permissions.all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
// endregion

// fragment 변경
fun FragmentActivity.replaceFragment(@IdRes res: Int, fragment: Fragment, tag: String? = null) {
    val fragmentTransaction: FragmentTransaction = this.supportFragmentManager.beginTransaction()
    if (tag != null) {
        fragmentTransaction.replace(res, fragment, tag)
    } else {
        fragmentTransaction.replace(res, fragment)
    }
    fragmentTransaction.addToBackStack(null)
//    fragmentTransaction.commit()
    fragmentTransaction.commitAllowingStateLoss()
}