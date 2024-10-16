package com.aros.apron.entity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dji.sdk.keyvalue.value.camera.CameraType;
import dji.sdk.keyvalue.value.camera.VideoResolutionFrameRate;
import dji.sdk.keyvalue.value.common.ComponentIndexType;
import dji.sdk.keyvalue.value.common.DoubleMinMax;

public class Movement {




    private static class MovementHolder {
        private static final Movement INSTANCE = new Movement();
    }

    private Movement() {
    }

    public static final Movement getInstance() {
        return MovementHolder.INSTANCE;
    }

    private int msg_type = 60013;
    private String homepointLat;//返航点经纬度
    private String homepointLong;

    private int distance;//距离航点
    private String horizontalSpeed;//水平速度
    private String verticalSpeed;//垂直速度
    private int windSpeed;//风速
    private int ultrasonicHeight;//超声波测高，只在飞行高度5m时奏效,单位/分米
    private boolean rtkSign;//rtk标志
    private int satelliteNumber;//卫星数量
    private String GPSSignalLevel;//GPS信号等级
    private boolean levelObstacleAvoidance;//水平避障
    private int remoteControlSignal;//遥控器信号
    private int pictureBiographySignal;//图传信号
    private int electricityInfoA;//A电量信息
    private int voltageInfoA;//A电压信息
    private double batteryTemperatureA;//A电池温度
    private int electricityInfoB;//B电量信息
    private int voltageInfoB;//B电压信息
    private double batteryTemperatureB;//B电池温度
    /**
     * 改了，原本是0云台1FPV
     */
    private List<ComponentIndexType> currentView ;//视角
    private String planeMessage;//飞机提示信息
    private String warningMessage;//飞机警告信息
    private int angleYaw;//飞机飞行机头角度
    private String flightPathName;//航线名称
    private String planeMode ;//飞机模式

    private int cameraMode;//相机模式(拍照/录像) 0拍照 1录像
    private int isShootingPhoto;//是否正在拍照 1正在拍照 0未在拍照
    private int isRecording;//是否正在录像 1正在录像 0未录像
    private int recordingTime;//录像时间 单位s
    private int cameraVideoStreamSource;//当前视频源 1广角 2变焦 3红外
    private double cameraZoomRatios=0;//镜头变焦倍数
    private double thermalZoomRatios;//红外镜头变焦倍数
    private boolean continuous;//变焦档位是否连续 (true表示gears最小值-最大值都可使用，false表示只支持数组内关键档位)
    private int[] gears;//变焦倍率范围的关键档位
    private int isVirtualStickEnable;//是否获取控制权 0未获取控制权  1已获取控制权
    private boolean isDistanceLimitEnabled;//是否启用限远
    private int thermalDisplayMode;//红外模式 1仅红外  2红外分屏
    private String flightId;//航线id
    private String waypointMissionExecuteState;//航线执行状态
    private boolean airlineFlight;//是否航线飞行
    private int flightPathStatus;//航线状态航线状态 （0 航线飞行中 1 航线暂停中 2航线已终止）
    private String waylineExecutingInterruptReason;//航线暂停原因
    private int goHomeState;//返航执行状态  0未触发返航 1返航中 2返航下降中 3返航完成
    private long timestamp;//时间戳
    private int remainFlightTime;//剩余飞行时间
    private int returnHomePower;//返航电量所需百分比
    private int landingPower;//降落电量所需百分比
    private String missionName;//当前正在执行的航线名
    private int currentWaypointIndex;//当前航点下标
    private String currentLongitude="0";//当前经度
    private String currentLatitude="0";//当前纬度
    private float currentAltitude = -1.0F;//当前高度
    private double flyingHeight;//飞行高度
    private String roll;//机身姿态
    private String pitch;
    private String yaw="0";
    private String gimbalRoll;//云台角度
    private String gimbalPitch;
    private String gimbalYaw;
    private boolean planeWing;//当前飞机的桨叶是否转动
    private String aircraftTotalFlightDistance;//总体飞行距离，单位：米。飞行器断电后不会清零。
    private String aircraftTotalFlightTimes;//总体飞行次数，飞行器断电后不会清零。
    private String aircraftTotalFlightDuration;//总体飞行时长，单位：秒。飞行器断电后不会清零。
    //    加的
    private boolean isFlightController = false;//飞控是否连接
    private Boolean isCameraConnection = false; //相机是否连接
    private List<ComponentIndexType> availableCameraList = null;
    private double focalLenght = 0.0;//焦距
    private CameraType curCameraType ;//当前镜头类型
//    三个值分别为传感器高宽和广角焦距
    private Map<CameraType,float[]> cameraTypeParameter = new HashMap<CameraType,float[]>(){
        {put(CameraType.M3T, new float[]{4.8f, 6.4f,4.4f});put(CameraType.ZENMUSE_H20T,new float[]{5.56f, 7.41f,4.5f});
        put(CameraType.ZENMUSE_H30T,new float[]{5.539f,7.386f,6.72f});}
    };

    private double minFocalLenght = 0.0;//最小焦距
    private DoubleMinMax gimbalYawRange = null;
    private DoubleMinMax gimbalPitchRange = null;
    private List<VideoResolutionFrameRate>  KeyVideoResolutionFrameRateRange = null;
    private int width;
    private int height;

    private String alternatePointLat;
    private String alternatePointLon;
    //    加的
    private String devieGBCode = null;//移动设备国际编码

    private float angleH = -1.0F;//FOV高
    private float angleV = -1.0F;//FOV宽
    private float countYaw;
    private float altitude=0.0F;//RTK模式下的海拔高度

    public float getAltitude() {
        return altitude;
    }

    public void setAltitude(float altitude) {
        this.altitude = altitude;
    }

    public float getCountYaw() {
        return countYaw;
    }

    public void setCountYaw(float countYaw) {
        this.countYaw = countYaw;
    }



    public float getAngleH() {
        return angleH;
    }

    public void setAngleH(float angleH) {
        this.angleH = angleH;
    }

    public float getAngleV() {
        return angleV;
    }

    public void setAngleV(float angleV) {
        this.angleV = angleV;
    }

    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    public void setCurrentWaypointIndex(int currentWaypointIndex) {
        this.currentWaypointIndex = currentWaypointIndex;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getGPSSignalLevel() {
        return GPSSignalLevel;
    }

    public void setGPSSignalLevel(String GPSSignalLevel) {
        this.GPSSignalLevel = GPSSignalLevel;
    }

    public double getBatteryTemperatureA() {
        return batteryTemperatureA;
    }

    public void setBatteryTemperatureA(double batteryTemperatureA) {
        this.batteryTemperatureA = batteryTemperatureA;
    }

    public double getBatteryTemperatureB() {
        return batteryTemperatureB;
    }

    public void setBatteryTemperatureB(double batteryTemperatureB) {
        this.batteryTemperatureB = batteryTemperatureB;
    }

    public String getAircraftTotalFlightDistance() {
        return aircraftTotalFlightDistance;
    }

    public void setAircraftTotalFlightDistance(String aircraftTotalFlightDistance) {
        this.aircraftTotalFlightDistance = aircraftTotalFlightDistance;
    }

    public String getAircraftTotalFlightTimes() {
        return aircraftTotalFlightTimes;
    }

    public void setAircraftTotalFlightTimes(String aircraftTotalFlightTimes) {
        this.aircraftTotalFlightTimes = aircraftTotalFlightTimes;
    }

    public String getAircraftTotalFlightDuration() {
        return aircraftTotalFlightDuration;
    }

    public void setAircraftTotalFlightDuration(String aircraftTotalFlightDuration) {
        this.aircraftTotalFlightDuration = aircraftTotalFlightDuration;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getFlyingHeight() {
        return flyingHeight;
    }

    public void setFlyingHeight(double flyingHeight) {
        this.flyingHeight = flyingHeight;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getYaw() {
        return yaw;
    }

    public void setYaw(String yaw) {
        this.yaw = yaw;
    }

    public String getGimbalRoll() {
        return gimbalRoll;
    }

    public void setGimbalRoll(String gimbalRoll) {
        this.gimbalRoll = gimbalRoll;
    }

    public String getGimbalPitch() {
        return gimbalPitch;
    }

    public void setGimbalPitch(String gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }

    public String getGimbalYaw() {
        return gimbalYaw;
    }

    public void setGimbalYaw(String gimbalYaw) {
        this.gimbalYaw = gimbalYaw;
    }

    public boolean isPlaneWing() {
        return planeWing;
    }

    public void setPlaneWing(boolean planeWing) {
        this.planeWing = planeWing;
    }

    public int getRemainFlightTime() {
        return remainFlightTime;
    }

    public void setRemainFlightTime(int remainFlightTime) {
        this.remainFlightTime = remainFlightTime;
    }

    public int getReturnHomePower() {
        return returnHomePower;
    }

    public void setReturnHomePower(int returnHomePower) {
        this.returnHomePower = returnHomePower;
    }

    public int getLandingPower() {
        return landingPower;
    }

    public void setLandingPower(int landingPower) {
        this.landingPower = landingPower;
    }

    public int getIsVirtualStickEnable() {
        return isVirtualStickEnable;
    }

    public void setIsVirtualStickEnable(int isVirtualStickEnable) {
        this.isVirtualStickEnable = isVirtualStickEnable;
    }

    public boolean isAirlineFlight() {
        return airlineFlight;
    }

    public void setAirlineFlight(boolean airlineFlight) {
        this.airlineFlight = airlineFlight;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getGoHomeState() {
        return goHomeState;
    }

    public void setGoHomeState(int goHomeState) {
        this.goHomeState = goHomeState;
    }

    public String getWaylineExecutingInterruptReason() {
        return waylineExecutingInterruptReason;
    }

    public void setWaylineExecutingInterruptReason(String waylineExecutingInterruptReason) {
        this.waylineExecutingInterruptReason = waylineExecutingInterruptReason;
    }

    public String getWaypointMissionExecuteState() {
        return waypointMissionExecuteState;
    }

    public void setWaypointMissionExecuteState(String waypointMissionExecuteState) {
        this.waypointMissionExecuteState = waypointMissionExecuteState;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public int getThermalDisplayMode() {
        return thermalDisplayMode;
    }

    public void setThermalDisplayMode(int thermalDisplayMode) {
        this.thermalDisplayMode = thermalDisplayMode;
    }

    public boolean isDistanceLimitEnabled() {
        return isDistanceLimitEnabled;
    }

    public void setDistanceLimitEnabled(boolean distanceLimitEnabled) {
        isDistanceLimitEnabled = distanceLimitEnabled;
    }


    private boolean thermalContinuous;
    private int[] thermalGears;

    public boolean isThermalContinuous() {
        return thermalContinuous;
    }

    public void setThermalContinuous(boolean thermalContinuous) {
        this.thermalContinuous = thermalContinuous;
    }

    public int[] getThermalGears() {
        return thermalGears;
    }

    public void setThermalGears(int[] thermalGears) {
        this.thermalGears = thermalGears;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public int[] getGears() {
        return gears;
    }

    public void setGears(int[] gears) {
        this.gears = gears;
    }

    public double getCameraZoomRatios() {
        return cameraZoomRatios;
    }

    public void setCameraZoomRatios(double cameraZoomRatios) {
        this.cameraZoomRatios = cameraZoomRatios;
    }

    public double getThermalZoomRatios() {
        return thermalZoomRatios;
    }

    public void setThermalZoomRatios(double thermalZoomRatios) {
        this.thermalZoomRatios = thermalZoomRatios;
    }

    public int getIsShootingPhoto() {
        return isShootingPhoto;
    }

    public void setIsShootingPhoto(int isShootingPhoto) {
        this.isShootingPhoto = isShootingPhoto;
    }

    public int getIsRecording() {
        return isRecording;
    }

    public void setIsRecording(int isRecording) {
        this.isRecording = isRecording;
    }

    public int getRecordingTime() {
        return recordingTime;
    }

    public void setRecordingTime(int recordingTime) {
        this.recordingTime = recordingTime;
    }


    public String getHomepointLat() {
        return homepointLat;
    }

    public void setHomepointLat(String homepointLat) {
        this.homepointLat = homepointLat;
    }

    public String getHomepointLong() {
        return homepointLong;
    }

    public void setHomepointLong(String homepointLong) {
        this.homepointLong = homepointLong;
    }

    public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }

    public int getUltrasonicHeight() {
        return ultrasonicHeight;
    }

    public void setUltrasonicHeight(int ultrasonicHeight) {
        this.ultrasonicHeight = ultrasonicHeight;
    }

    public int getCameraVideoStreamSource() {
        return cameraVideoStreamSource;
    }

    public void setCameraVideoStreamSource(int cameraVideoStreamSource) {
        this.cameraVideoStreamSource = cameraVideoStreamSource;
    }


    public String getPlaneMode() {
        return planeMode;
    }

    public void setPlaneMode(String planeMode) {
        this.planeMode = planeMode;
    }


    public String getFlightPathName() {
        return flightPathName;
    }

    public void setFlightPathName(String flightPathName) {
        this.flightPathName = flightPathName;
    }

    public int getCameraMode() {
        return cameraMode;
    }

    public void setCameraMode(int cameraMode) {
        this.cameraMode = cameraMode;
    }

    public int getAngleYaw() {
        return angleYaw;
    }

    public void setAngleYaw(int angleYaw) {
        this.angleYaw = angleYaw;
    }

    public int getFlightPathStatus() {
        return flightPathStatus;
    }

    public void setFlightPathStatus(int flightPathStatus) {
        this.flightPathStatus = flightPathStatus;
    }

    public String getWarningMessage() {
        return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
        this.warningMessage = warningMessage;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(int liveStatus) {
        this.liveStatus = liveStatus;
    }

    private int liveStatus;//推流状态 0失败  1成功

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(String horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public String getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(String verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public boolean isRtkSign() {
        return rtkSign;
    }

    public void setRtkSign(boolean rtkSign) {
        this.rtkSign = rtkSign;
    }

    public int getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(int satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }


    public int getRemoteControlSignal() {
        return remoteControlSignal;
    }

    public void setRemoteControlSignal(int remoteControlSignal) {
        this.remoteControlSignal = remoteControlSignal;
    }

    public int getPictureBiographySignal() {
        return pictureBiographySignal;
    }

    public void setPictureBiographySignal(int pictureBiographySignal) {
        this.pictureBiographySignal = pictureBiographySignal;
    }

    public int getElectricityInfoA() {
        return electricityInfoA;
    }

    public void setElectricityInfoA(int electricityInfoA) {
        this.electricityInfoA = electricityInfoA;
    }

    public int getVoltageInfoA() {
        return voltageInfoA;
    }

    public void setVoltageInfoA(int voltageInfoA) {
        this.voltageInfoA = voltageInfoA;
    }

    public int getElectricityInfoB() {
        return electricityInfoB;
    }

    public void setElectricityInfoB(int electricityInfoB) {
        this.electricityInfoB = electricityInfoB;
    }

    public int getVoltageInfoB() {
        return voltageInfoB;
    }

    public void setVoltageInfoB(int voltageInfoB) {
        this.voltageInfoB = voltageInfoB;
    }

    public List<ComponentIndexType> getCurrentView() {
        return currentView;
    }

    public void setCurrentView(List<ComponentIndexType> currentView) {
        this.currentView = currentView;
    }


    public String getPlaneMessage() {
        return planeMessage;
    }

    public void setPlaneMessage(String planeMessage) {
        this.planeMessage = planeMessage;
    }


    public boolean isLevelObstacleAvoidance() {
        return levelObstacleAvoidance;
    }

    public void setLevelObstacleAvoidance(boolean levelObstacleAvoidance) {
        this.levelObstacleAvoidance = levelObstacleAvoidance;
    }

    public float getCurrentAltitude() {//高度返回的是这个
        return Float.parseFloat(String.valueOf(flyingHeight));
    }

    public void setCurrentAltitude(float currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public boolean isFlightController() {
        return isFlightController;
    }

    public void setFlightController(boolean flightController) {
        isFlightController = flightController;
    }

    public Boolean getCameraConnection() {
        return isCameraConnection;
    }

    public void setCameraConnection(Boolean cameraConnection) {
        isCameraConnection = cameraConnection;
    }

    public List<ComponentIndexType> getAvailableCameraList() {
        return availableCameraList;
    }

    public void setAvailableCameraList(List<ComponentIndexType> availableCameraList) {
        this.availableCameraList = availableCameraList;
    }

    public double getFocalLenght() {
        return focalLenght;
    }

    public void setFocalLenght(double focalLenght) {
        this.focalLenght = focalLenght;
    }

    public double getMinFocalLenght() {
        return minFocalLenght;
    }

    public void setMinFocalLenght(double minFocalLenght) {
        this.minFocalLenght = minFocalLenght;
    }

    public DoubleMinMax getGimbalYawRange() {
        return gimbalYawRange;
    }

    public void setGimbalYawRange(DoubleMinMax gimbalYawRange) {
        this.gimbalYawRange = gimbalYawRange;
    }

    public DoubleMinMax getGimbalPitchRange() {
        return gimbalPitchRange;
    }

    public void setGimbalPitchRange(DoubleMinMax gimbalPitchRange) {
        this.gimbalPitchRange = gimbalPitchRange;
    }

    public List<VideoResolutionFrameRate> getKeyVideoResolutionFrameRateRange() {
        return KeyVideoResolutionFrameRateRange;
    }

    public void setKeyVideoResolutionFrameRateRange(List<VideoResolutionFrameRate> keyVideoResolutionFrameRateRange) {
        KeyVideoResolutionFrameRateRange = keyVideoResolutionFrameRateRange;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDevieGBCode() {
        return devieGBCode;
    }

    public void setDevieGBCode(String devieGBCode) {
        this.devieGBCode = devieGBCode;
    }
    public void setAlternatePointLat(String alternatePointLat) {
        this.alternatePointLat = alternatePointLat;
    }

    public String getAlternatePointLat() {
        return alternatePointLat;
    }

    public void setAlternatePointLon(String alternatePointLon) {
        this.alternatePointLon = alternatePointLon;
    }

    public String getAlternatePointLon() {
        return alternatePointLon;
    }

    public CameraType getCurCameraType() {
        return curCameraType;
    }

    public void setCurCameraType(CameraType curCameraType) {
        this.curCameraType = curCameraType;
    }

    public Map<CameraType, float[]> getCameraTypeParameter() {
        return cameraTypeParameter;
    }

    public void setCameraTypeParameter(Map<CameraType, float[]> cameraTypeParameter) {
        this.cameraTypeParameter = cameraTypeParameter;
    }
}
