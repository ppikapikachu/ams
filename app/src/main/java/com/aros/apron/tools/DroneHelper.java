package com.aros.apron.tools;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import dji.sdk.keyvalue.key.FlightControllerKey;
import dji.sdk.keyvalue.key.GimbalKey;
import dji.sdk.keyvalue.key.KeyTools;
import dji.sdk.keyvalue.value.common.EmptyMsg;
import dji.sdk.keyvalue.value.flightcontroller.FlightCoordinateSystem;
import dji.sdk.keyvalue.value.flightcontroller.GoHomeState;
import dji.sdk.keyvalue.value.flightcontroller.RemoteControllerFlightMode;
import dji.sdk.keyvalue.value.flightcontroller.RollPitchControlMode;
import dji.sdk.keyvalue.value.flightcontroller.VerticalControlMode;
import dji.sdk.keyvalue.value.flightcontroller.VirtualStickFlightControlParam;
import dji.sdk.keyvalue.value.flightcontroller.YawControlMode;
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotation;
import dji.sdk.keyvalue.value.gimbal.GimbalAngleRotationMode;
import dji.v5.common.callback.CommonCallbacks;
import dji.v5.common.error.IDJIError;
import dji.v5.manager.KeyManager;
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager;


public class DroneHelper {


    private String TAG = "DroneHelper";
    private static final int GIMBAL_FORWARD = 0;
    private static final int GIMBAL_DOWN = 1;
    VirtualStickFlightControlParam virtualStickFlightControlParam;


    private DroneHelper() {
    }

    private static class DroneHelperHolder {
        private static final DroneHelper INSTANCE = new DroneHelper();
    }

    public static DroneHelper getInstance() {
        return DroneHelper.DroneHelperHolder.INSTANCE;
    }


    public void exitVirtualStickMode() {
        VirtualStickManager.getInstance().disableVirtualStick(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onSuccess() {
                LogUtil.log(TAG, "控制权已取消");
            }

            @Override
            public void onFailure(@NonNull IDJIError error) {
                LogUtil.log(TAG, "取消控制权失败:" + error.toString());
            }

        });
    }

    private int enableVirtualStickTimes;
    private boolean virtualStickEnable;

    public boolean isVirtualStickEnable() {
        return virtualStickEnable;
    }

    public void setVirtualStickEnable(boolean virtualStickEnable) {
        this.virtualStickEnable = virtualStickEnable;
    }

    public void setVerticalModeToVelocity() {

        RemoteControllerFlightMode remoteControllerFlightMode = KeyManager.getInstance().getValue(KeyTools.createKey(FlightControllerKey.KeyRemoteControllerFlightMode));
        if (remoteControllerFlightMode != null && remoteControllerFlightMode == RemoteControllerFlightMode.P) {
            virtualStickFlightControlParam = new VirtualStickFlightControlParam();
            virtualStickFlightControlParam.setVerticalControlMode(VerticalControlMode.VELOCITY);
            virtualStickFlightControlParam.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            virtualStickFlightControlParam.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            virtualStickFlightControlParam.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
            VirtualStickManager.getInstance().setVirtualStickAdvancedModeEnabled(true);
            VirtualStickManager.getInstance().enableVirtualStick(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onSuccess() {
                    LogUtil.log(TAG, "第" + enableVirtualStickTimes + "次获取控制权成功");
                    virtualStickEnable = true;
                    enableVirtualStickTimes = 0;

                }

                @Override
                public void onFailure(@NonNull IDJIError error) {
                    if (!virtualStickEnable) {
                        if (enableVirtualStickTimes < 5) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LogUtil.log(TAG, "第" + enableVirtualStickTimes + "次获取控制权失败" + new Gson().toJson(error));
                                    enableVirtualStickTimes++;
                                    setVerticalModeToVelocity();
                                }
                            }, 1000);
                        }
                    } else {
                        LogUtil.log(TAG, "视觉降落获取控制权失败" + new Gson().toJson(error));
                    }
                }
            });
        } else {
            LogUtil.log(TAG, "视觉降落控制权获取失败:不在P挡");
        }


    }


    public void setGimbalPitchDegree() {

        Boolean isConnect = KeyManager.getInstance().getValue(KeyTools.createKey(GimbalKey.
                KeyConnection, 0));
        if (isConnect!=null&&isConnect) {
            GimbalAngleRotation rotation = new GimbalAngleRotation();
            rotation.setMode(GimbalAngleRotationMode.ABSOLUTE_ANGLE);
            rotation.setYaw(0.0);
            rotation.setRoll(0.0);
            rotation.setPitch(-90.0);
            KeyManager.getInstance().performAction(KeyTools.createKey(GimbalKey.KeyRotateByAngle, 0), rotation, new CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>() {
                        @Override
                        public void onSuccess(EmptyMsg emptyMsg) {
                            LogUtil.log(TAG, "云台朝下");
                        }

                        @Override
                        public void onFailure(@NonNull IDJIError error) {
                            LogUtil.log(TAG, "fail:" + error.toString());
                        }
                    }
            );

        } else {
            LogUtil.log(TAG, "云台未连接");
        }
    }


    public void moveVxVyYawrateHeight(double mPitch, double mRoll, double mYaw, double mThrottle) {
//        设置了pitch、roll和Vertical为速度模式，yaw角速度模式
        virtualStickFlightControlParam.setPitch(mPitch);//左右
        virtualStickFlightControlParam.setRoll(mRoll);//前后
        virtualStickFlightControlParam.setYaw(mYaw);
        virtualStickFlightControlParam.setVerticalThrottle(mThrottle);//上下
        sendMovementCommand(virtualStickFlightControlParam);
    }

    public void sendMovementCommand(VirtualStickFlightControlParam param) {
        VirtualStickManager.getInstance().sendVirtualStickAdvancedParam(param);
    }

    //设置备降点
    public void setAlternateLandingPoint() {
        Boolean isConnect = KeyManager.getInstance().getValue(KeyTools.createKey(FlightControllerKey.KeyConnection));
        if (isConnect != null && isConnect) {
            GoHomeState goHomeState = KeyManager.getInstance().getValue(KeyTools.createKey(FlightControllerKey.KeyGoHomeStatus));
            if (goHomeState != null) {
                if (goHomeState.value() == 0) {
                    KeyManager.getInstance().performAction(KeyTools.createKey(FlightControllerKey.KeyStopGoHome), new CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>() {
                        @Override
                        public void onSuccess(EmptyMsg emptyMsg) {
                        }

                        @Override
                        public void onFailure(@NonNull IDJIError error) {
                        }
                    });
                }
            }
        } else {
            LogUtil.log(TAG, "设置备降点失败,无人机未连接");
        }

    }
}
