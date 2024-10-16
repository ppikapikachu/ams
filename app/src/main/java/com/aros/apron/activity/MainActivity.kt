package com.aros.apron.activity

//import com.aros.apron.BuildConfig
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import com.aros.apron.base.BaseActivity
import com.aros.apron.callback.MGS28181Listener
import com.aros.apron.databinding.ActivityMainBinding
import com.aros.apron.entity.MQMessage
import com.aros.apron.entity.Movement
import com.aros.apron.manager.*
import com.aros.apron.manager.FlightManager.*
import com.aros.apron.tools.*
import com.aros.apron.util.CameraControllerUtil
import com.aros.apron.util.FileUtil
import com.aros.apron.util.VideoStreamThread
import com.google.gson.Gson
import com.gosuncn.lib28181agent.GS28181SDKManager
import com.gosuncn.lib28181agent.Types
import dji.sdk.keyvalue.key.DJIKey
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.key.ProductKey
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.payload.WidgetType
import dji.sdk.keyvalue.value.payload.WidgetValue
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.callback.CommonCallbacks.CompletionCallback
import dji.v5.common.error.IDJIError
import dji.v5.manager.KeyManager
import dji.v5.manager.aircraft.payload.PayloadCenter
import dji.v5.manager.aircraft.payload.PayloadIndexType
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.interfaces.ICameraStreamManager
import dji.v5.manager.interfaces.ICameraStreamManager.ReceiveStreamListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.aruco.Aruco
import org.opencv.aruco.Dictionary


class MainActivity : BaseActivity() {

    var cameraManager = MediaDataCenter.getInstance().cameraStreamManager
    private var mainBinding: ActivityMainBinding? = null
    private var startArucoType = 0  //1执行机库二维码识别  2执行备降点二维码识别
    private var dictionary: Dictionary? = null
    private var mqMessage: MQMessage? = null

    //    加的
    private var btn_right: ImageButton? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var drawer_right: LinearLayout? = null

    //加的,帧数据相关
    private var manager = GS28181SDKManager.getInstance()
    private var startTime: Long = 0
    private var endTime: Long = 0
    private val continueSendVideo = false
    private val mimeType = 4

    //    帧数据监听
    private var sendVideoStreamListener: ReceiveStreamListener? = null
    private var sendVideoWithARInfoListener: ReceiveStreamListener? = null
    private var sendVideoWithARInfoXListener: ReceiveStreamListener? = null
    private var sendVideoWithARInfoToLocalListener: ReceiveStreamListener? = null

    //    发流相关
    private var videoStreamThread: VideoStreamThread? = null
    private var spinner: Spinner? = null
    private var sendVideoStreamBtn: Button? = null
    private var vedioPos = -1 //选项卡的发流方式

    //相机索引
    private var componentIndexType = ComponentIndexType.LEFT_OR_MAIN

    private var editYaw: EditText? = null
    private var editPitch: EditText? = null
    private var compensateBtn: Button? = null
    private var isCompensate = false //是否开启补偿

    //    工具类
    var fileUtil: FileUtil? = FileUtil.getInstance()
    private val cameraControllerUtil: CameraControllerUtil = CameraControllerUtil.getInstance()
    private var llTouch: RelativeLayout? = null//屏幕点击
    var delay: Long = 0

    var myLayoutActivity: MyLayoutActivity? = null
    override fun useEventBus(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback)
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
        dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding!!.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        needConnect()
        initDJIManager()
//        加的，暂时先注释，在MyL中调用
        initCameraStream()

        initView()

        myLayoutActivity = MyLayoutActivity(this);
        myLayoutActivity!!.create()//初始化，调用方法
    }

    var width = 0
    var height = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 在这里判断一下如果是按下操作就获取坐标然后执行方法
        val x = event.x.toInt()
        val y = event.y.toInt()
        Log.i(TAG, "点击屏幕的位置：" + x + "--" + y + "屏幕长高" + width + "--" + height)
        MGS28181Listener().onZoomControl(Types.ZOOM_IN_CTRL, height, width, 98, 99, x, y)
        return super.onTouchEvent(event)
    }

    fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> onTouchEvent(event)
            else -> {}
        }
        /**
         * 注意返回值
         * true：view继续响应Touch操作；
         * false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
         */
        return true
    }

    fun initSpinner() {
        spinner!!.selectedItem
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                id: Long
            ) {
                val re = adapterView.getItemAtPosition(pos).toString()
                vedioPos = pos
                Log.i(TAG, "选择下标为：$pos---方式为：$re")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun initView() {
        if (PreferenceUtils.getInstance().isDebugMode) {
            mainBinding?.layoutDebugMode?.visibility = View.VISIBLE
        } else {
            mainBinding?.layoutDebugMode?.visibility = View.GONE

        }

        mainBinding?.startAlter?.setOnClickListener {
            AlternateLandingManager.getInstance().startTaskProcess(null)
//            startArucoType=1
//            DroneHelper.getInstance().setGimbalPitchDegree()

//            MediaDataCenter.getInstance().mediaManager.setMediaFileXMPCustomInfo(
//                "wangmingge",
//                object :
//                    CommonCallbacks.CompletionCallback {
//                    override fun onSuccess() {
//                        Log.e("写入成功","----")
//                    }
//
//                    override fun onFailure(error: IDJIError) {
//                        Log.e("写入失败", Gson().toJson(error))
//                    }
//                })

        }

        mainBinding?.startMission?.setOnClickListener {
//            val customExpandNameSettings = CustomExpandNameSettings()
//            customExpandNameSettings.encodingType = EnCodingType.UTF8
//            customExpandNameSettings.forceCreateFolder = false
//            customExpandNameSettings.relativePosition = RelativePosition.POSITION_END
//            customExpandNameSettings.priority = 0
//            customExpandNameSettings.customContent =
//                "flightId111" + PreferenceUtils.getInstance().flightId
//            KeyManager.getInstance().setValue(
//                DJIKey.create(CameraKey.KeyCustomExpandFileNameSettings),
//                customExpandNameSettings,
//                object : CompletionCallback {
//                    override fun onSuccess() {
//                        LogUtil.log(TAG, "设置文件后缀success")
//                        KeyManager.getInstance().performAction<EmptyMsg>(
//                            DJIKey.create<EmptyMsg, EmptyMsg>(CameraKey.KeyStartShootPhoto),
//                            object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg?> {
//                                override fun onSuccess(emptyMsg: EmptyMsg?) {
//                                    Log.e("拍照成功",  "拍照成功:") }
//                                override fun onFailure(error: IDJIError) {
//                                    Log.e("拍照失败",  "拍照失败:" + Gson().toJson(error)) }
//
//                            })
//                    }
//
//                    override fun onFailure(idjiError: IDJIError) {
//                        LogUtil.log(TAG, "设置自定义文件后缀失败：" + Gson().toJson(idjiError))
//                    }
//                })

//            KeyManager.getInstance().setValue<CameraMode>(
//                DJIKey.create<CameraMode>(CameraKey.KeyCameraMode),
//                CameraMode.PHOTO_NORMAL,
//                object : CompletionCallback {
//                    override fun onSuccess() {
//                        Log.e("切换模式", "success")
//                        KeyManager.getInstance().performAction<EmptyMsg>(
//                            DJIKey.create<EmptyMsg, EmptyMsg>(CameraKey.KeyStartShootPhoto),
//                            object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg?> {
//                                override fun onSuccess(emptyMsg: EmptyMsg?) {
//                                    Log.e("拍照成功",  "拍照成功:") }
//                                override fun onFailure(error: IDJIError) {
//                                    Log.e("拍照失败",  "拍照失败:" + Gson().toJson(error)) }
//
//                            })
//                    }
//
//
//                    override fun onFailure(error: IDJIError) {
//                        Log.e("切换模式失败", Gson().toJson(error))
//                    }
//
//
//                })

//            val message=MQMessage ()
//           message.secret_key="admin123"
//           message.kmz_url="http://162.14.115.91:9000/test/kmz/测试1·.kmz"
//           message.access_key="admin"
//           message.flight_name="测试1·2024_04_02_09_18_45"
//           message.msg_type=60003
//           message.upload_url="http://162.14.115.91:9000/test/88AEDD00D02A/54403fbf-f964-4585-9dff-f8dfd7b4b61d"
//           message.flightId="flightId"
////            //楼上350
//           message.rtmp_push_url="rtmp://47.97.39.183/live/1581F5FJB229Q00A003W"
////            //楼下小机库2
//           message.rtmp_push_url="rtmp://47.97.39.183/live/1581F5FJB229Q00A003A"
//           message.isGuidingFlight=0
//            if (Movement.getInstance().goHomeState != 1 && Movement.getInstance().goHomeState != 2) {
//                    // 1.缓存推流地址,minIO配置
//                    PreferenceUtils.getInstance().setStreamAndMinIOConfig(message)
//                    // 2.收到60003直接回复
//                    StreamManager.getInstance().sendReply2Server(mqttAndroidClient, message)
//                    // 3.开启推流
//                    StreamManager.getInstance().startLive(mqttAndroidClient, message)
//                    // 4.关闭避障
//                    PerceptionManager.getInstance().setPerceptionEnable(false)
//                    MissionManager.getInstance().startTaskProcess(mqttAndroidClient, message)
//            } else {
//                LogUtil.log(TAG, "返航模式,无法上传航线")
//            }
//
        }

        mainBinding?.btnLock?.setOnClickListener {
            val widgetValue = WidgetValue()
            widgetValue.value = 1
            widgetValue.index = 0
            widgetValue.type = WidgetType.SWITCH
            val payloadManagerMap = PayloadCenter.getInstance().payloadManager
            payloadManagerMap[PayloadIndexType.RIGHT]!!.setWidgetValue(
                widgetValue,
                object : CompletionCallback {
                    override fun onSuccess() {
                        ToastUtil.showToast("setWidgetValue success")
                    }

                    override fun onFailure(idjiError: IDJIError) {
                        Log.e(TAG, "错误:${Gson().toJson(idjiError)}")
                    }
                })
        }
        mainBinding?.btnUnlock?.setOnClickListener {
            val widgetValue = WidgetValue()
            widgetValue.value = 0
            widgetValue.index = 0
            widgetValue.type = WidgetType.SWITCH
            val payloadManagerMap = PayloadCenter.getInstance().payloadManager
            payloadManagerMap[PayloadIndexType.RIGHT]!!.setWidgetValue(
                widgetValue,
                object : CompletionCallback {
                    override fun onSuccess() {
                        ToastUtil.showToast("setWidgetValue success")
                    }

                    override fun onFailure(idjiError: IDJIError) {
                        Log.e(TAG, "错误:${Gson().toJson(idjiError)}")
                    }
                })
        }
        mainBinding?.btnRoll?.setOnClickListener {
            val widgetValue = WidgetValue()
            widgetValue.value = 1
            widgetValue.index = 1
            widgetValue.type = WidgetType.BUTTON
            val payloadManagerMap = PayloadCenter.getInstance().payloadManager
            payloadManagerMap[PayloadIndexType.RIGHT]!!.setWidgetValue(
                widgetValue,
                object : CompletionCallback {
                    override fun onSuccess() {
                        ToastUtil.showToast("setWidgetValue success")
                    }

                    override fun onFailure(idjiError: IDJIError) {
                        Log.e(TAG, "错误:${Gson().toJson(idjiError)}")
                    }
                })

        }
        mainBinding?.btnRollall?.setOnClickListener {
            val widgetValue = WidgetValue()
            widgetValue.value = 1
            widgetValue.index = 2
            widgetValue.type = WidgetType.BUTTON
            val payloadManager = PayloadCenter.getInstance().payloadManager[PayloadIndexType.RIGHT]
            payloadManager?.setWidgetValue(
                widgetValue,
                object : CompletionCallback {
                    override fun onSuccess() {
                        ToastUtil.showToast("setWidgetValue success")
                    }

                    override fun onFailure(idjiError: IDJIError) {
                        Log.e(TAG, "错误:${Gson().toJson(idjiError)}")
                    }
                })


        }
//        mainBinding?.sendVideoStreamBtn?.setOnClickListener {
//
//        }
    }

    private val handler: Handler = Handler(Looper.getMainLooper())

    private fun initDJIManager() {
        val isFlightControllerConnect =
            KeyManager.getInstance().getValue(DJIKey.create(FlightControllerKey.KeyConnection))
        //加的，设置飞控状态
        if (isFlightControllerConnect != null) {
            Movement.getInstance().isFlightController = isFlightControllerConnect
        }else{
            Movement.getInstance().isFlightController = false
        }

        if (isFlightControllerConnect == null || !isFlightControllerConnect) {
            handler.postDelayed({
                initDJIManager()
            }, 1000)
        } else {
            RTKManager.getInstance().initRTKInfo()
            StreamManager.getInstance().initStreamManager(mqttAndroidClient)
            FlightManager.getInstance().initFlightInfo(mqttAndroidClient)
            MissionManager.getInstance().initMissionManager(mqttAndroidClient)
            BatteryManager.getInstance().initBatteryInfo(mqttAndroidClient)
            MediaManager.init(mqttAndroidClient)
            LEDsSettingsManager.getInstance().initLEDsInfo()
            AlternateLandingManager.getInstance().initAlterLandingInfo(mqttAndroidClient)
            WayLineExecutingInterruptManager.getInstance()
                .initWayLineExecutingInterruptInfo(mqttAndroidClient)
            CameraManager.getInstance().initCameraInfo(mqttAndroidClient)
            StickManager.getInstance().initStickInfo(mqttAndroidClient)
            GimbalManager.getInstance().initGimbalInfo()

            //这里修改推流逻辑
            Handler().postDelayed(Runnable {
                StreamManager.getInstance()
                    .startLiveWithCustom()
            }, 5000)
            val productType =
                KeyManager.getInstance().getValue(KeyTools.createKey(ProductKey.KeyProductType))
            LogUtil.log(TAG, "设备类型:" + productType!!.name)
            ApronArucoDetect.getInstance().productType = productType!!.name
        }
    }


    /**
     * 加参数componentIndexType
     */
    private fun initCameraStream() {
        //            添加监听可用镜头
        MediaDataCenter.getInstance().cameraStreamManager.addAvailableCameraUpdatedListener { availableCameraList ->
            Movement.getInstance().currentView = availableCameraList
            var componentIndexType =ComponentIndexType.FPV
            if (availableCameraList.contains(ComponentIndexType.LEFT_OR_MAIN))//有相机就用相机
                componentIndexType = ComponentIndexType.LEFT_OR_MAIN
            LogUtil.log(TAG,"当前可用镜头视角"+availableCameraList)
            mainBinding?.svCameraStream?.holder?.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {}
                override fun surfaceChanged(
                    holder: SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    cameraManager.putCameraStreamSurface(
                        /**
                         * 这里和下面改了，原本是固定RIGHT OR MAIN
                         */
                        componentIndexType,
                        holder.surface,
                        width,
                        height,
                        ICameraStreamManager.ScaleType.FIX_XY
                    )
                }

                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    cameraManager.removeCameraStreamSurface(holder.surface)
                }
            })
        }

        cameraManager.addFrameListener(
            ComponentIndexType.LEFT_OR_MAIN,
            ICameraStreamManager.FrameFormat.YUV420_888
        ) { frameData, _, _, width, height, _ ->
            when (startArucoType) {
                1 ->
                    ApronArucoDetect.getInstance()?.detectArucoTags(
                        height,
                        width,
                        frameData,
                        dictionary,
                    )

                2 ->
                    AlternateArucoDetect.getInstance()?.detectArucoTags(
                        height,
                        width,
                        frameData,
                        dictionary,
                    )
            }
        }
//        加的
//        sendVideoStreamListener =
//            ReceiveStreamListener { data, offset, length, info ->
//                if (continueSendVideo && data != null) {
//                    startTime = System.currentTimeMillis()
//                    Log.i(TAG, startTime.toString() + "-----发送的时间--开始")
//                    if (info.mimeType == ICameraStreamManager.MimeType.H264) {
//                        val re: Int = manager.sendVideoStream(
//                            System.currentTimeMillis(),
//                            if (info.isKeyFrame) 1 else FileUtil.getFrameType(data),
//                            data
//                        )
//                        Log.i(TAG, "发送视频流sendVideoStream:$re")
//                    } else {
//                        val re: Int = manager.sendVideoStreamH265(
//                            System.currentTimeMillis(),
//                            if (info.isKeyFrame) 1 else FileUtil.getFrameType(data),
//                            data
//                        )
//                        Log.i(TAG, "发送视频流sendVideoStreamH265:$re")
//                    }
//                    endTime = System.currentTimeMillis()
//                    Log.i(TAG, "发送的时间--结束-----$endTime")
//                    //                    推太快就等待直到满足33ms推一帧，即30的帧率
//                    val delay: Long = startTime - endTime
//                    if (delay < 40) {
//                        Log.i(TAG, "推太快，用时-----$delay")
//                        try {
//                            //将上一次的推流耗时近似当做下一次的耗时，也就是两帧之间是（40-delay）+下次网络推流用时delay，即保证每两帧间隔40ms
//                            Thread.sleep(40L - delay)
//                        } catch (e: InterruptedException) {
//                            e.printStackTrace()
//                        }
//                    }
//                } else {
//                    if (continueSendVideo) {
//                        Log.i(TAG, "发送视频流sendVideoStream:" + "已开启")
//                    } else {
//                        Log.i(TAG, "发送视频流sendVideoStream:" + "数据为空")
//                    }
//                }
//                if (!continueSendVideo) {
//                    cameraManager.removeReceiveStreamListener(sendVideoStreamListener!!)
//                }
//            }

    }

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            if (status == SUCCESS) {
//                LogUtil.log(TAG, "Version Name=" + BuildConfig.VERSION_NAME)
            } else {
                super.onManagerConnected(status)
            }
        }
    }

//    接收总线事件,主线程接收事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(message: String?) {
        when (message) {
            FLAG_START_DETECT_ARUCO_APRON ->
                KeyManager.getInstance().performAction<EmptyMsg>(
//                    停止飞行器自主降落
                    KeyTools.createKey<EmptyMsg, EmptyMsg>(FlightControllerKey.KeyStopAutoLanding),
                    object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg?> {
                        override fun onSuccess(emptyMsg: EmptyMsg?) {
                            LogUtil.log(TAG, "取消降落,识别机库二维码")
                            if (startArucoType == 1) {
                                return
                            }
                            startArucoType = 1
                            ApronArucoDetect.getInstance().setDetectedBigMarkers()
                            DroneHelper.getInstance().setGimbalPitchDegree()
                            //每次触发识别二维码时，为避免获取控制权失败,使多次获取控制权
                            DroneHelper.getInstance().isVirtualStickEnable = false
                            DroneHelper.getInstance().setVerticalModeToVelocity()
                        }

                        override fun onFailure(error: IDJIError) {
                            if (startArucoType == 1) {
                                return
                            }
                            startArucoType = 1
                            LogUtil.log(TAG, "取消降落,识别机库二维码失败:" + Gson().toJson(error))
                            ApronArucoDetect.getInstance().setDetectedBigMarkers()
                            DroneHelper.getInstance().setGimbalPitchDegree()
                            //每次触发识别二维码时，为避免获取控制权失败,使多次获取控制权
                            DroneHelper.getInstance().isVirtualStickEnable = false
                            DroneHelper.getInstance().setVerticalModeToVelocity()
                        }
                    })

            FLAG_START_DETECT_ARUCO_ALTERNATE ->
                KeyManager.getInstance().performAction<EmptyMsg>(
                    KeyTools.createKey<EmptyMsg, EmptyMsg>(FlightControllerKey.KeyStopAutoLanding),
                    object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg?> {
                        override fun onSuccess(emptyMsg: EmptyMsg?) {
                            LogUtil.log(TAG, "取消降落,识别备降点二维码")
                            if (startArucoType == 2) {
                                return
                            }
                            startArucoType = 2
                            DroneHelper.getInstance().setGimbalPitchDegree()
                            //每次触发识别二维码时，为避免获取控制权失败,使多次获取控制权
                            DroneHelper.getInstance().isVirtualStickEnable = false
                            DroneHelper.getInstance().setVerticalModeToVelocity()
                        }

                        override fun onFailure(error: IDJIError) {
                            if (startArucoType == 2) {
                                return
                            }
                            startArucoType = 2
                            LogUtil.log(
                                TAG,
                                "取消降落,识别备降点二维码失败:" + Gson().toJson(error)
                            )
                            DroneHelper.getInstance().setGimbalPitchDegree()
                            //每次触发识别二维码时，为避免获取控制权失败,使多次获取控制权
                            DroneHelper.getInstance().isVirtualStickEnable = false
                            DroneHelper.getInstance().setVerticalModeToVelocity()
                        }
                    })

            FLAG_DOWN_LAND ->
                KeyManager.getInstance().performAction<EmptyMsg>(
                    KeyTools.createKey<EmptyMsg, EmptyMsg>(FlightControllerKey.KeyStartAutoLanding),
                    object : CommonCallbacks.CompletionCallbackWithParam<EmptyMsg?> {
                        override fun onSuccess(emptyMsg: EmptyMsg?) {
                            startArucoType = 0
                        }

                        override fun onFailure(error: IDJIError) {
                            LogUtil.log(TAG, "自动降落调用失败${error.description()}")
                        }
                    })
//            禁用二维码识别
            FLAG_STOP_ARUCO ->
                startArucoType = 0

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        myLayoutActivity?.onDestroy()
    }
}