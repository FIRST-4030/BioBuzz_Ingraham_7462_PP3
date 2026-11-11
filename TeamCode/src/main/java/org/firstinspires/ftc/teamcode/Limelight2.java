package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Limelight2 {
    public enum tagIDs {
        RED_SCORING,
        RED_AUDIENCE,
        BLUE_SCORING,
        BLUE_AUDIENCE
    }

    public final int[] Blue_ScoringIDs = {38, 39, 40, 41};
    public final int[] Blue_AudienceIDs = {42, 43, 44, 45};
    public final int[] Red_AudienceIDs = {30, 31, 32, 33};
    public final int[] Red_ScoringIDs = {34, 35, 36, 37};
    private final Limelight3A limelight3A;
    private LLResult latestResult;
    private LLStatus Status;

    public Limelight2(HardwareMap hardwareMap) {
        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.setPollRateHz(100);
        limelight3A.start();
    }

    public void update() {
        latestResult = limelight3A.getLatestResult();
        Status = limelight3A.getStatus();
    }

    public void setPipeline(int pipeline) {
        limelight3A.pipelineSwitch(pipeline);
    }

    public boolean isRunning() {
        return limelight3A.isRunning();
    }

    public boolean isWorking() {
        return isWorking();
    }

    public boolean canSeeTagWithID(int[] tagIDs) {
        List<LLResultTypes.FiducialResult> seenTags = latestResult.getFiducialResults();
        for (LLResultTypes.FiducialResult seenTag : seenTags) {
            for (int id : tagIDs) {
                if (id == seenTag.getFiducialId()) return true;
            }
        }
        return false;
    }

    public boolean canSeeTagWithID(int tagID) {
        List<LLResultTypes.FiducialResult> seenTags = latestResult.getFiducialResults();
        for (LLResultTypes.FiducialResult seenTag : seenTags) {
            if (seenTag.getFiducialId() == tagID) return true;
        }
        return false;
    }

    public boolean canSeeTagWithID(tagIDs tagType) {
        List<LLResultTypes.FiducialResult> seenTags = latestResult.getFiducialResults();
        int[] tagIDs = getCorrespondingIDs(tagType);
        for (LLResultTypes.FiducialResult seenTag : seenTags) {
            for (int tagID : tagIDs)
                if (seenTag.getFiducialId() == tagID) return true;
        }
        return false;
    }

    public double getXError() {
        return latestResult.getTx();
    }

    public double getYError() {
        return latestResult.getTy();
    }

    public double getTa() {
        return latestResult.getTa();
    }

    private int[] getCorrespondingIDs(tagIDs tagID) {
        switch (tagID) {
            case BLUE_AUDIENCE:
                return Blue_AudienceIDs;
            case BLUE_SCORING:
                return Blue_ScoringIDs;
            case RED_AUDIENCE:
                return Red_AudienceIDs;
        }
        return Red_ScoringIDs;
    }
}
