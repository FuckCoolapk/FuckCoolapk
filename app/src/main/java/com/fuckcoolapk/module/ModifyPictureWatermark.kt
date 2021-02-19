package com.fuckcoolapk.module

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.fuckcoolapk.utils.*
import com.fuckcoolapk.utils.ktx.*
import com.fuckcoolapk.view.CheckBoxForHook
import com.watermark.androidwm_light.Watermark
import com.watermark.androidwm_light.WatermarkBuilder
import com.watermark.androidwm_light.bean.WatermarkImage
import com.watermark.androidwm_light.bean.WatermarkText
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ModifyPictureWatermark {
    fun init() {
        val instance = getHookField(CoolapkContext.classLoader.loadClass("com.coolapk.market.view.settings.settingsynch.SettingSynchronized"), "INSTANCE")
        //动态临时关闭图片水印
        XposedHelpers.findClass("com.coolapk.market.view.feedv8.SubmitExtraViewPart", CoolapkContext.classLoader)
                .hookAfterMethod("initWith", "com.coolapk.market.view.feedv8.SubmitFeedV8Activity") {
                    (it.thisObject.callMethod("getView") as LinearLayout).addView(LinearLayout(CoolapkContext.activity).apply {
                        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(CoolapkContext.context, 48f)).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        orientation = LinearLayout.VERTICAL
                        addView(CheckBoxForHook(CoolapkContext.activity).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                gravity = Gravity.END
                                rightMargin = dp2px(CoolapkContext.context, 10f)
                            }
                            text = "临时关闭水印"
                            setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                                val pictureWatermarkPosition = CoolapkSP.coolapkSP.getString("picture_watermark_position", "0")
                                if (b) {
                                    if (pictureWatermarkPosition != "0") {
                                        OwnSP.set("pictureWatermarkPosition", pictureWatermarkPosition!!)
                                        instance.callMethod("uploadSetting", "picture_watermark_position", "0", 1)
                                    }
                                } else {
                                    if (pictureWatermarkPosition != "0") {
                                        instance.callMethod("uploadSetting", "picture_watermark_position", OwnSP.ownSP.getString("pictureWatermarkPosition", "0"), 1)
                                        OwnSP.remove("pictureWatermarkPosition")
                                    }
                                }
                            }
                        })
                    })
                }
        XposedHelpers.findClass("com.coolapk.market.view.feedv8.BaseFeedContentHolder\$startSubmitFeed$2", CoolapkContext.classLoader)
                .hookBeforeMethod("onNext", "com.coolapk.market.network.Result") {
                    val pictureWatermarkPosition = OwnSP.ownSP.getString("pictureWatermarkPosition", "-1")
                    if (pictureWatermarkPosition != "-1") {
                        instance.callMethod("uploadSetting", "picture_watermark_position", pictureWatermarkPosition, 1)
                        OwnSP.remove("pictureWatermarkPosition")
                    }
                }
        //回复临时关闭图片水印
        XposedHelpers.findClass("com.coolapk.market.view.feed.ReplyActivity", CoolapkContext.classLoader)
                .hookAfterMethod("initView") {
                    val viewBuilding = it.thisObject.getObjectField("binding")!!
                    val contentView = (viewBuilding.getObjectField("contentView") as LinearLayout).getChildAt(4) as LinearLayout
                    contentView.addView(CheckBox(CoolapkContext.activity).apply {
                        text = "临时关闭水印"
                        setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                            val pictureWatermarkPosition = CoolapkSP.coolapkSP.getString("picture_watermark_position", "0")
                            if (b) {
                                if (pictureWatermarkPosition != "0") {
                                    OwnSP.set("pictureWatermarkPosition", pictureWatermarkPosition!!)
                                    instance.callMethod("uploadSetting", "picture_watermark_position", "0", 1)
                                }
                            } else {
                                if (pictureWatermarkPosition != "0") {
                                    instance.callMethod("uploadSetting", "picture_watermark_position", OwnSP.ownSP.getString("pictureWatermarkPosition", "0"), 1)
                                    OwnSP.remove("pictureWatermarkPosition")
                                }
                            }
                        }
                    })
                }
        XposedHelpers.findClass("com.coolapk.market.view.feed.ReplyActivity\$doPost$1", CoolapkContext.classLoader)
                .hookBeforeMethod("onNext", "com.coolapk.market.network.Result") {
                    val pictureWatermarkPosition = OwnSP.ownSP.getString("pictureWatermarkPosition", "-1")
                    if (pictureWatermarkPosition != "-1") {
                        instance.callMethod("uploadSetting", "picture_watermark_position", pictureWatermarkPosition, 1)
                        OwnSP.remove("pictureWatermarkPosition")
                    }
                }
        //自定义水印
        if (OwnSP.ownSP.getBoolean("enableCustomWatermark", false)) {
            XposedHelpers.findClass("com.coolapk.market.model.ImageUploadOption", CoolapkContext.classLoader)
                    .hookBeforeMethod("create", String::class.java, String::class.java, String::class.java, Bundle::class.java) {
                        if (OwnSP.ownSP.getString("pictureWatermarkPosition", "-1") == "-1") {
                            val url = it.args[0] as String //file:///storage/emulated/0/Android/data/com.coolapk.market/cache/image_cache/xxxxxxxxxxxxxxxxxxxxxx
                            LogUtil.d(url)
                            val file = File(url.substring(url.indexOf("file://") + 7))
                            LogUtil.d(file.absolutePath)
                            val coolFileUtilsClass = XposedHelpers.findClass("com.coolapk.market.util.CoolFileUtils", CoolapkContext.classLoader)
                            var compressFormat = Bitmap.CompressFormat.JPEG
                            when (XposedHelpers.callStaticMethod(coolFileUtilsClass, "getImageFileType", file.absolutePath) as String) {
                                "jpg" -> compressFormat = Bitmap.CompressFormat.JPEG
                                "jpeg" -> compressFormat = Bitmap.CompressFormat.JPEG
                                "png" -> compressFormat = Bitmap.CompressFormat.PNG
                            }
                            val bitmap = doWaterMark(file).outputImage
                            val fileOutputStream = FileOutputStream(file)
                            bitmap.compress(compressFormat, 100, fileOutputStream)
                            fileOutputStream.flush()
                            fileOutputStream.close()
                        }
                    }
        }
    }
}

fun <T> doWaterMark(objects: T): Watermark {
    var watermarkBuilder: WatermarkBuilder? =null
    watermarkBuilder = if (objects is File){
        WatermarkBuilder.create(CoolapkContext.context,BitmapFactory.decodeFile(objects.absolutePath) as Bitmap)
    }else{
        WatermarkBuilder.create(CoolapkContext.context,objects as ImageView)
    }
    return if (OwnSP.ownSP.getBoolean("enablePictureWatermark", false)) {
        if (OwnSP.ownSP.getString("waterMarkPicturePath", "")==""){
            LogUtil.toast("水印图片不可为空")
            watermarkBuilder.watermark
        }else{
            watermarkBuilder.loadWatermarkImage(WatermarkImage(BitmapFactory.decodeFile(OwnSP.ownSP.getString("waterMarkPicturePath", ""))).apply {
                setPositionX(OwnSP.ownSP.getString("waterMarkPositionX", "0")!!.toDouble())
                setPositionY(OwnSP.ownSP.getString("waterMarkPositionY", "0")!!.toDouble())
                setRotation(OwnSP.ownSP.getString("waterMarkRotation", "-30")!!.toDouble())
                setImageAlpha(OwnSP.ownSP.getString("waterMarkAlpha", "50")!!.toInt())
                size = OwnSP.ownSP.getString("waterMarkPictureSize", "0.2")!!.toDouble()
            })
                    .setTileMode(OwnSP.ownSP.getBoolean("enableTileWatermark", true))
                    .watermark
        }
    } else {
        watermarkBuilder.loadWatermarkText(WatermarkText(OwnSP.ownSP.getString("waterMarkText", if (CoolapkContext.loginSession.callMethod("isLogin") as Boolean) (CoolapkContext.loginSession.callMethod("getUserName") as String) else "水印文字")).apply {
                    setPositionX(OwnSP.ownSP.getString("waterMarkPositionX", "0")!!.toDouble())
                    setPositionY(OwnSP.ownSP.getString("waterMarkPositionY", "0")!!.toDouble())
                    setRotation(OwnSP.ownSP.getString("waterMarkRotation", "-30")!!.toDouble())
                    textAlpha = OwnSP.ownSP.getString("waterMarkAlpha", "50")!!.toInt()
                    textSize = OwnSP.ownSP.getString("waterMarkTextSize", "20")!!.toDouble()
                    textColor = Color.parseColor(OwnSP.ownSP.getString("waterMarkTextColor", "#000000"))
                })
                .setTileMode(OwnSP.ownSP.getBoolean("enableTileWatermark", true))
                .watermark
    }
}