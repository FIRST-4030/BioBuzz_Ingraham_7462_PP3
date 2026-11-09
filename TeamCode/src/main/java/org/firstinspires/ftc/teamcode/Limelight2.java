//package org.firstinspires.ftc.teamcode;
//
//import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLResultTypes;
//import com.qualcomm.hardware.limelightvision.LLStatus;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import java.util.List;
//
//public class Limelight2 {
//    private final Limelight3A limelight3A;
//    private LLResult latestResult;
//    private LLStatus Status;
//    public enum HiveCell()
//    public Limelight2(HardwareMap hardwareMap){
//        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
//        limelight3A.setPollRateHz(100);
//        limelight3A.start();
//    }
//
//    public void Update (){
//        latestResult = limelight3A.getLatestResult();
//        Status = limelight3A.getStatus();
//    }
//
//    public boolean isRunning(){
//        return limelight3A.isRunning();
//    }
//
//    public boolean isWorking(){
//        return isWorking();
//    }
//
////    public boolean canSeeTag(int tagID){
////        List<LLResultTypes.FiducialResult> seenTags = latestResult.getFiducialResults();
////        for (seenTag: seenTags){
////            if (seenTag)
////        }
////    }
//}
